package org.geotools.gml3.iso;

import java.io.InputStream;

import org.geotools.xml.PullParser;
import org.opengis.feature.simple.SimpleFeature;

import junit.framework.TestCase;

/**
 * 
 *
 * @source $URL$
 */
public class GML3SolidFeatureParsingTest extends TestCase {

    public void testWithoutSchema() throws Exception {
        InputStream in = getClass().getResourceAsStream( "testSolid.xml");
        TestSolidConfiguration gml = new TestSolidConfiguration();
        PullParser parser = new PullParser( gml, in, SimpleFeature.class );
        
        int nfeatures = 0;
        SimpleFeature f = null;
        while( ( f = (SimpleFeature) parser.parse() ) != null ) {
            nfeatures++;
            System.out.println(f.getDefaultGeometry());
        }
        
        //assertEquals( 49, nfeatures );
    }
    
/*    public void testWithSchema() throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        
        //copy the schema to a temporary file
        File xsd = new File( "target/states.xsd");
        //xsd.deleteOnExit();
        
        Document schema = db.parse( getClass().getResourceAsStream( "states.xsd" ));
        Transformer tx = TransformerFactory.newInstance().newTransformer();
        tx.transform( new DOMSource( schema ) , new StreamResult( xsd ) );
        
        //update the schemaLocation to point at the schema
        Document instance = db.parse( getClass().getResourceAsStream( "states.xml"));
        instance.getDocumentElement().setAttribute( "schemaLocation", 
            "http://www.openplans.org/topp target/states.xsd");
        
        File xml = new File( "target/states.xml");
        //xml.deleteOnExit();
        tx.transform( new DOMSource( instance ), new StreamResult( xml ) );
        
        InputStream in = new FileInputStream( xml );
        GMLConfiguration_ISO gml = new GMLConfiguration_ISO();
        StreamingParser parser = new StreamingParser( gml, in, SimpleFeature.class );
        
        int nfeatures = 0;
        SimpleFeature f = null;
        while( ( f = (SimpleFeature) parser.parse() ) != null ) {
            nfeatures++;
            assertNotNull( f.getAttribute( "STATE_NAME"));
            assertNotNull( f.getAttribute( "STATE_ABBR"));
            assertTrue( f.getAttribute( "SAMP_POP") instanceof Double );
        }
        
        assertEquals( 49, nfeatures );
    }
    
    public void testParse3D() throws Exception {
        Parser p = new Parser(new GMLConfiguration_ISO());
        Object g = p.parse(GML3SolidFeatureParsingTest.class.getResourceAsStream("polygon3d.xml"));
        assertThat(g, instanceOf(Polygon.class));

        Polygon polygon = (Polygon) g;
        assertEquals(3, CoordinateSequences.coordinateDimension(polygon));
        Geometry expected = new WKTReader().read(
                "POLYGON((94000 471000 10, 94001 471000 11, 94001 471001 12, 94000 471001 13, 94000 471000 10))");
        assertTrue(CoordinateSequences.equalsND(expected, polygon));
    }*/
}
