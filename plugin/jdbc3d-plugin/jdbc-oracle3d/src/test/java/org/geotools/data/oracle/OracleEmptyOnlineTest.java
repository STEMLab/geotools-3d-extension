package org.geotools.data.oracle;

import org.geotools.jdbc3d.JDBCEmptyOnlineTest;
import org.geotools.jdbc3d.JDBCEmptyTestSetup;

/**
 * 
 *
 * @source $URL$
 */
public class OracleEmptyOnlineTest extends JDBCEmptyOnlineTest {

    @Override
    protected JDBCEmptyTestSetup createTestSetup() {
        return new OracleEmptyTestSetup();
    }

}
