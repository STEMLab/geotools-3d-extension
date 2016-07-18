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
package org.geotools.data.postgis3d.ps;

import org.geotools.data.postgis3d.PostGISBooleanTestSetup;
import org.geotools.jdbc3d.JDBCBooleanOnlineTest;
import org.geotools.jdbc3d.JDBCBooleanTestSetup;

/**
 * 
 *
 * @source $URL$
 */
public class PostGISBooleanOnlineTest extends JDBCBooleanOnlineTest {

    @Override
    protected JDBCBooleanTestSetup createTestSetup() {
        return new PostGISBooleanTestSetup(new PostGISPSTestSetup());
    }

}
