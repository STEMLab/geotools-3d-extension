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
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Point;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * 
 *
 * @source $URL$
 */
public class GMLPointTypeBindingTest extends AbstractGMLBindingTest {
    MutablePicoContainer container;
    ElementInstance point;
    ElementInstance coord;
    ElementInstance coords;

    GeometryBuilder builder = new GeometryBuilder(DefaultGeographicCRS.WGS84_3D);
    protected void setUp() throws Exception {
        super.setUp();

        point = createElement(GML.NAMESPACE, "myPoint", GML.POINTTYPE, null);
        coord = createElement(GML.NAMESPACE, "coord", GML.COORDTYPE, null);
        coords = createElement(GML.NAMESPACE, "coordinates", GML.COORDINATESTYPE, null);

        container = new DefaultPicoContainer();
        container.registerComponentInstance(builder);
        container.registerComponentImplementation(GMLPointTypeBinding.class);
    }

    public void testParseCoordinate() throws Exception {
        Node node = createNode(point, new ElementInstance[] { coord },
                new Object[] { builder.createDirectPosition(new double[] {12.34, 56.78, 90.11}) }, null, null);

        GMLPointTypeBinding strategy = (GMLPointTypeBinding) container.getComponentInstanceOfType(GMLPointTypeBinding.class);

        Point p = (Point) strategy.parse(point, node, null);
        DirectPosition dp = p.getDirectPosition();
        assertNotNull(p);
        assertEquals(dp.getOrdinate(0), 12.34, 0d);
        assertEquals(dp.getOrdinate(1), 56.78, 0d);
        assertEquals(dp.getOrdinate(2), 90.11, 0d);
    }

    public void testParseCoordinates() throws Exception {
    	DirectPosition dp = builder.createDirectPosition(new double[] {12.34, 56.78, 90.11});
        Node node = createNode(point, new ElementInstance[] { coords },
                new Object[] { createPointArray(builder, dp) }, null, null);

        GMLPointTypeBinding strategy = (GMLPointTypeBinding) container.getComponentInstanceOfType(GMLPointTypeBinding.class);

        Point p = (Point) strategy.parse(point, node, null);
        dp = p.getDirectPosition();
        assertNotNull(p);
        assertEquals(dp.getOrdinate(0), 12.34, 0d);
        assertEquals(dp.getOrdinate(1), 56.78, 0d);
        assertEquals(dp.getOrdinate(2), 90.11, 0d);
    }

    public void testParseMultiCoordinates() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {12.34, 56.78, 90.11});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {12.13, 14.15, 16.17});
        Node node = createNode(point, new ElementInstance[] { coords },
                new Object[] {
                		createPointArray(
                				builder, new DirectPosition[] { dp1, dp2 })
                }, null, null);

        GMLPointTypeBinding strategy = (GMLPointTypeBinding) container.getComponentInstanceOfType(GMLPointTypeBinding.class);

        try {
            Point p = (Point) strategy.parse(point, node, null);
            fail("Should have thrown an exception");
        } catch (RuntimeException e) {
            //ok
        }
    }
}
