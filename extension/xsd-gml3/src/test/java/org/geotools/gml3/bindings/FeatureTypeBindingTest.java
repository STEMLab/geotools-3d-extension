package org.geotools.gml3.bindings;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.xsd.XSDSchema;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.gml3.GML3D;
import org.geotools.gml3.GML3TestSupport;
import org.geotools.gml3.GMLConfiguration3D;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.Encoder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.LineString;
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
import org.w3c.dom.Document;

import junit.framework.TestCase;

public class FeatureTypeBindingTest extends GML3TestSupport {
    /*
    public void testWithoutSchema() throws Exception {
        InputStream in = getClass().getResourceAsStream( "testSolid.xml");
        GMLConfiguration3D gml = new GMLConfiguration3D(true);
        gml.getProperties().add(GMLConfiguration3D.NO_FEATURE_BOUNDS);
        StreamingParser parser = new StreamingParser( gml, in, SimpleFeature.class );

        int nfeatures = 0;
        SimpleFeature f = null;
        while( ( f = (SimpleFeature) parser.parse() ) != null ) {
            nfeatures++;
            assertNotNull( f.getAttribute( "the_geom"));
        }

        assertEquals( 2, nfeatures );
    }
     */
    public void testWithSchema() throws Exception {
        FeatureCollection features = getTestFeatures();

        assertEquals( 1, features.size() );

        FeatureIterator fi = features.features();
        try {
            for ( int i = 0; i < 1; i++ ) {
                assertTrue( fi.hasNext() );

                SimpleFeature f = (SimpleFeature) fi.next();
                assertNull( f.getAttribute( "the_geom" ) );
            }
        }
        finally {
            fi.close();
        }

        GMLConfiguration3D conf = new GMLConfiguration3D();
        conf.getProperties().add(GMLConfiguration3D.NO_FEATURE_BOUNDS);

        XSDSchema schema = GML3D.getInstance().getSchema();

        Encoder encoder = new Encoder(conf, schema);

        FileOutputStream fos = new FileOutputStream("test2.gml", true);
        Document dom = encoder.encodeAsDOM(features, GML3D.FeatureCollection);

        printDocument(dom, fos);
    }

    public FeatureCollection getTestFeatures() {
        GML3SolidMockData.solid(document, document, false);
        Solid solid = null;
        try {
            solid = (Solid) parse();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /*
        Hints hints = GeoTools.getDefaultHints();
        hints.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D);
        hints.put(Hints.GEOMETRY_VALIDATE, false);
        GeometryBuilder geometryBuilder = new GeometryBuilder(hints);
        Solid solid = getSolids(geometryBuilder).get(0);
        */

        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();

        //set the name
        b.setName( "test" );

        //add a geometry property
        //b.setCRS( DefaultGeographicCRS.WGS84_3D );
        //b.add( "geom", Solid.class );

        //build the type
        SimpleFeatureType type = b.buildFeatureType();

        //create the builder
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);

        //add the values
        builder.add( "test1" );
        builder.add( solid );

        //build the feature with provided ID
        SimpleFeature feature = builder.buildFeature( "fid.1" );

        List<SimpleFeature> list = new ArrayList<SimpleFeature>();
        list.add(feature);
        SimpleFeatureCollection collection = new ListFeatureCollection(type, list);

        return collection;
    }

    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), 
                new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }

    public static ArrayList<ArrayList<DirectPosition>> getSolidPoints(GeometryBuilder builder) {
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

    public static Solid makeSolid(GeometryBuilder builder, ArrayList<DirectPosition> points) {
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

    public static ArrayList<Solid> getSolids(GeometryBuilder builder) {
        ArrayList<Solid> solids = new ArrayList<Solid>();
        ArrayList<ArrayList<DirectPosition>> solidPoints = getSolidPoints(builder);

        for (int i = 0; i < 9; i++) {
            solids.add(makeSolid(builder, solidPoints.get(i)));
        }

        return solids;
    }
}
