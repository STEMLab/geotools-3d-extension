package org.geotools.data.oracle;

import java.util.HashMap;

import org.geotools.jdbc3d.JDBCDataStoreFactory;
import org.geotools.jdbc3d.JDBCPrimaryKeyFinderOnlineTest;
import org.geotools.jdbc3d.JDBCPrimaryKeyFinderTestSetup;

/**
 * 
 *
 * @source $URL$
 */
public class OraclePrimaryKeyFinderOnlineTest extends JDBCPrimaryKeyFinderOnlineTest {

    @Override
    protected JDBCPrimaryKeyFinderTestSetup createTestSetup() {
        return new OraclePrimaryKeyFinderTestSetup();
    }
    
    @Override
    protected HashMap createDataStoreFactoryParams() throws Exception {
        HashMap params = super.createDataStoreFactoryParams();
        params.put(JDBCDataStoreFactory.PK_METADATA_TABLE.key, "GT_PK_METADATA");
        return params;
    }

}
