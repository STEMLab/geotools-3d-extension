package org.geotools.geometry.iso.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.geotools.geometry.iso.coordinate.LineStringImpl;
import org.geotools.geometry.iso.primitive.CurveImpl;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.LineSegment;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.geometry.primitive.SurfacePatch;

/**
 * @author hgryoo
 *
 */
public class TriangulationUtil {
	
	public SurfacePatch triangulateSurface(SurfacePatch s) {
		SurfaceBoundary sb = s.getBoundary();
		Ring exterior = sb.getExterior();
		List<DirectPosition> extPos = asDirectPositions(exterior);
		
		List<Ring> interiors = sb.getInteriors();
		List< List<DirectPosition> > intPos = new ArrayList<List<DirectPosition>>();
		for(Ring r : interiors) {
			List<DirectPosition> pos = asDirectPositions(r);
			intPos.add(pos);
		}
		
		return null;
	}
	
	public List<DirectPosition> asDirectPositions(Ring r) {
		// Iterate all Curves
		List<DirectPosition> rList = new ArrayList<DirectPosition>();
		Collection<? extends Primitive> elements = r.getElements();
		for (Primitive p : elements) {
			Curve c = (Curve) p;
			Iterator<? extends CurveSegment> tCurveSegmentIter = c.getSegments()
					.iterator();
			CurveSegment tSegment = null;

			// Iterate all CurveSegments (= LineStrings)
			while (tCurveSegmentIter.hasNext()) {
				tSegment = tCurveSegmentIter.next();

				// TODO: This version only handles the CurveSegment type
				// LineString
				LineStringImpl tLineString = (LineStringImpl) tSegment;

				Iterator<LineSegment> tLineSegmentIter = tLineString
						.asLineSegments().iterator();
				while (tLineSegmentIter.hasNext()) {
					LineSegment tLineSegment = tLineSegmentIter.next();
					// Add new Coordinate, which is the start point of the
					// actual LineSegment
					rList.add( tLineSegment.getStartPoint());
				}
			}
			// Add new Coordinate, which is the end point of the last
			// curveSegment
			rList.add( tSegment.getEndPoint());
		}
		return rList;
	}
	
	public double[] asDoubleArray(List<DirectPosition> list) {
		if(list.isEmpty() || list.size() == 0) {
			return null;
		}
		
		int dimension = list.get(0).getDimension();
		double[] arr = new double[list.size() * dimension];
		
		for(int i = 0; i < list.size(); i++) {
			DirectPosition p = list.get(i);
			
			arr[i * dimension] = p.getOrdinate(0);
			arr[i * dimension + 1] = p.getOrdinate(1);
			if(dimension == 3) arr[i * dimension + 2]  = p.getOrdinate(2);
		}
		return arr;
	}
}
