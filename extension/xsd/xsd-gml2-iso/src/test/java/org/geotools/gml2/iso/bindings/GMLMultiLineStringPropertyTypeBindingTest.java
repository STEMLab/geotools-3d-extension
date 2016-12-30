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

import org.geotools.geometry.ISOGeometryBuilder;
import org.geotools.gml2.iso.GML;
import org.geotools.gml2.iso.bindings.GMLGeometryAssociationTypeBinding;
import org.geotools.gml2.iso.bindings.GMLMultiLineStringPropertyTypeBinding;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * 
 *
 * @source $URL$
 */
public class GMLMultiLineStringPropertyTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance association;
    ElementInstance geometry;

    ISOGeometryBuilder builder = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84_3D);
    protected void setUp() throws Exception {
        super.setUp();

        association = createElement(GML.NAMESPACE, "myMultiLineStringProperty",
                GML.MULTILINESTRINGPROPERTYTYPE, null);
        geometry = createElement(GML.NAMESPACE, "myMultiLineString", GML.MULTILINESTRINGTYPE, null);

        container = new DefaultPicoContainer();
        container.registerComponentInstance(builder);
        container.registerComponentImplementation(GMLGeometryAssociationTypeBinding.class);
        container.registerComponentImplementation(GMLMultiLineStringPropertyTypeBinding.class);
    }

    public void testWithGeometry() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {0, 0, 0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {1, 1, 1});
    	PointArray pa1 = builder.createPointArray();
    	pa1.add(dp1);
    	pa1.add(dp2);
    	
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {2, 2, 0});
    	DirectPosition dp4 = builder.createDirectPosition(new double[] {3, 3, 1});
    	PointArray pa2 = builder.createPointArray();
    	pa2.add(dp3);
    	pa2.add(dp4);
    	
    	Curve c1 = builder.createCurve(pa1);
    	Curve c2 = builder.createCurve(pa2);
        Node node = createNode(association, new ElementInstance[] { geometry },
                new Object[] {
                    builder.createMultiCurve(new HashSet(Arrays.asList(c1, c2)))
                }, null, null);

        GMLGeometryAssociationTypeBinding s = (GMLGeometryAssociationTypeBinding) container
            .getComponentInstanceOfType(GMLGeometryAssociationTypeBinding.class);

        GMLMultiLineStringPropertyTypeBinding s1 = (GMLMultiLineStringPropertyTypeBinding) container
            .getComponentInstanceOfType(GMLMultiLineStringPropertyTypeBinding.class);

        MultiCurve p = (MultiCurve) s1.parse(association, node,
                s.parse(association, node, null));
        assertNotNull(p);
    }
}
