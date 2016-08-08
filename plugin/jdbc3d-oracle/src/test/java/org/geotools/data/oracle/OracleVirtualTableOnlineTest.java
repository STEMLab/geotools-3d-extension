package org.geotools.data.oracle;

import org.geotools.jdbc3d.JDBCDataStoreAPITestSetup;
import org.geotools.jdbc3d.JDBCVirtualTableOnlineTest;

/**
 * 
 *
 * @source $URL$
 */
public class OracleVirtualTableOnlineTest extends JDBCVirtualTableOnlineTest {

    @Override
    protected JDBCDataStoreAPITestSetup createTestSetup() {
        return new OracleDataStoreAPITestSetup(new OracleTestSetup());
    }

}
