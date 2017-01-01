/**
 * 
 */
package org.geotools.gml3.iso;

import java.io.InputStream;

import org.geotools.factory.Hints;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.PullParser;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.ISOGeometryBuilder;

/**
 * @author hgryoo
 *
 */
public class GML3SolidFeatureParsingTest {

	@Test
	public void testWithoutSchema() throws Exception {
		InputStream in = getClass().getResourceAsStream("testSolid.xml");
		GMLConfiguration_ISO gml = new GMLConfiguration_ISO();
		PullParser parser = new PullParser(gml, in, SimpleFeature.class);
		
		SimpleFeature f = null;

		while((f = (SimpleFeature) parser.parse()) != null) {
			System.out.println(f);
		}
	}

}
