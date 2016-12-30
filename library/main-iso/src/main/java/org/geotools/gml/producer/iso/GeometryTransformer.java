/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
 *
 */
/*
 * GeometryTransformer.java
 *
 * Created on October 24, 2003, 1:08 PM
 */
package org.geotools.gml.producer.iso;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.geotools.referencing.CRS;
import org.geotools.referencing.CRS.AxisOrder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.util.logging.Logging;
import org.geotools.xml.transform.TransformerBase;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * Used to walk through GeometryObjects issuing SAX events
 * as needed.
 * <p>
 * Please note that this GeometryTransformer issues GML2 events,
 * the Coordinate 
 *
 * @author Ian Schneider
 *
 *
 * @source $URL$
 */
public class GeometryTransformer extends TransformerBase {
    
    static final Logger LOGGER = Logging.getLogger(GeometryTransformer.class);
    
    protected boolean useDummyZ = false;
    
    protected int numDecimals = 4;
    
    public void setUseDummyZ(boolean flag){
        useDummyZ = flag;
    }
   
    public void setNumDecimals(int num) {
    	numDecimals = num;
    }
    
    /**
     * @TODO remove constant from GometryTraslator contructor call
     */
    public org.geotools.xml.transform.Translator createTranslator(
            ContentHandler handler) {
        return new GeometryTranslator(handler, numDecimals, useDummyZ);
    }
    
    public static class GeometryTranslator extends TranslatorSupport {
        protected CoordinateWriter coordWriter = new CoordinateWriter();
        
        public GeometryTranslator(ContentHandler handler) {            
            this(handler,"gml",GMLUtils.GML_URL);
        }
       
        public GeometryTranslator(ContentHandler handler, String prefix, String nsUri ) {
            super(handler,prefix,nsUri);
            coordWriter.setPrefix( prefix );
            coordWriter.setNamespaceUri( nsUri );
        }
        
        public GeometryTranslator(ContentHandler handler, int numDecimals) {
            this(handler,"gml",GMLUtils.GML_URL,numDecimals);
        }

        public GeometryTranslator(ContentHandler handler, String prefix, String nsUri, int numDecimals) {
            this(handler,prefix,nsUri);
            coordWriter = new CoordinateWriter(numDecimals, false);
            coordWriter.setPrefix( prefix );
            coordWriter.setNamespaceUri( nsUri );
        }
        
        public GeometryTranslator(ContentHandler handler, int numDecimals, boolean isDummyZEnabled) {
            this(handler,"gml",GMLUtils.GML_URL,numDecimals,isDummyZEnabled);
        }
        
        public GeometryTranslator(ContentHandler handler, String prefix, String nsUri, int numDecimals, boolean isDummyZEnabled) {
            this(handler,prefix,nsUri);
            coordWriter = new CoordinateWriter(numDecimals, isDummyZEnabled);
            coordWriter.setPrefix( prefix );
            coordWriter.setNamespaceUri( nsUri );
        }
        /**
         * Constructor for GeometryTranslator allowing the specification of the number of
         * valid dimension represented in the Coordinates.
         * @param handler
         * @param prefix
         * @param nsUri
         * @param numDecimals
         * @param isDummyZEnabled
         * @param dimension If this value is 3; the coordinate.z will be used rather than dummyZ
         * since 2.4.1
         */
        public GeometryTranslator(ContentHandler handler, String prefix, String nsUri, int numDecimals, boolean isDummyZEnabled, int dimension) {
            this(handler,prefix,nsUri);
            coordWriter = new CoordinateWriter(numDecimals, isDummyZEnabled, dimension );
            coordWriter.setPrefix( prefix );
            coordWriter.setNamespaceUri( nsUri );
        }        
        public boolean isDummyZEnabled(){
            return coordWriter.isDummyZEnabled();
        }
        
        public int getNumDecimals(){
            return coordWriter.getNumDecimals();
        }
        
        public void encode(Object o, String srsName)
        throws IllegalArgumentException {
            if (o instanceof Geometry) {
            	Geometry geom = (Geometry) o;
                encode(geom, srsName, geom.getCoordinateDimension());
            } else {
                throw new IllegalArgumentException("Unable to encode " + o);
            }
        }
        
        public void encode(Object o) throws IllegalArgumentException {
            encode(o, null);
        }
        
        public void encode(Envelope bounds) {
            encode(bounds, null);
        }
        
        public void encode(Envelope bounds, String srsName) {
            // DJB: old behavior for null bounds:
            //
            //<gml:Box srsName="http://www.opengis.net/gml/srs/epsg.xml#0">
            //<gml:coordinates decimal="." cs="," ts=" ">0,0 -1,-1</gml:coordinates>
            //</gml:Box>
            //
            // new behavior:
            // <gml:null>unknown</gml:null>
            if(bounds == null) {
            	encodeNullBounds();
                
                return; // we're done!
            }
            String boxName = boxName();
            
            if ((srsName == null) || srsName.equals("")) {
                start(boxName);
            } else {
                AttributesImpl atts = new AttributesImpl();
                atts.addAttribute("", "srsName", "srsName", "", srsName);
                start(boxName, atts);
            }
            
            try {
            	DirectPosition lower = bounds.getLowerCorner();
            	DirectPosition upper = bounds.getUpperCorner();
            	
                /*double[] coords = new double[4];
                coords[0] = bounds.getMinX();
                coords[1] = bounds.getMinY();
                coords[2] = bounds.getMaxX();
                coords[3] = bounds.getMaxY();
                CoordinateSequence coordSeq = new PackedCoordinateSequence.Double(coords, 2);*/
            	CoordinateReferenceSystem crs = null;
            	if(bounds.getCoordinateReferenceSystem() == null) {
            		crs = DefaultGeographicCRS.WGS84_3D;
            	} else {
            		crs = bounds.getCoordinateReferenceSystem();
            	}
            	
            	ISOGeometryBuilder builder = new ISOGeometryBuilder(crs);
            	PointArray pa = builder.createPointArray();
            	pa.add(lower);
            	pa.add(upper);
                coordWriter.writeCoordinates(pa, contentHandler);
            } catch (SAXException se) {
                throw new RuntimeException(se);
            }
            
            end(boxName);
        }
        
        /**
         * Method to be subclasses in order to allow for gml3 encoding for null enevelope.
         */
        protected void encodeNullBounds() {
        	start("null");
            String text = "unknown";
            try{
                contentHandler.characters(text.toCharArray(), 0, text.length());
            } catch(Exception e) //this shouldnt happen!!
            {
                System.out.println("got exception while writing null boundedby:"+e.getLocalizedMessage());
                e.printStackTrace();
            }
            end("null");
        }
        
        /**
         * Method to be subclassed in order to allow for gml3 encoding of envelopes.
         * @return "Box"
         */
        protected String boxName() {
        	return "Box";
        }
        
        /**
         * Encodes the given geometry with no srsName attribute and forcing 2D
         */
        public void encode(Geometry geometry) {
            String srsName = null;
            Integer dimension = null;
            // see if we have a EPSG CRS attached to the geometry
            try {
                CoordinateReferenceSystem crs = geometry.getCoordinateReferenceSystem();
                dimension = crs.getCoordinateSystem().getDimension();
                Integer code = CRS.lookupEpsgCode(crs, false);
                if(code != null) {
                    if(AxisOrder.NORTH_EAST.equals(CRS.getAxisOrder(crs))) {
                        srsName = "urn:ogc:def:crs:EPSG::" + code;
                    } else {
                        srsName = "EPSG:" + code;
                    }
                }
            } catch(Exception e) {
                LOGGER.fine("Failed to encode the CoordinateReferenceSystem into a srsName");
            }
            encode(geometry, srsName, dimension);
        }

        /**
         * Encodes the given geometry with the provided srsName attribute and for the specified dimensions
         * @param geometry non null geometry to encode
         * @param srsName srsName attribute for the geometry, or <code>null</code>
         * @param dimensions shall laid between 1, 2, or 3. Number of coordinate dimensions to force.
         * TODO: dimensions is not being taken into account currently. Jody?
         */
        public void encode(Geometry geometry, String srsName, final int dimensions) {
            String geomName = GMLUtils.getGeometryName(geometry);
            
            if ((srsName == null) || srsName.equals("")) {
                start(geomName);
            } else {
                AttributesImpl atts = new AttributesImpl();
                atts.addAttribute("", "srsName", "srsName", "", srsName);
                start(geomName, atts);
            }
            
            int geometryType = GMLUtils.getGeometryType(geometry);
            
            ISOGeometryBuilder builder = new ISOGeometryBuilder(geometry.getCoordinateReferenceSystem());
            PointArray coordSeq = builder.createPointArray();
            switch (geometryType) {
                case GMLUtils.POINT:
                    DirectPosition dp = ((Point) geometry).getDirectPosition();
                    coordSeq.add(dp);
                    try {
                        coordWriter.writeCoordinates(coordSeq, contentHandler);
                    } catch (SAXException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case GMLUtils.LINESTRING:
                    List<? extends CurveSegment> segments = ((Curve) geometry).getSegments();
            		for(CurveSegment cs : segments) {
                		for(Position p : cs.getSamplePoints()) {
                			if(coordSeq.size() == 0 || !coordSeq.get(coordSeq.size() - 1).equals(p)) {
                				coordSeq.add(p);
                			}
                		}
                	}
                    try {
                        coordWriter.writeCoordinates(coordSeq, contentHandler);
                    } catch (SAXException s) {
                        throw new RuntimeException(s);
                    }
    
                    break;
                    
                case GMLUtils.POLYGON:
                    writePolygon((Surface) geometry);
                    break;
                case GMLUtils.MULTIPOINT:
                case GMLUtils.MULTILINESTRING:
                case GMLUtils.MULTIPOLYGON:
                case GMLUtils.MULTIGEOMETRY:
                    writeMulti((MultiPrimitive) geometry,
                            GMLUtils.getMemberName(geometryType));
                    
                    break;
            }
            
            end(geomName);
        }
        
		private void writePolygon(Surface geometry) {
            String outBound = "outerBoundaryIs";
            String lineRing = "LinearRing";
            String inBound = "innerBoundaryIs";
            start(outBound);
            start(lineRing);
            

            SurfaceBoundary boundary = geometry.getBoundary();
            
            ISOGeometryBuilder builder = new ISOGeometryBuilder(geometry.getCoordinateReferenceSystem());
            
            try {
            	PointArray pa = builder.createPointArray();
            	Ring exterior = boundary.getExterior();
            	
            	//TODO assuming that only one element
            	Collection<? extends Primitive> elements = exterior.getElements();
            	Curve c = (Curve) elements.iterator().next();
            	
            	List<? extends CurveSegment> segments = c.getSegments();
            	
            	for(CurveSegment cs : segments) {
            		for(Position p : cs.getSamplePoints()) {
            			if(pa.size() == 0 || !pa.get(pa.size() - 1).equals(p)) {
            				pa.add(p);
            			}
            		}
            	}
                coordWriter.writeCoordinates(pa, contentHandler);
            } catch (SAXException s) {
                throw new RuntimeException(s);
            }
            
            end(lineRing);
            end(outBound);
            
            for (Ring r : boundary.getInteriors()) {
                start(inBound);
                start(lineRing);
                
                try {
                	PointArray pa = builder.createPointArray();
                	//TODO assuming that only one element
                	Collection<? extends Primitive> elements = r.getElements();
                	Curve c = (Curve) elements.iterator().next();
                	
                	List<? extends CurveSegment> segments = c.getSegments();
                	
                	for(CurveSegment cs : segments) {
                		for(Position p : cs.getSamplePoints()) {
                			if(pa.size() == 0 || !pa.get(pa.size() - 1).equals(p)) {
                				pa.add(p);
                			}
                		}
                	}
                    coordWriter.writeCoordinates(pa, contentHandler);
                } catch (SAXException s) {
                    throw new RuntimeException(s);
                }
                
                end(lineRing);
                end(inBound);
            }
        }
        
        private void writeMulti(MultiPrimitive geometry, String member) {
            for (Geometry p : geometry.getElements()) {
                start(member);
                
                encode(p);
                
                end(member);
            }
        }
    }
}
