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

import javax.xml.namespace.QName;

import org.geotools.gml3.iso.bindings.PolygonTypeBinding;
import org.opengis.geometry.ISOGeometryBuilder;

/**
 * 
 *
 * @source $URL$
 */
public class PolygonPatchTypeBinding extends org.geotools.gml3.iso.bindings.PolygonPatchTypeBinding
    implements Comparable {

    public PolygonPatchTypeBinding(ISOGeometryBuilder gb) {
        super(gb);
    }
    
    @Override
    public Object getProperty(Object object, QName name) throws Exception {
        return new PolygonTypeBinding( gBuilder ).getProperty(object, name);
    }

    public int compareTo(Object o) {
        if (o instanceof PolygonTypeBinding) {
            return 1;
        } else {
            return 0;
        }
    }
}
