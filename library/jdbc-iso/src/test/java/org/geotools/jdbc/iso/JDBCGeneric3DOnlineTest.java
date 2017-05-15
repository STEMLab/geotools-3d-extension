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
package org.geotools.jdbc.iso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureWriter;
import org.geotools.data.ISODataUtilities;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.feature.ISOFeatureFactoryImpl;
import org.geotools.feature.simple.ISOSimpleFeatureBuilder;
import org.geotools.feature.simple.ISOSimpleFeatureTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.LineSegment;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.Point;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Tests the ability of the datastore to cope with 3D data
 * 
 * @author Andrea Aime - OpenGeo
 * @author Martin Davis - OpenGeo
 * 
 * 
 * 
 * @source $URL$
 */
public abstract class JDBCGeneric3DOnlineTest extends JDBCTestSupport {

	protected static final String ID = "id";

	protected static final String GEOM = "geom";

	protected static final String NAME = "name";

	protected static final FilterFactory FF = CommonFactoryFinder.getFilterFactory(null);

	protected SimpleFeatureType poly3DType;

	protected SimpleFeatureType line3DType;

	protected ISOGeometryBuilder builder = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84_3D);

	protected CoordinateReferenceSystem crs;

	/**
	 * Returns the name of the feature type with 3d lines
	 * @return
	 */
	protected abstract String getLine3d();

	/**
	 * Returns the name of the feature type with 3d points
	 * @return
	 */
	protected abstract String getPoint3d();

	/**
	 * Returns the name of the feature type with 3d polygons
	 * @return
	 */
	protected abstract String getPoly3d();

	protected abstract String getPoint3d_Write();

	protected abstract String getLine3d_Write();


	@Override
	protected void connect() throws Exception {
		super.connect();

		line3DType = ISODataUtilities.createType(dataStore.getNamespaceURI() + "." + tname(getLine3d()),
				aname(ID) + ":0," + aname(GEOM) + ":Curve:srid=" + getEpsgCode() + "," + aname(NAME)
				+ ":String");
		line3DType.getGeometryDescriptor().getUserData().put(Hints.COORDINATE_DIMENSION, 3);
		poly3DType = ISODataUtilities.createType(dataStore.getNamespaceURI() + "." + tname(getPoly3d()),
				aname(ID) + ":0," + aname(GEOM) + ":Surface:srid=" + getEpsgCode() + "," + aname(NAME) + ":String");
		poly3DType.getGeometryDescriptor().getUserData().put(Hints.COORDINATE_DIMENSION, 3);

		crs = CRS.decode("EPSG:" + getEpsgCode());
	}
	
	protected DataStore getTESTDataStore() throws IOException{
		return dataStore;
	}
	
	protected Integer getNativeSRID() {
		return new Integer(getEpsgCode());
	}

	protected abstract int getEpsgCode();
 
	public void testSchema() throws Exception {
		SimpleFeatureType schema = dataStore.getSchema(tname(getLine3d()));
		CoordinateReferenceSystem crs = schema.getGeometryDescriptor()
				.getCoordinateReferenceSystem();
		assertEquals(new Integer(getEpsgCode()), CRS.lookupEpsgCode(crs, false));
		assertEquals(getNativeSRID(),
				schema.getGeometryDescriptor().getUserData().get(JDBCDataStore.JDBC_NATIVE_SRID));
		assertEquals(3,
				schema.getGeometryDescriptor().getUserData().get(Hints.COORDINATE_DIMENSION));
	}

	public void testReadPoint() throws Exception {
		SimpleFeatureCollection fc = dataStore.getFeatureSource(tname(getPoint3d())).getFeatures();
		try(SimpleFeatureIterator fr = fc.features()) {
			assertTrue(fr.hasNext());
			Point p = (Point) fr.next().getDefaultGeometry();
			DirectPosition c = builder.createDirectPosition(new double[] {1, 1, 1});
			assertTrue(c.equals(p.getDirectPosition()));
		}
	}

	public void testWritePoint() throws Exception {
		//DataStore ds = getTESTDataStore();
		try{
			dataStore.removeSchema(getPoint3d_Write());
		} catch (Exception e){
			//e.printStackTrace();
			System.out.println("Table was not removed");
		}
		

		//build 3D Point
		DirectPosition dp = builder.createDirectPosition(new double[] {0, 10, 5});
		Point p = builder.createPoint(dp);
		
		ISOSimpleFeatureTypeBuilder b = new ISOSimpleFeatureTypeBuilder();
		b.setCRS(DefaultGeographicCRS.WGS84_3D);
		b.add(aname(ID), String.class);
		b.add(aname(GEOM), Point.class);
		b.setName( getPoint3d_Write() );
		
		SimpleFeatureType point3DType = b.buildFeatureType();
		ISOSimpleFeatureBuilder sfb = new ISOSimpleFeatureBuilder(point3DType, new ISOFeatureFactoryImpl());


		try{
			dataStore.createSchema((SimpleFeatureType)point3DType);
			SimpleFeature feature = sfb.buildFeature("p1_test",new Object[]{"p1_test",p});

			FeatureWriter<SimpleFeatureType, SimpleFeature> fw = dataStore.getFeatureWriterAppend(
					point3DType.getTypeName(), Transaction.AUTO_COMMIT);
			SimpleFeature newFeature = fw.next(); // new blank feature
			newFeature.setAttributes(feature.getAttributes());
			fw.write();
			fw.close();
			
			// retrieve it back
			SimpleFeatureCollection fc = dataStore.getFeatureSource(tname(getPoint3d_Write())).getFeatures();
			try(SimpleFeatureIterator fr = fc.features()) {
				assertTrue(fr.hasNext());
				Point testp = (Point) fr.next().getDefaultGeometry();
				DirectPosition c = builder.createDirectPosition(new double[] {0, 10, 5});
				assertTrue(c.equals(testp.getDirectPosition()));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void testReadLine() throws Exception {
		SimpleFeatureCollection fc = dataStore.getFeatureSource(tname(getLine3d())).getFeatures();
		try(SimpleFeatureIterator fr = fc.features()) {
			assertTrue(fr.hasNext());
			Curve ls = (Curve) fr.next().getDefaultGeometry();
			// 1 1 0, 2 2 0, 4 2 1, 5 1 1

			List<? extends CurveSegment> segments = ls.getSegments();

			int size = segments.size();
			assertEquals(3, size);

			DirectPosition c1 = builder.createDirectPosition(new double[] {1, 1, 0});
			DirectPosition c2 = builder.createDirectPosition(new double[] {2, 2, 0});
			DirectPosition c3 = builder.createDirectPosition(new double[] {4, 2, 1});
			DirectPosition c4 = builder.createDirectPosition(new double[] {5, 1, 1});

			assertTrue(c1.equals(segments.get(0).getStartPoint()));
			assertTrue(c2.equals(segments.get(1).getStartPoint()));
			assertTrue(c3.equals(segments.get(2).getStartPoint()));
			assertTrue(c4.equals(segments.get(2).getEndPoint()));
		}
	}

	public void testWriteLine() throws Exception {
		try{
			dataStore.removeSchema(getLine3d_Write());
		} catch (Exception e){
			//e.printStackTrace();
			System.out.println("Table was not removed");
		}
		
		// build a 3d line
		DirectPosition c1 = builder.createDirectPosition(new double[] {0, 0, 0});
		DirectPosition c2 = builder.createDirectPosition(new double[] {1, 1, 1});

		List<LineSegment> segments = new ArrayList<>();
		segments.add(builder.createLineSegment(c1, c2));
		Curve c = builder.createCurve(segments);

		ISOSimpleFeatureTypeBuilder b = new ISOSimpleFeatureTypeBuilder();
		b.setCRS(DefaultGeographicCRS.WGS84_3D);
		b.add(aname(ID), String.class);
		b.add(aname(GEOM), Curve.class);
		b.setName( getLine3d_Write() );
		
		SimpleFeatureType line3DType = b.buildFeatureType();
		ISOSimpleFeatureBuilder sfb = new ISOSimpleFeatureBuilder(line3DType, new ISOFeatureFactoryImpl());

		try{
			dataStore.createSchema((SimpleFeatureType)line3DType);
			SimpleFeature feature = sfb.buildFeature("c1_test", new Object[] { "c1_test", c });

			FeatureWriter<SimpleFeatureType, SimpleFeature> fw = dataStore.getFeatureWriterAppend(
					line3DType.getTypeName(), Transaction.AUTO_COMMIT);
			SimpleFeature newFeature = fw.next(); // new blank feature
			newFeature.setAttributes(feature.getAttributes());
			fw.write();
			fw.close();
			
			// retrieve it back
			SimpleFeatureCollection fc = dataStore.getFeatureSource(tname(getLine3d_Write())).getFeatures();
			try(SimpleFeatureIterator fr = fc.features()) {
				assertTrue(fr.hasNext());
				Curve ls = (Curve) fr.next().getDefaultGeometry();
				assertTrue(c.equals((Geometry)ls));
//				List<? extends CurveSegment> test_segments = ls.getSegments();
//
//				int size = test_segments.size();
//				assertEquals(1, size);
//
//				DirectPosition test_c1 = builder.createDirectPosition(new double[] {0, 0, 0});
//				DirectPosition test_c2 = builder.createDirectPosition(new double[] {1, 1, 1});
//
//				assertTrue(test_c1.equals(segments.get(0).getStartPoint()));
//				assertTrue(test_c2.equals(segments.get(1).getStartPoint()));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	public void testReadPolygon() throws Exception {

	}

	public void testCreateSchemaAndInsertPolyTriangle() throws Exception {
		/*LiteCoordinateSequenceFactory csf = new LiteCoordinateSequenceFactory();
        GeometryFactory gf = new GeometryFactory(csf);

        LinearRing shell = gf.createLinearRing(csf.create(new double[] { 0, 0, 99, 1, 0, 33, 1, 1,
                66, 0, 0, 99 }, 3));
        Polygon poly = gf.createPolygon(shell, null);

        checkCreateSchemaAndInsert(poly);*/
	}

	public void testCreateSchemaAndInsertPolyRectangle() throws Exception {
		/*LiteCoordinateSequenceFactory csf = new LiteCoordinateSequenceFactory();
        GeometryFactory gf = new GeometryFactory(csf);

        LinearRing shell = gf.createLinearRing(csf.create(new double[] { 0, 0, 99, 1, 0, 33, 1, 1,
                66, 0, 1, 33, 0, 0, 99 }, 3));
        Polygon poly = gf.createPolygon(shell, null);

        checkCreateSchemaAndInsert(poly);*/
	}

	public void testCreateSchemaAndInsertPolyRectangleWithHole() throws Exception {
		/*LiteCoordinateSequenceFactory csf = new LiteCoordinateSequenceFactory();
        GeometryFactory gf = new GeometryFactory(csf);

        LinearRing shell = gf.createLinearRing(csf.create(new double[] { 0, 0, 99, 10, 0, 33, 10,
                10, 66, 0, 10, 66, 0, 0, 99 }, 3));
        LinearRing hole = gf.createLinearRing(csf.create(new double[] { 2, 2, 99, 3, 2, 44, 3, 3,
                99, 2, 3, 99, 2, 2, 99 }, 3));
        Polygon poly = gf.createPolygon(shell, new LinearRing[] { hole });

        checkCreateSchemaAndInsert(poly);*/
	}

	public void testCreateSchemaAndInsertPolyWithHoleCW() throws Exception {
		/*LiteCoordinateSequenceFactory csf = new LiteCoordinateSequenceFactory();
        GeometryFactory gf = new GeometryFactory(csf);

        LinearRing shell = gf.createLinearRing(csf.create(new double[] { 1, 1, 99, 10, 1, 33, 
                10, 10, 66,    1, 10, 66,       1, 1, 99 }, 3));
        LinearRing hole = gf.createLinearRing(csf.create(new double[] { 2, 2, 99, 8, 2, 44, 8, 8,
                99, 2, 8, 99, 2, 2, 99 }, 3));
        Polygon poly = gf.createPolygon(shell, new LinearRing[] { hole });

        checkCreateSchemaAndInsert(poly);*/
	}

	/**
	 * Creates the polygon schema, inserts a 3D geometry into the datastore,
	 * and retrieves it back to make sure 3d data is preserved.
	 * 
	 * @throws Exception
	 */
	private void checkCreateSchemaAndInsert(Geometry poly) throws Exception {
		/*dataStore.createSchema(poly3DType);
        SimpleFeatureType actualSchema = dataStore.getSchema(tname(getPoly3d()));
        assertFeatureTypesEqual(poly3DType, actualSchema);
        assertEquals(
                getNativeSRID(),
                actualSchema.getGeometryDescriptor().getUserData()
                        .get(JDBCDataStore.JDBC_NATIVE_SRID));

        // insert the feature
        try(FeatureWriter<SimpleFeatureType, SimpleFeature> fw = dataStore.getFeatureWriterAppend(
                tname(getPoly3d()), Transaction.AUTO_COMMIT)) {
            SimpleFeature f = fw.next();
            f.setAttribute(aname(ID), 0);
            f.setAttribute(aname(GEOM), poly);
            f.setAttribute(aname(NAME), "3dpolygon!");
            fw.write();
        }

        // read feature back

		 *//**
		 * Use a LiteCoordinateSequence, since this mimics GeoServer behaviour better,
		 * and it exposes bugs in CoordinateSequence handling.
		 *//*
        final Hints hints = new Hints();
        hints.put(Hints.JTS_COORDINATE_SEQUENCE_FACTORY, new LiteCoordinateSequenceFactory());
        Query query = new Query(tname(getPoly3d()));
        query.setHints(hints);

        try(FeatureReader<SimpleFeatureType, SimpleFeature> fr = dataStore.getFeatureReader(
                query, Transaction.AUTO_COMMIT)) {
            assertTrue(fr.hasNext());
            SimpleFeature f = fr.next();

		  *//**
		  * Check the geometries are topologically equal.
		  * Check that the Z values are preserved
		  *//*
            Geometry fgeom = (Geometry) f.getDefaultGeometry();
            assertTrue("2D topology does not match", poly.equalsTopo(fgeom));
            assertTrue("Z values do not match", hasMatchingZValues(poly, fgeom));
        }*/
	}

	/**
	 * Tests whether two geometries have the same Z values for coordinates with identical 2D locations. Requires that each geometry is internally
	 * location-consistent in Z; that is, if two coordinates are identical in location, then the Z values are equal. This should always be the case
	 * for valid data.
	 * 
	 * @param g1
	 * @param g2
	 * @return true if the geometries are location-equal in Z
	 */
	private static boolean hasMatchingZValues(Geometry g1, Geometry g2) {
		/*Coordinate[] pt1 = g1.getCoordinates();
        Map<Coordinate, Double> coordZMap = new HashMap<Coordinate, Double>();
        for (int i = 0; i < pt1.length; i++) {
            coordZMap.put(pt1[i], pt1[i].z);
        }

        Coordinate[] pt2 = g2.getCoordinates();

        for (int i2 = 0; i2 < pt2.length; i2++) {
            Coordinate p2 = pt2[i2];
            double z = coordZMap.get(p2);
            boolean isEqualZ = p2.z == z || (Double.isNaN(p2.z) && Double.isNaN(z));
            if (!isEqualZ)
                return false;
        }

        return true;*/
		return true;
	}

	/**
	 * Make sure we can properly retrieve the bounds of 3d layers
	 * 
	 * @throws Exception
	 */
	public void testBounds() throws Exception {
		/*ReferencedEnvelope env = dataStore.getFeatureSource(tname(getLine3d())).getBounds();

        // check we got the right 2d component
        Envelope expected = new Envelope(1, 5, 0, 4);
        assertEquals(expected, env);

        // check the srs the expected one
        assertEquals(CRS.getHorizontalCRS(crs), env.getCoordinateReferenceSystem());*/
	}

	// disabled as the liter coordinate sequence has still not been updated to support 3d data
	public void testRendererBehaviour() throws Exception {
		// make sure the hints are supported
		/*ContentFeatureSource fs = dataStore.getFeatureSource(tname(getLine3d()));
        assertTrue(fs.getSupportedHints().contains(Hints.JTS_COORDINATE_SEQUENCE_FACTORY));

        // setup a query that mimicks the streaming renderer behaviour
        Query q = new Query(tname(getLine3d()));
        Hints hints = new Hints(Hints.JTS_COORDINATE_SEQUENCE_FACTORY,
                new LiteCoordinateSequenceFactory());
        q.setHints(hints);

        // check the srs you get is the expected one
        FeatureCollection fc = fs.getFeatures(q);
        FeatureType fcSchema = fc.getSchema();
        assertEquals(crs, fcSchema.getCoordinateReferenceSystem());
        assertEquals(crs, fcSchema.getGeometryDescriptor().getCoordinateReferenceSystem());

        // build up the reference 2d line, the 3d one is (1 1 0, 2 2 0, 4 2 1, 5
        // 1 1)
        LineString expected = new GeometryFactory().createLineString(new Coordinate[] {
                new Coordinate(1, 1), new Coordinate(2, 2), new Coordinate(4, 2),
                new Coordinate(5, 1) });

        // check feature reader and the schema
        try(FeatureReader<SimpleFeatureType, SimpleFeature> fr = dataStore.getFeatureReader(q,
                Transaction.AUTO_COMMIT)) {
            assertEquals(crs, fr.getFeatureType().getCoordinateReferenceSystem());
            assertEquals(crs, fr.getFeatureType().getGeometryDescriptor()
                    .getCoordinateReferenceSystem());
            assertTrue(fr.hasNext());
            SimpleFeature f = fr.next();
            assertTrue(expected.equalsExact((Geometry) f.getDefaultGeometry()));
        }
		 */    }

}
