package org.geotools.data.oracle;

import org.geotools.jdbc3d.JDBCAggregateFunctionOnlineTest;
import org.geotools.jdbc3d.JDBCTestSetup;

/**
 * 
 *
 * @source $URL$
 */
public class OracleAggregateFunctionOnlineTest extends JDBCAggregateFunctionOnlineTest {

    @Override
    protected JDBCTestSetup createTestSetup() {
        return new OracleTestSetup();
    }

}
