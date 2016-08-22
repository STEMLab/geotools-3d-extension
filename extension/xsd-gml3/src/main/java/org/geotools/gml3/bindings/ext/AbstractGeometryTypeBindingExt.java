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

import org.geotools.gml2.SrsSyntax;
import org.geotools.gml3.bindings.AbstractGeometryTypeBinding;
import org.geotools.xml.Configuration;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Donguk Seo
 *
 */
public class AbstractGeometryTypeBindingExt extends AbstractGeometryTypeBinding {

    /**
     * @param config
     * @param srsSyntax
     */
    public AbstractGeometryTypeBindingExt(Configuration config, SrsSyntax srsSyntax) {
        super(config, srsSyntax);
    }

        
    @Override
    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {
        //set the crs
        if (value instanceof Geometry) {
            CoordinateReferenceSystem crs = GML3ParsingUtils3D.crs(node);

            if (crs != null) {
                Geometry geometry = (Geometry) value;
                geometry.setUserData(crs);
                /*
                if (geometry.getUserData() == null) {
                    geometry.setUserData(new HashMap());
                }
                if (geometry.getUserData() instanceof Map) {
                    ((Map) geometry.getUserData()).put("crs", crs);
                }
                */
            }
        }

        return value;
    }


    @Override
    public Object getProperty(Object object, QName name) throws Exception {
        if (object instanceof org.geotools.geometry.iso.root.GeometryImpl) {
            return null;
        }
        
        return super.getProperty(object, name);
    }
}
