/**
 * 
 */
package org.geotools.iso.tutorial;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import javax.xml.namespace.QName;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.jaxb.CityGMLBuilder;
import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.module.Modules;
import org.citygml4j.model.module.citygml.CityGMLModuleType;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLInputFilter;
import org.citygml4j.xml.io.reader.CityGMLReader;
import org.citygml4j.xml.io.reader.FeatureReadMode;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.data.postgis3d.PostgisNGDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.Hints;
import org.geotools.feature.ISOFeatureFactoryImpl;
import org.geotools.feature.simple.ISOSimpleFeatureTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.ISOFilterFactoryImpl;
import org.geotools.geometry.iso.io.wkt.ParseException;
import org.geotools.geometry.iso.io.wkt.WKTReader;
import org.geotools.geometry.iso.util.JTSParser;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.jdbc.iso.JDBCDataStore;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeocentricCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.action.SafeAction;
import org.geotools.swing.data.JDataStoreWizard;
import org.geotools.swing.table.FeatureCollectionTableModel;
import org.geotools.swing.wizard.JWizard;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.LineSegment;
import org.opengis.geometry.primitive.Curve;

import com.vividsolutions.jts.geom.MultiPolygon;

/**
 * @author hgryoo
 *
 */
public class CityGML4JConvertorTest extends JFrame {
	
	private JComboBox featureTypeCBox;
	private JTable table;
	private JTextField text;
	private static ISOGeometryBuilder geomBuilder;
	
	private SimpleFeatureType schema;
	private List<SimpleFeature> features;
	private DataStore dataStore;
	private Map<String, Building> buildings = new HashMap<String, Building>(); 
	private Map<String, AbstractBoundarySurface> boundaries = new HashMap<String, AbstractBoundarySurface>();
	private Map<String, MultiPolygon> buildingMap = new HashMap<String, MultiPolygon>();
	
	public void importCityGML() throws Exception {
		
		CityGMLContext ctx = CityGMLContext.getInstance();
		CityGMLBuilder builder = ctx.createCityGMLBuilder();
		CityGMLInputFactory in = builder.createCityGMLInputFactory();
		in.setProperty(CityGMLInputFactory.FEATURE_READ_MODE, FeatureReadMode.SPLIT_PER_FEATURE);

		URL url = CityGML4JConvertorTest.class.getResource("CityGML_Building.gml");
		CityGMLReader reader = in.createCityGMLReader(new File(url.getPath()));
		reader = in.createFilteredCityGMLReader(reader, new CityGMLInputFilter() {
			// return true if you want to consume the CityGML feature
						// of the given qualified XML name, false otherwise
			public boolean accept(QName name) {
				return Modules.isModuleNamespace(name.getNamespaceURI(), CityGMLModuleType.BUILDING)
						&& (name.getLocalPart().equals("Building") || name.getLocalPart().contains("Surface"));
			}
		});
		
		int num = 0;
		while (reader.hasNext()) {
			Object obj = reader.nextFeature();
			
			if(obj instanceof Building) {
				Building b = (Building) obj;
				String id = b.getId();
				buildings.put(id, b);
			} else {
				AbstractBoundarySurface abs = (AbstractBoundarySurface) obj;
				String id = abs.getId();
				boundaries.put(id, abs);
			}
		}
		
		for(Building b : buildings.values()) {
			List<BoundarySurfaceProperty> list = b.getBoundedBySurface();
			List<AbstractBoundarySurface> surfaces = new ArrayList<AbstractBoundarySurface>();
			
			for(BoundarySurfaceProperty bsp : list) {
				String href = bsp.getHref();
				AbstractBoundarySurface surface = boundaries.get(href.replace("#", ""));
				surfaces.add(surface);
			}
			
			MultiPolygon mp = CityGML2JTS.toMultiPolygon(surfaces);
			buildingMap.put(b.getId(), mp);
			num++;
		}
		
		System.out.println("number of buildings : " + num);
		//System.out.println(buildingMap);
		reader.close();
		
		
		ISOSimpleFeatureTypeBuilder b = new ISOSimpleFeatureTypeBuilder();
		b.setCRS(DefaultGeographicCRS.WGS84_3D);
		b.setName("buildings");
		b.add("bid", String.class);
		b.add("geom", org.opengis.geometry.aggregate.MultiSurface.class);
		this.schema = b.buildFeatureType();
		SimpleFeatureBuilder fBuilder = new SimpleFeatureBuilder(this.schema, new ISOFeatureFactoryImpl());
		
		features = new ArrayList<SimpleFeature>();
		for(Map.Entry<String, MultiPolygon> e : buildingMap.entrySet()) {
			MultiPolygon jtsMP = e.getValue();
			org.opengis.geometry.aggregate.MultiSurface isoMS = JTSParser.parseMultiPolygon(geomBuilder, jtsMP);
			fBuilder.add( e.getKey() );
			fBuilder.add( isoMS );
			SimpleFeature feature = fBuilder.buildFeature( e.getKey() );
			features.add(feature);
		}
		
	}
	
	public CityGML4JConvertorTest() {
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
		fileMenu.add(new SafeAction("Insert to PostGIS database...") {
			public void action(ActionEvent e) throws Throwable {
				insertTable();
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
		
		dataMenu.add(new SafeAction("Geometry") {
			public void action(ActionEvent e) throws Throwable {
				queryFeatures();
			}
		});
	}
	
	private void insertTable() {
		try {
			PostgisNGDataStoreFactory factory = new PostgisNGDataStoreFactory();
			JDataStoreWizard wizard = new JDataStoreWizard(factory);
			int result = wizard.showModalDialog();
			if (result == JWizard.FINISH) {
				Map<String, Object> connectionParameters = wizard.getConnectionParameters();

				dataStore = DataStoreFinder.getDataStore(connectionParameters);

				if (dataStore == null) {
					JOptionPane.showMessageDialog(null, "Could not connect - check parameters");
				}
				JDBCDataStore jds = (JDBCDataStore) dataStore;
				//jds.setDatabaseSchema(null);

				dataStore.createSchema((SimpleFeatureType) schema);
				//SimpleFeatureType actualSchema = dataStore1.getSchema(typeName);

				// insert the feature
				FeatureWriter<SimpleFeatureType, SimpleFeature> fw = dataStore.getFeatureWriterAppend(
						schema.getTypeName(), Transaction.AUTO_COMMIT);
				//SimpleFeature f = fw.next();
				
				for(SimpleFeature sf : features) {
					SimpleFeature newFeature = fw.next(); // new blank feature
					newFeature.setAttributes(sf.getAttributes());
					fw.write();
				}
				//fw.write();
				fw.close();
				System.out.println("Inserting features is done");
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
	
	private void queryFeatures() throws ParseException  {
		String typeName = (String) featureTypeCBox.getSelectedItem();
		SimpleFeatureSource source;
		try {
			PostgisNGDataStoreFactory factory = new PostgisNGDataStoreFactory();
			JDataStoreWizard wizard = new JDataStoreWizard(factory);
			int result = wizard.showModalDialog();
			if (result == JWizard.FINISH) {
				Map<String, Object> connectionParameters = wizard.getConnectionParameters();

				dataStore = DataStoreFinder.getDataStore(connectionParameters);

				if (dataStore == null) {
					JOptionPane.showMessageDialog(null, "Could not connect - check parameters");
				}
				
				source = dataStore.getFeatureSource("buildings");
				FeatureType schema = source.getSchema();
				String name = schema.getGeometryDescriptor().getLocalName();
	
				//ReferencedEnvelope env = (ReferencedEnvelope) source.getBounds();
				
				//org.opengis.geometry.DirectPosition lower = env.getLowerCorner();
				//org.opengis.geometry.DirectPosition upper = env.getUpperCorner();
				
				//LineSegment ls = geomBuilder.createLineSegment(lower, upper);
				WKTReader reader = new WKTReader(DefaultGeographicCRS.WGS84_3D);
				Curve c = (Curve) reader.read("Curve(197988.1167951445 982358.3391372405 44.896768043749034, 197707.54445907986 981242.0744264929 44.933580635115504, 196896.2460717163 981484.9939220184 44.9573813341558, 197336.92284817807 981939.0492767135 44.96757285669446, 197710.42638736032 982117.941429229 44.94236546009779, 197087.63210040703 982214.1873574951 44.95265289582312, 196558.73953191517 981738.4784381008 44.936082392930984, 196602.33145256573 980978.1767511205 44.89442145451903, 197340.31366120372 981047.639376733 44.9323018733412)");
				
				ISOFilterFactoryImpl ff = new ISOFilterFactoryImpl();
				Filter filter = ff.intersects(schema.getGeometryDescriptor().getLocalName(), c);
				
				Query query = new Query(typeName, filter);
	
				SimpleFeatureCollection features = source.getFeatures(query);
				
				try {
					SimpleFeatureIterator it = features.features();
					while(it.hasNext()) {
						SimpleFeature s = it.next();
						System.out.println(s);
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				}
				
				FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
				
				table.setModel(model);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
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
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Hints h = new Hints();
		h.put(Hints.GEOMETRY_VALIDATE, false);
		h.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D);
		geomBuilder = new ISOGeometryBuilder(h);
		CityGML4JConvertorTest frame = new CityGML4JConvertorTest();
		frame.importCityGML();
		frame.setVisible(true);
	}

}
