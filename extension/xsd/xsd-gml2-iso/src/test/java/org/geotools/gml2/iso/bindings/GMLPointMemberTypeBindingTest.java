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

import org.geotools.geometry.ISOGeometryBuilder;
import org.geotools.gml2.iso.GML;
import org.geotools.gml2.iso.bindings.GMLGeometryAssociationTypeBinding;
import org.geotools.gml2.iso.bindings.GMLPointMemberTypeBinding;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.primitive.Point;
/**
 * 
 *
 * @source $URL$
 */
public class GMLPointMemberTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance association;
    ElementInstance geometry;

    protected void setUp() throws Exception {
        super.setUp();

        association = createElement(GML.NAMESPACE, "myAssociation", GML.POINTMEMBERTYPE, null);
        geometry = createElement(GML.NAMESPACE, "myGeometry", GML.POINTTYPE, null);
    }

    public void testWithGeometry() throws Exception {
    	ISOGeometryBuilder builder = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84_3D);
        Node node = createNode(association, new ElementInstance[] { geometry },
                new Object[] { builder.createPoint(new double[] {0, 0, 0}) }, null, null);
        GMLGeometryAssociationTypeBinding s1 = (GMLGeometryAssociationTypeBinding) getBinding(GML.GEOMETRYASSOCIATIONTYPE);
        Geometry g = (Geometry) s1.parse(association, node, null);

        GMLPointMemberTypeBinding s2 = (GMLPointMemberTypeBinding) getBinding(GML.POINTMEMBERTYPE);
        g = (Geometry) s2.parse(association, node, g);

        assertNotNull(g);
        assertTrue(g instanceof Point);
    }
}
