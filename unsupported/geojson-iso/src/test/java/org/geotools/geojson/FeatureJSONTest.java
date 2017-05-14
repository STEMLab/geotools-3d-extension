/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2010, Open Source Geospatial Foundation (OSGeo)
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

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.geotools.data.ISODataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.ISOSimpleFeatureBuilder;
import org.geotools.feature.simple.ISOSimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.iso.io.wkt.WKTReader;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * 
 *
 * @source $URL$
 */
public class FeatureJSONTest extends GeoJSONTestSupport {

	GeometryJSON gjson;
    FeatureJSON fjson;
    SimpleFeatureType featureType;
    ISOSimpleFeatureBuilder fb;
    ISOGeometryBuilder gb;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        ISOSimpleFeatureTypeBuilder tb = new ISOSimpleFeatureTypeBuilder();
        tb.setName("feature");
        tb.setSRS("EPSG:4329");
        tb.add("int", Integer.class);
        tb.add("double", Double.class);
        tb.add("string", String.class);
        tb.add("geometry", Geometry.class);
        
        featureType = tb.buildFeatureType();
        fb = new ISOSimpleFeatureBuilder(featureType);
        gb = new ISOGeometryBuilder(CRS.decode("EPSG:4329"));
        
        gjson = new GeometryJSON(gb);
        fjson = new FeatureJSON(gjson);
    }
        
    public void testFeatureWrite() throws Exception {
        
        StringWriter writer = new StringWriter();
        fjson.writeFeature(feature(1), writer);
        
        assertEquals(strip(featureText(1)), writer.toString());
    }
    
    public void testWriteReadNoProperties() throws Exception {
        ISOSimpleFeatureTypeBuilder tb = new ISOSimpleFeatureTypeBuilder();
        tb.add("geom", Point.class, CRS.decode("EPSG:4329"));
        tb.add("name", String.class);
        tb.add("quantity", Integer.class);
        tb.setName("outbreak");
        SimpleFeatureType schema = tb.buildFeatureType();
        
        ISOSimpleFeatureBuilder fb = new ISOSimpleFeatureBuilder(schema);
        fb.add(new WKTReader(CRS.decode("EPSG:4329")).read("POINT(10 20 10)"));
        SimpleFeature feature = fb.buildFeature("outbreak.1");
        
        ISOGeometryBuilder gb = new ISOGeometryBuilder(CRS.decode("EPSG:4329"));
        GeometryJSON gjson = new GeometryJSON(gb);
        FeatureJSON fj = new FeatureJSON(gjson);
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        fj.writeFeature(feature, os);
        
        String json = os.toString();
        
        // here it would break because the written json was incorrect
        SimpleFeature feature2 = fj.readFeature(json);
        assertNotNull(feature2);
        //assertEquals(feature.getID(), feature2.getID());
    }
    
    public void testFeatureRead() throws Exception {
        SimpleFeature f1 = feature(1);
        SimpleFeature f2 = fjson.readFeature(reader(strip(featureText(1)))); 
        assertEqualsLax(f1, f2);
    }

    public void testFeatureWithGeometryCollectionRead() throws Exception {
        String json = strip("{" + 
            "  'type':'Feature'," + 
            "  'geometry': {" + 
            "    'type':'GeometryCollection'," + 
            "    'geometries':[{" + 
            "        'type':'Point','coordinates':[4,6,3]" + 
            "      },{" + 
            "        'type':'LineString','coordinates':[[4,6,1],[7,10,5]]" + 
            "      }" + 
            "     ]" + 
            "  }," + 
            "  'properties':{" + 
            "    'name':'Name123'," + 
            "    'label':'Label321'," + 
            "    'roles':'[1,2,3]'" + 
            "  }," + 
            "  'id':'fid-7205cfc1_138e7ce8900_-7ffe'" + 
            "}");

        SimpleFeature f1 = fjson.readFeature(json);
        assertNotNull(f1.getDefaultGeometry());

        MultiPrimitive gc = (MultiPrimitive) f1.getDefaultGeometry();
        assertEquals(2, gc.getElements().size());

        WKTReader wkt = new WKTReader(CRS.decode("EPSG:4329"));
        
        Iterator<? extends Primitive> it = gc.getElements().iterator();
        assertTrue(wkt.read("Point (4 6 1)").equals(it.next()));
        assertTrue(wkt.read("Curve (4 6 1, 7 10 5)").equals(it.next()));

        assertEquals("fid-7205cfc1_138e7ce8900_-7ffe", f1.getID());
        assertEquals("Name123", f1.getAttribute("name"));
        assertEquals("Label321", f1.getAttribute("label"));
        assertEquals("[1,2,3]", f1.getAttribute("roles"));
    }

    public void testFeatureWithGeometryCollectionRead2() throws Exception {
        String json = strip("{"+
            "   'type':'Feature',"+
            "   'geometry':{"+
            "      'type':'GeometryCollection',"+
            "      'geometries':["+
            "         {"+
            "            'type':'Polygon',"+
            "            'coordinates':[[[-28.1107, 142.998, 5], [-28.1107, 148.623, 5], [-30.2591, 148.623, 5], [-30.2591, 142.998, 5], [-28.1107, 142.998, 5]]]"+
            "         },"+
            "         {"+
            "            'type':'Polygon',"+
            "            'coordinates':[[[-27.1765, 142.998, 5], [-25.6811, 146.4258, 5], [-27.1765, 148.5352, 5], [-27.1765, 142.998, 5]]]"+
            "         }"+
            "     ]"+
            "   },"+
            "   'properties':{"+
            "      'name':'',"+
            "      'caseSN':'x_2000a',"+
            "      'siteNum':2"+
            "   },"+
            "   'id':'fid-397164b3_13880d348b9_-7a5c'"+
            "}");
        
        SimpleFeature f1 = fjson.readFeature(json);
        assertNotNull(f1.getDefaultGeometry());

        MultiPrimitive gc = (MultiPrimitive) f1.getDefaultGeometry();
        assertEquals(2, gc.getElements().size());

        WKTReader wkt = new WKTReader(CRS.decode("EPSG:4329"));
        
        Iterator<? extends Primitive> it = gc.getElements().iterator();
        
        assertTrue(wkt.read("Surface((-28.1107 142.998 5, -28.1107 148.623  5, -30.2591 148.623  5, -30.2591 142.998 5, -28.1107 142.998 5))").equals(it.next()));
        assertTrue(wkt.read("Surface((-27.1765 142.998 5, -25.6811 146.4258 5, -27.1765 148.5352 5, -27.1765 142.998 5))").equals(it.next()));

        assertEquals("fid-397164b3_13880d348b9_-7a5c", f1.getID());
        assertEquals("", f1.getAttribute("name"));
        assertEquals("x_2000a", f1.getAttribute("caseSN"));
        assertEquals(2l, f1.getAttribute("siteNum"));

        
    }
    public void testFeatureWithRegularGeometryAttributeRead() throws Exception {
        SimpleFeature f = fjson.readFeature(reader(strip("{" + 
        "   'type': 'Feature'," +
        "   'geometry': {" +
        "     'type': 'Point'," +
        "     'coordinates': [0.1, 0.2, 0.3]," +
        "   }," +
        "   'properties': {" +
        "     'int': 1," +
        "     'double': 0.1," +
        "     'string': 'one'," +
        "     'otherGeometry': {" +
        "        'type': 'LineString'," +
        "        'coordinates': [[1.1, 1.2, 1.0], [1.3, 1.4, 1.0]]" +
        "     }"+
        "   }," +
        "   'id': 'feature.0'" +
        " }")));
        
        assertNotNull(f);
        assertTrue(f.getDefaultGeometry() instanceof Point);
        
        Point p = (Point) f.getDefaultGeometry();
        DirectPosition dp = p.getDirectPosition();
        
        assertEquals(0.1, dp.getOrdinate(0), 0.1);
        assertEquals(0.2, dp.getOrdinate(1), 0.1);
        assertEquals(0.3, dp.getOrdinate(2), 0.1);
        
        assertTrue(f.getAttribute("otherGeometry") instanceof Curve);
        /*assertTrue(new GeometryFactory().createLineString(new Coordinate[]{
            new Coordinate(1.1, 1.2), new Coordinate(1.3, 1.4)}).equals((LineString)f.getAttribute("otherGeometry")));*/
        
        assertEquals(1, ((Number)f.getAttribute("int")).intValue());
        assertEquals(0.1, ((Number)f.getAttribute("double")).doubleValue());
        assertEquals("one", f.getAttribute("string"));
    }
    
    public void testFeatureWithDefaultGeometryEqualsNullRead() throws Exception {
        SimpleFeature f = fjson.readFeature(reader(strip("{" +
        "   'type': 'Feature'," +
        "   'geometry': null," +
        "   'properties': {" +
        "     'int': 1," +
        "     'double': 0.1," +
        "     'string': 'one'" +
        "   }," +
        "   'id': 'feature.0'" +
        " }")));

        assertNotNull(f);
        assertTrue(f.getDefaultGeometry() == null);

        assertEquals(1, ((Number)f.getAttribute("int")).intValue());
        assertEquals(0.1, ((Number)f.getAttribute("double")).doubleValue());
        assertEquals("one", f.getAttribute("string"));
    }

    public void testFeatureWithRegularGeometryAttributeNoDefaultGeometryRead() throws Exception {
        SimpleFeature f = fjson.readFeature(reader(strip("{" + 
        "   'type': 'Feature'," +
        "   'properties': {" +
        "     'int': 1," +
        "     'double': 0.1," +
        "     'string': 'one'," +
        "     'otherGeometry': {" +
        "        'type': 'LineString'," +
        "        'coordinates': [[1.1, 1.2], [1.3, 1.4]]" +
        "     }"+
        "   }," +
        "   'id': 'feature.0'" +
        " }")));
        
        assertNotNull(f);
        assertTrue(f.getDefaultGeometry() instanceof Curve);
        
        Curve l = (Curve) f.getDefaultGeometry();
        
        //TODO
        //assertTrue(new GeometryFactory().createLineString(new Coordinate[]{
        //        new Coordinate(1.1, 1.2), new Coordinate(1.3, 1.4)}).equals(l));
        
        assertTrue(f.getAttribute("otherGeometry") instanceof Curve);
        //assertTrue(new GeometryFactory().createLineString(new Coordinate[]{
        //    new Coordinate(1.1, 1.2), new Coordinate(1.3, 1.4)}).equals((LineString)f.getAttribute("otherGeometry")));
        
        assertEquals(1, ((Number)f.getAttribute("int")).intValue());
        assertEquals(0.1, ((Number)f.getAttribute("double")).doubleValue());
        assertEquals("one", f.getAttribute("string"));
    }
    
    
    
    public void testFeatureWithBoundsWrite() throws Exception {
        String json = 
            "{" + 
            "   'type': 'Feature'," +
            "   'bbox': [1.1, 1.1, 1.1, 1.1], " + 
            "   'geometry': {" +
            "     'type': 'Point'," +
            "     'coordinates': [1.1, 1.1]" +
            "   }," +
            "   'properties': {" +
            "     'int': 1," +
            "     'double': 1.1," +
            "     'string': 'one'" +
            "   }," +
            "   'id': 'feature.1'" +
            " }";
        
        fjson.setEncodeFeatureBounds(true);
        assertEquals(strip(json), fjson.toString(feature(1)));
    }
    
    public void testFeatureWithCRSWrite() throws Exception {
        fjson.setEncodeFeatureCRS(true);
        assertEquals(strip(featureWithCRSText()), fjson.toString(feature(1)));
    }

    public void testFeatureNoGeometryWrite() throws Exception {
        String json = 
            "{" + 
            "   'type': 'Feature'," +
            "   'properties': {" +
            "     'foo': 'FOO'" +
            "   }," +
            "   'id': 'feature.foo'" +
            " }";
        
        ISOSimpleFeatureTypeBuilder tb = new ISOSimpleFeatureTypeBuilder();
        tb.setName("nogeom");
        tb.add("foo", String.class);
        
        SimpleFeatureType ft = tb.buildFeatureType();
        ISOSimpleFeatureBuilder b = new ISOSimpleFeatureBuilder(ft);
        b.add("FOO");
        
        SimpleFeature f = b.buildFeature("feature.foo");
        assertEquals(strip(json), fjson.toString(f));
    }
    
    String featureWithCRSText() {
        String json = 
            "{" + 
            "   'type': 'Feature'," +
            "   'crs': {" +
            "     'type': 'name'," +
            "     'properties': {" +
            "       'name': 'EPSG:4326'" + 
            "     }" +
            "   }, " + 
            "   'geometry': {" +
            "     'type': 'Point'," +
            "     'coordinates': [1.1, 1.1]" +
            "   }," +
            "   'properties': {" +
            "     'int': 1," +
            "     'double': 1.1," +
            "     'string': 'one'" +
            "   }," +
            "   'id': 'feature.1'" +
            " }";
        return json;
    }

    public void testFeatureWithCRSRead() throws Exception {
        SimpleFeature f = fjson.readFeature(reader(strip(featureWithCRSText())));
        assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:4326"), 
            f.getFeatureType().getCoordinateReferenceSystem()));
    }
    
    String featureWithBBOXText() {
        String json = 
            "{" + 
            "   'type': 'Feature'," +
            "   'bbox': [1.1, 1.1, 1.1, 1.1]," +
            "   'geometry': {" +
            "     'type': 'Point'," +
            "     'coordinates': [1.1, 1.1]" +
            "   }," +
            "   'properties': {" +
            "     'int': 1," +
            "     'double': 1.1," +
            "     'string': 'one'" +
            "   }," +
            "   'id': 'feature.1'" +
            " }";
        return json;
    }
    
    public void testFeatureWithBBOXRead() throws Exception {
        SimpleFeature f = fjson.readFeature(reader(strip(featureWithBBOXText())));
        assertEquals(1.1, f.getBounds().getMinX(), 0.1d);
        assertEquals(1.1, f.getBounds().getMaxX(), 0.1d);
        assertEquals(1.1, f.getBounds().getMinY(), 0.1d);
        assertEquals(1.1, f.getBounds().getMaxY(), 0.1d);
    }
    
    String featureWithBoundedByAttributeText() {
        String json = 
            "{" + 
            "   'type': 'Feature'," +
            "   'geometry': {" +
            "     'type': 'Point'," +
            "     'coordinates': [1.1, 1.1, 1.1]" +
            "   }," +
            "   'properties': {" +
            "     'boundedBy': [-1.2, -1.3, 0.0, 1.2, 1.3, 1.0]," +
            "     'int': 1," +
            "     'double': 1.1," +
            "     'string': 'one'" +
            "   }," +
            "   'id': 'feature.1'" +
            " }";
        return json;
    }
    
    SimpleFeature featureWithBoundedByAttribute() throws NoSuchAuthorityCodeException, FactoryException {
        ISOSimpleFeatureTypeBuilder tb = new ISOSimpleFeatureTypeBuilder();
        tb.setName("feature");
        tb.add("geometry", Point.class);
        tb.add("boundedBy", Envelope.class);
        tb.add("int", Integer.class);
        tb.add("double", Double.class);
        tb.add("string", String.class);
        
        
        ISOSimpleFeatureBuilder b = new ISOSimpleFeatureBuilder(tb.buildFeatureType());
        ISOGeometryBuilder gb = new ISOGeometryBuilder(CRS.decode("EPSG:4329"));
        
        b.add(gb.createPoint(gb.createDirectPosition(new double[] {1.1, 1.1, 1.1})));
        
        DirectPosition lower = gb.createDirectPosition(new double[] {-1.2, -1.3, 0.0});
        DirectPosition upper = gb.createDirectPosition(new double[] {1.2, 1.3, 1.0});
        
        b.add(gb.createEnvelope(lower, upper));
        b.add(1);
        b.add(1.1);
        b.add("one");
        return b.buildFeature("feature.1");
    }
    
    public void testFeatureWithBoundedByAttributeRead() throws Exception {
        SimpleFeature f = fjson.readFeature(reader(strip(featureWithBoundedByAttributeText())));
        List l = (List) f.getAttribute("boundedBy");
        
        assertEquals(-1.2, (Double) l.get(0), 0.1d);
        assertEquals(-1.3, (Double) l.get(1), 0.1d);
        assertEquals(1.2, (Double) l.get(2), 0.1d);
        assertEquals(1.3, (Double) l.get(3), 0.1d);
    }
    
    public void testFeatureWithoutPropertiesRead() throws Exception {
        SimpleFeature f = fjson.readFeature(reader(strip(featureWithoutPropertiesText())));
        assertEquals(1, f.getFeatureType().getAttributeCount());
        assertEquals("geometry", f.getFeatureType().getDescriptor(0).getLocalName());

        assertEquals(1.2, ((Point)f.getDefaultGeometry()).getDirectPosition().getOrdinate(0));
        assertEquals(3.4, ((Point)f.getDefaultGeometry()).getDirectPosition().getOrdinate(1));
        assertEquals(1.1, ((Point)f.getDefaultGeometry()).getDirectPosition().getOrdinate(2));
    }

    String featureWithoutPropertiesText() {
        String json = 
            "{" + 
            "   'type': 'Feature'," +
            "   'geometry': {" +
            "     'type': 'Point'," +
            "     'coordinates': [1.2, 3.4, 1.1]" +
            "   }," +
            "   'id': 'feature.1'" +
            " }";
        return json;
    }

    public void testFeatureWithGeometryAfterPropertiesRead() throws Exception {
        SimpleFeature f1 = feature(1);
        SimpleFeature f2 = fjson.readFeature(reader(strip(featureTextWithGeometryAfterProperties(1))));
        assertEqualsLax(f1, f2);

    }

    String featureTextWithGeometryAfterProperties(int val) {
        String text = 
            "{" +
            "  'type': 'Feature'," +
            "'  properties': {" +
            "     'int': " + val + "," +
            "     'double': " + (val + 0.1) + "," +
            "     'string': '" + toString(val) + "'" + 
            "   }," +
            "  'geometry': {" +
            "     'type': 'Point'," +
            "     'coordinates': [" + (val+0.1) + "," + (val+0.1) + "]" +
            "   }, " +
            "   'id':'feature." + val + "'" +
            "}";
            
            return text;
    }

    public void testFeatureWithBoundedByAttributeWrite() throws Exception {
        StringWriter writer = new StringWriter();
        fjson.writeFeature(featureWithBoundedByAttribute(), writer);
        assertEquals(strip(featureWithBoundedByAttributeText()), writer.toString());
    }

    public void testFeatureCollectionWrite() throws Exception {
        StringWriter writer = new StringWriter();
        fjson.writeFeatureCollection(collection(), writer);
        assertEquals(strip(collectionText()), writer.toString());
    }
    
    public void testFeatureCollectionRead() throws Exception {
        
        FeatureCollection actual = 
            fjson.readFeatureCollection(reader(strip(collectionText())));
        assertNotNull(actual);
        
        FeatureCollection expected = collection();
        assertEquals(expected.size(), actual.size());
        
        FeatureIterator a = actual.features();
        FeatureIterator e = expected.features();
        
        while(e.hasNext()) {
            assertTrue(a.hasNext());
            assertEqualsLax((SimpleFeature)e.next(), (SimpleFeature) a.next());
        }
        a.close();
        e.close();
    }
    
    public void testFeatureCollectionStreamBasic() throws Exception {
        testFeatureCollectionStream(false, false);
    }

    public void testFeatureCollectionStreamFull() throws Exception {
        testFeatureCollectionStream(true, true);
    }
    
    void testFeatureCollectionStream(boolean withBounds, boolean withCRS) throws Exception {
        FeatureIterator<SimpleFeature> features = 
            fjson.streamFeatureCollection(reader(strip(collectionText(withBounds, withCRS))));
        
        FeatureCollection expected = collection();
        FeatureIterator e = expected.features();
        
        while(e.hasNext()) {
            features.hasNext(); //ensure that hasNext() does not skip features
            assertTrue(features.hasNext());
            assertEqualsLax((SimpleFeature)e.next(), features.next());
        }
        
        features.close();
        e.close();
    }

    public void testFeatureCollectionWithBoundsWrite() throws Exception {
        fjson.setEncodeFeatureCollectionBounds(true);
        assertEquals(strip(collectionText(true, false)), fjson.toString(collection()));
    }
    
    public void testFeatureCollectionWithCRSWrite() throws Exception {
        fjson.setEncodeFeatureCollectionCRS(true);
        assertEquals(strip(collectionText(false, true)), fjson.toString(collection()));
    }

    public void testFeatureCollectionWithNonWGS84CRSWrite() throws Exception {
        String json =
            "{" +
            "  'type': 'FeatureCollection'," +
            "  'crs': {" +
            "    'type': 'name'," +
            "    'properties': {" +
            "      'name': 'EPSG:4329'" +
            "    }" +
            "  }," +
            "  'features': [" +
            "    {" +
            "      'type': 'Feature'," +
            "      'geometry': {" +
            "        'type': 'Point', " +
            "        'coordinates': [2.003750834E7, 2.003750834E7, 2.003750834E7]" +
            "      }," +
            "      'properties': {" +
            "      }," +
            "      'id': 'xyz.1'" +
            "    }" +
            "  ]" +
            "}";

        ISOSimpleFeatureTypeBuilder tb = new ISOSimpleFeatureTypeBuilder();
        tb.add("geom", Point.class, CRS.decode("EPSG:4329"));
        tb.add("name", String.class);
        tb.setName("xyz");
        SimpleFeatureType schema = tb.buildFeatureType();

        DefaultFeatureCollection fc = new DefaultFeatureCollection();

        ISOSimpleFeatureBuilder fb = new ISOSimpleFeatureBuilder(schema);
        fb.add(new WKTReader(CRS.decode("EPSG:4329")).read("Point(20037508.34 20037508.34 20037508.34)"));
        fc.add(fb.buildFeature("xyz.1"));

        ISOGeometryBuilder gb = new ISOGeometryBuilder(CRS.decode("EPSG:4329"));
        GeometryJSON gjson = new GeometryJSON(gb);
        FeatureJSON fj = new FeatureJSON(gjson);
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        fj.writeFeatureCollection(fc, os);

        assertEquals(strip(json), os.toString());
    }

    public void testFeatureCollectionWithCRSRead() throws Exception {
        String json = collectionText(true, true);
        FeatureCollection fcol = fjson.readFeatureCollection(strip(collectionText(true, true)));
        assertNotNull(fcol.getSchema().getCoordinateReferenceSystem());

        FeatureIterator it = fcol.features();
        while(it.hasNext()) {
            assertNotNull(it.next().getType().getCoordinateReferenceSystem());
        }
    }
    
    public void testFeatureCollectionWithMissingAttributeRead() throws Exception {
      String collectionText = collectionText(true, true, false, true, false);
      SimpleFeatureType ftype = fjson.readFeatureCollectionSchema((strip(collectionText)), false);
      
      assertNotNull(ftype.getDescriptor("double"));
      assertEquals(Double.class, ftype.getDescriptor("double").getType().getBinding());
      assertNotNull(ftype.getDescriptor("int"));
      assertEquals(Long.class, ftype.getDescriptor("int").getType().getBinding());
      assertNotNull(ftype.getDescriptor("string"));
      assertEquals(String.class, ftype.getDescriptor("string").getType().getBinding());
      
      assertNotNull(ftype.getCoordinateReferenceSystem());
      
      fjson.setFeatureType(ftype);
      SimpleFeatureCollection fcol = (SimpleFeatureCollection) fjson.readFeatureCollection((strip(collectionText)));
      
      assertEquals(ftype, fcol.getSchema());
      
      FeatureIterator it = fcol.features();
      while(it.hasNext()) {
        assertEquals(ftype, it.next().getType());
      }
    }
    
    @Test
    public void testFeatureCollectionWithNullAttributeRead() throws Exception {
      String collectionText = collectionText(true, true, false, false, true);
      SimpleFeatureType ftype = fjson.readFeatureCollectionSchema((strip(collectionText)), true);
      
      System.out.println("type: " + ftype);
      
      assertEquals(4, ftype.getAttributeCount());
      
      assertNotNull(ftype.getDescriptor("int"));
      assertEquals(Long.class, ftype.getDescriptor(1).getType().getBinding());
      assertNotNull(ftype.getDescriptor("double"));
      assertEquals(Double.class, ftype.getDescriptor(2).getType().getBinding());
      assertNotNull(ftype.getDescriptor("string"));
      assertEquals(String.class, ftype.getDescriptor(3).getType().getBinding());
      
      assertNotNull(ftype.getCoordinateReferenceSystem());
      
      fjson.setFeatureType(ftype);
      SimpleFeatureCollection fcol = (SimpleFeatureCollection) fjson.readFeatureCollection((strip(collectionText)));
      
      assertEquals(ftype, fcol.getSchema());
      
      FeatureIterator it = fcol.features();
      while(it.hasNext()) {
        assertEquals(ftype, it.next().getType());
      }
    }
    
    public void testFeatureCollectionWithNullAttributeAllFeaturesRead() throws Exception {
      String collectionText = collectionText(true, true, false, false, false, true);
      SimpleFeatureType ftype = fjson.readFeatureCollectionSchema((strip(collectionText)), false);
      
      assertNotNull(ftype.getDescriptor("double"));
      // type defaults to String as all values were null
      assertEquals(String.class, ftype.getDescriptor("double").getType().getBinding());
      assertNotNull(ftype.getDescriptor("int"));
      assertEquals(Long.class, ftype.getDescriptor("int").getType().getBinding());
      assertNotNull(ftype.getDescriptor("string"));
      assertEquals(String.class, ftype.getDescriptor("string").getType().getBinding());
      
      assertNotNull(ftype.getCoordinateReferenceSystem());
      
      fjson.setFeatureType(ftype);
      SimpleFeatureCollection fcol = (SimpleFeatureCollection) fjson.readFeatureCollection((strip(collectionText)));
      
      assertEquals(ftype, fcol.getSchema());
      
      FeatureIterator it = fcol.features();
      while(it.hasNext()) {
        assertEquals(ftype, it.next().getType());
      }
    }

    public void testFeatureCollectionWithCRSPostFeaturesRead() throws Exception {
        String json = collectionText(true, true);
        FeatureCollection fcol = fjson.readFeatureCollection(strip(collectionText(true, true, true, false, false)));
        assertNotNull(fcol.getSchema().getCoordinateReferenceSystem());

        FeatureIterator it = fcol.features();
        while(it.hasNext()) {
            assertNotNull(it.next().getType().getCoordinateReferenceSystem());
        }
    }

    public void testFeatureCollectionWithTypePostFeaturesRead() throws Exception {
        String json = strip("{ " +
            "  'features' : [{ " +"     'geometry' : { 'coordinates' : [ 17.633333, 59.85, 15.09], 'type' : 'Point' }," +     
            "     'type' : 'Feature'," +  
            "     'properties' : { 'name' : 'Station' }" +
            "  }]," + 
            "  'type' : 'FeatureCollection'" +
            "}");
        FeatureCollection fcol = fjson.readFeatureCollection(json);
        FeatureIterator it = fcol.features();
        assertTrue(it.hasNext());

        SimpleFeature f = (SimpleFeature) it.next();
        assertTrue(new WKTReader(CRS.decode("EPSG:4329")).read("POINT (17.633333 59.85 15.09)").equals((Geometry)f.getDefaultGeometry()));
        assertEquals("Station", f.getAttribute("name"));
        it.close();
    }

    public void testEmptyFeatureCollection() throws Exception {
        String json = strip("{'type':'FeatureCollection','features':[]}");
        FeatureCollection fcol = fjson.readFeatureCollection(json);
        assertNull(fcol.getSchema());
        assertTrue(fcol.isEmpty());
    }

    public void testCRSWrite() throws Exception {
        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
        StringWriter writer = new StringWriter();
        fjson.writeCRS(crs, writer);

        assertEquals(strip(crsText()), writer.toString());
    }

    public void testCRSRead() throws Exception {
        Object crs = fjson.readCRS(reader(strip(crsText())));
        assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("epsg:4326"), crs));
    }

    public void testFeatureCollectionWithNullBoundsWrite() throws Exception {
        DefaultFeatureCollection features = new DefaultFeatureCollection() {
            @Override
            public ReferencedEnvelope getBounds() {
                return null;
            }
        };
        features.add(feature(0));

        String json = fjson.toString(features);

    }
    
    public void testFeatureCollectionWithNullGeometrySchemaRead() throws Exception {
      String json = strip(
          "{" +
          "  'type': 'FeatureCollection'," +
          "  'features': [" +
          "    {" +
          "      'type': 'Feature'," +
          "      'geometry': null," +
          "      'properties': {" +
          "      }," +
          "      'id': 'xyz.1'" +
          "    }" +
          "  ]" +
          "}");

      SimpleFeatureType type = fjson.readFeatureCollectionSchema(json, true);
      assertNull(type.getGeometryDescriptor());
    }

    public void testFeatureCollectionWithoutGeometrySchemaRead() throws Exception {
      String json = strip(
          "{" +
          "  'type': 'FeatureCollection'," +
          "  'features': [" +
          "    {" +
          "      'type': 'Feature'," +
          "      'properties': {" +
          "      }," +
          "      'id': 'xyz.1'" +
          "    }" +
          "  ]" +
          "}");

      SimpleFeatureType type = fjson.readFeatureCollectionSchema(json, true);
      assertNull(type.getGeometryDescriptor());
    }

    public void testFeatureCollectionConflictingTypesSchemaRead() throws Exception {
      String json = strip(
          "{" +
          "  'type': 'FeatureCollection'," +
          "  'features': [" +
          "    {" +
          "      'type': 'Feature'," +
          "      'properties': {" +
          "         'prop': 1" +
          "      }" +
          "    }," +
          "    {" +
          "      'type': 'Feature'," +
          "      'properties': {" +
          "        'prop': 'xyz'" +
          "      }" +
          "    }" +
          "  ]" +
          "}");

      try {
        fjson.readFeatureCollectionSchema(json, false);
        fail("Should have thrown IllegalStateException");
      } catch (IllegalStateException e) {
      }
    }
    
    public void testFeatureCollectionWithoutGeometryReadWriteFromFeatureSource() throws Exception {
      String json = strip(
          "{" +
          "  'type': 'FeatureCollection'," +
          "  'features': [" +
          "    {" +
          "      'type': 'Feature'," +
          "      'properties': {" +
          "      }," +
          "      'id': 'xyz.1'" +
          "    }" +
          "  ]" +
          "}");

      SimpleFeatureSource fs = ISODataUtilities.source(fjson.readFeatureCollection(json));
      fjson.toString(fs.getFeatures());
    }
    
    public void testFeatureCollectionConflictingButInterchangeableTypesSchemaRead() throws Exception {
      String json = strip(
          "{" +
          "  'type': 'FeatureCollection'," +
          "  'features': [" +
          "    {" +
          "      'type': 'Feature'," +
          "      'properties': {" +
          "         'prop': 1" +
          "      }" +
          "    }," +
          "    {" +
          "      'type': 'Feature'," +
          "      'properties': {" +
          "        'prop': 1.0" +
          "      }" +
          "    }" +
          "  ]" +
          "}");

        SimpleFeatureType type = fjson.readFeatureCollectionSchema(json, false);
        assertEquals(Double.class, type.getDescriptor("prop").getType().getBinding());
    }

    public void testFeatureCollectionWithIdPropertyReadWrite() throws Exception {
     
      String json = strip(
          "{" +
          "  'type': 'FeatureCollection'," +
          "  'features': [" +
          "    {" +
          "      'type': 'Feature'," +
          "      'properties': {" +
          "         'id': 'one'" +
          "      }," +
          "      'id': 'xyz.1'" +
          "    }" +
          "  ]" +
          "}");
      
      FeatureCollection fc = fjson.readFeatureCollection(json);
      assertNotNull(fc.getSchema().getDescriptor("id"));
      Feature feat = fc.features().next();
      assertEquals("one", feat.getProperty("id").getValue());
      assertEquals("xyz.1", feat.getIdentifier().getID());
      
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      fjson.writeFeatureCollection(fc, os);

      assertEquals(json, os.toString());
    }

    String crsText() {
        return 
            "{" + 
            "    'type': 'name',"+
            "    'properties': {"+
            "       'name': 'EPSG:4326'"+
            "     }"+
            "}";
    }
    
    SimpleFeature feature(int val) {
        fb.add(val);
        fb.add(val + 0.1);
        fb.add(toString(val));
        fb.add(gb.createPoint(gb.createDirectPosition(new double[] { val+0.1,val+0.1,val+0.1})));
        
        return fb.buildFeature("feature." + val);
    }
    
    SimpleFeature featureMissingAttribute(int val) {
      fb.add(val);
      fb.add(val + 0.1);
      fb.add(gb.createPoint(gb.createDirectPosition(new double[] { val+0.1,val+0.1,val+0.1})));
      
      return fb.buildFeature("feature." + val);
    }
    
    SimpleFeature featureNullAttribute(int val) {
      fb.add(val);
      fb.add(null);
      fb.add(toString(val));
      fb.add(gb.createPoint(gb.createDirectPosition(new double[] { val+0.1,val+0.1,val+0.1})));
      
      return fb.buildFeature("feature." + val);
    }
    
    String featureText(int val) {
      return featureText(val, false, false);
    }
    
    String featureText(int val, boolean missingAttribute, boolean nullAttribute) {
        if (missingAttribute && nullAttribute) {
          throw new IllegalArgumentException("For tests, use only one of either missingAttribute or nullAttribute");
        }
        String text = 
        "{" +
        "  'type': 'Feature'," +
        "  'geometry': {" +
        "     'type': 'Point'," +
        "     'coordinates': [" + (val+0.1) + "," + (val+0.1) + "]" +
        "   }, " +
        "'  properties': {" +
        "     'int': " + val + "," +
        (missingAttribute ? "" : (nullAttribute ? ("     'double': null,") : (
        "     'double': " + (val + 0.1) + ","))) +
        "     'string': '" + toString(val) + "'" + 
        "   }," +
        "   'id':'feature." + val + "'" +
        "}";
        
        return text;
    }

    FeatureCollection collection() {
        DefaultFeatureCollection collection = new DefaultFeatureCollection(null, featureType);
        for (int i = 0; i < 3; i++) {
            collection.add(feature(i));
        }
        return collection;
    }
    
    String collectionText() {
        return collectionText(false,false);
    }
    
    String collectionText(boolean withBounds, boolean withCRS) {
        return collectionText(withBounds, withCRS, false, false, false, false);
    }
    
    String collectionText(boolean withBounds, boolean withCRS, boolean crsAfter, boolean missingFirstFeatureAttribute, boolean nullFirstFeatureAttribute) {
      return collectionText(withBounds, withCRS, crsAfter, missingFirstFeatureAttribute, nullFirstFeatureAttribute, false);
    }

    String collectionText(boolean withBounds, boolean withCRS, boolean crsAfter, boolean missingFirstFeatureAttribute, boolean nullFirstFeatureAttribute, boolean nullAttributeAllFeatures) {
        StringBuffer sb = new StringBuffer();
        sb.append("{'type':'FeatureCollection',");
        if (withBounds) {
            FeatureCollection features = collection();
            ReferencedEnvelope bbox = features.getBounds();
            sb.append("'bbox': [");
            sb.append(bbox.getMinX()).append(",").append(bbox.getMinY()).append(",")
                .append(bbox.getMaxX()).append(",").append(bbox.getMaxY());
            sb.append("],");
        }
        if (withCRS && !crsAfter) {
            sb.append("'crs': {");
            sb.append("  'type': 'name',");
            sb.append("  'properties': {");
            sb.append("    'name': 'EPSG:4326'");
            sb.append("   }");
            sb.append("},");
        }
        sb.append("'features':[");
        if (nullAttributeAllFeatures) {
            // creates all features with a null attribute
            for (int i = 0; i < 3; i++) {
                sb.append(featureText(i, false, true)).append(",");
            }
        } else {
            // only the first feature will have a null or missing attribute
            sb.append(featureText(0, missingFirstFeatureAttribute, nullFirstFeatureAttribute)).append(",");
            for (int i = 1; i < 3; i++) {
                sb.append(featureText(i, false, false)).append(",");
            }
        }
        sb.setLength(sb.length()-1);
        sb.append("]");
        if (withCRS && crsAfter) {
            sb.append(",'crs': {");
            sb.append("  'type': 'name',");
            sb.append("  'properties': {");
            sb.append("    'name': 'EPSG:4326'");
            sb.append("   }");
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }
}
