/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
 *    Refractions Research Inc. Can be found on the web at:
 *    http://www.refractions.net/
 */
package org.geotools.data.oracle.sdo;

import java.util.logging.Logger;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

/**
 * Converts a ISO geometry into the equivalent MDSYS.SDO_GEOMETRY SQL syntax. 
 * Useful for non prepared statement based dialects and for debugging purposes 
 * @author Taehoon Kim, Pusan National University
 *
 *
 * @source $URL$
 */
public class SDOSqlDumper {
    private static final Logger LOGGER = org.geotools.util.logging.Logging.getLogger("org.geotools.data.oracle.sdo");

    /**
     * Converts ISO Geometry to a String version of a SDO Geometry.  This
     * should move to a utility class, as we now have more than  one class
     * using this (which is why it changed to public static)
     * 
     * TODO: Multi eometries
     *
     * @param g The ISO Geometry to convert.
     * @param srid DOCUMENT ME!
     *
     * @return A String representation of the SDO Geometry.
     */
    public static String toSDOGeom(Geometry g, int srid) {
        if (Point.class.isAssignableFrom(g.getClass())) {
            return toSDOGeom((Point) g, srid);
        } else if (Curve.class.isAssignableFrom(g.getClass())) {
            return toSDOGeom((Curve) g, srid);
        } else if (Surface.class.isAssignableFrom(g.getClass())) {
            if(g.equals(g.getEnvelope())) {
                return toSDOGeom(g.getEnvelope(), srid);
            } else {
                return toSDOGeom((Surface) g, srid);
            }
        } else if (MultiCurve.class.isAssignableFrom(g.getClass())) {
            return toSDOGeom((MultiCurve) g, srid);
        } else if (MultiSurface.class.isAssignableFrom(g.getClass())) {
            return toSDOGeom((MultiSurface) g, srid);
        }
        else {
            LOGGER.warning("Got a literal geometry that I can't handle: "
                + g.getClass().getName());
            return "";
        }
    }

    /**
     * TODO: Encode more then 1
     * @param line
     * @param srid
     */
    private static String toSDOGeom(MultiCurve curves, int srid) {
         if( curves.getElements().size() == 1 ){
             return toSDOGeom( curves.getElements().iterator().next(), srid);          
         }
         throw new UnsupportedOperationException("Cannot encode MultiLineString (yet)");
    }
    /**
     * TODO: Encode more then 1
     * @param line
     * @param srid
     */
    private static String toSDOGeom(MultiSurface surfaces, int srid) {
    	if( surfaces.getElements().size() == 1 ){
        	return toSDOGeom( surfaces.getElements().iterator().next(), srid);           
        }
        throw new UnsupportedOperationException("Cannot encode MultiPolygon (yet)");
    }

    /**
     * Converts a Point Geometry in an SDO SQL geometry construction statement.
     * 
     *
     * @param point The point to encode.
     * @param srid DOCUMENT ME!
     *
     * @return An SDO SQL geometry object construction statement
     */
    private static String toSDOGeom(Point point, int srid) {
        /*
    	if (SDO.D(point) > 2) {
            LOGGER.warning("" + SDO.D(point)
                + " dimensioned geometry provided."
                + " This encoder only supports 2D geometries. The query will be constructed as"
                + " a 2D query.");
        }
		*/
        StringBuffer buffer = new StringBuffer("MDSYS.SDO_GEOMETRY(");

        buffer.append(SDO.D(point));
        buffer.append("001,");

        if (srid > 0) {
            LOGGER.fine("Using layer SRID: " + srid);
            buffer.append(srid);
        } else {
            LOGGER.fine("Using NULL SRID: ");
            buffer.append("NULL");
        }

        buffer.append(",MDSYS.SDO_POINT_TYPE(");
        buffer.append(point.getDirectPosition().getOrdinate(0));
        buffer.append(",");
        buffer.append(point.getDirectPosition().getOrdinate(1));
        if(point.getCoordinateDimension() == 3) {
        	buffer.append(",");
        	buffer.append(point.getDirectPosition().getOrdinate(2));
        	buffer.append("),NULL,NULL)");
        }
        else {
        	buffer.append(",NULL),NULL,NULL)");	
        }
        

        return buffer.toString();
    }

    /**
     * Converts a LineString Geometry in an SDO SQL geometry construction
     * statement.
     * 
     * <p>
     * 2D geometries is assumed. If higher dimensional geometries are used the
     * query will be encoded as a 2D geometry.
     * </p>
     *
     * @param curve The line to encode.
     * @param srid DOCUMENT ME!
     *
     * @return An SDO SQL geometry object construction statement
     */
    private static String toSDOGeom(Curve curve, int srid) {
    	/*
        if (SDO.D(curve) > 2) {
            LOGGER.warning("" + SDO.D(curve)
                + " dimensioned geometry provided."
                + " This encoder only supports 2D geometries. The query will be constructed as"
                + " a 2D query.");
        }
        */

        StringBuffer buffer = new StringBuffer("MDSYS.SDO_GEOMETRY(");

        buffer.append(SDO.D(curve));
        buffer.append("002,");

        if (srid > 0) {
            LOGGER.fine("Using layer SRID: " + srid);
            buffer.append(srid);
        } else {
            LOGGER.fine("Using NULL SRID: ");
            buffer.append("NULL");
        }

        buffer.append(",NULL,MDSYS.SDO_ELEM_INFO_ARRAY(1,2,1),");
        buffer.append("MDSYS.SDO_ORDINATE_ARRAY(");
        
        LineString result = curve.asLineString(0.0, 0.0);
        PointArray resultPoints = result.getControlPoints();

        for (int i = 0; i < resultPoints.size(); i++) {
        	DirectPosition dp = resultPoints.get(i).getDirectPosition();
            buffer.append(dp.getOrdinate(0));
            buffer.append(",");
            buffer.append(dp.getOrdinate(1));
            if(dp.getDimension() == 3){
            	buffer.append(",");
                buffer.append(dp.getOrdinate(2));
            }

            if (i != (resultPoints.size() - 1)) {
                buffer.append(",");
            }
        }

        buffer.append("))");

        return buffer.toString();
    }
    
    /**
     * Converts a Polygon Geometry in an SDO SQL geometry construction
     * statement.
     * 
     * <p>
     * 2D geometries is assumed. If higher dimensional geometries are used the
     * query will be encoded as a 2D geometry.
     * </p>
     *
     * @param surface The polygon to encode.
     * @param srid DOCUMENT ME!
     *
     * @return An SDO SQL geometry object construction statement
     */
    private static String toSDOGeom(Surface surface, int srid) {
        /*
        if (SDO.D(surface) > 2) {
            LOGGER.warning("" + SDO.D(surface)
                + " dimensioned geometry provided."
                + " This encoder only supports 2D geometries. The query will be constructed as"
                + " a 2D query.");
        }
		*/
    	
    	StringBuffer buffer = new StringBuffer();
        SurfaceBoundary surfaceBoundary = surface.getBoundary();
        if (surfaceBoundary.getExterior() != null) {
            buffer.append("MDSYS.SDO_GEOMETRY(");
            buffer.append(SDO.D(surface));
            buffer.append("003,");

            if (srid > 0) {
                LOGGER.fine("Using layer SRID: " + srid);
                buffer.append(srid);
            } else {
                LOGGER.fine("Using NULL SRID: ");
                buffer.append("NULL");
            }

            buffer.append(",NULL,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,1),");
            buffer.append("MDSYS.SDO_ORDINATE_ARRAY(");
            
            ISOGeometryBuilder builder = new ISOGeometryBuilder(surface.getCoordinateReferenceSystem());
        	PointArray coords = SDO.counterClockWise(builder, SDO.getPointsOfRing(surfaceBoundary.getExterior()));

            for (int i = 0, size = coords.size(); i < size; i++) {
            	DirectPosition cur = coords.get(i).getDirectPosition();
                buffer.append(cur.getOrdinate(0));
                buffer.append(",");
                buffer.append(cur.getOrdinate(1));
                if(cur.getDimension() == 3){
                	buffer.append(",");
                    buffer.append(cur.getOrdinate(2));
                }
                
                if (i != (size - 1)) {
                    buffer.append(",");
                }
        }

            buffer.append("))");
        } else {
            LOGGER.warning("No Exterior ring on polygon.  "
                + "This encode only supports Polygons with exterior rings.");
        }

        if (surfaceBoundary.getInteriors().size() > 0) {
            LOGGER.warning("Polygon contains Interior Rings. "
                + "These rings will not be included in the query.");
        }

        return buffer.toString();
    }
    
    /**
     * Converts an Envelope in an SDO SQL geometry construction statement.
     *
     * @param envelope The envelope to encode.
     * @param srid DOCUMENT ME!
     *
     * @return An SDO SQL geometry object construction statement
     */
    private static String toSDOGeom(Envelope envelope, int srid) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("MDSYS.SDO_GEOMETRY(");
        buffer.append(envelope.getDimension());
        buffer.append("003,");

        if (srid > 0) {
            LOGGER.fine("Using layer SRID: " + srid);
            buffer.append(srid);
        } else {
            LOGGER.fine("Using NULL SRID: ");
            buffer.append("NULL");
        }

        buffer.append(",NULL,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,3),");
        buffer.append("MDSYS.SDO_ORDINATE_ARRAY(");
        buffer.append(envelope.getLowerCorner().getOrdinate(0));
        buffer.append(",");
        buffer.append(envelope.getLowerCorner().getOrdinate(1));
        if(envelope.getDimension() == 3){
        	buffer.append(",");
        	buffer.append(envelope.getLowerCorner().getOrdinate(2));
            
        }
        buffer.append(",");	
        buffer.append(envelope.getUpperCorner().getOrdinate(0));
        buffer.append(",");
        buffer.append(envelope.getUpperCorner().getOrdinate(1));
        if(envelope.getDimension() == 3){
        	buffer.append(",");
        	buffer.append(envelope.getUpperCorner().getOrdinate(2));
        }
        buffer.append("))");

        return buffer.toString();
    }

	public static String toSDOGeom(Solid g, int srid) {
		
		return null;
	}
}
