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
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.annotation.ByRef;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Namespace;
import org.bytedeco.javacpp.annotation.Platform;

/**
 * 
 *
 */
@Platform(include = "<vector>")
@Namespace("std")
@Name("vector<void*>")
public class PointerVector extends Pointer {
        static {
                Loader.load();
        }

        public PointerVector() {
                allocate();
        }

        public PointerVector(long n) {
                allocate(n);
        }

        // (vector<void*>*)p
        public PointerVector(Pointer p) {
                super(p);
        }

        // new std::vector<void*>()
        private native void allocate();

        // new std::vector<void*>(n)
        private native void allocate(long n);

        @Name("operator=")
        public native @ByRef PointerVector copy(@ByRef PointerVector x);

        public native long size();

        public native @Cast("bool") boolean empty();

        @Name("operator[]")
        public native @ByRef PointerPointer get(long n);

        public native @ByRef PointerPointer at(long n);

}
