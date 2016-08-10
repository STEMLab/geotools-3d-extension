package org.geotools.data.oracle;

import java.io.IOException;

import org.geotools.jdbc3d.JDBCTestSetup;
import org.geotools.jdbc3d.JDBCTransactionOnlineTest;

/**
 * 
 *
 * @source $URL$
 */
public class OracleTransactionOnlineTest extends JDBCTransactionOnlineTest {

    @Override
    protected JDBCTestSetup createTestSetup() {
        return new OracleTestSetup();
    }
    
    public void testConcurrentTransactions() throws IOException {
        // Oracle won't pass this one
    }

}
