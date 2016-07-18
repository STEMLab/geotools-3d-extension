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
package org.geotools.geometry.iso.sfcgal.wrapper;

import java.util.ArrayList;

import junit.framework.TestCase;

/**
 * @author Donguk Seo
 *
 */
public class SFWrapperTest extends TestCase {

        public void testMain() {
                _testSFCoordiante();
                _testSFEnvelope();
                _testSFGeometry();
                _testSFPoint();
                _testSFLineString();
                _testSFSurface();
                _testSFPolygon();
                _testSFTriangle();
                _testSFPolyhedralSurface();
                _testSFTriangulatedSurface();
                _testSFSolid();
                _testSFGeometryCollection();
                _testSFMultiPoint();
        }

        public void _testSFCoordiante() {
                SFCoordinate c = new SFCoordinate(1.4, 2.1);

                assertNotNull(c);
                assertFalse(c.isEmpty());

                /*
                 * System.out.println("c.x() : " + c.x()); System.out.println("c.y() : " + c.y()); System.out.println("c.z() : " + c.z());
                 * System.out.println("coordianteDimension : " + c.coordinateDimension());
                 */
        }

        public void _testSFEnvelope() {
                SFCoordinate c1 = new SFCoordinate(0.0, 0.0, 0.0);
                SFCoordinate c2 = new SFCoordinate(2.0, 2.0, 2.0);
                SFEnvelope envelope1 = new SFEnvelope(c1, c2);

                SFCoordinate c3 = new SFCoordinate(4.0, 0.0, 0.0);
                SFCoordinate c4 = new SFCoordinate(6.0, 2.0, 2.0);
                SFEnvelope envelope2 = new SFEnvelope(c3);
                envelope2.expandToInclude(c4);

                assertNotNull(envelope1);
                assertFalse(envelope1.isEmpty());
                /*
                 * System.out.println("envelope1.is3D() : " + envelope1.is3D()); System.out.println("envelope1.xmin() : " + envelope1.xMin());
                 * System.out.println("envelope1.ymin() : " + envelope1.yMin()); System.out.println("envelope1.zmin() : " + envelope1.zMin());
                 * System.out.println("envelope1.xmax() : " + envelope1.xMax()); System.out.println("envelope1.ymax() : " + envelope1.yMax());
                 * System.out.println("envelope1.zmax() : " + envelope1.zMax()); System.out.println("Envelope.contains(envelope1, envelope2) : " +
                 * SFEnvelope.contains(envelope1, envelope2)); System.out.println("Envelope.overlaps(envelope1, envelope2) : " +
                 * SFEnvelope.overlaps(envelope1, envelope2));
                 */
                assertFalse(SFEnvelope.contains(envelope1, envelope2));
                assertFalse(SFEnvelope.overlaps(envelope1, envelope2));

                SFSolid solid = envelope1.toSolid();
                assertNotNull(solid);
        }

        public void _testSFGeometry() {
                SFGeometry geometry = new SFGeometry();

                System.out.println(geometry.is3D());
        }

        public void _testSFPoint() {
                SFPoint p = new SFPoint(2.0, 1.5, 0);
                SFPoint p2 = new SFPoint(p);

                assertNotNull(p);
                assertNotNull(p2);

                assertFalse(p.isEmpty());

                p.setM(2.5);
                /*
                 * System.out.println("x : " + p.x()); System.out.println("y : " + p.y()); System.out.println("z : " + p.z());
                 * System.out.println("m : " + p.m()); System.out.println("dimension : " + p.dimension()); System.out.println("coordinateDimension : "
                 * + p.coordinateDimension()); System.out.println("wkt : " + p.asText(1)); System.out.println("p == p2 ? " + p.equals(p2));
                 */
                assertTrue(p.equals(p2));
        }

        public void _testSFLineString() {
                SFPoint p1 = new SFPoint(1.1, 2.2, 3.3, 1);
                SFPoint p2 = new SFPoint(0.5, 1.5, 2.5, 2);
                SFPoint p3 = new SFPoint(9.4, 10.2, 1.1, 3);
                SFLineString ls = new SFLineString(p1, p2);

                assertNotNull(ls);
                ls.addPoint(p3);

                assertFalse(ls.isEmpty());
                assertFalse(ls.isClosed());
                /*
                 * System.out.println("coodinateDemension : " + ls.pointN(2).coordinateDimension()); System.out.println("demension : " +
                 * ls.pointN(2).dimension()); System.out .println("ls numpoins : " + ls.numPoints() + " numsegments : " + ls.numSegments());
                 */
                SFGeometry boundary = ls.boundary();
                assertNotNull(boundary);
                /*
                 * System.out.println("ls1 boundary type : " + boundary.geometryType()); System.out.println("ls1 boundary numGeometries : " +
                 * boundary.numGeometries());
                 */
        }

        public void _testSFSurface() {
                SFSurface surface = new SFSurface();

                System.out.println(surface.geometryType());
        }

        public void _testSFPolygon() {
                SFPoint p1 = new SFPoint(1.1, 2.2, 3.3, 1);
                SFPoint p2 = new SFPoint(0.5, 1.5, 2.5, 2);
                SFPoint p3 = new SFPoint(9.4, 10.2, 1.1, 3);
                SFLineString ls = new SFLineString(p1, p2);
                ls.addPoint(p3);
                ls.addPoint(p1);

                ArrayList<SFPoint> points = new ArrayList<SFPoint>();
                points.add(p2);
                points.add(p3);
                points.add(p1);
                points.add(p2);
                SFLineString ls1 = new SFLineString(points);

                SFPolygon polygon = new SFPolygon(ls);
                polygon.addInteriorRing(ls1);

                assertNotNull(polygon);
                assertFalse(polygon.isEmpty());
                if (polygon.isCounterClockWiseOriented()) {
                        System.out.println("counter clockwised");
                } else {
                        System.out.println("not counterclockwised");
                }

        }

        public void _testSFTriangle() {
                SFPoint p1 = new SFPoint(1.1, 2.2, 3.3, 1);
                SFPoint p2 = new SFPoint(0.5, 1.5, 2.5, 2);
                SFPoint p3 = new SFPoint(9.4, 10.2, 1.1, 3);

                SFTriangle tr = new SFTriangle(p1, p2, p3);

                assertNotNull(tr);
                assertFalse(tr.isEmpty());

                /*
                 * System.out.println("tr.vertex(2) coodinateDemension : " + tr.vertex(2).coordinateDimension());
                 * System.out.println("tr.vertex(2) demension : " + tr.vertex(2).dimension()); System.out.println("tr.vertex(2) m : " +
                 * tr.vertex(2).m());
                 */

                SFTriangle tr2 = new SFTriangle(tr);
                assertNotNull(tr2);
                tr2.reverse();
                /*
                 * System.out.println("tr2.vertex(2) coodinateDemension : " + tr2.vertex(2).coordinateDimension());
                 * System.out.println("tr2.vertex(2) demension : " + tr2.vertex(2).dimension()); System.out.println("tr2.vertex(2) m : " +
                 * tr2.vertex(2).m());
                 */
                SFPolygon polygon = tr.toPolygon();
                assertNotNull(polygon);
                // System.out.println("polygon.numRings() " + polygon.numRings());
        }

        public void _testSFPolyhedralSurface() {
                SFPoint p1 = new SFPoint(0, 0, 0);
                SFPoint p2 = new SFPoint(0, -2, 0);
                SFPoint p3 = new SFPoint(2, -2, 0);
                SFPoint p4 = new SFPoint(2, 0, 0);
                SFPoint p5 = new SFPoint(0, 0, 2);
                SFPoint p6 = new SFPoint(0, -2, 2);
                SFPoint p7 = new SFPoint(2, -2, 2);
                SFPoint p8 = new SFPoint(2, 0, 2);
                SFLineString ls1 = new SFLineString();
                ls1.addPoint(p1);
                ls1.addPoint(p2);
                ls1.addPoint(p3);
                ls1.addPoint(p4);
                ls1.addPoint(p1);

                ArrayList<SFPoint> points = new ArrayList<SFPoint>();
                points.add(p3);
                points.add(p4);
                points.add(p8);
                points.add(p7);
                points.add(p3);
                SFLineString ls2 = new SFLineString(points);

                SFLineString ls3 = new SFLineString();
                ls3.addPoint(p5);
                ls3.addPoint(p6);
                ls3.addPoint(p7);
                ls3.addPoint(p8);
                ls3.addPoint(p5);

                SFLineString ls4 = new SFLineString();
                ls4.addPoint(p2);
                ls4.addPoint(p1);
                ls4.addPoint(p5);
                ls4.addPoint(p6);
                ls4.addPoint(p2);
                ArrayList<SFLineString> rings = new ArrayList<SFLineString>();
                rings.add(ls4);

                SFPolygon polygon1 = new SFPolygon();
                polygon1.setExteriorRing(ls1);
                SFPolygon polygon2 = new SFPolygon(ls2);
                SFPolygon polygon3 = new SFPolygon(ls3);
                SFPolygon polygon4 = new SFPolygon(rings);

                ArrayList<SFPolygon> polygons = new ArrayList<SFPolygon>();
                polygons.add(polygon1);
                polygons.add(polygon2);
                polygons.add(polygon3);

                SFPolyhedralSurface polyhedral = new SFPolyhedralSurface(polygons);
                assertNotNull(polyhedral);
                polyhedral.addPolygon(polygon4);

                /*
                 * System.out.println("PolyhedralSurface toString() : " + polyhedral.asText(0));
                 * System.out.println("PolyhedralSurface boundary.toString() : " + polyhedral.boundary().asText(0));
                 * System.out.println("PolyhedralSurface GeometryType() : " + polyhedral.geometryType());
                 * System.out.println("PolyhedralSurface numPolygons() : " + polyhedral.numPolygons());
                 */
        }

        public void _testSFTriangulatedSurface() {
                SFPoint p1 = new SFPoint(1.1, 2.2, 3.3, 1);
                SFPoint p2 = new SFPoint(0.5, 1.5, 2.5, 2);
                SFPoint p3 = new SFPoint(9.4, 10.2, 1.1, 3);
                SFPoint p4 = new SFPoint(2.1, 3.2, 4.3, 1);
                SFPoint p5 = new SFPoint(1.5, 2.5, 3.5, 2);
                SFPoint p6 = new SFPoint(10.4, 11.2, 2.1, 3);
                SFPoint p7 = new SFPoint(3.1, 4.2, 5.3, 1);
                SFPoint p8 = new SFPoint(2.5, 3.5, 4.5, 2);
                SFPoint p9 = new SFPoint(11.4, 12.2, 3.1, 3);
                SFPoint p10 = new SFPoint(4.1, 5.2, 6.3, 1);
                SFPoint p11 = new SFPoint(3.5, 4.5, 5.5, 2);
                SFPoint p12 = new SFPoint(12.4, 13.2, 4.1, 3);

                SFTriangle tr1 = new SFTriangle(p1, p2, p3);
                SFTriangle tr2 = new SFTriangle(p4, p5, p6);
                SFTriangle tr3 = new SFTriangle(p7, p8, p9);
                SFTriangle tr4 = new SFTriangle(p10, p11, p12);

                ArrayList<SFTriangle> triangles = new ArrayList<SFTriangle>();
                triangles.add(tr1);
                triangles.add(tr2);

                SFTriangulatedSurface trs = new SFTriangulatedSurface(triangles);
                assertNotNull(trs);
                assertFalse(trs.isEmpty());

                trs.addTriangle(tr3);
                trs.addTriangle(tr4);

                /*
                 * System.out.println("trs coodinateDemension : " + trs.coordinateDimension()); System.out.println("trs demension : " +
                 * trs.dimension()); System.out.println("trs numTriangles : " + trs.numTriangles()); System.out.println("trs numGeometries : " +
                 * trs.numGeometries()); System.out.println("trs toString : " + trs.toString());
                 */
        }

        public static SFSolid makeSolid(ArrayList<SFPoint> pointList) {
                SFPoint p1 = pointList.get(0);
                SFPoint p5 = pointList.get(4);
                SFPoint p2 = pointList.get(1);
                SFPoint p6 = pointList.get(5);
                SFPoint p3 = pointList.get(2);
                SFPoint p7 = pointList.get(6);
                SFPoint p4 = pointList.get(3);
                SFPoint p8 = pointList.get(7);
                SFLineString ls1 = new SFLineString();
                ls1.addPoint(p1);
                ls1.addPoint(p4);
                ls1.addPoint(p3);
                ls1.addPoint(p2);
                ls1.addPoint(p1);

                ArrayList<SFPoint> points = new ArrayList<SFPoint>();
                points.add(p3);
                points.add(p4);
                points.add(p8);
                points.add(p7);
                points.add(p3);
                SFLineString ls2 = new SFLineString(points);

                SFLineString ls3 = new SFLineString();
                ls3.addPoint(p5);
                ls3.addPoint(p6);
                ls3.addPoint(p7);
                ls3.addPoint(p8);
                ls3.addPoint(p5);

                SFLineString ls4 = new SFLineString();
                ls4.addPoint(p6);
                ls4.addPoint(p5);
                ls4.addPoint(p1);
                ls4.addPoint(p2);
                ls4.addPoint(p6);
                ArrayList<SFLineString> rings = new ArrayList<SFLineString>();
                rings.add(ls4);

                SFLineString ls5 = new SFLineString();
                ls5.addPoint(p2);
                ls5.addPoint(p3);
                ls5.addPoint(p7);
                ls5.addPoint(p6);
                ls5.addPoint(p2);

                SFLineString ls6 = new SFLineString();
                ls6.addPoint(p4);
                ls6.addPoint(p1);
                ls6.addPoint(p5);
                ls6.addPoint(p8);
                ls6.addPoint(p4);

                SFPolygon polygon1 = new SFPolygon();
                polygon1.setExteriorRing(ls1);
                SFPolygon polygon2 = new SFPolygon(ls2);
                SFPolygon polygon3 = new SFPolygon(ls3);
                SFPolygon polygon4 = new SFPolygon(rings);
                SFPolygon polygon5 = new SFPolygon(ls5);
                SFPolygon polygon6 = new SFPolygon(ls6);

                ArrayList<SFPolygon> polygons = new ArrayList<SFPolygon>();
                polygons.add(polygon1);
                polygons.add(polygon2);
                polygons.add(polygon3);
                polygons.add(polygon4);
                polygons.add(polygon5);
                polygons.add(polygon6);

                SFPolyhedralSurface polyhedral = new SFPolyhedralSurface(polygons);
                return new SFSolid(polyhedral);
        }

        public static void _testSFSolid() {
                SFPoint p1 = new SFPoint(0, 0, 0);
                SFPoint p2 = new SFPoint(0, -2, 0);
                SFPoint p3 = new SFPoint(2, -2, 0);
                SFPoint p4 = new SFPoint(2, 0, 0);
                SFPoint p5 = new SFPoint(0, 0, 2);
                SFPoint p6 = new SFPoint(0, -2, 2);
                SFPoint p7 = new SFPoint(2, -2, 2);
                SFPoint p8 = new SFPoint(2, 0, 2);

                ArrayList<SFPoint> points1 = new ArrayList<SFPoint>();
                points1.add(p1);
                points1.add(p2);
                points1.add(p3);
                points1.add(p4);
                points1.add(p5);
                points1.add(p6);
                points1.add(p7);
                points1.add(p8);

                SFSolid solid = makeSolid(points1);
                assertNotNull(solid);

                /*
                 * System.out.println("solid1.asText() : \n\t" + solid.asText(1)); System.out.println("solid1.geometryType() : " +
                 * solid.geometryType()); System.out.println("solid1.geometryTypeId() : " + solid.geometryTypeId());
                 * System.out.println("solid1.numGeometries() : " + solid.numGeometries()); System.out.println("solid1.numInteriorShells() : " +
                 * solid.numInteriorShells()); System.out.println("solid1.numShells() : " + solid.numShells());
                 * System.out.println("solid1.geometryN(0).asText() : " + solid.geometryN(0).asText(0));
                 * System.out.println("solid1.shellN(0).asText(0) : " + solid.shellN(0).asText(0));
                 */
        }

        public void _testSFGeometryCollection() {
                SFPoint p1 = new SFPoint(1.1, 2.2, 3.3, 1);
                SFPoint p2 = new SFPoint(0.5, 1.5, 2.5, 2);
                SFPoint p3 = new SFPoint(9.4, 10.2, 1.1, 3);
                SFGeometryCollection collection = new SFGeometryCollection();
                assertNotNull(collection);

                collection.addGeometry(p1);
                collection.addGeometry(p2);
                collection.addGeometry(p3);

                assertFalse(collection.isEmpty());

                SFLineString ls = new SFLineString();
                ls.addPoint(p1);
                ls.addPoint(p2);
                ls.addPoint(p3);
                collection.addGeometry(ls);

                /*
                 * System.out.println("collection geometryType() : " + collection.geometryType()); System.out.println("collection geometryTypeId() : "
                 * + collection.geometryTypeId()); System.out.println("collection asText() : " + collection.asText(0));
                 * System.out.println("collection numGeometries : " + collection.numGeometries());
                 * System.out.println("collection geometryN(1).geometryType() : " + collection.geometryN(1).geometryType());
                 * System.out.println("collection geometryN(1).asText() : " + collection.geometryN(1).asText(0));
                 */
        }

        public void _testSFMultiPoint() {
                SFPoint p1 = new SFPoint(1.1, 2.2, 3.3, 1);
                SFPoint p2 = new SFPoint(0.5, 1.5, 2.5, 2);
                SFPoint p3 = new SFPoint(9.4, 10.2, 1.1, 3);

                SFMultiPoint multiPoint = new SFMultiPoint();

                assertNotNull(multiPoint);
                multiPoint.addGeometry(p1);
                multiPoint.addGeometry(p2);
                multiPoint.addGeometry(p3);

                assertFalse(multiPoint.isEmpty());

                /*
                 * System.out.println("multiPoint.geomtryType() : " + multiPoint.geometryType()); System.out.println("multiPoint.numGeometries() : " +
                 * multiPoint.numGeometries()); System.out.println("multiPoint.pointN(2).coodinateDemension : " +
                 * multiPoint.pointN(2).coordinateDimension()); System.out.println("multiPoint.pointN(2).demension : " +
                 * multiPoint.pointN(2).dimension()); System.out.println("multiPoint.pointN(2).m : " + multiPoint.pointN(2).m());
                 */
        }

}
