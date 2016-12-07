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
import org.opengis.geometry.primitive.Ring;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * 
 *
 * @source $URL$
 */
public class GMLLinearRingTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance ring;
    ElementInstance coord1;
    ElementInstance coord2;
    ElementInstance coord3;
    ElementInstance coord4;
    ElementInstance coord5;
    ElementInstance coords;
    MutablePicoContainer container;

    GeometryBuilder builder = new GeometryBuilder(DefaultGeographicCRS.WGS84_3D);
    protected void setUp() throws Exception {
        super.setUp();

        ring = createElement(GML.NAMESPACE, "myLineString", GML.LINEARRINGTYPE, null);
        coord1 = createElement(GML.NAMESPACE, "coord", GML.COORDTYPE, null);
        coord2 = createElement(GML.NAMESPACE, "coord", GML.COORDTYPE, null);
        coord3 = createElement(GML.NAMESPACE, "coord", GML.COORDTYPE, null);
        coord4 = createElement(GML.NAMESPACE, "coord", GML.COORDTYPE, null);
        coord5 = createElement(GML.NAMESPACE, "coord", GML.COORDTYPE, null);

        coords = createElement(GML.NAMESPACE, "coordinates", GML.COORDINATESTYPE, null);

        container = new DefaultPicoContainer();
        container.registerComponentInstance(builder);
        container.registerComponentImplementation(GMLLinearRingTypeBinding.class);
    }

    public void testCoordFour() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {3.0, 4.0, 4.0});
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {5.0, 2.0, 3.0});
        Node node = createNode(ring, new ElementInstance[] { coord1, coord2, coord3, coord4 },
                new Object[] {
                		dp1,
                		dp2,
                		dp3,
                		dp1
                }, null, null);

        GMLLinearRingTypeBinding s = (GMLLinearRingTypeBinding) container.getComponentInstanceOfType(GMLLinearRingTypeBinding.class);
        Ring linearRing = (Ring) s.parse(ring, node, null);

        assertNotNull(linearRing);
        //TODO
        /*
        assertEquals(linearRing.getNumPoints(), 4);
        assertEquals(linearRing.getPointN(0).getX(), 1d, 0);
        assertEquals(linearRing.getPointN(0).getY(), 2d, 0);
        assertEquals(linearRing.getPointN(1).getX(), 3d, 0);
        assertEquals(linearRing.getPointN(1).getY(), 4d, 0);
        assertEquals(linearRing.getPointN(2).getX(), 5d, 0);
        assertEquals(linearRing.getPointN(2).getY(), 6d, 0);
        assertEquals(linearRing.getPointN(3).getX(), 1d, 0);
        assertEquals(linearRing.getPointN(3).getY(), 2d, 0);
        */
    }

    public void testCoordLessThanFour() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {3.0, 4.0, 4.0});
        Node node = createNode(ring, new ElementInstance[] { coord1, coord2, coord3 },
                new Object[] {
                		dp1,
                		dp2,
                		dp1
                }, null, null);

        GMLLinearRingTypeBinding s = (GMLLinearRingTypeBinding) container.getComponentInstanceOfType(GMLLinearRingTypeBinding.class);

        try {
        	Ring linearRing = (Ring) s.parse(ring, node, null);
            fail("Should have thrown an exception");
        } catch (Exception e) {
            //ok
        }
    }

    public void testCoordMoreThanFour() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {3.0, 4.0, 4.0});
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {5.0, 2.0, 3.0});
    	DirectPosition dp4 = builder.createDirectPosition(new double[] {7.0, 0.0, 4.0});

        Node node = createNode(ring,
                new ElementInstance[] { coord1, coord2, coord3, coord4, coord5 },
                new Object[] {
                    dp1,
                    dp2,
                    dp3,
                    dp4,
                    dp1
                }, null, null);

        GMLLinearRingTypeBinding s = (GMLLinearRingTypeBinding) container.getComponentInstanceOfType(GMLLinearRingTypeBinding.class);
        Ring linearRing = (Ring) s.parse(ring, node, null);

        assertNotNull(linearRing);
        //TODO
        /*
        assertEquals(linearRing.getNumPoints(), 5);
        assertEquals(linearRing.getPointN(0).getX(), 1d, 0);
        assertEquals(linearRing.getPointN(0).getY(), 2d, 0);
        assertEquals(linearRing.getPointN(1).getX(), 3d, 0);
        assertEquals(linearRing.getPointN(1).getY(), 4d, 0);
        assertEquals(linearRing.getPointN(2).getX(), 5d, 0);
        assertEquals(linearRing.getPointN(2).getY(), 6d, 0);
        assertEquals(linearRing.getPointN(3).getX(), 7d, 0);
        assertEquals(linearRing.getPointN(3).getY(), 8d, 0);
        assertEquals(linearRing.getPointN(4).getX(), 1d, 0);
        assertEquals(linearRing.getPointN(4).getY(), 2d, 0);
        */
    }

    public void testCoordinatesFour() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {3.0, 4.0, 4.0});
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {5.0, 2.0, 3.0});
        Node node = createNode(ring, new ElementInstance[] { coords },
                new Object[] {
                	createPointArray(
                			builder, new DirectPosition[] { dp1, dp2, dp3, dp1 }),
                }, null, null);

        GMLLinearRingTypeBinding s = (GMLLinearRingTypeBinding) container.getComponentInstanceOfType(GMLLinearRingTypeBinding.class);

        Ring linearRing = (Ring) s.parse(ring, node, null);
        assertNotNull(linearRing);
        //TODO
        /*
        assertEquals(linearRing.getNumPoints(), 4);
        assertEquals(linearRing.getPointN(0).getX(), 1d, 0);
        assertEquals(linearRing.getPointN(0).getY(), 2d, 0);
        assertEquals(linearRing.getPointN(1).getX(), 3d, 0);
        assertEquals(linearRing.getPointN(1).getY(), 4d, 0);
        assertEquals(linearRing.getPointN(2).getX(), 5d, 0);
        assertEquals(linearRing.getPointN(2).getY(), 6d, 0);
        assertEquals(linearRing.getPointN(3).getX(), 1d, 0);
        assertEquals(linearRing.getPointN(3).getY(), 2d, 0);
        */
    }

    public void testCoordinatesLessThanFour() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {3.0, 4.0, 4.0});
        Node node = createNode(ring, new ElementInstance[] { coords },
                new Object[] {
                		createPointArray(
                    			builder, new DirectPosition[] { dp1, dp2, dp1 }),
                }, null, null);

        GMLLinearRingTypeBinding s = (GMLLinearRingTypeBinding) container.getComponentInstanceOfType(GMLLinearRingTypeBinding.class);

        Ring linearRing;

        try {
            linearRing = (Ring) s.parse(ring, node, null);
            fail("Should have thrown an exception with less then 4 points");
        } catch (Exception e) {
            //ok
        }
    }

    public void testCoordinatesMoreThanFour() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {3.0, 4.0, 4.0});
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {5.0, 2.0, 3.0});
    	DirectPosition dp4 = builder.createDirectPosition(new double[] {7.0, 0.0, 4.0});
        Node node = createNode(ring, new ElementInstance[] { coords },
                new Object[] {
                    		createPointArray(
                        			builder, new DirectPosition[] { dp1, dp2, dp3, dp4, dp1 }),
                }, null, null);

        GMLLinearRingTypeBinding s = (GMLLinearRingTypeBinding) container.getComponentInstanceOfType(GMLLinearRingTypeBinding.class);

        Ring linearRing = (Ring) s.parse(ring, node, null);
        assertNotNull(linearRing);
        //TODO
        /*
        assertEquals(linearRing.getNumPoints(), 5);
        assertEquals(linearRing.getPointN(0).getX(), 1d, 0);
        assertEquals(linearRing.getPointN(0).getY(), 2d, 0);
        assertEquals(linearRing.getPointN(1).getX(), 3d, 0);
        assertEquals(linearRing.getPointN(1).getY(), 4d, 0);
        assertEquals(linearRing.getPointN(2).getX(), 5d, 0);
        assertEquals(linearRing.getPointN(2).getY(), 6d, 0);
        assertEquals(linearRing.getPointN(3).getX(), 7d, 0);
        assertEquals(linearRing.getPointN(3).getY(), 8d, 0);
        assertEquals(linearRing.getPointN(4).getX(), 1d, 0);
        assertEquals(linearRing.getPointN(4).getY(), 2d, 0);
        */
    }
}
