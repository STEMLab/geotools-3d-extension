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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.geotools.factory.CommonFactoryFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.FilterFactory2;

/**
 * @author hgryoo
 *
 */
public class PropertyDataStore3DTest {
    
    private PropertyDataStore store;
    static FilterFactory2 ff = (FilterFactory2) CommonFactoryFinder.getFilterFactory(null);
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        File dir = new File(".", "propertyTestData" );
        dir.mkdir();
        
        File file = new File( dir ,"road.properties");
        if( file.exists()){
            file.delete();
        }
        BufferedWriter writer = new BufferedWriter( new FileWriter( file ) );
        writer.write("_=id:Integer,geom:ISOGeometry"); writer.newLine();
        writer.write("fid1=1|fid1=1|Solid((((-125745.58224841699 3813.6302470150695 0.0, -126738.38435842091 3815.241221547709 0.0, -126731.71448564173 4815.075754128498 0.0, -125738.91237563781 4813.464779595859 0.0, -125745.58224841699 3813.6302470150695 0.0))"
        + ", ((-126731.71448564173 4815.075754128498 0.0, -126738.38435842091 3815.241221547709 0.0, -126738.38435842091 3815.241221547709 3000.0, -126731.71448564173 4815.075754128498 3000.0, -126731.71448564173 4815.075754128498 0.0))"
        + ", ((-125745.58224841699 3813.6302470150695 3000.0, -125738.91237563781 4813.464779595859 3000.0, -126731.71448564173 4815.075754128498 3000.0, -126738.38435842091 3815.241221547709 3000.0, -125745.58224841699 3813.6302470150695 3000.0))"
        + ", ((-125738.91237563781 4813.464779595859 3000.0, -125745.58224841699 3813.6302470150695 3000.0, -125745.58224841699 3813.6302470150695 0.0, -125738.91237563781 4813.464779595859 0.0, -125738.91237563781 4813.464779595859 3000.0))"
        + ", ((-125738.91237563781 4813.464779595859 0.0, -126731.71448564173 4815.075754128498 0.0, -126731.71448564173 4815.075754128498 3000.0, -125738.91237563781 4813.464779595859 3000.0, -125738.91237563781 4813.464779595859 0.0))"
        + ", ((-125745.58224841699 3813.6302470150695 0.0, -125745.58224841699 3813.6302470150695 3000.0, -126738.38435842091 3815.241221547709 3000.0, -126738.38435842091 3815.241221547709 0.0, -125745.58224841699 3813.6302470150695 0.0))))"); writer.newLine();
        writer.close();
        
        store = new PropertyDataStore( dir, "propertyTestData" );
    }

    /**
     * @throws java.lang.Exception
     */
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
    
    @Test
    public void testGetNames() throws IOException {
        String names[] = store.getTypeNames();
        Arrays.sort(names);
        
        for(String s : names) {
            System.out.println(s);
        }
    }
    
    @Test
    public void testGetSchema() throws IOException {
        SimpleFeatureType type = store.getSchema( "road" );
        assertNotNull( type );
        assertEquals( "road", type.getTypeName() );
        assertEquals( "propertyTestData", type.getName().getNamespaceURI().toString() );
        assertEquals( 2, type.getAttributeCount() );
        
        AttributeDescriptor id = type.getDescriptor(0);
        AttributeDescriptor geom = type.getDescriptor(1);
        
        System.out.println(geom);
    }
}
