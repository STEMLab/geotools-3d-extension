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
import java.util.HashSet;

import org.geotools.geometry.GeometryBuilder;
import org.geotools.gml2.GML;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.primitive.Point;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * 
 *
 * @source $URL$
 */
public class GMLMultiPointPropertyTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance association;
    ElementInstance geometry;

    GeometryBuilder builder = new GeometryBuilder(DefaultGeographicCRS.WGS84_3D);
    protected void setUp() throws Exception {
        super.setUp();

        association = createElement(GML.NAMESPACE, "myMultiPointProperty",
                GML.MULTIPOINTPROPERTYTYPE, null);
        geometry = createElement(GML.NAMESPACE, "myMultiPoint", GML.MULTIPOINTTYPE, null);

        container = new DefaultPicoContainer();
        container.registerComponentInstance(builder);
        container.registerComponentImplementation(GMLGeometryAssociationTypeBinding.class);
        container.registerComponentImplementation(GMLMultiPointPropertyTypeBinding.class);
    }

    public void testWithGeometry() throws Exception {
    	Point p1 = builder.createPoint(new double[] {0, 0, 0});
    	Point p2 = builder.createPoint(new double[] {1, 1, 1});

        Node node = createNode(association, new ElementInstance[] { geometry },
                new Object[] { builder.createMultiPoint(new HashSet(Arrays.asList(p1, p2))) },
                null, null);

        GMLGeometryAssociationTypeBinding s = (GMLGeometryAssociationTypeBinding) container
            .getComponentInstanceOfType(GMLGeometryAssociationTypeBinding.class);

        GMLMultiPointPropertyTypeBinding s1 = (GMLMultiPointPropertyTypeBinding) container
            .getComponentInstanceOfType(GMLMultiPointPropertyTypeBinding.class);

        MultiPoint p = (MultiPoint) s1.parse(association, node, s.parse(association, node, null));
        assertNotNull(p);
    }
}
