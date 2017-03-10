package org.geotools.data.jdbc.iso;

import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class LoggableStatement implements PreparedStatement { 

        /**
         * used for storing parameter values needed for producing log
         */
        private ArrayList parameterValues;

        /**
         *the query string with question marks as parameter placeholders
         */
        private String sqlTemplate;

        /**
         *  a statement created from a real database connection
         */
        private PreparedStatement wrappedStatement;

        /**
                * Constructs a LoggableStatement.
                *
                * Creates {@link java.sql.PreparedStatement PreparedStatement} with the query string <code>sql</code> using
                * the specified <code>connection</code> by calling {@link java.sql.Connection#prepareStatement(String)}.
                * <p>
                * Whenever a call is made to this <code>LoggableStatement</code> it is forwarded to the prepared statment created from
                * <code>connection</code> after first saving relevant parameters for use in logging output.
                *
                * @param Connection java.sql.Connection a JDBC-connection to be used for obtaining a "real statement"
                * @param sql java.lang.String thw sql to exectute
                * @exception java.sql.SQLException if a <code>PreparedStatement</code> cannot be created
                * using the supplied <code>connection</code> and <code>sql</code>
                */

        public LoggableStatement(Connection connection, String sql)
                throws SQLException {
                wrappedStatement = connection.prepareStatement(sql);
                sqlTemplate = sql;
                parameterValues = new ArrayList();
        }

        /**
         * JDBC 2.0
         *
         * Adds a set of parameters to the batch.
         *
         * @exception SQLException if a database access error occurs
         * @see Statement#addBatch
         */
        public void addBatch() throws java.sql.SQLException {
                wrappedStatement.addBatch();
        }
        /**
         * JDBC 2.0
         *
         * Adds a SQL command to the current batch of commmands for the statement.
         * This method is optional.
         *
         * @param sql typically this is a static SQL INSERT or UPDATE statement
         * @exception SQLException if a database access error occurs, or the
         * driver does not support batch statements
         */
        public void addBatch(String sql) throws java.sql.SQLException {
                wrappedStatement.addBatch(sql);
        }
        /**
         * Cancels this <code>Statement</code> object if both the DBMS and
         * driver support aborting an SQL statement.
         * This method can be used by one thread to cancel a statement that
         * is being executed by another thread.
         *
         * @exception SQLException if a database access error occurs
         */
        public void cancel() throws SQLException {
                wrappedStatement.cancel();
        }
        /**
         * JDBC 2.0
         *
         * Makes the set of commands in the current batch empty.
         * This method is optional.
         *
         * @exception SQLException if a database access error occurs or the
         * driver does not support batch statements
         */
        public void clearBatch() throws java.sql.SQLException {
                wrappedStatement.clearBatch();
        }
        /**
         * Clears the current parameter values immediately.
         * <P>In general, parameter values remain in force for repeated use of a
         * Statement. Setting a parameter value automatically clears its
         * previous value.  However, in some cases it is useful to immediately
         * release the resources used by the current parameter values; this can
         * be done by calling clearParameters.
         *
         * @exception SQLException if a database access error occurs
         */
        public void clearParameters() throws java.sql.SQLException {
                wrappedStatement.clearParameters();
        }
        /**
         * Clears all the warnings reported on this <code>Statement</code>
         * object. After a call to this method,
         * the method <code>getWarnings</code> will return
         * null until a new warning is reported for this Statement.
         *
         * @exception SQLException if a database access error occurs
         */
        public void clearWarnings() throws java.sql.SQLException {
                wrappedStatement.clearWarnings();
        }
        /**
         * Releases this <code>Statement</code> object's database
         * and JDBC resources immediately instead of waiting for
         * this to happen when it is automatically closed.
         * It is generally good practice to release resources as soon as
         * you are finished with them to avoid tying up database
         * resources.
         * <P><B>Note:</B> A Statement is automatically closed when it is
         * garbage collected. When a Statement is closed, its current
         * ResultSet, if one exists, is also closed.
         *
         * @exception SQLException if a database access error occurs
         */
        public void close() throws java.sql.SQLException {
                wrappedStatement.close();
        }
        /**
         * Executes any kind of SQL statement.
         * Some prepared statements return multiple results; the execute
         * method handles these complex statements as well as the simpler
         * form of statements handled by executeQuery and executeUpdate.
         *
         * @exception SQLException if a database access error occurs
         * @see Statement#execute
         */
        public boolean execute() throws java.sql.SQLException {
                return wrappedStatement.execute();
        }
        /**
         * Executes a SQL statement that may return multiple results.
         * Under some (uncommon) situations a single SQL statement may return
         * multiple result sets and/or update counts.  Normally you can ignore
         * this unless you are (1) executing a stored procedure that you know may
         * return multiple results or (2) you are dynamically executing an
         * unknown SQL string.  The  methods <code>execute</code>,
         * <code>getMoreResults</code>, <code>getResultSet</code>,
         * and <code>getUpdateCount</code> let you navigate through multiple results.
         *
         * The <code>execute</code> method executes a SQL statement and indicates the
         * form of the first result.  You can then use getResultSet or
         * getUpdateCount to retrieve the result, and getMoreResults to
         * move to any subsequent result(s).
         *
         * @param sql any SQL statement
         * @return true if the next result is a ResultSet; false if it is
         * an update count or there are no more results
         * @exception SQLException if a database access error occurs
         * @see #getResultSet
         * @see #getUpdateCount
         * @see #getMoreResults
         */
        public boolean execute(String sql) throws java.sql.SQLException {
                return wrappedStatement.execute(sql);
        }
        /**
         * JDBC 2.0
         *
         * Submits a batch of commands to the database for execution.
         * This method is optional.
         *
         * @return an array of update counts containing one element for each
         * command in the batch.  The array is ordered according
         * to the order in which commands were inserted into the batch.
         * @exception SQLException if a database access error occurs or the
         * driver does not support batch statements
         */
        public int[] executeBatch() throws java.sql.SQLException {
                return wrappedStatement.executeBatch();
        }
        /**
         * Executes the SQL query in this <code>PreparedStatement</code> object
         * and returns the result set generated by the query.
         *
         * @return a ResultSet that contains the data produced by the
         * query; never null
         * @exception SQLException if a database access error occurs
         */
        public java.sql.ResultSet executeQuery() throws java.sql.SQLException {
                return wrappedStatement.executeQuery();
        }
        /**
         * Executes a SQL statement that returns a single ResultSet.
         *
         * @param sql typically this is a static SQL SELECT statement
         * @return a ResultSet that contains the data produced by the
         * query; never null
         * @exception SQLException if a database access error occurs
         */
        public java.sql.ResultSet executeQuery(String sql)
                throws java.sql.SQLException {
                return wrappedStatement.executeQuery(sql);
        }
        /**
         * Executes the SQL INSERT, UPDATE or DELETE statement
         * in this <code>PreparedStatement</code> object.
         * In addition,
         * SQL statements that return nothing, such as SQL DDL statements,
         * can be executed.
         *
         * @return either the row count for INSERT, UPDATE or DELETE statements;
         * or 0 for SQL statements that return nothing
         * @exception SQLException if a database access error occurs
         */
        public int executeUpdate() throws java.sql.SQLException {
                return wrappedStatement.executeUpdate();
        }
        /**
         * Executes an SQL INSERT, UPDATE or DELETE statement. In addition,
         * SQL statements that return nothing, such as SQL DDL statements,
         * can be executed.
         *
         * @param sql a SQL INSERT, UPDATE or DELETE statement or a SQL
         * statement that returns nothing
         * @return either the row count for INSERT, UPDATE or DELETE or 0
         * for SQL statements that return nothing
         * @exception SQLException if a database access error occurs
         */
        public int executeUpdate(String sql) throws java.sql.SQLException {
                return wrappedStatement.executeUpdate(sql);
        }
        /**
         * JDBC 2.0
         *
         * Returns the <code>Connection</code> object
         * that produced this <code>Statement</code> object.
         * @return the connection that produced this statement
         * @exception SQLException if a database access error occurs
         */
        public java.sql.Connection getConnection() throws java.sql.SQLException {
                return wrappedStatement.getConnection();
        }
        /**
         * JDBC 2.0
         *
         * Retrieves the direction for fetching rows from
         * database tables that is the default for result sets
         * generated from this <code>Statement</code> object.
         * If this <code>Statement</code> object has not set
         * a fetch direction by calling the method <code>setFetchDirection</code>,
         * the return value is implementation-specific.
         *
         * @return the default fetch direction for result sets generated
         *          from this <code>Statement</code> object
         * @exception SQLException if a database access error occurs
         */
        public int getFetchDirection() throws java.sql.SQLException {
                return wrappedStatement.getFetchDirection();
        }
        /**
         * JDBC 2.0
         *
         * Retrieves the number of result set rows that is the default
         * fetch size for result sets
         * generated from this <code>Statement</code> object.
         * If this <code>Statement</code> object has not set
         * a fetch size by calling the method <code>setFetchSize</code>,
         * the return value is implementation-specific.
         * @return the default fetch size for result sets generated
         *          from this <code>Statement</code> object
         * @exception SQLException if a database access error occurs
         */
        public int getFetchSize() throws java.sql.SQLException {
                return wrappedStatement.getFetchSize();
        }
        /**
         * Returns the maximum number of bytes allowed
         * for any column value.
         * This limit is the maximum number of bytes that can be
         * returned for any column value.
         * The limit applies only to BINARY,
         * VARBINARY, LONGVARBINARY, CHAR, VARCHAR, and LONGVARCHAR
         * columns.  If the limit is exceeded, the excess data is silently
         * discarded.
         *
         * @return the current max column size limit; zero means unlimited
         * @exception SQLException if a database access error occurs
         */
        public int getMaxFieldSize() throws java.sql.SQLException {
                return wrappedStatement.getMaxFieldSize();
        }
        /**
         * Retrieves the maximum number of rows that a
         * ResultSet can contain.  If the limit is exceeded, the excess
         * rows are silently dropped.
         *
         * @return the current max row limit; zero means unlimited
         * @exception SQLException if a database access error occurs
         */
        public int getMaxRows() throws java.sql.SQLException {
                return wrappedStatement.getMaxRows();
        }
        /**
         * JDBC 2.0
         *
         * Gets the number, types and properties of a ResultSet's columns.
         *
         * @return the description of a ResultSet's columns
         * @exception SQLException if a database access error occurs
         */
        public java.sql.ResultSetMetaData getMetaData()
                throws java.sql.SQLException {
                return wrappedStatement.getMetaData();
        }
        /**
         * Moves to a Statement's next result.  It returns true if
         * this result is a ResultSet.  This method also implicitly
         * closes any current ResultSet obtained with getResultSet.
         *
         * There are no more results when (!getMoreResults() &&
         * (getUpdateCount() == -1)
         *
         * @return true if the next result is a ResultSet; false if it is
         * an update count or there are no more results
         * @exception SQLException if a database access error occurs
         * @see #execute
         */
        public boolean getMoreResults() throws java.sql.SQLException {
                return wrappedStatement.getMoreResults();
        }
        /**
         * Retrieves the number of seconds the driver will
         * wait for a Statement to execute. If the limit is exceeded, a
         * SQLException is thrown.
         *
         * @return the current query timeout limit in seconds; zero means unlimited
         * @exception SQLException if a database access error occurs
         */
        public int getQueryTimeout() throws java.sql.SQLException {
                return wrappedStatement.getQueryTimeout();
        }
        /**
         *  Returns the current result as a <code>ResultSet</code> object.
         *  This method should be called only once per result.
         *
         * @return the current result as a ResultSet; null if the result
         * is an update count or there are no more results
         * @exception SQLException if a database access error occurs
         * @see #execute
         */
        public java.sql.ResultSet getResultSet() throws java.sql.SQLException {
                return wrappedStatement.getResultSet();
        }
        /**
         * JDBC 2.0
         *
         * Retrieves the result set concurrency.
         */
        public int getResultSetConcurrency() throws java.sql.SQLException {
                return wrappedStatement.getResultSetConcurrency();
        }
        /**
         * JDBC 2.0
         *
         * Determine the result set type.
         */
        public int getResultSetType() throws java.sql.SQLException {
                return wrappedStatement.getResultSetType();
        }
        /**
         *  Returns the current result as an update count;
         *  if the result is a ResultSet or there are no more results, -1
         *  is returned.
         *  This method should be called only once per result.
         *
         * @return the current result as an update count; -1 if it is a
         * ResultSet or there are no more results
         * @exception SQLException if a database access error occurs
         * @see #execute
         */
        public int getUpdateCount() throws java.sql.SQLException {
                return wrappedStatement.getUpdateCount();
        }
        /**
         * Retrieves the first warning reported by calls on this Statement.
         * Subsequent Statement warnings will be chained to this
         * SQLWarning.
         *
         * <p>The warning chain is automatically cleared each time
         * a statement is (re)executed.
         *
         * <P><B>Note:</B> If you are processing a ResultSet, any
         * warnings associated with ResultSet reads will be chained on the
         * ResultSet object.
         *
         * @return the first SQLWarning or null
         * @exception SQLException if a database access error occurs
         */
        public java.sql.SQLWarning getWarnings() throws java.sql.SQLException {
                return wrappedStatement.getWarnings();
        }
        /**
         * JDBC 2.0
         *
         * Sets an Array parameter.
         *
         * @param i the first parameter is 1, the second is 2, ...
         * @param x an object representing an SQL array
         * @exception SQLException if a database access error occurs
         */
        public void setArray(int i, java.sql.Array x)
                throws java.sql.SQLException {

                wrappedStatement.setArray(i, x);
                saveQueryParamValue(i, x);

        }
        /**
         * Sets the designated parameter to the given input stream, which will have
         * the specified number of bytes.
         * When a very large ASCII value is input to a LONGVARCHAR
         * parameter, it may be more practical to send it via a
         * java.io.InputStream. JDBC will read the data from the stream
         * as needed, until it reaches end-of-file.  The JDBC driver will
         * do any necessary conversion from ASCII to the database char format.
         *
         * <P><B>Note:</B> This stream object can either be a standard
         * Java stream object or your own subclass that implements the
         * standard interface.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the Java input stream that contains the ASCII parameter value
         * @param length the number of bytes in the stream
         * @exception SQLException if a database access error occurs
         */
        public void setAsciiStream(
                int parameterIndex,
                java.io.InputStream x,
                int length)
                throws java.sql.SQLException {

                wrappedStatement.setAsciiStream(parameterIndex, x, length);
                saveQueryParamValue(parameterIndex, x);
        }
        /**
         * Sets the designated parameter to a java.lang.BigDecimal value.
         * The driver converts this to an SQL NUMERIC value when
         * it sends it to the database.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @exception SQLException if a database access error occurs
         */
        public void setBigDecimal(int parameterIndex, java.math.BigDecimal x)
                throws java.sql.SQLException {
                wrappedStatement.setBigDecimal(parameterIndex, x);
                saveQueryParamValue(parameterIndex, x);

        }
        /**
         * Sets the designated parameter to the given input stream, which will have
         * the specified number of bytes.
         * When a very large binary value is input to a LONGVARBINARY
         * parameter, it may be more practical to send it via a
         * java.io.InputStream. JDBC will read the data from the stream
         * as needed, until it reaches end-of-file.
         *
         * <P><B>Note:</B> This stream object can either be a standard
         * Java stream object or your own subclass that implements the
         * standard interface.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the java input stream which contains the binary parameter value
         * @param length the number of bytes in the stream
         * @exception SQLException if a database access error occurs
         */
        public void setBinaryStream(
                int parameterIndex,
                java.io.InputStream x,
                int length)
                throws java.sql.SQLException {
                wrappedStatement.setBinaryStream(parameterIndex, x, length);
                saveQueryParamValue(parameterIndex, x);

        }
        /**
         * JDBC 2.0
         *
         * Sets a BLOB parameter.
         *
         * @param i the first parameter is 1, the second is 2, ...
         * @param x an object representing a BLOB
         * @exception SQLException if a database access error occurs
         */
        public void setBlob(int i, java.sql.Blob x) throws java.sql.SQLException {
                wrappedStatement.setBlob(i, x);
                saveQueryParamValue(i, x);
        }
        /**
         * Sets the designated parameter to a Java boolean value.  The driver converts this
         * to an SQL BIT value when it sends it to the database.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @exception SQLException if a database access error occurs
         */
        public void setBoolean(int parameterIndex, boolean x)
                throws java.sql.SQLException {
                wrappedStatement.setBoolean(parameterIndex, x);
                saveQueryParamValue(parameterIndex, new Boolean(x));

        }
        /**
         * Sets the designated parameter to a Java byte value.  The driver converts this
         * to an SQL TINYINT value when it sends it to the database.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @exception SQLException if a database access error occurs
         */
        public void setByte(int parameterIndex, byte x)
                throws java.sql.SQLException {
                wrappedStatement.setByte(parameterIndex, x);
                saveQueryParamValue(parameterIndex, new Integer(x));
        }
        /**
         * Sets the designated parameter to a Java array of bytes.  The driver converts
         * this to an SQL VARBINARY or LONGVARBINARY (depending on the
         * argument's size relative to the driver's limits on VARBINARYs)
         * when it sends it to the database.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @exception SQLException if a database access error occurs
         */
        public void setBytes(int parameterIndex, byte[] x)
                throws java.sql.SQLException {
                wrappedStatement.setBytes(parameterIndex, x);
                saveQueryParamValue(parameterIndex, x);
        }
        /**
         * JDBC 2.0
         *
         * Sets the designated parameter to the given <code>Reader</code>
         * object, which is the given number of characters long.
         * When a very large UNICODE value is input to a LONGVARCHAR
         * parameter, it may be more practical to send it via a
         * java.io.Reader. JDBC will read the data from the stream
         * as needed, until it reaches end-of-file.  The JDBC driver will
         * do any necessary conversion from UNICODE to the database char format.
         *
         * <P><B>Note:</B> This stream object can either be a standard
         * Java stream object or your own subclass that implements the
         * standard interface.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the java reader which contains the UNICODE data
         * @param length the number of characters in the stream
         * @exception SQLException if a database access error occurs
         */
        public void setCharacterStream(
                int parameterIndex,
                java.io.Reader reader,
                int length)
                throws java.sql.SQLException {
                wrappedStatement.setCharacterStream(parameterIndex, reader, length);
                saveQueryParamValue(parameterIndex, reader);

        }
        /**
         * JDBC 2.0
         *
         * Sets a CLOB parameter.
         *
         * @param i the first parameter is 1, the second is 2, ...
         * @param x an object representing a CLOB
         * @exception SQLException if a database access error occurs
         */
        public void setClob(int i, java.sql.Clob x) throws java.sql.SQLException {
                wrappedStatement.setClob(i, x);
                saveQueryParamValue(i, x);

        }
        /**
         * Defines the SQL cursor name that will be used by
         * subsequent Statement <code>execute</code> methods. This name can then be
         * used in SQL positioned update/delete statements to identify the
         * current row in the ResultSet generated by this statement.  If
         * the database doesn't support positioned update/delete, this
         * method is a noop.  To insure that a cursor has the proper isolation
         * level to support updates, the cursor's SELECT statement should be
         * of the form 'select for update ...'. If the 'for update' phrase is
         * omitted, positioned updates may fail.
         *
         * <P><B>Note:</B> By definition, positioned update/delete
         * execution must be done by a different Statement than the one
         * which generated the ResultSet being used for positioning. Also,
         * cursor names must be unique within a connection.
         *
         * @param name the new cursor name, which must be unique within
         *             a connection
         * @exception SQLException if a database access error occurs
         */
        public void setCursorName(String name) throws java.sql.SQLException {
                wrappedStatement.setCursorName(name);

        }
        /**
         * Sets the designated parameter to a java.sql.Date value.  The driver converts this
         * to an SQL DATE value when it sends it to the database.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @exception SQLException if a database access error occurs
         */
        public void setDate(int parameterIndex, java.sql.Date x)
                throws java.sql.SQLException {

                wrappedStatement.setDate(parameterIndex, x);
                saveQueryParamValue(parameterIndex, x);
        }
        /**
         * JDBC 2.0
         *
         * Sets the designated parameter to a java.sql.Date value,
         * using the given <code>Calendar</code> object.  The driver uses
         * the <code>Calendar</code> object to construct an SQL DATE,
         * which the driver then sends to the database.  With a
         * a <code>Calendar</code> object, the driver can calculate the date
         * taking into account a custom timezone and locale.  If no
         * <code>Calendar</code> object is specified, the driver uses the default
         * timezone and locale.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @param cal the <code>Calendar</code> object the driver will use
         *            to construct the date
         * @exception SQLException if a database access error occurs
         */
        public void setDate(
                int parameterIndex,
                java.sql.Date x,
                java.util.Calendar cal)
                throws java.sql.SQLException {
                wrappedStatement.setDate(parameterIndex, x, cal);
                saveQueryParamValue(parameterIndex, x);
        }
        /**
         * Sets the designated parameter to a Java double value.  The driver converts this
         * to an SQL DOUBLE value when it sends it to the database.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @exception SQLException if a database access error occurs
         */
        public void setDouble(int parameterIndex, double x)
                throws java.sql.SQLException {
                wrappedStatement.setDouble(parameterIndex, x);
                saveQueryParamValue(parameterIndex, new Double(x));
        }
        /**
         * Sets escape processing on or off.
         * If escape scanning is on (the default), the driver will do
         * escape substitution before sending the SQL to the database.
         *
         * Note: Since prepared statements have usually been parsed prior
         * to making this call, disabling escape processing for prepared
         * statements will have no effect.
         *
         * @param enable true to enable; false to disable
         * @exception SQLException if a database access error occurs
         */
        public void setEscapeProcessing(boolean enable)
                throws java.sql.SQLException {
                wrappedStatement.setEscapeProcessing(enable);

        }
        /**
         * JDBC 2.0
         *
         * Gives the driver a hint as to the direction in which
         * the rows in a result set
         * will be processed. The hint applies only to result sets created
         * using this Statement object.  The default value is
         * ResultSet.FETCH_FORWARD.
         * <p>Note that this method sets the default fetch direction for
         * result sets generated by this <code>Statement</code> object.
         * Each result set has its own methods for getting and setting
         * its own fetch direction.
         * @param direction the initial direction for processing rows
         * @exception SQLException if a database access error occurs
         * or the given direction
         * is not one of ResultSet.FETCH_FORWARD, ResultSet.FETCH_REVERSE, or
         * ResultSet.FETCH_UNKNOWN
         */
        public void setFetchDirection(int direction) throws java.sql.SQLException {
                wrappedStatement.setFetchDirection(direction);
        }
        /**
         * JDBC 2.0
         *
         * Gives the JDBC driver a hint as to the number of rows that should
         * be fetched from the database when more rows are needed.  The number
         * of rows specified affects only result sets created using this
         * statement. If the value specified is zero, then the hint is ignored.
         * The default value is zero.
         *
         * @param rows the number of rows to fetch
         * @exception SQLException if a database access error occurs, or the
         * condition 0 <= rows <= this.getMaxRows() is not satisfied.
         */
        public void setFetchSize(int rows) throws java.sql.SQLException {
                wrappedStatement.setFetchSize(rows);
        }
        /**
         * Sets the designated parameter to a Java float value.  The driver converts this
         * to an SQL FLOAT value when it sends it to the database.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @exception SQLException if a database access error occurs
         */
        public void setFloat(int parameterIndex, float x)
                throws java.sql.SQLException {
                wrappedStatement.setFloat(parameterIndex, x);
                saveQueryParamValue(parameterIndex, new Float(x));

        }
        /**
         * Sets the designated parameter to a Java int value.  The driver converts this
         * to an SQL INTEGER value when it sends it to the database.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @exception SQLException if a database access error occurs
         */
        public void setInt(int parameterIndex, int x)
                throws java.sql.SQLException {
                wrappedStatement.setInt(parameterIndex, x);
                saveQueryParamValue(parameterIndex, new Integer(x));
        }
        /**
         * Sets the designated parameter to a Java long value.  The driver converts this
         * to an SQL BIGINT value when it sends it to the database.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @exception SQLException if a database access error occurs
         */
        public void setLong(int parameterIndex, long x)
                throws java.sql.SQLException {
                wrappedStatement.setLong(parameterIndex, x);
                saveQueryParamValue(parameterIndex, new Long(x));

        }
        /**
         * Sets the limit for the maximum number of bytes in a column to
         * the given number of bytes.  This is the maximum number of bytes
         * that can be returned for any column value.  This limit applies
         * only to BINARY, VARBINARY, LONGVARBINARY, CHAR, VARCHAR, and
         * LONGVARCHAR fields.  If the limit is exceeded, the excess data
         * is silently discarded. For maximum portability, use values
         * greater than 256.
         *
         * @param max the new max column size limit; zero means unlimited
         * @exception SQLException if a database access error occurs
         */
        public void setMaxFieldSize(int max) throws java.sql.SQLException {
                wrappedStatement.setMaxFieldSize(max);

        }
        /**
         * Sets the limit for the maximum number of rows that any
         * ResultSet can contain to the given number.
         * If the limit is exceeded, the excess
         * rows are silently dropped.
         *
         * @param max the new max rows limit; zero means unlimited
         * @exception SQLException if a database access error occurs
         */
        public void setMaxRows(int max) throws java.sql.SQLException {
                wrappedStatement.setMaxRows(max);
        }
        /**
         * Sets the designated parameter to SQL NULL.
         *
         * <P><B>Note:</B> You must specify the parameter's SQL type.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param sqlType the SQL type code defined in java.sql.Types
         * @exception SQLException if a database access error occurs
         */
        public void setNull(int parameterIndex, int sqlType)
                throws java.sql.SQLException {
                wrappedStatement.setNull(parameterIndex, sqlType);
                saveQueryParamValue(parameterIndex, null);
        }
        /**
         * JDBC 2.0
         *
         * Sets the designated parameter to SQL NULL.  This version of setNull should
         * be used for user-named types and REF type parameters.  Examples
         * of user-named types include: STRUCT, DISTINCT, JAVA_OBJECT, and
         * named array types.
         *
         * <P><B>Note:</B> To be portable, applications must give the
         * SQL type code and the fully-qualified SQL type name when specifying
         * a NULL user-defined or REF parameter.  In the case of a user-named type
         * the name is the type name of the parameter itself.  For a REF
         * parameter the name is the type name of the referenced type.  If
         * a JDBC driver does not need the type code or type name information,
         * it may ignore it.
         *
         * Although it is intended for user-named and Ref parameters,
         * this method may be used to set a null parameter of any JDBC type.
         * If the parameter does not have a user-named or REF type, the given
         * typeName is ignored.
         *
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param sqlType a value from java.sql.Types
         * @param typeName the fully-qualified name of an SQL user-named type,
         *  ignored if the parameter is not a user-named type or REF
         * @exception SQLException if a database access error occurs
         */
        public void setNull(int paramIndex, int sqlType, String typeName)
                throws java.sql.SQLException {
                wrappedStatement.setNull(paramIndex, sqlType, typeName);
                saveQueryParamValue(paramIndex, null);

        }
        /**
         * <p>Sets the value of a parameter using an object; use the
         * java.lang equivalent objects for integral values.
         *
         * <p>The JDBC specification specifies a standard mapping from
         * Java Object types to SQL types.  The given argument java object
         * will be converted to the corresponding SQL type before being
         * sent to the database.
         *
         * <p>Note that this method may be used to pass datatabase-
         * specific abstract data types, by using a Driver-specific Java
         * type.
         *
         * If the object is of a class implementing SQLData,
         * the JDBC driver should call its method <code>writeSQL</code> to write it
         * to the SQL data stream.
         * If, on the other hand, the object is of a class implementing
         * Ref, Blob, Clob, Struct,
         * or Array, then the driver should pass it to the database as a value of the
         * corresponding SQL type.
         *
         * This method throws an exception if there is an ambiguity, for example, if the
         * object is of a class implementing more than one of those interfaces.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the object containing the input parameter value
         * @exception SQLException if a database access error occurs
         */
        public void setObject(int parameterIndex, Object x)
                throws java.sql.SQLException {
                wrappedStatement.setObject(parameterIndex, x);
                saveQueryParamValue(parameterIndex, x);
        }
        /**
                 * Sets the value of the designated parameter with the given object.
                 * This method is like setObject above, except that it assumes a scale of zero.
                 *
                 * @param parameterIndex the first parameter is 1, the second is 2, ...
                 * @param x the object containing the input parameter value
                 * @param targetSqlType the SQL type (as defined in java.sql.Types) to be
                 *                      sent to the database
                 * @exception SQLException if a database access error occurs
                 */
        public void setObject(int parameterIndex, Object x, int targetSqlType)
                throws java.sql.SQLException {
                wrappedStatement.setObject(parameterIndex, x, targetSqlType);
                saveQueryParamValue(parameterIndex, x);
        }
        /**
         * <p>Sets the value of a parameter using an object. The second
         * argument must be an object type; for integral values, the
         * java.lang equivalent objects should be used.
         *
         * <p>The given Java object will be converted to the targetSqlType
         * before being sent to the database.
         *
         * If the object has a custom mapping (is of a class implementing SQLData),
         * the JDBC driver should call its method <code>writeSQL</code> to write it
         * to the SQL data stream.
         * If, on the other hand, the object is of a class implementing
         * Ref, Blob, Clob, Struct,
         * or Array, the driver should pass it to the database as a value of the
         * corresponding SQL type.
         *
         * <p>Note that this method may be used to pass datatabase-
         * specific abstract data types.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the object containing the input parameter value
         * @param targetSqlType the SQL type (as defined in java.sql.Types) to be
         * sent to the database. The scale argument may further qualify this type.
         * @param scale for java.sql.Types.DECIMAL or java.sql.Types.NUMERIC types,
         *          this is the number of digits after the decimal point.  For all other
         *          types, this value will be ignored.
         * @exception SQLException if a database access error occurs
         * @see Types
         */
        public void setObject(
                int parameterIndex,
                Object x,
                int targetSqlType,
                int scale)
                throws java.sql.SQLException {

                wrappedStatement.setObject(parameterIndex, x, targetSqlType, scale);
                saveQueryParamValue(parameterIndex, x);
        }
        /**
         * Sets the number of seconds the driver will
         * wait for a Statement to execute to the given number of seconds.
         * If the limit is exceeded, a SQLException is thrown.
         *
         * @param seconds the new query timeout limit in seconds; zero means
         * unlimited
         * @exception SQLException if a database access error occurs
         */
        public void setQueryTimeout(int seconds) throws java.sql.SQLException {
                wrappedStatement.setQueryTimeout(seconds);
        }
        /**
         * JDBC 2.0
         *
         * Sets a REF(&lt;structured-type&gt;) parameter.
         *
         * @param i the first parameter is 1, the second is 2, ...
         * @param x an object representing data of an SQL REF Type
         * @exception SQLException if a database access error occurs
         */
        public void setRef(int i, java.sql.Ref x) throws java.sql.SQLException {
                wrappedStatement.setRef(i, x);
                saveQueryParamValue(i, x);

        }
        /**
         * Sets the designated parameter to a Java short value.  The driver converts this
         * to an SQL SMALLINT value when it sends it to the database.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @exception SQLException if a database access error occurs
         */
        public void setShort(int parameterIndex, short x)
                throws java.sql.SQLException {
                wrappedStatement.setShort(parameterIndex, x);
                saveQueryParamValue(parameterIndex, new Integer(x));
        }
        /**
         * Sets the designated parameter to a Java String value.  The driver converts this
         * to an SQL VARCHAR or LONGVARCHAR value (depending on the argument's
         * size relative to the driver's limits on VARCHARs) when it sends
         * it to the database.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @exception SQLException if a database access error occurs
         */
        public void setString(int parameterIndex, String x)
                throws java.sql.SQLException {

                wrappedStatement.setString(parameterIndex, x);
                saveQueryParamValue(parameterIndex, x);
        }
        /**
         * Sets the designated parameter to a java.sql.Time value.  The driver converts this
         * to an SQL TIME value when it sends it to the database.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @exception SQLException if a database access error occurs
         */
        public void setTime(int parameterIndex, java.sql.Time x)
                throws java.sql.SQLException {
                wrappedStatement.setTime(parameterIndex, x);
                saveQueryParamValue(parameterIndex, x);
        }
        /**
         * JDBC 2.0
         *
         * Sets the designated parameter to a java.sql.Time value,
         * using the given <code>Calendar</code> object.  The driver uses
         * the <code>Calendar</code> object to construct an SQL TIME,
         * which the driver then sends to the database.  With a
         * a <code>Calendar</code> object, the driver can calculate the time
         * taking into account a custom timezone and locale.  If no
         * <code>Calendar</code> object is specified, the driver uses the default
         * timezone and locale.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @param cal the <code>Calendar</code> object the driver will use
         *            to construct the time
         * @exception SQLException if a database access error occurs
         */
        public void setTime(
                int parameterIndex,
                java.sql.Time x,
                java.util.Calendar cal)
                throws java.sql.SQLException {
                wrappedStatement.setTime(parameterIndex, x, cal);
                saveQueryParamValue(parameterIndex, x);

        }
        /**
         * Sets the designated parameter to a java.sql.Timestamp value.  The driver
         * converts this to an SQL TIMESTAMP value when it sends it to the
         * database.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @exception SQLException if a database access error occurs
         */
        public void setTimestamp(int parameterIndex, java.sql.Timestamp x)
                throws java.sql.SQLException {
                wrappedStatement.setTimestamp(parameterIndex, x);
                saveQueryParamValue(parameterIndex, x);
        }
        /**
         * JDBC 2.0
         *
         * Sets the designated parameter to a java.sql.Timestamp value,
         * using the given <code>Calendar</code> object.  The driver uses
         * the <code>Calendar</code> object to construct an SQL TIMESTAMP,
         * which the driver then sends to the database.  With a
         * a <code>Calendar</code> object, the driver can calculate the timestamp
         * taking into account a custom timezone and locale.  If no
         * <code>Calendar</code> object is specified, the driver uses the default
         * timezone and locale.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the parameter value
         * @param cal the <code>Calendar</code> object the driver will use
         *            to construct the timestamp
         * @exception SQLException if a database access error occurs
         */
        public void setTimestamp(
                int parameterIndex,
                java.sql.Timestamp x,
                java.util.Calendar cal)
                throws java.sql.SQLException {
                wrappedStatement.setTimestamp(parameterIndex, x, cal);
                saveQueryParamValue(parameterIndex, x);
        }
        /**
         * Sets the designated parameter to the given input stream, which will have
         * the specified number of bytes.
         * When a very large UNICODE value is input to a LONGVARCHAR
         * parameter, it may be more practical to send it via a
         * java.io.InputStream. JDBC will read the data from the stream
         * as needed, until it reaches end-of-file.  The JDBC driver will
         * do any necessary conversion from UNICODE to the database char format.
         * The byte format of the Unicode stream must be Java UTF-8, as
         * defined in the Java Virtual Machine Specification.
         *
         * <P><B>Note:</B> This stream object can either be a standard
         * Java stream object or your own subclass that implements the
         * standard interface.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @param x the java input stream which contains the
         * UNICODE parameter value
         * @param length the number of bytes in the stream
         * @exception SQLException if a database access error occurs
         * @deprecated
         */
        public void setUnicodeStream(
                int parameterIndex,
                java.io.InputStream x,
                int length)
                throws java.sql.SQLException {
                wrappedStatement.setUnicodeStream(parameterIndex, x, length);
                saveQueryParamValue(parameterIndex, x);
        }

        /**
         * Returns the sql statement string (question marks replaced with set parameter values)
         * that will be (or has been) executed by the {@link java.sql.PreparedStatement PreparedStatement} that this
         * <code>LoggableStatement</code> is a wrapper for.
         * <p>
         * @return java.lang.String the statemant represented by this <code>LoggableStatement</code>
         */
        public String getQueryString() {

                StringBuffer buf = new StringBuffer();
                int qMarkCount = 0;
                ArrayList chunks = new ArrayList();
                StringTokenizer tok = new StringTokenizer(sqlTemplate+" ", "?");
                while (tok.hasMoreTokens()) {
                        String oneChunk = tok.nextToken();
                        buf.append(oneChunk);

                        try {
                                Object value;
                                if (parameterValues.size() > 1 + qMarkCount) {
                                        value = parameterValues.get(1 + qMarkCount++);
                                } else {
                                        if (tok.hasMoreTokens()) {
                                                value = null;
                                        } else {
                                                value = "";
                                        }
                                }
                                buf.append("" + value);
                        } catch (Throwable e) {
                                buf.append(
                                        "ERROR WHEN PRODUCING QUERY STRING FOR LOG."
                                                + e.toString());
                                // catch this without whining, if this fails the only thing wrong is probably this class
                        }
                }
                return buf.toString().trim();
        }

        /**
         * Saves the parameter value <code>obj</code> for the specified <code>position</code> for use in logging output
         *
         * @param position position (starting at 1) of the parameter to save
         * @param obj java.lang.Object the parameter value to save
         */
        private void saveQueryParamValue(int position, Object obj) {
                String strValue;
                if (obj instanceof String || obj instanceof Date) {
                        // if we have a String or Date , include '' in the saved value
                        strValue = "'" + obj + "'";
                } else {

                        if (obj == null) {
                                // convert null to the string null
                                strValue = "null";
                        } else {
                                // unknown object (includes all Numbers), just call toString
                                strValue = obj.toString();
                        }
                }

                // if we are setting a position larger than current size of parameterValues, first make it larger
                while (position >= parameterValues.size()) {
                        parameterValues.add(null);
                }
                // save the parameter
                parameterValues.set(position, strValue);
        }
         
        // JDK1.4.2瑜� �쐞�븳 �씪遺� �븿�닔 援ы쁽
        public ParameterMetaData getParameterMetaData() throws SQLException{return (ParameterMetaData)new Object();}
        public void setURL(int parameterIndex, URL x) throws SQLException{}
        public boolean execute(String sql,  int autoGeneratedKeys) throws SQLException{return false;}
        public boolean execute(String sql,  int[] columnIndexes) throws SQLException{return false;}
        public boolean execute(String sql,  String[] columnNames) throws SQLException{return false;}    
        public int executeUpdate(String sql, String[] columnNames) throws SQLException{return 0;}
        public int executeUpdate(String sql, int[] columnIndexes) throws SQLException{return 0;}        
        public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException{return 0;}
        public ResultSet getGeneratedKeys() throws SQLException{return (ResultSet)new Object();}        
        public boolean getMoreResults(int current) throws SQLException{return false;}
        public int getResultSetHoldability()    throws SQLException{return 0;}

        @Override
        public boolean isClosed() throws SQLException {
                // TODO Auto-generated method stub
                return false;
        }

        @Override
        public void setPoolable(boolean poolable) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public boolean isPoolable() throws SQLException {
                // TODO Auto-generated method stub
                return false;
        }

        @Override
        public void closeOnCompletion() throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public boolean isCloseOnCompletion() throws SQLException {
                // TODO Auto-generated method stub
                return false;
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
                // TODO Auto-generated method stub
                return false;
        }

        @Override
        public void setRowId(int parameterIndex, RowId x) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setNString(int parameterIndex, String value) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setNClob(int parameterIndex, NClob value) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setClob(int parameterIndex, Reader reader) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
                // TODO Auto-generated method stub
                
        }

        @Override
        public void setNClob(int parameterIndex, Reader reader) throws SQLException {
                // TODO Auto-generated method stub
                
        }       
}
