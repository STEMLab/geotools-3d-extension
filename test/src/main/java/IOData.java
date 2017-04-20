import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.data.csv.iso.CSVDataStoreFactory;
import org.geotools.data.kairos.KairosNGDataStoreFactory;
import org.geotools.data.postgis3d.PostgisNGDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.visitor.UniqueVisitor;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.action.SafeAction;
import org.geotools.swing.data.JDataStoreWizard;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.swing.table.FeatureCollectionTableModel;
import org.geotools.swing.wizard.JWizard;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.ISOGeometryBuilder;

import com.vividsolutions.jts.geom.GeometryFactory;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
public class IOData extends JFrame{
	private static DataStore dataStore;
	static SimpleFeatureType TYPE; 
	private JComboBox featureTypeCBox;
	private JTable table;
	private JTextField text;
	private static File file;
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
	 public static void main(String[] args) throws Exception {
			// TODO Auto-generated method stub
			Hints h = new Hints();
			h.put(Hints.GEOMETRY_VALIDATE, false);
			h.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D);
		
			JFrame frame = new IOData();
			frame.setVisible(true);
		}
		public IOData() {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			getContentPane().setLayout(new BorderLayout());

			text = new JTextField(80);
			text.setText("include"); // include selects everything!
			getContentPane().add(text, BorderLayout.NORTH);

			table = new JTable();
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setModel(new DefaultTableModel(5, 5));
			table.setPreferredScrollableViewportSize(new Dimension(500, 200));

			JScrollPane scrollPane = new JScrollPane(table);
			getContentPane().add(scrollPane, BorderLayout.CENTER);

			JMenuBar menubar = new JMenuBar();
			setJMenuBar(menubar);

			JMenu fileMenu = new JMenu("File");
			menubar.add(fileMenu);

			featureTypeCBox = new JComboBox();
			menubar.add(featureTypeCBox);

			JMenu dataMenu = new JMenu("Data");
			menubar.add(dataMenu);
			pack();
			fileMenu.add(new SafeAction("Open csvfile...") {
				public void action(ActionEvent e) throws Throwable {
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					file = JFileDataStoreChooser.showOpenFile("csv", null);
			        if (file == null) {
			            return;
			        }
				}
			});
			fileMenu.add(new SafeAction("Connect to PostGIS database...") {
				public void action(ActionEvent e) throws Throwable {
					connect(new PostgisNGDataStoreFactory());
					System.out.println("Connection succeeded");
				}
			});

			fileMenu.add(new SafeAction("Insert to PostGIS database...") {
				public void action(ActionEvent e) throws Throwable {
					insertTable();
				}
			});
			fileMenu.add(new SafeAction("create separate table...") {
				public void action(ActionEvent e) throws Throwable {
					SimpleFeatureType cTYPE = DataUtilities.createType("correctData",
			                // "the_geom:Point:srid=4326," + // <- the geometry attribute: Point type
			         		"zoneName:String," + 
			         		"user_no:Integer," + 
			         		"companyName:String," + 
			         		"inout:String," +   
			                 "timestamp:Date"   // a number attribute
			         );
		   			SimpleFeatureType eTYPE = DataUtilities.createType("errorData",
			                // "the_geom:Point:srid=4326," + // <- the geometry attribute: Point type
			         		"zoneName:String," + 
			         		"user_no:Integer," + 
			         		"companyName:String," + 
			         		"inout:String," +   
			                 "timestamp:Date"   // a number attribute
			         );
		   			SimpleFeatureBuilder cfeatureBuilder = new SimpleFeatureBuilder(cTYPE);
		   			SimpleFeatureBuilder efeatureBuilder = new SimpleFeatureBuilder(eTYPE);
	   				dataStore.createSchema(cfeatureBuilder.getFeatureType());
	   				dataStore.createSchema(efeatureBuilder.getFeatureType());
				}
			});
			fileMenu.add(new SafeAction("separate error...") {
				public void action(ActionEvent e) throws Throwable {
					SeparateError();
				}
			});

			
		}
		public void SeparateError() throws SchemaException{
			String typeName = "ZonData";
			SimpleFeatureSource source;
			try {
				source = dataStore.getFeatureSource(typeName);
	   			//FeatureType schema = source.getSchema();
				//String name = schema.getGeometryDescriptor().getLocalName();
				
	   	        FeatureWriter<SimpleFeatureType, SimpleFeature> cfw = dataStore.getFeatureWriterAppend(
	   	        		"correctData", Transaction.AUTO_COMMIT);
	   	     FeatureWriter<SimpleFeatureType, SimpleFeature> efw = dataStore.getFeatureWriterAppend(
	   	        		"errorData", Transaction.AUTO_COMMIT);
				Filter filter ;
				//Query query = new Query(typeName, filter, new String[] { "loc" });
				
				//SimpleFeatureCollection features = source.getFeatures();
				Query query = new Query(typeName, Filter.INCLUDE, new String[] { "user_no" });
				UniqueVisitor visitor = new UniqueVisitor("user_no");
				SimpleFeatureCollection collection = source.getFeatures( query );
				collection.accepts(visitor, null);
				Set<String> set = (Set<String>) visitor.getResult().getValue();
				Object[] al = set.toArray();
				for(int i = 0;i < al.length;i++) {
					int uid = (int) al[i]; 
					filter = CQL.toFilter("user_no = " + uid);
					query = new Query(typeName, filter, new String[] { "zoneName","user_no","companyName","inout","timestamp" });
					collection = source.getFeatures( query );
					SimpleFeatureIterator iterator = collection.features();
					while (iterator.hasNext()) {
						SimpleFeature feature = iterator.next();
						
						if(iterator.hasNext()) {
							SimpleFeature nfeature = iterator.next();
							if(isValidPair(feature, nfeature)){
								SimpleFeature newFeature = cfw.next(); // new blank feature
								newFeature.setAttributes(feature.getAttributes());
								cfw.write();
								//transaction.commit();
								newFeature = cfw.next(); // new blank feature
								newFeature.setAttributes(nfeature.getAttributes());
								cfw.write();
								//transaction.commit();
							}else{
								SimpleFeature newFeature = efw.next(); // new blank feature
								newFeature.setAttributes(feature.getAttributes());
								efw.write();
								//transaction.commit();
								newFeature = efw.next(); // new blank feature
								newFeature.setAttributes(nfeature.getAttributes());
								efw.write();
								//transaction.commit();
							}
							
						}else{//not pair
							SimpleFeature newFeature = efw.next(); // new blank feature
							newFeature.setAttributes(feature.getAttributes());
							efw.write();
							//transaction.commit();
						}
						//transaction.close();
						
					}
					iterator.close();
				}
				efw.close();
				cfw.close();
				System.out.println("finish");
				//FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
				//table.setModel(model);
			} catch (IOException | CQLException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
				e.printStackTrace();
			} 
		}
		public boolean isValidPair(SimpleFeature firstFeature,SimpleFeature secondFeature){
			boolean start = compare(firstFeature.getAttribute("inout").toString(),"진입",Strength.Primary);
			boolean end = compare(secondFeature.getAttribute("inout").toString(),"진출",Strength.Primary);
			boolean isSameZone=true;
			if(start&&end){
				isSameZone = compare(firstFeature.getAttribute("zoneName").toString(),secondFeature.getAttribute("zoneName").toString(),Strength.Primary);
			}
			
			return start&&end&&isSameZone;
		}
		public static void connect(DataStoreFactorySpi format){
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
		}
	public static void insertTable() throws SchemaException {
		// TODO Auto-generated method stub
		//

        
        TYPE = DataUtilities.createType("ZonData",
                // "the_geom:Point:srid=4326," + // <- the geometry attribute: Point type
         		"zoneName:String," + 
         		"user_no:Integer," + 
         		"companyName:String," + 
         		"inout:String," +   
                 "timestamp:Date"   // a number attribute
         );

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
