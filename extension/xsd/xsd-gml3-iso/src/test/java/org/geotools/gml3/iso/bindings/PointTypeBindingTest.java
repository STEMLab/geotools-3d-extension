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
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.primitive.Point;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.w3c.dom.Document;

/**
 * 
 *
 * @source $URL$
 */
public class PointTypeBindingTest extends GML3TestSupport {
    
    public void testPos() throws Exception {
        GML3MockData.point(document, document);

        Point p = (Point) parse();
        assertNotNull(p);
        assertEquals(gb.createDirectPosition(new double[] {1d, 2d})
        		, p.getDirectPosition());

        assertEquals(p.getCoordinateReferenceSystem(), DefaultGeographicCRS.WGS84);
    }
    
    public void testPos3D() throws Exception {
        GML3MockData.point3D(document, document);

        Point p = (Point) parse();
        assertNotNull(p);
        assertEquals(gb3D.createDirectPosition(new double[] {1d, 2d, 10d})
        		, p.getDirectPosition());

        assertEquals(p.getCoordinateReferenceSystem(), DefaultGeographicCRS.WGS84_3D);
    }

    public void testEncode() throws Exception {
        Point p = GML3MockData.point();
        Document dom = encode(p, GML.Point);

        assertEquals(1,
            dom.getElementsByTagNameNS(GML.NAMESPACE, GML.pos.getLocalPart()).getLength());
        //assertEquals("urn:x-ogc:def:crs:EPSG:6.11.2:4326",
        assertEquals(p.getCoordinateReferenceSystem(), CRS.decode(dom.getDocumentElement().getAttribute("srsName")));
    }
    
    public void testEncode2D() throws Exception {
    	Point point = GML3MockData.point();
        Document doc = encode(point, GML.Point);
        
        checkDimension(doc, GML.Point.getLocalPart(), 2);
        checkPosOrdinates(doc, 2);
    }

    public void testEncode3D() throws Exception {
    	Point point = GML3MockData.point_3D();
        Document doc = encode(point, GML.Point);
        
        checkDimension(doc, GML.Point.getLocalPart(), 3);
        checkPosOrdinates(doc, 3);
    }

}
