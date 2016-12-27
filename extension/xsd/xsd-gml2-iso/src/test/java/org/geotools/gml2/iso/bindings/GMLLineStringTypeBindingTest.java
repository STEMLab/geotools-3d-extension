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

import org.geotools.geometry.GeometryBuilder;
import org.geotools.gml2.iso.GML;
import org.geotools.gml2.iso.bindings.GMLLineStringTypeBinding;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.primitive.Curve;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * 
 *
 * @source $URL$
 */
public class GMLLineStringTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance line;
    ElementInstance coord1;
    ElementInstance coord2;
    ElementInstance coord3;
    ElementInstance coords;
    MutablePicoContainer container;

    GeometryBuilder builder = new GeometryBuilder(DefaultGeographicCRS.WGS84_3D);
    protected void setUp() throws Exception {
        super.setUp();

        line = createElement(GML.NAMESPACE, "myLineString", GML.LINESTRINGTYPE, null);
        coord1 = createElement(GML.NAMESPACE, "coord", GML.COORDTYPE, null);
        coord2 = createElement(GML.NAMESPACE, "coord", GML.COORDTYPE, null);
        coord3 = createElement(GML.NAMESPACE, "coord", GML.COORDTYPE, null);

        coords = createElement(GML.NAMESPACE, "coordinates", GML.COORDINATESTYPE, null);

        container = new DefaultPicoContainer();
        container.registerComponentInstance(builder);
        container.registerComponentImplementation(GMLLineStringTypeBinding.class);
    }

    public void testCoordTwo() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {4.0, 5.0, 6.0});
        Node node = createNode(line, new ElementInstance[] { coord1, coord2 },
                new Object[] {
                		dp1,
                		dp2
                }, null, null);

        GMLLineStringTypeBinding s = (GMLLineStringTypeBinding) container.getComponentInstanceOfType(GMLLineStringTypeBinding.class);
        Curve lineString = (Curve) s.parse(line, node, null);

        assertNotNull(lineString);
        //TODO
        /*assertEquals(lineString.getNumPoints(), 2);
        assertEquals(lineString.getPointN(0).getX(), 1d, 0);
        assertEquals(lineString.getPointN(0).getY(), 2d, 0);
        assertEquals(lineString.getPointN(1).getX(), 3d, 0);
        assertEquals(lineString.getPointN(1).getY(), 4d, 0);
        */    
    }

    public void testCoordSingle() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
        Node node = createNode(line, new ElementInstance[] { coord1 },
                new Object[] { dp1 }, null, null);

        GMLLineStringTypeBinding s = (GMLLineStringTypeBinding) container.getComponentInstanceOfType(GMLLineStringTypeBinding.class);

        try {
            Curve lineString = (Curve) s.parse(line, node, null);
            fail("Should have died with just one coordinate");
        } catch (RuntimeException e) {
            //ok
        }
    }

    public void testCoordMulti() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {4.0, 5.0, 6.0});
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {7.0, 8.0, 9.0});
        Node node = createNode(line, new ElementInstance[] { coord1, coord2, coord3 },
                new Object[] {
                		dp1,
                		dp2,
                		dp3
                }, null, null);

        GMLLineStringTypeBinding s = (GMLLineStringTypeBinding) container.getComponentInstanceOfType(GMLLineStringTypeBinding.class);
        Curve lineString = (Curve) s.parse(line, node, null);

        assertNotNull(lineString);
        //TODO
        /*
        assertEquals(lineString.getNumPoints(), 3);
        assertEquals(lineString.getPointN(0).getX(), 1d, 0);
        assertEquals(lineString.getPointN(0).getY(), 2d, 0);
        assertEquals(lineString.getPointN(1).getX(), 3d, 0);
        assertEquals(lineString.getPointN(1).getY(), 4d, 0);
        assertEquals(lineString.getPointN(2).getX(), 5d, 0);
        assertEquals(lineString.getPointN(2).getY(), 6d, 0);
        */
    }

    public void testCoordinatesTwo() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {4.0, 5.0, 6.0});
        Node node = createNode(line, new ElementInstance[] { coords },
                new Object[] {
                		createPointArray(
                				builder, new DirectPosition[] { dp1, dp2 }),
                }, null, null);

        GMLLineStringTypeBinding s = (GMLLineStringTypeBinding) container.getComponentInstanceOfType(GMLLineStringTypeBinding.class);

        Curve lineString = (Curve) s.parse(line, node, null);
        assertNotNull(lineString);
        //TODO
        /*
        assertEquals(lineString.getNumPoints(), 2);
        assertEquals(lineString.getPointN(0).getX(), 1d, 0);
        assertEquals(lineString.getPointN(0).getY(), 2d, 0);
        assertEquals(lineString.getPointN(1).getX(), 3d, 0);
        assertEquals(lineString.getPointN(1).getY(), 4d, 0);
        */
    }

    public void testCoordinatesSingle() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
        Node node = createNode(line, new ElementInstance[] { coords },
                new Object[] { createPointArray(
        				builder, new DirectPosition[] { dp1 }), },
                null, null);

        GMLLineStringTypeBinding s = (GMLLineStringTypeBinding) container.getComponentInstanceOfType(GMLLineStringTypeBinding.class);

        try {
        	Curve lineString = (Curve) s.parse(line, node, null);
            fail("Should have died with just one coordinate");
        } catch (RuntimeException e) {
            //ok
        }
    }

    public void testCoordinatesMulti() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {4.0, 5.0, 6.0});
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {7.0, 8.0, 9.0});
        Node node = createNode(line, new ElementInstance[] { coords },
                new Object[] {
                		createPointArray(
                				builder, new DirectPosition[] { dp1, dp2, dp3 }),
                }, null, null);

        GMLLineStringTypeBinding s = (GMLLineStringTypeBinding) container.getComponentInstanceOfType(GMLLineStringTypeBinding.class);

        Curve lineString = (Curve) s.parse(line, node, null);
        assertNotNull(lineString);
        //TODO
        /*
        assertEquals(lineString.getNumPoints(), 3);
        assertEquals(lineString.getPointN(0).getX(), 1d, 0);
        assertEquals(lineString.getPointN(0).getY(), 2d, 0);
        assertEquals(lineString.getPointN(1).getX(), 3d, 0);
        assertEquals(lineString.getPointN(1).getY(), 4d, 0);
        assertEquals(lineString.getPointN(2).getX(), 5d, 0);
        assertEquals(lineString.getPointN(2).getY(), 6d, 0);
        */
    }
}
