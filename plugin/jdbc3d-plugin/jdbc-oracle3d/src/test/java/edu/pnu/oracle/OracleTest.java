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
package edu.pnu.oracle;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.xsd.XSDSchema;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.gml3.GML3D;
import org.geotools.gml3.GMLConfiguration3D;
import org.geotools.jdbc3d.JDBCDataStore;
import org.geotools.xml.Encoder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author hgryoo
 *
 */
public class OracleTest {
    ContentFeatureSource featureSource;
    
    protected Map<String, Object> createProperties() {
        Map<String, Object> fixture = new HashMap<String, Object>();
        fixture.put("driver", "oracle.jdbc.driver.OracleDriver");
        fixture.put("url", "jdbc:oracle:thin:@localhost:1521:orcl");
        fixture.put("host", "localhost");
        fixture.put("port", "1521");
        fixture.put("database", "orcl");
        fixture.put("username", "system");
        fixture.put("user", "system");
        fixture.put("password", "stem9987");
        fixture.put("passwd", "stem9987");
        fixture.put("dbtype", "Oracle" );
        return fixture;
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws SAXException, ParserConfigurationException, TransformerException {
        try {
            JDBCDataStore dataStore = (JDBCDataStore) DataStoreFinder.getDataStore( createProperties() );
            
            FeatureSource<SimpleFeatureType, SimpleFeature> fs = dataStore.getFeatureSource("ROOM");
            
            FeatureCollection fc = fs.getFeatures();
            FeatureIterator it = fc.features();
            /*while(it.hasNext()) {
                Feature f = (Feature) it.next();
                System.out.println(f);
            }*/
            
            GMLConfiguration3D conf = new GMLConfiguration3D();
            conf.getProperties().add(GMLConfiguration3D.NO_FEATURE_BOUNDS);

            XSDSchema schema = GML3D.getInstance().getSchema();
            
            Encoder encoder = new Encoder(conf, schema);
            
            FileOutputStream fos = new FileOutputStream("test2.gml", true);
            Document dom = encoder.encodeAsDOM(fc, GML3D.FeatureCollection);
            
            printDocument(dom, fos);
            
            /*File f = new File("test.gml");
            
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder =
            factory.newDocumentBuilder();
            Document document = builder.parse(f);
            */
            System.out.println();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), 
             new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }


}
