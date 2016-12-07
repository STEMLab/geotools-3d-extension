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

import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.geometry.GeometryBuilder;
import org.geotools.gml2.GML;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;


/**
 * Binding object for the type http://www.opengis.net/gml:LineStringType.
 *
 * <p>
 *        <pre>
 *         <code>
 *  &lt;complexType name="LineStringType"&gt;
 *      &lt;annotation&gt;
 *          &lt;documentation&gt;         A LineString is defined by two or more
 *              coordinate tuples, with          linear interpolation
 *              between them.        &lt;/documentation&gt;
 *      &lt;/annotation&gt;
 *      &lt;complexContent&gt;
 *          &lt;extension base="gml:AbstractGeometryType"&gt;
 *              &lt;sequence&gt;
 *                  &lt;choice&gt;
 *                      &lt;element ref="gml:coord" minOccurs="2" maxOccurs="unbounded"/&gt;
 *                      &lt;element ref="gml:coordinates"/&gt;
 *                  &lt;/choice&gt;
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
public class GMLLineStringTypeBinding extends AbstractComplexBinding {
    GeometryBuilder gBuilder;

    public GMLLineStringTypeBinding(GeometryBuilder gBuilder) {
        this.gBuilder = gBuilder;
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return GML.LineStringType;
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
        return Curve.class;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Object parse(ElementInstance instance, Node node, Object value)
        throws Exception {
        List coordinates = node.getChildren("coord");

        if (coordinates.size() == 1) {
            throw new RuntimeException("Linestring must have at least 2 coordinates");
        }

        if (!coordinates.isEmpty()) {
            PointArray pArr = gBuilder.createPointArray();
            for (int i = 0; i < coordinates.size(); i++) {
                Node cnode = (Node) coordinates.get(i);
                DirectPosition dp = (DirectPosition) cnode.getValue();
                pArr.add(dp);
            }

            return gBuilder.createCurve(pArr);
        }

        if (node.getChild("coordinates") != null) {
            Node cnode = (Node) node.getChild("coordinates");
            PointArray pArr = (PointArray) cnode.getValue();
            
            if(pArr.size() == 1) {
            	throw new RuntimeException("Linestring must have at least 2 coordinates");
            }
            
            return gBuilder.createCurve(pArr);
        }

        throw new RuntimeException("Could not find coordinates to build linestring");
    }

    public Object getProperty(Object object, QName name)
        throws Exception {
    	Curve lineString = (Curve) object;

        if (GML.coordinates.equals(name)) {
        	PointArray pa = gBuilder.createPointArray();
        	List<? extends CurveSegment> segments = lineString.getSegments();
        	
        	for(CurveSegment cs : segments) {
        		pa.add(cs.getStartPoint());
        	}
        	pa.add( segments.get(segments.size() - 1).getEndPoint() );
        	return pa;
        }

        return null;
    }
}
