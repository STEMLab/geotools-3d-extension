/*
 *  GeoTools - The Open Source Java GIS Toolkit
 *  http://geotools.org
 * 
 *  (C) 2015, Open Source Geospatial Foundation (OSGeo)
 * 
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 *
 * Copyright (C) 2001 - 2014 Vivid Solutions
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, contact:
 *
 *     Vivid Solutions
 *     Suite #1A
 *     2328 Government Street
 *     Victoria BC  V8T 5G5
 *     Canada
 *
 *     (250)385-6040
 *     www.vividsolutions.com
 */
package org.geotools.data.postgis3d;

import java.io.IOException;

import org.geotools.factory.GeoTools;
import org.geotools.referencing.CRS;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Point;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
//import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ByteArrayInStream;
import com.vividsolutions.jts.io.ByteOrderDataInStream;
import com.vividsolutions.jts.io.ByteOrderValues;
import com.vividsolutions.jts.io.InStream;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBWriter;

/**
 * Reads a {@link Geometry}from a byte stream in Postgis Extended Well-Known Binary format. Supports
 * use of an {@link InStream}, which allows easy use with arbitrary byte stream sources.
 * <p>
 * This class reads the format describe in {@link WKBWriter}. It also partially handles the
 * <b>Extended WKB</b> format used by PostGIS, by parsing and storing SRID values and supporting .
 * The reader repairs structurally-invalid input (specifically, LineStrings and LinearRings which
 * contain too few points have vertices added, and non-closed rings are closed).
 * <p>
 * This class is designed to support reuse of a single instance to read multiple geometries. This
 * class is not thread-safe; each thread should create its own instance.
 *
 * @see WKBWriter for a formal format specification
 */
public class WKBReader {
    /**
     * Converts a hexadecimal string to a byte array. The hexadecimal digit symbols are
     * case-insensitive.
     *
     * @param hex a string containing hex digits
     * @return an array of bytes with the value of the hex string
     */
    public static byte[] hexToBytes(String hex) {
        int byteLen = hex.length() / 2;
        byte[] bytes = new byte[byteLen];

        for (int i = 0; i < hex.length() / 2; i++) {
            int i2 = 2 * i;
            if (i2 + 1 > hex.length())
                throw new IllegalArgumentException("Hex string has odd length");

            int nib1 = hexToInt(hex.charAt(i2));
            int nib0 = hexToInt(hex.charAt(i2 + 1));
            byte b = (byte) ((nib1 << 4) + (byte) nib0);
            bytes[i] = b;
        }
        return bytes;
    }

    private static int hexToInt(char hex) {
        int nib = Character.digit(hex, 16);
        if (nib < 0)
            throw new IllegalArgumentException("Invalid hex digit: '" + hex + "'");
        return nib;
    }

    private static final String INVALID_GEOM_TYPE_MSG = "Invalid geometry type encountered in ";

    //private CurvedGeometryFactory factory;
    private ISOGeometryBuilder builder;

    private CoordinateSequenceFactory csFactory;

    //private PrecisionModel precisionModel;

    // default dimension - will be set on read
    private int inputDimension = 2;

    private boolean hasSRID = false;

    private int SRID = 0;

    /**
     * true if structurally invalid input should be reported rather than repaired. At some point
     * this could be made client-controllable.
     */
    private boolean isStrict = false;

    private ByteOrderDataInStream dis = new ByteOrderDataInStream();

    private double[] ordValues;

    public WKBReader() {
        this(new ISOGeometryBuilder(GeoTools.getDefaultHints()));
    }

    public WKBReader(ISOGeometryBuilder geometrybuilder) {
        this.builder = geometrybuilder;//getCurvedGeometryFactory(geometryFactory);
        //precisionModel = builder.getPrecisionModel();
        //csFactory = builder.getCoordinateSequenceFactory();
    }

    /**
     * Reads a single {@link Geometry} in WKB format from a byte array.
     *
     * @param bytes the byte array to read from
     * @return the geometry read
     * @throws ParseException if the WKB is ill-formed
     */
    public Geometry read(byte[] bytes) throws ParseException {
        // possibly reuse the ByteArrayInStream?
        // don't throw IOExceptions, since we are not doing any I/O
        try {
            return read(new ByteArrayInStream(bytes));
        } catch (IOException ex) {
            throw new RuntimeException("Unexpected IOException caught: " + ex.getMessage());
        }
    }

    /**
     * Reads a {@link Geometry} in binary WKB format from an {@link InStream}.
     *
     * @param is the stream to read from
     * @return the Geometry read
     * @throws IOException if the underlying stream creates an error
     * @throws ParseException if the WKB is ill-formed
     */
    public Geometry read(InStream is) throws IOException, ParseException {
        dis.setInStream(is);
        Geometry g = readGeometry();
        return g;
    }

    protected Geometry readGeometry() throws IOException, ParseException {
        // determine byte order
        byte byteOrderWKB = dis.readByte();
        // always set byte order, since it may change from geometry to geometry
        int byteOrder = byteOrderWKB == WKBConstants.wkbNDR ? ByteOrderValues.LITTLE_ENDIAN
                : ByteOrderValues.BIG_ENDIAN;
        dis.setOrder(byteOrder);

        int typeInt = dis.readInt();
        int geometryType = typeInt & 0xff;
        // determine if Z values are present
        boolean hasZ = (typeInt & 0x80000000) != 0;
        inputDimension = hasZ ? 3 : 2;
        // determine if SRIDs are present
        hasSRID = (typeInt & 0x20000000) != 0;

        int SRID = 0;
        if (hasSRID) {
            SRID = dis.readInt();
        }

        // only allocate ordValues buffer if necessary
        if (ordValues == null || ordValues.length < inputDimension)
            ordValues = new double[inputDimension];

        Geometry geom = readGeometry(geometryType);
        setSRID(geom, SRID);
        return geom;
    }

    protected Geometry readGeometry(int geometryType) throws IOException, ParseException {
        Geometry geom = null;
        switch (geometryType) {
        case WKBConstants.wkbPoint:
            geom = readPoint();
            break;
        case WKBConstants.wkbLineString:
            geom = readLineString();
            break;
        case WKBConstants.wkbPolygon:
        	//TODO 
        	throw new UnsupportedOperationException();
            //geom = readPolygon();
            //break;
        case WKBConstants.wkbMultiPoint:
        	//TODO 
        	throw new UnsupportedOperationException();
            //geom = readMultiPoint();
            //break;
        case WKBConstants.wkbMultiCurve:
        case WKBConstants.wkbMultiLineString:
        	//TODO 
        	throw new UnsupportedOperationException();
            //geom = readMultiLineString();
            //break;
        case WKBConstants.wkbMultiPolygon:
        case WKBConstants.wkbMultiSurface:
        	//TODO 
        	throw new UnsupportedOperationException();
            //geom = readMultiPolygon();
            //break;
        case WKBConstants.wkbGeometryCollection:
        	//TODO 
        	throw new UnsupportedOperationException();
            //geom = readGeometryCollection();
            //break;
        case WKBConstants.wkbCircularString:
        	//TODO 
        	throw new UnsupportedOperationException();
            //geom = readCircularString();
            //break;
        case WKBConstants.wkbCompoundCurve:
        	//TODO 
        	throw new UnsupportedOperationException();
            //geom = readCompoundCurve();
            //break;
        case WKBConstants.wkbCurvePolygon:
        	//TODO 
        	throw new UnsupportedOperationException();
            //geom = readCurvePolygon();
            //break;

        default:
            throw new ParseException("Unknown WKB type " + geometryType);
        }
        return geom;
    }

    /**
     * Sets the SRID, if it was specified in the WKB
     *
     * @param g the geometry to update
     * @return the geometry with an updated SRID value, if required
     */
    private Geometry setSRID(Geometry g, int SRID) {
        if (SRID != 0)
			try {
				builder.setCoordinateReferenceSystem(CRS.decode("EPSG:" + SRID));
			} catch (NoSuchAuthorityCodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FactoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        return g;
    }

    private Point readPoint() throws IOException {
        //CoordinateSequence pts = readCoordinateSequence(1);
    	double[] pt = readCoordinateSequence(1);
    	
        return builder.createPoint(pt);

    }

    private Curve readLineString() throws IOException {
        int size = dis.readInt();
        double[] pts = readCoordinateSequenceLineString(size);
        PointArray parr = builder.createPointArray(pts);
        return builder.createCurve(parr);
    }
    
    /*
    private Geometry readCircularString() throws IOException {
        int size = dis.readInt();
        CoordinateSequence pts = readCoordinateSequenceCircularString(size);
        return builder.createCurvedGeometry(pts);
    }

    private Geometry readCompoundCurve() throws IOException, ParseException {
        int numGeom = dis.readInt();
        List<LineString> geoms = new ArrayList<>();
        for (int i = 0; i < numGeom; i++) {
            Geometry g = readGeometry();
            if (!(g instanceof LineString))
                throw new ParseException(INVALID_GEOM_TYPE_MSG + "CompoundCurve");
            geoms.add((LineString) g);
        }
        return builder.createCurvedGeometry(geoms);
    }

    private LinearRing readLinearRing() throws IOException {
        int size = dis.readInt();
        CoordinateSequence pts = readCoordinateSequenceRing(size);
        return builder.createLinearRing(pts);
    }

    protected Polygon readPolygon() throws IOException {
        int numRings = dis.readInt();
        LinearRing[] holes = null;
        if (numRings > 1)
            holes = new LinearRing[numRings - 1];

        LinearRing shell = readLinearRing();
        for (int i = 0; i < numRings - 1; i++) {
            holes[i] = readLinearRing();
        }
        return builder.createPolygon(shell, holes);
    }

    protected Polygon readCurvePolygon() throws IOException, ParseException {
        int numRings = dis.readInt();
        LinearRing[] holes = null;
        if (numRings > 1)
            holes = new LinearRing[numRings - 1];

        LinearRing shell = readRing();
        for (int i = 0; i < numRings - 1; i++) {
            holes[i] = readRing();
        }
        return builder.createPolygon(shell, holes);
    }

    private LinearRing readRing() throws IOException, ParseException {
        LineString ls = (LineString) readGeometry();
        if (!(ls instanceof LinearRing)) {
            if (!ls.isClosed()) {
                if (ls instanceof CompoundCurve) {
                    CompoundCurve cc = (CompoundCurve) ls;
                    List<LineString> components = cc.getComponents();
                    Coordinate start = components.get(0).getCoordinateN(0);
                    LineString lastGeom = components.get(components.size() - 1);
                    Coordinate end = lastGeom.getCoordinateN((lastGeom.getNumPoints() - 1));
                    components.add(builder.createLineString(new Coordinate[] { start, end }));
                    ls = builder.createCurvedGeometry(components);
                } else {
                    Coordinate start = ls.getCoordinateN(0);
                    Coordinate end = ls.getCoordinateN((ls.getNumPoints() - 1));
                    // turn it into a compound and add the segment that closes it
                    LineString closer = builder.createLineString(new Coordinate[] { start, end });
                    ls = builder.createCurvedGeometry(ls, closer);
                }
            } else {
                if (ls instanceof CompoundCurve) {
                    // this case should never happen, but let's be robust against
                    // alternative geometry factories not behaving as expected
                    CompoundCurve cc = (CompoundCurve) ls;
                    ls = new CompoundRing(cc.getComponents(), cc.getFactory(), cc.getTolerance());
                } else {
                    ls = new LinearRing(ls.getCoordinateSequence(), ls.getFactory());
                }
            }
        }
        
        return (LinearRing) ls;

    }

    private MultiPoint readMultiPoint() throws IOException, ParseException {
        int numGeom = dis.readInt();
        Point[] geoms = new Point[numGeom];
        for (int i = 0; i < numGeom; i++) {
            Geometry g = readGeometry();
            if (!(g instanceof Point))
                throw new ParseException(INVALID_GEOM_TYPE_MSG + "MultiPoint");
            geoms[i] = (Point) g;
        }
        return builder.createMultiPoint(geoms);
    }

    private MultiLineString readMultiLineString() throws IOException, ParseException {
        int numGeom = dis.readInt();
        LineString[] geoms = new LineString[numGeom];
        for (int i = 0; i < numGeom; i++) {
            Geometry g = readGeometry();
            if (!(g instanceof LineString))
                throw new ParseException(INVALID_GEOM_TYPE_MSG + "MultiLineString");
            geoms[i] = (LineString) g;
        }
        return builder.createMultiLineString(geoms);
    }

    private MultiPolygon readMultiPolygon() throws IOException, ParseException {
        int numGeom = dis.readInt();
        Polygon[] geoms = new Polygon[numGeom];
        for (int i = 0; i < numGeom; i++) {
            Geometry g = readGeometry();
            if (!(g instanceof Polygon))
                throw new ParseException(INVALID_GEOM_TYPE_MSG + "MultiPolygon");
            geoms[i] = (Polygon) g;
        }
        return builder.createMultiPolygon(geoms);
    }

    private GeometryCollection readGeometryCollection() throws IOException, ParseException {
        int numGeom = dis.readInt();
        Geometry[] geoms = new Geometry[numGeom];
        for (int i = 0; i < numGeom; i++) {
            geoms[i] = readGeometry();
        }
        return builder.createGeometryCollection(geoms);
    }*/

    //private CoordinateSequence readCoordinateSequence(int size) throws IOException {
    private double[] readCoordinateSequence(int size) throws IOException {
        //CoordinateSequence seq = csFactory.create(size, inputDimension);
        //int targetDim = seq.getDimension();
        //if (targetDim > inputDimension)
        //    targetDim = inputDimension;
    	double[] seq = new double[size * inputDimension];
        for (int i = 0; i < size; i++) {
            readCoordinate();
            //for (int j = 0; j < targetDim; j++) {
            for (int j = 0; j < inputDimension; j++) {
                //seq.setOrdinate(i, j, ordValues[j]);
            	seq[(i * inputDimension) + j] = ordValues[j];
            }
        }
        return seq;
    }
    private double[] repair(double[] seq, int size) {
    	double[] newseq = new double[size * inputDimension];
        int n = seq.length;
        for (int i = 0; i < n; i++)
            newseq[i] = seq[i];
        // fill remaining coordinates with end point, if it exists
        if (n > 0) {
          for (int i = n; i < size; i++)
            newseq[i] = seq[n - 1];
        }
        return newseq;
    }
    //private CoordinateSequence readCoordinateSequenceCircularString(int size) throws IOException {
    private double[] readCoordinateSequenceCircularString(int size) throws IOException {
    	double[] seq = readCoordinateSequence(size);
        if (isStrict)
            return seq;
        if (seq.length == 0 || seq.length >= 3)
            return seq;
        return repair(seq, 3);
    }

    private double[] readCoordinateSequenceLineString(int size) throws IOException {
    	double[] seq = readCoordinateSequence(size);
        if (isStrict)
            return seq;
        if (seq.length == 0 || seq.length >= 2)
            return seq;
        return repair(seq, 2);
    }
    private boolean isRing(double[] seq) {
    	int n = seq.length;
      	if (n == 0) return true;
      	// too few points
      	if (n <= 3) 
      		return false;
      	// test if closed
      	return seq[0] == seq[n - inputDimension]
      		&& seq[1] == seq[n - inputDimension - 1];
     
    }
    private double[] ensureValidRing(double[] seq) {
    	int n = seq.length;
      	// empty sequence is valid
      	if (n == 0) return seq; 
      	// too short - make a new one
      	if (n <= 3) 
      		return createClosedRing(seq, 4);
      	
      	boolean isClosed = seq[0] == seq[n - inputDimension]
    		&& seq[1] == seq[n - inputDimension - 1];
      	if (isClosed) return seq;
      	// make a new closed ring
      	return createClosedRing(seq, n+1);
    }
    private double[] createClosedRing(double[] seq, int size) {
    	double[] newseq = new double[size * inputDimension];
        int n = seq.length;
        // fill remaining coordinates with start point  
        for (int i = 0; i < n; i++)
            newseq[i] = seq[i];
        // fill remaining coordinates with end point, if it exists

        for (int i = n; i < size; i++)
          newseq[i] = seq[0];
        
        return newseq;
    }
    private double[] readCoordinateSequenceRing(int size) throws IOException {
    	double[] seq = readCoordinateSequence(size);
        if (isStrict)
            return seq;
        //if (CoordinateSequences.isRing(seq))
        if (isRing(seq))
            return seq;
        return ensureValidRing(seq);
    }
    
    /**
     * Reads a coordinate value with the specified dimensionality. Makes the X and Y ordinates
     * precise according to the precision model in use.
     */
    private void readCoordinate() throws IOException {
        for (int i = 0; i < inputDimension; i++) {
            //if (i <= 1) {
            //    ordValues[i] = precisionModel.makePrecise(dis.readDouble());
            //} else {
                ordValues[i] = dis.readDouble();
            //}

        }
    }

    /**
     * Casts the provided geometry factory to a curved one if possible, or wraps it into one with
     * infinite tolerance (the linearization will happen using the default base segments number set
     * in {@link CircularArc}
     * 
     * @param gf
     * @return
     */
    /*private CurvedGeometryFactory getCurvedGeometryFactory(GeometryFactory gf) {
        CurvedGeometryFactory curvedFactory;
        if (gf instanceof CurvedGeometryFactory) {
            curvedFactory = (CurvedGeometryFactory) gf;
        } else {
            curvedFactory = new CurvedGeometryFactory(gf, Double.MAX_VALUE);
        }
        return curvedFactory;
    }*/

}