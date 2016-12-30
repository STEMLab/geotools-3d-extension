/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2016, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.data.property.iso;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geotools.data.ISODataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.feature.ISOFeatureFactoryImpl;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.ISOGeometryBuilder;
import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
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

/**
 * @author hgryoo
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PropertyDataStore3DTest {
    
    private static PropertyDataStore store;
    private static File dir;
    
    static FilterFactory2 ff = (FilterFactory2) CommonFactoryFinder.getFilterFactory(null);
    
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        dir = new File(".", "propertyTestData" );
        dir.mkdir();
        
        File file = new File(dir, "solid.properties");
        if (file.exists()) {
            file.delete();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("_=name:String,geom:Geometry");
        writer.newLine();
        writer.close();
    }

    /**
     * @throws java.lang.Exception
     */
    /*
    @After
    public void tearDown() throws Exception {
        if( store != null ){
            store.dispose();
        }
        
        File dir = new File( "propertyTestData" );
        File list[]=dir.listFiles();
        for( int i=0; i<list.length;i++){
            list[i].delete();
        }
        dir.delete();
    }
    */
    
    @Test
    public void testGetNames() throws IOException {
        store = new PropertyDataStore( dir, "propertyTestData" );
        String names[] = store.getTypeNames();
        Arrays.sort(names);
        
        for(String s : names) {
            System.out.println(s);
        }
    }
    
    @Test
    public void testGetSchema() throws IOException {
        store = new PropertyDataStore( dir, "propertyTestData" );
        SimpleFeatureType type = store.getSchema( "solid" );
        assertNotNull( type );
        assertEquals( "solid", type.getTypeName() );
        assertEquals( "propertyTestData", type.getName().getNamespaceURI().toString() );
        assertEquals( 2, type.getAttributeCount() );
    }

    @Test
    public void testInsert3DLine() throws Exception {
        // write out new feature
        store = new PropertyDataStore( dir, "propertyTestData" );
        SimpleFeatureStore fs = (SimpleFeatureStore) store.getFeatureSource("solid");
        
        final String featureId = "full3d.newLine";
        
        Hints hints = new Hints(Hints.FEATURE_FACTORY, ISOFeatureFactoryImpl.class);
        FeatureFactory featureFactory = CommonFactoryFinder.getFeatureFactory(hints);
        
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(fs.getSchema(), featureFactory);
        
        SimpleFeature feature = builder.buildFeature(featureId, new Object[] { "name", makeSolid() } );
        
        List<FeatureId> ids = fs.addFeatures(ISODataUtilities.collection(feature));
        assertEquals(1, ids.size());
    }
    
    @Test
    public void testRead() throws Exception {
        store = new PropertyDataStore( dir, "propertyTestData" );
        SimpleFeatureSource fs = store.getFeatureSource("solid");
        
        SimpleFeatureCollection features = fs.getFeatures();
        assertEquals(1, features.size());
        
        SimpleFeatureIterator i = features.features();
        
        while(i.hasNext()) {
            SimpleFeature feature = i.next();
            Property p = feature.getProperty("geom");
            assertTrue(p.getType() instanceof GeometryType);
            assertTrue(p.getValue() instanceof Geometry);
        }
    }
    
    
    private List<DirectPosition> getSolidPoint() {
        ISOGeometryBuilder builder = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84_3D);
        DirectPosition p1 = builder.createDirectPosition(new double[] { 2, 0, 0 });
        DirectPosition p2 = builder.createDirectPosition(new double[] { 2, -2, 0 });
        DirectPosition p3 = builder.createDirectPosition(new double[] { 4, -2, 0 });
        DirectPosition p4 = builder.createDirectPosition(new double[] { 4, 0, 0 });
        DirectPosition p5 = builder.createDirectPosition(new double[] { 2, 0, 2 });
        DirectPosition p6 = builder.createDirectPosition(new double[] { 2, -2, 2 });
        DirectPosition p7 = builder.createDirectPosition(new double[] { 4, -2, 2 });
        DirectPosition p8 = builder.createDirectPosition(new double[] { 4, 0, 2 });

        ArrayList<DirectPosition> points1 = new ArrayList<DirectPosition>();
        points1.add(p1);
        points1.add(p2);
        points1.add(p3);
        points1.add(p4);
        points1.add(p5);
        points1.add(p6);
        points1.add(p7);
        points1.add(p8);
        
        return points1;
    }
    
    public Solid makeSolid() {
        List<DirectPosition> points = getSolidPoint();
        
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

        Hints hints = GeoTools.getDefaultHints();
        hints.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D);
        hints.put(Hints.GEOMETRY_VALIDATE, false);
        ISOGeometryBuilder builder = new ISOGeometryBuilder(hints);
        
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
}
