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

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.geotools.gml3.iso.GML;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.Point;


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
    	
    	Set lines = new HashSet();
    	if(node.hasChild(Curve.class)){
    		lines.addAll(node.getChildValues(Curve.class));
    	}
    	if(node.hasChild(Curve[].class)){
    		Curve[] l = (Curve[])node.getChildValue(Curve[].class);
    		for(int i = 0; i < l.length; i++){
    			lines.add(l[i]);
    		}
    	}
    	
    	
        return gBuilder.createMultiCurve(lines);
        
    }

    public Object getProperty(Object object, QName name)
        throws Exception {
    	
    	
        if (GML.lineStringMember.equals(name.getLocalPart())) {
        	
        	MultiCurve multiLine = (MultiCurve) object;
        	Curve[] members = new Curve[multiLine.getElements().size()];
        	int i = 0;
            for (OrientableCurve line : multiLine.getElements()) {
                members[i++] = (Curve)line;
            }
            
            GML3EncodingUtils.setChildIDs(multiLine);

            return members;
            
        }
        

        return null;
    }
}
