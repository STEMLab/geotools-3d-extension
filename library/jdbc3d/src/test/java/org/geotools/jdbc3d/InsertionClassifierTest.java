/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2015, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.jdbc3d;

//import com.vividsolutions.jts.geom.Geometry;
//import com.vividsolutions.jts.geom.GeometryFactory;
//import com.vividsolutions.jts.geom.LineString;
//import com.vividsolutions.jts.geom.LinearRing;
//import com.vividsolutions.jts.geom.Polygon;
import junit.framework.TestCase;
import org.geotools.factory.Hints;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.iso.PositionFactoryImpl;
import org.geotools.geometry.iso.coordinate.GeometryFactoryImpl;
import org.geotools.geometry.iso.coordinate.LineSegmentImpl;
import org.geotools.geometry.iso.primitive.CurveImpl;
import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.geometry.jts.LiteCoordinateSequence;
import org.geotools.jdbc.InsertionClassifier;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.PositionFactory;
import org.opengis.geometry.coordinate.GeometryFactory;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.PrimitiveFactory;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InsertionClassifierTest extends TestCase {

    private GeometryFactory geometryFactory = new GeometryFactoryImpl();

    public void testSegregateSimple() throws Exception {
        SimpleFeatureType featureType = buildType();
        Collection<SimpleFeature> features = new ArrayList<>();
        features.add(createFeature(featureType, "toto", createLineString(), createPolygon()));
        features.add(createFeature(featureType, "tutu", createLineString(), createPolygon()));
        Map<InsertionClassifier, Collection<SimpleFeature>> actual =
                InsertionClassifier.classify(featureType, features);
        assertEquals(1, actual.size());
        for (InsertionClassifier kind : actual.keySet()) {
            assertEquals(false, kind.useExisting);
            assertEquals(2, kind.geometryTypes.size());
            //assertEquals(LineString.class, kind.geometryTypes.get("geom1"));
            assertEquals(Curve.class, kind.geometryTypes.get("geom1"));
            //assertEquals(Polygon.class, kind.geometryTypes.get("geom2"));
            assertEquals(Surface.class, kind.geometryTypes.get("geom2"));
            assertEquals(2, actual.get(kind).size());
        }
    }

    public void testSegregateMultipleGeomKinds() throws Exception {
        SimpleFeatureType featureType = buildType();
        Collection<SimpleFeature> features = new ArrayList<>();
        features.add(createFeature(featureType, "toto", createLineString(), createPolygon()));
        features.add(createFeature(featureType, "tutu", createLineString(), createLineString()));
        Map<InsertionClassifier, Collection<SimpleFeature>> actual =
                InsertionClassifier.classify(featureType, features);
        assertEquals(2, actual.size());
        Set<Class<? extends Geometry>> geom2Classes = new HashSet<>();
        for (InsertionClassifier kind : actual.keySet()) {
            assertEquals(false, kind.useExisting);
            assertEquals(2, kind.geometryTypes.size());
            //assertEquals(LineString.class, kind.geometryTypes.get("geom1"));
            assertEquals(Curve.class, kind.geometryTypes.get("geom1"));
            geom2Classes.add(kind.geometryTypes.get("geom2"));
            assertEquals(1, actual.get(kind).size());
        }
        //assertEquals(new HashSet<>(Arrays.asList(LineString.class, Polygon.class)), geom2Classes);
        assertEquals(new HashSet<>(Arrays.asList(Curve.class, Surface.class)), geom2Classes);
        
    }

    public void testSegregateUseExisting() throws Exception {
        SimpleFeatureType featureType = buildType();
        Collection<SimpleFeature> features = new ArrayList<>();
        SimpleFeature f2 = createFeature(featureType, "toto", createLineString(), createPolygon());
        f2.getUserData().put(Hints.USE_PROVIDED_FID, true);
        features.add(f2);
        features.add(createFeature(featureType, "tutu", createLineString(), createPolygon()));
        Map<InsertionClassifier, Collection<SimpleFeature>> actual =
                InsertionClassifier.classify(featureType, features);
        assertEquals(2, actual.size());
        Set<Boolean> uses = new HashSet<>();
        for (InsertionClassifier kind : actual.keySet()) {
            uses.add(kind.useExisting);
            assertEquals(2, kind.geometryTypes.size());
            //assertEquals(LineString.class, kind.geometryTypes.get("geom1"));
            assertEquals(Curve.class, kind.geometryTypes.get("geom1"));
            //assertEquals(Polygon.class, kind.geometryTypes.get("geom2"));
            assertEquals(Surface.class, kind.geometryTypes.get("geom2"));
            assertEquals(1, actual.get(kind).size());
        }
        assertEquals(new HashSet<>(Arrays.asList(Boolean.FALSE, Boolean.TRUE)), uses);
    }

    public void testSegregateNullGeom() throws Exception {
        SimpleFeatureType featureType = buildType();
        Collection<SimpleFeature> features = new ArrayList<>();
        features.add(createFeature(featureType, "toto", createLineString(), createPolygon()));
        features.add(createFeature(featureType, "tutu", createLineString(), null));
        Map<InsertionClassifier, Collection<SimpleFeature>> actual =
                InsertionClassifier.classify(featureType, features);
        assertEquals(2, actual.size());
        Set<Class<? extends Geometry>> geom2Classes = new HashSet<>();
        for (InsertionClassifier kind : actual.keySet()) {
            assertEquals(false, kind.useExisting);
            assertEquals(2, kind.geometryTypes.size());
            //assertEquals(LineString.class, kind.geometryTypes.get("geom1"));
            assertEquals(Curve.class, kind.geometryTypes.get("geom1"));
            geom2Classes.add(kind.geometryTypes.get("geom2"));
            assertEquals(1, actual.get(kind).size());
        }
        //assertEquals(new HashSet<>(Arrays.asList(null, Polygon.class)), geom2Classes);
        assertEquals(new HashSet<>(Arrays.asList(null, Surface.class)), geom2Classes);
       }

    //private Polygon createPolygon() {
    private Surface createPolygon() {
        return createPolygon(0, 0, 1, 1, 2, 2, 0, 0);
    }

    //private LineString createLineString() {
    private Curve createLineString() {
        return createLineString(0, 1, 2, 3);
    }

    //private SimpleFeature createFeature(SimpleFeatureType featureType, String name, LineString geom1, Geometry geom2) {
    private SimpleFeature createFeature(SimpleFeatureType featureType, String name, Curve geom1, Geometry geom2) {
          
    	SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);
        builder.add(name);
        builder.add(geom1);
        builder.add(geom2);
        return builder.buildFeature(name);
    }

    //private Polygon createPolygon(float... coords) {
    private Surface createPolygon(float... coords) {
    	CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
    	PositionFactory postitionFactory = new PositionFactoryImpl( crs );
        //LinearRing shell = new LinearRing(new LiteCoordinateSequence(coords), geometryFactory);
    	PrimitiveFactory primitiveFactory = new PrimitiveFactoryImpl( crs, postitionFactory );
        
    	Curve curve = createLineString( coords );
    	List<OrientableCurve> curves = new ArrayList<OrientableCurve>();
    	curves.add( curve); 
    	Ring ring = primitiveFactory.createRing( curves );
        SurfaceBoundary boundary = primitiveFactory.createSurfaceBoundary(ring,new ArrayList());
        return primitiveFactory.createSurface(boundary);
    	//return new Polygon(shell, null, geometryFactory);
    }

    //private LineString createLineString(float... coords) {
    private Curve createLineString(float... coords) {
    	CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
    	List segments = new ArrayList(coords.length / 2 - 1);
    	for(int i = 0;i < coords.length / 2 - 1;i++) {
    		segments.add( new LineSegmentImpl( crs, new double[]{coords[i],coords[i + 1]}, new double[]{coords[i + 2],coords[i + 3]}, 0) );
    		
    	}
		
        //return new LineString(new LiteCoordinateSequence(coords), geometryFactory);
    	return new CurveImpl( crs, segments );	
    }

    private static SimpleFeatureType buildType() {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Test");
        builder.add("name", String.class);
        //builder.add("geom1", LineString.class);
        builder.add("geom1", Curve.class);
        builder.add("geom2", Geometry.class);
        return builder.buildFeatureType();
    }
}