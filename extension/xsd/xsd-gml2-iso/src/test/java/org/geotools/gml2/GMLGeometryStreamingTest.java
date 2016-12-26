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
package org.geotools.gml2;

import java.io.InputStream;

import org.geotools.xml.Configuration;
import org.geotools.xml.StreamingParser;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Surface;

import junit.framework.TestCase;

/**
 * 
 *
 * @source $URL$
 */
public class GMLGeometryStreamingTest extends TestCase {
    public void testStreamByXpath() throws Exception {
        Configuration configuration = new GMLConfiguration_ISO();
        InputStream input = getClass().getResourceAsStream("geometry.xml");
        String xpath = "/pointMember | /lineStringMember | /polygonMember";

        //String xpath = "/child::*";
        StreamingParser parser = new StreamingParser(configuration, input, xpath);

        makeAssertions(parser);
    }

    //    public void testStreamByType() throws Exception {
    //    	Configuration configuration = new GMLConfiguration();
    //    	InputStream input = getClass().getResourceAsStream("geometry.xml");
    //        StreamingParser parser = new StreamingParser(configuration, input , Geometry.class );
    //        
    //        makeAssertions( parser );
    //    }
    private void makeAssertions(StreamingParser parser) {
        Object o = parser.parse();
        assertNotNull(o);
        assertTrue(o instanceof Point);

        o = parser.parse();
        assertNotNull(o);
        assertTrue(o instanceof Curve);

        o = parser.parse();
        assertNotNull(o);
        assertTrue(o instanceof Surface);

        o = parser.parse();
        assertNull(o);
    }
}
