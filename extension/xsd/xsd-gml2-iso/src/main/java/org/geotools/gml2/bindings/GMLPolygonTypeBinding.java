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
package org.geotools.gml2.bindings;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.geometry.GeometryBuilder;
import org.geotools.gml2.GML;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;


/**
 * Binding object for the type http://www.opengis.net/gml:PolygonType.
 *
 * <p>
 *        <pre>
 *         <code>
 *  &lt;complexType name="PolygonType"&gt;
 *      &lt;annotation&gt;
 *          &lt;documentation&gt;         A Polygon is defined by an outer
 *              boundary and zero or more inner          boundaries which
 *              are in turn defined by LinearRings.       &lt;/documentation&gt;
 *      &lt;/annotation&gt;
 *      &lt;complexContent&gt;
 *          &lt;extension base="gml:AbstractGeometryType"&gt;
 *              &lt;sequence&gt;
 *                  &lt;element ref="gml:outerBoundaryIs"/&gt;
 *                  &lt;element ref="gml:innerBoundaryIs" minOccurs="0" maxOccurs="unbounded"/&gt;
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
 *
 *
 *
 * @source $URL$
 */
public class GMLPolygonTypeBinding extends AbstractComplexBinding {
    GeometryBuilder gBuilder;

    public GMLPolygonTypeBinding(GeometryBuilder gBuilder) {
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
        Ring shell = (Ring) node.getChild("outerBoundaryIs").getValue();

        List innerRings = node.getChildren("innerBoundaryIs");
        List holes = new ArrayList();

        for (int i = 0; i < innerRings.size(); i++) {
            Node inode = (Node) innerRings.get(i);
            holes.add(inode.getValue());
        }
        
        SurfaceBoundary sb = gBuilder.createSurfaceBoundary(shell, holes);
        return gBuilder.createSurface(sb);
    }

    public Object getProperty(Object object, QName name)
        throws Exception {
        Surface polygon = (Surface) object;

        SurfaceBoundary sb = polygon.getBoundary();
        if (GML.outerBoundaryIs.equals(name)) {
            return sb.getExterior();
        }

        if (GML.innerBoundaryIs.equals(name)) {
        	return sb.getInteriors();
        }

        return null;
    }
}
