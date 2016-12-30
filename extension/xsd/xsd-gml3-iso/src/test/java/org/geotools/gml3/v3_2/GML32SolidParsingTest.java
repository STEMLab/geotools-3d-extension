/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2016, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gml3.v3_2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.geotools.data.DataUtilities;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.xml.Parser;
import org.geotools.xml.StreamingParser;
import org.opengis.feature.simple.SimpleFeature;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Point;

import junit.framework.TestCase;

/**
 * @author Donguk Seo
 *
 */
public class GML32SolidParsingTest extends TestCase {
    public void testWithoutSchema() throws Exception {
        InputStream in = getClass().getResourceAsStream( "testSolid.xml");
        GMLConfiguration gml = new GMLConfiguration(true);
        StreamingParser parser = new StreamingParser( gml, in, SimpleFeature.class );
        
        int nfeatures = 0;
        SimpleFeature f = null;
        while( ( f = (SimpleFeature) parser.parse() ) != null ) {
            nfeatures++;
            assertNotNull( f.getAttribute( "the_geom"));
        }
        
        assertEquals( 2, nfeatures );
    }
    
    public void testWithSchema() throws Exception {
        File schema = File.createTempFile("testSolid", "xsd");
        schema.deleteOnExit();
        FileUtils.copyURLToFile(getClass().getResource("testSolid.xsd"), schema);
        
        Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
            getClass().getResourceAsStream( "testSolid.xml" )   
        );
        URL schemaURL = DataUtilities.fileToURL( schema.getAbsoluteFile() );        
        dom.getDocumentElement().setAttribute( "xsi:schemaLocation", "http://www.geotools.org/test " + schemaURL.getFile() );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TransformerFactory.newInstance().newTransformer().transform( 
            new DOMSource( dom ), new StreamResult( out ) );
        
        GMLConfiguration config = new GMLConfiguration(true);
        Parser p = new Parser( config );
        Object o = p.parse( new ByteArrayInputStream( out.toByteArray() ) );
        assertTrue( o instanceof FeatureCollection );
        
        FeatureCollection features = (FeatureCollection) o;
        assertEquals( 2, features.size() );
        
        FeatureIterator fi = features.features();
        try {
            for ( int i = 0; i < 2; i++ ) {
                assertTrue( fi.hasNext() );
                
                SimpleFeature f = (SimpleFeature) fi.next();
                assertNotNull( f.getAttribute( "the_geom" ) );
            }
        }
        finally {
            fi.close();
        }
    }

}
