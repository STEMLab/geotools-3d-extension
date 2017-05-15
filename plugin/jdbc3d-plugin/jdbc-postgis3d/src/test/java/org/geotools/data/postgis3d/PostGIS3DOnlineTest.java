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
package org.geotools.data.postgis3d;

import org.geotools.jdbc.iso.JDBC3DOnlineTest;
import org.geotools.jdbc.iso.JDBC3DTestSetup;

/**
 * 
 *
 * @source $URL$
 */
public class PostGIS3DOnlineTest extends JDBC3DOnlineTest  {

    @Override
    protected JDBC3DTestSetup createTestSetup() {
        return new PostGIS3DTestSetup(new PostGISTestSetup());
    }
    
//    protected DataStore getTESTDataStore() throws IOException{
//		Map<String, Object> params = new HashMap<>();
//		params.put("dbtype", "postgis");
//		//params.put("url", "jdbc:postgresql://localhost/test2");
//		params.put("host", "localhost");
//		params.put("database", "test2");
//		params.put("schema", "public");
//		params.put("port", 5432);
//		params.put("user", "postgres");
//		params.put("passwd", "postgres");
//		DataStore dataStore = DataStoreFinder.getDataStore(params);
//		return dataStore;
//	}

}
