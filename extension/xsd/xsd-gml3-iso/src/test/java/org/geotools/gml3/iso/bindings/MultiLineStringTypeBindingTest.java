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
package org.geotools.gml3.iso.bindings;

import org.geotools.gml3.iso.GML;
import org.geotools.gml3.iso.GML3TestSupport;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.primitive.Curve;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;







/**
 * 
 *
 * @source $URL$
 */
public class MultiLineStringTypeBindingTest extends GML3TestSupport {
    
    public void test() throws Exception {
        GML3MockData.multiLineString(document, document);

        MultiCurve multiLineString = (MultiCurve) parse();
        assertNotNull(multiLineString);

        assertEquals(2, multiLineString.getElements().size());
    }
    
    public void test3D() throws Exception {
        GML3MockData.multiLineString3D(document, document);

        MultiCurve multiLineString = (MultiCurve) parse();
        assertNotNull(multiLineString);

        assertEquals(2, multiLineString.getElements().size());
        Curve[] lines = new Curve[multiLineString.getElements().size()];
        multiLineString.getElements().toArray(lines);
        Curve line = lines[0];
   
        DirectPosition p = line.getStartPoint();
        double[] tp = {1.0,2.0,10.0};
        assertEquals(tp,p.getCoordinate());
        double[] tp2 = {3d,4d,20d};
        p = line.getEndPoint();
        assertEquals(tp2, p.getCoordinate());
    }
   
    public void testEncode() throws Exception {
        Geometry geometry = GML3MockData.multiLineString();
        //GML3EncodingUtils.setID(geometry, "geometry");
        Document dom = encode(geometry, GML.MultiLineString);
        // print(dom);
        //assertEquals("geometry", getID(dom.getDocumentElement()));
        assertEquals(2, dom.getElementsByTagNameNS(GML.NAMESPACE, "lineStringMember").getLength());
        NodeList children = dom.getElementsByTagNameNS(GML.NAMESPACE, GML.LineString.getLocalPart());
        assertEquals(2, children.getLength());
       // assertEquals("geometry.1", getID(children.item(0)));
       // assertEquals("geometry.2", getID(children.item(1)));

        checkDimension(dom, GML.MultiLineString.getLocalPart(), 2);
        checkDimension(dom, GML.LineString.getLocalPart(), 2);
        //checkPosListOrdinates(dom, 2 * geometry.getGeometryN(0).getNumPoints());
    }
}
