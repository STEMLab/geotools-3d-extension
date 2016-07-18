package org.geotools.data.postgis3d;

import org.geotools.jdbc3d.JDBCThreeValuedLogicOnlineTest;
import org.geotools.jdbc3d.JDBCThreeValuedLogicTestSetup;

public class PostGISThreeValuedLogicOnlineTest extends JDBCThreeValuedLogicOnlineTest {

    @Override
    protected JDBCThreeValuedLogicTestSetup createTestSetup() {
        return new JDBCThreeValuedLogicTestSetup(new PostGISTestSetup());
    }

}
