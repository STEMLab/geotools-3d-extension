package org.geotools.data.postgis3d.ps;

import org.geotools.data.postgis3d.PostgisLobTestSetup;
import org.geotools.jdbc3d.JDBCLobOnlineTest;
import org.geotools.jdbc3d.JDBCLobTestSetup;

/**
 * 
 *
 * @source $URL$
 */
public class PostgisLobOnlineTest extends JDBCLobOnlineTest {

    @Override
    protected JDBCLobTestSetup createTestSetup() {
        return new PostgisLobTestSetup(new PostGISPSTestSetup());
    }

}
