package org.geotools.data.oracle;

import org.geotools.jdbc3d.JDBCNoPrimaryKeyOnlineTest;
import org.geotools.jdbc3d.JDBCNoPrimaryKeyTestSetup;

/**
 * 
 *
 * @source $URL$
 */
public class OracleNoPrimaryKeyOnlineTest extends JDBCNoPrimaryKeyOnlineTest {

    @Override
    protected JDBCNoPrimaryKeyTestSetup createTestSetup() {
        return new OracleNoPrimaryKeyTestSetup();
    }

}
