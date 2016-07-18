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
import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.StdString;

/**
 * @author Donguk Seo
 *
 */
@Platform(include = "cpp/SFPreparedGeometry.h")
public class SFPreparedGeometry extends Pointer {
        static {
                Loader.load();
        }

        public SFPreparedGeometry() {
                allocate();
        }

        public SFPreparedGeometry(SFGeometry g) {
                this(g, 0);
        }

        public SFPreparedGeometry(SFGeometry g, long srid) {
                allocate(g, srid);
        }

        private native void allocate();

        private native void allocate(@ByRef SFGeometry g, long srid);

        public native @ByRef SFGeometry geometry();

        public native void resetGeometry(SFGeometry g);

        public native @ByRef @Cast("srid_t") long SRID();

        public native void resetSRID(@Cast("srid_t") long srid);

        public native @ByRef SFEnvelope envelope();

        public native void invalidateCache();

        public String asEWKT() {
                return asEWKT(-1);
        }

        public native @StdString String asEWKT(int numDecimals);

}
