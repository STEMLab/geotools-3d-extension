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

import java.util.Arrays;

import org.geotools.geometry.GeometryBuilder;
import org.geotools.gml2.GML;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.picocontainer.defaults.DefaultPicoContainer;
/**
 * 
 *
 * @source $URL$
 */
public class GMLGeometryCollectionTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance gcol;
    ElementInstance point1;
    ElementInstance point2;
    ElementInstance line1;
    ElementInstance ring1;
    ElementInstance poly1;
    GeometryBuilder gb;

    protected void setUp() throws Exception {
        super.setUp();

        point1 = createElement(GML.NAMESPACE, "myPoint", GML.POINTMEMBERTYPE, null);
        point2 = createElement(GML.NAMESPACE, "myPoint", GML.POINTMEMBERTYPE, null);
        line1 = createElement(GML.NAMESPACE, "myLine", GML.LINESTRINGMEMBERTYPE, null);
        ring1 = createElement(GML.NAMESPACE, "myLine", GML.LINEARRINGMEMBERTYPE, null);
        poly1 = createElement(GML.NAMESPACE, "myPoly", GML.POLYGONMEMBERTYPE, null);
        gcol = createElement(GML.NAMESPACE, "myColl", GML.GEOMETRYCOLLECTIONTYPE, null);
        
        gb = new GeometryBuilder(DefaultGeographicCRS.WGS84_3D);
        container = new DefaultPicoContainer();
        container.registerComponentInstance(gb);
        container.registerComponentImplementation(GMLGeometryCollectionTypeBinding.class);
    }

    public void testHomogeneous() throws Exception {
        Node node = createNode(gcol, new ElementInstance[] { point1, point2 },
                new Object[] {
                	gb.createPoint(new double[] {0, 0, 0}), gb.createPoint(new double[] {1, 1, 1})
                }, null, null);

        GMLGeometryCollectionTypeBinding s = (GMLGeometryCollectionTypeBinding) container
            .getComponentInstanceOfType(GMLGeometryCollectionTypeBinding.class);

        MultiPrimitive gc = (MultiPrimitive) s.parse(gcol, node, null);
        assertNotNull(gc);
        assertEquals(gc.getElements().size(), 2);
        /*
        assertTrue(gc.getGeometryN(0) instanceof Point);
        assertTrue(gc.getGeometryN(1) instanceof Point);
        assertEquals(((Point) gc.getGeometryN(0)).getX(), 0d, 0d);
        assertEquals(((Point) gc.getGeometryN(0)).getY(), 0d, 0d);
        assertEquals(((Point) gc.getGeometryN(1)).getX(), 1d, 0d);
        assertEquals(((Point) gc.getGeometryN(1)).getY(), 1d, 0d);
        */
    }

    public void testHeterogeneous() throws Exception {
    	
    	

    	DirectPosition dp1 = gb.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = gb.createDirectPosition(new double[] {3.0, 4.0, 4.0});
    	DirectPosition dp3 = gb.createDirectPosition(new double[] {5.0, 2.0, 3.0});
    	PointArray lsPa = gb.createPointArray();
    	lsPa.add(dp1);
    	lsPa.add(dp2);
    	
    	PointArray lrPa = gb.createPointArray();
    	lrPa.add(dp1);
    	lrPa.add(dp2);
    	lrPa.add(dp3);
    	lrPa.add(dp1);
    	Ring ring = gb.createRing(Arrays.asList(gb.createCurve(lrPa)));
    	SurfaceBoundary sb = gb.createSurfaceBoundary(ring);
    	
        Node node = createNode(gcol, new ElementInstance[] { point1, point2, line1, ring1, poly1 },
                new Object[] {
                	gb.createPoint(new double[] {0, 0, 0}), gb.createPoint(new double[] {1, 1, 1}),
                	gb.createCurve(lsPa),
                	ring,
                	gb.createSurface(sb)
                }, null, null);

        GMLGeometryCollectionTypeBinding s = (GMLGeometryCollectionTypeBinding) container
            .getComponentInstanceOfType(GMLGeometryCollectionTypeBinding.class);

        MultiPrimitive gc = (MultiPrimitive) s.parse(gcol, node, null);
        assertNotNull(gc);
        assertEquals(gc.getElements().size(), 5);
        //TODO
        /*
        assertTrue(gc.getGeometryN(0) instanceof Point);
        assertTrue(gc.getGeometryN(1) instanceof Point);
        assertTrue(gc.getGeometryN(2) instanceof LineString);
        assertTrue(gc.getGeometryN(3) instanceof LinearRing);
        assertTrue(gc.getGeometryN(4) instanceof Polygon);
        */
    }
}
