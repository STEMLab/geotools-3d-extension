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

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.ByRef;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Platform;

/**
 * @author Donguk Seo
 *
 */
@Platform(include = { "cpp/SFEnvelope.h", "cpp/SFEnvelope.cpp" })
public class SFEnvelope extends Pointer {
        static {
                Loader.load();
        }

        /**
         * Default constructor (empty bounding box)
         */
        public SFEnvelope() {
                allocate();
        }

        /**
         * 2D box constructor with min, max values
         * @param xmin
         * @param xmax
         * @param ymin
         * @param ymax
         */
        public SFEnvelope(double xmin, double xmax, double ymin, double ymax) {
                allocate(xmin, xmax, ymin, ymax);
        }

        /**
         * 3D box constructor with min, max values
         * @param xmin
         * @param xmax
         * @param ymin
         * @param ymax
         * @param zmin
         * @param zmax
         */
        public SFEnvelope(double xmin, double xmax, double ymin, double ymax, double zmin,
                        double zmax) {
                allocate(xmin, xmax, ymin, ymax, zmin, zmax);
        }

        /**
         * Envelope constructor with one coordinate
         * @param p
         */
        public SFEnvelope(SFCoordinate p) {
                allocate(p);
        }

        /**
         * Envelope constructor with two coordinates
         * @param p1
         * @param p2
         */
        public SFEnvelope(SFCoordinate p1, SFCoordinate p2) {
                allocate(p1, p2);
        }

        /**
         * Copy constructor
         * @param p
         */
        public SFEnvelope(Pointer p) {
                super(p);
        }

        /**
         * Call the native empty envelope constructor
         */
        private native void allocate();

        /**
         * Call the native 2D envelope constructor with min, max values
         * @param xmin
         * @param xmax
         * @param ymin
         * @param ymax
         */
        private native void allocate(double xmin, double xmax, double ymin, double ymax);

        /**
         * Call the native 3D envelope consructor with min, max values
         * @param xmin
         * @param xmax
         * @param ymin
         * @param ymax
         * @param zmin
         * @param zmax
         */
        private native void allocate(double xmin, double xmax, double ymin, double ymax,
                        double zmin, double zmax);

        /**
         * Call the native envelope constructor with one coordinate
         * @param p
         */
        private native void allocate(@ByRef SFCoordinate p);

        /**
         * Call the native envelope constructor with two coordinates
         * @param p1
         * @param p2
         */
        private native void allocate(@ByRef SFCoordinate p1, @ByRef SFCoordinate p2);

        /**
         * Assign operator
         * @param c
         * @return Instance of SFEnvelope
         */
        @Name("operator=")
        public native @ByRef SFEnvelope assign(@ByRef SFEnvelope c);

        /**
         * @return True, if this envelope is empty
         */
        public native @Cast("bool") boolean isEmpty();

        /**
         * @return True, if this coordinate dimension of this envelope is 3D
         */
        public native @Cast("bool") boolean is3D();

        /**
         * Expand the box to include coordinate
         * @param coordinate SFCoordinate Instance
         */
        public native void expandToInclude(@ByRef SFCoordinate coordinate);

        /**
         * @return Returns the x-min value
         */
        public native @ByRef double xMin();

        /**
         * @return Returns the y-min value
         */
        public native @ByRef double yMin();

        /**
         * @return Returns the z-min value
         */
        public native @ByRef double zMin();

        /**
         * @return Returns the x-max value
         */
        public native @ByRef double xMax();

        /**
         * @return Returns the y-max value
         */
        public native @ByRef double yMax();

        /**
         * @return Retursn the z-max value
         */
        public native @ByRef double zMax();

        /**
         * @param a
         * @param b
         * @return True, if the envelope contains the envelope b
         */
        public static native @Cast("bool") boolean contains(@ByRef SFEnvelope a, @ByRef SFEnvelope b);

        /**
         * @param a
         * @param b
         * @return True, if the envelope overlaps the envelope b 
         */
        public static native @Cast("bool") boolean overlaps(@ByRef SFEnvelope a, @ByRef SFEnvelope b);

        /**
         * Convert to 2D LinearRing
         * @return Instance of SFLineString
         */
        public native @ByRef SFLineString toRing();

        /**
         * Convert to 2D Polygon
         * @return Instance of SFPolygon
         */
        public native @ByRef SFPolygon toPolygon();

        /**
         * Convert to 3D Solid
         * @return Instance of SFSolid
         */
        public native @ByRef SFSolid toSolid();

        /**
         * Equal operator
         * @param other
         * @return True, if this envelope is equal to another envelope
         */
        @Name("operator==")
        public native @Cast("bool") boolean equals(@ByRef SFEnvelope other);

}
