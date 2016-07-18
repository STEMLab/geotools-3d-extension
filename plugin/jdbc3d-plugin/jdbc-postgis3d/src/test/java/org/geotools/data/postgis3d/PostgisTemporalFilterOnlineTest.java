package org.geotools.data.postgis3d;

import org.geotools.jdbc3d.JDBCDateTestSetup;
import org.geotools.jdbc3d.JDBCTemporalFilterOnlineTest;

public class PostgisTemporalFilterOnlineTest extends JDBCTemporalFilterOnlineTest {

    @Override
    protected JDBCDateTestSetup createTestSetup() {
        return new PostgisDateTestSetup(new PostGISTestSetup());
    }

}
