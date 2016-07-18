package org.geotools.data.postgis3d;

import org.geotools.jdbc3d.JDBCSkipColumnOnlineTest;
import org.geotools.jdbc3d.JDBCSkipColumnTestSetup;

/**
 * 
 *
 * @source $URL$
 */
public class PostgisSkipColumnOnlineTest extends JDBCSkipColumnOnlineTest {

    @Override
    protected JDBCSkipColumnTestSetup createTestSetup() {
        return new PostgisSkipColumnTestSetup();
    }

}
