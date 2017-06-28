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

import org.geotools.geometry.iso.util.PointArrayUtil;
import org.geotools.gml3.iso.GML;
import org.geotools.gml3.iso.GML3TestSupport;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Curve;
import org.w3c.dom.Document;


/**
 * 
 *
 * @source $URL$
 */
public class DirectPositionListTypeBindingTest extends GML3TestSupport {

    public void test2D() throws Exception {
        GML3MockData.element(GML.posList, document, document);
        document.getDocumentElement().setAttribute("srsDimension", "2");
        document.getDocumentElement().setAttribute("count", "1");
        document.getDocumentElement().appendChild(document.createTextNode("1.0 2.0 "));

        DirectPosition[] dps = (DirectPosition[]) parse();
        assertNotNull(dps);

        assertEquals(1, dps.length);
        assertTrue(dps[0] instanceof DirectPosition);

        assertEquals(1d, dps[0].getOrdinate(0), 0d);
        assertEquals(2d, dps[0].getOrdinate(1), 0d);
    }
    
    public void test3D() throws Exception {
        GML3MockData.element(GML.posList, document, document);
        document.getDocumentElement().setAttribute("srsDimension", "3");

        document.getDocumentElement().appendChild(document.createTextNode("1.0 2.0 1.0 3 4 5"));

        DirectPosition[] dps = (DirectPosition[]) parse();
        assertNotNull(dps);

        assertEquals(2, dps.length);
        assertTrue(dps[0] instanceof DirectPosition);

        assertEquals(1d, dps[0].getOrdinate(0), 0d);
        assertEquals(2d, dps[0].getOrdinate(1), 0d);
        assertEquals(1d, dps[0].getOrdinate(2), 0d);

        assertEquals(3d, dps[1].getOrdinate(0), 0d);
        assertEquals(4d, dps[1].getOrdinate(1), 0d);
        assertEquals(5d, dps[1].getOrdinate(2), 0d);
    }
    
    public void testEncode2D() throws Exception {
    	Curve line = (Curve)GML3MockData.lineString();
    	PointArray seq = PointArrayUtil.toList(GML3MockData.gb,line);
    	DirectPosition[] dps = pointArrayToDirectPositionArray(seq);
        
        Document doc = encode(dps, GML.posList);
        checkPosListOrdinates(doc, 2 * seq.size());
    }
    
    public void testEncode3D() throws Exception {
    	Curve line = (Curve) GML3MockData.lineStringLite3D();
    	PointArray seq = PointArrayUtil.toList(GML3MockData.gb,line);
    	DirectPosition[] dps = pointArrayToDirectPositionArray(seq);
    	
        Document doc = encode(dps, GML.posList);
        doc.getDocumentElement().setAttribute("srsDimension", "3");
        checkPosListOrdinates(doc, 3 * seq.size());
    }
    
    private DirectPosition[] pointArrayToDirectPositionArray(PointArray seq) {
    	DirectPosition[] dps = new DirectPosition[seq.size()];
    	for(int i = 0; i < seq.size(); i++) {
    		Position p = seq.get(i);
    		DirectPosition dp = p.getDirectPosition();
    		dps[i] = dp;
    	}
    	return dps;
    }   
}
