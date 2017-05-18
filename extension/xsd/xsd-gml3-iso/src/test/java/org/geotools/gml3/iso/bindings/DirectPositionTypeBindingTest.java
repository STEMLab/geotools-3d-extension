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
import org.opengis.geometry.primitive.Point;
import org.w3c.dom.Document;

/**
 * 
 *
 * @source $URL$
 */
public class DirectPositionTypeBindingTest extends GML3TestSupport {
	
	//We don't need test 1D
	/*
    public void test1D() throws Exception {
        GML3MockData.element(GML.pos, document, document);
        document.getDocumentElement().appendChild(document.createTextNode("1.0"));

        DirectPosition pos = (DirectPosition) parse();

        assertNotNull(pos);
        assertEquals(pos.getOrdinate(0), 1.0, 0);
    }
	*/
    public void test2D() throws Exception {
        GML3MockData.element(GML.pos, document, document);
        document.getDocumentElement().appendChild(document.createTextNode("1.0 2.0"));

        DirectPosition pos = (DirectPosition) parse();

        assertNotNull(pos);
        assertEquals(pos.getOrdinate(0), 1.0, 0);
        assertEquals(pos.getOrdinate(1), 2.0, 0);
    }
    
    public void testEncode2D() throws Exception {
    	Point point = GML3MockData.point();
    	DirectPosition seq = point.getDirectPosition();
        Document doc = encode(seq, GML.pos);
        checkPosOrdinates(doc, 2);
    }
    
    public void testEncode3D() throws Exception {
    	Point point = GML3MockData.point_3D();
    	DirectPosition seq = point.getDirectPosition();
        Document doc = encode(seq, GML.pos);
        checkPosOrdinates(doc, 3);
    }
    

}
