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
import java.util.HashSet;

import org.geotools.geometry.GeometryBuilder;
import org.geotools.gml2.iso.GML;
import org.geotools.gml2.iso.bindings.GMLGeometryAssociationTypeBinding;
import org.geotools.gml2.iso.bindings.GMLMultiPolygonPropertyTypeBinding;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.junit.Before;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Surface;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * 
 *
 * @source $URL$
 */
public class GMLMultiPolygonPropertyTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance association;
    ElementInstance geometry;

    GeometryBuilder builder = new GeometryBuilder(DefaultGeographicCRS.WGS84_3D);
    @Before
    protected void setUp() throws Exception {
        super.setUp();

        association = createElement(GML.NAMESPACE, "myMultiPolygonProperty",
                GML.MULTIPOLYGONPROPERTYTYPE, null);
        geometry = createElement(GML.NAMESPACE, "myMultiPolygon", GML.MULTIPOINTTYPE, null);
        
        container = new DefaultPicoContainer();
        container.registerComponentInstance(builder);
        container.registerComponentImplementation(GMLGeometryAssociationTypeBinding.class);
        container.registerComponentImplementation(GMLMultiPolygonPropertyTypeBinding.class);
    }

    public void testWithGeometry() throws Exception {
        DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {3.0, 4.0, 4.0});
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {5.0, 2.0, 3.0});
    	PointArray pa1 = createPointArray(builder, new DirectPosition[] {dp1, dp2, dp3, dp1});
    	
    	DirectPosition dp4 = builder.createDirectPosition(new double[] {3.0, 5.0, 5.0});
    	DirectPosition dp5 = builder.createDirectPosition(new double[] {5.0, 6.0, 6.0});
    	DirectPosition dp6 = builder.createDirectPosition(new double[] {7.0, 4.0, 5.0});
    	PointArray pa2 = createPointArray(builder, new DirectPosition[] {dp4, dp5, dp6, dp4});
    	
    	Surface p1 = builder.createSurface(builder.createSurfaceBoundary(pa1));
    	Surface p2 = builder.createSurface(builder.createSurfaceBoundary(pa2));

        Node node = createNode(association, new ElementInstance[] { geometry },
                new Object[] { builder.createMultiSurface(new HashSet(Arrays.asList(p1, p2)))},
                null, null);

        GMLGeometryAssociationTypeBinding s = (GMLGeometryAssociationTypeBinding) container
            .getComponentInstanceOfType(GMLGeometryAssociationTypeBinding.class);

        GMLMultiPolygonPropertyTypeBinding s1 = (GMLMultiPolygonPropertyTypeBinding) container
            .getComponentInstanceOfType(GMLMultiPolygonPropertyTypeBinding.class);

        MultiSurface p = (MultiSurface) s1.parse(association, node, s.parse(association, node, null));
        assertNotNull(p);
    }
}
