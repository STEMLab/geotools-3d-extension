import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureWriter;
import org.geotools.data.ISODataUtilities;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.data.csv.iso.CSVDataStoreFactory;
import org.geotools.data.postgis3d.PostgisNGDataStoreFactory;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.feature.ISOFeatureFactoryImpl;
import org.geotools.feature.simple.ISOSimpleFeatureBuilder;
import org.geotools.feature.simple.ISOSimpleFeatureTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.FunctionFactory;
import org.geotools.filter.ISOFilterFactoryImpl;
import org.geotools.filter.function.ISODefaultFunctionFactory;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.iso.coordinate.DirectPositionImpl;
import org.geotools.geometry.iso.coordinate.PointArrayImpl;
import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.geometry.iso.util.SolidUtil;
//import org.geotools.gml2.GMLConfiguration_ISO;
import org.geotools.jdbc.iso.JDBCDataStore;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.action.SafeAction;
import org.geotools.swing.data.JDataStoreWizard;
import org.geotools.swing.table.FeatureCollectionTableModel;
import org.geotools.swing.wizard.JWizard;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.vividsolutions.jts.geom.MultiPolygon;


public class ExtruderTest2 extends JFrame{
	private DataStore dataStore;
	private JComboBox featureTypeCBox;
	private JTable table;
	private JTextField text;
	private static Hints hints = null;


	private static ISOGeometryBuilder builder;
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Hints h = new Hints();
		h.put(Hints.GEOMETRY_VALIDATE, false);
		h.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D);
		builder = new ISOGeometryBuilder(h);
		JFrame frame = new ExtruderTest2();
		frame.setVisible(true);
	}
	public ExtruderTest2() {
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
				connect(new CSVDataStoreFactory());
			}
		});
		/*fileMenu.add(new SafeAction("gmlToGeometry...") {
			public void action(ActionEvent e) throws Throwable {
				gmlToGeometry();
			}
		});*/

		fileMenu.addSeparator();
		fileMenu.add(new SafeAction("Exit") {
			public void action(ActionEvent e) throws Throwable {
				System.exit(0);
			}
		});
		dataMenu.add(new SafeAction("Get features") {
			public void action(ActionEvent e) throws Throwable {
				filterFeatures();
			}
		});
		dataMenu.add(new SafeAction("saveExtrudedPolygon"){
			public void action(ActionEvent e)throws Throwable{
				saveFeatures();
			}
		});
	}
	private void SolidToTable(ISOGeometryBuilder gb3D,List<Solid>solidList, List<String>idList, List<String>partIDList, List<Double>heightList,DataStore dataStore1, SimpleFeatureType schema) throws NoSuchAuthorityCodeException, FactoryException {
	
		ISOSimpleFeatureBuilder builder = new ISOSimpleFeatureBuilder(schema, new ISOFeatureFactoryImpl());
		
		try {							
				FeatureWriter<SimpleFeatureType, SimpleFeature> fw = dataStore1.getFeatureWriterAppend(
						schema.getTypeName(), Transaction.AUTO_COMMIT);	
										
				for(int i = 0 ; i < solidList.size(); i++){
					builder.add(idList.get(i));
					builder.add(partIDList.get(i));
					builder.add(solidList.get(i));
					SimpleFeature tempFeature = builder.buildFeature(null);
					SimpleFeature writtenFeature = fw.next();
					writtenFeature.setAttribute("building_id", tempFeature.getAttribute("building_id"));
					writtenFeature.setAttribute("part_id", tempFeature.getAttribute("part_id"));
					writtenFeature.setAttribute("geom", tempFeature.getAttribute("geom"));
					fw.write();
				}
				fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveFeatures() throws NoSuchAuthorityCodeException, FactoryException {
		String typeName = (String) featureTypeCBox.getSelectedItem();
		SimpleFeatureSource source;
		
		try {
			source = dataStore.getFeatureSource(typeName);
			Filter filter = CQL.toFilter(text.getText());
			SimpleFeatureCollection features = source.getFeatures(filter);
			FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
			DataStore dataStoreForFeatures;

			JDataStoreWizard wizard = new JDataStoreWizard(new PostgisNGDataStoreFactory());
			int result = wizard.showModalDialog();

			if (result == JWizard.FINISH) {
				SimpleFeatureType forSchema = source.getSchema();
				
				//set feature type
				ISOSimpleFeatureTypeBuilder featureTypeBuilder = new ISOSimpleFeatureTypeBuilder();
				featureTypeBuilder.setName("lotte_indoors");
				featureTypeBuilder.setCRS(CRS.decode("EPSG:4329"));
				featureTypeBuilder.length(150).add("id", String.class);
				featureTypeBuilder.add("geom", Solid.class);
				forSchema = featureTypeBuilder.buildFeatureType();
				
				Map<String, Object> connectionParameters = wizard.getConnectionParameters();
				dataStoreForFeatures = DataStoreFinder.getDataStore(connectionParameters);
				if (dataStoreForFeatures == null) {
					JOptionPane.showMessageDialog(null, "Could not connect - check parameters");
				}
				dataStoreForFeatures.createSchema(forSchema);
				
				// JDBCDataStore jds = (JDBCDataStore)dataStore1;
				// jds.setDatabaseSchema(null);

				// dataStore1.createSchema((SimpleFeatureType) schema);

				// SimpleFeatureType actualSchema =
				// dataStore1.getSchema(typeName);

				// insert the feature

				// SimpleFeature f = fw.next();
				SimpleFeatureCollection sfc = source.getFeatures();
				SimpleFeatureIterator iterator = sfc.features();
				FeatureWriter<SimpleFeatureType, SimpleFeature> fw = dataStoreForFeatures.getFeatureWriterAppend(
						forSchema.getTypeName(), Transaction.AUTO_COMMIT);
				while (iterator.hasNext()) {
					SimpleFeature feature = iterator.next();		
					SimpleFeature writtenFeature = fw.next();
					writtenFeature.setAttribute("id", feature.getAttribute("id"));
					writtenFeature.setAttribute("geom", feature.getAttribute("geom"));
					fw.write();
				}
				fw.close();
				table.setModel(model);
			}
		} catch (IOException | CQLException e) {
			// TODO Auto-generated catch blocka
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*private void gmlToGeometry() {
		/*GMLConfiguration configuration = new GMLConfiguration();
		InputStream input = getClass().getResourceAsStream("geometry.xml");
        String xpath = "/pointMember | /lineStringMember | /polygonMember";

        //String xpath = "/child::*";
        StreamingParser parser = new StreamingParser(configuration, input, xpath);
        Object o = parser.parse();//point
        o = parser.parse();//linestring
        o = parser.parse();//polygon
        
		try {
			File initialFile = new File("feature.xml");
		    InputStream in = new FileInputStream(initialFile);
	        //InputStream in = getClass().getResourceAsStream("feature.xml");
	
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setNamespaceAware(true);
	
	        Document document;
		
			document = factory.newDocumentBuilder().parse(in);
		

	        //update hte schema location
	        document.getDocumentElement().removeAttribute("xsi:schemaLocation");
	
	        //reserialize the document
	        File schemaFile = File.createTempFile("test", "xsd");
	        schemaFile.deleteOnExit();
	
	        Transformer tx = TransformerFactory.newInstance().newTransformer();
	        tx.transform(new DOMSource(document), new StreamResult(schemaFile));
	
	        in.close();
	        in = new FileInputStream(schemaFile);
			
	        GMLConfiguration_ISO configuration = new GMLConfiguration_ISO();
	        configuration.getProperties().add(Parser.Properties.IGNORE_SCHEMA_LOCATION);
	        configuration.getProperties().add(Parser.Properties.PARSE_UNKNOWN_ELEMENTS);
	
	        StreamingParser parser = new StreamingParser(configuration, in, "//TestFeature");
	
	        for (int i = 0; i < 3; i++) {
	            SimpleFeature f = (SimpleFeature) parser.parse();
	        }
		} catch (SAXException | IOException | ParserConfigurationException | TransformerFactoryConfigurationError | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	private void boxToSolid() {
		String typeName = "newFlag2";
		SimpleFeatureSource source;
		try {
			source = dataStore.getFeatureSource(typeName);
   			FeatureType schema = source.getSchema();
			//String name = schema.getGeometryDescriptor().getLocalName();
		
			Filter filter = CQL.toFilter(text.getText());
			Query query = new Query(typeName, filter, new String[] { "loc" });

			SimpleFeatureCollection features = source.getFeatures(query);

			FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
			table.setModel(model);
		} catch (IOException | CQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
	}
	private void connect(DataStoreFactorySpi format) {
		JDataStoreWizard wizard = new JDataStoreWizard(format);
		int result = wizard.showModalDialog();
		if (result == JWizard.FINISH) {
			Map<String, Object> connectionParameters = wizard.getConnectionParameters();
			try {
				dataStore = DataStoreFinder.getDataStore(connectionParameters);

				if (dataStore == null) {
					JOptionPane.showMessageDialog(null, "Could not connect - check parameters");
				}
				updateUI();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void insertTable() {
		String typeName = (String) featureTypeCBox.getSelectedItem();

		try {
			SimpleFeatureSource source = dataStore.getFeatureSource(typeName);
			//source = dataStore.getFeatureSource(typeName);

			FeatureType schema = dataStore.getSchema(typeName);//source.getSchema();
			DataStore dataStore1;
			JDataStoreWizard wizard = new JDataStoreWizard(new PostgisNGDataStoreFactory());
			int result = wizard.showModalDialog();
			if (result == JWizard.FINISH) {
				Map<String, Object> connectionParameters = wizard.getConnectionParameters();

				dataStore1 = DataStoreFinder.getDataStore(connectionParameters);

				if (dataStore1 == null) {
					JOptionPane.showMessageDialog(null, "Could not connect - check parameters");
				}
				JDBCDataStore jds = (JDBCDataStore)dataStore1;
				jds.setDatabaseSchema(null);
				SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
				builder.setName("geom");
				builder.setCRS(DefaultGeographicCRS.WGS84);
				builder.length(100).add("building_id",Character[].class);
				builder.add("geom",MultiPolygon.class);
				builder.add("height",Float.class);
				
				
				
				dataStore1.createSchema((SimpleFeatureType) schema);
				//SimpleFeatureType actualSchema = dataStore1.getSchema(typeName);
				
				// insert the feature
				FeatureWriter<SimpleFeatureType, SimpleFeature> fw = dataStore1.getFeatureWriterAppend(
						typeName, Transaction.AUTO_COMMIT);
				//SimpleFeature f = fw.next();
				SimpleFeatureCollection sfc = source.getFeatures();
				SimpleFeatureIterator iterator = sfc.features();
				while (iterator.hasNext()) {
					SimpleFeature feature = iterator.next();
					SimpleFeature newFeature = fw.next(); // new blank feature
					newFeature.setAttributes(feature.getAttributes());
					fw.write();
				}
				//fw.write();
				fw.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void updateUI()  {
		ComboBoxModel cbm;
		try {
			cbm = new DefaultComboBoxModel(dataStore.getTypeNames());
			featureTypeCBox.setModel(cbm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		table.setModel(new DefaultTableModel(5, 5));
	}
	
	private void filterFeatures()  {
		String typeName = (String) featureTypeCBox.getSelectedItem();
		SimpleFeatureSource source;
		try {
			source = dataStore.getFeatureSource(typeName);
			Filter filter = CQL.toFilter(text.getText());
			SimpleFeatureCollection features = source.getFeatures(filter);
			FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
			table.setModel(model);
			
			SimpleFeatureIterator iterator = features.features();
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				//System.out.println(feature.getAttribute("the_geom"));
				Solid s = SolidUtil.createSolidWithHeight(builder,(MultiPolygon)feature.getAttribute("the_geom"), (double)feature.getAttribute("BUILDING_H"));
				
				//SimpleFeature newFeature = fw.next(); // new blank feature
				//newFeature.setAttributes(feature.getAttributes());
				//fw.write();
			}
			
		} catch (IOException | CQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
	}
	private void countFeatures() throws Exception {
		String typeName = (String) featureTypeCBox.getSelectedItem();
		SimpleFeatureSource source = dataStore.getFeatureSource(typeName);

		Filter filter = CQL.toFilter(text.getText());
		SimpleFeatureCollection features = source.getFeatures(filter);

		int count = features.size();
		JOptionPane.showMessageDialog(text, "Number of selected features:" + count);
	}
	private void queryFeatures()  {
		String typeName = (String) featureTypeCBox.getSelectedItem();
		SimpleFeatureSource source;
		try {
			source = dataStore.getFeatureSource(typeName);
			FeatureType schema = source.getSchema();
			String name = schema.getGeometryDescriptor().getLocalName();

			Filter filter = CQL.toFilter(text.getText());

			Query query = new Query(typeName, filter, new String[] { name });

			SimpleFeatureCollection features = source.getFeatures(query);

			FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
			table.setModel(model);
		} catch (IOException | CQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 


	}
}
