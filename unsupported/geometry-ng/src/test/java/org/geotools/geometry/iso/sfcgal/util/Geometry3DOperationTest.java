/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2015, Spatio-temporal Databases Laboratory(STEMLab)
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
import java.util.List;

import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.geometry.iso.io.wkt.ParseException;
import org.geotools.geometry.iso.io.wkt.WKTReader;
import org.geotools.geometry.iso.primitive.CurveImpl;
import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.geometry.iso.primitive.RingImplUnsafe;
import org.geotools.geometry.iso.primitive.SurfaceImpl;
import org.geotools.geometry.iso.root.GeometryImpl;
import org.geotools.geometry.iso.sfcgal.wrapper.SFSolid;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

import junit.framework.TestCase;

/**
 * @author Donguk Seo
 *
 */
public class Geometry3DOperationTest extends TestCase {
        private static Hints hints = null;

        private static ISOGeometryBuilder builder = null;

        public void testMain() {
                hints = GeoTools.getDefaultHints();
                hints.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D);
                hints.put(Hints.GEOMETRY_VALIDATE, false);
                builder = new ISOGeometryBuilder(hints);

                // _testPointPoint();
                // _testPointCurve();
                // _testPointSurface();
                // _testPointSolid();
                // _testCurveCurve();
                // _testCurveSurface();
                // _testCurveSolid();
                // _testSurfaceSurface();
                // _testSurfaceSolid();
                // _testSolidSolid();
                testPointInSolid();
        }
        
        public void testPointInSolid() {
                Solid solid = getSolids(builder).get(8);
                
                Point point = builder.createPoint(-161949.5098996643, 8871.603480360383, 0.0);
                
                System.out.println(solid.toString());
                System.out.println(solid.contains(point));
        }

        public void _testPointPoint() {
                Point point1 = builder.createPoint(0, 0, 0);
                Point point2 = builder.createPoint(0, 0, 0);
                Point point3 = builder.createPoint(0, 1, 0);

                System.out.println(point1.toString());
                System.out.println(point2.toString());
                relateTest(point1, point2);

                System.out.println(point3.toString());
                relateTest(point1, point3);
        }

        public void _testPointCurve() {
                Point point = builder.createPoint(0, -1, 2);
                ArrayList<Curve> curves = getCurves(builder);

                System.out.println(point.toString());
                for (Curve curve : curves) {
                        System.out.println(curve.toString());
                        relateTest(point, curve);
                }
        }

        public void _testPointSurface() {
                Point point = builder.createPoint(0, -1, 2);
                ArrayList<Surface> surfaces = getSurfaces(builder);

                System.out.println(point.toString());
                for (Surface surface : surfaces) {
                        System.out.println(surface.toString());
                        relateTest(point, surface);
                }
        }

        public void _testPointSolid() {
                Point point = builder.createPoint(0, -1, 0);
                ArrayList<Solid> solids = getSolids(builder);

                System.out.println(point.toString());
                for (Solid solid : solids) {
                        System.out.println(solid.toString());
                        relateTest(point, solid);
                }
        }

        public void _testCurveCurve() {
                ArrayList<Curve> curves = getCurves(builder);
                Curve curve = curves.get(0);

                System.out.println(curve.toString());
                for (int i = 0; i < curves.size(); i++) {
                        System.out.println("------- Test -------");
                        System.out.println(curves.get(i).toString());
                        relateTest(curve, curves.get(i));
                }
        }

        public void _testCurveSurface() {
                Surface surface = getSurfaces(builder).get(0);
                ArrayList<Curve> curves = getCurves(builder);

                System.out.println(surface.toString());
                for (Curve curve : curves) {
                        System.out.println("------- Test -------");
                        System.out.println(curve.toString());
                        relateTest(curve, surface);
                }
        }

        public void _testCurveSolid() {
                String wkt = "Solid((((0.0 0.0 0.0, 2.0 0.0 0.0, 2.0 -2.0 0.0, 0.0 -2.0 0.0, 0.0 0.0 0.0)), ((2.0 -2.0 0.0, 2.0 0.0 0.0, 2.0 0.0 2.0, 2.0 -2.0 2.0, 2.0 -2.0 0.0)), ((0.0 0.0 2.0, 0.0 -2.0 2.0, 2.0 -2.0 2.0, 2.0 0.0 2.0, 0.0 0.0 2.0)), ((0.0 -2.0 2.0, 0.0 0.0 2.0, 0.0 0.0 0.0, 0.0 -2.0 0.0, 0.0 -2.0 2.0)), ((0.0 -2.0 0.0, 2.0 -2.0 0.0, 2.0 -2.0 2.0, 0.0 -2.0 2.0, 0.0 -2.0 0.0)), ((0.0 0.0 0.0, 0.0 0.0 2.0, 2.0 0.0 2.0, 2.0 0.0 0.0, 0.0 0.0 0.0))))";
                // Solid solid = getSolids(builder).get(0);
                WKTReader reader = new WKTReader(hints);
                Solid solid = null;
                try {
                        solid = (Solid) reader.read(wkt);
                } catch (ParseException e) {
                        e.printStackTrace();
                }

                SFSolid sfsolid = (SFSolid) SFCGALConvertor.geometryToSFCGALGeometry(solid);

                System.out.println("sfsolid : " + sfsolid.asText(1));
                ArrayList<Curve> curves = getCurves(builder);

                // solid = rotate(solid, 30, 0, 0, 0, 0, 0);
                System.out.println(solid.toString());
                int i = 1;
                for (Curve curve : curves) {
                        // curve = rotate(curve, 30, 0, 0, 0, 0, 0);
                        System.out.println("------- Test " + i++ + "-------");
                        System.out.println(curve.toString());
                        relateTest(solid, curve);
                }
        }

        public void _testSurfaceSurface() {
                ArrayList<Surface> surfaces = getSurfaces(builder);
                Surface surface = surfaces.get(0);

                System.out.println(surface.toString());
                for (int i = 0; i < surfaces.size(); i++) {
                        System.out.println("------- Test " + (i + 1) + "-------");
                        System.out.println(surfaces.get(i).toString());
                        relateTest(surface, surfaces.get(i));
                }
        }

        public void _testSurfaceSolid() {
                Solid solid = getSolids(builder).get(0);
                ArrayList<Surface> surfaces = getSurfaces(builder);

                // solid = rotate(solid, 90, 0, 0, 0, 0, 0);
                System.out.println(solid.toString());
                int i = 1;
                for (Surface surface : surfaces) {
                        // surface= rotate(surface, 90, 0, 0, 0, 0, 0);
                        System.out.println("------- Test " + i++ + "-------");
                        System.out.println(surface.toString());
                        relateTest(solid, surface);
                }

        }

        public void _testSolidSolid() {
                ArrayList<Solid> solids = getSolids(builder);
                Solid solid = solids.get(0);

                System.out.println(solid.toString());
                for (int i = 0; i < solids.size(); i++) {
                        System.out.println("------- Test " + i + " -------");
                        System.out.println("Solid " + i + " : " + solids.get(i).toString());
                        relateTest(solid, solids.get(i));
                }

        }

        public void relateTest(Geometry gA, Geometry gB) {
                System.out.println("equals : " + gA.equals(gB));
                System.out.println("intersects : " + gA.intersects(gB));
                System.out.println("disjoint : " + ((GeometryImpl) gA).disjoint(gB));
                System.out.println("touches : " + ((GeometryImpl) gA).touches(gB));
                System.out.println("contains : " + gA.contains(gB));
                System.out.println("within : " + ((GeometryImpl) gA).within(gB));
                System.out.println("crosses : " + ((GeometryImpl) gA).crosses(gB));
                System.out.println("overlaps : " + ((GeometryImpl) gA).overlaps(gB));

                /*
                 * System.out.println("union : " + gA.union(gB).toString()); System.out.println("difference : " + gA.difference(gB).toString());
                 * System.out.println("intersection : " + gA.intersection(gB).toString()); System.out.println("symmetric difference : " +
                 * gA.symmetricDifference(gB).toString());
                 */
        }

        public static Curve makeCurve(ISOGeometryBuilder builder, DirectPosition position1,
                        DirectPosition position2) {
                PrimitiveFactoryImpl pmF = (PrimitiveFactoryImpl) builder.getPrimitiveFactory();

                List<DirectPosition> positions1 = new ArrayList<DirectPosition>();
                positions1.add(position1);
                positions1.add(position2);
                Curve curve = pmF.createCurveByDirectPositions(positions1);

                return curve;
        }

        public static Curve makeCurve(ISOGeometryBuilder builder, List<DirectPosition> positions) {
                PrimitiveFactoryImpl pmF = (PrimitiveFactoryImpl) builder.getPrimitiveFactory();

                Curve curve = pmF.createCurveByDirectPositions(positions);

                return curve;
        }

        public static ArrayList<Curve> getCurves(ISOGeometryBuilder builder) {
                ArrayList<Curve> curves = new ArrayList<Curve>();
                PrimitiveFactoryImpl pmF = (PrimitiveFactoryImpl) builder.getPrimitiveFactory();

                // ///// 1 Curve(1 curve segment) with 1 Solid cases ////////
                // 1. P1
                DirectPosition position1 = builder.createDirectPosition(new double[] { -1, -1, 2 });
                DirectPosition position2 = builder.createDirectPosition(new double[] { 1, 1, 2 });

                DirectPosition position3 = builder.createDirectPosition(new double[] { 0, 0, 2 });
                DirectPosition position4 = builder.createDirectPosition(new double[] { -1, 1, 2 });

                DirectPosition position5 = builder.createDirectPosition(new double[] { -1, 1, 3 });
                DirectPosition position6 = builder.createDirectPosition(new double[] { 1, -1, 1 });

                DirectPosition position7 = builder.createDirectPosition(new double[] { 0, 0, 2 });
                DirectPosition position8 = builder.createDirectPosition(new double[] { 1, -1, 1 });

                // 2. P2
                DirectPosition position9 = builder.createDirectPosition(new double[] { -1, -3, 3 });
                DirectPosition position10 = builder.createDirectPosition(new double[] { 3, 1, -1 });

                DirectPosition position11 = builder.createDirectPosition(new double[] { 0, -2, 2 });
                DirectPosition position12 = builder.createDirectPosition(new double[] { 3, 1, -1 });

                DirectPosition position13 = builder.createDirectPosition(new double[] { 0, -2, 2 });
                DirectPosition position14 = builder.createDirectPosition(new double[] { 2, 0, 0 });

                // 3. L1
                DirectPosition position15 = builder
                                .createDirectPosition(new double[] { -1, -1, 2 });
                DirectPosition position16 = builder.createDirectPosition(new double[] { 0, -1, 2 });

                DirectPosition position17 = builder
                                .createDirectPosition(new double[] { 0.5, 0, 2 });
                DirectPosition position18 = builder
                                .createDirectPosition(new double[] { 1.5, 0, 2 });

                DirectPosition position19 = builder
                                .createDirectPosition(new double[] { -1, -1, 3 });
                DirectPosition position20 = builder.createDirectPosition(new double[] { 1, -1, 1 });

                DirectPosition position21 = builder.createDirectPosition(new double[] { 0, -1, 2 });
                DirectPosition position22 = builder.createDirectPosition(new double[] { 1, -1, 1 });

                // 4. L2
                DirectPosition position23 = builder
                                .createDirectPosition(new double[] { -1, -1, 3 });
                DirectPosition position24 = builder
                                .createDirectPosition(new double[] { 3, -1, -1 });

                DirectPosition position25 = builder.createDirectPosition(new double[] { 0, -1, 2 });
                DirectPosition position26 = builder
                                .createDirectPosition(new double[] { 3, -1, -1 });

                DirectPosition position27 = builder.createDirectPosition(new double[] { 0, -1, 2 });
                DirectPosition position28 = builder.createDirectPosition(new double[] { 2, -1, 0 });

                // 5. A1
                DirectPosition position29 = builder.createDirectPosition(new double[] { 1, -1, 3 });
                DirectPosition position30 = builder.createDirectPosition(new double[] { 1, -1, 2 });

                DirectPosition position31 = builder.createDirectPosition(new double[] { 1, -1, 3 });
                DirectPosition position32 = builder.createDirectPosition(new double[] { 1, -1, 1 });

                DirectPosition position33 = builder.createDirectPosition(new double[] { 1, -1, 2 });
                DirectPosition position34 = builder.createDirectPosition(new double[] { 1, -1, 1 });

                // 6. A2
                DirectPosition position35 = builder
                                .createDirectPosition(new double[] { 1, -1, -1 });
                DirectPosition position36 = builder.createDirectPosition(new double[] { 1, -1, 3 });

                DirectPosition position37 = builder.createDirectPosition(new double[] { 1, -1, 2 });
                DirectPosition position38 = builder
                                .createDirectPosition(new double[] { 1, -1, -1 });

                DirectPosition position39 = builder.createDirectPosition(new double[] { 1, -1, 2 });
                DirectPosition position40 = builder.createDirectPosition(new double[] { 1, -1, 0 });

                // 7. P1L1
                DirectPosition position41 = builder.createDirectPosition(new double[] { -1, 0, 2 });
                DirectPosition position42 = builder.createDirectPosition(new double[] { 1, 0, 2 });

                DirectPosition position43 = builder.createDirectPosition(new double[] { 0, 0, 2 });
                DirectPosition position44 = builder.createDirectPosition(new double[] { 1, 0, 2 });

                DirectPosition position45 = builder.createDirectPosition(new double[] { -2, 0, 4 });
                DirectPosition position46 = builder
                                .createDirectPosition(new double[] { 3, -2.5, -1 });

                DirectPosition position47 = builder.createDirectPosition(new double[] { 0, -1, 2 });
                DirectPosition position48 = builder
                                .createDirectPosition(new double[] { 3, -2.5, -1 });

                DirectPosition position49 = builder.createDirectPosition(new double[] { 0, -1, 2 });
                DirectPosition position50 = builder.createDirectPosition(new double[] { 2, -2, 0 });

                // 8. P2L1
                DirectPosition position51 = builder.createDirectPosition(new double[] { -1, 0, 2 });
                DirectPosition position52 = builder.createDirectPosition(new double[] { 3, 0, 2 });

                DirectPosition position53 = builder.createDirectPosition(new double[] { 0, 0, 2 });
                DirectPosition position54 = builder.createDirectPosition(new double[] { 3, 0, 2 });

                DirectPosition position55 = builder.createDirectPosition(new double[] { 0, 0, 2 });
                DirectPosition position56 = builder.createDirectPosition(new double[] { 2, 0, 2 });

                // 9. P1A2
                DirectPosition position57 = builder.createDirectPosition(new double[] { -1, 1, 2 });
                DirectPosition position58 = builder.createDirectPosition(new double[] { 1, -1, 2 });

                DirectPosition position59 = builder.createDirectPosition(new double[] { 0, 0, 2 });
                DirectPosition position60 = builder.createDirectPosition(new double[] { 1, -1, 2 });

                DirectPosition position61 = builder
                                .createDirectPosition(new double[] { 2.5, 0.5, 3 });
                DirectPosition position62 = builder.createDirectPosition(new double[] { 0.5, -1.5,
                                -1 });

                DirectPosition position63 = builder
                                .createDirectPosition(new double[] { 2.5, 0.5, 3 });
                DirectPosition position64 = builder.createDirectPosition(new double[] { 1, -1, 0 });

                DirectPosition position65 = builder.createDirectPosition(new double[] { 2, 0, 2 });
                DirectPosition position66 = builder.createDirectPosition(new double[] { 1, -1, 0 });

                // 10. P2A1
                DirectPosition position67 = builder.createDirectPosition(new double[] { -1, 1, 2 });
                DirectPosition position68 = builder.createDirectPosition(new double[] { 3, -3, 2 });

                DirectPosition position69 = builder.createDirectPosition(new double[] { 0, 0, 2 });
                DirectPosition position70 = builder.createDirectPosition(new double[] { 3, -3, 2 });

                DirectPosition position71 = builder.createDirectPosition(new double[] { 0, 0, 2 });
                DirectPosition position72 = builder.createDirectPosition(new double[] { 2, -2, 2 });

                // 11. L1A1
                DirectPosition position73 = builder
                                .createDirectPosition(new double[] { -1, -1, 2 });
                DirectPosition position74 = builder.createDirectPosition(new double[] { 1, -1, 2 });

                DirectPosition position75 = builder.createDirectPosition(new double[] { 0, -1, 2 });
                DirectPosition position76 = builder.createDirectPosition(new double[] { 1, -1, 2 });

                DirectPosition position77 = builder
                                .createDirectPosition(new double[] { -1, -1, 3 });
                DirectPosition position78 = builder
                                .createDirectPosition(new double[] { 3, -1, -1 });

                DirectPosition position79 = builder.createDirectPosition(new double[] { 0, -1, 2 });
                DirectPosition position80 = builder
                                .createDirectPosition(new double[] { 3, -1, -1 });

                DirectPosition position81 = builder.createDirectPosition(new double[] { 0, -1, 2 });
                DirectPosition position82 = builder.createDirectPosition(new double[] { 1, -1, 0 });

                // 12. L2A1
                DirectPosition position83 = builder
                                .createDirectPosition(new double[] { -1, -1, 2 });
                DirectPosition position84 = builder.createDirectPosition(new double[] { 3, -1, 2 });

                DirectPosition position85 = builder.createDirectPosition(new double[] { 0, -1, 2 });
                DirectPosition position86 = builder.createDirectPosition(new double[] { 3, -1, 2 });

                DirectPosition position87 = builder.createDirectPosition(new double[] { 0, -1, 2 });
                DirectPosition position88 = builder.createDirectPosition(new double[] { 2, -1, 2 });

                // 13. P1L1A1
                DirectPosition position89 = builder
                                .createDirectPosition(new double[] { -0.5, -3, 2 });
                DirectPosition position90 = builder
                                .createDirectPosition(new double[] { 1.5, 1, 2 });

                DirectPosition position91 = builder.createDirectPosition(new double[] { 0, -2, 2 });
                DirectPosition position92 = builder
                                .createDirectPosition(new double[] { 1.5, 1, 2 });

                DirectPosition position93 = builder.createDirectPosition(new double[] { 0, -2, 2 });
                DirectPosition position94 = builder.createDirectPosition(new double[] { 1, 0, 2 });

                curves.add(makeCurve(builder, position1, position2));
                curves.add(makeCurve(builder, position3, position4));
                curves.add(makeCurve(builder, position5, position6));
                curves.add(makeCurve(builder, position7, position8));
                curves.add(makeCurve(builder, position9, position10));
                curves.add(makeCurve(builder, position11, position12));
                curves.add(makeCurve(builder, position13, position14));
                curves.add(makeCurve(builder, position15, position16));
                curves.add(makeCurve(builder, position17, position18));
                curves.add(makeCurve(builder, position19, position20));
                curves.add(makeCurve(builder, position21, position22));
                curves.add(makeCurve(builder, position23, position24));
                curves.add(makeCurve(builder, position25, position26));
                curves.add(makeCurve(builder, position27, position28));
                curves.add(makeCurve(builder, position29, position30));
                curves.add(makeCurve(builder, position31, position32));
                curves.add(makeCurve(builder, position33, position34));
                curves.add(makeCurve(builder, position35, position36));
                curves.add(makeCurve(builder, position37, position38));
                curves.add(makeCurve(builder, position39, position40));
                curves.add(makeCurve(builder, position41, position42));
                curves.add(makeCurve(builder, position43, position44));
                curves.add(makeCurve(builder, position45, position46));
                curves.add(makeCurve(builder, position47, position48));
                curves.add(makeCurve(builder, position49, position50));
                curves.add(makeCurve(builder, position51, position52));
                curves.add(makeCurve(builder, position53, position54));
                curves.add(makeCurve(builder, position55, position56));
                curves.add(makeCurve(builder, position57, position58));
                curves.add(makeCurve(builder, position59, position60));
                curves.add(makeCurve(builder, position61, position62));
                curves.add(makeCurve(builder, position63, position64));
                curves.add(makeCurve(builder, position65, position66));
                curves.add(makeCurve(builder, position67, position68));
                curves.add(makeCurve(builder, position69, position70));
                curves.add(makeCurve(builder, position71, position72));
                curves.add(makeCurve(builder, position73, position74));
                curves.add(makeCurve(builder, position75, position76));
                curves.add(makeCurve(builder, position77, position78));
                curves.add(makeCurve(builder, position79, position80));
                curves.add(makeCurve(builder, position81, position82));
                curves.add(makeCurve(builder, position83, position84));
                curves.add(makeCurve(builder, position85, position86));
                curves.add(makeCurve(builder, position87, position88));
                curves.add(makeCurve(builder, position89, position90));
                curves.add(makeCurve(builder, position91, position92));
                curves.add(makeCurve(builder, position93, position94));

                return curves;
        }

        public static Surface makeSurface(ISOGeometryBuilder builder, List<DirectPosition> positions) {
                PrimitiveFactoryImpl pmF = (PrimitiveFactoryImpl) builder.getPrimitiveFactory();
                SurfaceImpl surf = pmF.createSurfaceByDirectPositions(positions);

                return surf;
        }

        public static ArrayList<Surface> getSurfaces(ISOGeometryBuilder builder) {
                // 1. P1
                ArrayList<DirectPosition> positions1 = new ArrayList<DirectPosition>();
                positions1.add(builder.createDirectPosition(new double[] { 1, 0, 3 }));
                positions1.add(builder.createDirectPosition(new double[] { 2, -1, 3 }));
                positions1.add(builder.createDirectPosition(new double[] { 3, 0, 1 }));
                positions1.add(builder.createDirectPosition(new double[] { 2, 1, 1 }));
                positions1.add(positions1.get(0));

                ArrayList<DirectPosition> positions2 = new ArrayList<DirectPosition>();
                positions2.add(builder.createDirectPosition(new double[] { 2, 0, 2 }));
                positions2.add(builder.createDirectPosition(new double[] { 3, 0, 2 }));
                positions2.add(builder.createDirectPosition(new double[] { 3, 0, 3 }));
                positions2.add(builder.createDirectPosition(new double[] { 2, 0, 3 }));
                positions2.add(positions2.get(0));

                ArrayList<DirectPosition> positions3 = new ArrayList<DirectPosition>();
                positions3.add(builder.createDirectPosition(new double[] { 1, 0, 3 }));
                positions3.add(builder.createDirectPosition(new double[] { 3, 0, 1 }));
                positions3.add(builder.createDirectPosition(new double[] { 5, 0, 3 }));
                positions3.add(builder.createDirectPosition(new double[] { 3, 0, 5 }));
                positions3.add(positions3.get(0));

                ArrayList<DirectPosition> positions4 = new ArrayList<DirectPosition>();
                positions4.add(builder.createDirectPosition(new double[] { 0, 0, 2 }));
                positions4.add(builder.createDirectPosition(new double[] { 0.5, -0.5, 1 }));
                positions4.add(builder.createDirectPosition(new double[] { 1, -1, 1 }));
                positions4.add(builder.createDirectPosition(new double[] { 1, -1, 1.5 }));
                positions4.add(positions4.get(0));

                // 2. P2
                ArrayList<DirectPosition> positions5 = new ArrayList<DirectPosition>();
                positions5.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions5.add(builder.createDirectPosition(new double[] { 1, -2, 2.5 }));
                positions5.add(builder.createDirectPosition(new double[] { 2, -2, 2 }));
                positions5.add(builder.createDirectPosition(new double[] { 1, -2, 3.5 }));
                positions5.add(positions5.get(0));

                ArrayList<DirectPosition> positions6 = new ArrayList<DirectPosition>(); //
                positions6.add(builder.createDirectPosition(new double[] { 0, -2, 0 }));
                positions6.add(builder.createDirectPosition(new double[] { 1.5, -1.5, 0.5 }));
                positions6.add(builder.createDirectPosition(new double[] { 2, 0, 2 }));
                positions6.add(positions6.get(0));

                // 3. L1
                ArrayList<DirectPosition> positions7 = new ArrayList<DirectPosition>(); //
                positions7.add(builder.createDirectPosition(new double[] { 0.5, 0, 2 }));
                positions7.add(builder.createDirectPosition(new double[] { 1.5, 0, 2 }));
                positions7.add(builder.createDirectPosition(new double[] { 1.5, 1, 3 }));
                positions7.add(builder.createDirectPosition(new double[] { 0.5, 1, 3 }));
                positions7.add(positions7.get(0));

                ArrayList<DirectPosition> positions8 = new ArrayList<DirectPosition>(); //
                positions8.add(builder.createDirectPosition(new double[] { 0.5, 1, 3 }));
                positions8.add(builder.createDirectPosition(new double[] { 0.5, -1, 1 }));
                positions8.add(builder.createDirectPosition(new double[] { 1.5, -1, 1 }));
                positions8.add(builder.createDirectPosition(new double[] { 1.5, 1, 3 }));
                positions8.add(positions8.get(0));

                ArrayList<DirectPosition> positions9 = new ArrayList<DirectPosition>(); //
                positions9.add(builder.createDirectPosition(new double[] { 0.5, 0, 2 }));
                positions9.add(builder.createDirectPosition(new double[] { 0.5, -1, 1 }));
                positions9.add(builder.createDirectPosition(new double[] { 1.5, -1, 1 }));
                positions9.add(builder.createDirectPosition(new double[] { 1.5, 0, 2 }));
                positions9.add(positions9.get(0));

                // 4. L2
                ArrayList<DirectPosition> positions10 = new ArrayList<DirectPosition>(); //
                positions10.add(builder.createDirectPosition(new double[] { 0.5, 1, 3 }));
                positions10.add(builder.createDirectPosition(new double[] { 0.5, -2, 0 }));
                positions10.add(builder.createDirectPosition(new double[] { 1.5, -2, 0 }));
                positions10.add(builder.createDirectPosition(new double[] { 1.5, 1, 3 }));
                positions10.add(positions10.get(0));

                ArrayList<DirectPosition> positions11 = new ArrayList<DirectPosition>(); //
                positions11.add(builder.createDirectPosition(new double[] { 0.5, 0, 2 }));
                positions11.add(builder.createDirectPosition(new double[] { 0.5, -3, -1 }));
                positions11.add(builder.createDirectPosition(new double[] { 1.5, -3, -1 }));
                positions11.add(builder.createDirectPosition(new double[] { 1.5, 0, 2 }));
                positions11.add(positions11.get(0));

                ArrayList<DirectPosition> positions12 = new ArrayList<DirectPosition>(); //
                positions12.add(builder.createDirectPosition(new double[] { 0.5, 0, 2 }));
                positions12.add(builder.createDirectPosition(new double[] { 0.5, -2, 0 }));
                positions12.add(builder.createDirectPosition(new double[] { 1.5, -2, 0 }));
                positions12.add(builder.createDirectPosition(new double[] { 1.5, 0, 2 }));
                positions12.add(positions12.get(0));

                // 5. A1
                ArrayList<DirectPosition> positions13 = new ArrayList<DirectPosition>(); //
                positions13.add(builder.createDirectPosition(new double[] { 0.5, -1, 2 }));
                positions13.add(builder.createDirectPosition(new double[] { 1.5, -1, 2 }));
                positions13.add(builder.createDirectPosition(new double[] { 1.5, -1, 3 }));
                positions13.add(builder.createDirectPosition(new double[] { 0.5, -1, 3 }));
                positions13.add(positions13.get(0));

                ArrayList<DirectPosition> positions14 = new ArrayList<DirectPosition>(); //
                positions14.add(builder.createDirectPosition(new double[] { 0.5, -1, 3 }));
                positions14.add(builder.createDirectPosition(new double[] { 0.5, -1, 1 }));
                positions14.add(builder.createDirectPosition(new double[] { 1.5, -1, 1 }));
                positions14.add(builder.createDirectPosition(new double[] { 1.5, -1, 3 }));
                positions14.add(positions14.get(0));

                ArrayList<DirectPosition> positions15 = new ArrayList<DirectPosition>(); //
                positions15.add(builder.createDirectPosition(new double[] { 0.5, -1, 2 }));
                positions15.add(builder.createDirectPosition(new double[] { 0.5, -1, 1 }));
                positions15.add(builder.createDirectPosition(new double[] { 1.5, -1, 1 }));
                positions15.add(builder.createDirectPosition(new double[] { 1.5, -1, 2 }));
                positions15.add(positions15.get(0));

                // 6. A2
                ArrayList<DirectPosition> positions16 = new ArrayList<DirectPosition>(); //
                positions16.add(builder.createDirectPosition(new double[] { 0.5, -1, 3 }));
                positions16.add(builder.createDirectPosition(new double[] { 0.5, -1, -1 }));
                positions16.add(builder.createDirectPosition(new double[] { 1.5, -1, -1 }));
                positions16.add(builder.createDirectPosition(new double[] { 1.5, -1, 3 }));
                positions16.add(positions16.get(0));

                ArrayList<DirectPosition> positions17 = new ArrayList<DirectPosition>(); //
                positions17.add(builder.createDirectPosition(new double[] { 0.5, -1, 2 }));
                positions17.add(builder.createDirectPosition(new double[] { 0.5, -1, -1 }));
                positions17.add(builder.createDirectPosition(new double[] { 1.5, -1, -1 }));
                positions17.add(builder.createDirectPosition(new double[] { 1.5, -1, 2 }));
                positions17.add(positions17.get(0));

                ArrayList<DirectPosition> positions18 = new ArrayList<DirectPosition>(); //
                positions18.add(builder.createDirectPosition(new double[] { 0.5, -1, 2 }));
                positions18.add(builder.createDirectPosition(new double[] { 0.5, -1, 0 }));
                positions18.add(builder.createDirectPosition(new double[] { 1.5, -1, 0 }));
                positions18.add(builder.createDirectPosition(new double[] { 1.5, -1, 2 }));
                positions18.add(positions18.get(0));

                // 7. A3
                ArrayList<DirectPosition> positions19 = new ArrayList<DirectPosition>(); //
                positions19.add(builder.createDirectPosition(new double[] { 1, -1, 2 }));
                positions19.add(builder.createDirectPosition(new double[] { 1, -2, 1 }));
                positions19.add(builder.createDirectPosition(new double[] { 2, -1, 1 }));
                positions19.add(positions19.get(0));

                ArrayList<DirectPosition> positions20 = new ArrayList<DirectPosition>(); //
                positions20.add(builder.createDirectPosition(new double[] { 1, -1, 2 }));
                positions20.add(builder.createDirectPosition(new double[] { 0, -1, 1 }));
                positions20.add(builder.createDirectPosition(new double[] { 2, -1, 1 }));
                positions20.add(positions20.get(0));

                ArrayList<DirectPosition> positions21 = new ArrayList<DirectPosition>(); //
                positions21.add(builder.createDirectPosition(new double[] { 1, -1, 2.5 }));
                positions21.add(builder.createDirectPosition(new double[] { -0.5, -1, 1 }));
                positions21.add(builder.createDirectPosition(new double[] { 2.5, -1, 1 }));
                positions21.add(positions21.get(0));

                // 8. A4
                ArrayList<DirectPosition> positions22 = new ArrayList<DirectPosition>(); //
                positions22.add(builder.createDirectPosition(new double[] { 1, -1, 2 }));
                positions22.add(builder.createDirectPosition(new double[] { 0, -1, 1 }));
                positions22.add(builder.createDirectPosition(new double[] { 1, -1, 0 }));
                positions22.add(builder.createDirectPosition(new double[] { 2, -1, 1 }));
                positions22.add(positions22.get(0));

                // 9. P1A1
                ArrayList<DirectPosition> positions23 = new ArrayList<DirectPosition>(); //
                positions23.add(builder.createDirectPosition(new double[] { 0, 0, 2 }));
                positions23.add(builder.createDirectPosition(new double[] { 1, -1, 2 }));
                positions23.add(builder.createDirectPosition(new double[] { 0.5, -0.5, 3 }));
                positions23.add(builder.createDirectPosition(new double[] { -0.5, 0.5, 3 }));
                positions23.add(positions23.get(0));

                ArrayList<DirectPosition> positions24 = new ArrayList<DirectPosition>(); //
                positions24.add(builder.createDirectPosition(new double[] { -0.5, 0.5, 3 }));
                positions24.add(builder.createDirectPosition(new double[] { 0.5, -0.5, 1 }));
                positions24.add(builder.createDirectPosition(new double[] { 1.5, -1.5, 1 }));
                positions24.add(builder.createDirectPosition(new double[] { 0.5, -0.5, 3 }));
                positions24.add(positions24.get(0));

                ArrayList<DirectPosition> positions25 = new ArrayList<DirectPosition>(); //
                positions25.add(builder.createDirectPosition(new double[] { 0, 0, 2 }));
                positions25.add(builder.createDirectPosition(new double[] { 0.5, -0.5, 1 }));
                positions25.add(builder.createDirectPosition(new double[] { 1.5, -1.5, 1 }));
                positions25.add(builder.createDirectPosition(new double[] { 1, -1, 2 }));
                positions25.add(positions25.get(0));

                // 10. P2A1
                ArrayList<DirectPosition> positions26 = new ArrayList<DirectPosition>(); //
                positions26.add(builder.createDirectPosition(new double[] { 0, 0, 2 }));
                positions26.add(builder.createDirectPosition(new double[] { 2, -2, 2 }));
                positions26.add(builder.createDirectPosition(new double[] { 2, -2, 3 }));
                positions26.add(builder.createDirectPosition(new double[] { 0, 0, 3 }));
                positions26.add(positions26.get(0));

                ArrayList<DirectPosition> positions27 = new ArrayList<DirectPosition>(); //
                positions27.add(builder.createDirectPosition(new double[] { 0, 0, 2 }));
                positions27.add(builder.createDirectPosition(new double[] { 1, -1, 1 }));
                positions27.add(builder.createDirectPosition(new double[] { 2, 0, 2 }));
                positions27.add(positions27.get(0));

                // 11. L1A1
                ArrayList<DirectPosition> positions28 = new ArrayList<DirectPosition>(); //
                positions28.add(builder.createDirectPosition(new double[] { 0.5, 0, 2 }));
                positions28.add(builder.createDirectPosition(new double[] { 0.5, -1, 2 }));
                positions28.add(builder.createDirectPosition(new double[] { 1.5, -1, 2 }));
                positions28.add(builder.createDirectPosition(new double[] { 1.5, 0, 2 }));
                positions28.add(positions28.get(0));

                ArrayList<DirectPosition> positions29 = new ArrayList<DirectPosition>(); //
                positions29.add(builder.createDirectPosition(new double[] { 0.5, 0.5, 3 }));
                positions29.add(builder.createDirectPosition(new double[] { 0.5, -1.5, -1 }));
                positions29.add(builder.createDirectPosition(new double[] { 1.5, -1.5, -1 }));
                positions29.add(builder.createDirectPosition(new double[] { 1.5, 0.5, 3 }));
                positions29.add(positions29.get(0));

                ArrayList<DirectPosition> positions30 = new ArrayList<DirectPosition>(); //
                positions30.add(builder.createDirectPosition(new double[] { 0.5, 0, 2 }));
                positions30.add(builder.createDirectPosition(new double[] { 0.5, -1.5, -1 }));
                positions30.add(builder.createDirectPosition(new double[] { 1.5, -1.5, -1 }));
                positions30.add(builder.createDirectPosition(new double[] { 1.5, 0, 2 }));
                positions30.add(positions30.get(0));

                ArrayList<DirectPosition> positions31 = new ArrayList<DirectPosition>(); //
                positions31.add(builder.createDirectPosition(new double[] { 0.5, 0, 2 }));
                positions31.add(builder.createDirectPosition(new double[] { 0.5, -1, 0 }));
                positions31.add(builder.createDirectPosition(new double[] { 1.5, -1, 0 }));
                positions31.add(builder.createDirectPosition(new double[] { 1.5, 0, 2 }));
                positions31.add(positions31.get(0));

                // 12. L2A1
                ArrayList<DirectPosition> positions32 = new ArrayList<DirectPosition>(); //
                positions32.add(builder.createDirectPosition(new double[] { 0.5, 0, 2 }));
                positions32.add(builder.createDirectPosition(new double[] { 0.5, -2, 2 }));
                positions32.add(builder.createDirectPosition(new double[] { 1.5, -2, 2 }));
                positions32.add(builder.createDirectPosition(new double[] { 1.5, 0, 2 }));
                positions32.add(positions32.get(0));

                // 13. L3A1
                ArrayList<DirectPosition> positions33 = new ArrayList<DirectPosition>(); //
                positions33.add(builder.createDirectPosition(new double[] { 0, -1, 2 }));
                positions33.add(builder.createDirectPosition(new double[] { 1, -2, 2 }));
                positions33.add(builder.createDirectPosition(new double[] { 2, -1, 2 }));
                positions33.add(positions33.get(0));

                // 14.L4A1
                ArrayList<DirectPosition> positions34 = new ArrayList<DirectPosition>(); //
                positions34.add(builder.createDirectPosition(new double[] { 0, -1, 2 }));
                positions34.add(builder.createDirectPosition(new double[] { 1, -2, 2 }));
                positions34.add(builder.createDirectPosition(new double[] { 2, -1, 2 }));
                positions34.add(builder.createDirectPosition(new double[] { 1, 0, 2 }));
                positions34.add(positions34.get(0));

                // 15. P1A2

                ArrayList<DirectPosition> positions35 = new ArrayList<DirectPosition>(); //
                positions35.add(builder.createDirectPosition(new double[] { 0, 0, 2 }));
                positions35.add(builder.createDirectPosition(new double[] { 1, -2, 1 }));
                positions35.add(builder.createDirectPosition(new double[] { 2, -1, 1 }));
                positions35.add(positions35.get(0));

                // 16. P2A2
                ArrayList<DirectPosition> positions36 = new ArrayList<DirectPosition>(); //
                positions36.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions36.add(builder.createDirectPosition(new double[] { 1, -1, 0 }));
                positions36.add(builder.createDirectPosition(new double[] { 2, 0, 2 }));
                positions36.add(positions36.get(0));

                // 17. L1A2
                ArrayList<DirectPosition> positions37 = new ArrayList<DirectPosition>(); //
                positions37.add(builder.createDirectPosition(new double[] { 1, -1, 3 }));
                positions37.add(builder.createDirectPosition(new double[] { 1, -1, 1 }));
                positions37.add(builder.createDirectPosition(new double[] { 3, -1, 1 }));
                positions37.add(builder.createDirectPosition(new double[] { 3, -1, 3 }));
                positions37.add(positions37.get(0));

                // 18. L2A2
                ArrayList<DirectPosition> positions38 = new ArrayList<DirectPosition>(); //
                positions38.add(builder.createDirectPosition(new double[] { 1, -1, 2 }));
                positions38.add(builder.createDirectPosition(new double[] { 0, -1, 0 }));
                positions38.add(builder.createDirectPosition(new double[] { 1, -1, 0 }));
                positions38.add(builder.createDirectPosition(new double[] { 2, -1, 2 }));
                positions38.add(positions38.get(0));

                // 19. L3A2
                ArrayList<DirectPosition> positions39 = new ArrayList<DirectPosition>(); //
                positions39.add(builder.createDirectPosition(new double[] { 0, -1, 2 }));
                positions39.add(builder.createDirectPosition(new double[] { 2, -1, 0 }));
                positions39.add(builder.createDirectPosition(new double[] { 2, -1, 2 }));
                positions39.add(positions39.get(0));

                // 20. P2A3
                ArrayList<DirectPosition> positions40 = new ArrayList<DirectPosition>(); //
                positions40.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions40.add(builder.createDirectPosition(new double[] { 0.5, -2.5, 1.5 }));
                positions40.add(builder.createDirectPosition(new double[] { 2.5, -0.5, 1.5 }));
                positions40.add(builder.createDirectPosition(new double[] { 2, 0, 2 }));
                positions40.add(positions40.get(0));

                // 22. P3A3
                ArrayList<DirectPosition> positions41 = new ArrayList<DirectPosition>(); //
                positions41.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions41.add(builder.createDirectPosition(new double[] { 2, -2, 0 }));
                positions41.add(builder.createDirectPosition(new double[] { 2, 0, 2 }));
                positions41.add(positions41.get(0));

                // 23 L2A3
                ArrayList<DirectPosition> positions42 = new ArrayList<DirectPosition>(); //
                positions42.add(builder.createDirectPosition(new double[] { 0, -1, 3 }));
                positions42.add(builder.createDirectPosition(new double[] { 0, -1, 1 }));
                positions42.add(builder.createDirectPosition(new double[] { 3, -1, 1 }));
                positions42.add(builder.createDirectPosition(new double[] { 3, -1, 3 }));
                positions42.add(positions42.get(0));

                // 24. L3A3
                ArrayList<DirectPosition> positions43 = new ArrayList<DirectPosition>(); //
                positions43.add(builder.createDirectPosition(new double[] { 1, -2, 2 }));
                positions43.add(builder.createDirectPosition(new double[] { 2, -2, 1 }));
                positions43.add(builder.createDirectPosition(new double[] { 2, -1, 2 }));
                positions43.add(positions43.get(0));

                // 25. L4A4
                ArrayList<DirectPosition> positions44 = new ArrayList<DirectPosition>(); //
                positions44.add(builder.createDirectPosition(new double[] { 0, -1, 2 }));
                positions44.add(builder.createDirectPosition(new double[] { 0, -1, 0 }));
                positions44.add(builder.createDirectPosition(new double[] { 2, -1, 0 }));
                positions44.add(builder.createDirectPosition(new double[] { 2, -1, 2 }));
                positions44.add(positions44.get(0));

                ArrayList<DirectPosition> positions45 = new ArrayList<DirectPosition>(); //
                positions45.add(builder.createDirectPosition(new double[] { 1, -1, 2 }));
                positions45.add(builder.createDirectPosition(new double[] { 1, -1, 0 }));
                positions45.add(builder.createDirectPosition(new double[] { 2, -1, 0 }));
                positions45.add(builder.createDirectPosition(new double[] { 2, -1, 2 }));
                positions45.add(positions45.get(0));

                // 26. P1L1
                ArrayList<DirectPosition> positions46 = new ArrayList<DirectPosition>(); //
                positions46.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions46.add(builder.createDirectPosition(new double[] { 0, -2, 1 }));
                positions46.add(builder.createDirectPosition(new double[] { 1, -1, 1.5 }));
                positions46.add(positions46.get(0));

                // 27. P2L1
                ArrayList<DirectPosition> positions47 = new ArrayList<DirectPosition>(); //
                positions47.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions47.add(builder.createDirectPosition(new double[] { 0, -2, 0 }));
                positions47.add(builder.createDirectPosition(new double[] { 1, -1, 1 }));
                positions47.add(positions47.get(0));

                // 28. P2L2
                ArrayList<DirectPosition> positions48 = new ArrayList<DirectPosition>(); //
                positions48.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions48.add(builder.createDirectPosition(new double[] { 0, -2, 0 }));
                positions48.add(builder.createDirectPosition(new double[] { 2, -1, 1 }));
                positions48.add(positions48.get(0));

                // 29. P1L1A1
                ArrayList<DirectPosition> positions49 = new ArrayList<DirectPosition>(); //
                positions49.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions49.add(builder.createDirectPosition(new double[] { 0, -2, 1 }));
                positions49.add(builder.createDirectPosition(new double[] { 1, -1, 2 }));
                positions49.add(positions49.get(0));

                ArrayList<DirectPosition> positions50 = new ArrayList<DirectPosition>(); //
                positions50.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions50.add(builder.createDirectPosition(new double[] { 1, -1, 2 }));
                positions50.add(builder.createDirectPosition(new double[] { 0, -1, 2 }));
                positions50.add(positions50.get(0));

                // 30. P1L2A1
                ArrayList<DirectPosition> positions51 = new ArrayList<DirectPosition>(); //
                positions51.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions51.add(builder.createDirectPosition(new double[] { 2, -1, 2 }));
                positions51.add(builder.createDirectPosition(new double[] { 0, -1, 2 }));
                positions51.add(positions51.get(0));

                // 31. P2L1A1
                ArrayList<DirectPosition> positions52 = new ArrayList<DirectPosition>(); //
                positions52.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions52.add(builder.createDirectPosition(new double[] { 2, -2, 2 }));
                positions52.add(builder.createDirectPosition(new double[] { 1, -1, 2 }));
                positions52.add(positions52.get(0));

                // 32. P2L2A1
                ArrayList<DirectPosition> positions53 = new ArrayList<DirectPosition>(); //
                positions53.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions53.add(builder.createDirectPosition(new double[] { 2, -2, 2 }));
                positions53.add(builder.createDirectPosition(new double[] { 0, -1, 2 }));
                positions53.add(positions53.get(0));

                // 33. P2L3A1
                ArrayList<DirectPosition> positions54 = new ArrayList<DirectPosition>(); //
                positions54.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions54.add(builder.createDirectPosition(new double[] { 2, -2, 2 }));
                positions54.add(builder.createDirectPosition(new double[] { 2, -1, 2 }));
                positions54.add(builder.createDirectPosition(new double[] { 0, -1, 2 }));
                positions54.add(positions54.get(0));

                // 34. P3L1A1
                ArrayList<DirectPosition> positions55 = new ArrayList<DirectPosition>(); //
                positions55.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions55.add(builder.createDirectPosition(new double[] { 0, -2, 0 }));
                positions55.add(builder.createDirectPosition(new double[] { 2, 0, 0 }));
                positions55.add(positions55.get(0));

                // 35. P3L2A1
                ArrayList<DirectPosition> positions56 = new ArrayList<DirectPosition>(); //
                positions56.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions56.add(builder.createDirectPosition(new double[] { 2, -2, 2 }));
                positions56.add(builder.createDirectPosition(new double[] { 0, 0, 2 }));
                positions56.add(positions56.get(0));

                // 36. P4L4A1
                ArrayList<DirectPosition> positions57 = new ArrayList<DirectPosition>(); //
                positions57.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions57.add(builder.createDirectPosition(new double[] { 2, -2, 2 }));
                positions57.add(builder.createDirectPosition(new double[] { 2, 0, 2 }));
                positions57.add(builder.createDirectPosition(new double[] { 0, 0, 2 }));
                positions57.add(positions57.get(0));

                // 37. P1L2A2
                ArrayList<DirectPosition> positions58 = new ArrayList<DirectPosition>(); //
                positions58.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions58.add(builder.createDirectPosition(new double[] { 2, -1, 0 }));
                positions58.add(builder.createDirectPosition(new double[] { 2, -1, 2 }));
                positions58.add(positions58.get(0));

                // 38. P2L1A2
                ArrayList<DirectPosition> positions59 = new ArrayList<DirectPosition>(); //
                positions59.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions59.add(builder.createDirectPosition(new double[] { 2, -1, 0 }));
                positions59.add(builder.createDirectPosition(new double[] { 2, 0, 2 }));
                positions59.add(positions59.get(0));

                // 39. P4L2A2
                ArrayList<DirectPosition> positions60 = new ArrayList<DirectPosition>(); //
                positions60.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions60.add(builder.createDirectPosition(new double[] { 0, -2, 0 }));
                positions60.add(builder.createDirectPosition(new double[] { 2, 0, 0 }));
                positions60.add(builder.createDirectPosition(new double[] { 2, 0, 2 }));
                positions60.add(positions60.get(0));

                // 40. P2L2A3
                ArrayList<DirectPosition> positions61 = new ArrayList<DirectPosition>(); //
                positions61.add(builder.createDirectPosition(new double[] { 0, -2, 2 }));
                positions61.add(builder.createDirectPosition(new double[] { 0, -2, 0 }));
                positions61.add(builder.createDirectPosition(new double[] { 2, -1, 0 }));
                positions61.add(builder.createDirectPosition(new double[] { 2, -1, 2 }));
                positions61.add(positions61.get(0));

                ArrayList<Surface> surfaces = new ArrayList<Surface>();
                
                surfaces.add(makeSurface(builder, positions1));
                surfaces.add(makeSurface(builder, positions2));
                surfaces.add(makeSurface(builder, positions3));
                surfaces.add(makeSurface(builder, positions4));
                surfaces.add(makeSurface(builder, positions5));
                surfaces.add(makeSurface(builder, positions6));
                surfaces.add(makeSurface(builder, positions7));
                surfaces.add(makeSurface(builder, positions8));
                surfaces.add(makeSurface(builder, positions9));
                surfaces.add(makeSurface(builder, positions10));
                surfaces.add(makeSurface(builder, positions11));
                surfaces.add(makeSurface(builder, positions12));
                surfaces.add(makeSurface(builder, positions13));
                surfaces.add(makeSurface(builder, positions14));
                surfaces.add(makeSurface(builder, positions15));
                surfaces.add(makeSurface(builder, positions16));
                surfaces.add(makeSurface(builder, positions17));
                surfaces.add(makeSurface(builder, positions18));                 
                surfaces.add(makeSurface(builder, positions19));
                surfaces.add(makeSurface(builder, positions20));
                surfaces.add(makeSurface(builder, positions21));
                surfaces.add(makeSurface(builder, positions22));
                surfaces.add(makeSurface(builder, positions23));
                surfaces.add(makeSurface(builder, positions24));
                surfaces.add(makeSurface(builder, positions25));
                surfaces.add(makeSurface(builder, positions26));
                surfaces.add(makeSurface(builder, positions27));
                surfaces.add(makeSurface(builder, positions28));
                surfaces.add(makeSurface(builder, positions29));
                surfaces.add(makeSurface(builder, positions30));
                surfaces.add(makeSurface(builder, positions31));
                surfaces.add(makeSurface(builder, positions32));
                surfaces.add(makeSurface(builder, positions33));
                surfaces.add(makeSurface(builder, positions34));
                surfaces.add(makeSurface(builder, positions35));
                surfaces.add(makeSurface(builder, positions36));
                surfaces.add(makeSurface(builder, positions37));
                surfaces.add(makeSurface(builder, positions38));
                surfaces.add(makeSurface(builder, positions39));
                surfaces.add(makeSurface(builder, positions40));
                surfaces.add(makeSurface(builder, positions41));
                surfaces.add(makeSurface(builder, positions42));
                surfaces.add(makeSurface(builder, positions43));
                surfaces.add(makeSurface(builder, positions44));
                surfaces.add(makeSurface(builder, positions45));
                surfaces.add(makeSurface(builder, positions46));
                surfaces.add(makeSurface(builder, positions47));
                surfaces.add(makeSurface(builder, positions48));
                surfaces.add(makeSurface(builder, positions49));
                surfaces.add(makeSurface(builder, positions50));
                surfaces.add(makeSurface(builder, positions51));
                surfaces.add(makeSurface(builder, positions52));
                surfaces.add(makeSurface(builder, positions53));
                surfaces.add(makeSurface(builder, positions54));
                surfaces.add(makeSurface(builder, positions55));
                surfaces.add(makeSurface(builder, positions56));
                surfaces.add(makeSurface(builder, positions57));
                surfaces.add(makeSurface(builder, positions58));
                surfaces.add(makeSurface(builder, positions59));
                surfaces.add(makeSurface(builder, positions60));
                surfaces.add(makeSurface(builder, positions61));

                return surfaces;
        }

        public static ArrayList<ArrayList<DirectPosition>> getSolidPoints(ISOGeometryBuilder builder) {
                ArrayList<ArrayList<DirectPosition>> solidPoints = new ArrayList<ArrayList<DirectPosition>>();

                DirectPosition p1 = builder.createDirectPosition(new double[] { 0, 0, 0 });
                DirectPosition p2 = builder.createDirectPosition(new double[] { 0, -2, 0 });
                DirectPosition p3 = builder.createDirectPosition(new double[] { 2, -2, 0 });
                DirectPosition p4 = builder.createDirectPosition(new double[] { 2, 0, 0 });
                DirectPosition p5 = builder.createDirectPosition(new double[] { 0, 0, 2 });
                DirectPosition p6 = builder.createDirectPosition(new double[] { 0, -2, 2 });
                DirectPosition p7 = builder.createDirectPosition(new double[] { 2, -2, 2 });
                DirectPosition p8 = builder.createDirectPosition(new double[] { 2, 0, 2 });

                ArrayList<DirectPosition> points1 = new ArrayList<DirectPosition>();
                points1.add(p1);
                points1.add(p2);
                points1.add(p3);
                points1.add(p4);
                points1.add(p5);
                points1.add(p6);
                points1.add(p7);
                points1.add(p8);

                //
                DirectPosition p11 = builder.createDirectPosition(new double[] { 2, 0, 0 });
                DirectPosition p12 = builder.createDirectPosition(new double[] { 2, -2, 0 });
                DirectPosition p13 = builder.createDirectPosition(new double[] { 4, -2, 0 });
                DirectPosition p14 = builder.createDirectPosition(new double[] { 4, 0, 0 });
                DirectPosition p15 = builder.createDirectPosition(new double[] { 2, 0, 2 });
                DirectPosition p16 = builder.createDirectPosition(new double[] { 2, -2, 2 });
                DirectPosition p17 = builder.createDirectPosition(new double[] { 4, -2, 2 });
                DirectPosition p18 = builder.createDirectPosition(new double[] { 4, 0, 2 });

                ArrayList<DirectPosition> points2 = new ArrayList<DirectPosition>();
                points2.add(p11);
                points2.add(p12);
                points2.add(p13);
                points2.add(p14);
                points2.add(p15);
                points2.add(p16);
                points2.add(p17);
                points2.add(p18);

                //
                DirectPosition p21 = builder.createDirectPosition(new double[] { 2, 0, 2 });
                DirectPosition p22 = builder.createDirectPosition(new double[] { 2, -2, 2 });
                DirectPosition p23 = builder.createDirectPosition(new double[] { 4, -2, 2 });
                DirectPosition p24 = builder.createDirectPosition(new double[] { 4, 0, 2 });
                DirectPosition p25 = builder.createDirectPosition(new double[] { 2, 0, 4 });
                DirectPosition p26 = builder.createDirectPosition(new double[] { 2, -2, 4 });
                DirectPosition p27 = builder.createDirectPosition(new double[] { 4, -2, 4 });
                DirectPosition p28 = builder.createDirectPosition(new double[] { 4, 0, 4 });

                ArrayList<DirectPosition> points3 = new ArrayList<DirectPosition>();
                points3.add(p21);
                points3.add(p22);
                points3.add(p23);
                points3.add(p24);
                points3.add(p25);
                points3.add(p26);
                points3.add(p27);
                points3.add(p28);

                DirectPosition p31 = builder.createDirectPosition(new double[] { 1, 0, 0 });
                DirectPosition p32 = builder.createDirectPosition(new double[] { 1, -2, 0 });
                DirectPosition p33 = builder.createDirectPosition(new double[] { 3, -2, 0 });
                DirectPosition p34 = builder.createDirectPosition(new double[] { 3, 0, 0 });
                DirectPosition p35 = builder.createDirectPosition(new double[] { 1, 0, 2 });
                DirectPosition p36 = builder.createDirectPosition(new double[] { 1, -2, 2 });
                DirectPosition p37 = builder.createDirectPosition(new double[] { 3, -2, 2 });
                DirectPosition p38 = builder.createDirectPosition(new double[] { 3, 0, 2 });

                ArrayList<DirectPosition> points4 = new ArrayList<DirectPosition>();
                points4.add(p31);
                points4.add(p32);
                points4.add(p33);
                points4.add(p34);
                points4.add(p35);
                points4.add(p36);
                points4.add(p37);
                points4.add(p38);

                DirectPosition p41 = builder.createDirectPosition(new double[] { -1, 0, -1 });
                DirectPosition p42 = builder.createDirectPosition(new double[] { -1, -2, -1 });
                DirectPosition p43 = builder.createDirectPosition(new double[] { 3, -2, -1 });
                DirectPosition p44 = builder.createDirectPosition(new double[] { 3, 0, -1 });
                DirectPosition p45 = builder.createDirectPosition(new double[] { -1, 0, 3 });
                DirectPosition p46 = builder.createDirectPosition(new double[] { -1, -2, 3 });
                DirectPosition p47 = builder.createDirectPosition(new double[] { 3, -2, 3 });
                DirectPosition p48 = builder.createDirectPosition(new double[] { 3, 0, 3 });

                ArrayList<DirectPosition> points5 = new ArrayList<DirectPosition>();
                points5.add(p41);
                points5.add(p42);
                points5.add(p43);
                points5.add(p44);
                points5.add(p45);
                points5.add(p46);
                points5.add(p47);
                points5.add(p48);

                DirectPosition p51 = builder.createDirectPosition(new double[] { 0.5, -0.5, 0.5 });
                DirectPosition p52 = builder.createDirectPosition(new double[] { 0.5, -1.5, 0.5 });
                DirectPosition p53 = builder.createDirectPosition(new double[] { 1.5, -1.5, 0.5 });
                DirectPosition p54 = builder.createDirectPosition(new double[] { 1.5, -0.5, 0.5 });
                DirectPosition p55 = builder.createDirectPosition(new double[] { 0.5, -0.5, 1.5 });
                DirectPosition p56 = builder.createDirectPosition(new double[] { 0.5, -1.5, 1.5 });
                DirectPosition p57 = builder.createDirectPosition(new double[] { 1.5, -1.5, 1.5 });
                DirectPosition p58 = builder.createDirectPosition(new double[] { 1.5, -0.5, 1.5 });

                ArrayList<DirectPosition> points6 = new ArrayList<DirectPosition>();
                points6.add(p51);
                points6.add(p52);
                points6.add(p53);
                points6.add(p54);
                points6.add(p55);
                points6.add(p56);
                points6.add(p57);
                points6.add(p58);

                DirectPosition p61 = builder.createDirectPosition(new double[] { 1, -1, 0 });
                DirectPosition p62 = builder.createDirectPosition(new double[] { 1, -2, 0 });
                DirectPosition p63 = builder.createDirectPosition(new double[] { 2, -2, 0 });
                DirectPosition p64 = builder.createDirectPosition(new double[] { 2, -1, 0 });
                DirectPosition p65 = builder.createDirectPosition(new double[] { 1, -1, 1 });
                DirectPosition p66 = builder.createDirectPosition(new double[] { 1, -2, 1 });
                DirectPosition p67 = builder.createDirectPosition(new double[] { 2, -2, 1 });
                DirectPosition p68 = builder.createDirectPosition(new double[] { 2, -1, 1 });

                ArrayList<DirectPosition> points7 = new ArrayList<DirectPosition>();
                points7.add(p61);
                points7.add(p62);
                points7.add(p63);
                points7.add(p64);
                points7.add(p65);
                points7.add(p66);
                points7.add(p67);
                points7.add(p68);

                DirectPosition p71 = builder.createDirectPosition(new double[] { 0.5, -0.5, -1 });
                DirectPosition p72 = builder.createDirectPosition(new double[] { 0.5, -1.5, -1 });
                DirectPosition p73 = builder.createDirectPosition(new double[] { 1.5, -1.5, -1 });
                DirectPosition p74 = builder.createDirectPosition(new double[] { 1.5, -0.5, -1 });
                DirectPosition p75 = builder.createDirectPosition(new double[] { 0.5, -0.5, 3 });
                DirectPosition p76 = builder.createDirectPosition(new double[] { 0.5, -1.5, 3 });
                DirectPosition p77 = builder.createDirectPosition(new double[] { 1.5, -1.5, 3 });
                DirectPosition p78 = builder.createDirectPosition(new double[] { 1.5, -0.5, 3 });

                ArrayList<DirectPosition> points8 = new ArrayList<DirectPosition>();
                points8.add(p71);
                points8.add(p72);
                points8.add(p73);
                points8.add(p74);
                points8.add(p75);
                points8.add(p76);
                points8.add(p77);
                points8.add(p78);
                
                DirectPosition p81 = builder.createDirectPosition(new double[] { -125745.58224841699, 3813.6302470150695, 0.0 });
                DirectPosition p82 = builder.createDirectPosition(new double[] { -125738.91237563781, 4813.464779595859, 0.0 });
                DirectPosition p83 = builder.createDirectPosition(new double[] { -126731.71448564173, 4815.075754128498, 0.0 });
                DirectPosition p84 = builder.createDirectPosition(new double[] { -126738.38435842091, 3815.241221547709, 0.0 });
                DirectPosition p85 = builder.createDirectPosition(new double[] { -125745.58224841699, 3813.6302470150695, 3000.0 });
                DirectPosition p86 = builder.createDirectPosition(new double[] { -125738.91237563781, 4813.464779595859, 3000.0 });
                DirectPosition p87 = builder.createDirectPosition(new double[] { -126731.71448564173, 4815.075754128498, 3000.0 });
                DirectPosition p88 = builder.createDirectPosition(new double[] { -126738.38435842091, 3815.241221547709, 3000.0 });

                ArrayList<DirectPosition> points9 = new ArrayList<DirectPosition>();
                points9.add(p81);
                points9.add(p82);
                points9.add(p83);
                points9.add(p84);
                points9.add(p85);
                points9.add(p86);
                points9.add(p87);
                points9.add(p88);

                solidPoints.add(points1);
                solidPoints.add(points2);
                solidPoints.add(points3);
                solidPoints.add(points4);
                solidPoints.add(points5);
                solidPoints.add(points6);
                solidPoints.add(points7);
                solidPoints.add(points8);
                solidPoints.add(points9);

                return solidPoints;
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

        public static ArrayList<Solid> getSolids(ISOGeometryBuilder builder) {
                ArrayList<Solid> solids = new ArrayList<Solid>();
                ArrayList<ArrayList<DirectPosition>> solidPoints = getSolidPoints(builder);

                for (int i = 0; i < 9; i++) {
                        solids.add(makeSolid(builder, solidPoints.get(i)));
                }

                return solids;
        }

        public static DirectPosition rotate(DirectPosition dp, double angle, double pivotX,
                        double pivotY, double offsetX, double offsetY, double offsetZ) {
                double[] coordinate = dp.getCoordinate();
                double x = Math.cos(angle * Math.PI / 180) * (coordinate[0] - pivotX)
                                - Math.sin(angle * Math.PI / 180) * (coordinate[1] - pivotY)
                                + pivotX + offsetX;
                double y = Math.sin(angle * Math.PI / 180) * (coordinate[0] - pivotX)
                                + Math.cos(angle * Math.PI / 180) * (coordinate[1] - pivotY)
                                + pivotY + offsetY;
                double z = coordinate[2] + offsetZ;

                x = Math.round(x * Math.pow(10, 15)) / Math.pow(10, 15);
                y = Math.round(y * Math.pow(10, 15)) / Math.pow(10, 15);

                DirectPosition newDP = builder.createDirectPosition(new double[] { x, y, z });

                return newDP;
        }

        public static ArrayList<DirectPosition> rotate(List<DirectPosition> positions,
                        double angle, double pivotX, double pivotY, double offsetX, double offsetY,
                        double offsetZ) {
                ArrayList<DirectPosition> newPositions = new ArrayList<DirectPosition>();

                for (DirectPosition dp : positions) {
                        newPositions.add(rotate(dp, angle, pivotX, pivotY, offsetX, offsetY,
                                        offsetZ));
                }

                return newPositions;
        }

        public static Curve rotate(Curve curve, double angle, double pivotX, double pivotY,
                        double offsetX, double offsetY, double offsetZ) {
                ArrayList<DirectPosition> newPositions = rotate(
                                ((CurveImpl) curve).asDirectPositions(), angle, pivotX, pivotY,
                                offsetX, offsetY, offsetZ);

                return makeCurve(builder, newPositions);
        }

        public static Surface rotate(Surface surface, double angle, double pivotX, double pivotY,
                        double offsetX, double offsetY, double offsetZ) {
                Ring exterior = surface.getBoundary().getExterior();

                ArrayList<DirectPosition> newPositions = rotate(
                                ((RingImplUnsafe) exterior).asDirectPositions(), angle, pivotX,
                                pivotY, offsetX, offsetY, offsetZ);

                return makeSurface(builder, newPositions);
        }

        public static Solid rotate(Solid solid, double angle, double pivotX, double pivotY,
                        double offsetX, double offsetY, double offsetZ) {
                Shell exterior = solid.getBoundary().getExterior();
                ArrayList<OrientableSurface> elements = (ArrayList<OrientableSurface>) exterior
                                .getElements();
                ArrayList<OrientableSurface> newElements = new ArrayList<OrientableSurface>();

                for (OrientableSurface surface : elements) {
                        newElements.add(rotate((Surface) surface, angle, pivotX, pivotY, offsetX,
                                        offsetY, offsetZ));
                }

                PrimitiveFactoryImpl pmFF = (PrimitiveFactoryImpl) builder.getPrimitiveFactory();
                Shell newExterior = pmFF.createShell(newElements);
                List<Shell> interiors = new ArrayList<Shell>();

                SolidBoundary solidBoundary = pmFF.createSolidBoundary(newExterior, interiors);
                Solid rotatedSolid = pmFF.createSolid(solidBoundary);

                return rotatedSolid;
        }
}
