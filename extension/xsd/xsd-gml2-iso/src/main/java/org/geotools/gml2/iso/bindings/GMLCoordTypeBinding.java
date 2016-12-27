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
package org.geotools.gml2.iso.bindings;

import java.math.BigDecimal;

import javax.xml.namespace.QName;

import org.geotools.geometry.GeometryBuilder;
import org.geotools.gml2.iso.GML;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.DirectPosition;


/**
 * Binding object for the type http://www.opengis.net/gml:CoordType.
 *
 * <p>
 *        <pre>
 *         <code>
 *  &lt;complexType name="CoordType"&gt;
 *      &lt;annotation&gt;
 *          &lt;documentation&gt;         Represents a coordinate tuple in one,
 *              two, or three dimensions.       &lt;/documentation&gt;
 *      &lt;/annotation&gt;
 *      &lt;sequence&gt;
 *          &lt;element name="X" type="decimal"/&gt;
 *          &lt;element name="Y" type="decimal" minOccurs="0"/&gt;
 *          &lt;element name="Z" type="decimal" minOccurs="0"/&gt;
 *      &lt;/sequence&gt;
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
public class GMLCoordTypeBinding extends AbstractComplexBinding {
    GeometryBuilder gBuilder;

    public GMLCoordTypeBinding(GeometryBuilder gBuilder) {
        this.gBuilder = gBuilder;
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return GML.CoordType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Class getType() {
        return DirectPosition.class;
    }

    /**
     * <!-- begin-user-doc -->
     * Returns a coordinate sequence with a single coordinate in it.
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Object parse(ElementInstance instance, Node node, Object value)
        throws Exception {
        int dimension = 1;
        double x;
        double y;
        double z;
        x = y = z = Double.NaN;

        x = ((BigDecimal) node.getChild("X").getValue()).doubleValue();
        
        
        if (!node.getChildren("Y").isEmpty()) {
            dimension++;
            y = ((BigDecimal) node.getChild("Y").getValue()).doubleValue();
        }

        DirectPosition p = null;
        if (!node.getChildren("Z").isEmpty()) {
            dimension++;
            z = ((BigDecimal) node.getChild("Z").getValue()).doubleValue();
        }
        p = gBuilder.createDirectPosition(new double[] {x, y, z});
        
        return p;
    }

    public Object getProperty(Object object, QName name)
        throws Exception {
    	DirectPosition c = (DirectPosition) object;

        if ("X".equals(name.getLocalPart())) {
            return new Double(c.getOrdinate(0));
        }

        if ("Y".equals(name.getLocalPart())) {
            return new Double(c.getOrdinate(1));
        }

        if ("Z".equals(name.getLocalPart()) && !new Double(c.getOrdinate(2)).isNaN()) {
            return new Double(c.getOrdinate(2));
        }

        return null;
    }
}
