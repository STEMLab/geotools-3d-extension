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
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.primitive.Curve;


/**
 * Binding object for the type http://www.opengis.net/gml:MultiLineStringType.
 *
 * <p>
 *        <pre>
 *         <code>
 *  &lt;complexType name="MultiLineStringType"&gt;
 *      &lt;annotation&gt;
 *          &lt;documentation&gt;A MultiLineString is defined by one or more LineStrings, referenced through lineStringMember elements. Deprecated with GML version 3.0. Use MultiCurveType instead.&lt;/documentation&gt;
 *      &lt;/annotation&gt;
 *      &lt;complexContent&gt;
 *          &lt;extension base="gml:AbstractGeometricAggregateType"&gt;
 *              &lt;sequence&gt;
 *                  &lt;element maxOccurs="unbounded" minOccurs="0" ref="gml:lineStringMember"/&gt;
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
public class MultiLineStringTypeBinding extends AbstractComplexBinding {
    ISOGeometryBuilder gBuilder;

    public MultiLineStringTypeBinding(ISOGeometryBuilder gBuilder) {
        this.gBuilder = gBuilder;
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return GML.MultiLineStringType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Class getType() {
        return MultiCurve.class;
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
        List lines = node.getChildValues(Curve.class);

        /*return gBuilder.createMultiLineString((MultiCurve[]) lines.toArray(
                new LineString[lines.size()]));*/
        return null;
    }

    public Object getProperty(Object object, QName name)
        throws Exception {
        if (GML.lineStringMember.equals(name)) {
            /*MultiLineString multiLineString = (MultiLineString) object;
            LineString[] members = new LineString[multiLineString.getNumGeometries()];

            for (int i = 0; i < members.length; i++) {
                members[i] = (LineString) multiLineString.getGeometryN(i);
            }

            GML3EncodingUtils.setChildIDs(multiLineString);
            */
            return null;
        }

        return null;
    }
}
