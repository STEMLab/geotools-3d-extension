/*
 *    GeoTools - The Open Source Java GIS Toolkitap
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
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;


/**
 * 
 *
 * @source $URL$
 */
public class GMLLineStringPropertyTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance association;
    ElementInstance geometry;

    protected void setUp() throws Exception {
        super.setUp();

        association = createElement(GML.NAMESPACE, "myLineStringProperty",
                GML.LINESTRINGPROPERTYTYPE, null);
        geometry = createElement(GML.NAMESPACE, "myLineString", GML.LINESTRINGTYPE, null);
    }

    public void testWithGeometry() throws Exception {
    	GeometryBuilder builder = new GeometryBuilder(DefaultGeographicCRS.WGS84_3D);
    	
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {3.0, 4.0, 4.0});
    	DirectPosition dp3 = builder.createDirectPosition(new double[] {5.0, 2.0, 3.0});
    	PointArray pa = builder.createPointArray();
    	pa.add(dp1);
    	pa.add(dp2);
    	pa.add(dp3);
    	pa.add(dp1);
    	
        Node node = createNode(association, new ElementInstance[] { geometry },
                new Object[] {
                	builder.createCurve(pa)
                }, null, null);
        GMLGeometryAssociationTypeBinding s = (GMLGeometryAssociationTypeBinding) getBinding(GML.GEOMETRYASSOCIATIONTYPE);
        GMLLineStringPropertyTypeBinding s1 = (GMLLineStringPropertyTypeBinding) getBinding(GML.LINESTRINGPROPERTYTYPE);
        Curve p = (Curve) s1.parse(association, node, s.parse(association, node, null));
        assertNotNull(p);
    }
}
