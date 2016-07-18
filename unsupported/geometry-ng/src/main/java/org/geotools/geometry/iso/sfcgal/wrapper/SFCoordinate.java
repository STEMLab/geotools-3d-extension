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
@Platform(include = "cpp/SFCoordinate.h")
public class SFCoordinate extends Pointer {
        static {
                Loader.load();
        }

        /**
         * Empty coordinate constructor
         */
        public SFCoordinate() {
                allocate();
        }

        /**
         * XY constructor with exact coordinates.
         * @param x
         * @param y
         */
        public SFCoordinate(double x, double y) {
                allocate(x, y);
        }

        /**
         * XYZ constructor with exact coordinates.
         * @param x
         * @param y
         * @param z
         */
        public SFCoordinate(double x, double y, double z) {
                allocate(x, y, z);
        }

        /**
         * Copy constructor
         * @param p
         */
        public SFCoordinate(Pointer p) {
                super(p);
        }

        /**
         * Call the native empty coordinate constructor
         */
        private native void allocate();

        /**
         * Call the native coordinate constructor with xyz
         * @param x
         * @param y
         * @param z
         */
        private native void allocate(double x, double y, double z);

        /**
         * Call the native coordinate constructor with xy
         * @param x
         * @param y
         */
        private native void allocate(double x, double y);

        @Name("operator=")
        public native @ByRef SFCoordinate assign(@ByRef SFCoordinate c);

        /**
         * @return Returns the coordinate dimension
         */
        public native int coordinateDimension();

        /**
         * @return True, if the coordinate is empty
         */
        public native @Cast("bool") boolean isEmpty();

        /**
         * @return True, if the coordinate is 3D
         */
        public native @Cast("bool") boolean is3D();

        /**
         * @return returns the x value
         */
        public native double x();

        /**
         * @return returns the y value
         */
        public native double y();

        /**
         * @return return the z value
         */
        public native double z();

        public native @ByRef SFCoordinate round(@ByRef long scaleFactor);

        /**
         * Compares with another point (lexicographic order)
         * @param c SFCoordinate Instance 
         * @return True, if this coordinate is smaller than another coordinate
         */
        @Name("operator<")
        public native @Cast("bool") boolean isSmallerThan(@ByRef SFCoordinate c);

        /**
         * Compares with another point
         * @param c SFCoordinate Instance
         * @return True, if this coordinate is equal to another coordinate 
         */
        @Name("operator==")
        public native @Cast("bool") boolean equals(@ByRef SFCoordinate c);

        /**
         * Compares with another point
         * @param c SFCoordiante Instance
         * @return True, if this coordinate is not equal to another coordinate
         */
        @Name("operator!=")
        public native @Cast("bool") boolean notEquals(@ByRef SFCoordinate c);

        public static void main(String args[]) {
                SFCoordinate coord = new SFCoordinate(1.1, 2.2, 3.3);
                System.out.println(coord.x() + " " + coord.y() + " " + coord.z());
        }
}
