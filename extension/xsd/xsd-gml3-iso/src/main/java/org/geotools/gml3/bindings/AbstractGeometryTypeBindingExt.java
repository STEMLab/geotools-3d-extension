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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.geotools.gml2.SrsSyntax;
import org.geotools.gml2.bindings.GML2EncodingUtils;
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
        if (value instanceof Geometry ||
                value instanceof org.geotools.geometry.iso.root.GeometryImpl) {
            CoordinateReferenceSystem crs = GML3ParsingUtils.crs(node);
            if (crs != null) {
                GML3ParsingUtils.setCRS(value, crs);
            }
            
            String id = GML3ParsingUtils.id(node);
            if (id != null) {
                GML3ParsingUtils.setID(value, id);
            }
            
            String name = GML3ParsingUtils.name(node);
            if (name != null) {
                GML3ParsingUtils.setName(value, name);
            }
            
            String description = GML3ParsingUtils.description(node);
            if (description != null) {
                GML3ParsingUtils.setDescription(value, description);
            }
        }

        return value;
    }


    @Override
    public Object getProperty(Object object, QName name) throws Exception {
        if ("srsName".equals(name.getLocalPart())) {
            CoordinateReferenceSystem crs = GML3EncodingUtils.getCRS(object);
            if (crs != null) {
                return GML3EncodingUtils.toURI(crs, srsSyntax);
            }
        }
        
        if ("srsDimension".equals(name.getLocalPart())) {
            return GML3EncodingUtils.getGeometryDimension(object, config);
        }
        
        if ("id".equals(name.getLocalPart())) {
            return GML3EncodingUtils.getID(object);
        }

        if ("name".equals(name.getLocalPart())) {
            return GML3EncodingUtils.getName(object);
        }
        
        if ("description".equals(name.getLocalPart())) {
            return GML3EncodingUtils.getDescription(object);
        }
        
        return null;
    }
}
