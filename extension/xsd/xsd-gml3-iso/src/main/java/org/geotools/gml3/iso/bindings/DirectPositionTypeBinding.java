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

import javax.xml.namespace.QName;

import org.geotools.gml3.iso.GML;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.ISOGeometryBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Binding object for the type http://www.opengis.net/gml:DirectPositionType.
 *
 * <p>
 *        <pre>
 *         <code>
 *  &lt;complexType name="DirectPositionType"&gt;
 *      &lt;annotation&gt;
 *          &lt;documentation&gt;DirectPosition instances hold the coordinates for a position within some coordinate reference system (CRS). Since
 *                          DirectPositions, as data types, will often be included in larger objects (such as geometry elements) that have references to CRS, the
 *                          "srsName" attribute will in general be missing, if this particular DirectPosition is included in a larger element with such a reference to a
 *                          CRS. In this case, the CRS is implicitly assumed to take on the value of the containing object's CRS.&lt;/documentation&gt;
 *      &lt;/annotation&gt;
 *      &lt;simpleContent&gt;
 *          &lt;extension base="gml:doubleList"&gt;
 *              &lt;attributeGroup ref="gml:SRSReferenceGroup"/&gt;
 *          &lt;/extension&gt;
 *      &lt;/simpleContent&gt;
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
public class DirectPositionTypeBinding extends AbstractComplexBinding {
	ISOGeometryBuilder gBuilder;

    public DirectPositionTypeBinding(ISOGeometryBuilder gBuilder) {
        this.gBuilder = gBuilder;
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return GML.DirectPositionType;
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
        return DirectPosition.class;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Object parse(ElementInstance instance, Node node, Object value)
        throws Exception {
        double[] position = (double[]) value;
        
        DirectPosition dp = gBuilder.createDirectPosition(position);
        return dp;
    }

    public Element encode(Object object, Document document, Element value)
        throws Exception {
    	
    	//TODO
        /*CoordinateSequence cs = (CoordinateSequence) object;

        StringBuffer sb = new StringBuffer();

        // assume either zero or one coordinate
        if (cs.size() >= 1) {
            int dim = cs.getDimension();
            for (int d = 0; d < dim; d++) {
                double v = cs.getOrdinate(0, d);
                if (Double.isNaN(v) && d > 1) {
                    continue;
                }

                // separator char is a blank
                sb.append(String.valueOf(v)).append(" ");
            }
            if (dim > 0) {
                sb.setLength(sb.length()-1);
            }
        }

        value.appendChild(document.createTextNode(sb.toString()));
*/
        return value;
    }
}
