package org.geotools.data.oracle;

import org.geotools.jdbc3d.JDBCLobOnlineTest;
import org.geotools.jdbc3d.JDBCLobTestSetup;

/**
 * 
 *
 * @source $URL$
 */
public class OracleLobOnlineTest extends JDBCLobOnlineTest {

    @Override
    protected JDBCLobTestSetup createTestSetup() {
        return new OracleLobTestSetup();
    }

}
