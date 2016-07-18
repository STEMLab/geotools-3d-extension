package org.geotools.data.postgis3d;

import java.util.TimeZone;

import org.geotools.data.postgis3d.PostgisDateTestSetup;
import org.geotools.jdbc3d.JDBCDateTestSetup;
import org.geotools.jdbc3d.JDBCTimeZoneDateOnlineTest;

/**
 * 
 *
 * @source $URL$
 */
public class PostgisTimeZoneCETDateOnlineTest extends JDBCTimeZoneDateOnlineTest {

    @Override
    protected JDBCDateTestSetup createTestSetup() {
        super.setTimeZone(TimeZone.getTimeZone("CET"));
        return new PostgisDateTestSetup(new PostGISTestSetup());
    }
    
}
