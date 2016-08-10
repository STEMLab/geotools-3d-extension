package org.geotools.data.oracle;

import java.util.TimeZone;

import org.geotools.jdbc3d.JDBCDateTestSetup;
import org.geotools.jdbc3d.JDBCTimeZoneDateOnlineTest;

/**
 * 
 *
 * @source $URL$
 */
public class OracleTimeZoneGMTPlus12DateOnlineTest extends JDBCTimeZoneDateOnlineTest {

    @Override
    protected JDBCDateTestSetup createTestSetup() {
        super.setTimeZone(TimeZone.getTimeZone("Etc/GMT+12"));
        return new OracleDateTestSetup(new OracleTestSetup());
    }
    
}
