/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2015, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.geometry.iso.sfcgal.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.geometry.iso.aggregate.AggregateFactoryImpl;
import org.geotools.geometry.iso.aggregate.MultiCurveImpl;
import org.geotools.geometry.iso.aggregate.MultiPointImpl;
import org.geotools.geometry.iso.aggregate.MultiPrimitiveImpl;
import org.geotools.geometry.iso.aggregate.MultiSolidImpl;
import org.geotools.geometry.iso.aggregate.MultiSurfaceImpl;
import org.geotools.geometry.iso.coordinate.DirectPositionImpl;
import org.geotools.geometry.iso.coordinate.GeometryFactoryImpl;
import org.geotools.geometry.iso.coordinate.PolygonImpl;
import org.geotools.geometry.iso.coordinate.PolyhedralSurfaceImpl;
import org.geotools.geometry.iso.coordinate.TriangleImpl;
import org.geotools.geometry.iso.coordinate.TriangulatedSurfaceImpl;
import org.geotools.geometry.iso.primitive.CurveImpl;
import org.geotools.geometry.iso.primitive.PointImpl;
import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.geometry.iso.primitive.RingImplUnsafe;
import org.geotools.geometry.iso.primitive.SolidImpl;
import org.geotools.geometry.iso.primitive.SurfaceBoundaryImpl;
import org.geotools.geometry.iso.primitive.SurfaceImpl;
import org.geotools.geometry.iso.sfcgal.wrapper.SFAlgorithm;
import org.geotools.geometry.iso.sfcgal.wrapper.SFGeometry;
import org.geotools.geometry.iso.sfcgal.wrapper.SFGeometryCollection;
import org.geotools.geometry.iso.sfcgal.wrapper.SFLineString;
import org.geotools.geometry.iso.sfcgal.wrapper.SFMultiLineString;
import org.geotools.geometry.iso.sfcgal.wrapper.SFMultiPoint;
import org.geotools.geometry.iso.sfcgal.wrapper.SFMultiPolygon;
import org.geotools.geometry.iso.sfcgal.wrapper.SFMultiSolid;
import org.geotools.geometry.iso.sfcgal.wrapper.SFPoint;
import org.geotools.geometry.iso.sfcgal.wrapper.SFPolygon;
import org.geotools.geometry.iso.sfcgal.wrapper.SFPolyhedralSurface;
import org.geotools.geometry.iso.sfcgal.wrapper.SFSolid;
import org.geotools.geometry.iso.sfcgal.wrapper.SFTriangle;
import org.geotools.geometry.iso.sfcgal.wrapper.SFTriangulatedSurface;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.AggregateFactory;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.coordinate.PolyhedralSurface;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.coordinate.Triangle;
import org.opengis.geometry.coordinate.TriangulatedSurface;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.geometry.primitive.SurfacePatch;

/**
 * @author Donguk Seo
 *
 */
public class SFCGALConvertor {
        
        // GeometryType
        public static final int SFCGAL_POINT_ID = 1;

        public static final int SFCGAL_LINESTRING_ID = 2;

        public static final int SFCGAL_POLYGON_ID = 3;

        public static final int SFCGAL_MULTIPOINT_ID = 4;

        public static final int SFCGAL_MULTILINESTRING_ID = 5;

        public static final int SFCGAL_MULTIPOLYGON_ID = 6;

        public static final int SFCGAL_GEOMETRYCOLLECTION_ID = 7;

        public static final int SFCGAL_POLYHEDRALSURFACE_ID = 15;

        public static final int SFCGAL_TRIANGULATEDSURFACE_ID = 16;

        public static final int SFCGAL_TRIANGLE_ID = 100;

        public static final int SFCGAL_SOLID_ID = 101;

        public static final int SFCGAL_MULTISOLID_ID = 102;

        private static ISOGeometryBuilder builder = null;

        static {
                Hints hints = GeoTools.getDefaultHints();
                hints.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D);
                hints.put(Hints.GEOMETRY_VALIDATE, false);
                builder = new ISOGeometryBuilder(hints);
        }

        /**
         * Convert a DirectPosition instance to a SFPoint
         * @param p DirectPosition instance
         * @return Instance of SFPoint
         */
        public static SFPoint directPositionToSFCGALPoint(DirectPosition p) {
                SFPoint point = null;
                double[] coord = p.getCoordinate();
                double x = coord[0];
                double y = coord[1];
                double z;

                if (coord.length == 3) {
                        z = coord[2];
                        point = new SFPoint(x, y, z);
                } else {
                        point = new SFPoint(x, y);
                }

                return point;
        }

        /**
         * Convert a Point instance to a SFPoint
         * @param p Point instance
         * @return Instance of SFPoint
         */
        public static SFPoint pointToSFCGALPoint(Point p) {
                SFPoint point = null;
                point = directPositionToSFCGALPoint(p.getDirectPosition());

                return point;
        }

        /**
         * Convert a LineString instance to a SFLineString
         * @param ls LineString instance
         * @return Instance of SFLineString
         */
        public static SFLineString lineStringToSFCGALLineString(LineString ls) {
                SFLineString lineString = null;
                PointArray controlPoints = ls.getControlPoints();

                ArrayList<SFPoint> points = new ArrayList<SFPoint>();
                for (int i = 0; i < controlPoints.size(); i++) {
                        DirectPosition position = controlPoints.get(i).getDirectPosition();

                        points.add(directPositionToSFCGALPoint(position));
                }

                lineString = new SFLineString(points);

                return lineString;
        }

        /**
         * Convert a Curve instance to a SFLineString
         * @param curvge Curve instance
         * @return Instance of SFLineString
         */
        public static SFLineString curveToSFCGALLineString(Curve curve) {

                return lineStringToSFCGALLineString(curve.asLineString(0.0, 0.0));
        }

        /**
         * Convert a Ring instance to a SFLineString
         * @param ring Ring instance
         * @return Instance of SFLineString
         */
        public static SFLineString ringToSFCGALLineString(Ring ring) {
                SFLineString lineString = null;
                
                List<OrientableCurve> generators = ring.getGenerators();
                Iterator elementIter = generators.iterator();

                Curve element = (Curve) elementIter.next();
                LineString result = element.asLineString(0.0, 0.0);
                PointArray resultPoints = result.getControlPoints();
                while (elementIter.hasNext()) {
                    element = (Curve) elementIter.next();
                    LineString nextLine = element.asLineString(0.0, 0.0);
                    
                    if (nextLine.getEndPoint().equals(result.getStartPoint())) {
                        LinkedList<Position> posToAdd = new LinkedList<Position>(nextLine.getControlPoints());
                        posToAdd.removeLast();
                        resultPoints.addAll(0, posToAdd);
                    } else if (result.getEndPoint().equals(nextLine.getStartPoint())) {
                        LinkedList<Position> posToAdd = new LinkedList<Position>(nextLine.getControlPoints());
                        posToAdd.removeFirst();
                        resultPoints.addAll(posToAdd);
                    } else {
                        throw new IllegalArgumentException("The LineString do not agree in a start and end point");
                    }
                }
                
                ArrayList<SFPoint> points = new ArrayList<SFPoint>();
                Iterator iter = resultPoints.iterator();
                while (iter.hasNext()) {
                    Position position = (Position) iter.next();
                    points.add(directPositionToSFCGALPoint(position.getDirectPosition()));
                }
                lineString = new SFLineString(points);
                
                return lineString;
        }

        /**
         * Convert a Polygon instance to a SFPolygon
         * @param poly Polygon instance
         * @return Instance of SFPolygon
         */
        public static SFPolygon polygonToSFCGALPolygon(Polygon poly) {
                SFPolygon polygon = null;
                ArrayList<SFLineString> rings = new ArrayList<SFLineString>();

                Ring exterior = poly.getBoundary().getExterior();
                List<Ring> interiors = poly.getBoundary().getInteriors();

                rings.add(ringToSFCGALLineString(exterior));

                Iterator interiorIt = interiors.iterator();
                while (interiorIt.hasNext()) {
                        rings.add(ringToSFCGALLineString((Ring) interiorIt.next()));
                }

                polygon = new SFPolygon(rings);

                return polygon;
        }

        /**
         * Convert a Triangle instance to a SFTriangle
         * @param tri Triangle instance
         * @return Instance of SFTriangle
         */
        public static SFTriangle triangleToSFCGALTriangle(Triangle tri) {
                SFTriangle triangle = null;
                List<Position> corners = tri.getCorners();

                SFPoint p = directPositionToSFCGALPoint(corners.get(0).getDirectPosition());
                SFPoint q = directPositionToSFCGALPoint(corners.get(1).getDirectPosition());
                SFPoint r = directPositionToSFCGALPoint(corners.get(2).getDirectPosition());

                triangle = new SFTriangle(p, q, r);

                return triangle;
        }

        /**
         * Convert a Surface instance to a SFPolygon
         * @param surface Surface instance
         * @return Instance of SFPolygon
         */
        public static SFPolygon surfaceToSFCGALPolygon(Surface surface) {
                SFPolygon polygon = null;
                ArrayList<SFLineString> rings = new ArrayList<SFLineString>();

                Ring exterior = surface.getBoundary().getExterior();
                List<Ring> interiors = surface.getBoundary().getInteriors();

                rings.add(ringToSFCGALLineString(exterior));

                if(interiors != null) {
	                Iterator iter = interiors.iterator();
	                while (iter.hasNext()) {
	                        rings.add(ringToSFCGALLineString((Ring) iter.next()));
	                }
                }

                polygon = new SFPolygon(rings);

                return polygon;
        }

        /**
         * Convert a PolyhedralSurface instance to a SFPolyhedralSurface
         * @param polyhedral PolyhedralSurface instance
         * @return Instance of SFPolyhedralSurface
         */
        public static SFPolyhedralSurface polyhedralSurfaceToSFCGALPolyhedralSurface(
                        PolyhedralSurface polyhedral) {
                SFPolyhedralSurface polyhedralSurface = null;
                List<? extends Polygon> patches = polyhedral.getPatches();

                ArrayList<SFPolygon> polygons = new ArrayList<SFPolygon>();
                Iterator iter = patches.iterator();
                while (iter.hasNext()) {
                        if (iter.next() instanceof PolygonImpl) {
                                polygons.add(polygonToSFCGALPolygon((Polygon) iter.next()));
                        } else if (iter.next() instanceof TriangleImpl) {
                                SFTriangle triangle = triangleToSFCGALTriangle((Triangle) iter
                                                .next());
                                polygons.add(triangle.toPolygon());
                        }
                }

                polyhedralSurface = new SFPolyhedralSurface(polygons);

                return polyhedralSurface;
        }

        /**
         * Convert a TriangulatedSurface instance to a SFTriangulatedSurface
         * @param triangulated TriangulatedSurface instance
         * @return Instance of SFTriangulatedSurface
         */
        public static SFTriangulatedSurface triangulatedSurfaceToSFCGALPolyhedralSurface(
                        TriangulatedSurface triangulated) {
                SFTriangulatedSurface triangulatedSurface = null;
                List<Triangle> patches = triangulated.getPatches();

                ArrayList<SFTriangle> triangles = new ArrayList<SFTriangle>();
                Iterator iter = patches.iterator();
                while (iter.hasNext()) {
                        SFTriangle triangle = triangleToSFCGALTriangle((Triangle) iter.next());
                        triangles.add(triangle);
                }

                triangulatedSurface = new SFTriangulatedSurface(triangles);

                return triangulatedSurface;
        }

        /**
         * Convert a Shell instance to a SFPolyhedralSurface
         * @param shell Shell instance
         * @return Instance of SFPolyhedralSurface
         */
        public static SFPolyhedralSurface shellToSFCGALPolyhedralSurface(Shell shell) {
                SFPolyhedralSurface polyhedral = null;
                ArrayList<SFPolygon> polygons = new ArrayList<SFPolygon>();
                List<OrientableSurface> surfaces = (List<OrientableSurface>) shell.getElements();

                Iterator iter = surfaces.iterator();
                while (iter.hasNext()) {
                        polygons.add(surfaceToSFCGALPolygon((Surface) iter.next()));
                }

                polyhedral = new SFPolyhedralSurface(polygons);

                return polyhedral;
        }

        /**
         * Convert a Solid instance to a SFSolid
         * @param solid Solid instance
         * @return Instance of SFPSolid
         */
        public static SFSolid solidToSFCGALSolid(Solid solid) {
                SFSolid sfcgalSolid = null;
                Shell exterior = solid.getBoundary().getExterior();
                Shell[] interiors = solid.getBoundary().getInteriors();

                ArrayList<SFPolyhedralSurface> shells = new ArrayList<SFPolyhedralSurface>();
                shells.add(shellToSFCGALPolyhedralSurface(exterior));

                if (interiors != null) {
                        for (int i = 0; i < interiors.length; i++) {
                                shells.add(shellToSFCGALPolyhedralSurface(interiors[i]));
                        }
                }

                sfcgalSolid = new SFSolid(shells);

                return sfcgalSolid;
        }

        /**
         * Convert a MultiPrimitive instance to a SFGeometryCollection
         * @param multiPrimitive MultiPrimitive instance
         * @return Instance of SFGeometryCollection
         */
        public static SFGeometryCollection multiPrimitiveToSFCGALGeometryCollection(
                        MultiPrimitive multiPrimitive) {
                SFGeometryCollection sfcgalGeometryCollection = new SFGeometryCollection();
                Set<Primitive> elements = (Set<Primitive>) multiPrimitive.getElements();

                Iterator iter = elements.iterator();
                while (iter.hasNext()) {
                        sfcgalGeometryCollection
                                        .addGeometry(geometryToSFCGALGeometry((Geometry) iter
                                                        .next()));
                }

                return sfcgalGeometryCollection;
        }

        /**
         * Convert a MultiPoint instance to a SFMultiPoint
         * @param multiPoint MultiPoint instance
         * @return Instance of SFMultiPoint
         */
        public static SFMultiPoint multiPointToSFCGALMultiPoint(MultiPoint multiPoint) {
                SFMultiPoint sfcgalMultiPoint = new SFMultiPoint();
                Set<Point> elements = multiPoint.getElements();

                Iterator iter = elements.iterator();
                while (iter.hasNext()) {
                        sfcgalMultiPoint.addGeometry(pointToSFCGALPoint((Point) iter.next()));
                }

                return sfcgalMultiPoint;
        }

        /**
         * Convert a MultiCurve instance to a SFMultiLineString
         * @param multiCurve MultiCurve instance
         * @return Instance of SFMultiCurve
         */
        public static SFMultiLineString multiCurveToSFCGALMultiLineString(MultiCurve multiCurve) {
                SFMultiLineString sfcgalMultiLineString = new SFMultiLineString();
                Set<OrientableCurve> elements = multiCurve.getElements();

                Iterator iter = elements.iterator();
                while (iter.hasNext()) {
                        sfcgalMultiLineString.addGeometry(curveToSFCGALLineString((Curve) iter
                                        .next()));
                }

                return sfcgalMultiLineString;
        }

        /**
         * Convert a MultiSurface instance to a SFMultiPolygon
         * @param multiSurface MultiSurface instance
         * @return Instance of SFMultiPolygon
         */
        public static SFMultiPolygon multiSurfaceToSFCGALMultiPolygon(MultiSurface multiSurface) {
                SFMultiPolygon sfcgalMultiPolygon = new SFMultiPolygon();
                Set<OrientableSurface> elements = multiSurface.getElements();

                Iterator iter = elements.iterator();
                while (iter.hasNext()) {
                        sfcgalMultiPolygon
                                        .addGeometry(surfaceToSFCGALPolygon((Surface) iter.next()));
                }

                return sfcgalMultiPolygon;
        }

        /**
         * Convert a MultiSolidImpl instance to a SFMultiSolid
         * @param multiSolid MultiSolidImpl instance
         * @return Instance of SFMultiSolid
         */
        public static SFMultiSolid multiSolidToSFCGALMultiSolid(MultiSolidImpl multiSolid) {
                SFMultiSolid sfcgalMultiSolid = new SFMultiSolid();
                Set<Primitive> elements = multiSolid.getElements();

                Iterator iter = elements.iterator();
                while (iter.hasNext()) {
                        sfcgalMultiSolid.addGeometry(solidToSFCGALSolid((Solid) iter.next()));
                }

                return sfcgalMultiSolid;
        }


        /**
         * Convert a SFPoint instance to a DirectPosition
         * @param p SFPoint instance
         * @return Instance of DirectPosition
         */
        public static DirectPosition directPositionFromSFCGALPoint(SFPoint p) {
                /*
                 * Hints hints = GeoTools.getDefaultHints(); hints.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D); hints.put(Hints.GEOMETRY_VALIDATE,
                 * false); GeometryBuilder builder = new GeometryBuilder(hints);
                 */
                DirectPosition position = null;

                double[] coord = new double[p.coordinateDimension()];
                coord[0] = p.x();
                coord[1] = p.y();
                if (p.coordinateDimension() >= 3)
                        coord[2] = p.z();
                if (p.coordinateDimension() == 4)
                        coord[3] = p.m();
                position = builder.createDirectPosition(coord);

                return position;
        }

        /**
         * Convert a SFPoint instance to a Point
         * @param p SFPoint instance
         * @return Instance of Point
         */
        public static Point pointFromSFCGALPoint(SFPoint p) {
                /*
                 * Hints hints = GeoTools.getDefaultHints(); hints.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D); hints.put(Hints.GEOMETRY_VALIDATE,
                 * false); GeometryBuilder builder = new GeometryBuilder(hints);
                 */
                Point point = builder.createPoint(directPositionFromSFCGALPoint(p));

                return point;
        }

        /**
         * Convert a SFLineString instance to a LineString
         * @param ls SFLineString instance
         * @return Instance of LineString
         */
        public static LineString lineStringFromSFCGALLineString(SFLineString ls) {
                LineString lineString = null;
                List<Position> positions = new ArrayList<Position>();

                for (int i = 0; i < ls.numPoints(); i++) {
                        DirectPosition position = directPositionFromSFCGALPoint(ls.pointN(i));

                        positions.add(position);
                }

                lineString = builder.createLineString(positions);

                return lineString;
        }

        /**
         * Convert a SFLineString instance to a Curve
         * @param ls SFLineString instance
         * @return Instance of Curve
         */
        public static Curve curveFromSFCGALLineString(SFLineString ls) {
                Curve curve = null;
                LineString lineString = lineStringFromSFCGALLineString(ls);
                List<CurveSegment> segments = new ArrayList<CurveSegment>();
                segments.add(lineString);

                curve = builder.createCurve(segments);

                return curve;
        }

        /**
         * Convert a SFLineString instance to a Ring
         * @param ls SFLineString instance
         * @return Instance of Ring
         */
        public static Ring ringFromSFCGALLineString(SFLineString ls) {
                Ring ring = null;

                if (!ls.isClosed())
                        return null;

                Curve curve = curveFromSFCGALLineString(ls);
                List<OrientableCurve> curves = new ArrayList<OrientableCurve>();
                curves.add(curve);

                ring = builder.createRing(curves);

                return ring;
        }

        /**
         * Convert a SFPolygon instance to a Polygon
         * @param poly SFPolygon instance
         * @return Instance of Polygon
         */
        public static Polygon polygonFromSFCGALPolygon(SFPolygon poly) {
                Polygon polygon = null;

                Ring exterior = ringFromSFCGALLineString(poly.exteriorRing());

                List<OrientableCurve> interiors = new ArrayList<OrientableCurve>();
                for (int i = 0; i < poly.numInteriorRings(); i++) {
                        Ring interior = ringFromSFCGALLineString(poly.interiorRingN(i));

                        interiors.add(interior);
                }

                SurfaceBoundary boundary = builder.createSurfaceBoundary(exterior, interiors);
                polygon = builder.createPolygon(boundary);

                return polygon;
        }

        /**
         * Convert a SFTriangle instance to a Triangle
         * @param tri SFTriangle instance
         * @return Instance of Triangle
         */
        public static Triangle triangleFromSFCGALTriangle(SFTriangle tri) {
                Triangle triangle = null;
                SFPolygon poly = tri.toPolygon();
                Polygon polygon = polygonFromSFCGALPolygon(poly);

                DirectPosition position1 = directPositionFromSFCGALPoint(tri.vertex(0));
                DirectPosition position2 = directPositionFromSFCGALPoint(tri.vertex(1));
                DirectPosition position3 = directPositionFromSFCGALPoint(tri.vertex(2));

                GeometryFactoryImpl geometryFactory = (GeometryFactoryImpl) builder
                                .getGeometryFactory();
                triangle = geometryFactory.createTriangle(
                                (TriangulatedSurface) polygon.getBoundary(), position1, position2,
                                position3);

                return triangle;
        }

        /**
         * Convert a SFPolygon instance to a Surface
         * @param poly SFPolygon instance
         * @return Instance of Surface
         */
        public static Surface surfaceFromSFCGALPolygon(SFPolygon poly) {
                Surface surface = null;
                Polygon polygon = polygonFromSFCGALPolygon(poly);

                surface = builder.createSurface(polygon.getBoundary());

                return surface;
        }

        /**
         * Convert a SFTriangle instance to a Surface
         * @param tri SFTriangle instance
         * @return Instance of Surface
         */
        public static Surface surfaceFromSFCGALTriangle(SFTriangle tri) {
                SFPolygon poly = tri.toPolygon();

                return surfaceFromSFCGALPolygon(poly);
        }

        /**
         * Convert a SFPolyhedralSurface instance to a PolyhedralSurface
         * @param polyhedral SFPolyhedralSurface instance
         * @return Instance of PolyhedralSurface
         */
        public static PolyhedralSurface polyhedralSurfaceFromSFCGALPolyhedralSurface(
                        SFPolyhedralSurface polyhedral) {
                PolyhedralSurface polyhedralSurface = null;

                SFGeometry boundary = polyhedral.boundary();
                SFGeometry merge = boundary.geometryN(0);
                if (boundary.geometryTypeId() == SFCGAL_GEOMETRYCOLLECTION_ID
                                || boundary.geometryTypeId() == SFCGAL_MULTILINESTRING_ID) {
                        merge = SFAlgorithm.union3D(merge, boundary);
                }
                merge = getSFGeometry(merge);

                Curve exterior = curveFromSFCGALLineString((SFLineString) merge);
                SurfaceBoundary surfaceBoundary = builder.createSurfaceBoundary(exterior);
                polyhedralSurface = new PolyhedralSurfaceImpl((SurfaceBoundaryImpl) surfaceBoundary);

                return polyhedralSurface;
        }

        /**
         * Convert a SFTriangulatedSurface instance to a TriangulatedSurface
         * @param triangulated SFTriangulatedSurface instance
         * @return Instance of TriangulatedSurface
         */
        public static TriangulatedSurface triangulatedSurfaceFromSFCGALTriangulatedSurface(
                        SFTriangulatedSurface triangulated) {
                TriangulatedSurface triangulatedSurface = null;

                SFGeometry boundary = triangulated.boundary();
                System.out.println("boundary of TIN : " + boundary.asText(1));
                SFGeometry merge = boundary.geometryN(0);
                if (boundary.geometryTypeId() == SFCGAL_GEOMETRYCOLLECTION_ID
                                || boundary.geometryTypeId() == SFCGAL_MULTILINESTRING_ID) {
                        merge = SFAlgorithm.union3D(merge, boundary);
                }
                merge = getSFGeometry(merge);

                Curve exterior = curveFromSFCGALLineString((SFLineString) merge);
                SurfaceBoundary surfaceBoundary = builder.createSurfaceBoundary(exterior);
                triangulatedSurface = new TriangulatedSurfaceImpl(
                                (SurfaceBoundaryImpl) surfaceBoundary);

                return triangulatedSurface;
        }

        /**
         * Convert a SFPolyhedralSurface instance to a Shell
         * @param polyhedral SFPolyhedralSurface instance
         * @return Instance of Shell
         */
        public static Shell shellFromSFCGALPolyhedralSurface(SFPolyhedralSurface polyhedral) {
                Shell shell = null;
                PrimitiveFactoryImpl primitiveFactory = (PrimitiveFactoryImpl) builder
                                .getPrimitiveFactory();

                List<OrientableSurface> orientableSurfaces = new ArrayList<OrientableSurface>();
                for (int i = 0; i < polyhedral.numPolygons(); i++) {
                        orientableSurfaces.add(surfaceFromSFCGALPolygon(polyhedral.polygonN(i)));
                }

                shell = primitiveFactory.createShell(orientableSurfaces);

                return shell;
        }

        /**
         * Convert a SFSolid instance to a Solid
         * @param sfSolid SFSolid instance
         * @return Instance of Solid
         */
        public static Solid solidFromSFCGALSolid(SFSolid sfSolid) {
                Solid solid = null;
                PrimitiveFactoryImpl primitiveFactory = (PrimitiveFactoryImpl) builder
                                .getPrimitiveFactory();

                Shell exterior = shellFromSFCGALPolyhedralSurface(sfSolid.exteriorShell());

                List<Shell> interiors = new ArrayList<Shell>();
                for (int i = 0; i < sfSolid.numInteriorShells(); i++) {
                        Shell interior = shellFromSFCGALPolyhedralSurface(sfSolid
                                        .interiorShellN(i));

                        interiors.add(interior);
                }

                SolidBoundary boundary = primitiveFactory.createSolidBoundary(exterior, interiors);
                solid = builder.createSolid(boundary);

                return solid;
        }

        /**
         * Convert a SFGeometryCollection instance to a MultiPrimitive
         * @param geometryCollection SFGeometryCollection instance
         * @return Instance of MultiPrimitive
         */
        public static MultiPrimitive multiPrimitiveFromSFCGALGeometryCollection(
                        SFGeometryCollection geometryCollection) {
                MultiPrimitive multiPrimitive = null;
                AggregateFactory aggregateFactory = (AggregateFactoryImpl) builder
                                .getAggregateFactory();

                Set<Primitive> primitives = new HashSet<Primitive>();
                for (int i = 0; i < geometryCollection.numGeometries(); i++) {
                        Geometry g = geometryFromSFCGALGeometry(geometryCollection.geometryN(i));

                        primitives.add((Primitive) g);
                }

                multiPrimitive = aggregateFactory.createMultiPrimitive(primitives);

                return multiPrimitive;
        }

        /**
         * Convert a SFMultiPoint instance to a MultiPoint
         * @param sfMultiPoint SFMultiPoint instance
         * @return Instance of MultiPoint
         */
        public static MultiPoint multiPointFromSFCGALMultiPoint(SFMultiPoint sfMultiPoint) {
                MultiPoint multiPoint = null;
                AggregateFactory aggregateFactory = builder.getAggregateFactory();

                Set<Point> points = new HashSet<Point>();
                for (int i = 0; i < sfMultiPoint.numGeometries(); i++) {
                        Point p = pointFromSFCGALPoint(sfMultiPoint.pointN(i));

                        points.add(p);
                }

                multiPoint = aggregateFactory.createMultiPoint(points);

                return multiPoint;
        }

        /**
         * Convert a SFMultiLineString instance to a MultiCurve
         * @param sfMultiLineString SFMultiLineString instance
         * @return Instance of MultiCurve
         */
        public static MultiCurve multiCurveFromSFCGALMultiLineString(
                        SFMultiLineString sfMultiLineString) {
                MultiCurve multiCurve = null;
                AggregateFactory aggregateFactory = builder.getAggregateFactory();

                Set<OrientableCurve> curves = new HashSet<OrientableCurve>();
                for (int i = 0; i < sfMultiLineString.numGeometries(); i++) {
                        Curve curve = curveFromSFCGALLineString(sfMultiLineString
                                        .lineStringN(i));

                        curves.add(curve);
                }

                multiCurve = aggregateFactory.createMultiCurve(curves);

                return multiCurve;
        }

        /**
         * Convert a SFMultiPolygon instance to a MultiSurface
         * @param sfMultiPolygon SFMultiPolygon instance
         * @return Instance of MultiSurface
         */
        public static MultiSurface multiSurfaceFromSFCGALMultiPolygon(
                        SFMultiPolygon sfMultiPolygon) {
                MultiSurface multiSurface = null;
                AggregateFactory aggregateFactory = builder.getAggregateFactory();

                Set<OrientableSurface> surfaces = new HashSet<OrientableSurface>();
                for (int i = 0; i < sfMultiPolygon.numGeometries(); i++) {
                        Surface surface = surfaceFromSFCGALPolygon(sfMultiPolygon.polygonN(i));

                        surfaces.add(surface);
                }

                multiSurface = aggregateFactory.createMultiSurface(surfaces);

                return multiSurface;
        }

        /**
         * Convert a SFMultiSolid instance to a MultiSolid
         * @param sfMultiSolid SFMultiSolid instance
         * @return Instance of MultiSolid
         */
        public static MultiSolidImpl multiSolidFromSFCGALMultiSolid(SFMultiSolid sfMultiSolid) {
                MultiSolidImpl multiSolid = null;
                AggregateFactoryImpl aggregateFactory = (AggregateFactoryImpl) builder
                                .getAggregateFactory();

                Set<Primitive> solids = new HashSet<Primitive>();
                for (int i = 0; i < sfMultiSolid.numGeometries(); i++) {
                        Solid solid = solidFromSFCGALSolid(sfMultiSolid.solidN(i));

                        solids.add(solid);
                }

                multiSolid = (MultiSolidImpl) aggregateFactory.createMultiSolid(solids);

                return multiSolid;
        }

        public static SFGeometry getSFGeometry(SFGeometry geom) {
                SFGeometry geometry = geom;

                if ((geom.geometryTypeId() == SFCGAL_POINT_ID) && !(geom instanceof SFPoint)) {
                        geometry = new SFPoint(geom);
                } else if ((geom.geometryTypeId() == SFCGAL_LINESTRING_ID)
                                && !(geom instanceof SFLineString)) {
                        geometry = new SFLineString(geom);
                } else if ((geom.geometryTypeId() == SFCGAL_POLYGON_ID)
                                && !(geom instanceof SFPolygon)) {
                        geometry = new SFPolygon(geom);
                } else if ((geom.geometryTypeId() == SFCGAL_TRIANGLE_ID)
                                && !(geom instanceof SFTriangle)) {
                        geometry = new SFTriangle(geom);
                } else if ((geom.geometryTypeId() == SFCGAL_POLYHEDRALSURFACE_ID)
                                && !(geom instanceof SFPolyhedralSurface)) {
                        geometry = new SFPolyhedralSurface(geom);
                } else if ((geom.geometryTypeId() == SFCGAL_TRIANGULATEDSURFACE_ID)
                                && !(geom instanceof SFTriangulatedSurface)) {
                        geometry = new SFTriangulatedSurface(geom);
                } else if ((geom.geometryTypeId() == SFCGAL_SOLID_ID) && !(geom instanceof SFSolid)) {
                        geometry = new SFSolid(geom);
                } else if ((geom.geometryTypeId() == SFCGAL_GEOMETRYCOLLECTION_ID)
                                && !(geom instanceof SFGeometryCollection)) {
                        geometry = new SFGeometryCollection();
                        for (int i = 0; i < geom.numGeometries(); i++) {
                                SFGeometry g = getSFGeometry(geom.geometryN(i));
                                ((SFGeometryCollection) geometry).addGeometry(g);
                        }
                } else if ((geom.geometryTypeId() == SFCGAL_MULTIPOINT_ID)
                                && !(geom instanceof SFMultiPoint)) {
                        geometry = new SFMultiPoint(geom);
                } else if ((geom.geometryTypeId() == SFCGAL_MULTILINESTRING_ID)
                                && !(geom instanceof SFMultiLineString)) {
                        geometry = new SFMultiLineString(geom);
                } else if ((geom.geometryTypeId() == SFCGAL_MULTIPOLYGON_ID)
                                && !(geom instanceof SFMultiPolygon)) {
                        geometry = new SFMultiPolygon(geom);
                } else if ((geom.geometryTypeId() == SFCGAL_MULTISOLID_ID)
                                && !(geom instanceof SFMultiSolid)) {
                        geometry = new SFMultiSolid(geom);
                }

                return geometry;
        }

        /**
         * Convert a SFGeometry instance to a Geometry
         * @param geom SFGeometry instance
         * @return Instance of Geometry
         */
        public static Geometry geometryFromSFCGALGeometry(SFGeometry geom) {
                Geometry geometry = null;
                geom = getSFGeometry(geom);

                if (geom.geometryTypeId() == SFCGAL_POINT_ID) {
                        geometry = pointFromSFCGALPoint((SFPoint) geom);
                } else if (geom.geometryTypeId() == SFCGAL_LINESTRING_ID) {
                        geometry = curveFromSFCGALLineString((SFLineString) geom);
                } else if (geom.geometryTypeId() == SFCGAL_POLYGON_ID) {
                        geometry = surfaceFromSFCGALPolygon((SFPolygon) geom);
                } else if (geom.geometryTypeId() == SFCGAL_TRIANGLE_ID) {
                        geometry = surfaceFromSFCGALTriangle((SFTriangle) geom);
                } else if (geom.geometryTypeId() == SFCGAL_POLYHEDRALSURFACE_ID) {
                        geometry = polyhedralSurfaceFromSFCGALPolyhedralSurface((SFPolyhedralSurface) geom);
                } else if (geom.geometryTypeId() == SFCGAL_TRIANGULATEDSURFACE_ID) {
                        geometry = triangulatedSurfaceFromSFCGALTriangulatedSurface((SFTriangulatedSurface) geom);
                } else if (geom.geometryTypeId() == SFCGAL_SOLID_ID) {
                        geometry = solidFromSFCGALSolid((SFSolid) geom);
                } else if (geom.geometryTypeId() == SFCGAL_GEOMETRYCOLLECTION_ID) {
                        geometry = multiPrimitiveFromSFCGALGeometryCollection((SFGeometryCollection) geom);
                } else if (geom.geometryTypeId() == SFCGAL_MULTIPOINT_ID) {
                        geometry = multiPointFromSFCGALMultiPoint((SFMultiPoint) geom);
                } else if (geom.geometryTypeId() == SFCGAL_MULTILINESTRING_ID) {
                        geometry = multiCurveFromSFCGALMultiLineString((SFMultiLineString) geom);
                } else if (geom.geometryTypeId() == SFCGAL_MULTIPOLYGON_ID) {
                        geometry = multiSurfaceFromSFCGALMultiPolygon((SFMultiPolygon) geom);
                } else if (geom.geometryTypeId() == SFCGAL_MULTISOLID_ID) {
                        geometry = multiSolidFromSFCGALMultiSolid((SFMultiSolid) geom);
                }

                return geometry;
        }

        /**
         * Convert a Geometry instance to a SFGeometry
         * @param geom Geometry instance
         * @return Instance of SFGeometry
         */
        public static SFGeometry geometryToSFCGALGeometry(Geometry geom) {
                SFGeometry geometry = null;

                if (geom instanceof DirectPositionImpl) {
                        geometry = SFCGALConvertor
                                        .directPositionToSFCGALPoint((DirectPosition) geom);
                } else if (geom instanceof PointImpl) {
                        geometry = SFCGALConvertor.pointToSFCGALPoint((Point) geom);
                } else if (geom instanceof CurveImpl) {
                        geometry = SFCGALConvertor.curveToSFCGALLineString((Curve) geom);
                } else if (geom instanceof RingImplUnsafe) {
                        geometry = SFCGALConvertor.ringToSFCGALLineString((Ring) geom);
                } else if (geom instanceof SurfaceImpl) {
                        List<SurfacePatch> patches = (ArrayList<SurfacePatch>) ((Surface) geom)
                                        .getPatches();
                        SurfacePatch patch = patches.get(0);

                        if (patches.size() == 1 && (patch instanceof TriangleImpl)) {
                                geometry = SFCGALConvertor
                                                .triangleToSFCGALTriangle((Triangle) patch);
                        } else {
                                geometry = SFCGALConvertor.surfaceToSFCGALPolygon((Surface) geom);
                        }
                } else if (geom instanceof PolyhedralSurfaceImpl) {
                        geometry = SFCGALConvertor
                                        .polyhedralSurfaceToSFCGALPolyhedralSurface((PolyhedralSurface) geom);
                } else if (geom instanceof TriangulatedSurfaceImpl) {
                        geometry = SFCGALConvertor
                                        .triangulatedSurfaceToSFCGALPolyhedralSurface((TriangulatedSurface) geom);
                } else if (geom instanceof SolidImpl) {
                        geometry = SFCGALConvertor.solidToSFCGALSolid((Solid) geom);
                } else if (geom instanceof MultiPrimitiveImpl) {
                        geometry = SFCGALConvertor
                                        .multiPrimitiveToSFCGALGeometryCollection((MultiPrimitive) geom);
                } else if (geom instanceof MultiPointImpl) {
                        geometry = SFCGALConvertor.multiPointToSFCGALMultiPoint((MultiPoint) geom);
                } else if (geom instanceof MultiCurveImpl) {
                        geometry = SFCGALConvertor
                                        .multiCurveToSFCGALMultiLineString((MultiCurve) geom);
                } else if (geom instanceof MultiSurfaceImpl) {
                        geometry = SFCGALConvertor
                                        .multiSurfaceToSFCGALMultiPolygon((MultiSurface) geom);
                } else if (geom instanceof MultiSolidImpl) {
                        geometry = SFCGALConvertor
                                        .multiSolidToSFCGALMultiSolid((MultiSolidImpl) geom);
                }

                return geometry;
        }
}
