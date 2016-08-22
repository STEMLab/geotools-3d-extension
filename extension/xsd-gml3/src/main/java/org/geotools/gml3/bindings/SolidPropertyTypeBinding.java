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
package org.geotools.gml3.bindings;

import javax.xml.namespace.QName;

import org.geotools.geometry.iso.primitive.SolidImpl;
import org.geotools.gml3.GML;
import org.geotools.gml3.XSDIdRegistry;

/**
 * @author Donguk Seo
 *
 */
public class SolidPropertyTypeBinding extends GeometryPropertyTypeBindingBase {

    public SolidPropertyTypeBinding(GML3EncodingUtils encodingUtils, XSDIdRegistry idRegistry) {
        super(encodingUtils, idRegistry);
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return GML.SolidPropertyType;
    }

    public Class getType() {
        return SolidImpl.class;
    }
}
