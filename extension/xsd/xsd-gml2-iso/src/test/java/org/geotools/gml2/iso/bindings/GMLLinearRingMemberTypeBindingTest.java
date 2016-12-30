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

import java.util.Arrays;

import org.geotools.gml2.iso.GML;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Ring;
/**
 * 
 *
 * @source $URL$
 */
public class GMLLinearRingMemberTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance association;
    ElementInstance geometry;

    protected void setUp() throws Exception {
        super.setUp();

        association = createElement(GML.NAMESPACE, "myAssociation", GML.LINEARRINGMEMBERTYPE, null);
        geometry = createElement(GML.NAMESPACE, "myGeometry", GML.LINEARRINGTYPE, null);
    }

    public void testWithGeometry() throws Exception {
        ISOGeometryBuilder builder = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84_3D);
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {3.0, 4.0, 4.0});
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {5.0, 2.0, 3.0});
    	PointArray pa = createPointArray(builder, new DirectPosition[] {dp1, dp2, dp3, dp1});
    	
        Node node = createNode(association, new ElementInstance[] { geometry },
                new Object[] {
                	builder.createRing(Arrays.asList(builder.createCurve(pa)))
                }, null, null);
        GMLGeometryAssociationTypeBinding s1 = (GMLGeometryAssociationTypeBinding) getBinding(GML.GEOMETRYASSOCIATIONTYPE);
        Geometry g = (Geometry) s1.parse(association, node, null);

        GMLLinearRingMemberTypeBinding s2 = (GMLLinearRingMemberTypeBinding) getBinding(GML.LINEARRINGMEMBERTYPE);
        g = (Geometry) s2.parse(association, node, g);

        assertNotNull(g);
        assertTrue(g instanceof Ring);
    }
}
