import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import javax.swing.JFrame;

import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.geojson.GeoJSONDataStore;
import org.geotools.factory.Hints;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.test.TestData;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.primitive.Point;

/**
 * 
 */

/**
 * @author hgryoo
 *
 */
public class GeoJSONTest {
	
	public static void main(String[] args) throws Exception {
		URL url = TestData.url(GeoJSONDataStore.class, "featureCollection.json");
		// URL url = new
		// URL("http://geojson.xyz/naturalearth-3.3.0/ne_110m_admin_1_states_provinces.geojson");
		GeoJSONDataStore fds = new GeoJSONDataStore(url);
		String type = fds.getNames().get(0).getLocalPart();
		Query query = new Query(type);
		FeatureReader<SimpleFeatureType, SimpleFeature> reader = fds.getFeatureReader(query, null);
		SimpleFeatureType schema = reader.getFeatureType();
		//System.out.println(schema);
		assertTrue(Point.class.isAssignableFrom(schema.getGeometryDescriptor().getType().getBinding()));
		assertNotNull(schema);
		int count = 0;
		while (reader.hasNext()) {
			SimpleFeature next = reader.next();
			//System.out.println(next.getAttribute("name"));
			count++;
		}
		assertEquals(7, count);
	}

}