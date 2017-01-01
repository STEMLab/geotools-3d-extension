/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gml3.iso.bindings.ext;

import org.geotools.gml3.iso.bindings.SurfaceTypeBinding;
import org.opengis.geometry.ISOGeometryBuilder;


/**
 * 
 *
 * @source $URL$
 */
public class MultiPolygonTypeBinding extends org.geotools.gml3.iso.bindings.MultiPolygonTypeBinding {

    public MultiPolygonTypeBinding(ISOGeometryBuilder gBuilder) {
        super(gBuilder);
    }

    /**
     * Implement comparable because MultiPolygonBinding, MultiSurfaceBinding and Surface
     * are bound to the same class, MultiPolygon. Since MultiPolygon is deprecated
     * by gml3 and MultiSurface only has children that are also mapped to MultiPolygons,
     * Surface always wins.
     */
    public int compareTo(Object o) {
        if (o instanceof SurfaceTypeBinding) {
            return 1;
        } else {
            return 0;
        }
    }
}
