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
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;


/**
 * 
 *
 * @source $URL$
 */
public class LineStringTypeBindingTest extends GML3TestSupport {
    
    public void testPos() throws Exception {
        document.appendChild(GML3MockData.lineStringWithPos(document, null));

        Curve line = (Curve) parse();
        assertNotNull(line);
        PointArray seq = PointArrayUtil.toList(GML3MockData.gb,line);
        
        assertEquals(1.0, seq.get(0).getDirectPosition().getCoordinate()[0]);
        assertEquals(2.0, seq.get(0).getDirectPosition().getCoordinate()[1]);
        assertEquals(3.0, seq.get(1).getDirectPosition().getCoordinate()[0]);
        assertEquals(4.0, seq.get(1).getDirectPosition().getCoordinate()[1]);
    }
    
    public void testPos3D() throws Exception {
        document.appendChild(GML3MockData.lineStringWithPos3D(document, null));

        Curve line = (Curve) parse();
        PointArray seq = PointArrayUtil.toList(GML3MockData.gb,line);
        assertNotNull(line);

        assertEquals(1d, seq.get(0).getDirectPosition().getCoordinate()[0]);
        assertEquals(2d, seq.get(0).getDirectPosition().getCoordinate()[1]);
        assertEquals(10d, seq.get(0).getDirectPosition().getCoordinate()[2]);
        assertEquals(3d, seq.get(1).getDirectPosition().getCoordinate()[0]);
        assertEquals(4d, seq.get(1).getDirectPosition().getCoordinate()[1]);
        assertEquals(20d, seq.get(1).getDirectPosition().getCoordinate()[2]);
    }

    public void testPosList() throws Exception {
        document.appendChild(GML3MockData.lineStringWithPosList(document, null));

        Curve line = (Curve) parse();
        assertNotNull(line);

        PointArray seq = PointArrayUtil.toList(GML3MockData.gb,line);
        
        assertEquals(1.0, seq.get(0).getDirectPosition().getCoordinate()[0]);
        assertEquals(2.0, seq.get(0).getDirectPosition().getCoordinate()[1]);
        assertEquals(3.0, seq.get(1).getDirectPosition().getCoordinate()[0]);
        assertEquals(4.0, seq.get(1).getDirectPosition().getCoordinate()[1]);
    }
    
    public void testPosList3D() throws Exception {
        document.appendChild(GML3MockData.lineStringWithPosList3D(document, null));

        Curve line = (Curve) parse();
        assertNotNull(line);

        PointArray seq = PointArrayUtil.toList(GML3MockData.gb,line);
        
        assertEquals(1d, seq.get(0).getDirectPosition().getCoordinate()[0]);
        assertEquals(2d, seq.get(0).getDirectPosition().getCoordinate()[1]);
        assertEquals(10d, seq.get(0).getDirectPosition().getCoordinate()[2]);
        assertEquals(3d, seq.get(1).getDirectPosition().getCoordinate()[0]);
        assertEquals(4d, seq.get(1).getDirectPosition().getCoordinate()[1]);
        assertEquals(20d, seq.get(1).getDirectPosition().getCoordinate()[2]);
    }
    
    /**
     * Tests encoding using a CoordinateArraySequence
     * (which requires special logic to get the dimension correct)
     * @throws Exception
     */
    public void testEncodeLineString() throws Exception {
    	Curve line = GML3MockData.lineString();
        Document doc = encode(line, GML.LineString);
        PointArray seq = PointArrayUtil.toList(GML3MockData.gb,line);
        checkDimension(doc, GML.LineString.getLocalPart(), 2);
        checkPosListOrdinates(doc, 2 * seq.size());
    }
    
    
    
  
    

}
