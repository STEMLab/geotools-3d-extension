/**
 * 
 */
package org.geotools.geometry.iso.util;

import java.util.Collection;

import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.aggregate.MultiSurface;
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
		for(CurveSegment cs : (c.getPrimitive().getSegments())) {
			pa.addAll(cs.getSamplePoints());
		}
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
		
		while(elements.iterator().hasNext()) {
			//TODO : fix assuming curve
			Curve c = (Curve) elements.iterator().next();
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
