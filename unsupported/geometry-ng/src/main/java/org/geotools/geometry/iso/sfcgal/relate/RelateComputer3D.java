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
package org.geotools.geometry.iso.sfcgal.relate;

import java.util.ArrayList;

import org.geotools.geometry.iso.root.GeometryImpl;
import org.geotools.geometry.iso.sfcgal.util.SFCGALConvertor;
import org.geotools.geometry.iso.sfcgal.wrapper.SFAlgorithm;
import org.geotools.geometry.iso.sfcgal.wrapper.SFGeometry;
import org.geotools.geometry.iso.sfcgal.wrapper.SFGeometryCollection;
import org.geotools.geometry.iso.sfcgal.wrapper.SFLineString;
import org.geotools.geometry.iso.sfcgal.wrapper.SFMultiPoint;
import org.geotools.geometry.iso.sfcgal.wrapper.SFPoint;
import org.geotools.geometry.iso.sfcgal.wrapper.SFPolygon;
import org.geotools.geometry.iso.sfcgal.wrapper.SFSolid;
import org.geotools.geometry.iso.topograph2D.Dimension;
import org.geotools.geometry.iso.topograph2D.Location;

/**
 * @author Donguk Seo
 *
 */
public class RelateComputer3D {

        private IntersectionMatrix3D tIM;

        private SFGeometry geometryA;

        private SFGeometry geometryB;

        private SFGeometry boundaryA;

        private SFGeometry boundaryB;

        /* intersection between Closure of geometryA and Closure of geometryB */
        SFGeometry intersectionCC = null;

        /* intersection between Boundary of geometryA and Closure of geometryB */
        SFGeometry intersectionBC = null;

        /* intersection between Closure of geometryA and Boundary of geometryB */
        SFGeometry intersectionCB = null;

        /* intersection between Boundary of geometryA and Boundary of geometryB */
        SFGeometry intersectionBB = null;

        /**
         * @param gA
         * @param gB
         */
        public RelateComputer3D(SFGeometry gA, SFGeometry gB) {
                geometryA = gA;
                geometryB = gB;
                boundaryA = getBoundary(geometryA);
                boundaryB = getBoundary(geometryB);

                // Because Intersection operation of SFCGAL has a bug now,
                // Intersect point between two geometries must be added to LineString(or Polygon) object to get correct intersection.
                if (geometryA.geometryTypeId() == SFCGALConvertor.SFCGAL_LINESTRING_ID) {
                        if (!SFAlgorithm.covers3D(geometryB, geometryA)) {
                                geometryA = addPointToLineString(geometryA, boundaryB);
                        }
                } else if (geometryB.geometryTypeId() == SFCGALConvertor.SFCGAL_LINESTRING_ID) {
                        if (!SFAlgorithm.covers3D(geometryA, geometryB)) {
                                geometryB = addPointToLineString(geometryB, boundaryA);
                        }
                } else if (geometryA.geometryTypeId() == SFCGALConvertor.SFCGAL_POLYGON_ID) {
                        if (!SFAlgorithm.covers3D(geometryB, geometryA)) {
                                geometryA = addPointToPolygon(geometryA, boundaryB);
                        }
                } else if (geometryB.geometryTypeId() == SFCGALConvertor.SFCGAL_POLYGON_ID) {
                        if (!SFAlgorithm.covers3D(geometryA, geometryB)) {
                                geometryB = addPointToPolygon(geometryB, boundaryA);
                        }
                }
        }

        /**
         * Compute the Intersection Matrix for the two given geometry objects.
         * To compute the Intersection Matrix, use intersection operation.
         * And IntersectionMatrix3D has only interior and boundary elements, not exterior.
         * 
         * @return Returns the IntersectionMatrix3D.
         */
        public IntersectionMatrix3D computeIM3D() {
                tIM = new IntersectionMatrix3D();

                intersectionCC = SFAlgorithm.intersection3D(geometryA, geometryB);
                intersectionBC = SFAlgorithm.intersection3D(boundaryA, geometryB);
                intersectionCB = SFAlgorithm.intersection3D(geometryA, boundaryB);
                intersectionBB = SFAlgorithm.intersection3D(boundaryA, boundaryB);

                if (intersectionCC.geometryTypeId() == SFCGALConvertor.SFCGAL_LINESTRING_ID) {
                        intersectionCC = simplifyLineString(intersectionCC);
                }
                if (intersectionCB.geometryTypeId() == SFCGALConvertor.SFCGAL_LINESTRING_ID) {
                        intersectionCB = simplifyLineString(intersectionCB);
                }
                if (intersectionBC.geometryTypeId() == SFCGALConvertor.SFCGAL_LINESTRING_ID) {
                        intersectionBC = simplifyLineString(intersectionBC);
                }
                if (intersectionBB.geometryTypeId() == SFCGALConvertor.SFCGAL_LINESTRING_ID) {
                        intersectionBB = simplifyLineString(intersectionBB);
                }

                // Return "FFFF", if the intersection is empty.
                if (intersectionCC.isEmpty()) {
                        tIM.set(Location.INTERIOR, Location.INTERIOR, Dimension.FALSE);
                        tIM.set(Location.INTERIOR, Location.BOUNDARY, Dimension.FALSE);
                        tIM.set(Location.BOUNDARY, Location.INTERIOR, Dimension.FALSE);
                        tIM.set(Location.BOUNDARY, Location.BOUNDARY, Dimension.FALSE);

                        return tIM;
                }
                computeInteriorInterior();
                computeInteriorBoundary();
                computeBoundaryInterior();
                computeBoundaryBoundary();

                return tIM;
        }

        /**
         * @param g
         * @return Returns the boundary of g
         * If the geometry is polygon, returns the boundary.
         * If the geometry is solid, returns the exterior Shell.
         * Because SFCGAL has no algorithm supports interior shell yet.
         */
        public SFGeometry getBoundary(SFGeometry g) {
                SFGeometry boundary = null;

                if (g.geometryTypeId() == SFCGALConvertor.SFCGAL_POLYGON_ID) {
                        boundary = ((SFPolygon) g).boundary();
                } else if (g.geometryTypeId() == SFCGALConvertor.SFCGAL_SOLID_ID) {
                        boundary = ((SFSolid) g).exteriorShell();
                } else {
                        boundary = g.boundary();
                }

                return boundary;
        }

        /**
         * @param g
         * @return LineString
         * If p1, p2, p3 of LineString are collinear, p2 is removed.
         */
        public SFGeometry simplifyLineString(SFGeometry g) {
                SFLineString line = new SFLineString(g);

                if (line.numPoints() == 2)
                        return line;

                ArrayList<SFPoint> points = new ArrayList<SFPoint>();
                points.add(line.startPoint());
                for (int i = 1; i < line.numPoints() - 1; i++) {
                        SFPoint prevP = line.pointN(i - 1);
                        SFPoint currP = line.pointN(i);
                        SFPoint nextP = line.pointN(i + 1);

                        SFLineString tLine = new SFLineString(prevP, nextP);
                        if (!SFAlgorithm.covers3D(tLine, currP)) {
                                points.add(currP);
                        }
                }
                points.add(line.endPoint());

                if (line.numPoints() == points.size())
                        return line;

                return new SFLineString(points);
        }

        public boolean isContain(ArrayList<SFPoint> points, SFPoint p) {
                for (SFPoint point : points) {
                        if (point.equals(p)) {
                                return true;
                        }
                }

                return false;
        }

        /**
         * Add intersect point between Polygon and other geometry to polygon for correct result of intersection operation.
         * 
         * @param poly
         * @param other
         * @return SFPolygon
         */
        public SFGeometry addPointToPolygon(SFGeometry poly, SFGeometry other) {
                if (poly.dimension() != 2 || other.dimension() == 0) {
                        return poly;
                }

                SFPolygon polygon = (SFPolygon) SFCGALConvertor.getSFGeometry(poly);
                SFLineString exteriorRing = (SFLineString) polygon.exteriorRing();
                SFLineString newExteriorRing = (SFLineString) addPointToLineString(exteriorRing,
                                other);
                newExteriorRing.addPoint(exteriorRing.endPoint());

                return new SFPolygon(newExteriorRing);
        }

        /**
         * Add intersect point between LineString and other geometry to LineString for correct result of intersection operation.
         * 
         * @param line
         * @param other
         * @return SFLineString
         */
        public SFGeometry addPointToLineString(SFGeometry line, SFGeometry other) {
                if (line.dimension() != 1 || other.dimension() == 0) {
                        return line;
                }

                SFLineString lineString = (SFLineString) SFCGALConvertor.getSFGeometry(line);

                ArrayList<SFPoint> points = new ArrayList<SFPoint>();
                for (int i = 0; i < lineString.numPoints() - 1; i++) {
                        if (!isContain(points, lineString.pointN(i))) {
                                points.add(lineString.pointN(i));
                        }
                        SFLineString tempLine = new SFLineString(lineString.pointN(i),
                                        lineString.pointN(i + 1));
                        SFGeometry intersection = SFAlgorithm.intersection3D(tempLine, other);
                        intersection = SFCGALConvertor.getSFGeometry(intersection);

                        if (intersection.isEmpty())
                                continue;
                        int typeID = intersection.geometryTypeId();
                        switch (typeID) {
                        case SFCGALConvertor.SFCGAL_GEOMETRYCOLLECTION_ID:
                        case SFCGALConvertor.SFCGAL_MULTIPOINT_ID:
                        case SFCGALConvertor.SFCGAL_MULTILINESTRING_ID:
                                ArrayList<SFGeometry> orderedGeometry = new ArrayList<SFGeometry>();
                                for (int j = 0; j < intersection.numGeometries(); j++) {
                                        double d1 = SFAlgorithm.distance3D(lineString.pointN(i),
                                                        intersection.geometryN(j));
                                        boolean isInserted = false;
                                        for (int k = 0; k < orderedGeometry.size(); k++) {
                                                double d2 = SFAlgorithm.distance3D(
                                                                lineString.pointN(i),
                                                                orderedGeometry.get(k));

                                                if (d1 < d2) {
                                                        orderedGeometry.add(k,
                                                                        intersection.geometryN(j));
                                                        isInserted = true;
                                                        break;
                                                }
                                        }

                                        if (!isInserted) {
                                                orderedGeometry.add(intersection.geometryN(j));
                                        }
                                }

                                for (int j = 0; j < orderedGeometry.size(); j++) {
                                        merge(points, orderedGeometry.get(j));
                                }
                                break;
                        default:
                                merge(points, intersection);
                        }

                }
                if (!isContain(points, lineString.endPoint())) {
                        points.add(lineString.endPoint());
                }

                return new SFLineString(points);
        }

        public void merge(ArrayList<SFPoint> points, SFGeometry geometry) {
                geometry = SFCGALConvertor.getSFGeometry(geometry);
                if (geometry.geometryTypeId() == SFCGALConvertor.SFCGAL_POINT_ID) {
                        SFPoint point = (SFPoint) geometry;
                        if (!isContain(points, point)) {
                                points.add(point);
                        }
                } else if (geometry.geometryTypeId() == SFCGALConvertor.SFCGAL_LINESTRING_ID) {
                        SFLineString line = (SFLineString) geometry;
                        ArrayList<SFPoint> orderedPoints = new ArrayList<SFPoint>();
                        for (int i = 0; i < line.numPoints(); i++) {
                                double d1 = SFAlgorithm.distance3D(points.get(points.size() - 1),
                                                line.pointN(i));
                                boolean isInserted = false;
                                for (int j = 0; j < orderedPoints.size(); j++) {
                                        double d2 = SFAlgorithm.distance3D(
                                                        points.get(points.size() - 1),
                                                        orderedPoints.get(j));

                                        if (d1 < d2) {
                                                orderedPoints.add(j, line.pointN(i));
                                                isInserted = true;
                                                break;
                                        }
                                }

                                if (!isInserted) {
                                        orderedPoints.add(line.pointN(i));
                                }
                        }

                        for (int i = 0; i < orderedPoints.size(); i++) {
                                if (!isContain(points, orderedPoints.get(i))) {
                                        points.add(orderedPoints.get(i));
                                }
                        }
                }
        }

        /**
         * Compute (Interior, Interior) element of Intersection Matrix
         */
        public void computeInteriorInterior() {
                SFGeometry difference1 = SFAlgorithm.difference3D(intersectionCC, intersectionBC);
                SFGeometry difference2 = SFAlgorithm.difference3D(intersectionCC, intersectionCB);

                // The equals operation about TIN of SFCGAL, is not correct.
                // So if the difference between of two geometries is empty, they will be equal.

                // If intersection between two geometries(closures) is equal to intersection between boundaryA and geometryB,
                // the geometryB touches boundaryA, not interior of geometryA.
                // If intersection between two geometries(closures) is equal to intersection between geometryA and boundaryB,
                // the geometryA touches boundaryB, not interior of geometryB.
                if (intersectionCC.equals(intersectionBC) || difference1.isEmpty()) {
                        tIM.set(Location.INTERIOR, Location.INTERIOR, Dimension.FALSE); // "F***"
                } else if (intersectionCC.equals(intersectionCB) || difference2.isEmpty()) {
                        tIM.set(Location.INTERIOR, Location.INTERIOR, Dimension.FALSE); // "F***"
                } else {
                        tIM.set(Location.INTERIOR, Location.INTERIOR, intersectionCC.dimension());
                }
        }

        /**
         * Compute (Interior, Boundary) element of Intersection Matrix
         */
        public void computeInteriorBoundary() {
                SFGeometry difference1 = SFAlgorithm.difference3D(intersectionCC, intersectionBC);
                SFGeometry difference2 = SFAlgorithm.difference3D(intersectionCB, intersectionBB);

                // If intersection between two geometries(closures) is equal to intersection between boundaryA and geometryB,
                // the geometryB touches boundaryA, not interior of geometryA.
                // If intersection between geometryA and boundaryB is equal to intersection between two boundaries,
                // the boundaryB touches boundaryA, not interior of geometryA.
                if (intersectionCC.equals(intersectionBC) || difference1.isEmpty()) {
                        tIM.set(Location.INTERIOR, Location.BOUNDARY, Dimension.FALSE); // "*F**"
                } else if (intersectionCB.equals(intersectionBB) || difference2.isEmpty()) {
                        tIM.set(Location.INTERIOR, Location.BOUNDARY, Dimension.FALSE); // "*F**"
                } else {
                        tIM.set(Location.INTERIOR, Location.BOUNDARY, intersectionCB.dimension());
                }
        }

        /**
         * Compute (Boundary, Interior) element of Intersection Matrix
         */
        public void computeBoundaryInterior() {
                SFGeometry difference1 = SFAlgorithm.difference3D(intersectionCC, intersectionCB);
                SFGeometry difference2 = SFAlgorithm.difference3D(intersectionBC, intersectionCB);

                // If intersection between two geometries(closures) is equal to intersection between geometryA and boundaryB,
                // the geometryB touches boundaryA, not interior of geometryA.
                // If intersection between boundaryA and geometryB is equal to intersection between two boundaries,
                // the boundaryA touches boundaryB, not interior of geometryB.
                if (intersectionCC.equals(intersectionCB) || difference1.isEmpty()) {
                        tIM.set(Location.BOUNDARY, Location.INTERIOR, Dimension.FALSE); // "**F*"
                } else if (intersectionBC.equals(intersectionBB) || difference2.isEmpty()) {
                        tIM.set(Location.BOUNDARY, Location.INTERIOR, Dimension.FALSE); // "**F*"
                } else {
                        tIM.set(Location.BOUNDARY, Location.INTERIOR, intersectionBC.dimension());
                }
        }

        /**
         * Compute (Boundary, Boundary) element of Intersection Matrix
         */
        public void computeBoundaryBoundary() {
                if (intersectionBB.isEmpty()) {
                        tIM.set(Location.BOUNDARY, Location.BOUNDARY, Dimension.FALSE); // "***F"
                } else {
                        tIM.set(Location.BOUNDARY, Location.BOUNDARY, intersectionBB.dimension());
                }
        }
}
