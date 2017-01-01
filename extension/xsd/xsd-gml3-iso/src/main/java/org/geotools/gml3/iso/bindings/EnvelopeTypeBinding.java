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

import org.geotools.geometry.jts.LiteCoordinateSequence;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope3D;
import org.geotools.gml2.SrsSyntax;
import org.geotools.gml3.iso.GML;
import org.geotools.gml3.iso.GMLConfiguration_ISO;
import org.geotools.gml3.iso.bindings.GML3ParsingUtils;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.Configuration;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Binding object for the type http://www.opengis.net/gml:EnvelopeType.
 *
 * <p>
 *        <pre>
 *         <code>
 *  &lt;complexType name="EnvelopeType"&gt;
 *      &lt;annotation&gt;
 *          &lt;documentation&gt;Envelope defines an extent using a pair of positions defining opposite corners in arbitrary dimensions. The first direct
 *                          position is the "lower corner" (a coordinate position consisting of all the minimal ordinates for each dimension for all points within the envelope),
 *                          the second one the "upper corner" (a coordinate position consisting of all the maximal ordinates for each dimension for all points within the
 *                          envelope).&lt;/documentation&gt;
 *      &lt;/annotation&gt;
 *      &lt;choice&gt;
 *          &lt;sequence&gt;
 *              &lt;element name="lowerCorner" type="gml:DirectPositionType"/&gt;
 *              &lt;element name="upperCorner" type="gml:DirectPositionType"/&gt;
 *          &lt;/sequence&gt;
 *          &lt;element maxOccurs="2" minOccurs="2" ref="gml:coord"&gt;
 *              &lt;annotation&gt;
 *                  &lt;appinfo&gt;deprecated&lt;/appinfo&gt;
 *                  &lt;documentation&gt;deprecated with GML version 3.0&lt;/documentation&gt;
 *              &lt;/annotation&gt;
 *          &lt;/element&gt;
 *          &lt;element maxOccurs="2" minOccurs="2" ref="gml:pos"&gt;
 *              &lt;annotation&gt;
 *                  &lt;appinfo&gt;deprecated&lt;/appinfo&gt;
 *                  &lt;documentation&gt;Deprecated with GML version 3.1. Use the explicit properties "lowerCorner" and "upperCorner" instead.&lt;/documentation&gt;
 *              &lt;/annotation&gt;
 *          &lt;/element&gt;
 *          &lt;element ref="gml:coordinates"&gt;
 *              &lt;annotation&gt;
 *                  &lt;documentation&gt;Deprecated with GML version 3.1.0. Use the explicit properties "lowerCorner" and "upperCorner" instead.&lt;/documentation&gt;
 *              &lt;/annotation&gt;
 *          &lt;/element&gt;
 *      &lt;/choice&gt;
 *      &lt;attributeGroup ref="gml:SRSReferenceGroup"/&gt;
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
public class EnvelopeTypeBinding extends AbstractComplexBinding {
	ISOGeometryBuilder gb;
    Configuration config;
    SrsSyntax srsSyntax;

    public EnvelopeTypeBinding(ISOGeometryBuilder gb, Configuration config, SrsSyntax srsSyntax) {
    	this.gb = gb;
        this.config = config;
        this.srsSyntax = srsSyntax;
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return GML.EnvelopeType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Class getType() {
        return Envelope.class;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Object parse(ElementInstance instance, Node node, Object value)
        throws Exception {
        CoordinateReferenceSystem crs = GML3ParsingUtils.crs(node);

        if (node.getChild("lowerCorner") != null) {
            DirectPosition l = (DirectPosition) node.getChildValue("lowerCorner");
            DirectPosition u = (DirectPosition) node.getChildValue("upperCorner");

            return gb.createEnvelope(l, u);
        }

        if (node.hasChild(DirectPosition.class)) {
            List dp = node.getChildValues(DirectPosition.class);
            DirectPosition dp1 = (DirectPosition) dp.get(0);
            DirectPosition dp2 = (DirectPosition) dp.get(1);

            return gb.createEnvelope(dp1, dp2);
        }

        if (node.hasChild(PointArray.class)) {
        	PointArray pa = (PointArray) node.getChildValue(PointArray.class);
        	DirectPosition dp1 = (DirectPosition) pa.get(0);
            DirectPosition dp2 = (DirectPosition) pa.get(1);
            
            return gb.createEnvelope(dp1, dp2);
        }

        return null;
    }

    public Element encode(Object object, Document document, Element value)
        throws Exception {
        Envelope envelope = (Envelope) object;

        if (envelope == null) {
            value.appendChild(document.createElementNS(getTarget().getNamespaceURI(), GML.Null.getLocalPart()));
        }

        return null;
    }

    public Object getProperty(Object object, QName name) {
        Envelope envelope = (Envelope) object;

        if (envelope == null) {
            return null;
        }

        if (name.getLocalPart().equals("lowerCorner")) {
        	return envelope.getLowerCorner();
        }

        if (name.getLocalPart().equals("upperCorner")) {
        	return envelope.getUpperCorner();
        }

        /*if (envelope instanceof ReferencedEnvelope) {
            String localName = name.getLocalPart();
            if (localName.equals("srsName")) {
                return GML3EncodingUtils.toURI(((ReferencedEnvelope) envelope)
                        .getCoordinateReferenceSystem(), srsSyntax);
            } else if (localName.equals("srsDimension")) {
                //check if srsDimension is turned off
                if (config.hasProperty(GMLConfiguration_ISO.NO_SRS_DIMENSION)) {
                    return null;
                }

                CoordinateReferenceSystem crs = ((ReferencedEnvelope) envelope)
                        .getCoordinateReferenceSystem();
                if (crs != null) {
                    return crs.getCoordinateSystem().getDimension();
                }
            }
        }*/

        return null;
    }
}
