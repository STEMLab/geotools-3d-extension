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

import org.geotools.geometry.GeometryBuilder;
import org.geotools.gml2.GML;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;

/**
 * 
 *
 * @source $URL$
 */
public class GMLLineStringMemberTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance association;
    ElementInstance geometry;

    protected void setUp() throws Exception {
        super.setUp();

        association = createElement(GML.NAMESPACE, "myAssociation", GML.LINESTRINGMEMBERTYPE, null);
        geometry = createElement(GML.NAMESPACE, "myGeometry", GML.LINESTRINGTYPE, null);
    }

    public void testWithGeometry() throws Exception {
    	GeometryBuilder builder = new GeometryBuilder(DefaultGeographicCRS.WGS84_3D);
    	
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {0, 0, 0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {1, 1, 1});
    	PointArray pa = builder.createPointArray();
    	pa.add(dp1);
    	pa.add(dp2);
    	
        Node node = createNode(association, new ElementInstance[] { geometry },
                new Object[] {
                	builder.createCurve(pa)
                }, null, null);
        GMLGeometryAssociationTypeBinding s1 = (GMLGeometryAssociationTypeBinding) getBinding(GML.GEOMETRYASSOCIATIONTYPE);
        Geometry g = (Geometry) s1.parse(association, node, null);

        GMLLineStringMemberTypeBinding s2 = (GMLLineStringMemberTypeBinding) getBinding(GML.LINESTRINGMEMBERTYPE);
        g = (Geometry) s2.parse(association, node, g);

        assertNotNull(g);
        assertTrue(g instanceof Curve);
    }
}
