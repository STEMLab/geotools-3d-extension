import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
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
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.ISOFeatureFactoryImpl;
import org.geotools.feature.simple.ISOSimpleFeatureTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.AttributeExpressionImpl;
import org.geotools.filter.ISOFilterFactoryImpl;
import org.geotools.filter.LiteralExpressionImpl;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.iso.coordinate.DirectPositionImpl;
import org.geotools.geometry.iso.io.wkt.ParseException;
import org.geotools.geometry.iso.io.wkt.WKTReader;
import org.geotools.geometry.iso.primitive.PointImpl;
import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.geometry.jts.ReferencedEnvelope3D;
import org.geotools.gml3.iso.GMLConfiguration_ISO;
import org.geotools.jdbc.iso.JDBCDataStore;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.action.SafeAction;
import org.geotools.swing.data.JDataStoreWizard;
import org.geotools.swing.table.FeatureCollectionTableModel;
import org.geotools.swing.wizard.JWizard;
import org.geotools.xml.PullParser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.IncludeFilter;
import org.opengis.geometry.BoundingBox3D;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.Position;
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


public class TTADemoTest extends JFrame{
	private DataStore dataStore;
	private SimpleFeatureCollection currentfeatures;
	private JComboBox featureTypeCBox;
	private JTable table;
	private JLabel label;
	private JTextField text;
	private JProgressBar progressBar;
	private static Hints hints = null;

	private static ISOGeometryBuilder builder;
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Hints h = new Hints();
		h.put(Hints.GEOMETRY_VALIDATE, false);
		h.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D);
		builder = new ISOGeometryBuilder(h);
		JFrame frame = new TTADemoTest();
		frame.setVisible(true);
	}
	public TTADemoTest() {
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
		
		/*label = new JLabel();
		label.setText("time : ");
		getContentPane().add(label, BorderLayout.SOUTH);*/

		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		getContentPane().add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setPreferredSize(new Dimension(getContentPane().getWidth(), 16));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		label = new JLabel("status");
		label.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(label);
		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);

		JMenu fileMenu = new JMenu("File");
		menubar.add(fileMenu);

		featureTypeCBox = new JComboBox();
		menubar.add(featureTypeCBox);

		JMenu dataMenu = new JMenu("Data");
		menubar.add(dataMenu);
		pack();
		fileMenu.add(new SafeAction("Open GML File...") {
			public void action(ActionEvent e) throws Throwable {
				connectGML(new GMLDataStoreFactory());
			}
		});
		fileMenu.add(new SafeAction("Open csvfile...") {
			public void action(ActionEvent e) throws Throwable {
				connect(new CSVDataStoreFactory());
			}
		});
		/*fileMenu.add(new SafeAction("Connect to PostGIS database...") {
			public void action(ActionEvent e) throws Throwable {
				connect(new PostgisNGDataStoreFactory());
				System.out.println("Connection succeeded");
			}
		});*/
		/*fileMenu.add(new SafeAction("pointToTable...") {
			public void action(ActionEvent e) throws Throwable {
				pointToTable();
			}
		});
		fileMenu.add(new SafeAction("getLineString...") {
			public void action(ActionEvent e) throws Throwable {
				getLineString();
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
				filterFeatureswithtext();
			}
		});
		/*dataMenu.add(new SafeAction("contains: solid") {
			public void action(ActionEvent e) throws Throwable {
				constainsfilter();
			}
		});
		dataMenu.add(new SafeAction("within: solid") {
			public void action(ActionEvent e) throws Throwable {
				withinfilter();
			}
		});

		dataMenu.add(new SafeAction("bbox: solid") {
			public void action(ActionEvent e) throws Throwable {
				bboxfilter();
			}
		});*/
		

		/*dataMenu.add(new SafeAction("Count") {
			public void action(ActionEvent e) throws Throwable {
				countFeatures();
			}
		});
		dataMenu.add(new SafeAction("Geometry") {
			public void action(ActionEvent e) throws Throwable {
				queryFeatures();
			}
		});*/
	}
	public ArrayList<Solid> getSolids(ISOGeometryBuilder builder) {
		ArrayList<Solid> solids = new ArrayList<Solid>();
		ArrayList<ArrayList<DirectPosition>> solidPoints = getSolidPoints(builder);

		for (int i = 0; i < 9; i++) {
			solids.add(makeSolid(builder, solidPoints.get(i)));
		}

		return solids;
	}
	public Solid makeSolid(ISOGeometryBuilder builder, ArrayList<DirectPosition> points) {
		DirectPosition position1 = points.get(0);
		DirectPosition position2 = points.get(1);
		DirectPosition position3 = points.get(2);
		DirectPosition position4 = points.get(3);
		DirectPosition position5 = points.get(4);
		DirectPosition position6 = points.get(5);
		DirectPosition position7 = points.get(6);
		DirectPosition position8 = points.get(7);

		// create a list of connected positions
		List<Position> dps1 = new ArrayList<Position>();
		dps1.add(position1);
		dps1.add(position4);
		dps1.add(position3);
		dps1.add(position2);
		dps1.add(position1);

		List<Position> dps2 = new ArrayList<Position>();
		dps2.add(position3);
		dps2.add(position4);
		dps2.add(position8);
		dps2.add(position7);
		dps2.add(position3);

		List<Position> dps3 = new ArrayList<Position>();
		dps3.add(position5);
		dps3.add(position6);
		dps3.add(position7);
		dps3.add(position8);
		dps3.add(position5);

		List<Position> dps4 = new ArrayList<Position>();
		dps4.add(position6);
		dps4.add(position5);
		dps4.add(position1);
		dps4.add(position2);
		dps4.add(position6);

		List<Position> dps5 = new ArrayList<Position>();
		dps5.add(position2);
		dps5.add(position3);
		dps5.add(position7);
		dps5.add(position6);
		dps5.add(position2);

		List<Position> dps6 = new ArrayList<Position>();
		dps6.add(position1);
		dps6.add(position5);
		dps6.add(position8);
		dps6.add(position4);
		dps6.add(position1);

		// create linestring from directpositions
		LineString line1 = builder.createLineString(dps1);
		LineString line2 = builder.createLineString(dps2);
		LineString line3 = builder.createLineString(dps3);
		LineString line4 = builder.createLineString(dps4);
		LineString line5 = builder.createLineString(dps5);
		LineString line6 = builder.createLineString(dps6);

		// create curvesegments from line
		ArrayList<CurveSegment> segs1 = new ArrayList<CurveSegment>();
		segs1.add(line1);
		ArrayList<CurveSegment> segs2 = new ArrayList<CurveSegment>();
		segs2.add(line2);
		ArrayList<CurveSegment> segs3 = new ArrayList<CurveSegment>();
		segs3.add(line3);
		ArrayList<CurveSegment> segs4 = new ArrayList<CurveSegment>();
		segs4.add(line4);
		ArrayList<CurveSegment> segs5 = new ArrayList<CurveSegment>();
		segs5.add(line5);
		ArrayList<CurveSegment> segs6 = new ArrayList<CurveSegment>();
		segs6.add(line6);

		// Create list of OrientableCurves that make up the surface
		OrientableCurve curve1 = builder.createCurve(segs1);
		List<OrientableCurve> orientableCurves1 = new ArrayList<OrientableCurve>();
		orientableCurves1.add(curve1);
		OrientableCurve curve2 = builder.createCurve(segs2);
		List<OrientableCurve> orientableCurves2 = new ArrayList<OrientableCurve>();
		orientableCurves2.add(curve2);
		OrientableCurve curve3 = builder.createCurve(segs3);
		List<OrientableCurve> orientableCurves3 = new ArrayList<OrientableCurve>();
		orientableCurves3.add(curve3);
		OrientableCurve curve4 = builder.createCurve(segs4);
		List<OrientableCurve> orientableCurves4 = new ArrayList<OrientableCurve>();
		orientableCurves4.add(curve4);
		OrientableCurve curve5 = builder.createCurve(segs5);
		List<OrientableCurve> orientableCurves5 = new ArrayList<OrientableCurve>();
		orientableCurves5.add(curve5);
		OrientableCurve curve6 = builder.createCurve(segs6);
		List<OrientableCurve> orientableCurves6 = new ArrayList<OrientableCurve>();
		orientableCurves6.add(curve6);

		// create the interior ring and a list of empty interior rings (holes)
		
		PrimitiveFactoryImpl pmFF = (PrimitiveFactoryImpl) builder.getPrimitiveFactory();
		
		Ring extRing1 = pmFF.createRing(orientableCurves1);
		Ring extRing2 = pmFF.createRing(orientableCurves2);
		Ring extRing3 = pmFF.createRing(orientableCurves3);
		Ring extRing4 = pmFF.createRing(orientableCurves4);
		Ring extRing5 = pmFF.createRing(orientableCurves5);
		Ring extRing6 = pmFF.createRing(orientableCurves6);

		// create surfaceboundary by rings
		SurfaceBoundary sb1 = pmFF.createSurfaceBoundary(extRing1, new ArrayList<Ring>());
		SurfaceBoundary sb2 = pmFF.createSurfaceBoundary(extRing2, new ArrayList<Ring>());
		SurfaceBoundary sb3 = pmFF.createSurfaceBoundary(extRing3, new ArrayList<Ring>());
		SurfaceBoundary sb4 = pmFF.createSurfaceBoundary(extRing4, new ArrayList<Ring>());
		SurfaceBoundary sb5 = pmFF.createSurfaceBoundary(extRing5, new ArrayList<Ring>());
		SurfaceBoundary sb6 = pmFF.createSurfaceBoundary(extRing6, new ArrayList<Ring>());

		// create the surface
		Surface surface1 = pmFF.createSurface(sb1);
		Surface surface2 = pmFF.createSurface(sb2);
		Surface surface3 = pmFF.createSurface(sb3);
		Surface surface4 = pmFF.createSurface(sb4);
		Surface surface5 = pmFF.createSurface(sb5);
		Surface surface6 = pmFF.createSurface(sb6);

		List<OrientableSurface> surfaces = new ArrayList<OrientableSurface>();
		surfaces.add(surface1);
		surfaces.add(surface2);
		surfaces.add(surface3);
		surfaces.add(surface4);
		surfaces.add(surface5);
		surfaces.add(surface6);

		Shell exteriorShell = pmFF.createShell(surfaces);
		List<Shell> interiors = new ArrayList<Shell>();

		SolidBoundary solidBoundary = pmFF.createSolidBoundary(exteriorShell, interiors);
		Solid solid = pmFF.createSolid(solidBoundary);

		return solid;
	}
	public ArrayList<ArrayList<DirectPosition>> getSolidPoints(ISOGeometryBuilder builder) {
		ArrayList<ArrayList<DirectPosition>> solidPoints = new ArrayList<ArrayList<DirectPosition>>();

		DirectPosition p1 = builder.createDirectPosition(new double[] { 0, 0, 0 });
		DirectPosition p2 = builder.createDirectPosition(new double[] { 0, -2, 0 });
		DirectPosition p3 = builder.createDirectPosition(new double[] { 2, -2, 0 });
		DirectPosition p4 = builder.createDirectPosition(new double[] { 2, 0, 0 });
		DirectPosition p5 = builder.createDirectPosition(new double[] { 0, 0, 2 });
		DirectPosition p6 = builder.createDirectPosition(new double[] { 0, -2, 2 });
		DirectPosition p7 = builder.createDirectPosition(new double[] { 2, -2, 2 });
		DirectPosition p8 = builder.createDirectPosition(new double[] { 2, 0, 2 });

		ArrayList<DirectPosition> points1 = new ArrayList<DirectPosition>();
		points1.add(p1);
		points1.add(p2);
		points1.add(p3);
		points1.add(p4);
		points1.add(p5);
		points1.add(p6);
		points1.add(p7);
		points1.add(p8);

		//
		DirectPosition p11 = builder.createDirectPosition(new double[] { 2, 0, 0 });
		DirectPosition p12 = builder.createDirectPosition(new double[] { 2, -2, 0 });
		DirectPosition p13 = builder.createDirectPosition(new double[] { 4, -2, 0 });
		DirectPosition p14 = builder.createDirectPosition(new double[] { 4, 0, 0 });
		DirectPosition p15 = builder.createDirectPosition(new double[] { 2, 0, 2 });
		DirectPosition p16 = builder.createDirectPosition(new double[] { 2, -2, 2 });
		DirectPosition p17 = builder.createDirectPosition(new double[] { 4, -2, 2 });
		DirectPosition p18 = builder.createDirectPosition(new double[] { 4, 0, 2 });

		ArrayList<DirectPosition> points2 = new ArrayList<DirectPosition>();
		points2.add(p11);
		points2.add(p12);
		points2.add(p13);
		points2.add(p14);
		points2.add(p15);
		points2.add(p16);
		points2.add(p17);
		points2.add(p18);

		//
		DirectPosition p21 = builder.createDirectPosition(new double[] { 2, 0, 2 });
		DirectPosition p22 = builder.createDirectPosition(new double[] { 2, -2, 2 });
		DirectPosition p23 = builder.createDirectPosition(new double[] { 4, -2, 2 });
		DirectPosition p24 = builder.createDirectPosition(new double[] { 4, 0, 2 });
		DirectPosition p25 = builder.createDirectPosition(new double[] { 2, 0, 4 });
		DirectPosition p26 = builder.createDirectPosition(new double[] { 2, -2, 4 });
		DirectPosition p27 = builder.createDirectPosition(new double[] { 4, -2, 4 });
		DirectPosition p28 = builder.createDirectPosition(new double[] { 4, 0, 4 });

		ArrayList<DirectPosition> points3 = new ArrayList<DirectPosition>();
		points3.add(p21);
		points3.add(p22);
		points3.add(p23);
		points3.add(p24);
		points3.add(p25);
		points3.add(p26);
		points3.add(p27);
		points3.add(p28);

		DirectPosition p31 = builder.createDirectPosition(new double[] { 1, 0, 0 });
		DirectPosition p32 = builder.createDirectPosition(new double[] { 1, -2, 0 });
		DirectPosition p33 = builder.createDirectPosition(new double[] { 3, -2, 0 });
		DirectPosition p34 = builder.createDirectPosition(new double[] { 3, 0, 0 });
		DirectPosition p35 = builder.createDirectPosition(new double[] { 1, 0, 2 });
		DirectPosition p36 = builder.createDirectPosition(new double[] { 1, -2, 2 });
		DirectPosition p37 = builder.createDirectPosition(new double[] { 3, -2, 2 });
		DirectPosition p38 = builder.createDirectPosition(new double[] { 3, 0, 2 });

		ArrayList<DirectPosition> points4 = new ArrayList<DirectPosition>();
		points4.add(p31);
		points4.add(p32);
		points4.add(p33);
		points4.add(p34);
		points4.add(p35);
		points4.add(p36);
		points4.add(p37);
		points4.add(p38);

		DirectPosition p41 = builder.createDirectPosition(new double[] { -1, 0, -1 });
		DirectPosition p42 = builder.createDirectPosition(new double[] { -1, -2, -1 });
		DirectPosition p43 = builder.createDirectPosition(new double[] { 3, -2, -1 });
		DirectPosition p44 = builder.createDirectPosition(new double[] { 3, 0, -1 });
		DirectPosition p45 = builder.createDirectPosition(new double[] { -1, 0, 3 });
		DirectPosition p46 = builder.createDirectPosition(new double[] { -1, -2, 3 });
		DirectPosition p47 = builder.createDirectPosition(new double[] { 3, -2, 3 });
		DirectPosition p48 = builder.createDirectPosition(new double[] { 3, 0, 3 });

		ArrayList<DirectPosition> points5 = new ArrayList<DirectPosition>();
		points5.add(p41);
		points5.add(p42);
		points5.add(p43);
		points5.add(p44);
		points5.add(p45);
		points5.add(p46);
		points5.add(p47);
		points5.add(p48);

		DirectPosition p51 = builder.createDirectPosition(new double[] { 0.5, -0.5, 0.5 });
		DirectPosition p52 = builder.createDirectPosition(new double[] { 0.5, -1.5, 0.5 });
		DirectPosition p53 = builder.createDirectPosition(new double[] { 1.5, -1.5, 0.5 });
		DirectPosition p54 = builder.createDirectPosition(new double[] { 1.5, -0.5, 0.5 });
		DirectPosition p55 = builder.createDirectPosition(new double[] { 0.5, -0.5, 1.5 });
		DirectPosition p56 = builder.createDirectPosition(new double[] { 0.5, -1.5, 1.5 });
		DirectPosition p57 = builder.createDirectPosition(new double[] { 1.5, -1.5, 1.5 });
		DirectPosition p58 = builder.createDirectPosition(new double[] { 1.5, -0.5, 1.5 });

		ArrayList<DirectPosition> points6 = new ArrayList<DirectPosition>();
		points6.add(p51);
		points6.add(p52);
		points6.add(p53);
		points6.add(p54);
		points6.add(p55);
		points6.add(p56);
		points6.add(p57);
		points6.add(p58);

		DirectPosition p61 = builder.createDirectPosition(new double[] { 1, -1, 0 });
		DirectPosition p62 = builder.createDirectPosition(new double[] { 1, -2, 0 });
		DirectPosition p63 = builder.createDirectPosition(new double[] { 2, -2, 0 });
		DirectPosition p64 = builder.createDirectPosition(new double[] { 2, -1, 0 });
		DirectPosition p65 = builder.createDirectPosition(new double[] { 1, -1, 1 });
		DirectPosition p66 = builder.createDirectPosition(new double[] { 1, -2, 1 });
		DirectPosition p67 = builder.createDirectPosition(new double[] { 2, -2, 1 });
		DirectPosition p68 = builder.createDirectPosition(new double[] { 2, -1, 1 });

		ArrayList<DirectPosition> points7 = new ArrayList<DirectPosition>();
		points7.add(p61);
		points7.add(p62);
		points7.add(p63);
		points7.add(p64);
		points7.add(p65);
		points7.add(p66);
		points7.add(p67);
		points7.add(p68);

		DirectPosition p71 = builder.createDirectPosition(new double[] { 0.5, -0.5, -1 });
		DirectPosition p72 = builder.createDirectPosition(new double[] { 0.5, -1.5, -1 });
		DirectPosition p73 = builder.createDirectPosition(new double[] { 1.5, -1.5, -1 });
		DirectPosition p74 = builder.createDirectPosition(new double[] { 1.5, -0.5, -1 });
		DirectPosition p75 = builder.createDirectPosition(new double[] { 0.5, -0.5, 3 });
		DirectPosition p76 = builder.createDirectPosition(new double[] { 0.5, -1.5, 3 });
		DirectPosition p77 = builder.createDirectPosition(new double[] { 1.5, -1.5, 3 });
		DirectPosition p78 = builder.createDirectPosition(new double[] { 1.5, -0.5, 3 });

		ArrayList<DirectPosition> points8 = new ArrayList<DirectPosition>();
		points8.add(p71);
		points8.add(p72);
		points8.add(p73);
		points8.add(p74);
		points8.add(p75);
		points8.add(p76);
		points8.add(p77);
		points8.add(p78);

		DirectPosition p81 = builder.createDirectPosition(new double[] { -125745.58224841699, 3813.6302470150695, 0.0 });
		DirectPosition p82 = builder.createDirectPosition(new double[] { -125738.91237563781, 4813.464779595859, 0.0 });
		DirectPosition p83 = builder.createDirectPosition(new double[] { -126731.71448564173, 4815.075754128498, 0.0 });
		DirectPosition p84 = builder.createDirectPosition(new double[] { -126738.38435842091, 3815.241221547709, 0.0 });
		DirectPosition p85 = builder.createDirectPosition(new double[] { -125745.58224841699, 3813.6302470150695, 3000.0 });
		DirectPosition p86 = builder.createDirectPosition(new double[] { -125738.91237563781, 4813.464779595859, 3000.0 });
		DirectPosition p87 = builder.createDirectPosition(new double[] { -126731.71448564173, 4815.075754128498, 3000.0 });
		DirectPosition p88 = builder.createDirectPosition(new double[] { -126738.38435842091, 3815.241221547709, 3000.0 });

		ArrayList<DirectPosition> points9 = new ArrayList<DirectPosition>();
		points9.add(p81);
		points9.add(p82);
		points9.add(p83);
		points9.add(p84);
		points9.add(p85);
		points9.add(p86);
		points9.add(p87);
		points9.add(p88);

		solidPoints.add(points1);
		solidPoints.add(points2);
		solidPoints.add(points3);
		solidPoints.add(points4);
		solidPoints.add(points5);
		solidPoints.add(points6);
		solidPoints.add(points7);
		solidPoints.add(points8);
		solidPoints.add(points9);

		return solidPoints;
	}
	private void pointToTable() {
		String typeName = "newFlag";
		Point al = new PointImpl(new DirectPositionImpl(DefaultGeographicCRS.WGS84_3D,new double[]{0,0,0}));
		ISOSimpleFeatureTypeBuilder b = new ISOSimpleFeatureTypeBuilder();
		b.setCRS(DefaultGeographicCRS.WGS84_3D);
		b.setName( typeName );
		b.add("loc", Point.class);
		SimpleFeatureType schema = b.buildFeatureType();
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(schema, new ISOFeatureFactoryImpl());
		builder.add( al );
		SimpleFeature feature = builder.buildFeature( "fid.1" );
		try {
			//source = dataStore.getFeatureSource(typeName);
			//DataStore dataStore1;
			//JDataStoreWizard wizard = new JDataStoreWizard(new PostgisNGDataStoreFactory());
			JDataStoreWizard wizard = new JDataStoreWizard(new CSVDataStoreFactory());
			int result = wizard.showModalDialog();
			if (result == JWizard.FINISH) {
				Map<String, Object> connectionParameters = wizard.getConnectionParameters();
				dataStore = DataStoreFinder.getDataStore(connectionParameters);
				if (dataStore == null) {
					JOptionPane.showMessageDialog(null, "Could not connect - check parameters");
				}
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
	private void constainsfilter() {
		String typeName = (String) featureTypeCBox.getSelectedItem();
		SimpleFeatureSource source;
		try {
			source = dataStore.getFeatureSource(typeName);
   			FeatureType schema = source.getSchema();
			//String name = schema.getGeometryDescriptor().getLocalName();
		
			//Filter filter = CQL.toFilter(text.getText());
   			Hints h = new Hints();
   			h.put(Hints.FILTER_FACTORY, ISOFilterFactoryImpl.class);
   			FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(h);
   		    //Envelope bbox = new ReferencedEnvelope3D(-1, 1, -1, 1, -1, 1, DefaultGeographicCRS.WGS84 );
   			GeometryBuilder gb = new GeometryBuilder(DefaultGeographicCRS.WGS84);
   			ArrayList<Solid> al = getSolids(builder);
   		    Filter filter = ff.contains("geom", (Geometry)al.get(0));
			Query query = new Query(typeName, filter, new String[] { "geom" });

			SimpleFeatureCollection features = source.getFeatures(query);

			FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
			table.setModel(model);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
	}
	private void withinfilter() {
		String typeName = (String) featureTypeCBox.getSelectedItem();
		SimpleFeatureSource source;
		try {
			source = dataStore.getFeatureSource(typeName);
   			FeatureType schema = source.getSchema();
			//String name = schema.getGeometryDescriptor().getLocalName();
		
			//Filter filter = CQL.toFilter(text.getText());
   			Hints h = new Hints();
   			h.put(Hints.FILTER_FACTORY, ISOFilterFactoryImpl.class);
   			FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(h);
   		    //Envelope bbox = new ReferencedEnvelope3D(-1, 1, -1, 1, -1, 1, DefaultGeographicCRS.WGS84 );
   			GeometryBuilder gb = new GeometryBuilder(DefaultGeographicCRS.WGS84);
   			ArrayList<Solid> al = getSolids(builder);
   		    Filter filter = ff.within("geom", (Geometry)al.get(0));
			Query query = new Query(typeName, filter, new String[] { "geom" });

			SimpleFeatureCollection features = source.getFeatures(query);

			FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
			table.setModel(model);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
	}
	private void bboxfilter() {
		String typeName = (String) featureTypeCBox.getSelectedItem();
		SimpleFeatureSource source;
		try {
			source = dataStore.getFeatureSource(typeName);
   			FeatureType schema = source.getSchema();
			//String name = schema.getGeometryDescriptor().getLocalName();
		
			//Filter filter = CQL.toFilter(text.getText());
   			Hints h = new Hints();
   			h.put(Hints.FILTER_FACTORY, ISOFilterFactoryImpl.class);
   			FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(h);
   		    //Envelope bbox = new ReferencedEnvelope3D(-1, 1, -1, 1, -1, 1, DefaultGeographicCRS.WGS84 );
   			GeometryBuilder gb = new GeometryBuilder(DefaultGeographicCRS.WGS84);
   			//ArrayList<Solid> al = getSolids(builder);
   		    Filter filter = ff.bbox(new AttributeExpressionImpl("geom"), (BoundingBox3D)new ReferencedEnvelope3D(-2,2,-2,2,-2,2,DefaultGeographicCRS.WGS84));
			Query query = new Query(typeName, filter, new String[] { "geom" });

			SimpleFeatureCollection features = source.getFeatures(query);

			FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
			table.setModel(model);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
	}
	private void getLineString() {
		String typeName = (String) featureTypeCBox.getSelectedItem();
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
	private void connectGML(DataStoreFactorySpi format) {
		JDataStoreWizard wizard = new JDataStoreWizard(format);
		int result = wizard.showModalDialog();
		if (result == JWizard.FINISH) {
			Map<String, Object> connectionParameters = wizard.getConnectionParameters();
			//File f = 
			URL oracle;
			try {
				File f = (File) connectionParameters.get("file");
				oracle = f.toURI().toURL();
				 //BufferedReader in = new BufferedReader(
		        //new InputStreamReader(oracle.openStream()));
				long start = System.currentTimeMillis();
				 currentfeatures = (SimpleFeatureCollection) parseGML(oracle.openStream());
				 long end = System.currentTimeMillis();
					//System.out.println( "실행 시간 : " + ( end - start )/1000.0 );
					label.setText("load complete : " + ( end - start )/1000.0 + " sec");
					FeatureCollectionTableModel model = new FeatureCollectionTableModel(currentfeatures);
					table.setModel(model);
		        /*String inputLine;
		        while ((inputLine = in.readLine()) != null)
		            System.out.println(inputLine);
		        in.close();*/
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
			/*try {
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
			}*/

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
	private void filterFeatureswithtext() {
		//String typeName = (String) featureTypeCBox.getSelectedItem();
		//SimpleFeatureSource source;
		try {
			//source = dataStore.getFeatureSource(typeName);
			String sql = text.getText();
			String filtertype = "include";
			String[] cql = sql.split(":");
			Hints h = new Hints();
   			h.put(Hints.FILTER_FACTORY, ISOFilterFactoryImpl.class);
   			FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(h);
   			WKTReader wktr = new WKTReader(DefaultGeographicCRS.WGS84_3D);
   			Filter filter = CQL.toFilter("include");
   			long start = System.currentTimeMillis();
   			if(cql[0].equalsIgnoreCase("contains")) {
   				filter = ff.contains("the_geom", wktr.read(cql[1]));
   				filtertype = "contains ";
   			}
   			else if(cql[0].equalsIgnoreCase("equals")) {
   				filter = ff.equals("the_geom", wktr.read(cql[1]));
   				filtertype = "equals ";
   			}
   			else if(cql[0].equalsIgnoreCase("intersects")) {
   				filter = ff.intersects("the_geom", wktr.read(cql[1]));
   				filtertype = "intersects ";
   			} 

   		
   			
			//SimpleFeatureCollection features = source.getFeatures(filter);
   			List<SimpleFeature> sfs = new ArrayList<SimpleFeature>();
   			SimpleFeatureIterator iterator = currentfeatures.features();
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				
				if(filter.evaluate(feature)) {
					sfs.add(feature);
				}
			}
			SimpleFeatureCollection result = ISODataUtilities.collection(sfs);
   			
			long end = System.currentTimeMillis();
			//System.out.println( "실행 시간 : " + ( end - start )/1000.0 );
			label.setText(filtertype + "complete : " + ( end - start )/1000.0 + " sec");
			FeatureCollectionTableModel model = new FeatureCollectionTableModel(result);
			table.setModel(model);
			
		} catch (CQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	private void filterFeatures()  {
		String typeName = (String) featureTypeCBox.getSelectedItem();
		SimpleFeatureSource source;
		try {
			source = dataStore.getFeatureSource(typeName);
			String sql = text.getText();
			String[] cql = sql.split(":");
			Hints h = new Hints();
   			h.put(Hints.FILTER_FACTORY, ISOFilterFactoryImpl.class);
   			FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(h);
   			WKTReader wktr = new WKTReader(DefaultGeographicCRS.WGS84_3D);
   			Filter filter = CQL.toFilter("include");
   			long start = System.currentTimeMillis();
   		

   		
   			if(cql[0].equalsIgnoreCase("contains")) {
   				filter = ff.contains("geom", (Geometry)wktr.read(cql[1]));
   			}
   			else if(cql[0].equalsIgnoreCase("equals")) {
   				filter = ff.equals("geom", (Geometry)wktr.read(cql[1]));
   			}
   			else if(cql[0].equalsIgnoreCase("intersects")) {
   				filter = ff.intersects("geom", (Geometry)wktr.read(cql[1]));
   			} 
			SimpleFeatureCollection features = source.getFeatures(filter);
			long end = System.currentTimeMillis();
			System.out.println( "실행 시간 : " + ( end - start )/1000.0 );
			label.setText("time : " + ( end - start )/1000.0);
			FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
			table.setModel(model);
			
		} catch (IOException | CQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
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
	private FeatureCollection parseGML(InputStream in) throws Exception {
		GMLConfiguration_ISO gml = new GMLConfiguration_ISO();
		PullParser parser = new PullParser(gml, in, SimpleFeature.class);
		gml.setGeometryFactory(builder);
		SimpleFeature f = null;
		List<SimpleFeature> sfs = new ArrayList<SimpleFeature>();
		
		while((f = (SimpleFeature) parser.parse()) != null) {
			sfs.add(f);
		}
		
		FeatureCollection fc = ISODataUtilities.collection(sfs);
		return fc;
	}
}
