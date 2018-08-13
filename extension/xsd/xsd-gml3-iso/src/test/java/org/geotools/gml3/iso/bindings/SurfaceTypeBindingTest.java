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

import java.util.List;

import org.geotools.gml3.iso.GML;
import org.geotools.gml3.iso.GML3TestSupport;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;


/**
 * 
 *
 * @source $URL$
 */
public class SurfaceTypeBindingTest extends GML3TestSupport {
    
    @Override
    protected boolean enableExtendedArcSurfaceSupport() {
        return false;
    }

    public void testSurface3D() throws Exception {
        GML3MockData.surface3D(document, document, true);

        Surface polygon = (Surface) parse();
        assertNotNull(polygon);
        
        SurfaceBoundary sb = polygon.getBoundary();
        Ring exterior = sb.getExterior();
        //TODO : test
        
        List<Ring> interior = sb.getInteriors();
        assertEquals(interior.size(), 1);
        
        /*
        LineString exterior = polygon.getExteriorRing();
        assertTrue(new Coordinate(1d, 2d, 10d).equals3D(exterior.getCoordinateN(0)));
        LineString interior = polygon.getInteriorRingN(0);
        assertTrue(new Coordinate(1d, 2d, 10d).equals3D(interior.getCoordinateN(0)));
        */
    }
    
    //Curved polygon is not supported yet
    /*public void testEncodeCurved() throws Exception {
        Polygon poly = GML3MockData.curvePolygon();
        Document doc = encode(poly, GML.Polygon);
        print(doc);
    }*/

}
