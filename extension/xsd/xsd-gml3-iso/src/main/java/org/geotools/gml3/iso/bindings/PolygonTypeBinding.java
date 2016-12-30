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
package org.geotools.gml3.iso.bindings;

import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.gml3.iso.GML;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;


/**
 * Binding object for the type http://www.opengis.net/gml:PolygonType.
 *
 * <p>
 *        <pre>
 *         <code>
 *  &lt;complexType name="PolygonType"&gt;
 *      &lt;annotation&gt;
 *          &lt;documentation&gt;A Polygon is a special surface that is defined by a single surface patch. The boundary of this patch is coplanar and the polygon uses planar interpolation in its interior. It is backwards compatible with the Polygon of GML 2, GM_Polygon of ISO 19107 is implemented by PolygonPatch.&lt;/documentation&gt;
 *      &lt;/annotation&gt;
 *      &lt;complexContent&gt;
 *          &lt;extension base="gml:AbstractSurfaceType"&gt;
 *              &lt;sequence&gt;
 *                  &lt;element minOccurs="0" ref="gml:exterior"/&gt;
 *                  &lt;element maxOccurs="unbounded" minOccurs="0" ref="gml:interior"/&gt;
 *              &lt;/sequence&gt;
 *          &lt;/extension&gt;
 *      &lt;/complexContent&gt;
 *  &lt;/complexType&gt;
 *
 *          </code>
 *         </pre>
 * </p>
 *
 * @generated
 * @author Hyung-Gyu Ryoo, Pusan National University
 *
 *
 * @source $URL$
 */
public class PolygonTypeBinding extends AbstractComplexBinding {
    ISOGeometryBuilder gBuilder;

    public PolygonTypeBinding(ISOGeometryBuilder gBuilder) {
        this.gBuilder = gBuilder;
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return GML.PolygonType;
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
    public Class getType() {
        return Surface.class;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Object parse(ElementInstance instance, Node node, Object value)
        throws Exception {
        //TODO: schema allows no exterior ring, but what the heck is that all about ?
        Ring exterior = (Ring) node.getChildValue("exterior");
        List<Ring> interiors = null;

        if (node.hasChild("interior")) {
            List list = node.getChildValues("interior");
            interiors = list;
        }

        SurfaceBoundary sb = gBuilder.createSurfaceBoundary(exterior, interiors);
        return gBuilder.createSurface(sb);
    }

    public Object getProperty(Object object, QName name)
        throws Exception {
    	Surface polygon = (Surface) object;
    	SurfaceBoundary sb = polygon.getBoundary();
    	
        if ("exterior".equals(name.getLocalPart())) {
            return sb.getExterior();
        }

        if ("interior".equals(name.getLocalPart())) {
        	return sb.getInteriors();
            /*int n = polygon.getNumInteriorRing();

            if (n > 0) {
                LineString[] interior = new LineString[n];

                for (int i = 0; i < n; i++) {
                    interior[i] = polygon.getInteriorRingN(i);
                }

                return interior;
            }*/
        }

        return null;
    }
}
