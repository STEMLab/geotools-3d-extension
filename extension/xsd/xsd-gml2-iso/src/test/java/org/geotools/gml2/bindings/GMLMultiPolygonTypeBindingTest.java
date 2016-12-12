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
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.coordinate.PointArray;
import org.picocontainer.defaults.DefaultPicoContainer;
/**
 * 
 *
 * @source $URL$
 */
public class GMLMultiPolygonTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance mp;
    ElementInstance poly1;
    ElementInstance poly2;

    GeometryBuilder builder = new GeometryBuilder(DefaultGeographicCRS.WGS84_3D);
    protected void setUp() throws Exception {
        super.setUp();

        poly1 = createElement(GML.NAMESPACE, "myPoly", GML.POLYGONMEMBERTYPE, null);
        poly2 = createElement(GML.NAMESPACE, "myPoly", GML.POLYGONMEMBERTYPE, null);
        mp = createElement(GML.NAMESPACE, "myPoly", GML.MULTIPOLYGONTYPE, null);

        container = new DefaultPicoContainer();
        container.registerComponentInstance(builder);
        container.registerComponentImplementation(GMLGeometryCollectionTypeBinding.class);
        container.registerComponentImplementation(GMLMultiPolygonTypeBinding.class);
    }

    public void test() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {3.0, 4.0, 4.0});
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {5.0, 2.0, 3.0});
    	PointArray pa1 = createPointArray(builder, new DirectPosition[] {dp1, dp2, dp3});
    	
    	DirectPosition dp4 = builder.createDirectPosition(new double[] {3.0, 5.0, 5.0});
    	DirectPosition dp5 = builder.createDirectPosition(new double[] {5.0, 6.0, 6.0});
    	DirectPosition dp6 = builder.createDirectPosition(new double[] {7.0, 4.0, 5.0});
    	PointArray pa2 = createPointArray(builder, new DirectPosition[] {dp4, dp5, dp6});
    	
        Node node = createNode(mp, new ElementInstance[] { poly1, poly2 },
                new Object[] {
                		builder.createSurface(builder.createSurfaceBoundary(pa1)),
                		builder.createSurface(builder.createSurfaceBoundary(pa2))
                }, null, null);

        GMLGeometryCollectionTypeBinding s1 = (GMLGeometryCollectionTypeBinding) container
            .getComponentInstanceOfType(GMLGeometryCollectionTypeBinding.class);
        GMLMultiPolygonTypeBinding s2 = (GMLMultiPolygonTypeBinding) container
            .getComponentInstanceOfType(GMLMultiPolygonTypeBinding.class);

        MultiSurface mpoly = (MultiSurface) s2.parse(mp, node, s1.parse(mp, node, null));

        assertNotNull(mpoly);
        assertEquals(mpoly.getElements().size(), 2);
    }
}
