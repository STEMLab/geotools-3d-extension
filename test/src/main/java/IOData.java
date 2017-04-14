import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.postgis3d.PostgisNGDataStoreFactory;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.swing.data.JDataStoreWizard;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.swing.wizard.JWizard;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
public class IOData {
	private static DataStore dataStore;
	private enum Strength {
	    Primary(Collator.PRIMARY), //base char
	    Secondary(Collator.SECONDARY), //base char + accent
	    Tertiary(Collator.TERTIARY), // base char + accent + case
	    Identical(Collator.IDENTICAL); //base char + accent + case + bits
	    
	    int getStrength() { return fStrength; }
	    
	    private int fStrength;
	    private Strength(int aStrength){
	      fStrength = aStrength;
	    }
	  }
	 private static boolean compare(String aThis, String aThat, Strength aStrength){
		    Collator collator = Collator.getInstance(Locale.FRANCE);
		    collator.setStrength(aStrength.getStrength());
		    int comparison = collator.compare(aThis, aThat);
		    if ( comparison == 0 ) {
		    	return true;
		      //log("Collator sees them as the same : " + aThis + ", " + aThat + " - " + aStrength);
		    }
		    return false;
		  }
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, SchemaException {
		// TODO Auto-generated method stub
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        File file = JFileDataStoreChooser.showOpenFile("csv", null);
        if (file == null) {
            return;
        }
        final SimpleFeatureType TYPE = DataUtilities.createType("ZonData",
               // "the_geom:Point:srid=4326," + // <- the geometry attribute: Point type
        		"zoneName:String," + 
        		"user_no:Integer," + 
        		"companyName:String," + 
        		"inout:String," +   
                "timestamp:Date"   // a number attribute
        );
        //System.out.println("TYPE:"+TYPE);
        JDataStoreWizard wizard = new JDataStoreWizard(new PostgisNGDataStoreFactory());
		int result = wizard.showModalDialog();
		if (result == JWizard.FINISH) {
			Map<String, Object> connectionParameters = wizard.getConnectionParameters();
			try {
				dataStore = DataStoreFinder.getDataStore(connectionParameters);

				if (dataStore == null) {
					JOptionPane.showMessageDialog(null, "Could not connect - check parameters");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
 List<SimpleFeature> features = new ArrayList<>();
        
        /*
         * GeometryFactory will be used to create the geometry attribute of each feature,
         * using a Point object for the location.
         */
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
        try {
			dataStore.createSchema(featureBuilder.getFeatureType());
		
        FeatureWriter<SimpleFeatureType, SimpleFeature> fw = dataStore.getFeatureWriterAppend(
        		"ZonData", Transaction.AUTO_COMMIT);
        InputStreamReader in = new InputStreamReader(new FileInputStream(file), "euc-kr");
        //BufferedReader reader = new BufferedReader(in);
        try (BufferedReader reader = new BufferedReader(in) ){
            /* First line of the data file is the header */
            String line = reader.readLine();
            System.out.println("Header: " + line);
            
            for (line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.trim().length() > 0) { // skip blank lines
                    String tokens[] = line.split("\\,");
                    String name = tokens[1].trim();
                    if(compare(name,"롯데월드몰",Strength.Primary)){
                    	//double latitude = Double.parseDouble(tokens[5]);
                        //double longitude = Double.parseDouble(tokens[6]);
                        String zomename = tokens[4].trim();
                        String inout = tokens[7].trim();
                        int user_no = Integer.parseInt(tokens[0].trim());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        
                        Date time = sdf.parse(tokens[8].trim());
                        java.sql.Date d1 = new java.sql.Date(time.getTime());
                        /* Longitude (= x coord) first ! */
                        //Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
                   
                        //featureBuilder.add(point);
                        featureBuilder.add(zomename);
                        featureBuilder.add(user_no);
                        featureBuilder.add(name);
                        featureBuilder.add(inout);
                        featureBuilder.add(d1);
                        SimpleFeature feature = featureBuilder.buildFeature(null);
                        
                        
    					SimpleFeature newFeature = fw.next(); // new blank feature
    					newFeature.setAttributes(feature.getAttributes());
    					fw.write();
        				
                    }
                    
                }
            }
        } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
}
