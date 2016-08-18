/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This interface can be implemented to perform custom behavior on each connection as it gets
 * borrowed from the connection pool and then released back to the pool.
 * 
 * @author Andrea Aime - GeoSolutions
 * 
 */
public interface ConnectionLifecycleListener3D {

    /**
     * Called when the collection is being borrowed from the connection pool
     */
    public void onBorrow(JDBCDataStore3D store, Connection cx) throws SQLException;

    /**
     * Called when the collection is being released back to the connection pool
     */
    public void onRelease(JDBCDataStore3D store, Connection cx) throws SQLException;

    /**
     * Called when the connection comes to a commit
     * @throws SQLException 
     */
    public void onCommit(JDBCDataStore3D store, Connection cx) throws SQLException;
    
    /**
     * Called when the connection comes to a rollback
     */
    public void onRollback(JDBCDataStore3D store, Connection cx) throws SQLException;

}
