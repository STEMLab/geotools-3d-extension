/**
 * 
 */
package org.geotools.geometry.iso.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @author hgryoo
 *
 */
public class JTSParser {
	
	public static MultiSurface parseMultiPolygon(ISOGeometryBuilder builder, com.vividsolutions.jts.geom.MultiPolygon mp) {
		
		Set<Surface> surfaces = new HashSet<Surface>();
		for(int i = 0; i < mp.getNumGeometries(); i++) {
			Polygon geom = (Polygon) mp.getGeometryN(i);
			if(!geom.isEmpty()) {
				surfaces.add(parsePolygon(builder, geom));
			}
		}
		
		return builder.createMultiSurface(surfaces);
	}
	
	public static Surface parsePolygon(ISOGeometryBuilder builder, com.vividsolutions.jts.geom.Polygon poly) {
		
		Ring exterior = parseLinearRing(builder, (LinearRing) poly.getExteriorRing());
		
		List<Ring> interiors = new ArrayList<Ring>();
		for(int i = 0; i < poly.getNumInteriorRing(); i++) {
			interiors.add(parseLinearRing(builder, (LinearRing) poly.getInteriorRingN(i)));
		}
		
		SurfaceBoundary sb = builder.createSurfaceBoundary(exterior, interiors);
		return builder.createSurface(sb);
	}
	
	public static Ring parseLinearRing(ISOGeometryBuilder builder, com.vividsolutions.jts.geom.LinearRing lr) {
		Curve c = parseLineString(builder, lr);
		return builder.createRing(Arrays.asList(c));
	}
	
	public static Curve parseLineString(ISOGeometryBuilder builder, com.vividsolutions.jts.geom.LineString l) {
		PointArray pa = builder.createPointArray();
		for(int i = 0; i < l.getNumPoints(); i++) {
			com.vividsolutions.jts.geom.Point jtsP = l.getPointN(i);
			Point isoP = parsePoint(builder, jtsP);
			pa.add(isoP);
		}
		return builder.createCurve(pa);
	}
	
	public static Point parsePoint(ISOGeometryBuilder builder, com.vividsolutions.jts.geom.Point p) {
		com.vividsolutions.jts.geom.Coordinate c = p.getCoordinate();
		DirectPosition dp = parseCoordinate(builder, c);
		return builder.createPoint(dp);
	}
	
	public static DirectPosition parseCoordinate(ISOGeometryBuilder builder, com.vividsolutions.jts.geom.Coordinate c) {
		return builder.createDirectPosition(new double[] {c.x, c.y, c.z} );
	}
	
}
