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

import java.util.HashSet;
import java.util.Set;

import org.geotools.geometry.iso.io.wkt.Coordinate;
import org.geotools.gml3.iso.GML;
import org.geotools.gml3.iso.GML3TestSupport;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Point;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;




/**
 * 
 *
 * @source $URL$
 */
public class MultiPointTypeBindingTest extends GML3TestSupport {
    
    public void test() throws Exception {
        GML3MockData.multiPoint(document, document);

        MultiPoint multiPoint = (MultiPoint) parse();
        assertNotNull(multiPoint);

        assertEquals(4, multiPoint.getElements().size());
    }
    
    public void test3D() throws Exception {
        GML3MockData.multiPoint3D(document, document);

        MultiPoint multiPoint = (MultiPoint) parse();
        assertNotNull(multiPoint);

        assertEquals(4, multiPoint.getElements().size());
        Point[] pl = new Point[multiPoint.getElements().size()];
        multiPoint.getElements().toArray(pl);
        Point p = (Point)pl[0];      
        assertTrue(new Coordinate(1d, 2d, 10d).equals3D(getCoordinateOfDirectPositionAsCoordinate(p.getDirectPosition())));
    }
    public Coordinate getCoordinateOfDirectPositionAsCoordinate(DirectPosition p){
    	double[] dp = p.getCoordinate();
    	Coordinate cp = null;
    	if(dp.length == 2){
    		 cp = new Coordinate(dp[0],dp[1]);
    	}
    	else if(dp.length == 3){
    		cp = new Coordinate(dp[0],dp[1],dp[2]);
    	}
		return cp;
    	
    }
    public void testEncode() throws Exception {
        Geometry geometry = (Geometry)GML3MockData.multiPoint();
        //GML3EncodingUtils.setID(geometry, "geometry");
        Document dom = encode(geometry, GML.MultiPoint);
        // print(dom);
        //assertEquals("geometry", getID(dom.getDocumentElement()));
        assertEquals(2, dom.getElementsByTagNameNS(GML.NAMESPACE, "pointMember").getLength());
        NodeList children = dom.getElementsByTagNameNS(GML.NAMESPACE, GML.Point.getLocalPart());
        assertEquals(2, children.getLength());
        //assertEquals("geometry.1", getID(children.item(0)));
        //assertEquals("geometry.2", getID(children.item(1)));
    }

}
