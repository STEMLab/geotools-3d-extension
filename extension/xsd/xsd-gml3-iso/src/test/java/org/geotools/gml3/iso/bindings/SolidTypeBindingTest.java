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
import org.opengis.geometry.complex.CompositeSurface;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;
import org.opengis.geometry.primitive.Surface;
import org.w3c.dom.Document;


/**
 * 
 *
 * @source $URL$
 */
public class SolidTypeBindingTest extends GML3TestSupport {
    
    @Override
    protected boolean enableExtendedArcSurfaceSupport() {
        return true;
    }

    public void testNoInterior() throws Exception {
        GML3MockData.Solid(document, document,false);
        
        Solid solid = (Solid)parse();

        
        assertNotNull(solid);
    }

    public void testWithInterior() throws Exception {
        GML3MockData.Solid(document, document, true);

        Solid solid = (Solid) parse();
        assertNotNull(solid);
        
        SolidBoundary sb = solid.getBoundary();
        CompositeSurface exterior = sb.getExterior();
        //TODO : test
        
        Shell[] interior = sb.getInteriors();
        assertEquals(interior.length, 1);
        
        /*
        LineString exterior = polygon.getExteriorRing();
        assertTrue(new Coordinate(1d, 2d, 10d).equals3D(exterior.getCoordinateN(0)));
        LineString interior = polygon.getInteriorRingN(0);
        assertTrue(new Coordinate(1d, 2d, 10d).equals3D(interior.getCoordinateN(0)));
        */
    }
    
     
    public void testEncode() throws Exception {
    	Surface poly = GML3MockData.polygon_3D();
        Document doc = encode(poly, GML.Polygon);
        
        checkDimension(doc, GML.Polygon.getLocalPart(), 3);
        //checkPosListOrdinates(doc, 3 * poly);
    }
    
    
    //Curved polygon is not supported yet
    /*public void testEncodeCurved() throws Exception {
        Polygon poly = GML3MockData.curvePolygon();
        Document doc = encode(poly, GML.Polygon);
        print(doc);
    }*/

}
