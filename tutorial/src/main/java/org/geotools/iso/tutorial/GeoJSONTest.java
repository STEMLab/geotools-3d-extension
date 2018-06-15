package org.geotools.iso.tutorial;

import java.io.File;

import javax.swing.JFileChooser;

import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.iso.data.geojson.GeoJSONDataStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * @author hgryoo
 *
 */
public class GeoJSONTest {
	
	public static void main(String[] args) throws Exception {
		JFileChooser fileDlg = new JFileChooser();
	    int result = fileDlg.showOpenDialog(null);
        if(result == JFileChooser.APPROVE_OPTION){
        	File f = fileDlg.getSelectedFile();
    		GeoJSONDataStore fds = new GeoJSONDataStore(f.toURL());
    		String type = fds.getNames().get(0).getLocalPart();
    		Query query = new Query(type);
    		FeatureReader<SimpleFeatureType, SimpleFeature> reader = fds.getFeatureReader(query, null);
    		SimpleFeatureType schema = reader.getFeatureType();
    		int count = 0;
    		while (reader.hasNext()) {
    			SimpleFeature next = reader.next();
    			System.out.println(next);
    			count++;
    		}
        }
	}

}