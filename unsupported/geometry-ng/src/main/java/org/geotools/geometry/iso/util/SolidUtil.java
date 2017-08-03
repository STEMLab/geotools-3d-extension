package org.geotools.geometry.iso.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class SolidUtil {

	public static Solid makeFromEnvelope(ISOGeometryBuilder builder, DirectPosition l, DirectPosition u) {

		DirectPosition position1 = builder
				.createDirectPosition(new double[] { l.getOrdinate(0), u.getOrdinate(1), l.getOrdinate(2) }); // LUL
		DirectPosition position2 = builder
				.createDirectPosition(new double[] { l.getOrdinate(0), l.getOrdinate(1), l.getOrdinate(2) }); // LLL
		DirectPosition position3 = builder
				.createDirectPosition(new double[] { u.getOrdinate(0), l.getOrdinate(1), l.getOrdinate(2) }); // ULL
		DirectPosition position4 = builder
				.createDirectPosition(new double[] { u.getOrdinate(0), u.getOrdinate(1), l.getOrdinate(2) }); // UUL
		DirectPosition position5 = builder
				.createDirectPosition(new double[] { l.getOrdinate(0), u.getOrdinate(1), u.getOrdinate(2) }); // LUU
		DirectPosition position6 = builder
				.createDirectPosition(new double[] { l.getOrdinate(0), l.getOrdinate(1), u.getOrdinate(2) }); // LLU
		DirectPosition position7 = builder
				.createDirectPosition(new double[] { u.getOrdinate(0), l.getOrdinate(1), u.getOrdinate(2) }); // ULU
		DirectPosition position8 = builder
				.createDirectPosition(new double[] { u.getOrdinate(0), u.getOrdinate(1), u.getOrdinate(2) }); // UUU

		List<Position> dps1 = new ArrayList<Position>();
		dps1.add(position1);
		dps1.add(position4);
		dps1.add(position3);
		dps1.add(position2);
		dps1.add(position1);

		List<Position> dps2 = new ArrayList<Position>();
		dps2.add(position3);
		dps2.add(position4);
		dps2.add(position8);
		dps2.add(position7);
		dps2.add(position3);

		List<Position> dps3 = new ArrayList<Position>();
		dps3.add(position5);
		dps3.add(position6);
		dps3.add(position7);
		dps3.add(position8);
		dps3.add(position5);

		List<Position> dps4 = new ArrayList<Position>();
		dps4.add(position6);
		dps4.add(position5);
		dps4.add(position1);
		dps4.add(position2);
		dps4.add(position6);

		List<Position> dps5 = new ArrayList<Position>();
		dps5.add(position2);
		dps5.add(position3);
		dps5.add(position7);
		dps5.add(position6);
		dps5.add(position2);

		List<Position> dps6 = new ArrayList<Position>();
		dps6.add(position1);
		dps6.add(position5);
		dps6.add(position8);
		dps6.add(position4);
		dps6.add(position1);

		// create linestring from directpositions
		LineString line1 = builder.createLineString(dps1);
		LineString line2 = builder.createLineString(dps2);
		LineString line3 = builder.createLineString(dps3);
		LineString line4 = builder.createLineString(dps4);
		LineString line5 = builder.createLineString(dps5);
		LineString line6 = builder.createLineString(dps6);

		// create curvesegments from line
		ArrayList<CurveSegment> segs1 = new ArrayList<CurveSegment>();
		segs1.add(line1);
		ArrayList<CurveSegment> segs2 = new ArrayList<CurveSegment>();
		segs2.add(line2);
		ArrayList<CurveSegment> segs3 = new ArrayList<CurveSegment>();
		segs3.add(line3);
		ArrayList<CurveSegment> segs4 = new ArrayList<CurveSegment>();
		segs4.add(line4);
		ArrayList<CurveSegment> segs5 = new ArrayList<CurveSegment>();
		segs5.add(line5);
		ArrayList<CurveSegment> segs6 = new ArrayList<CurveSegment>();
		segs6.add(line6);

		// Create list of OrientableCurves that make up the surface
		OrientableCurve curve1 = builder.createCurve(segs1);
		List<OrientableCurve> orientableCurves1 = new ArrayList<OrientableCurve>();
		orientableCurves1.add(curve1);
		OrientableCurve curve2 = builder.createCurve(segs2);
		List<OrientableCurve> orientableCurves2 = new ArrayList<OrientableCurve>();
		orientableCurves2.add(curve2);
		OrientableCurve curve3 = builder.createCurve(segs3);
		List<OrientableCurve> orientableCurves3 = new ArrayList<OrientableCurve>();
		orientableCurves3.add(curve3);
		OrientableCurve curve4 = builder.createCurve(segs4);
		List<OrientableCurve> orientableCurves4 = new ArrayList<OrientableCurve>();
		orientableCurves4.add(curve4);
		OrientableCurve curve5 = builder.createCurve(segs5);
		List<OrientableCurve> orientableCurves5 = new ArrayList<OrientableCurve>();
		orientableCurves5.add(curve5);
		OrientableCurve curve6 = builder.createCurve(segs6);
		List<OrientableCurve> orientableCurves6 = new ArrayList<OrientableCurve>();
		orientableCurves6.add(curve6);

		// create the interior ring and a list of empty interior rings (holes)
		Ring extRing1 = builder.createRing(orientableCurves1);
		Ring extRing2 = builder.createRing(orientableCurves2);
		Ring extRing3 = builder.createRing(orientableCurves3);
		Ring extRing4 = builder.createRing(orientableCurves4);
		Ring extRing5 = builder.createRing(orientableCurves5);
		Ring extRing6 = builder.createRing(orientableCurves6);

		// create surfaceboundary by rings
		SurfaceBoundary sb1 = builder.createSurfaceBoundary(extRing1, new ArrayList<Ring>());
		SurfaceBoundary sb2 = builder.createSurfaceBoundary(extRing2, new ArrayList<Ring>());
		SurfaceBoundary sb3 = builder.createSurfaceBoundary(extRing3, new ArrayList<Ring>());
		SurfaceBoundary sb4 = builder.createSurfaceBoundary(extRing4, new ArrayList<Ring>());
		SurfaceBoundary sb5 = builder.createSurfaceBoundary(extRing5, new ArrayList<Ring>());
		SurfaceBoundary sb6 = builder.createSurfaceBoundary(extRing6, new ArrayList<Ring>());

		// create the surface
		Surface surface1 = builder.createSurface(sb1);
		Surface surface2 = builder.createSurface(sb2);
		Surface surface3 = builder.createSurface(sb3);
		Surface surface4 = builder.createSurface(sb4);
		Surface surface5 = builder.createSurface(sb5);
		Surface surface6 = builder.createSurface(sb6);

		List<OrientableSurface> surfaces = new ArrayList<OrientableSurface>();
		surfaces.add(surface1);
		surfaces.add(surface2);
		surfaces.add(surface3);
		surfaces.add(surface4);
		surfaces.add(surface5);
		surfaces.add(surface6);

		Shell exteriorShell = builder.createShell(surfaces);
		List<Shell> interiors = new ArrayList<Shell>();

		SolidBoundary solidBoundary = builder.createSolidBoundary(exteriorShell, interiors);
		Solid solid = builder.createSolid(solidBoundary);

		return solid;
	}

	public static Solid makeSolid(ISOGeometryBuilder builder, ArrayList<DirectPosition> points) {
		DirectPosition position1 = points.get(0);
		DirectPosition position2 = points.get(1);
		DirectPosition position3 = points.get(2);
		DirectPosition position4 = points.get(3);
		DirectPosition position5 = points.get(4);
		DirectPosition position6 = points.get(5);
		DirectPosition position7 = points.get(6);
		DirectPosition position8 = points.get(7);

		// create a list of connected positions
		List<Position> dps1 = new ArrayList<Position>();
		dps1.add(position1);
		dps1.add(position4);
		dps1.add(position3);
		dps1.add(position2);
		dps1.add(position1);

		List<Position> dps2 = new ArrayList<Position>();
		dps2.add(position3);
		dps2.add(position4);
		dps2.add(position8);
		dps2.add(position7);
		dps2.add(position3);

		List<Position> dps3 = new ArrayList<Position>();
		dps3.add(position5);
		dps3.add(position6);
		dps3.add(position7);
		dps3.add(position8);
		dps3.add(position5);

		List<Position> dps4 = new ArrayList<Position>();
		dps4.add(position6);
		dps4.add(position5);
		dps4.add(position1);
		dps4.add(position2);
		dps4.add(position6);

		List<Position> dps5 = new ArrayList<Position>();
		dps5.add(position2);
		dps5.add(position3);
		dps5.add(position7);
		dps5.add(position6);
		dps5.add(position2);

		List<Position> dps6 = new ArrayList<Position>();
		dps6.add(position1);
		dps6.add(position5);
		dps6.add(position8);
		dps6.add(position4);
		dps6.add(position1);

		// create linestring from directpositions
		LineString line1 = builder.createLineString(dps1);
		LineString line2 = builder.createLineString(dps2);
		LineString line3 = builder.createLineString(dps3);
		LineString line4 = builder.createLineString(dps4);
		LineString line5 = builder.createLineString(dps5);
		LineString line6 = builder.createLineString(dps6);

		// create curvesegments from line
		ArrayList<CurveSegment> segs1 = new ArrayList<CurveSegment>();
		segs1.add(line1);
		ArrayList<CurveSegment> segs2 = new ArrayList<CurveSegment>();
		segs2.add(line2);
		ArrayList<CurveSegment> segs3 = new ArrayList<CurveSegment>();
		segs3.add(line3);
		ArrayList<CurveSegment> segs4 = new ArrayList<CurveSegment>();
		segs4.add(line4);
		ArrayList<CurveSegment> segs5 = new ArrayList<CurveSegment>();
		segs5.add(line5);
		ArrayList<CurveSegment> segs6 = new ArrayList<CurveSegment>();
		segs6.add(line6);

		// Create list of OrientableCurves that make up the surface
		OrientableCurve curve1 = builder.createCurve(segs1);
		List<OrientableCurve> orientableCurves1 = new ArrayList<OrientableCurve>();
		orientableCurves1.add(curve1);
		OrientableCurve curve2 = builder.createCurve(segs2);
		List<OrientableCurve> orientableCurves2 = new ArrayList<OrientableCurve>();
		orientableCurves2.add(curve2);
		OrientableCurve curve3 = builder.createCurve(segs3);
		List<OrientableCurve> orientableCurves3 = new ArrayList<OrientableCurve>();
		orientableCurves3.add(curve3);
		OrientableCurve curve4 = builder.createCurve(segs4);
		List<OrientableCurve> orientableCurves4 = new ArrayList<OrientableCurve>();
		orientableCurves4.add(curve4);
		OrientableCurve curve5 = builder.createCurve(segs5);
		List<OrientableCurve> orientableCurves5 = new ArrayList<OrientableCurve>();
		orientableCurves5.add(curve5);
		OrientableCurve curve6 = builder.createCurve(segs6);
		List<OrientableCurve> orientableCurves6 = new ArrayList<OrientableCurve>();
		orientableCurves6.add(curve6);

		// create the interior ring and a list of empty interior rings (holes)

		PrimitiveFactoryImpl pmFF = (PrimitiveFactoryImpl) builder.getPrimitiveFactory();

		Ring extRing1 = pmFF.createRing(orientableCurves1);
		Ring extRing2 = pmFF.createRing(orientableCurves2);
		Ring extRing3 = pmFF.createRing(orientableCurves3);
		Ring extRing4 = pmFF.createRing(orientableCurves4);
		Ring extRing5 = pmFF.createRing(orientableCurves5);
		Ring extRing6 = pmFF.createRing(orientableCurves6);

		// create surfaceboundary by rings
		SurfaceBoundary sb1 = pmFF.createSurfaceBoundary(extRing1, new ArrayList<Ring>());
		SurfaceBoundary sb2 = pmFF.createSurfaceBoundary(extRing2, new ArrayList<Ring>());
		SurfaceBoundary sb3 = pmFF.createSurfaceBoundary(extRing3, new ArrayList<Ring>());
		SurfaceBoundary sb4 = pmFF.createSurfaceBoundary(extRing4, new ArrayList<Ring>());
		SurfaceBoundary sb5 = pmFF.createSurfaceBoundary(extRing5, new ArrayList<Ring>());
		SurfaceBoundary sb6 = pmFF.createSurfaceBoundary(extRing6, new ArrayList<Ring>());

		// create the surface
		Surface surface1 = pmFF.createSurface(sb1);
		Surface surface2 = pmFF.createSurface(sb2);
		Surface surface3 = pmFF.createSurface(sb3);
		Surface surface4 = pmFF.createSurface(sb4);
		Surface surface5 = pmFF.createSurface(sb5);
		Surface surface6 = pmFF.createSurface(sb6);

		List<OrientableSurface> surfaces = new ArrayList<OrientableSurface>();
		surfaces.add(surface1);
		surfaces.add(surface2);
		surfaces.add(surface3);
		surfaces.add(surface4);
		surfaces.add(surface5);
		surfaces.add(surface6);

		Shell exteriorShell = pmFF.createShell(surfaces);
		List<Shell> interiors = new ArrayList<Shell>();

		SolidBoundary solidBoundary = pmFF.createSolidBoundary(exteriorShell, interiors);
		Solid solid = pmFF.createSolid(solidBoundary);

		return solid;
	}

	public static List<DirectPosition> make3DPositions(ISOGeometryBuilder gb3D, DirectPosition pa, DirectPosition pb,
			double height) {
		DirectPosition p1 = gb3D.createDirectPosition(new double[] { pa.getOrdinate(0), pa.getOrdinate(1), 0 });
		DirectPosition p2 = gb3D.createDirectPosition(new double[] { pa.getOrdinate(0), pa.getOrdinate(1), height });
		DirectPosition p3 = gb3D.createDirectPosition(new double[] { pb.getOrdinate(0), pb.getOrdinate(1), 0 });
		DirectPosition p4 = gb3D.createDirectPosition(new double[] { pb.getOrdinate(0), pb.getOrdinate(1), height });

		List<DirectPosition> pointList = new ArrayList<DirectPosition>();
		pointList.add(p1);
		pointList.add(p3);
		pointList.add(p4);
		pointList.add(p2);
		pointList.add(p1);

		return pointList;
	}

	public static Surface createSurface(ISOGeometryBuilder gb3D, List<DirectPosition> upper) { // create
																								// one
																								// surface

		LineString line = gb3D.createLineString(upper);

		ArrayList<CurveSegment> cs1 = new ArrayList();
		cs1.add(line);

		OrientableCurve curve1 = gb3D.createCurve(cs1);
		List<OrientableCurve> orientableCurves1 = new ArrayList<OrientableCurve>();
		orientableCurves1.add(curve1);

		Ring extRing1 = gb3D.createRing(orientableCurves1);

		SurfaceBoundary sb1 = gb3D.createSurfaceBoundary(extRing1, new ArrayList<Ring>());

		Surface surface = gb3D.createSurface(sb1);

		return surface;
	}

	public static Solid createSolid(ISOGeometryBuilder gb3D, List<Surface> list) {

		List<OrientableSurface> surfaces = new ArrayList<OrientableSurface>();
		for (int i = 0; i < list.size(); i++) {
			surfaces.add(list.get(i));
		}
		Shell exteriorShell = gb3D.createShell(surfaces);
		List<Shell> interiors = new ArrayList<Shell>();

		SolidBoundary solidBoundary = gb3D.createSolidBoundary(exteriorShell, interiors);
		Solid solid = gb3D.createSolid(solidBoundary);

		return solid;
	}

	public static Surface[] makeLid(ISOGeometryBuilder gb3D, List<DirectPosition> coordList, double height) {
		List<DirectPosition> upper = new ArrayList<DirectPosition>();
		List<DirectPosition> lower = new ArrayList<DirectPosition>();
		List<DirectPosition> reversedLower = new ArrayList<DirectPosition>();
		Surface upSurface = null;
		Surface downSurface = null;
		try {
			for (int i = 0; i < coordList.size(); i++) {
				double[] point = coordList.get(i).getCoordinate();
				DirectPosition tempUpper = gb3D.createDirectPosition(new double[] { point[0], point[1], height });
				DirectPosition tempLower = gb3D.createDirectPosition(new double[] { point[0], point[1], 0 });
				upper.add(tempUpper);
				lower.add(tempLower);
			}
			DirectPosition firstPointLower = gb3D.createDirectPosition(
					new double[] { coordList.get(0).getCoordinate()[0], coordList.get(0).getCoordinate()[1], 0 });
			DirectPosition firstPointUpper = gb3D.createDirectPosition(
					new double[] { coordList.get(0).getCoordinate()[0], coordList.get(0).getCoordinate()[1], height });

			lower.add(firstPointLower);
			upper.add(firstPointUpper);

			Collections.reverse(lower);
			upSurface = createSurface(gb3D, upper);
			downSurface = createSurface(gb3D, lower);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return new Surface[] { upSurface, downSurface };
	}

	public static List<Surface> makeSurfaces(ISOGeometryBuilder gb3D, List<DirectPosition> points, double h) {
		List<Surface> sfList = new ArrayList<Surface>();
		for (int i = 0; i < points.size() - 1; i++) {
			List<DirectPosition> temp = (List<DirectPosition>) make3DPositions(gb3D, points.get(i), points.get(i + 1),
					h);
			sfList.add(createSurface(gb3D, temp));
		}
		Surface[] lids = makeLid(gb3D, points, h);
		sfList.add(lids[0]);
		sfList.add(lids[1]);

		return sfList;
	}

	public static List<DirectPosition> makeDirectPositions(ISOGeometryBuilder gb3D, Object geometry) {
		List<DirectPosition> points = null;
		if (geometry instanceof com.vividsolutions.jts.geom.MultiPolygon) {
			points = makeDirectPositionsFromMultiPolygon(gb3D, (MultiPolygon) geometry);
		} else if (geometry instanceof com.vividsolutions.jts.geom.Polygon) {
			points = makeDirectPositionsFromPolygon(gb3D, (Polygon) geometry);
		} else if (geometry instanceof org.geotools.geometry.iso.primitive.SurfaceImpl) {
			points = makeDirectPositionsFromSurface(gb3D, (Surface) geometry);
		}

		return points;

	}

	public static List<DirectPosition> makeDirectPositionsFromMultiPolygon(ISOGeometryBuilder gb, MultiPolygon mp) {
		Coordinate[] coordList = mp.getCoordinates();
		// PointArray points = gb.createPointArray();
		List<DirectPosition> points = new ArrayList<DirectPosition>();

		for (int i = 0; i < coordList.length; i++) {
			// change jts.Coordinate to primitive.DirectPosition
			DirectPosition temp = gb.createDirectPosition(new double[] { coordList[i].x, coordList[i].y, 0 });
			points.add(temp);
		}
		return points;
	}

	public static List<DirectPosition> makeDirectPositionsFromPolygon(ISOGeometryBuilder gb, Polygon p) {
		Coordinate[] coordList = p.getCoordinates();
		// PointArray points = gb.createPointArray();
		List<DirectPosition> points = new ArrayList<DirectPosition>();

		for (int i = 0; i < coordList.length; i++) {
			// change jts.Coordinate to primitive.DirectPosition
			DirectPosition temp = gb.createDirectPosition(new double[] { coordList[i].x, coordList[i].y, 0 });
			points.add(temp);
		}
		return points;
	}

	public static List<DirectPosition> makeDirectPositionsFromSurface(ISOGeometryBuilder gb, Surface s) {
		List<DirectPosition> points = new ArrayList<DirectPosition>();
		SurfaceBoundary sb = s.getBoundary();
		Ring extr = sb.getExterior();
		List<OrientableCurve> ocList = (List<OrientableCurve>) extr.getElements();
		OrientableCurve oc = ocList.get(0);
		Curve c = oc.getPrimitive();
		List<LineString> ls = (List<LineString>) c.getSegments();
		LineString l = ls.get(0);
		List<Position> tempPoints = l.getControlPoints();
		for (int i = 0; i < tempPoints.size(); i++) {
			Position tempPosition = tempPoints.get(i);
			points.add(tempPosition.getDirectPosition());
		}

		return points;
	}

	public static Solid createSolidWithHeight(ISOGeometryBuilder gb3D, Object geometry, double h) {
		Solid s;
		List<DirectPosition> points = makeDirectPositions(gb3D, geometry);
		List<Surface> sfList = makeSurfaces(gb3D, points, h);
		s = createSolid(gb3D, sfList);
		return s;
	}

	public static Solid createSolidWithHeight(ISOGeometryBuilder gb3D, Polygon p, double h) {
		Solid s;
		List<DirectPosition> points = makeDirectPositionsFromPolygon(gb3D, p);
		List<Surface> sfList = makeSurfaces(gb3D, points, h);
		s = createSolid(gb3D, sfList);
		return s;
	}

	public static Solid createSolidWithHeight(ISOGeometryBuilder gb3D, MultiPolygon mp, double h) {
		Solid s;
		List<DirectPosition> points = makeDirectPositionsFromMultiPolygon(gb3D, mp);
		List<Surface> sfList = makeSurfaces(gb3D, points, h);
		s = createSolid(gb3D, sfList);
		return s;
		// PointArray points = gb.createPointArray();
		/*
		 * List<DirectPosition> points = new ArrayList<DirectPosition>();
		 * 
		 * for(int i = 0 ; i < coordList.length ; i++){ //change jts.Coordinate
		 * to primitive.DirectPosition DirectPosition temp =
		 * gb.createDirectPosition(new double[]{coordList[i].x,
		 * coordList[i].y}); points.add(temp); }
		 */

		// List<LineString>lsList = new ArrayList<LineString>();

		/*
		 * List<Surface>sfList = new ArrayList<Surface>(); for(int i = 0 ; i <
		 * points.size()-1; i++){ List<DirectPosition>temp =
		 * (List<DirectPosition>)
		 * make3DPositions(points.get(i),points.get(i+1),h);
		 * sfList.add(createSurface(temp)); }
		 * 
		 * List<DirectPosition>temp = (List<DirectPosition>)
		 * make3DPositions(points.get(points.size()-1),points.get(0),h);
		 * sfList.add(createSurface(temp));
		 * 
		 * Surface[] lids = makeLid(points,h); sfList.add(lids[0]);
		 * sfList.add(lids[1]);
		 */

		// create list of LineString
		// call createLineStringWithHeight with input each 2 temp
		// call createSolidWithHeight with input of upper.

	}

	///// ForTest
}
