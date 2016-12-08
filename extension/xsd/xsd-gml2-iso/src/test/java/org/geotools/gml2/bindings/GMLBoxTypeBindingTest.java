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
import org.opengis.geometry.Envelope;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * 
 *
 * @source $URL$
 */
public class GMLBoxTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance box;
    ElementInstance coord1;
    ElementInstance coord2;
    ElementInstance coord3;
    ElementInstance coords;

    GeometryBuilder builder = new GeometryBuilder(DefaultGeographicCRS.WGS84_3D);
    protected void setUp() throws Exception {
        super.setUp();

        box = createElement(GML.NAMESPACE, "myBox", GML.BOXTYPE, null);
        coord1 = createElement(GML.NAMESPACE, "coord", GML.COORDTYPE, null);
        coord2 = createElement(GML.NAMESPACE, "coord", GML.COORDTYPE, null);
        coord3 = createElement(GML.NAMESPACE, "coord", GML.COORDTYPE, null);
        coords = createElement(GML.NAMESPACE, "coordinates", GML.COORDINATESTYPE, null);
        
        container.registerComponentInstance(builder);
    }

    public void testTwoCoord() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {4.0, 5.0, 6.0});
        Node node = createNode(box, new ElementInstance[] { coord1, coord2 },
                new Object[] { dp1, dp2 }, null, null);

        GMLBoxTypeBinding s = (GMLBoxTypeBinding) getBinding(GML.BOXTYPE);
        Envelope e = (Envelope) s.parse(box, node, null);
        assertNotNull(e);
        assertEquals(e.getLowerCorner().getOrdinate(0), 1d, 0d);
        assertEquals(e.getLowerCorner().getOrdinate(1), 2d, 0d);
        assertEquals(e.getLowerCorner().getOrdinate(2), 3d, 0d);
        
        assertEquals(e.getUpperCorner().getOrdinate(0), 4d, 0d);
        assertEquals(e.getUpperCorner().getOrdinate(1), 5d, 0d);
        assertEquals(e.getUpperCorner().getOrdinate(2), 6d, 0d);
    }

    public void testSingleCoord() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
        Node node = createNode(box, new ElementInstance[] { coord1 },
                new Object[] { createPointArray(builder, new DirectPosition[] { dp1 }) }, null, null);

        GMLBoxTypeBinding s = (GMLBoxTypeBinding) getBinding(GML.BOXTYPE);

        try {
            Envelope e = (Envelope) s.parse(box, node, null);
            fail("< 2 coordinate envelope should have thrown exception");
        } catch (Exception e) {
            //ok
        }
    }

    public void testMultiCoord() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {4.0, 5.0, 6.0});
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {7.0, 8.0, 9.0});
        Node node = createNode(box, new ElementInstance[] { coord1, coord2, coord3 },
                new Object[] {
                    dp1,
                    dp2,
                    dp3
                }, null, null);

        GMLBoxTypeBinding s = (GMLBoxTypeBinding) getBinding(GML.BOXTYPE);

        try {
            Envelope e = (Envelope) s.parse(box, node, null);
            fail("> 2 coordinate envelope should have thrown exception");
        } catch (Exception e) {
            //ok
        }
    }

    public void testTwoCoordinates() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {4.0, 5.0, 6.0});
        Node node = createNode(box, new ElementInstance[] { coords },
                new Object[] {
                	createPointArray(builder, new DirectPosition[] {dp1, dp2})
                }, null, null);

        GMLBoxTypeBinding s = (GMLBoxTypeBinding) getBinding(GML.BOXTYPE);
        Envelope e = (Envelope) s.parse(box, node, null);
        assertNotNull(e);
        assertEquals(e.getLowerCorner().getOrdinate(0), 1d, 0d);
        assertEquals(e.getLowerCorner().getOrdinate(1), 2d, 0d);
        assertEquals(e.getLowerCorner().getOrdinate(2), 3d, 0d);
        
        assertEquals(e.getUpperCorner().getOrdinate(0), 4d, 0d);
        assertEquals(e.getUpperCorner().getOrdinate(1), 5d, 0d);
        assertEquals(e.getUpperCorner().getOrdinate(2), 6d, 0d);
    }

    public void testSingleCoordinates() throws Exception {

    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
        Node node = createNode(box, new ElementInstance[] { coords },
                new Object[] { createPointArray(builder, new DirectPosition[] {dp1}) }, null, null);

        GMLBoxTypeBinding s = (GMLBoxTypeBinding) getBinding(GML.BOXTYPE);

        try {
            Envelope e = (Envelope) s.parse(box, node, null);
            fail("< 2 coordinate envelope should have thrown exception");
        } catch (Exception e) {
            //ok
        }
    }

    public void testMultiCoordinates() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {4.0, 5.0, 6.0});
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {7.0, 8.0, 9.0});
        Node node = createNode(box, new ElementInstance[] { coords },
                new Object[] {
                		createPointArray(builder, new DirectPosition[] {dp1, dp2, dp3})
                }, null, null);

        GMLBoxTypeBinding s = (GMLBoxTypeBinding) getBinding(GML.BOXTYPE);

        try {
            Envelope e = (Envelope) s.parse(box, node, null);
            fail("> 2 coordinate envelope should have thrown exception");
        } catch (Exception e) {
            //ok
        }
    }
}
