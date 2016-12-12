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

import java.net.URI;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.geotools.geometry.GeometryBuilder;
import org.geotools.gml2.GML;
import org.geotools.referencing.CRS;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Binding object for the type http://www.opengis.net/gml:AbstractGeometryType.
 *
 * <p>
 *        <pre>
 *         <code>
 *  &lt;complexType name="AbstractGeometryType" abstract="true"&gt;
 *      &lt;annotation&gt;
 *          &lt;documentation&gt;         All geometry elements are derived from
 *              this abstract supertype;          a geometry element may
 *              have an identifying attribute (gid).          It may be
 *              associated with a spatial reference system.       &lt;/documentation&gt;
 *      &lt;/annotation&gt;
 *      &lt;complexContent&gt;
 *          &lt;restriction base="anyType"&gt;
 *              &lt;attribute name="gid" type="ID" use="optional"/&gt;
 *              &lt;attribute name="srsName" type="anyURI" use="optional"/&gt;
 *          &lt;/restriction&gt;
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
public class GMLAbstractGeometryTypeBinding extends AbstractComplexBinding {
    Logger logger;

    GeometryBuilder builder;
    public GMLAbstractGeometryTypeBinding(Logger logger, GeometryBuilder builder) {
        this.logger = logger;
        this.builder = builder;
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return GML.ABSTRACTGEOMETRYTYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public int getExecutionMode() {
        return OVERRIDE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Class getType() {
        return Geometry.class;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Object parse(ElementInstance instance, Node node, Object value)
        throws Exception {
        if (node.hasAttribute("srsName")) {
            URI srs = (URI) node.getAttributeValue("srsName");
            CoordinateReferenceSystem crs = CRS.decode(srs.toString());
            
            if (crs != null) {
                if(!builder.getCoordinateReferenceSystem().equals(crs)) {
                	builder.setCoordinateReferenceSystem(crs);
                }
            } else {
                logger.warning("Could not create Coordinate Reference System for " + srs);
            }
            return crs;
        }
        return null;
    }

    public Object getProperty(Object object, QName name)
        throws Exception {
        if ("srsName".equals(name.getLocalPart())) {
        	Geometry geometry = (Geometry) object;
        	CoordinateReferenceSystem crs = geometry.getCoordinateReferenceSystem();
        	return GML2EncodingUtils.toURI(crs);
        }

        return null;
    }
}
