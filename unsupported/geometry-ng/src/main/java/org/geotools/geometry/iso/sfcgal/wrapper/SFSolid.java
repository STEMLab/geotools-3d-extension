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
@Platform(include = "cpp/SFSolid.h")
public class SFSolid extends SFGeometry {
        static {
                Loader.load();
        }

        public SFSolid() {
                allocate();
        }

        public SFSolid(ArrayList<SFPolyhedralSurface> shells) {
                PointerVector vector = new PointerVector(shells.size());

                for (int i = 0; i < shells.size(); i++) {
                        vector.get(i).put(shells.get(i));
                }

                allocate(vector);
        }

        public SFSolid(Pointer p) {
                super(p);
        }

        public SFSolid(SFPolyhedralSurface exteriorShell) {
                allocate(exteriorShell);
        }

        private native void allocate();

        private native void allocate(@ByRef PointerVector p);

        private native void allocate(@ByRef SFPolyhedralSurface exteriorShell);

        @Name("operator=")
        public native @ByRef SFSolid assign(@ByRef SFSolid tr);

        public native SFSolid clone();

        public native @StdString String geometryType();

        public native int geometryTypeId();

        public native int dimension();

        public native int coordinateDimension();

        public native @Cast("bool") boolean isEmpty();

        public native @Cast("bool") boolean is3D();

        public native @Cast("bool") boolean isMeasured();

        public native @ByRef SFPolyhedralSurface exteriorShell();

        public native @Cast("size_t") int numInteriorShells();

        public native @ByRef SFPolyhedralSurface interiorShellN(@Cast("size_t") int n);

        public native void addInteriorShell(@ByRef SFPolyhedralSurface shell);

        public native @Cast("size_t") int numShells();

        public native @ByRef SFPolyhedralSurface shellN(@Cast("size_t") int n);

}
