/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.feature.simple;

import org.geotools.filter.ISOFilterFactoryImpl;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.primitive.Point;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import junit.framework.TestCase;

/**
 * 
 *
 * @source $URL$
 */
public class ISOSimpleFeatureBuilderTest extends TestCase {

    ISOSimpleFeatureBuilder builder;

    protected void setUp() throws Exception {
        ISOSimpleFeatureTypeBuilder typeBuilder = new ISOSimpleFeatureTypeBuilder();
        typeBuilder.setName( "test" );
        typeBuilder.add( "point", Point.class, (CoordinateReferenceSystem) null );
        typeBuilder.add( "integer", Integer.class );
        typeBuilder.add( "float", Float.class );

        SimpleFeatureType featureType = typeBuilder.buildFeatureType();

        builder = new ISOSimpleFeatureBuilder(featureType);
        builder.setValidating(true);
    }

    public void testSanity() throws Exception {
        ISOGeometryBuilder gb = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84);
        builder.add( gb.createPoint( gb.createDirectPosition() ) );
        builder.add( new Integer( 1 ) );
        builder.add( new Float( 2.0 ) );

        SimpleFeature feature = builder.buildFeature( "fid" );
        assertNotNull( feature );

        assertEquals( 3, feature.getAttributeCount() );

        assertTrue( gb.createPoint( gb.createDirectPosition( new double[] {Double.NaN, Double.NaN} )).equals( feature.getAttribute( "point" ) ) );
        assertEquals( new Integer( 1 ) , feature.getAttribute( "integer" ) );
        assertEquals( new Float( 2.0 ) , feature.getAttribute( "float" ) );
    }

    public void testTooFewAttributes() throws Exception {
        ISOGeometryBuilder gb = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84);
        builder.add( gb.createPoint( gb.createDirectPosition( new double[] {0, 0} ) ) );
        builder.add( new Integer( 1 ) );

        SimpleFeature feature = builder.buildFeature( "fid" );
        assertNotNull( feature );

        assertEquals( 3, feature.getAttributeCount() );
        
        gb.createPoint( gb.createDirectPosition( new double[] {0, 0} ) ).equals( feature.getAttribute( "point" ) );
        assertTrue( gb.createPoint( gb.createDirectPosition( new double[] {0, 0} ) ).equals( feature.getAttribute( "point" ) ) );
        assertEquals( new Integer( 1 ) , feature.getAttribute( "integer" ) );
        assertNull( feature.getAttribute( "float" ) );
    }

    public void testSetSequential() throws Exception {
        ISOGeometryBuilder gb = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84);
        builder.set( "point", gb.createPoint( gb.createDirectPosition( new double[] {0, 0} ) ));
        builder.set( "integer", new Integer( 1 ) );
        builder.set( "float",  new Float( 2.0 ) );

        SimpleFeature feature = builder.buildFeature( "fid" );
        assertNotNull( feature );

        assertEquals( 3, feature.getAttributeCount() );

        Point p1 = gb.createPoint( gb.createDirectPosition( new double[] {0, 0} ) );
        Object p2 = feature.getAttribute( 0 );
        p1.equals(p2);
        
        
        assertTrue( gb.createPoint( gb.createDirectPosition( new double[] {0, 0} ) ).equals( feature.getAttribute( 0 ) ) );
        assertEquals( new Integer( 1 ) , feature.getAttribute( 1 ) );
        assertEquals( new Float( 2.0 ) , feature.getAttribute( 2 ) );
    }

    public void testSetNonSequential() throws Exception {
        ISOGeometryBuilder gb = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84);
        builder.set( "float",  new Float( 2.0 ) );
        builder.set( "point", gb.createPoint( gb.createDirectPosition( new double[] {0, 0} ) ) );
        builder.set( "integer", new Integer( 1 ) );

        SimpleFeature feature = builder.buildFeature( "fid" );
        assertNotNull( feature );

        assertEquals( 3, feature.getAttributeCount() );

        assertTrue( gb.createPoint( gb.createDirectPosition( new double[] {0, 0} ) ).equals( feature.getAttribute( 0 ) ) );
        assertEquals( new Integer( 1 ) , feature.getAttribute( 1 ) );
        assertEquals( new Float( 2.0 ) , feature.getAttribute( 2 ) );
    }

    public void testSetTooFew() throws Exception {
        builder.set("integer", new Integer(1));
        SimpleFeature feature = builder.buildFeature( "fid" );
        assertNotNull( feature );

        assertEquals( 3, feature.getAttributeCount() );

        assertNull( feature.getAttribute( 0 ) );
        assertEquals( new Integer( 1 ) , feature.getAttribute( 1 ) );
        assertNull( feature.getAttribute( 2 ) );
    }

    public void testConverting() throws Exception {
        builder.set( "integer", "1" );
        SimpleFeature feature = builder.buildFeature("fid");

        try {
            builder.set( "integer", "foo" );    
            fail( "should have failed" );
        }
        catch( Exception e ) {}

    }

    public void testCreateFeatureWithLength() throws Exception {

        ISOSimpleFeatureTypeBuilder builder=new ISOSimpleFeatureTypeBuilder(); //$NON-NLS-1$
        builder.setName("test");
        builder.length(5).add("name", String.class);

        SimpleFeatureType featureType = builder.buildFeatureType();
        SimpleFeature feature = ISOSimpleFeatureBuilder.build( featureType, new Object[]{"Val"}, "ID" );

        assertNotNull(feature);

        try{
            feature = ISOSimpleFeatureBuilder.build( featureType, new Object[]{"Longer Than 5"}, "ID" );
            feature.validate();
            fail("this should fail because the value is longer than 5 characters");
        }catch (Exception e) {
            // good
        }
    } 

    public void testCreateFeatureWithRestriction() throws Exception {
        FilterFactory fac = new ISOFilterFactoryImpl(); //CommonFactoryFinder.getFilterFactory(null);

        String attributeName = "string";
        PropertyIsEqualTo filter = fac.equals(fac.property("."), fac.literal("Value"));

        ISOSimpleFeatureTypeBuilder builder = new ISOSimpleFeatureTypeBuilder(); //$NON-NLS-1$
        builder.setName("test");
        builder.restriction(filter).add(attributeName, String.class);

        SimpleFeatureType featureType = builder.buildFeatureType();
        SimpleFeature feature = SimpleFeatureBuilder.build( featureType, new Object[]{"Value"}, "ID" );

        assertNotNull(feature);

        try {
            SimpleFeature sf = ISOSimpleFeatureBuilder.build( featureType, new Object[]{"NotValue"}, "ID" );
            sf.validate();
            fail( "PropertyIsEqualTo filter should have failed");
        }
        catch( Exception e ) {
            //good
        }

    }

    public void testUserData() throws Exception {
        ISOGeometryBuilder gb = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84);
        builder.add( gb.createPoint( gb.createDirectPosition( new double[] {0, 0} ) ) );
        builder.add( new Integer( 1 ) );
        builder.add( new Float( 2.0 ) );
        builder.featureUserData("foo", "bar");

        SimpleFeature feature = builder.buildFeature( "fid" );
        assertNotNull( feature );
        assertEquals("bar", feature.getUserData().get("foo"));

        builder = new ISOSimpleFeatureBuilder(feature.getFeatureType());
        builder.init(feature);
        feature = builder.buildFeature( "fid" );
        assertNotNull( feature );
        assertEquals("bar", feature.getUserData().get("foo"));
    }

}
