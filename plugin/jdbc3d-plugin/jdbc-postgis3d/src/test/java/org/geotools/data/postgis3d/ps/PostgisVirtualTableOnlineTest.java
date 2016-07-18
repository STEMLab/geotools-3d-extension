package org.geotools.data.postgis3d.ps;

import org.geotools.data.postgis3d.PostgisDataStoreAPITestSetup;
import org.geotools.jdbc3d.JDBCDataStoreAPITestSetup;
import org.geotools.jdbc3d.JDBCVirtualTableOnlineTest;

/**
 * 
 *
 * @source $URL$
 */
public class PostgisVirtualTableOnlineTest extends JDBCVirtualTableOnlineTest {

    @Override
    protected JDBCDataStoreAPITestSetup createTestSetup() {
        return new PostgisDataStoreAPITestSetup(new PostGISPSTestSetup());
    }

}
