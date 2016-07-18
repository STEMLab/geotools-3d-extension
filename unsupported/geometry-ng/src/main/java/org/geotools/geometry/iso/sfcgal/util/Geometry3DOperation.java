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

import org.geotools.geometry.iso.root.GeometryImpl;
import org.geotools.geometry.iso.sfcgal.relate.IntersectionMatrix3D;
import org.geotools.geometry.iso.sfcgal.relate.RelateOp3D;
import org.geotools.geometry.iso.sfcgal.wrapper.SFAlgorithm;
import org.geotools.geometry.iso.sfcgal.wrapper.SFGeometry;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.TransfiniteSet;

/**
 * @author Donguk Seo
 *
 */
public class Geometry3DOperation {
        
        /**
         * Compute the convexhull of geometry
         * 
         * 
         * @param geom
         * @return ConvexHull of the geometry
         */
        public static Geometry getConvexHull(GeometryImpl geom) {
                SFGeometry g = SFCGALConvertor.geometryToSFCGALGeometry((Geometry) geom);
                SFGeometry convex = SFAlgorithm.convexHull3D(g);

                return SFCGALConvertor.geometryFromSFCGALGeometry(convex);
        }

        /**
         * Compute the distance between two geometries using distance3D operation of SFCGAL
         * 
         * @param gA
         * @param gB
         * @return distance between two geometry objects
         */
        public static double distance(GeometryImpl gA, GeometryImpl gB) {
                SFGeometry geometryA = SFCGALConvertor.geometryToSFCGALGeometry(gA);
                SFGeometry geometryB = SFCGALConvertor.geometryToSFCGALGeometry(gB);
                double distance = SFAlgorithm.distance3D(geometryA, geometryB);

                return distance;
        }

        public static boolean relate(GeometryImpl gA, GeometryImpl gB, String intersectionPatternMatrix) {
                SFGeometry geometryA = SFCGALConvertor.geometryToSFCGALGeometry(gA);
                SFGeometry geometryB = SFCGALConvertor.geometryToSFCGALGeometry(gB);                
                IntersectionMatrix3D tIM = null;
                try {
                        tIM = RelateOp3D.relate(geometryA, geometryB);
                        return tIM.matches(intersectionPatternMatrix);
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return false;
        }

        /**
         *  
         * @param gA
         * @param gB
         * @return TRUE, if the gA is equal to gB
         */
        public static boolean equals(GeometryImpl gA, GeometryImpl gB) {
                SFGeometry geometryA = SFCGALConvertor.geometryToSFCGALGeometry(gA);
                SFGeometry geometryB = SFCGALConvertor.geometryToSFCGALGeometry(gB);
                boolean result = geometryA.equals(geometryB);

                return result;
        }
        
        public static boolean equals(SFGeometry gA, SFGeometry gB) {
                return gA.equals(gB);
        }

        /**
         * This operator tests, whether an object is spatially disjoint with other Geometry object
         * 
         * @param gA
         * @param gB
         * @return TRUE, if the gA is disjoint with gB
         */
        public static boolean disjoint(GeometryImpl gA, GeometryImpl gB) {
                return !intersects(gA, gB);
        }

        /**
         * This operator tests, whether an object spatially intersect with other Geometry object
         * 
         * @param gA
         * @param gB
         * @return TRUE, if the gA is intersect with gB
         */
        public static boolean intersects(GeometryImpl gA, GeometryImpl gB) {
                SFGeometry geometryA = SFCGALConvertor.geometryToSFCGALGeometry(gA);
                SFGeometry geometryB = SFCGALConvertor.geometryToSFCGALGeometry(gB);
                
                return intersects(geometryA, geometryB);
        }
        
        private static boolean intersects(SFGeometry gA, SFGeometry gB) {
                return SFAlgorithm.intersects3D(gA, gB);
        }

        /**
         * This operator tests, whether an object spatially touches with other Geometry object
         * 
         * @param gA
         * @param gB
         * @return TRUE, if the gA touches gB.
         */
        public static boolean touches(GeometryImpl gA, GeometryImpl gB) {
                SFGeometry geometryA = SFCGALConvertor.geometryToSFCGALGeometry(gA);
                SFGeometry geometryB = SFCGALConvertor.geometryToSFCGALGeometry(gB);
                
                return touches(geometryA, geometryB);
        }
        
        private static boolean touches(SFGeometry gA, SFGeometry gB) {
                IntersectionMatrix3D tIM = null;
                boolean isTouches = false;
                try {
                        tIM = RelateOp3D.relate(gA, gB);
                        if (tIM.matches("FT**") || tIM.matches("F*T*") || tIM.matches("F**T")) {
                                isTouches = true;
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return isTouches;
        }

        /**
         * This operator tests, whether an object spatially contains with other Geometry object
         * 
         * @param gA
         * @param gB
         * @return TRUE, if the gA is spatially contain gB.
         */
        public static boolean contains(GeometryImpl gA, GeometryImpl gB) {
                SFGeometry geometryA = SFCGALConvertor.geometryToSFCGALGeometry(gA);
                SFGeometry geometryB = SFCGALConvertor.geometryToSFCGALGeometry(gB);
                
                return contains(geometryA, geometryB);
        }
        
        private static boolean contains(SFGeometry gA, SFGeometry gB) {
                boolean result = !gA.equals(gB) && !touches(gA, gB)
                                && SFAlgorithm.covers3D(gA, gB);                
                return result;
        }        

        /**
         * This operator tests, whether an object is spatially within with other Geometry object
         * 
         * @param gA
         * @param gB
         * @return TRUE, if the gA is spatially within gB.
         */
        public static boolean within(GeometryImpl gA, GeometryImpl gB) {
                SFGeometry geometryA = SFCGALConvertor.geometryToSFCGALGeometry(gA);
                SFGeometry geometryB = SFCGALConvertor.geometryToSFCGALGeometry(gB);
                
                return within(geometryA, geometryB);
        }
        
        private static boolean within(SFGeometry gA, SFGeometry gB) {
                boolean result = !gA.equals(gB) && !touches(gB, gA)
                                && SFAlgorithm.covers3D(gB, gA);
                return result;
        }

        /**
         * This operator tests, whether an object spatially disjoint with other Geometry object
         * 
         * @param gA
         * @param gB
         * @return TRUE, if the gA overlaps with gB.
         */
        public static boolean overlaps(GeometryImpl gA, GeometryImpl gB) {
                SFGeometry geometryA = SFCGALConvertor.geometryToSFCGALGeometry(gA);
                SFGeometry geometryB = SFCGALConvertor.geometryToSFCGALGeometry(gB);

                return overlaps(geometryA, geometryB);
        }
        
        private static boolean overlaps(SFGeometry gA, SFGeometry gB) {
                SFGeometry intersection = SFAlgorithm.intersection3D(gA, gB);
                boolean result = false;
                
                if (gA.equals(gB))
                        result = false;
                else if (!intersection.isEmpty() && !contains(gA, gB) && !within(gA, gB)
                                && !touches(gA, gB)) {
                        if (gA.dimension() == 1 && gB.dimension() == 1) {
                                if (!intersection.isEmpty() && intersection.dimension() == 1) {
                                        result = true;
                                }
                        } else
                                result = true;
                }

                return result;
        }

        /**
         * This operator tests, whether an object spatially disjoint with other Geometry object
         * 
         * @param gA
         * @param gB
         * @return TRUE, if the gA crosses with gB.
         */
        public static boolean crosses(GeometryImpl gA, GeometryImpl gB) {
                SFGeometry geometryA = SFCGALConvertor.geometryToSFCGALGeometry(gA);
                SFGeometry geometryB = SFCGALConvertor.geometryToSFCGALGeometry(gB);
                
                return crosses(geometryA, geometryB);
        }
        
        private static boolean crosses(SFGeometry gA, SFGeometry gB) {
                SFGeometry intersection = SFAlgorithm.intersection3D(gA, gB);
                boolean result = false;
                
                if (gA.dimension() == 1 && gB.dimension() == 1) {
                        IntersectionMatrix3D tIM = null;
                        try {
                                tIM = RelateOp3D.relate(gA, gB);
                                if (tIM.matches("0***")) {
                                        result = true;
                                }
                        } catch (Exception e) {
                                e.printStackTrace();
                        }

                        return result;
                } else if (!intersection.isEmpty() && !touches(gA, gB) && !contains(gA, gB)
                                && !within(gA, gB)) {
                        result = true;
                }
                
                return result;
        }

        /**
         * Compute the union between two geometries using union3D operation of SFCGAL
         * 
         * @param gA
         * @param gB
         * @return union between two geometry objects
         */
        public static TransfiniteSet union(GeometryImpl gA, GeometryImpl gB) {
                SFGeometry geometryA = SFCGALConvertor.geometryToSFCGALGeometry(gA);
                SFGeometry geometryB = SFCGALConvertor.geometryToSFCGALGeometry(gB);
                SFGeometry union  = union(geometryA, geometryB);
                
                return SFCGALConvertor.geometryFromSFCGALGeometry(union);
        }
        
        private static SFGeometry union(SFGeometry gA, SFGeometry gB) {
                return SFAlgorithm.union3D(gA, gB);
        }
        
        /**
         * Compute the union between two geometries using intersection3D operation of SFCGAL
         * 
         * @param gA
         * @param gB
         * @return intersection between two geometry objects
         */
        public static TransfiniteSet intersection(GeometryImpl gA, GeometryImpl gB) {
                SFGeometry geometryA = SFCGALConvertor.geometryToSFCGALGeometry(gA);
                SFGeometry geometryB = SFCGALConvertor.geometryToSFCGALGeometry(gB);
                SFGeometry intersection = intersection(geometryA, geometryB);
                
                return SFCGALConvertor.geometryFromSFCGALGeometry(intersection);
        }
        
        private static SFGeometry intersection(SFGeometry gA, SFGeometry gB) {
                return SFAlgorithm.intersection3D(gA, gB);
        }

        /**
         * Compute the difference between two geometries using difference3D operation of SFCGAL
         * 
         * @param gA
         * @param gB
         * @return difference between two geometry objects
         */
        public static TransfiniteSet difference(GeometryImpl gA, GeometryImpl gB) {
                SFGeometry geometryA = SFCGALConvertor.geometryToSFCGALGeometry(gA);
                SFGeometry geometryB = SFCGALConvertor.geometryToSFCGALGeometry(gB);
                SFGeometry difference  = difference(geometryA, geometryB);

                return SFCGALConvertor.geometryFromSFCGALGeometry(difference);
        }
        
        private static SFGeometry difference(SFGeometry gA, SFGeometry gB) {
                return SFAlgorithm.difference3D(gA, gB);
        }

        /**
         * Compute the symmetric Difference between two geometries using 3D operations of SFCGAL
         * 
         * @param gA
         * @param gB
         * @return symmetric Difference between two geometry objects
         */
        public static TransfiniteSet symmetricDifference(GeometryImpl gA, GeometryImpl gB) {
                SFGeometry geometryA = SFCGALConvertor.geometryToSFCGALGeometry(gA);
                SFGeometry geometryB = SFCGALConvertor.geometryToSFCGALGeometry(gB);
                SFGeometry symDifference  = symmetricDifference(geometryA, geometryB);

                return SFCGALConvertor.geometryFromSFCGALGeometry(symDifference);
        }
        
        private static SFGeometry symmetricDifference(SFGeometry gA, SFGeometry gB) {
                SFGeometry union = union(gA, gB);
                SFGeometry intersection = intersection(gA, gB);
                SFGeometry symDifference = difference(union, intersection);
                
                return symDifference;
        }

}
