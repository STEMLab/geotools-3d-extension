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

/**
 * @author Donguk Seo
 *
 */
package org.geotools.gml3.bindings;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.gml3.GML;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;


/**
 * Binding object for the type http://www.opengis.net/gml:CompositeSurfaceType.
 *
 * @author Donguk Seo
 *
 * @source $URL$
 */
public class CompositeSurfaceTypeBinding extends AbstractComplexBinding implements Comparable {
    protected GeometryFactory gf;

    public CompositeSurfaceTypeBinding(GeometryFactory gf) {
        this.gf = gf;
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return GML.CompositeSurfaceType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Class getType() {
        return MultiPolygon.class;
    }

    public int getExecutionMode() {
        return BEFORE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Object parse(ElementInstance instance, Node node, Object value)
        throws Exception {
        
        List multiPolygons = node.getChildValues(MultiPolygon.class);
        List surfaces = new ArrayList<Polygon>();
        
        for (Object object : multiPolygons) {
            MultiPolygon multiPolygon = (MultiPolygon) object;
            
            for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                surfaces.add((Polygon) multiPolygon.getGeometryN(i));
            }
        }        
                
        return gf.createMultiPolygon((Polygon[]) surfaces.toArray(new Polygon[surfaces.size()]));
    }

    public Object getProperty(Object object, QName name)
        throws Exception {
        if ("surfaceMember".equals(name.getLocalPart())) {
            /**
             *  If Binding class is in substitution group, Encoder class calls getProperty() in superior Binding class(SurfaceTypeBinding).
             *  To use this method, implementing compareTo method of Comparable interface is necessary.
             */
            MultiPolygon multiSurface = (MultiPolygon) object;
            Polygon[] members = new Polygon[multiSurface.getNumGeometries()];

            for (int i = 0; i < members.length; i++) {
                members[i] = (Polygon) multiSurface.getGeometryN(i);
            }

            //GML3EncodingUtils.setChildIDs(multiSurface);

            return members;
        }
        
        return null;
    }
    
    public int compareTo(Object o) {
        if (o instanceof org.geotools.gml3.bindings.ext.SurfaceTypeBinding) {
            return -1;
        } else {
            return 0;
        }
    }    
}
