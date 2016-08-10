package org.geotools.data.oracle;

import org.geotools.jdbc3d.JDBCDateTestSetup;
import org.geotools.jdbc3d.JDBCTemporalFilterOnlineTest;

public class OracleTemporalFilterOnlineTest extends JDBCTemporalFilterOnlineTest {

    @Override
    protected JDBCDateTestSetup createTestSetup() {
        return new OracleDateTestSetup(new OracleTestSetup());
    }

}
