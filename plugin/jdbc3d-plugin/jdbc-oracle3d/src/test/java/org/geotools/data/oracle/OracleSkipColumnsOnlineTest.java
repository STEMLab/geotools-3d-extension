package org.geotools.data.oracle;

import org.geotools.jdbc3d.JDBCSkipColumnOnlineTest;
import org.geotools.jdbc3d.JDBCSkipColumnTestSetup;

/**
 * 
 *
 * @source $URL$
 */
public class OracleSkipColumnsOnlineTest extends JDBCSkipColumnOnlineTest {

    @Override
    protected JDBCSkipColumnTestSetup createTestSetup() {
        return new OracleSkipColumnTestSetup();
    }

}
