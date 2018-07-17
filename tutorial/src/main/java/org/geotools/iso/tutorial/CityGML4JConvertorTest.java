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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.geotools.geometry.iso.sfcgal.util.SFCGALConvertor;
import org.geotools.geometry.iso.util.JTSParser;
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
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Surface;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.math.Vector3D;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;

import earcut4j.Earcut;
import edu.pnu.stemlab.sfcgal4j.SFAlgorithm;
import edu.pnu.stemlab.sfcgal4j.SFGeometry;
import edu.pnu.stemlab.sfcgal4j.SFPolygon;

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
		
		/*
		reader = in.createFilteredCityGMLReader(reader, new CityGMLInputFilter() {
			// return true if you want to consume the CityGML feature
						// of the given qualified XML name, false otherwise
			public boolean accept(QName name) {
				return Modules.isModuleNamespace(name.getNamespaceURI(), CityGMLModuleType.BUILDING)
						&& (name.getLocalPart().equals("Building") || name.getLocalPart().contains("Surface"));
			}
		});
		*/
		
		int num = 0;
		while (reader.hasNext()) {
			Object obj = reader.nextFeature();
			
			if(obj instanceof Building) {
				Building b = (Building) obj;
				String id = b.getId().substring(0, 10);
				buildings.put(id, b);
			} else if(obj instanceof AbstractBoundarySurface){
				AbstractBoundarySurface abs = (AbstractBoundarySurface) obj;
				String id = abs.getId();
				boundaries.put(id, abs);
			} else {
				System.out.println(obj);
			}
		}
		
		CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
		CRSFactory csFactory = new CRSFactory();
		
		final String source = "+proj=lcc +lat_1=41.03333333333333 +lat_2=40.66666666666666 +lat_0=40.16666666666666 +lon_0=-74 +x_0=300000.0000000001 +y_0=0 +ellps=GRS80 +datum=NAD83 +to_meter=0.3048006096012192 +no_defs ";
		CoordinateReferenceSystem crs = csFactory.createFromParameters("SOURCE",source);
		final String custom = "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs ";
		CoordinateReferenceSystem customCRS = csFactory.createFromParameters("CUSTOM",custom);
		CoordinateTransform trans = ctFactory.createTransform(crs, customCRS);
		
		for(Building b : buildings.values()) {
			List<BoundarySurfaceProperty> list = b.getBoundedBySurface();
			List<AbstractBoundarySurface> surfaces = new ArrayList<AbstractBoundarySurface>();
			
			for(BoundarySurfaceProperty bsp : list) {
				String href = bsp.getHref();
				AbstractBoundarySurface surface = boundaries.get(href.replace("#", ""));
				
				System.out.println(surface.getClass().getSimpleName());
					
				surfaces.add(surface);
			}
			
			GeometryFactory gf = new GeometryFactory();
			MultiPolygon mp = CityGML2JTS.toMultiPolygon(surfaces);
			
			Polygon[] transformedPolygons = new Polygon[mp.getNumGeometries()];
			for(int i = 0; i< mp.getNumGeometries(); i++) {
				Geometry g = mp.getGeometryN(i);
				Polygon p = (Polygon) g;
				
				Coordinate[] coordinates = p.getCoordinates();
				Coordinate[] transformed = new Coordinate[coordinates.length];
				for(int j = 0; j < coordinates.length; j++) {
				    ProjCoordinate p1 = new ProjCoordinate();
				    ProjCoordinate p2 = new ProjCoordinate();
					
				    p1.x = coordinates[j].y;
				    p1.y = coordinates[j].x;
				    p1.z = coordinates[j].z;
				    
					trans.transform(p1, p2);
					transformed[j] = new Coordinate(p2.x, p2.y, coordinates[j].z);
				}
				
				Polygon p2 = gf.createPolygon(transformed);
				transformedPolygons[i] = p2;
			}
			
			List<Polygon> earcuts = new ArrayList<Polygon>();
			for(Polygon p : transformedPolygons) {
				if(!p.isEmpty()) {
					List<Polygon> triangles = triangulate(p.getExteriorRing().getCoordinates(), null, gf);
					earcuts.addAll(triangles);
				}
			}
			Polygon[] array = earcuts.toArray(new Polygon[earcuts.size()]);
			MultiPolygon transformedMP = gf.createMultiPolygon(array);
			
			
			/*
			List<Polygon> earcuts = new ArrayList<Polygon>();
			for(Polygon p : transformedPolygons) {
				if(!p.isEmpty()) {
					List<Polygon> triangles = getTriangles(p, gf);
					earcuts.addAll(triangles);
				}
			}
			Polygon[] array = earcuts.toArray(new Polygon[earcuts.size()]);
			MultiPolygon transformedMP = gf.createMultiPolygon(array);
			*/
			
			//MultiPolygon transformedMP = gf.createMultiPolygon(transformedPolygons);
			
			buildingMap.put(b.getId(), transformedMP);
			System.out.println("buildings : " + num++);
		}
		
		System.out.println("number of buildings : " + num);
		//System.out.println(buildingMap);
		reader.close();
		
		ISOSimpleFeatureTypeBuilder b = new ISOSimpleFeatureTypeBuilder();
		b.setCRS(DefaultGeographicCRS.WGS84_3D);
		b.setName("buildings");
		b.add("bid", String.class);
		b.add("geom", org.opengis.geometry.primitive.Surface.class);
		this.schema = b.buildFeatureType();
		SimpleFeatureBuilder fBuilder = new SimpleFeatureBuilder(this.schema, new ISOFeatureFactoryImpl());
		
		features = new ArrayList<SimpleFeature>();
		for(Map.Entry<String, MultiPolygon> e : buildingMap.entrySet()) {
			MultiPolygon jtsMP = e.getValue();
			org.opengis.geometry.aggregate.MultiSurface isoMS = JTSParser.parseMultiPolygon(geomBuilder, jtsMP);
			
			for(OrientableSurface os : isoMS.getElements()) {
				fBuilder.add( e.getKey() );
				
				SFGeometry geom = SFCGALConvertor.geometryToSFCGALGeometry(os);
				boolean valid = SFAlgorithm.isValid(geom, 0);
				if(!valid) {
					SFPolygon poly = (SFPolygon) geom;
					System.out.println(os);
				}
				
				fBuilder.add( os );
				SimpleFeature feature = fBuilder.buildFeature( e.getKey() );
				features.add(feature);
			}
		}
		
	}
	
	public static List<Polygon> getTriangles(Polygon p, GeometryFactory gf) {
		List<Polygon> triangles = new ArrayList<Polygon>();
		
		Coordinate[] coords = p.getCoordinates();
		double[] coordv = new double[(coords.length - 1) * 3];
		
		int idx = 0;
        for(int i = 0; i < coords.length - 1; i++) {
            coordv[idx] = coords[i].x;
            coordv[idx + 1] = coords[i].y;
            coordv[idx + 2] = coords[i].z;
            idx += 3;
        }
        
        List<Integer> tIdx = Earcut.earcut(coordv, null, 3);
        for(int i = 0; i < tIdx.size(); i += 3) {
            Coordinate c1 = coords[tIdx.get(i)];
            Coordinate c2 = coords[tIdx.get(i + 1)];
            Coordinate c3 = coords[tIdx.get(i + 2)];

            Polygon t = gf.createPolygon(new Coordinate[] {c1, c2, c3, c1} );
            triangles.add(t);
        }
        
		return triangles;
	}
	
	public static Vector3D calVector (Coordinate[] vertices) {
	      double vecx1 = vertices[1].x - vertices[0].x;
	      double vecx2 = vertices[2].x - vertices[0].x;
	     
	      double vecy1 = vertices[1].y - vertices[0].y;
	      double vecy2 = vertices[2].y - vertices[0].y;
	      
	      double vecz1 = vertices[1].z - vertices[0].z;
	      double vecz2 = vertices[2].z - vertices[0].z;

	      double nx = Math.abs(vecy1 * vecz2 - vecz1 * vecy2);
	      double ny = Math.abs(-(vecx1 * vecz2 - vecz1 * vecx2));
	      double nz = Math.abs(vecx1 * vecy2 - vecy1 * vecx2);
	      
	      return new Vector3D(nx, ny, nz);
	}
	
	public static List<Polygon> triangulate (Coordinate[] exterior, List<Coordinate[]> interior, GeometryFactory gf) {
		  Vector3D vec = calVector(exterior);
		  
		  double nx = vec.getX();
		  double ny = vec.getY();
		  double nz = vec.getZ();
		  
	      double max = Math.max(nx, ny);
	      max = Math.max(max, nz);
	      
	      List<Double> newVertices = new ArrayList<Double>();
	      List<Double> newInterior = new ArrayList<Double>();
	      if(nz == max){
	          for(int i = 0; i < exterior.length; i++) {
	        	  newVertices.add(exterior[i].x);
	              newVertices.add(exterior[i].y);
	          }

	          if(interior != null) {
		          for(int i = 0; i < interior.size(); i++) {
		        	  Coordinate[] coords = interior.get(i);
		        	  for(int j = 0; j < coords.length; j++) {
		        		  newInterior.add(coords[j].x);
		        		  newInterior.add(coords[j].y);
		        	  }
		          }
	          }
	      }
	      else if(nx == max){
	          for(int i = 0; i < exterior.length; i++) {
	        	  newVertices.add(exterior[i].y);
	              newVertices.add(exterior[i].z);
	          }

	          if(interior != null) {
		          for(int i = 0; i < interior.size(); i++) {
		        	  Coordinate[] coords = interior.get(i);
		        	  for(int j = 0; j < coords.length; j++) {
		        		  newInterior.add(coords[j].y);
		        		  newInterior.add(coords[j].z);
		        	  }
		          }
	          }
	      }
	      else {
	          for(int i = 0; i < exterior.length; i++) {
	        	  newVertices.add(exterior[i].x);
	              newVertices.add(exterior[i].z);
	          }
	          if(interior != null) {
		          for(int i = 0; i < interior.size(); i++) {
		        	  Coordinate[] coords = interior.get(i);
		        	  for(int j = 0; j < coords.length; j++) {
		        		  newInterior.add(coords[j].x);
		        		  newInterior.add(coords[j].z);
		        	  }
		          }
	          }
	      }
	      
	      int interiorStartIndex = (newVertices.size() / 2) - 1;
	      newVertices.addAll(newInterior);
	      
	      double[] array = new double[newVertices.size()];
	      for(int i = 0; i < newVertices.size(); i++) {
	    	  array[i] = newVertices.get(i);
	      }
	      
	      List<Integer> triangleIdx = Earcut.earcut(array, new int[] {interiorStartIndex}, 2);
	      
	      List<Double> vertices = new ArrayList<Double>();
	      for(int i = 0; i < exterior.length; i++) {
	    	  vertices.add(exterior[i].x);
	    	  vertices.add(exterior[i].y);
	    	  vertices.add(exterior[i].z);
          }

	      if(interior != null) {
	          for(int i = 0; i < interior.size(); i++) {
	        	  Coordinate[] coords = interior.get(i);
	        	  for(int j = 0; j < coords.length; j++) {
	        		  vertices.add(coords[j].x);
	        		  vertices.add(coords[j].y);
	        		  vertices.add(coords[j].z);
	        	  }
	          }
	      }
	      
          List<Coordinate> partition = new ArrayList<Coordinate>();
	      for(int i = 0; i < triangleIdx.size(); i++) {
	    	  Coordinate c = new Coordinate(
	    			  vertices.get(triangleIdx.get(i) * 3), 
	    			  vertices.get(triangleIdx.get(i) * 3 + 1),
	    			  vertices.get(triangleIdx.get(i) * 3 + 2));
	    	  partition.add(c);
	      }
	      
	      List<Polygon> triangles = new ArrayList<Polygon>();
	      for(int i = 0; i < partition.size(); i = i + 3) {
	    	  Polygon poly = gf.createPolygon(new Coordinate[] {
	    			  partition.get(i),
	    			  partition.get(i + 1),
	    			  partition.get(i + 2),
	    			  partition.get(i)
	    	  });
	    	  triangles.add(poly);
	      }
	      
	      return triangles;
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
		h.put(Hints.CRS, CRS.decode("EPSG:2263"));
		geomBuilder = new ISOGeometryBuilder(h);
		CityGML4JConvertorTest frame = new CityGML4JConvertorTest();
		frame.importCityGML();
		frame.setVisible(true);
	}

}
