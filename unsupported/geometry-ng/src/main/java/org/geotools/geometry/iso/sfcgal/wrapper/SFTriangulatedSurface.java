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
@Platform(include = "cpp/SFTriangulatedSurface.h")
public class SFTriangulatedSurface extends SFSurface {
        static {
                Loader.load();
        }

        public SFTriangulatedSurface() {
                allocate();
        }

        public SFTriangulatedSurface(ArrayList<SFTriangle> triangle) {
                PointerVector vector = new PointerVector(triangle.size());

                for (int i = 0; i < triangle.size(); i++) {
                        vector.get(i).put(triangle.get(i));
                }

                allocate(vector);
        }

        public SFTriangulatedSurface(Pointer p) {
                super(p);
        }

        private native void allocate();

        private native void allocate(@ByRef PointerVector p);

        @Name("operator=")
        public native @ByRef SFTriangulatedSurface assign(@ByRef SFTriangulatedSurface tr);

        public native SFTriangulatedSurface clone();

        public native @StdString String geometryType();

        public native int geometryTypeId();

        public native int dimension();

        public native int coordinateDimension();

        public native @Cast("bool") boolean isEmpty();

        public native @Cast("bool") boolean is3D();

        public native @Cast("bool") boolean isMeasured();

        public native @Cast("size_t") int numTriangles();

        public native @ByRef SFTriangle triangleN(@Cast("size_t") int n);

        public native void addTriangle(SFTriangle triangle);

        public native void addTriangles(@ByRef SFTriangulatedSurface other);

        public native @Cast("size_t") int numGeometries();

        public native @ByRef SFTriangle geometryN(@Cast("size_t") int n);

        public native void reserve(@Cast("size_t") int n);

}
