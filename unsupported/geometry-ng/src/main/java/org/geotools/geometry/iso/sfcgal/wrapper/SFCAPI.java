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
import org.bytedeco.javacpp.annotation.ByRef;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.StdString;

/**
 * @author Donguk Seo
 *
 */
@Platform(include = "cpp/SFCAPI.h")
public class SFCAPI {
        static {
                Loader.load();
        }

        public static native @ByRef SFGeometry SFCGAL_io_read_wkt(@ByRef @StdString String str,
                        @Cast("size_t") int len);

        public static void SFCGAL_io_write_binary_prepared(SFPreparedGeometry geom, String buffer,
                        int len) {
                SFCGAL_io_write_binary_prepared(geom, buffer.toCharArray(), len);
        }

        public static native void SFCGAL_io_write_binary_prepared(SFPreparedGeometry geom,
                        @Cast("char *") char[] buffer, @Cast("size_t") int len);

        public static native @ByRef SFPreparedGeometry SFCGAL_io_read_binary_prepared(
                        @StdString String str, @Cast("size_t") int len);

        public static native @ByRef SFPreparedGeometry SFCGAL_io_read_ewkt(@StdString String str,
                        @Cast("size_t") int len);

        public static native @ByRef SFGeometry SFCGAL_geometry_force_lhr(@ByRef SFGeometry g);

        public static native @ByRef SFGeometry SFCGAL_geometry_force_rhr(@ByRef SFGeometry g);

        public static native @ByRef SFTriangulatedSurface SFCGAL_geometry_triangulate_2dz(
                        @ByRef SFGeometry g);

}
