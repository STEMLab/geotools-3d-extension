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
package org.geotools.gml3.iso.bindings;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.gml3.iso.GML;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.complex.CompositeSurface;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Surface;

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
	protected ISOGeometryBuilder gBuilder;

    public CompositeSurfaceTypeBinding(ISOGeometryBuilder gBuilder) {
        this.gBuilder = gBuilder;
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
        return Surface.class;
    }

    public int getExecutionMode() {
        return AFTER;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Object parse(ElementInstance instance, Node node, Object value)
        throws Exception {
        
        List multiPolygons = node.getChildValues(Surface.class);
        List<OrientableSurface> surfaces = new ArrayList<OrientableSurface>();
        
        for (Object object : multiPolygons) {
            Surface surface = (Surface) object;
            surfaces.add(surface);
        }        
        
        return gBuilder.createCompositeSurface(surfaces);
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
        if (o instanceof org.geotools.gml3.iso.bindings.ext.SurfaceTypeBinding) {
            return -1;
        } else {
            return 0;
        }
    }    
}
