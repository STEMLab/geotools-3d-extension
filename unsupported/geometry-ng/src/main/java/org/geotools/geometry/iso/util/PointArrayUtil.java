/**
 * 
 */
package org.geotools.geometry.iso.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.geotools.geometry.iso.coordinate.LineStringImpl;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.coordinate.LineSegment;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.SurfaceBoundary;

/**
 * @author hgryoo
 *
 */
public class PointArrayUtil {
	
	public static PointArray toList(ISOGeometryBuilder builder, OrientableCurve c) {
		PointArray pa = builder.createPointArray();
		
		List<? extends CurveSegment> segments = c.getPrimitive().getSegments();
		for(int i = 0; i < segments.size() - 1; i++) {
			CurveSegment cs = segments.get(i);
			pa.addAll(cs.getSamplePoints().subList(0, cs.getSamplePoints().size() - 1));
		}
		CurveSegment eCs = segments.get(segments.size() - 1);
		pa.addAll(eCs.getSamplePoints());
		/*CurveSegment tSegment = null;

		// Iterate all CurveSegments (= LineStrings)
		for (int i = 0; i < this.curveSegments.size(); i++) {
			tSegment = this.curveSegments.get(i);

			// TODO: This version only handles the CurveSegment type LineString
			LineStringImpl tLineString = (LineStringImpl) tSegment;

			Iterator<LineSegment> tLineSegmentIter = tLineString
					.asLineSegments().iterator();
			while (tLineSegmentIter.hasNext()) {
				LineSegment tLineSegment = tLineSegmentIter.next();
				// Add new Coordinate, which is the start point of the actual
				// LineSegment
				rList.add( tLineSegment.getStartPoint().getDirectPosition() );
			}
		}
		// Add new Coordinate, which is the end point of the last curveSegment
		rList.add( tSegment.getEndPoint() );
		return rList;*/
		
		return pa;
	}
	
	public static PointArray toList(ISOGeometryBuilder builder, MultiCurve mc) {
		PointArray pa = builder.createPointArray();
		for(OrientableCurve c : mc.getElements()) {
			pa.addAll(toList(builder, c));
		}
		return pa;
	}
	
	
	public static PointArray toList(ISOGeometryBuilder builder, Ring r) {
		PointArray pa = builder.createPointArray();
		Collection<? extends Primitive> elements = r.getElements();
		
		Iterator<? extends Primitive> it = elements.iterator();
		while(it.hasNext()) {
			//TODO : fix assuming curve
			Curve c = (Curve) it.next();
			pa.addAll(toList(builder, c));
		}
		return pa;
	}
	
	public static PointArray toList(ISOGeometryBuilder builder, OrientableSurface s) {
		PointArray pa = builder.createPointArray();
		SurfaceBoundary sb = s.getBoundary();
		
		Ring ext = sb.getExterior();
		pa.addAll(toList(builder, ext));
		
		for(Ring inner : sb.getInteriors()) {
			pa.addAll(toList(builder, inner));
		}
		return pa;
	}
	
	public static PointArray toList(ISOGeometryBuilder builder, MultiSurface mc) {
		PointArray pa = builder.createPointArray();
		for(OrientableSurface s : mc.getElements()) {
			pa.addAll(toList(builder, s));
		}
		return pa;
	}
	
}
