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

import org.geotools.gml2.iso.GML;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.primitive.Point;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * 
 *
 * @source $URL$
 */
public class GMLMultiPointTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance mp;
    ElementInstance point1;
    ElementInstance point2;
    
    ISOGeometryBuilder builder = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84_3D);
    protected void setUp() throws Exception {
        super.setUp();

        point1 = createElement(GML.NAMESPACE, "myPoint", GML.POINTMEMBERTYPE, null);
        point2 = createElement(GML.NAMESPACE, "myPoint", GML.POINTMEMBERTYPE, null);
        mp = createElement(GML.NAMESPACE, "myMultiPoint", GML.MULTIPOINTTYPE, null);

        container = new DefaultPicoContainer();
        container.registerComponentInstance(builder);
        container.registerComponentImplementation(GMLGeometryCollectionTypeBinding.class);
        container.registerComponentImplementation(GMLMultiPointTypeBinding.class);
    }

    public void test() throws Exception {
    	Point p1 = builder.createPoint(new double[] {0, 0, 0});
    	Point p2 = builder.createPoint(new double[] {1, 1, 1});
        Node node = createNode(mp, new ElementInstance[] { point1, point2 },
                new Object[] {
                	p1,
                	p2
                }, null, null);

        GMLGeometryCollectionTypeBinding s1 = (GMLGeometryCollectionTypeBinding) container
            .getComponentInstanceOfType(GMLGeometryCollectionTypeBinding.class);
        GMLMultiPointTypeBinding s2 = (GMLMultiPointTypeBinding) container
            .getComponentInstanceOfType(GMLMultiPointTypeBinding.class);

        MultiPoint mpoint = (MultiPoint) s2.parse(mp, node, s1.parse(mp, node, null));

        assertNotNull(mpoint);
        assertEquals(mpoint.getElements().size(), 2);
        assertTrue(mpoint.getElements().contains(p1));
        assertTrue(mpoint.getElements().contains(p2));
    }
}
