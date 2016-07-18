package org.geotools.data.postgis3d.ps;

import org.geotools.data.postgis3d.PostgisDateTestSetup;
import org.geotools.jdbc3d.JDBCDateOnlineTest;
import org.geotools.jdbc3d.JDBCDateTestSetup;

/**
 * 
 *
 * @source $URL$
 */
public class PostgisDateOnlineTest extends JDBCDateOnlineTest {

    @Override
    protected JDBCDateTestSetup createTestSetup() {
        return new PostgisDateTestSetup(new PostGISPSTestSetup());
    }
    
}
