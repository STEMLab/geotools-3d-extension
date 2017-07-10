package org.geotools.data.postgis3d;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.geotools.data.jdbc.iso.GeometryToSQL;
import org.geotools.geometry.iso.aggregate.MultiSolidImpl;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.complex.CompositeCurve;
import org.opengis.geometry.complex.CompositePoint;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

public class GeometryToPostGISWKTString extends GeometryToSQL{

	public GeometryToPostGISWKTString(boolean lineBreak) {
		super(lineBreak);
	
	}
	
	/*public String solidToString(Solid s) {
	        return "Solid(" + solidBoundaryCoordToString((SolidBoundary) s.getBoundary()) + ")";
	}*/
	protected String solidToString(Solid s) 
	{
        return "POLYHEDRALSURFACE(" + shellCoordToString((Shell)s.getBoundary().getExterior()) + ")";
	}
}