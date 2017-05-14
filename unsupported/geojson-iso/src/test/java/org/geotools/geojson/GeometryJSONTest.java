/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2016, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.geojson;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.referencing.CRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

/**
 * 
 *
 * @source $URL$
 */
public class GeometryJSONTest extends GeoJSONTestSupport {

    ISOGeometryBuilder gb;
    ISOGeometryBuilder gb3D;
    
    GeometryJSON gjson;
    GeometryJSON gjson3D;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gb = new ISOGeometryBuilder(CRS.decode("EPSG:4326"));
        gb3D = new ISOGeometryBuilder(CRS.decode("EPSG:4329"));
        
        gjson = new GeometryJSON(gb);
        gjson3D = new GeometryJSON(gb3D);
    }
    
    public void testPointWrite() throws Exception {
        assertEquals(pointText(), gjson.toString(point()));
        assertEquals(point3dText(), gjson3D.toString(point3d()));
    }
    
    String pointText() {
        return strip("{'type': 'Point','coordinates':[100.1,0.1]}");
    }

    Point point() {
        Point p = gb.createPoint(gb.createDirectPosition(new double[] {100.1, 0.1}));
        return p;
    }
    
    String point3dText() {
        return strip("{'type': 'Point','coordinates':[100.1,0.1,10.2]}");
    }

    Point point3d() {
        Point p = gb3D.createPoint(gb3D.createDirectPosition(new double[] {100.1, 0.1, 10.2}));
        return p;
    }
    
    public void testPointRead() throws Exception {
        assertTrue(point().equals(gjson.readPoint(reader(pointText()))));
        assertTrue(point3d().equals(gjson3D.readPoint(reader(point3dText()))));
    }
     
    public void testLineWrite() throws Exception {
        assertEquals(lineText(), gjson.toString(line()));
        assertEquals(line2Text(), gjson.toString(line2()));
        assertEquals(line3dText(), gjson3D.toString(line3d()));
    }
    
    String lineText() {
        return strip(
            "{'type': 'LineString', 'coordinates': [[100.1,0.1],[101.1,1.1]]}");
    }

    String line2Text() {
        return strip("null");
    }

    Curve line() {
    	Curve l = gb.createCurve(array(gb, new double[][]{{100.1, 0.1},{101.1,1.1}}));
        return l;
    }

    Curve line2() {
    	Curve l = gb.createCurve(array(gb, new double[][]{}));
        return l;
    }
    
    String line3dText() {
        return strip(
            "{'type': 'LineString', 'coordinates': [[100.1,0.1,10.2],[101.1,1.1,10.2]]}");
    }

    Curve line3d() {
    	Curve l = gb3D.createCurve(array(gb3D, new double[][]{{100.1, 0.1, 10.2},{101.1,1.1, 10.2}}));
        return l;
    }
    
    public void testLineRead() throws Exception {
        assertTrue(line().equals(gjson.readLine(reader(lineText()))));
        assertNull(gjson.readLine(reader(line2Text())));
        assertTrue(line3d().equals(gjson3D.readLine(reader(line3dText()))));
    }
       
    public void testPolyWrite() throws Exception {
        assertEquals(polygonText1(), gjson.toString(polygon1()));
        assertEquals(polygonText2(), gjson.toString(polygon2()));
        assertEquals(polygonText3(), gjson3D.toString(polygon3()));
    }

    Surface polygon2() {
    	Surface poly;
    	SurfaceBoundary sb;
    	
    	Ring ext = gb.createRing(
				Arrays.asList(
						gb.createCurve( array(gb, new double[][]{ 
							{100.1, 0.1}, {101.1, 0.1}, {101.1, 1.1}, {100.1, 1.1}, {100.1, 0.1}
							}))));
    	
    	List<Ring> interior = 
    			Arrays.asList(
    					gb.createRing(
    							Arrays.asList(gb.createCurve(array(gb, new double[][]{
    								{100.2, 0.2}, {100.8, 0.2}, {100.8, 0.8}, {100.2, 0.8}, {100.2, 0.2}})))));
    	
    	sb = gb.createSurfaceBoundary(ext, interior);
    	poly = gb.createSurface(sb);
        return poly;
    }
    
    Surface polygon3() {
    	Surface poly;
    	SurfaceBoundary sb;
    	
    	Ring ext = gb3D.createRing(
				Arrays.asList(
						gb3D.createCurve( array(gb3D, new double[][]{ 
							{100.1, 0.1, 10.2}, {101.1, 0.1, 11.2}, {101.1, 1.1, 11.2}, {100.1, 1.1, 10.2}, {100.1, 0.1, 10.2}
							}))));
    	
    	List<Ring> interior = 
    			Arrays.asList(
    					gb3D.createRing(
    							Arrays.asList(gb3D.createCurve(array(gb3D, new double[][]{
            {100.2, 0.2, 10.2}, {100.8, 0.2, 11.2}, {100.8, 0.8, 11.2}, {100.2, 0.8, 10.2}, {100.2, 0.2, 10.2}})))));
    	
    	sb = gb3D.createSurfaceBoundary(ext, interior);
    	poly = gb3D.createSurface(sb);
        return poly;
    }
    
    String polygonText3() {
        return strip("{ 'type': 'Polygon',"+
        "    'coordinates': ["+
        "      [ [100.1, 0.1, 10.2], [101.1, 0.1, 11.2], [101.1, 1.1, 11.2], [100.1, 1.1, 10.2], [100.1, 0.1, 10.2] ],"+
        "      [ [100.2, 0.2, 10.2], [100.8, 0.2, 11.2], [100.8, 0.8, 11.2], [100.2, 0.8, 10.2], [100.2, 0.2, 10.2] ]"+
        "      ]"+
        "   }");
    }

    String polygonText1() {
        return strip("{ 'type': 'Polygon',"+
        "'coordinates': ["+
        "  [ [100.1, 0.1], [101.1, 0.1], [101.1, 1.1], [100.1, 1.1], [100.1, 0.1] ]"+
        "  ]"+
         "}");
    }
    
    String polygonText2() {
        return strip("{ 'type': 'Polygon',"+
        "    'coordinates': ["+
        "      [ [100.1, 0.1], [101.1, 0.1], [101.1, 1.1], [100.1, 1.1], [100.1, 0.1] ],"+
        "      [ [100.2, 0.2], [100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2] ]"+
        "      ]"+
        "   }");
    }

    Surface polygon1() {
    	Surface poly;
    	SurfaceBoundary sb;
    	
    	Ring ext = gb.createRing(
				Arrays.asList(
						gb.createCurve( array(gb, new double[][]{ 
							{100.1, 0.1}, {101.1, 0.1}, {101.1, 1.1}, {100.1, 1.1}, {100.1, 0.1}
						}))));
    	
    	sb = gb3D.createSurfaceBoundary(ext);
    	poly = gb3D.createSurface(sb);
        return poly;
    }
    
    public void testPolyRead() throws Exception {
        assertTrue(polygon1().equals(gjson.readSurface(reader(polygonText1()))));
        assertTrue(polygon2().equals(gjson.readSurface(reader(polygonText2()))));
        assertTrue(polygon3().equals(gjson3D.readSurface(reader(polygonText3()))));
    }
    
    public void testMultiPointWrite() throws Exception {
        assertEquals(multiPointText(), gjson.toString(multiPoint()));
        assertEquals(multiPoint3dText(), gjson3D.toString(multiPoint3d()));
    }

    String multiPointText() {
        return strip(
            "{ 'type': 'MultiPoint',"+
                "'coordinates': [ [100.1, 0.1], [101.1, 1.1] ]"+
            "}");
    }

    MultiPoint multiPoint() {
    	PointArray pa = array(gb, new double[][]{{100.1, 0.1}, {101.1, 1.1}});
    	
    	Set<Point> ps = new HashSet<Point>();
    	for(Position dp : pa) {
    		ps.add(gb.createPoint(dp));
    	}
    	
    	MultiPoint mpoint = gb.createMultiPoint(ps);
        return mpoint;
    }
    
    String multiPoint3dText() {
        return strip(
            "{ 'type': 'MultiPoint',"+
                "'coordinates': [ [100.1, 0.1, 10.2], [101.1, 1.1, 11.2] ]"+
            "}");
    }

    MultiPoint multiPoint3d() {
    	PointArray pa = array(gb3D, new double[][]{{100.1, 0.1, 10.2}, {101.1, 1.1, 11.2}});
    	
    	Set<Point> ps = new HashSet<Point>();
    	for(Position dp : pa) {
    		ps.add(gb3D.createPoint(dp));
    	}
    	
    	MultiPoint mpoint = gb3D.createMultiPoint(ps);
        return mpoint;
    }
    
    public void testMultiPointRead() throws Exception {
        assertTrue(multiPoint().equals(gjson.readMultiPoint(reader(multiPointText()))));
        assertTrue(multiPoint3d().equals(gjson3D.readMultiPoint(reader(multiPoint3dText()))));
    }
    
    public void testMultiLineWrite() throws Exception {
        assertEquals(multiLineText(), gjson.toString(multiLine()));
        assertEquals(multiLine3dText(), gjson3D.toString(multiLine3d()));
    }

    String multiLineText() {
        return strip(
            "{ 'type': 'MultiLineString',"+
            "    'coordinates': ["+
            "        [ [100.1, 0.1], [101.1, 1.1] ],"+
            "        [ [102.1, 2.1], [103.1, 3.1] ]"+
            "      ]"+
            "    }");
    }

    MultiCurve multiLine() {
    	Set<Curve> ps = new HashSet<Curve>();
    	ps.add(gb.createCurve(array(gb, new double[][]{{100.1, 0.1}, {101.1, 1.1}})));
    	ps.add(gb.createCurve(array(gb, new double[][]{{102.1, 2.1}, {103.1, 3.1}})));
    	
    	MultiCurve mline = gb.createMultiCurve(ps);
        return mline;
    }
    
    String multiLine3dText() {
        return strip(
            "{ 'type': 'MultiLineString',"+
            "    'coordinates': ["+
            "        [ [100.1, 0.1, 10.2], [101.1, 1.1, 10.2] ],"+
            "        [ [102.1, 2.1, 11.2], [103.1, 3.1, 11.2] ]"+
            "      ]"+
            "    }");
    }

    MultiCurve multiLine3d() {
    	Set<Curve> ps = new HashSet<Curve>();
    	ps.add(gb3D.createCurve(array(gb3D, new double[][]{{100.1, 0.1, 10.2}, {101.1, 1.1, 10.2}})));
    	ps.add(gb3D.createCurve(array(gb3D, new double[][]{{102.1, 2.1, 11.2}, {103.1, 3.1, 11.2}})));
    	
    	MultiCurve mline = gb.createMultiCurve(ps);
        return mline;
    }
    
    public void testMultiLineRead() throws Exception {
        assertTrue(multiLine().equals(gjson.readMultiCurve(reader(multiLineText()))));
        assertTrue(multiLine3d().equals(gjson3D.readMultiCurve(reader(multiLine3dText()))));
    }
    
    public void testMultiPolygonWrite() throws Exception {
        assertEquals(multiPolygonText(), gjson.toString(multiPolygon()));
        assertEquals(multiPolygon3dText(), gjson3D.toString(multiPolygon3d()));
    }

    String multiPolygonText() {
        return strip(
        "{ 'type': 'MultiPolygon',"+
        "    'coordinates': ["+
        "      [[[102.1, 2.1], [103.1, 2.1], [103.1, 3.1], [102.1, 3.1], [102.1, 2.1]]],"+
        "      [[[100.1, 0.1], [101.1, 0.1], [101.1, 1.1], [100.1, 1.1], [100.1, 0.1]],"+
        "       [[100.2, 0.2], [100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2]]]"+
        "      ]"+
        "    }");
    }

    MultiSurface multiPolygon() {
    	Surface poly1;
    	SurfaceBoundary sb;
    	Ring ext = gb.createRing(
				Arrays.asList(
						gb.createCurve( array(gb, new double[][]{ 
							{102.1, 2.1}, {103.1, 2.1}, {103.1, 3.1}, {102.1, 3.1}, {102.1, 2.1}
						}))));
    	
    	sb = gb.createSurfaceBoundary(ext);
    	poly1 = gb.createSurface(sb);
    	
    	
    	ext = gb.createRing(
				Arrays.asList(
						gb.createCurve( array(gb, new double[][]{ 
							{100.1, 0.1}, {101.1, 0.1}, {101.1, 1.1}, {100.1, 1.1}, {100.1, 0.1}
							}))));
    	List<Ring> interior = 
    			Arrays.asList(
    					gb.createRing(
    							Arrays.asList(gb.createCurve(array(gb, new double[][]{
    								{100.2, 0.2}, {100.8, 0.2}, {100.8, 0.8}, {100.2, 0.8}, {100.2, 0.2}})))));
    	
    	sb = gb.createSurfaceBoundary(ext, interior);
    	Surface poly2 = gb.createSurface(sb);
    	
    	Set<Surface> ss = new HashSet<Surface>();
    	ss.add(poly1);
    	ss.add(poly2);
    	
    	MultiSurface mpoly = gb.createMultiSurface(ss);
        return mpoly;
    }
    
    String multiPolygon3dText() {
        return strip(
        "{ 'type': 'MultiPolygon',"+
        "    'coordinates': ["+
        "      [[[102.1, 2.1, 10.2], [103.1, 2.1, 10.2], [103.1, 3.1, 10.2], [102.1, 3.1, 10.2], [102.1, 2.1, 10.2]]],"+
        "      [[[100.1, 0.1, 10.2], [101.1, 0.1, 10.2], [101.1, 1.1, 10.2], [100.1, 1.1, 10.2], [100.1, 0.1, 10.2]],"+
        "       [[100.2, 0.2, 10.2], [100.8, 0.2, 10.2], [100.8, 0.8, 10.2], [100.2, 0.8, 10.2], [100.2, 0.2, 10.2]]]"+
        "      ]"+
        "    }");
    }

    MultiSurface multiPolygon3d() {
    	Surface poly1;
    	SurfaceBoundary sb;
    	Ring ext = gb3D.createRing(
				Arrays.asList(
						gb3D.createCurve( array(gb3D, new double[][]{ 
							{102.1, 2.1, 10.2}, {103.1, 2.1, 10.2}, {103.1, 3.1, 10.2}, {102.1, 3.1, 10.2}, {102.1, 2.1, 10.2}
						}))));
    	
    	sb = gb3D.createSurfaceBoundary(ext);
    	poly1 = gb3D.createSurface(sb);
    	
    	
    	ext = gb3D.createRing(
				Arrays.asList(
						gb3D.createCurve( array(gb3D, new double[][]{ 
							{100.1, 0.1, 10.2}, {101.1, 0.1, 10.2}, {101.1, 1.1, 10.2}, {100.1, 1.1, 10.2}, {100.1, 0.1, 10.2}
							}))));
    	List<Ring> interior = 
    			Arrays.asList(
    					gb3D.createRing(
    							Arrays.asList(gb3D.createCurve(array(gb3D, new double[][]{
    								{100.2, 0.2, 10.2}, {100.8, 0.2, 10.2}, {100.8, 0.8, 10.2}, {100.2, 0.8, 10.2}, {100.2, 0.2, 10.2}})))));
    	
    	sb = gb3D.createSurfaceBoundary(ext, interior);
    	Surface poly2 = gb3D.createSurface(sb);
    	
    	Set<Surface> ss = new HashSet<Surface>();
    	ss.add(poly1);
    	ss.add(poly2);
    	
    	MultiSurface mpoly = gb3D.createMultiSurface(ss);
        return mpoly;
    }
    
    public void testMultiPolygonRead() throws IOException {
        assertTrue(multiPolygon().equals(gjson.readMultiSurface(reader(multiPolygonText()))));
        assertTrue(multiPolygon3d().equals(gjson3D.readMultiSurface(reader(multiPolygon3dText()))));
    }
    
    public void testGeometryCollectionWrite() throws Exception {
        assertEquals(collectionText(), gjson.toString(collection()));
        assertEquals(collection3dText(), gjson3D.toString(collection3d()));
    }

    private String collectionText() {
        return strip(
            "{ 'type': 'MultiPrimitive',"+
            "    'geometries': ["+
            "      { 'type': 'Point',"+
            "        'coordinates': [100.1, 0.1]"+
            "        },"+
            "      { 'type': 'LineString',"+
            "        'coordinates': [ [101.1, 0.1], [102.1, 1.1] ]"+
            "        }"+
            "    ]"+
            "  }");
    }

    private String collectionTypeLastText() {
        return strip(
                "{ "+
                        "    'geometries': ["+
                        "      { 'type': 'Point',"+
                        "        'coordinates': [100.1, 0.1]"+
                        "        },"+
                        "      { 'type': 'LineString',"+
                        "        'coordinates': [ [101.1, 0.1], [102.1, 1.1] ]"+
                        "        }"+
                        "    ], "+
                        "    'type': 'MultiPrimitive'" +
                        "  }");
    }

    MultiPrimitive collection() {
    	Set<Primitive> ps = new HashSet<Primitive>();
    	ps.add(gb.createPoint(gb.createDirectPosition(new double[] {100.1,0.1})));
    	ps.add(gb.createCurve(array(gb, new double[][]{{101.1, 0.1}, {102.1, 1.1}})));
        MultiPrimitive gcol = gb3D.createMultiPrimitive(ps);
        return gcol;
    }
    
    private String collection3dText() {
        return strip(
            "{ 'type': 'MultiPrimitive',"+
            "    'geometries': ["+
            "      { 'type': 'Point',"+
            "        'coordinates': [100.1, 0.1, 10.2]"+
            "        },"+
            "      { 'type': 'LineString',"+
            "        'coordinates': [ [101.1, 0.1, 10.2], [102.1, 1.1, 11.2] ]"+
            "        }"+
            "    ]"+
            "  }");
    }

    MultiPrimitive collection3d() {
    	Set<Primitive> ps = new HashSet<Primitive>();
    	ps.add(gb3D.createPoint(gb3D.createDirectPosition(new double[] {100.1,0.1, 10.2})));
    	ps.add(gb3D.createCurve(array(gb3D, new double[][]{{101.1, 0.1, 10.2}, {102.1, 1.1, 11.2}})));
        MultiPrimitive gcol = gb3D.createMultiPrimitive(ps);
        return gcol;
    }
    
    public void testGeometryCollectionRead() throws Exception {
        assertEqual(collection(), 
            (MultiPrimitive)gjson.readMultiPrimitive(reader(collectionText())));
        assertEqual(collection3d(), 
            (MultiPrimitive)gjson.readMultiPrimitive(reader(collection3dText())));
    }
    
    public void testRead() throws Exception {
        assertTrue(point().equals(gjson.read(reader(pointText()))));
        assertTrue(point3d().equals(gjson3D.read(reader(point3dText()))));
        assertTrue(line().equals(gjson.read(reader(lineText()))));
        assertTrue(line3d().equals(gjson3D.read(reader(line3dText()))));
        assertTrue(polygon1().equals(gjson.read(reader(polygonText1()))));
        assertTrue(polygon2().equals(gjson.read(reader(polygonText2()))));
        assertTrue(polygon3().equals(gjson3D.read(reader(polygonText3()))));
        assertTrue(multiPoint().equals(gjson.read(reader(multiPointText()))));
        assertTrue(multiPoint3d().equals(gjson3D.read(reader(multiPoint3dText()))));
        assertTrue(multiLine().equals(gjson.read(reader(multiLineText()))));
        assertTrue(multiLine3d().equals(gjson3D.read(reader(multiLine3dText()))));
        assertTrue(multiPolygon().equals(gjson.read(reader(multiPolygonText()))));
        assertTrue(multiPolygon3d().equals(gjson3D.read(reader(multiPolygon3dText()))));
        
        assertEqual(collection(), (MultiPrimitive) gjson.read(reader(collectionText())));
        assertEqual(collection3d(), (MultiPrimitive) gjson3D.read(reader(collection3dText())));
    }

    public void testReadOrder() throws Exception {
        String json = strip("{'coordinates':[100.1,0.1], 'type': 'Point'}");
        assertTrue(point().equals(gjson.read(reader(json))));

        json = strip("{'coordinates': [[100.1,0.1],[101.1,1.1]], 'type': 'LineString'}");
        assertTrue(line().equals(gjson.read(reader(json))));

        json = strip("{ 'coordinates': ["+
            "      [ [100.1, 0.1, 10.2], [101.1, 0.1, 11.2], [101.1, 1.1, 11.2], [100.1, 1.1, 10.2], [100.1, 0.1, 10.2] ],"+
            "      [ [100.2, 0.2, 10.2], [100.8, 0.2, 11.2], [100.8, 0.8, 11.2], [100.2, 0.8, 10.2], [100.2, 0.2, 10.2] ]"+
            "      ]"+
            ", 'type': 'Polygon' }");
        assertTrue(polygon3().equals(gjson3D.read(reader(json))));

        json = strip(
            "{ 'coordinates': [ [100.1, 0.1], [101.1, 1.1] ], 'type': 'MultiPoint'}");
        assertTrue(multiPoint().equals(gjson.read(reader(json))));
        
        json = strip(
        "{ 'coordinates': ["+
                "        [ [100.1, 0.1], [101.1, 1.1] ],"+
                "        [ [102.1, 2.1], [103.1, 3.1] ]"+
                "      ]"+
                "    , 'type': 'MultiLineString'}");
        assertTrue(multiLine().equals(gjson.read(reader(json))));
        
        json = strip(
            "{ 'coordinates': ["+
            "      [[[102.1, 2.1], [103.1, 2.1], [103.1, 3.1], [102.1, 3.1], [102.1, 2.1]]],"+
            "      [[[100.1, 0.1], [101.1, 0.1], [101.1, 1.1], [100.1, 1.1], [100.1, 0.1]],"+
            "       [[100.2, 0.2], [100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2]]]"+
            "      ]"+
            "   , 'type': 'MultiPolygon' }");
        assertTrue(multiPolygon().equals(gjson.read(reader(json))));
    }

    void assertEqual(MultiPrimitive col1, MultiPrimitive col2) {
        assertEquals(col1.getElements().size(), col2.getElements().size());
        for (int i = 0; i < col1.getElements().size(); i++) {
        	assertTrue(col1.equals(col2));
        }
    }
    
    PointArray array(ISOGeometryBuilder gb, double[][] coords) {
    	PointArray pa = gb.createPointArray();
    	for (int i = 0; i < coords.length; i++) {
            DirectPosition c = gb.createDirectPosition(coords[i].clone());
            pa.add(c);
        }
        return pa;
    }

    public void testGeometryCollectionReadTypeLast() throws IOException {
        Object obj = gjson.read(collectionTypeLastText());
        assertTrue(obj instanceof MultiPrimitive);

        MultiPrimitive gc = (MultiPrimitive) obj;
        assertEquals(2, gc.getElements().size());

        Iterator<? extends Primitive> it = gc.getElements().iterator();
        
        assertTrue(it.next() instanceof Point);
        assertTrue(it.next() instanceof Curve);
    }
    
    public void testPointOrderParsing() throws Exception {
        String input1 = "{\n" + "  \"type\": \"Point\",\n" + "  \"coordinates\": [10, 10]\n" + "}";
        String input2 = "{\n" + "  \"coordinates\": [10, 10],\n" + "  \"type\": \"Point\"\n" + "}";
        org.geotools.geojson.geom.GeometryJSON geometryJSON = new org.geotools.geojson.geom.GeometryJSON(gb);
        Point p1 = geometryJSON.readPoint(input1);
        assertEquals(10, p1.getDirectPosition().getOrdinate(0), 0d);
        assertEquals(10, p1.getDirectPosition().getOrdinate(1), 0d);
        Point p2 = geometryJSON.readPoint(input2);
        assertEquals(10, p2.getDirectPosition().getOrdinate(0), 0d);
        assertEquals(10, p2.getDirectPosition().getOrdinate(1), 0d);
    }
    
}
