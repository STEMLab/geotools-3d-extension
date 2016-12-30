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
package org.geotools.gml3.v3_2.bindings;

import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.gml3.XSDIdRegistry;
import org.geotools.gml3.bindings.GML3EncodingUtils;
import org.geotools.gml3.bindings.GeometryPropertyTypeBinding;
import org.geotools.gml3.v3_2.GML;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.primitive.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 * @author Donguk
 *
 */
public class ShellPropertyTypeBinding extends GeometryPropertyTypeBinding {
    
    GeometryFactory gf;
    
    public ShellPropertyTypeBinding(GML3EncodingUtils encodingUtils, XSDIdRegistry idRegistry, GeometryFactory gf) {
        super(encodingUtils, idRegistry);
        this.gf = gf;
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return GML.ShellPropertyType;
    }
    
    @Override
    public int getExecutionMode() {
        return OVERRIDE;
    }

    public Class getGeometryType() {
        return Shell.class;
    }

    @Override
    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {
        Shell shell = (Shell)node.getChildValue(Shell.class);
        
        return shell;
    }
    
    @Override
    public Element encode(Object object, Document document, Element value) throws Exception {
        return value;
    }
    
    @Override
    public Object getProperty(Object object, QName name) throws Exception {
        if ("Shell".equals(name.getLocalPart())) {
            return (Shell) object;
        }

        return null;
    }

    @Override
    /**
     * A ISOGeometry shell can't have properties.
     */
    public List getProperties(Object object) throws Exception {
        return null;
    }    
    
}
