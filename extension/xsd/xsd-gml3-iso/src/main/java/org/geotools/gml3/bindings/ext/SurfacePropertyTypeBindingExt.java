/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2016, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gml3.bindings.ext;

import javax.xml.namespace.QName;

import org.geotools.gml3.XSDIdRegistry;
import org.geotools.gml3.bindings.GML3EncodingUtils;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 * @author Donguk Seo
 *
 */
public class SurfacePropertyTypeBindingExt extends SurfacePropertyTypeBinding {

    /**
     * @param encodingUtils
     * @param idRegistry
     * @param gf
     */
    public SurfacePropertyTypeBindingExt(GML3EncodingUtils encodingUtils, XSDIdRegistry idRegistry,
            GeometryFactory gf) {
        super(encodingUtils, idRegistry, gf);
    }

    @Override
    public Object getProperty(Object object, QName name)
        throws Exception {
        /**
         * Override getProperty for a case of a MultiPolygon consists of a several Polygons.
         */
        if ("_Surface".equals(name.getLocalPart()) || "AbstractSurface".equals(name.getLocalPart())) {
            MultiPolygon multiPolygon = (MultiPolygon) object;
            // this MultiPolygon consists of a single Polygon wrapped in a MultiPolygon:
            if (multiPolygon.getNumGeometries() == 1) {
                return multiPolygon.getGeometryN(0);
            } else {
                return multiPolygon;
            }
        }

        return super.getProperty(object, name);
    }
}
