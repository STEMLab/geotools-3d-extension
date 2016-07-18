/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2006-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.data.postgis3d.ps;

import org.geotools.data.postgis3d.PostgisGeographyTestSetup;
import org.geotools.jdbc3d.JDBCDataStore;
import org.geotools.jdbc3d.JDBCGeographyOnlineTest;
import org.geotools.jdbc3d.JDBCGeographyTestSetup;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * 
 *
 * @source $URL$
 */
public class PostgisGeographyOnlineTest extends JDBCGeographyOnlineTest {

    @Override
    protected JDBCGeographyTestSetup createTestSetup() {
        return new PostgisGeographyTestSetup(new PostGISPSTestSetup());
    }

    @Override
    public void testSchema() throws Exception {
        super.testSchema();
        
        if (!isGeographySupportAvailable()) {
            return;
        }

        // extra check, pg specific: the native typename is actually geography
        SimpleFeatureType ft = dataStore.getFeatureSource(tname("geopoint")).getSchema();
        assertEquals("geography", ft.getGeometryDescriptor().getUserData().get(JDBCDataStore.JDBC_NATIVE_TYPENAME));
    }
}
