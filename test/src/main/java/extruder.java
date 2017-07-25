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
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.data.csv.iso.CSVDataStoreFactory;
//import org.geotools.data.kairos.KairosNGDataStoreFactory;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.data.postgis3d.PostgisNGDataStoreFactory;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.feature.ISOFeatureFactoryImpl;
import org.geotools.feature.simple.ISOSimpleFeatureTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.ISOFilterFactoryImpl;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.iso.coordinate.DirectPositionImpl;
import org.geotools.geometry.iso.coordinate.PointArrayImpl;
import org.geotools.geometry.iso.primitive.PointImpl;
import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
//import org.geotools.gml2.GMLConfiguration_ISO;
import org.geotools.jdbc.iso.JDBCDataStore;
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
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

import com.vividsolutions.jts.geom.MultiPolygon;


public class extruder extends JFrame{
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
		JFrame frame = new extruder();
		frame.setVisible(true);
	}
	public SimpleFeatureCollection getFeatures(){
		
		ShapefileDataStoreFactory shpDataFactory = new ShapefileDataStoreFactory();
		connect(shpDataFactory);
		
		
		return null;
		
	}
	public extruder() {
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
		fileMenu.add(new SafeAction("Open shapefile...") {
			public void action(ActionEvent e) throws Throwable {
				connect(new ShapefileDataStoreFactory());
			}
		});
		fileMenu.add(new SafeAction("Connect to PostGIS database...") {
			public void action(ActionEvent e) throws Throwable {
				connect(new PostgisNGDataStoreFactory());
				System.out.println("Connection succeeded");
			}
		});
		fileMenu.add(new SafeAction("Connect to Kairos database...") {
			public void action(ActionEvent e) throws Throwable {
				//connect(new KairosNGDataStoreFactory());
				System.out.println("Connection succeeded");
			}
		});
		fileMenu.add(new SafeAction("Insert to PostGIS database...") {
			public void action(ActionEvent e) throws Throwable {
				insertTable();
			}
		});
		fileMenu.add(new SafeAction("pointToTable...") {
			public void action(ActionEvent e) throws Throwable {
				pointToTable();
			}
		});
		fileMenu.add(new SafeAction("boxToSolid...") {
			public void action(ActionEvent e) throws Throwable {
				boxToSolid();
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
		dataMenu.add(new SafeAction("contains solid") {
			public void action(ActionEvent e) throws Throwable {
				constainsfilter();
			}
		});
		dataMenu.add(new SafeAction("Count") {
			public void action(ActionEvent e) throws Throwable {
				countFeatures();
			}
		});
		dataMenu.add(new SafeAction("Geometry") {
			public void action(ActionEvent e) throws Throwable {
				queryFeatures();
			}
		});
	}
	public ArrayList<Solid> getSolids(ISOGeometryBuilder builder) {
		ArrayList<Solid> solids = new ArrayList<Solid>();
		ArrayList<ArrayList<DirectPosition>> solidPoints = getSolidPoints(builder);

		for (int i = 0; i < 9; i++) {
			solids.add(makeSolid(builder, solidPoints.get(i)));
		}

		return solids;
	}

	private void pointToTable() {
		String typeName = "newFlag2";
		//hints = GeoTools.getDefaultHints();
		//hints.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D);
		//hints.put(Hints.GEOMETRY_VALIDATE, false);
		//hints.put(Hints.COORDINATE_DIMENSION, 3);
		//builder = new GeometryBuilder(hints);
		ArrayList<Solid> al = getSolids(builder);

		//List<DirectPosition> l = new ArrayList<DirectPosition>();
		/*PointArray lp = new PointArrayImpl(new DirectPositionImpl(DefaultGeographicCRS.WGS84_3D,new double[]{0,0,0}),new DirectPositionImpl(DefaultGeographicCRS.WGS84_3D,new double[]{1,1,1}));
		for(int i = 2;i < 3;i++) {
			lp.add(new DirectPositionImpl(DefaultGeographicCRS.WGS84_3D,new double[]{i,i,i}));
		}
		lp.add(new DirectPositionImpl(DefaultGeographicCRS.WGS84_3D,new double[]{0,0,0}));
		Curve al = builder.createCurve(lp);
		SurfaceBoundary s = builder.createSurfaceBoundary(al);
		Surface sf = builder.createSurface(s);*/
		//Point al = new PointImpl(new DirectPositionImpl(DefaultGeographicCRS.WGS84_3D,new double[]{0,0,0}));
		ISOSimpleFeatureTypeBuilder b = new ISOSimpleFeatureTypeBuilder();
		b.setCRS(DefaultGeographicCRS.WGS84_3D);
		//b.userData(Hints.COORDINATE_DIMENSION, 3);
		//set the name
		b.setName( typeName );
		//add some properties
		//add a geometry property
		//b.setCRS( DefaultGeographicCRS.WSG84 );
		//b.add( "location", Solid.class );
		//b.add("loc", Point.class);

		b.add("loc", Solid.class);

		SimpleFeatureType schema = b.buildFeatureType();
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(schema, new ISOFeatureFactoryImpl());
		//builder.userData(Hints.COORDINATE_DIMENSION, 3);
		builder.add( al.get(0) );
		SimpleFeature feature = builder.buildFeature( "fid.1" );
		try {
			//source = dataStore.getFeatureSource(typeName);
			//DataStore dataStore1;
			//JDataStoreWizard wizard = new JDataStoreWizard(new PostgisNGDataStoreFactory());
			/*JDataStoreWizard wizard = new JDataStoreWizard(new CSVDataStoreFactory());
			int result = wizard.showModalDialog();
			if (result == JWizard.FINISH) {
				Map<String, Object> connectionParameters = wizard.getConnectionParameters();
				dataStore = DataStoreFinder.getDataStore(connectionParameters);
				if (dataStore == null) {
					JOptionPane.showMessageDialog(null, "Could not connect - check parameters");
				}*/
				//JDBCDataStore jds = (JDBCDataStore)dataStore1;
				//jds.setDatabaseSchema(null);
				dataStore.createSchema((SimpleFeatureType) schema);
				//SimpleFeatureType actualSchema = dataStore1.getSchema(typeName);
				FeatureWriter<SimpleFeatureType, SimpleFeature> fw = dataStore.getFeatureWriterAppend(
						schema.getTypeName(), Transaction.AUTO_COMMIT);
				//SimpleFeature f = fw.next();
				//SimpleFeatureCollection sfc = (SimpleFeatureCollection) feature;
				//SimpleFeatureIterator iterator = sfc.features();
				//while (iterator.hasNext()) {
				//   SimpleFeature features = iterator.next();
				SimpleFeature newFeature = fw.next(); // new blank feature
				//newFeature.setAttributes(features.getAttributes());
				newFeature.setAttributes(feature.getAttributes());
				fw.write();
				//}
				//fw.write();
				fw.close();
				/*updateUI();
				String name = schema.getGeometryDescriptor().getLocalName();
				FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2( GeoTools.getDefaultHints() );
				Filter filter = ff.contains( ff.property( "loc"), ff.literal( feature.getDefaultGeometry() ) );
				Query query = new Query(typeName, filter, new String[] { name });
				SimpleFeatureSource source = dataStore.getFeatureSource(typeName);
				SimpleFeatureCollection features = source.getFeatures(query);

				FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
				table.setModel(model);*/
			//}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
	private void constainsfilter() {
		String typeName = (String) featureTypeCBox.getSelectedItem();;
		SimpleFeatureSource source;
		try {
			
			source = dataStore.getFeatureSource(typeName);
   			FeatureType schema = source.getSchema();
			//String name = schema.getGeometryDescriptor().getLocalName();
   			PointArray lp = new PointArrayImpl(new DirectPositionImpl(DefaultGeographicCRS.WGS84_3D,new double[]{0,0,0}),new DirectPositionImpl(DefaultGeographicCRS.WGS84_3D,new double[]{1,1,1}));
   			for(int i = 2;i < 3;i++) {
   				lp.add(new DirectPositionImpl(DefaultGeographicCRS.WGS84_3D,new double[]{i,i,i}));
   			}
   			lp.add(new DirectPositionImpl(DefaultGeographicCRS.WGS84_3D,new double[]{0,0,0}));
   			Curve al = builder.createCurve(lp);
   			SurfaceBoundary s = builder.createSurfaceBoundary(al);
   			Surface sf = builder.createSurface(s);
			//Filter filter = CQL.toFilter(text.getText());
   			Hints h = new Hints();
   			h.put(Hints.FILTER_FACTORY, ISOFilterFactoryImpl.class);
   			FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(h);
   		    //Envelope bbox = new ReferencedEnvelope3D(-1, 1, -1, 1, -1, 1, DefaultGeographicCRS.WGS84 );
   			ISOGeometryBuilder gb = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84);
   			//ArrayList<Solid> al = getSolids(builder);
   			Filter filter = ff.contains("loc", (Geometry)sf);
			Query query = new Query(typeName, filter, new String[] { "loc" });

			SimpleFeatureCollection features = source.getFeatures(query);

			FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
			table.setModel(model);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
	}
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
	
	private void insertTableWithJTS() {
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
		System.out.println(typeName);
		SimpleFeatureSource source;
		try {
			source = dataStore.getFeatureSource(typeName);
			Filter filter = CQL.toFilter(text.getText());
			SimpleFeatureCollection features = source.getFeatures(filter);
			FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
			
			
			SimpleFeatureIterator iterator=features.features();
			 try {
			     while( iterator.hasNext()  ){
			          SimpleFeature feature = iterator.next();
			          MultiPolygon tempMultiPolygon = (MultiPolygon) feature.getAttribute("the_geom");
			          
			          float tempHeight = (float) feature.getAttribute("PART_HEIGH");
			          
			          
			     }
			 }
			 finally {
			     iterator.close();
			 }
			
			table.setModel(model);
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
