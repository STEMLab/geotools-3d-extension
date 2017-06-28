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
import org.geotools.gml3.iso.GML3TestSupport;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Ring;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;


/**
 * 
 *
 * @source $URL$
 */
public class LinearRingTypeBindingTest extends GML3TestSupport {
    
    public void testPos() throws Exception {
        document.appendChild(GML3MockData.linearRingWithPos(document, null));

        Ring line = (Ring) parse();
        assertNotNull(line);
        
        PointArray seq = PointArrayUtil.toList(GML3MockData.gb,line);

        assertEquals(1d, seq.get(0).getDirectPosition().getCoordinate()[0]);
        assertEquals(2d, seq.get(0).getDirectPosition().getCoordinate()[1]);
        assertEquals(3d, seq.get(1).getDirectPosition().getCoordinate()[0]);
        assertEquals(4d, seq.get(1).getDirectPosition().getCoordinate()[1]);
        assertEquals(5d, seq.get(2).getDirectPosition().getCoordinate()[0]);
        assertEquals(2d, seq.get(2).getDirectPosition().getCoordinate()[1]);
        assertEquals(1d, seq.get(3).getDirectPosition().getCoordinate()[0]);
        assertEquals(2d, seq.get(3).getDirectPosition().getCoordinate()[1]);
        
        
    }

    public void testPosList() throws Exception {
        document.appendChild(GML3MockData.linearRingWithPosList(document, null));

        Ring line = (Ring) parse();
        assertNotNull(line);

        PointArray seq = PointArrayUtil.toList(GML3MockData.gb,line);
        
        assertEquals(1d, seq.get(0).getDirectPosition().getCoordinate()[0]);
        assertEquals(2d, seq.get(0).getDirectPosition().getCoordinate()[1]);       
        assertEquals(3d, seq.get(1).getDirectPosition().getCoordinate()[0]);
        assertEquals(4d, seq.get(1).getDirectPosition().getCoordinate()[1]);       
        assertEquals(5d, seq.get(2).getDirectPosition().getCoordinate()[0]);
        assertEquals(2d, seq.get(2).getDirectPosition().getCoordinate()[1]);     
        assertEquals(1d, seq.get(3).getDirectPosition().getCoordinate()[0]);
        assertEquals(2d, seq.get(3).getDirectPosition().getCoordinate()[1]);
       
    }
    
    public void testPos3D() throws Exception {
        document.appendChild(GML3MockData.linearRingWithPos3D(document, null, true));

        Ring line = (Ring) parse();
        assertNotNull(line);

        PointArray seq = PointArrayUtil.toList(GML3MockData.gb,line);
        
        assertEquals(1d, seq.get(0).getDirectPosition().getCoordinate()[0]);
        assertEquals(2d, seq.get(0).getDirectPosition().getCoordinate()[1]);
        assertEquals(10d, seq.get(0).getDirectPosition().getCoordinate()[2]);
        assertEquals(3d, seq.get(1).getDirectPosition().getCoordinate()[0]);
        assertEquals(4d, seq.get(1).getDirectPosition().getCoordinate()[1]);
        assertEquals(20d, seq.get(1).getDirectPosition().getCoordinate()[2]);
        assertEquals(5d, seq.get(2).getDirectPosition().getCoordinate()[0]);
        assertEquals(2d, seq.get(2).getDirectPosition().getCoordinate()[1]);
        assertEquals(20d, seq.get(2).getDirectPosition().getCoordinate()[2]);
        assertEquals(1d, seq.get(3).getDirectPosition().getCoordinate()[0]);
        assertEquals(2d, seq.get(3).getDirectPosition().getCoordinate()[1]);
        assertEquals(10d, seq.get(3).getDirectPosition().getCoordinate()[2]);
    }

    public void testPosList3D() throws Exception {
        document.appendChild(GML3MockData.linearRingWithPosList3D(document, null, true));

        Ring line = (Ring) parse();
        assertNotNull(line);

PointArray seq = PointArrayUtil.toList(GML3MockData.gb,line);
        
        assertEquals(1d, seq.get(0).getDirectPosition().getCoordinate()[0]);
        assertEquals(2d, seq.get(0).getDirectPosition().getCoordinate()[1]);
        assertEquals(10d, seq.get(0).getDirectPosition().getCoordinate()[2]);
        assertEquals(3d, seq.get(1).getDirectPosition().getCoordinate()[0]);
        assertEquals(4d, seq.get(1).getDirectPosition().getCoordinate()[1]);
        assertEquals(20d, seq.get(1).getDirectPosition().getCoordinate()[2]);
        assertEquals(5d, seq.get(2).getDirectPosition().getCoordinate()[0]);
        assertEquals(2d, seq.get(2).getDirectPosition().getCoordinate()[1]);
        assertEquals(20d, seq.get(2).getDirectPosition().getCoordinate()[2]);
        assertEquals(1d, seq.get(3).getDirectPosition().getCoordinate()[0]);
        assertEquals(2d, seq.get(3).getDirectPosition().getCoordinate()[1]);
        assertEquals(10d, seq.get(3).getDirectPosition().getCoordinate()[2]);
    }
    
    
}
