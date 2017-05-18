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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureWriter;
import org.geotools.data.ISODataUtilities;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.feature.ISOFeatureFactoryImpl;
import org.geotools.feature.simple.ISOSimpleFeatureBuilder;
import org.geotools.feature.simple.ISOSimpleFeatureTypeBuilder;
import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.LineSegment;
import org.opengis.geometry.coordinate.LineString;
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
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Tests the ability of the datastore to cope with 3D data
 * 
 * @author Andrea Aime - OpenGeo
 * @author Martin Davis - OpenGeo
 * @author Dongmin Kim
 * 
 * 
 * @source $URL$
 */
public abstract class JDBCGeneric3DOnlineTest extends JDBCTestSupport {

	protected static final String ID = "id";

	protected static final String GEOM = "geom";

	protected static final String NAME = "name";

	protected static final FilterFactory FF = CommonFactoryFinder.getFilterFactory(null);

	protected static Hints h = new Hints();
	//protected SimpleFeatureType poly3DType;

	//protected SimpleFeatureType line3DType;

	protected ISOGeometryBuilder builder;// = new ISOGeometryBuilder(new Hints()DefaultGeographicCRS.WGS84_3D);

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
	
	protected abstract String getSolid();

	protected abstract String getPoint3d_Write();

	protected abstract String getLine3d_Write();

	protected abstract String getPoly3d_Write();

	protected abstract String getSolid_Write();

	@Override
	protected void connect() throws Exception {
		super.connect();

//		line3DType = ISODataUtilities.createType(dataStore.getNamespaceURI() + "." + tname(getLine3d()),
//				aname(ID) + ":0," + aname(GEOM) + ":Curve:srid=" + getEpsgCode() + "," + aname(NAME)
//				+ ":String");
//		line3DType.getGeometryDescriptor().getUserData().put(Hints.COORDINATE_DIMENSION, 3);
//		poly3DType = ISODataUtilities.createType(dataStore.getNamespaceURI() + "." + tname(getPoly3d()),
//				aname(ID) + ":0," + aname(GEOM) + ":Surface:srid=" + getEpsgCode() + "," + aname(NAME) + ":String");
//		poly3DType.getGeometryDescriptor().getUserData().put(Hints.COORDINATE_DIMENSION, 3);

		
		h.put(Hints.GEOMETRY_VALIDATE, true);
		h.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D);
		builder = new ISOGeometryBuilder(h);
		
		crs = CRS.decode("EPSG:" + getEpsgCode());
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

	// 1,1,1
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

		try{
			//build 3D Point
			DirectPosition dp = builder.createDirectPosition(new double[] {0, 10, 5});
			Point point = writePoint(dp, getPoint3d_Write(), 10, "p1_test");

			// retrieve it back
			SimpleFeatureCollection fc = dataStore.getFeatureSource(tname(getPoint3d_Write())).getFeatures();
			try(SimpleFeatureIterator fr = fc.features()) {
				assertTrue(fr.hasNext());
				Point testp = (Point) fr.next().getDefaultGeometry();
				assertTrue(point.equals(testp));
				assertTrue(point.getDirectPosition().equals(testp.getDirectPosition()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected Point writePoint(DirectPosition dp, String schema, int id, String name) throws IOException{

		Point p = builder.createPoint(dp);

		ISOSimpleFeatureTypeBuilder b = new ISOSimpleFeatureTypeBuilder();
		b.setCRS(DefaultGeographicCRS.WGS84_3D);
		b.add(aname(ID), Integer.class);
		b.add(aname(GEOM), Point.class);
		b.add(aname(NAME), String.class);
		b.setName( schema );

		SimpleFeatureType point3DType = b.buildFeatureType();
		point3DType.getGeometryDescriptor().getUserData().put(Hints.COORDINATE_DIMENSION, 3);
		ISOSimpleFeatureBuilder sfb = new ISOSimpleFeatureBuilder(point3DType, new ISOFeatureFactoryImpl());

		dataStore.createSchema((SimpleFeatureType)point3DType);
		SimpleFeature feature = sfb.buildFeature(name,new Object[]{id,p,name});

		FeatureWriter<SimpleFeatureType, SimpleFeature> fw = dataStore.getFeatureWriterAppend(
				point3DType.getTypeName(), Transaction.AUTO_COMMIT);
		SimpleFeature newFeature = fw.next(); // new blank feature
		newFeature.setAttributes(feature.getAttributes());
		fw.write();
		fw.close();

		return p;
	}

	// 1 1 0, 2 2 0, 4 2 1, 5 1 1
	public void testReadLine() throws Exception {
		SimpleFeatureCollection fc = dataStore.getFeatureSource(tname(getLine3d())).getFeatures();
		try(SimpleFeatureIterator fr = fc.features()) {
			assertTrue(fr.hasNext());
			Curve ls = (Curve) fr.next().getDefaultGeometry();

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
		List<DirectPosition> dp_list = new ArrayList<DirectPosition>();
		dp_list.add(c1);
		dp_list.add(c2);
		
		try{
			Curve c = writeCurve(dp_list, getLine3d_Write(), 10, "c1_test" );
			
			// retrieve it back
			SimpleFeatureCollection fc = dataStore.getFeatureSource(tname(getLine3d_Write())).getFeatures();
			try(SimpleFeatureIterator fr = fc.features()) {
				assertTrue(fr.hasNext());
				Curve ls = (Curve) fr.next().getDefaultGeometry();
				assertTrue(c.equals((Geometry)ls));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	protected Curve writeCurve(List<DirectPosition> dp_list, String schema, int id, String name) throws IOException {
		List<LineSegment> segments = new ArrayList<>();
		segments.add(builder.createLineSegment(dp_list.get(0), dp_list.get(1)));
		Curve c = builder.createCurve(segments);

		ISOSimpleFeatureTypeBuilder b = new ISOSimpleFeatureTypeBuilder();
		b.setCRS(DefaultGeographicCRS.WGS84_3D);
		b.add(aname(ID), Integer.class);
		b.add(aname(GEOM), Curve.class);
		b.add(aname(NAME), String.class);
		b.setName( schema );
		
		SimpleFeatureType line3DType = b.buildFeatureType();
		line3DType.getGeometryDescriptor().getUserData().put(Hints.COORDINATE_DIMENSION, 3);

		ISOSimpleFeatureBuilder sfb = new ISOSimpleFeatureBuilder(line3DType, new ISOFeatureFactoryImpl());
		SimpleFeature feature = sfb.buildFeature(name, new Object[] { id, c, name });
		
		dataStore.createSchema((SimpleFeatureType)line3DType);
		
		FeatureWriter<SimpleFeatureType, SimpleFeature> fw = dataStore.getFeatureWriterAppend(
				line3DType.getTypeName(), Transaction.AUTO_COMMIT);
		SimpleFeature newFeature = fw.next(); // new blank feature
		newFeature.setAttributes(feature.getAttributes());
		fw.write();
		fw.close();
		return c;
	}


	public void testReadPolygon() throws Exception {
		SimpleFeatureCollection fc = dataStore.getFeatureSource(tname(getPoly3d())).getFeatures();
		try(SimpleFeatureIterator fr = fc.features()) {
			assertTrue(fr.hasNext());
			Surface test_surface = (Surface) fr.next().getDefaultGeometry();
			//(1 1 0, 2 2 0, 4 2 1, 5 1 1, 1 1 0)
			
			List<Position> dp_list = new ArrayList<>();
			dp_list.add(builder.createDirectPosition(new double[] {1, 1, 0}));
			dp_list.add(builder.createDirectPosition(new double[] {2, 2, 0}));
			dp_list.add(builder.createDirectPosition(new double[] {4, 2, 1}));
			dp_list.add(builder.createDirectPosition(new double[] {5, 1, 1}));
			dp_list.add(builder.createDirectPosition(new double[] {1, 1, 0}));
			
			Surface sf = makeSimpleSurface(dp_list);
			
			assertTrue(sf.equals(test_surface));
		}
	}
	
	public void testWritePolygon() throws Exception {
		//remove previous table
		try{
			dataStore.removeSchema(getPoly3d_Write());
		} catch (Exception e){
			//e.printStackTrace();
			System.out.println("getPoly3d_Write -- Table was empty");
		}
		
		// build a 3d polygon
		List<Position> dp_list = new ArrayList<>();
		dp_list.add(builder.createDirectPosition(new double[] {1, 1, 0}));
		dp_list.add(builder.createDirectPosition(new double[] {2, 2, 0}));
		dp_list.add(builder.createDirectPosition(new double[] {4, 2, 1}));
		dp_list.add(builder.createDirectPosition(new double[] {5, 1, 1}));
		dp_list.add(builder.createDirectPosition(new double[] {4, 0, 2}));
		dp_list.add(builder.createDirectPosition(new double[] {1, 1, 0}));
		try{
			Surface sf = writeSurface(dp_list, getPoly3d_Write(), 11 , "pl1_test");

			// retrieve it back
			SimpleFeatureCollection fc = dataStore.getFeatureSource(tname(getPoly3d_Write())).getFeatures();
			try(SimpleFeatureIterator fr = fc.features()) {
				assertTrue(fr.hasNext());
				Surface polygon = (Surface) fr.next().getDefaultGeometry();
				assertTrue(sf.equals((Geometry)polygon));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	protected Surface writeSurface(List<Position> dp_list, String schema, int id, String name) throws Exception, IOException {
		Surface sf = makeSimpleSurface(dp_list);

		ISOSimpleFeatureTypeBuilder b = new ISOSimpleFeatureTypeBuilder();
		b.setCRS(DefaultGeographicCRS.WGS84_3D);
		b.add(aname(ID), Integer.class);
		b.add(aname(GEOM), Surface.class);
		b.add(aname(NAME), String.class);
		b.setName( schema );

		SimpleFeatureType poly3dType = b.buildFeatureType();
		poly3dType.getGeometryDescriptor().getUserData().put(Hints.COORDINATE_DIMENSION, 3);
		ISOSimpleFeatureBuilder sfb = new ISOSimpleFeatureBuilder(poly3dType, new ISOFeatureFactoryImpl());


		dataStore.createSchema((SimpleFeatureType)poly3dType);
		SimpleFeature feature = sfb.buildFeature(name, new Object[] { id, sf, name });

		FeatureWriter<SimpleFeatureType, SimpleFeature> fw = dataStore.getFeatureWriterAppend(
				poly3dType.getTypeName(), Transaction.AUTO_COMMIT);
		SimpleFeature newFeature = fw.next(); // new blank feature
		newFeature.setAttributes(feature.getAttributes());
		fw.write();
		fw.close();
		return sf;
	}
	
	
	public void testReadSolid() throws Exception {
		SimpleFeatureCollection fc = dataStore.getFeatureSource(tname(getSolid())).getFeatures();
		try(SimpleFeatureIterator fr = fc.features()) {
			assertTrue(fr.hasNext());
			Solid sd_test = (Solid) fr.next().getDefaultGeometry();
//			 "((0 0 0, 0 0 1, 0 1 1, 0 1 0, 0 0 0)),"
//			 "((0 0 0, 0 1 0, 1 1 0, 1 0 0, 0 0 0)), "
//			 "((0 0 0, 1 0 0, 1 0 1, 0 0 1, 0 0 0)),"
//			 "((1 1 0, 1 1 1, 1 0 1, 1 0 0, 1 1 0)),"
//			 "((0 1 0, 0 1 1, 1 1 1, 1 1 0, 0 1 0)), "
//			 "((0 0 1, 1 0 1, 1 1 1, 0 1 1, 0 0 1))"
			
			List<Position> dp_list = new ArrayList<>();
			dp_list.add(builder.createDirectPosition(new double[] {0, 0, 0}));
			dp_list.add(builder.createDirectPosition(new double[] {1, 0, 0}));
			dp_list.add(builder.createDirectPosition(new double[] {1, 1, 0}));
			dp_list.add(builder.createDirectPosition(new double[] {0, 1, 0}));
			dp_list.add(builder.createDirectPosition(new double[] {0, 0, 1}));
			dp_list.add(builder.createDirectPosition(new double[] {1, 0, 1}));
			dp_list.add(builder.createDirectPosition(new double[] {1, 1, 1}));
			dp_list.add(builder.createDirectPosition(new double[] {0, 1, 1}));
			
			List<OrientableSurface> surfaces = makeOrientableSurfacesOfCube(dp_list);
			Solid solid = makeSolid(surfaces);
			assertTrue(solid.equals(sd_test));
		}
	}
	
	public void testWriteSolid() throws Exception {
		try{
			dataStore.removeSchema(getSolid_Write());
		} catch (Exception e){
			//e.printStackTrace();
			System.out.println("Table was not removed");
		}
		
		List<Position> dp_list = new ArrayList<>();
		dp_list.add(builder.createDirectPosition(new double[] { 0.5, -0.5, 0.5 }) );
		dp_list.add(builder.createDirectPosition(new double[] { 0.5, -1.5, 0.5 }) );
		dp_list.add(builder.createDirectPosition(new double[] { 1.5, -1.5, 0.5 }) );
		dp_list.add(builder.createDirectPosition(new double[] { 1.5, -0.5, 0.5 }) );
		dp_list.add(builder.createDirectPosition(new double[] { 0.5, -0.5, 1.5 }) );
		dp_list.add(builder.createDirectPosition(new double[] { 0.5, -1.5, 1.5 }) );
		dp_list.add(builder.createDirectPosition(new double[] { 1.5, -1.5, 1.5 }) );
		dp_list.add(builder.createDirectPosition(new double[] { 1.5, -0.5, 1.5 }) );
		try{
			Solid sld = writeSolid(dp_list, getSolid_Write(), 12, "sl1_test");

			// retrieve it back
			SimpleFeatureCollection fc = dataStore.getFeatureSource(tname(getSolid_Write())).getFeatures();
			try(SimpleFeatureIterator fr = fc.features()) {
				assertTrue(fr.hasNext());
				Solid test_solid = (Solid) fr.next().getDefaultGeometry();
				assertTrue(sld.equals((Geometry)test_solid));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param dp_list
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	protected Solid writeSolid(List<Position> dp_list, String schema, int id, String name ) throws Exception, IOException {
		List<OrientableSurface> surfaces = makeOrientableSurfacesOfCube(dp_list);
		Solid sld = makeSolid(surfaces);

		ISOSimpleFeatureTypeBuilder b = new ISOSimpleFeatureTypeBuilder();
		b.setCRS(DefaultGeographicCRS.WGS84_3D);
		b.add(aname(ID), Integer.class);
		b.add(aname(GEOM), Solid.class);
		b.add(aname(NAME), String.class);
		b.setName( schema );

		SimpleFeatureType SolidType = b.buildFeatureType();
		SolidType.getGeometryDescriptor().getUserData().put(Hints.COORDINATE_DIMENSION, 3);
		ISOSimpleFeatureBuilder sfb = new ISOSimpleFeatureBuilder(SolidType, new ISOFeatureFactoryImpl());

		dataStore.createSchema((SimpleFeatureType)SolidType);
		SimpleFeature feature = sfb.buildFeature(name, new Object[] { id, sld, name });

		FeatureWriter<SimpleFeatureType, SimpleFeature> fw = dataStore.getFeatureWriterAppend(
				SolidType.getTypeName(), Transaction.AUTO_COMMIT);
		SimpleFeature newFeature = fw.next(); // new blank feature
		newFeature.setAttributes(feature.getAttributes());
		fw.write();
		fw.close();
		return sld;
	}
	

	
	
	
	/*
	 * make Simple(no interior boundary) Surface
	 */
	protected Surface makeSimpleSurface(List<Position> dp) throws Exception{
		int size = dp.size();
		if (size < 3){
			throw new Exception("cannot make surface. direct positions are at least 3");
		}
		
		LineString line = builder.createLineString(dp);
		
		ArrayList<CurveSegment> edges = new ArrayList<>();
		edges.add(line);

		OrientableCurve curve = builder.createCurve(edges);
		List<OrientableCurve> o_curves = new ArrayList<OrientableCurve>();
		o_curves.add(curve);
		
		Ring exterior = builder.createRing(o_curves);
		SurfaceBoundary sb = builder.createSurfaceBoundary(exterior, new ArrayList<Ring>() );
		Surface sf = builder.createSurface(sb);
		return sf;
	}

	/*
	 * make SimpleSolid(Cube)
	 */
	protected Solid makeSolid(List<OrientableSurface> surfaces) throws Exception {
		Shell exteriorShell = builder.createShell(surfaces);
		List<Shell> interiors = new ArrayList<Shell>();

		SolidBoundary solidBoundary = builder.createSolidBoundary(exteriorShell, interiors);
		Solid solid = builder.createSolid(solidBoundary);

		return solid;
	}

	protected List<OrientableSurface> makeOrientableSurfacesOfCube(List<Position> dp_list) throws Exception {
		Position position1 = dp_list.get(0);
		Position position2 = dp_list.get(1);
		Position position3 = dp_list.get(2);
		Position position4 = dp_list.get(3);
		Position position5 = dp_list.get(4);
		Position position6 = dp_list.get(5);
		Position position7 = dp_list.get(6);
		Position position8 = dp_list.get(7);

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
		
		// create the surface
		Surface surface1 = makeSimpleSurface(dps1);
		Surface surface2 = makeSimpleSurface(dps2);
		Surface surface3 = makeSimpleSurface(dps3);
		Surface surface4 = makeSimpleSurface(dps4);
		Surface surface5 = makeSimpleSurface(dps5);
		Surface surface6 = makeSimpleSurface(dps6);

		List<OrientableSurface> surfaces = new ArrayList<OrientableSurface>();
		surfaces.add(surface1);
		surfaces.add(surface2);
		surfaces.add(surface3);
		surfaces.add(surface4);
		surfaces.add(surface5);
		surfaces.add(surface6);
		return surfaces;
	}
	
	public void testIntersectCurveInPointSchema(){
		try{
			DirectPosition p1 = builder.createDirectPosition(new double[] {4, 2, 1});
			Point point = builder.createPoint(p1);

			SimpleFeatureSource fc = dataStore.getFeatureSource(tname(getPoint3d()));
			FeatureType schema = fc.getSchema();
			String geom_descriptor = schema.getGeometryDescriptor().getLocalName();
			// Curve True 
			DirectPosition cp1 = builder.createDirectPosition(new double[] {3, 2, 1});
			DirectPosition cp2 = builder.createDirectPosition(new double[] {5, 2, 1});

			List<LineSegment> segments = new ArrayList<>();
			segments.add(builder.createLineSegment(cp1, cp2));
			Curve c1 = builder.createCurve(segments);


			SimpleFeatureIterator iterator = getIntersectionQuery(fc, geom_descriptor, c1);
			assertTrue(iterator.hasNext());
			while (iterator.hasNext()) {
				SimpleFeature iter_feature = iterator.next();
				assertTrue(point.equals((Geometry)iter_feature.getDefaultGeometry()));
			}
			
			iterator.close();
			
			// Curve False
			DirectPosition cp3 = builder.createDirectPosition(new double[] {13, 12, 1});
			DirectPosition cp4 = builder.createDirectPosition(new double[] {15, 12, 1});

			List<LineSegment> segments2 = new ArrayList<>();
			segments2.add(builder.createLineSegment(cp3, cp4));
			Curve c2 = builder.createCurve(segments2);

			SimpleFeatureIterator iterator2 = getIntersectionQuery(fc, geom_descriptor, c2);

			assertFalse(iterator2.hasNext());
			iterator2.close();
			
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void testIntersectSufaceInPointSchema(){
		try{
			DirectPosition p1 = builder.createDirectPosition(new double[] {4, 2, 1});
			Point point = builder.createPoint(p1);

			SimpleFeatureSource fc = dataStore.getFeatureSource(tname(getPoint3d()));
			FeatureType schema = fc.getSchema();
			String geom_descriptor = schema.getGeometryDescriptor().getLocalName();
			
			//True
			//make Surface
			List<Position> dp_list1 = new ArrayList<Position>();
			dp_list1.add(builder.createDirectPosition(new double[] {3.5, 2, 0.5}) );
			dp_list1.add(builder.createDirectPosition(new double[] {4.5, 2, 0.5}) );
			dp_list1.add(builder.createDirectPosition(new double[] {4.5, 2, 1.5}) );
			dp_list1.add(builder.createDirectPosition(new double[] {3.5, 2, 1.5}) );
			dp_list1.add(builder.createDirectPosition(new double[] {3.5, 2, 0.5}) );
			Surface surf = makeSimpleSurface(dp_list1);

			SimpleFeatureIterator iterator = getIntersectionQuery(fc, geom_descriptor, surf);
			assertTrue(iterator.hasNext());
			while (iterator.hasNext()) {
				SimpleFeature iter_feature = iterator.next();
				assertTrue(point.equals((Geometry)iter_feature.getDefaultGeometry()));
			}
			
			iterator.close();
			
			// False
			List<Position> dp_list2 = new ArrayList<Position>();
			dp_list2.add(builder.createDirectPosition(new double[] {13, 12, 10}) );
			dp_list2.add(builder.createDirectPosition(new double[] {4.5, 2, 0.5}) );
			dp_list2.add(builder.createDirectPosition(new double[] {4.5, 2, 1.5}) );
			dp_list2.add(builder.createDirectPosition(new double[] {9, 4, 8.5}) );
			dp_list2.add(builder.createDirectPosition(new double[] {13, 12, 10}) );	
			Surface surf2 = makeSimpleSurface(dp_list2);
			
			SimpleFeatureIterator iterator2 = getIntersectionQuery(fc, geom_descriptor, surf2);

			assertFalse(iterator2.hasNext());
			iterator2.close();
			
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void testIntersectSolidInPointSchema(){
		try{
			DirectPosition p1 = builder.createDirectPosition(new double[] {4, 2, 1});
			Point point = builder.createPoint(p1);

			SimpleFeatureSource fc = dataStore.getFeatureSource(tname(getPoint3d()));
			FeatureType schema = fc.getSchema();
			String geom_descriptor = schema.getGeometryDescriptor().getLocalName();
			
			//True
			//make Solid
			List<Position> dp_list1 = new ArrayList<Position>();
			dp_list1.add(builder.createDirectPosition(new double[] {3,1,1}) );
			dp_list1.add(builder.createDirectPosition(new double[] {5,1,1}) );
			dp_list1.add(builder.createDirectPosition(new double[] {5,2.5,1}) );
			dp_list1.add(builder.createDirectPosition(new double[] {3,2.5,1}) );
			dp_list1.add(builder.createDirectPosition(new double[] {3, 1, 5}) );
			dp_list1.add(builder.createDirectPosition(new double[] {5, 1, 5}) );
			dp_list1.add(builder.createDirectPosition(new double[] {5, 2.5, 5}) );
			dp_list1.add(builder.createDirectPosition(new double[] {3, 2.5, 5}) );
			
			List<OrientableSurface> surfaces = makeOrientableSurfacesOfCube(dp_list1);
			Solid sld = makeSolid(surfaces);
			
			SimpleFeatureIterator iterator = getIntersectionQuery(fc, geom_descriptor, sld);

			if (!iterator.hasNext()){
				iterator.close();
				assertTrue(false);
			}
			while (iterator.hasNext()) {
				SimpleFeature iter_feature = iterator.next();
				assertTrue(point.equals((Geometry)iter_feature.getDefaultGeometry()));
			}
			
			iterator.close();
			
			// False
			
			
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param fc
	 * @param geom_descriptor
	 * @param geometry
	 * @return
	 * @throws IOException
	 */
	protected SimpleFeatureIterator getIntersectionQuery(SimpleFeatureSource fc, String geom_descriptor, Geometry geometry) 
			throws IOException {
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2( h );
		Filter filter = ff.intersects( ff.property( geom_descriptor ), ff.literal( geometry ) );
		Query query = new Query(getPoint3d(), filter, new String[] { geom_descriptor });
		SimpleFeatureCollection features = fc.getFeatures(query);
		SimpleFeatureIterator iterator = features.features();
		return iterator;
	}

//
//	public void testCreateSchemaAndInsertPolyTriangle() throws Exception {
//		/*LiteCoordinateSequenceFactory csf = new LiteCoordinateSequenceFactory();
//        GeometryFactory gf = new GeometryFactory(csf);
//
//        LinearRing shell = gf.createLinearRing(csf.create(new double[] { 0, 0, 99, 1, 0, 33, 1, 1,
//                66, 0, 0, 99 }, 3));
//        Polygon poly = gf.createPolygon(shell, null);
//
//        checkCreateSchemaAndInsert(poly);*/
//	}
//
//	public void testCreateSchemaAndInsertPolyRectangle() throws Exception {
//		/*LiteCoordinateSequenceFactory csf = new LiteCoordinateSequenceFactory();
//        GeometryFactory gf = new GeometryFactory(csf);
//
//        LinearRing shell = gf.createLinearRing(csf.create(new double[] { 0, 0, 99, 1, 0, 33, 1, 1,
//                66, 0, 1, 33, 0, 0, 99 }, 3));
//        Polygon poly = gf.createPolygon(shell, null);
//
//        checkCreateSchemaAndInsert(poly);*/
//	}
//
//	public void testCreateSchemaAndInsertPolyRectangleWithHole() throws Exception {
//		/*LiteCoordinateSequenceFactory csf = new LiteCoordinateSequenceFactory();
//        GeometryFactory gf = new GeometryFactory(csf);
//
//        LinearRing shell = gf.createLinearRing(csf.create(new double[] { 0, 0, 99, 10, 0, 33, 10,
//                10, 66, 0, 10, 66, 0, 0, 99 }, 3));
//        LinearRing hole = gf.createLinearRing(csf.create(new double[] { 2, 2, 99, 3, 2, 44, 3, 3,
//                99, 2, 3, 99, 2, 2, 99 }, 3));
//        Polygon poly = gf.createPolygon(shell, new LinearRing[] { hole });
//
//        checkCreateSchemaAndInsert(poly);*/
//	}
//
//	public void testCreateSchemaAndInsertPolyWithHoleCW() throws Exception {
//		/*LiteCoordinateSequenceFactory csf = new LiteCoordinateSequenceFactory();
//        GeometryFactory gf = new GeometryFactory(csf);
//
//        LinearRing shell = gf.createLinearRing(csf.create(new double[] { 1, 1, 99, 10, 1, 33, 
//                10, 10, 66,    1, 10, 66,       1, 1, 99 }, 3));
//        LinearRing hole = gf.createLinearRing(csf.create(new double[] { 2, 2, 99, 8, 2, 44, 8, 8,
//                99, 2, 8, 99, 2, 2, 99 }, 3));
//        Polygon poly = gf.createPolygon(shell, new LinearRing[] { hole });
//
//        checkCreateSchemaAndInsert(poly);*/
//	}
//
//	/**
//	 * Creates the polygon schema, inserts a 3D geometry into the datastore,
//	 * and retrieves it back to make sure 3d data is preserved.
//	 * 
//	 * @throws Exception
//	 */
//	private void checkCreateSchemaAndInsert(Geometry poly) throws Exception {
//		/*dataStore.createSchema(poly3DType);
//        SimpleFeatureType actualSchema = dataStore.getSchema(tname(getPoly3d()));
//        assertFeatureTypesEqual(poly3DType, actualSchema);
//        assertEquals(
//                getNativeSRID(),
//                actualSchema.getGeometryDescriptor().getUserData()
//                        .get(JDBCDataStore.JDBC_NATIVE_SRID));
//
//        // insert the feature
//        try(FeatureWriter<SimpleFeatureType, SimpleFeature> fw = dataStore.getFeatureWriterAppend(
//                tname(getPoly3d()), Transaction.AUTO_COMMIT)) {
//            SimpleFeature f = fw.next();
//            f.setAttribute(aname(ID), 0);
//            f.setAttribute(aname(GEOM), poly);
//            f.setAttribute(aname(NAME), "3dpolygon!");
//            fw.write();
//        }
//
//        // read feature back
//
//		 *//**
//		 * Use a LiteCoordinateSequence, since this mimics GeoServer behaviour better,
//		 * and it exposes bugs in CoordinateSequence handling.
//		 *//*
//        final Hints hints = new Hints();
//        hints.put(Hints.JTS_COORDINATE_SEQUENCE_FACTORY, new LiteCoordinateSequenceFactory());
//        Query query = new Query(tname(getPoly3d()));
//        query.setHints(hints);
//
//        try(FeatureReader<SimpleFeatureType, SimpleFeature> fr = dataStore.getFeatureReader(
//                query, Transaction.AUTO_COMMIT)) {
//            assertTrue(fr.hasNext());
//            SimpleFeature f = fr.next();
//
//		  *//**
//		  * Check the geometries are topologically equal.
//		  * Check that the Z values are preserved
//		  *//*
//            Geometry fgeom = (Geometry) f.getDefaultGeometry();
//            assertTrue("2D topology does not match", poly.equalsTopo(fgeom));
//            assertTrue("Z values do not match", hasMatchingZValues(poly, fgeom));
//        }*/
//	}
//
//	/**
//	 * Tests whether two geometries have the same Z values for coordinates with identical 2D locations. Requires that each geometry is internally
//	 * location-consistent in Z; that is, if two coordinates are identical in location, then the Z values are equal. This should always be the case
//	 * for valid data.
//	 * 
//	 * @param g1
//	 * @param g2
//	 * @return true if the geometries are location-equal in Z
//	 */
//	private static boolean hasMatchingZValues(Geometry g1, Geometry g2) {
//		/*Coordinate[] pt1 = g1.getCoordinates();
//        Map<Coordinate, Double> coordZMap = new HashMap<Coordinate, Double>();
//        for (int i = 0; i < pt1.length; i++) {
//            coordZMap.put(pt1[i], pt1[i].z);
//        }
//
//        Coordinate[] pt2 = g2.getCoordinates();
//
//        for (int i2 = 0; i2 < pt2.length; i2++) {
//            Coordinate p2 = pt2[i2];
//            double z = coordZMap.get(p2);
//            boolean isEqualZ = p2.z == z || (Double.isNaN(p2.z) && Double.isNaN(z));
//            if (!isEqualZ)
//                return false;
//        }
//
//        return true;*/
//		return true;
//	}
//
//	/**
//	 * Make sure we can properly retrieve the bounds of 3d layers
//	 * 
//	 * @throws Exception
//	 */
//	public void testBounds() throws Exception {
//		/*ReferencedEnvelope env = dataStore.getFeatureSource(tname(getLine3d())).getBounds();
//
//        // check we got the right 2d component
//        Envelope expected = new Envelope(1, 5, 0, 4);
//        assertEquals(expected, env);
//
//        // check the srs the expected one
//        assertEquals(CRS.getHorizontalCRS(crs), env.getCoordinateReferenceSystem());*/
//	}
//
//	// disabled as the liter coordinate sequence has still not been updated to support 3d data
//	public void testRendererBehaviour() throws Exception {
//		// make sure the hints are supported
//		/*ContentFeatureSource fs = dataStore.getFeatureSource(tname(getLine3d()));
//        assertTrue(fs.getSupportedHints().contains(Hints.JTS_COORDINATE_SEQUENCE_FACTORY));
//
//        // setup a query that mimicks the streaming renderer behaviour
//        Query q = new Query(tname(getLine3d()));
//        Hints hints = new Hints(Hints.JTS_COORDINATE_SEQUENCE_FACTORY,
//                new LiteCoordinateSequenceFactory());
//        q.setHints(hints);
//
//        // check the srs you get is the expected one
//        FeatureCollection fc = fs.getFeatures(q);
//        FeatureType fcSchema = fc.getSchema();
//        assertEquals(crs, fcSchema.getCoordinateReferenceSystem());
//        assertEquals(crs, fcSchema.getGeometryDescriptor().getCoordinateReferenceSystem());
//
//        // build up the reference 2d line, the 3d one is (1 1 0, 2 2 0, 4 2 1, 5
//        // 1 1)
//        LineString expected = new GeometryFactory().createLineString(new Coordinate[] {
//                new Coordinate(1, 1), new Coordinate(2, 2), new Coordinate(4, 2),
//                new Coordinate(5, 1) });
//
//        // check feature reader and the schema
//        try(FeatureReader<SimpleFeatureType, SimpleFeature> fr = dataStore.getFeatureReader(q,
//                Transaction.AUTO_COMMIT)) {
//            assertEquals(crs, fr.getFeatureType().getCoordinateReferenceSystem());
//            assertEquals(crs, fr.getFeatureType().getGeometryDescriptor()
//                    .getCoordinateReferenceSystem());
//            assertTrue(fr.hasNext());
//            SimpleFeature f = fr.next();
//            assertTrue(expected.equalsExact((Geometry) f.getDefaultGeometry()));
//        }
//		 */    }
//
}
