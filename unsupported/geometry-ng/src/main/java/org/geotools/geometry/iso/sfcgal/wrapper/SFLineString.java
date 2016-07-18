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

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.ByRef;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.StdString;

/**
 * @author Donguk Seo
 *
 */
@Platform(include = "cpp/SFLineString.h")
public class SFLineString extends SFGeometry {
        static {
                Loader.load();
        }

        /**
         * Default constructor (empty LineString)
         */
        public SFLineString() {
                allocate();
        }

        /**
         * SFLineString constructor with a list of the SFPoints
         * @param p A list of the SFPoints
         */
        public SFLineString(ArrayList<SFPoint> p) {
                PointerVector vector = new PointerVector(p.size());

                for (int i = 0; i < p.size(); i++) {
                        vector.get(i).put(p.get(i));
                }

                allocate(vector);
        }

        /**
         * SFLineString constructor with start and end point
         * @param startPoint SFPoint instance
         * @param endPoint SFPoint instance
         */
        public SFLineString(SFPoint startPoint, SFPoint endPoint) {
                allocate(startPoint, endPoint);
        }

        /**
         * Copy constructor
         * @param p
         */
        public SFLineString(Pointer p) {
                super(p);
        }

        /**
         * Call the native SFLineString constructor
         */
        private native void allocate();

        /**
         * Call the native SFLineString constructor with a list
         * @param p
         */
        private native void allocate(@ByRef PointerVector p);

        /**
         * Call the native SFLineString constructor with start and end point
         * @param startPoint
         * @param endPoint
         */
        private native void allocate(@ByRef SFPoint startPoint, @ByRef SFPoint endPoint);

        /**
         * Assign operator
         * @param ls
         * @return Instance of SFLineString
         */
        @Name("operator=")
        public native @ByRef SFLineString assign(@ByRef SFLineString ls);

        /**
         * @return Returns the clone of the geometry
         */
        public native SFLineString clone();

        /**
         * @return Returns the type of the geometry as string
         */
        public native @StdString String geometryType();

        /**
         * @return Returns the type of the geometry as integer
         */
        public native int geometryTypeId();

        /**
         * @return Returns the dimension of the geometry
         */
        public native int dimension();

        /**
         * @return Returns the coordinate dimension of the geometry
         */
        public native int coordinateDimension();

        /**
         * @return True, if the geometry is empty
         */
        public native @Cast("bool") boolean isEmpty();

        /**
         * @return True, if coordinate dimension of the geometry is 3D
         */
        public native @Cast("bool") boolean is3D();

        /**
         * @return True, if the geometry is measured (has an m)
         */
        public native @Cast("bool") boolean isMeasured();

        /**
         * Remove all points from the LineString
         */
        public native void clear();

        /**
         * Reverse LineStrign orientation
         */
        public native void reverse();

        /**
         * @return Returns the number of points in a LineString
         */
        public native @Cast("size_t") int numPoints();

        /**
         * @return Returns the number of segmtns in a LineString
         */
        public native @Cast("size_t") int numSegments();

        /**
         * @param n
         * @return Returns the n-th point
         */
        public native @ByRef SFPoint pointN(@Cast("size_t") int n);

        /**
         * @param n
         * @return Returns the start point
         */
        public native @ByRef SFPoint startPoint();

        /**
         * @param n
         * @return Returns the end point
         */
        public native @ByRef SFPoint endPoint();

        /**
         * Add point to LineString
         * @param p SFPoint instance
         */
        public native void addPoint(SFPoint p);

        /**
         * @return True, if the LineString is closed
         */
        public native @Cast("bool") boolean isClosed();

        public native void reserve(@Cast("size_t") int n);

}
