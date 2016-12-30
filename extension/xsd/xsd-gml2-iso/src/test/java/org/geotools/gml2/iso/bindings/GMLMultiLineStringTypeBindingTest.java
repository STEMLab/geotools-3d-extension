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
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiCurve;
import org.picocontainer.defaults.DefaultPicoContainer;


/**
 * 
 *
 * @source $URL$
 */
public class GMLMultiLineStringTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance ml;
    ElementInstance line1;
    ElementInstance line2;

    ISOGeometryBuilder builder = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84_3D);
    protected void setUp() throws Exception {
        super.setUp();

        line1 = createElement(GML.NAMESPACE, "myLine", GML.LINESTRINGMEMBERTYPE, null);
        line2 = createElement(GML.NAMESPACE, "myLine", GML.LINESTRINGMEMBERTYPE, null);
        ml = createElement(GML.NAMESPACE, "myMultiLine", GML.MULTILINESTRINGTYPE, null);

        container = new DefaultPicoContainer();
        container.registerComponentInstance(builder);
        container.registerComponentImplementation(GMLGeometryCollectionTypeBinding.class);
        container.registerComponentImplementation(GMLMultiLineStringTypeBinding.class);
    }

    public void test() throws Exception {
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {0.0, 0.0, 0.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {1.0, 1.0, 1.0});
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {2.0, 2.0, 2.0});
    	DirectPosition dp4 = builder.createDirectPosition(new double[] {3.0, 3.0, 3.0});
        Node node = createNode(ml, new ElementInstance[] { line1, line2 },
                new Object[] {
                    builder.createCurve(
                    		createPointArray(builder, new DirectPosition[] { dp1, dp2 })),
                    builder.createCurve(
                    		createPointArray(builder, new DirectPosition[] { dp3, dp4 })
            		),
                }, null, null);

        GMLGeometryCollectionTypeBinding s1 = (GMLGeometryCollectionTypeBinding) container
            .getComponentInstanceOfType(GMLGeometryCollectionTypeBinding.class);
        GMLMultiLineStringTypeBinding s2 = (GMLMultiLineStringTypeBinding) container
            .getComponentInstanceOfType(GMLMultiLineStringTypeBinding.class);

        MultiCurve mline = (MultiCurve) s2.parse(ml, node, s1.parse(ml, node, null));

        assertNotNull(mline);
        assertEquals(mline.getElements().size(), 2);
    }
}
