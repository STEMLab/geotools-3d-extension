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

import org.geotools.geometry.ISOGeometryBuilder;
import org.geotools.gml2.iso.GML;
import org.geotools.gml2.iso.bindings.GMLPolygonTypeBinding;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Surface;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;


/**
 * 
 *
 * @source $URL$
 */
public class GMLPolygonTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance poly;
    ElementInstance oring;
    ElementInstance iring;
    MutablePicoContainer container;

    ISOGeometryBuilder builder = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84_3D);
    protected void setUp() throws Exception {
        super.setUp();

        poly = createElement(GML.NAMESPACE, "myPolygon", GML.POLYGONTYPE, null);
        oring = createElement(GML.NAMESPACE, "outerBoundaryIs", GML.LINEARRINGTYPE, null);
        iring = createElement(GML.NAMESPACE, "innerBoundaryIs", GML.LINEARRINGTYPE, null);

        container = new DefaultPicoContainer();
        container.registerComponentInstance(builder);
        container.registerComponentImplementation(GMLPolygonTypeBinding.class);
    }

    public void testNoInnerRing() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {3.0, 4.0, 4.0});
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {5.0, 2.0, 3.0});
    	PointArray pa = createPointArray(builder, new DirectPosition[] {dp1, dp2, dp3, dp1});
    	
        Node node = createNode(poly, new ElementInstance[] { oring },
                new Object[] {
                		builder.createRing(Arrays.asList(builder.createCurve(pa)))
                }, null, null);

        GMLPolygonTypeBinding s = (GMLPolygonTypeBinding) container.getComponentInstanceOfType(GMLPolygonTypeBinding.class);
        Surface p = (Surface) s.parse(poly, node, null);
        assertNotNull(p);
        //TODO
        /*
        assertEquals(p.getExteriorRing().getPointN(0).getX(), 1d, 0d);
        assertEquals(p.getExteriorRing().getPointN(0).getY(), 2d, 0d);
        assertEquals(p.getExteriorRing().getPointN(1).getX(), 3d, 0d);
        assertEquals(p.getExteriorRing().getPointN(1).getY(), 4d, 0d);
        assertEquals(p.getExteriorRing().getPointN(2).getX(), 5d, 0d);
        assertEquals(p.getExteriorRing().getPointN(2).getY(), 6d, 0d);
        assertEquals(p.getExteriorRing().getPointN(3).getX(), 1d, 0d);
        assertEquals(p.getExteriorRing().getPointN(3).getY(), 2d, 0d);
        */
    }

    public void testInnerRing() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {0, 0, 3});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {10, 0, 3});
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {10, 10, 3});
    	DirectPosition dp4 = builder.createDirectPosition(new double[] {0, 10, 3});
    	PointArray pa1 = createPointArray(builder, new DirectPosition[] {dp1, dp2, dp3, dp4, dp1});
    	
    	DirectPosition dp5 = builder.createDirectPosition(new double[] {1, 1, 3});
    	DirectPosition dp6 = builder.createDirectPosition(new double[] {8, 1, 3});
    	DirectPosition dp7 = builder.createDirectPosition(new double[] {8, 8, 3});
    	DirectPosition dp8 = builder.createDirectPosition(new double[] {1, 8, 3});
    	PointArray pa2 = createPointArray(builder, new DirectPosition[] {dp5, dp6, dp7, dp8, dp5});
        Node node = createNode(poly, new ElementInstance[] { oring, iring },
                new Object[] {
                		builder.createRing(Arrays.asList(builder.createCurve(pa1))),
                		builder.createRing(Arrays.asList(builder.createCurve(pa2)))
                }, null, null);

        GMLPolygonTypeBinding s = (GMLPolygonTypeBinding) container.getComponentInstanceOfType(GMLPolygonTypeBinding.class);
        Surface p = (Surface) s.parse(poly, node, null);
        assertNotNull(p);
        //TODO
        /*
        assertEquals(p.getExteriorRing().getPointN(0).getX(), 0d, 0d);
        assertEquals(p.getExteriorRing().getPointN(0).getY(), 0d, 0d);
        assertEquals(p.getExteriorRing().getPointN(1).getX(), 10d, 0d);
        assertEquals(p.getExteriorRing().getPointN(1).getY(), 0d, 0d);
        assertEquals(p.getExteriorRing().getPointN(2).getX(), 10d, 0d);
        assertEquals(p.getExteriorRing().getPointN(2).getY(), 10d, 0d);
        assertEquals(p.getExteriorRing().getPointN(3).getX(), 0d, 0d);
        assertEquals(p.getExteriorRing().getPointN(3).getY(), 10d, 0d);
        assertEquals(p.getExteriorRing().getPointN(4).getX(), 0d, 0d);
        assertEquals(p.getExteriorRing().getPointN(4).getY(), 0d, 0d);

        assertEquals(p.getInteriorRingN(0).getPointN(0).getX(), 1d, 0d);
        assertEquals(p.getInteriorRingN(0).getPointN(0).getY(), 1d, 0d);
        assertEquals(p.getInteriorRingN(0).getPointN(1).getX(), 9d, 0d);
        assertEquals(p.getInteriorRingN(0).getPointN(1).getY(), 1d, 0d);
        assertEquals(p.getInteriorRingN(0).getPointN(2).getX(), 9d, 0d);
        assertEquals(p.getInteriorRingN(0).getPointN(2).getY(), 9d, 0d);
        assertEquals(p.getInteriorRingN(0).getPointN(3).getX(), 1d, 0d);
        assertEquals(p.getInteriorRingN(0).getPointN(3).getY(), 9d, 0d);
        assertEquals(p.getInteriorRingN(0).getPointN(4).getX(), 1d, 0d);
        assertEquals(p.getInteriorRingN(0).getPointN(4).getY(), 1d, 0d);
        */
    }
}
