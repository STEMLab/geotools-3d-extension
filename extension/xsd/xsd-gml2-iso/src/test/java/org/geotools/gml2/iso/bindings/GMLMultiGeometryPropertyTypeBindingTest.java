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
import org.geotools.gml2.iso.bindings.GMLMultiGeometryPropertyTypeBinding;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.primitive.Point;
/**
 * 
 *
 * @source $URL$
 */
public class GMLMultiGeometryPropertyTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance association;
    ElementInstance geometry;

    protected void setUp() throws Exception {
        super.setUp();

        association = createElement(GML.NAMESPACE, "myMultiGeometryProperty",
                GML.GEOMETRYPROPERTYTYPE, null);
        geometry = createElement(GML.NAMESPACE, "myGeometryCollection", GML.GEOMETRYCOLLECTIONTYPE,
                null);
    }

    public void testWithGeometry() throws Exception {
    	GeometryBuilder builder = new GeometryBuilder(DefaultGeographicCRS.WGS84_3D);
        Point p1 = builder.createPoint(new double[] {0, 0, 0});
        Point p2 = builder.createPoint(new double[] {1, 1, 1});

        Node node = createNode(association, new ElementInstance[] { geometry },
                new Object[] {
                	builder.createMultiPrimitive(new HashSet(Arrays.asList(p1, p2)))
                }, null, null);

        GMLGeometryAssociationTypeBinding s = (GMLGeometryAssociationTypeBinding) getBinding(GML.GEOMETRYASSOCIATIONTYPE);
        GMLMultiGeometryPropertyTypeBinding s1 = (GMLMultiGeometryPropertyTypeBinding) getBinding(GML.MULTIGEOMETRYPROPERTYTYPE);

        MultiPrimitive p = (MultiPrimitive) s1.parse(association, node,
                s.parse(association, node, null));
        assertNotNull(p);
    }
}
