/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2010, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.iso.geojson.geom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotools.geometry.iso.util.PointArrayUtil;
import org.geotools.iso.geojson.GeoJSONUtil;
import org.geotools.iso.geojson.IContentHandler;
import org.json.simple.JSONAware;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.BoundingBox3D;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Reads and writes geometry objects to and from geojson.
 * <p>
 * <pre>
 * Point point = new Point(1,2);
 * 
 * GeometryJSON g = new GeometryJSON();
 * g.writePoint(point, "point.json"));
 * Point point2 = g.readPoint("point.json");
 * 
 * Geometry geometry = ...;
 * g.write(geometry, new File("geometry.json"));
 * geometry = g.read("geometry.json");
 * 
 * </pre>
 * </p>
 * @author Justin Deoliveira, OpenGeo
 *
 *
 *
 *
 * @source $URL$
 */
public class GeometryJSON {

    ISOGeometryBuilder builder;
    boolean trace = false;
    int decimals;
    double scale;

    /**
     * Constructs a geometry json instance.
     */
    public GeometryJSON(ISOGeometryBuilder builder) {
        this(builder, 4);
    }
    
    public GeometryJSON(CoordinateReferenceSystem crs) {
        this(new ISOGeometryBuilder(crs), 4);
    }
    
    /**
     * Constructs a geometry json instance specifying the number of decimals
     * to use when encoding floating point numbers.
     */
    public GeometryJSON(ISOGeometryBuilder builder, int decimals) {
        this.decimals = decimals;
        this.scale = Math.pow(10, decimals);
        this.builder = builder;
    }
    
    /**
     * Sets trace flag.
     */
    public void setTrace(boolean trace) {
        this.trace = trace;
    }
    
    /**
     * Tracing flag. 
     * <p>
     * When this flag is set parsed documents will be echoed to stdout 
     * during parsing.
     * </p>
     */
    public boolean isTrace() {
        return trace;
    }
    
    /**
     * Writes a Geometry instance as GeoJSON.
     * 
     * @param geometry The geometry.
     * @param output The output. See {@link GeoJSONUtil#toWriter(Object)} for details.
     */
    public void write(Geometry geometry, Object output) throws IOException {
        if (geometry == null) { //|| geometry.isEmpty()) {
            GeoJSONUtil.encode("null", output); 
        } else {
            GeoJSONUtil.encode(create(geometry), output);
        }
    }

    /**
     * Writes a Geometry instance as GeoJSON.
     * <p>
     * This method calls through to {@link #write(Geometry, Object)}
     * </p>
     * @param geometry The geometry.
     * @param output The output stream.
     */
    public void write(Geometry geometry, OutputStream output) throws IOException {
        GeoJSONUtil.encode(create(geometry), output);
    }

    /**
     * Writes a Geometry instance as GeoJSON returning the result as a string.
     * 
     * @param geometry The geometry.
     * 
     * @return The geometry encoded as GeoJSON 
     */
    public String toString(Geometry geometry) {
        StringWriter w = new StringWriter();
        try {
            write(geometry, w);
            return w.toString();
        } 
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        
    }
    
    Map<String,Object> create(Geometry geometry) {
        if (geometry instanceof Point) {
            return createPoint((Point)geometry);
        }
        
        if (geometry instanceof Curve) {
            return createLine((Curve)geometry);
        }
        
        if (geometry instanceof Surface) {
            return createSurface((Surface)geometry);
        }
        
        if (geometry instanceof MultiPoint) {
            return createMultiPoint((MultiPoint)geometry);
        }
        
        if (geometry instanceof MultiCurve) {
            return createMultiCurve((MultiCurve)geometry);
        }
        
        if (geometry instanceof MultiSurface) {
            return createMultiSurface((MultiSurface)geometry);
        }
        
        if (geometry instanceof MultiPrimitive) {
            return createMultiPrimitive((MultiPrimitive)geometry);
        }
        
        if (geometry instanceof Solid) {
        	return null;
        }
        
        throw new IllegalArgumentException("Unable to encode object " + geometry);
    }
    
    /**
     * Reads a Geometry instance from GeoJSON.
     *
     * @param input The input. See {@link GeoJSONUtil#toReader(Object)} for details.
     * 
     * @return The geometry instance.
     */
    public Geometry read(Object input) throws IOException {
        return parse(new ISOGeometryHandler(builder), input); 
    }

    /**
     * Reads a Geometry instance from GeoJSON.
     * <p>
     * This method calls through to {@link #read(Object)}
     * </p>
     * @param input The input stream.
     * 
     * @return The geometry instance.
     */
    public Geometry read(InputStream input) throws IOException {
        return read((Object)input); 
    }

    /**
     * Writes a Point as GeoJSON.
     * 
     * @param point The point.
     * @param output The output. See {@link GeoJSONUtil#toWriter(Object)} for details.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public void writePoint(Point point, Object output) throws IOException {
        encode(createPoint(point), output);
    }

    /**
     * Writes a Point as GeoJSON.
     * <p>
     * This method calls through to {@link #writePoint(Point, Object)}
     * </p>
     * @param point The point.
     * @param output The output stream.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public void writePoint(Point point, OutputStream output) throws IOException {
        writePoint(point, (Object) output);
    }

    Map<String,Object> createPoint(Point point) {
        LinkedHashMap obj = new LinkedHashMap();
        
        obj.put("type", "Point");
        obj.put("coordinates", new DirectPositionEncoder(point.getDirectPosition(), scale));        
        return obj;
    }
    
    /**
     * Reads a Point from GeoJSON.
     * 
     * @param input The input. See {@link GeoJSONUtil#toReader(Object)} for details.
     * 
     * @return The point.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public Point readPoint(Object input) throws IOException {
        return parse(new PointHandler(builder), input);
    }

    /**
     * Reads a Point from GeoJSON.
     * <p>
     * This method calls through to {@link #readPoint(Object)}
     * </p>
     * @param input The input stream.
     * 
     * @return The point.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public Point readPoint(InputStream input) throws IOException {
        return readPoint((Object)input);
    }

    /**
     * Writes a Curve as GeoJSON.
     * 
     * @param line The line string.
     * @param output The output. See {@link GeoJSONUtil#toWriter(Object)} for details.
     */
    public void writeLine(Curve line, Object output) throws IOException {
        encode(createLine(line), output);
    }

    /**
     * Writes a Curve as GeoJSON.
     * <p>
     * This method calls through to {@link #writeLine(Curve, Object)}
     * </p>
     * @param line The line string.
     * @param output The output stream.
     */
    public void writeLine(Curve line, OutputStream output) throws IOException {
        writeLine(line, (Object)output);
    }

    Map<String,Object> createLine(Curve line) {
        LinkedHashMap obj = new LinkedHashMap();
        
        obj.put("type", "LineString");
        obj.put("coordinates", new PointArrayEncoder(PointArrayUtil.toList(builder, line), scale));
        return obj;
    }
    
    /**
     * Reads a Curve from GeoJSON.
     * 
     * @param input The input. See {@link GeoJSONUtil#toReader(Object)} for details.
     * 
     * @return The line string.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public Curve readLine(Object input) throws IOException {
        return parse(new CurveHandler(builder), input);
    }

    /**
     * Reads a Curve from GeoJSON.
     * <p>
     * This method calls through to {@link #readLine(Object)}
     * </p>
     * @param input The input stream.
     * 
     * @return The line string.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public Curve readLine(InputStream input) throws IOException {
        return readLine((Object)input);
    }

    /**
     * Writes a Surface as GeoJSON.
     * 
     * @param poly The Surface.
     * @param output The output. See {@link GeoJSONUtil#toWriter(Object)} for details.
     */
    public void writeSurface(Surface poly, Object output) throws IOException {
        encode(createSurface(poly), output);
    }

    /**
     * Writes a Surface as GeoJSON.
     * <p>
     * This method calls through to {@link #writeSurface(Surface, Object)}
     * </p>
     * @param poly The Surface.
     * @param output The output stream.
     */
    public void writeSurface(Surface poly, OutputStream output) throws IOException {
        writeSurface(poly, (Object)output);
    }

    Map<String,Object> createSurface(Surface poly) {
        LinkedHashMap obj = new LinkedHashMap();
        
        obj.put("type", "Polygon");
        obj.put("coordinates", toList(poly));
        return obj;
    }
    
    /**
     * Reads a Surface from GeoJSON.
     *
     * @param input The input. See {@link GeoJSONUtil#toReader(Object)} for details.
     * 
     * @return The Surface.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public Surface readSurface(Object input) throws IOException {
        return parse(new SurfaceHandler(builder), input);
    }

    /**
     * Reads a Surface from GeoJSON.
     * <p>
     * This method calls through to {@link #readSurface(Object)}
     * </p>
     * @param input The input stream.
     * 
     * @return The Surface.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public Surface readSurface(InputStream input) throws IOException {
        return readSurface((Object)input);
    }

    /**
     * Writes a MultiPoint as GeoJSON.
     * 
     * @param mpoint The multi point.
     * @param output The output. See {@link GeoJSONUtil#toWriter(Object)} for details.
     */
    public void writeMultiPoint(MultiPoint mpoint, Object output) throws IOException {
        encode(createMultiPoint(mpoint), output);
    }

    /**
     * Writes a MultiPoint as GeoJSON.
     * <p>
     * This method calls through to {@link #writeMultiPoint(MultiPoint, Object)}
     * </p>
     * @param mpoint The multi point.
     * @param output The output stream.
     */
    public void writeMultiPoint(MultiPoint mpoint, OutputStream output) throws IOException {
        writeMultiPoint(mpoint, (Object)output);
    }

    Map<String,Object> createMultiPoint(MultiPoint mpoint) {
        LinkedHashMap obj = new LinkedHashMap();
        
        obj.put("type", "MultiPoint");
        obj.put("coordinates", toList(mpoint));
        return obj;
    }
    
    /**
     * Reads a MultiPoint from GeoJSON.
     * 
     * @param input The input. See {@link GeoJSONUtil#toReader(Object)} for details.
     * 
     * @return The multi point.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public MultiPoint readMultiPoint(Object input) throws IOException {
        return parse(new MultiPointHandler(builder), input);
    }

    /**
     * Reads a MultiPoint from GeoJSON.
     * <p>
     * This method calls through to {@link #readMultiPoint(Object)}
     * </p>
     * 
     * @param input The input stream.
     * 
     * @return The multi point.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public MultiPoint readMultiPoint(InputStream input) throws IOException {
        return readMultiPoint((Object)input);
    }

    /**
     * Writes a MultiCurve as GeoJSON.
     * 
     * @param mline The multi line string.
     * @param output The output. See {@link GeoJSONUtil#toWriter(Object)} for details.
     */
    public void writeMultiCurve(MultiCurve mline, Object output) throws IOException {
        encode(createMultiCurve(mline), output);
    }

    /**
     * Writes a MultiCurve as GeoJSON.
     * <p>
     * This method calls through to {@link #writeMultiCurve(MultiCurve, Object)}
     * </p>
     * @param mline The multi line string.
     * @param output The output stream.
     */
    public void writeMultiCurve(MultiCurve mline, OutputStream output) throws IOException {
        writeMultiCurve(mline, (Object)output);
    }

    Map<String,Object> createMultiCurve(MultiCurve mline) {
        LinkedHashMap obj = new LinkedHashMap();
        
        obj.put("type", "MultiLineString");
        obj.put("coordinates", toList(mline));
        return obj;
    }
    
    /**
     * Reads a MultiCurve from GeoJSON.
     * 
     * @param input The input. See {@link GeoJSONUtil#toReader(Object)} for details.
     * 
     * @return The multi line string.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public MultiCurve readMultiCurve(Object input) throws IOException {
        return parse(new MultiCurveHandler(builder), input);
    }

    /**
     * Reads a MultiCurve from GeoJSON.
     * <p>
     * This method calls through to {@link #readMultiCurve(Object)}
     * </p>
     * @param input The input stream.
     * 
     * @return The multi line string.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public MultiCurve readMultiCurve(InputStream input) throws IOException {
        return readMultiCurve((Object)input);
    }

    /**
     * Writes a MultiSurface as GeoJSON.
     * 
     * @param mpoly The multi Surface.
     * @param output The output. See {@link GeoJSONUtil#toWriter(Object)} for details.
     */
    public void writeMultiSurface(MultiSurface mpoly, Object output) throws IOException {
        encode(createMultiSurface(mpoly), output);
    }

    /**
     * Writes a MultiSurface as GeoJSON.
     * <p>
     * This method calls through to {@link #writeMultiSurface(MultiSurface, Object)}
     * </p>
     * @param mpoly The multi Surface.
     * @param output The output stream.
     */
    public void writeMultiSurface(MultiSurface mpoly, OutputStream output) throws IOException {
        writeMultiSurface(mpoly, (Object)output);
    }

    Map<String,Object> createMultiSurface(MultiSurface mpoly) {
        LinkedHashMap obj = new LinkedHashMap();

        obj.put("type", "MultiPolygon");
        obj.put("coordinates", toList(mpoly));
        return obj;
    }
    
    /**
     * Reads a MultiSurface from GeoJSON.
     * 
     * @param input The input. See {@link GeoJSONUtil#toReader(Object)} for details.
     * 
     * @return The multi Surface.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public MultiSurface readMultiSurface(Object input) throws IOException {
        return parse(new MultiSurfaceHandler(builder), input);
    }

    /**
     * Reads a MultiSurface from GeoJSON.
     * <p>
     * This method calls through to {@link #readMultiSurface(Object)}
     * </p>
     * @param input The input stream.
     * 
     * @return The multi Surface.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public MultiSurface readMultiSurface(InputStream input) throws IOException {
        return readMultiSurface((Object)input);
    }

    /**
     * Writes a MultiPrimitive as GeoJSON.
     * 
     * @param gcol The geometry collection.
     * @param output The output. See {@link GeoJSONUtil#toWriter(Object)} for details.
     */
    public void writeMultiPrimitive(MultiPrimitive gcol, Object output) throws IOException {
        encode(createMultiPrimitive(gcol), output);
    }

    /**
     * Writes a MultiPrimitive as GeoJSON.
     * <p>
     * This method calls through to {@link #writeMultiPrimitive(MultiPrimitive, Object)}
     * </p>
     * @param gcol The geometry collection.
     * @param output The output stream.
     */
    public void writeMultiPrimitive(MultiPrimitive gcol, OutputStream output) throws IOException {
        writeMultiPrimitive(gcol, (Object)output);
    }

    Map<String,Object> createMultiPrimitive(MultiPrimitive gcol) {
        LinkedHashMap obj = new LinkedHashMap();
        
        Set<? extends Primitive> elems = gcol.getElements();
        ArrayList geoms = new ArrayList(elems.size());
        
        for(Primitive p : elems) {
        	geoms.add(create(p));
        }
        
        obj.put("type", "GeometryCollection");
        obj.put("geometries", geoms);
        return obj;
    }
    
    /**
     * Reads a MultiPrimitive from GeoJSON.
     * 
     * @param input The input. See {@link GeoJSONUtil#toReader(Object)} for details.
     * 
     * @return The geometry collection.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public MultiPrimitive readMultiPrimitive(Object input) throws IOException {
        return parse(new MultiPrimitiveHandler(builder), input);
    }

    /**
     * Reads a MultiPrimitive from GeoJSON.
     * <p>
     * This method calls through to {@link #readMultiPrimitive(Object)}
     * </p>
     * @param input The input stream.
     * 
     * @return The geometry collection.
     * 
     * @throws IOException In the event of a parsing error or if the input json is invalid.
     */
    public MultiPrimitive readMultiPrimitive(InputStream input) throws IOException {
        return readMultiPrimitive((Object)input);
    }

    /**
     * Writes an BoundingBox instance as GeoJSON returning the result as a string.
     * 
     * @param bbox The bounding box.
     * 
     * @return The bounding box encoded as GeoJSON 
     */
    public String toString(BoundingBox bbox) {
    	
    	if(bbox instanceof BoundingBox3D) {
    		BoundingBox3D bbox3D = (BoundingBox3D) bbox;
    		return new StringBuffer().append("[").append(bbox3D.getMinX()).append(",")
    	            .append(bbox3D.getMinY()).append(",").append(bbox3D.getMinZ()).append(",")
    	            .append(bbox3D.getMaxX()).append(",").append(bbox3D.getMaxY()).append(",")
    	            .append(bbox3D.getMaxZ()).append("]").toString();
    	}
    	
        return new StringBuffer().append("[").append(bbox.getMinX()).append(",")
            .append(bbox.getMinY()).append(",").append(bbox.getMaxX()).append(",")
            .append(bbox.getMaxY()).append("]").toString();
    }
    
    /**
     * Writes an Envelope instance as GeoJSON returning the result as a string.
     * 
     * @param e The envelope
     * 
     * @return The envelope encoded as GeoJSON 
     */
    public String toString(Envelope e) {
    	DirectPosition lower = e.getLowerCorner();
    	DirectPosition upper = e.getUpperCorner();
    	
    	int dimension = lower.getDimension();
    	
    	StringBuffer buf = new StringBuffer();
    	buf.append("[");
    	
    	for(int i = 0; i < dimension; i++) {
    		buf.append(lower.getCoordinate()[i]);
    		buf.append(",");
    	}
    	
    	for(int i = 0; i < dimension - 1; i++) {
    		buf.append(upper.getCoordinate()[i]);
    		buf.append(",");
    	}
    	buf.append(upper.getCoordinate()[dimension - 1]);
    	buf.append("]");
    	return buf.toString();
    }
    
    <G extends Geometry> G parse(IContentHandler<G> handler, Object input) throws IOException {
        return GeoJSONUtil.parse(handler, input, trace);
    }
    
    void encode(Map<String,Object> obj, Object output) throws IOException {
        GeoJSONUtil.encode(obj, output);
    }
    
    List toList(Surface poly) {
    	SurfaceBoundary sb = poly.getBoundary();
    	
        ArrayList list = new ArrayList();
        list.add(new PointArrayEncoder(PointArrayUtil.toList(builder, sb.getExterior()), scale));
        
        for(Ring r : sb.getInteriors()) {
        	list.add(new PointArrayEncoder(PointArrayUtil.toList(builder, r), scale));
        }
        return list;
    }
    
    List toList(MultiPrimitive mgeom) {
    	Set<? extends Primitive> prims = mgeom.getElements();
    	
        ArrayList list = new ArrayList(prims.size());
        for(Primitive p : prims) {
        	if (p instanceof Surface) {
                list.add(toList((Surface)p));
            }
            else if (p instanceof Curve){
                list.add(new PointArrayEncoder(PointArrayUtil.toList(builder, (Curve)p), scale));
            }
            else if (p instanceof Point) {
                list.add(new DirectPositionEncoder(((Point)p).getDirectPosition(), scale));
            }
        }
        
        return list;
    }
    
    static class DirectPositionEncoder implements JSONAware {
        /**
         * The min value at which the decimal notation is used 
         * (below it, the computerized scientific one is used instead)
         */
        private static final double DECIMAL_MIN = Math.pow(10, -3);
        
        /**
         * The max value at which the decimal notation is used 
         * (above it, the computerized scientific one is used instead)
         */
        private static final double DECIMAL_MAX = Math.pow(10, 7);
        
        DirectPosition dp;
        double scale;

        DirectPositionEncoder(DirectPosition dp, double scale) {
            this.dp = dp;
            this.scale = scale;
        }
        
		@Override

        public String toJSONString() {
            StringBuilder sb = new StringBuilder();

	        sb.append("[");
	        formatDecimal(dp.getOrdinate(0), sb);
	        
	        sb.append(",");
	        formatDecimal(dp.getOrdinate(1), sb);
	        
	        if (dp.getDimension() > 2) {
	        	sb.append(",");
	            formatDecimal(dp.getOrdinate(2), sb);
	        	
	        }
	        
	        sb.append("]");
            
            return sb.toString();
        }

        public void writeJSONString(Writer out) throws IOException {
            out.write("[");
            out.write(String.valueOf(dp.getOrdinate(0)));
            out.write(",");
            out.write(String.valueOf(dp.getOrdinate(1)));
            if (dp.getDimension() > 2) {
                out.write(",");
                out.write(String.valueOf(dp.getOrdinate(2)));
            }
            out.write("]");
        }
        
        private void formatDecimal(double x, StringBuilder sb) {
            if(Math.abs(x) >= DECIMAL_MIN && x < DECIMAL_MAX) {
                x = Math.floor(x * scale + 0.5) / scale;
                long lx = (long) x;
                if(lx == x)
                    sb.append(lx);
                else
                    sb.append(x);
            } else {
                sb.append(x);
            }
        }
    	
    }
    
    
    static class PointArrayEncoder implements JSONAware /*, JSONStreamAware*/ {

        /**
         * The min value at which the decimal notation is used 
         * (below it, the computerized scientific one is used instead)
         */
        private static final double DECIMAL_MIN = Math.pow(10, -3);
        
        /**
         * The max value at which the decimal notation is used 
         * (above it, the computerized scientific one is used instead)
         */
        private static final double DECIMAL_MAX = Math.pow(10, 7);
        
        PointArray seq;
        double scale;
        
        PointArrayEncoder(PointArray seq, double scale) {
            this.seq = seq;
            this.scale = scale;
        }
        
        public String toJSONString() {
            int size = seq.size();
            
            StringBuilder sb = new StringBuilder();
            if (size > 1) {
                sb.append("["); 
            }
            
            for (int i = 0; i < seq.size(); i++) {
                Position coord = seq.get(i);
                DirectPosition dp = coord.getDirectPosition();
                sb.append("[");
                formatDecimal(dp.getOrdinate(0), sb);
                
                sb.append(",");
                formatDecimal(dp.getOrdinate(1), sb);
                
                if (dp.getDimension() > 2) {
                	sb.append(",");
                    formatDecimal(dp.getOrdinate(2), sb);
                	
                }
                
                sb.append("],");
            }
            sb.setLength(sb.length()-1);
            
            if (size > 1) {
                sb.append("]");
            }
            
            return sb.toString();
        }

        public void writeJSONString(Writer out) throws IOException {
            int size = seq.size();
            
            if (size > 1) {
                out.write("[");
            }
            
            for (int i = 0; i < seq.size(); i++) {
                Position coord = seq.get(i);
                DirectPosition dp = coord.getDirectPosition();
                
                out.write("[");
                out.write(String.valueOf(dp.getOrdinate(0)));
                out.write(",");
                out.write(String.valueOf(dp.getOrdinate(1)));
                if (dp.getDimension() > 2) {
                    out.write(",");
                    out.write(String.valueOf(dp.getOrdinate(2)));
                }
                out.write("]");
                if (i < seq.size()-1) {
                    out.write(",");
                }
            }
            
            if (size > 1) {
                out.write("]");
            }
            
        }
        
        private void formatDecimal(double x, StringBuilder sb) {
            if(Math.abs(x) >= DECIMAL_MIN && x < DECIMAL_MAX) {
                x = Math.floor(x * scale + 0.5) / scale;
                long lx = (long) x;
                if(lx == x)
                    sb.append(lx);
                else
                    sb.append(x);
            } else {
                sb.append(x);
            }
        }
    }
}
