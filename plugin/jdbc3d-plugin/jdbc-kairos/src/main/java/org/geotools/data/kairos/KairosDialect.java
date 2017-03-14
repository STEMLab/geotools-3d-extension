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
package org.geotools.data.kairos;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.geotools.data.jdbc.iso.FilterToSQL;
import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.iso.io.wkt.GeometryToWKTString;
import org.geotools.geometry.iso.io.wkt.WKTReader;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.jdbc.iso.BasicSQLDialect;
import org.geotools.jdbc.ColumnMetadata;
import org.geotools.jdbc.iso.JDBCDataStore;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.util.Version;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.coordinate.GeometryFactory;
import org.opengis.geometry.primitive.Curve;
//import org.opengis.geometry.coordinate.LineString;
//import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Solid;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

//import com.vividsolutions.jts.geom.Envelope;
//import com.vividsolutions.jts.geom.Geometry;
/*import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.io.ParseException;*/
//import com.vividsolutions.jts.io.WKBReader;
//import com.vividsolutions.jts.io.WKBWriter;
//import com.vividsolutions.jts.io.WKTReader;

public class KairosDialect extends BasicSQLDialect {

    static final Version V_5_5_0 = new Version("5.5");

    boolean looseBBOXEnabled = false;

    boolean estimatedExtentsEnabled = false;

    Version version;

    //WKBWriter wkbWriter = new WKBWriter();

    static Integer GEOM_POINT = Integer.valueOf(4001);

    static Integer GEOM_LINESTRING = Integer.valueOf(4002);

    static Integer GEOM_POLYGON = Integer.valueOf(4003);
    
    static Integer GEOM_SOLID = Integer.valueOf(1111);

    static Integer GEOM_MULTIPOINT = Integer.valueOf(4004);

    static Integer GEOM_MULTILINESTRING = Integer.valueOf(4005);

    static Integer GEOM_MULTIPOLYGON = Integer.valueOf(4006);

    static Integer GEOM_GEOMCOLLECTION = Integer.valueOf(4007);

    @SuppressWarnings({ "rawtypes", "serial" })
    final static Map<String, Class> TYPE_TO_CLASS_MAP = new HashMap<String, Class>() {
        {
            put("GEOMETRY", Geometry.class);
            put("POINT", Point.class);
            put("POINTN", Point.class);
            put("POINTZ", Point.class);
            /*put("LINESTRING", LineString.class);
            put("LINESTRINGM", LineString.class);
            put("LINESTRINGZ", LineString.class);*/
            put("LINESTRING", Curve.class);
            //put("LINESTRINGM", Curve.class);
            put("LINESTRINGZ", Curve.class);
            /*put("POLYGON", Polygon.class);
            put("POLYGONM", Polygon.class);
            put("POLYGONZ", Polygon.class);*/
            put("POLYGON", Ring.class);
            //put("POLYGONM", Ring.class);
            put("POLYGONZ", Ring.class);
            put("SOLID", Solid.class);
            put("MULTIPOINT", MultiPoint.class);
            put("MULTIPOINTM", MultiPoint.class);
            put("MULTIPOINTZ", MultiPoint.class);
            /*put("MULTILINESTRING", MultiLineString.class);
            put("MULTILINESTRINGM", MultiLineString.class);
            put("MULTILINESTRINGZ", MultiLineString.class);*/
            put("MULTILINESTRING", MultiCurve.class);
            put("MULTILINESTRINGM", MultiCurve.class);
            put("MULTILINESTRINGZ", MultiCurve.class);
            /*put("MULTIPOLYGON", MultiPolygon.class);
            put("MULTIPOLYGONM", MultiPolygon.class);
            put("MULTIPOLYGONZ", MultiPolygon.class);*/
            put("MULTIPOLYGON", MultiSurface.class);
            put("MULTIPOLYGONM", MultiSurface.class);
            put("MULTIPOLYGONZ", MultiSurface.class);
            /*put("GEOMETRYCOLLECTION", GeometryCollection.class);
            put("GEOMETRYCOLLECTIONM", GeometryCollection.class);
            put("GEOMETRYCOLLECTIONZ", GeometryCollection.class);*/
            put("GEOMETRYCOLLECTION", MultiPrimitive.class);
            put("GEOMETRYCOLLECTIONM", MultiPrimitive.class);
            put("GEOMETRYCOLLECTIONZ", MultiPrimitive.class);
            put("BYTEA", byte[].class);
        }
    };

    @SuppressWarnings({ "rawtypes", "serial" })
    final static Map<Class, String> CLASS_TO_TYPE_MAP = new HashMap<Class, String>() {
        {
            put(Geometry.class, "GEOMETRY");
            put(Point.class, "POINTZ");
            //put(LineString.class, "LINESTRING");
            put(Curve.class, "LINESTRINGZ");
            //put(Polygon.class, "POLYGON");
            put(Ring.class, "POLYGONZ");
            put(Solid.class,"SOLID");
            put(MultiPoint.class, "MULTIPOINT");
            //put(MultiLineString.class, "MULTILINESTRING");
            put(MultiCurve.class, "MULTILINESTRING");
            //put(MultiPolygon.class, "MULTIPOLYGON");
            put(MultiSurface.class, "MULTIPOLYGON");
            //put(GeometryCollection.class, "GEOMCOLLECTION");
            put(MultiPrimitive.class, "GEOMCOLLECTION");
            put(byte[].class, "BYTEA");
        }
    };

    public KairosDialect(JDBCDataStore dataStore) {
        super(dataStore);
    }

    public boolean isLooseBBOXEnabled() {
        return looseBBOXEnabled;
    }

    public void setLooseBBOXEnabled(boolean looseBBOXEnabled) {
        this.looseBBOXEnabled = looseBBOXEnabled;
    }

    public boolean isEstimatedExtentsEnabled() {
        return estimatedExtentsEnabled;
    }

    public void setEstimatedExtentsEnabled(boolean estimatedExtentsEnabled) {
        this.estimatedExtentsEnabled = estimatedExtentsEnabled;
    }

    @Override
    public boolean includeTable(String schemaName, String tableName, Connection cx)
            throws SQLException {
        if (tableName.equalsIgnoreCase("GEOMETRY_COLUMNS")) {
            return false;
        } else if (tableName.equalsIgnoreCase("SPATIAL_REF_SYS")) {
            return false;
        } else if (tableName.equalsIgnoreCase("SYS_PLAN_VIEW")) {
            return false;
        }

        // others?
        return true;
    }

    ThreadLocal<WKBAttributeIO> wkbReader = new ThreadLocal<WKBAttributeIO>();

    @Override
    public Geometry decodeGeometryValue(GeometryDescriptor descriptor, ResultSet rs, String column,
    		ISOGeometryBuilder factory, Connection cx) throws IOException, SQLException {
        WKBAttributeIO reader = getWKBReader(factory);
        return (Geometry) reader.read(rs, column);
    }

    public Geometry decodeGeometryValue(GeometryDescriptor descriptor, ResultSet rs, int column,
    		ISOGeometryBuilder factory, Connection cx) throws IOException, SQLException {
        WKBAttributeIO reader = getWKBReader(factory);
        return (Geometry) reader.read(rs, column);
    }

    WKBAttributeIO getWKBReader(ISOGeometryBuilder factory) {
        WKBAttributeIO reader = wkbReader.get();
        if (reader == null) {
            reader = new WKBAttributeIO(factory);
            wkbReader.set(reader);
        } else {
            reader.setGeometryFactory(factory);
        }
        return reader;
    }

    @Override
    public void encodeGeometryColumn(GeometryDescriptor gatt, String prefix, int srid, Hints hints,
            StringBuffer sql) {
        sql.append(" ST_ASBINARY(");
        encodeColumnName(prefix, gatt.getLocalName(), sql);
        sql.append(")");
    }

    @Override
    public void encodeGeometryEnvelope(String tableName, String geometryColumn, StringBuffer sql) {
        sql.append(" ST_ASTEXT(ST_ENVELOPE(");
        encodeColumnName(null, geometryColumn, sql);
        sql.append("))");
    }
    
   
    
    @Override
    public void encodeColumnName(String prefix, String raw, StringBuffer sql) {
        if (prefix != null) {
            sql.append(ne()).append(prefix).append(ne()).append(".");
        }
        sql.append(ne()).append(raw).append(ne());
    }

    @Override
    public List<ReferencedEnvelope> getOptimizedBounds(String schema,
            SimpleFeatureType featureType, Connection cx) throws SQLException, IOException {
        if (!estimatedExtentsEnabled)
            return null;

        String tableName = featureType.getTypeName();

        Statement st = null;
        ResultSet rs = null;

        List<ReferencedEnvelope> result = new ArrayList<ReferencedEnvelope>();
        Savepoint savePoint = null;
        try {
            st = cx.createStatement();
            if (!cx.getAutoCommit()) {
                savePoint = cx.setSavepoint();
            }

            GeometryDescriptor att = featureType.getGeometryDescriptor();
            String geometryField = att.getName().getLocalPart();

            // ==================Kairos======================
            // SELECT ST_ASBINARY(ST_EXTENT(geom)) from fishnet
            // ================================================

            // use estimated extent (optimizer statistics)
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT ST_ASBINARY(ST_EXTENT(\"");
            sql.append(geometryField).append("\"))");
            sql.append(" FROM \"");
            sql.append(tableName);
            sql.append("\"");

            rs = st.executeQuery(sql.toString());

            if (rs.next()) {
                //byte[] bytes = rs.getBytes(1);
            	String text = rs.getString(1);
                if (text != null) {
                    try {
                    	CoordinateReferenceSystem crs = att.getCoordinateReferenceSystem();

                        Geometry extGeom = new WKTReader(crs).read(text);
                        
                        // reproject and merge
                        result.add(new ReferencedEnvelope(extGeom.getEnvelope()));
                    } /*catch (ParseException e) {
                        String msg = "Error decoding wkb";
                        throw (IOException) new IOException(msg).initCause(e);
                    }*/ catch (org.geotools.geometry.iso.io.wkt.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
        } catch (SQLException e) {
            if (savePoint != null) {
                cx.rollback(savePoint);
            }
            LOGGER.log(Level.WARNING,
                    "Failed to use ST_Estimated_Extent, falling back on envelope aggregation", e);
            return null;
        } finally {
            if (savePoint != null) {
                cx.releaseSavepoint(savePoint);
            }
            dataStore.closeSafe(rs);
            dataStore.closeSafe(st);
        }
        return result;
    }

    @Override
    public Envelope decodeGeometryEnvelope(ResultSet rs, int column, Connection cx)
            throws SQLException, IOException {
    	Hints hints = GeoTools.getDefaultHints();
        hints.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D);
        hints.put(Hints.GEOMETRY_VALIDATE, false);
        try {
            String envelope = rs.getString(column);
            if (envelope != null) {
                return new WKTReader(hints).read(envelope).getEnvelope();
            } else {
                //return new Envelope();
            	return null;
            }
        } /*catch (ParseException e) {
            throw (IOException) new IOException("Error occurred parsing the bounds WKT")
                    .initCause(e);
        }*/ catch (org.geotools.geometry.iso.io.wkt.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getMapping(ResultSet columnMetaData, Connection cx) throws SQLException {
        String typeName = columnMetaData.getString("TYPE_NAME");

        Class<?> geometryClass = (Class) TYPE_TO_CLASS_MAP.get(typeName.toUpperCase());
        return geometryClass;
    }

    //@Override
    public void handleUserDefinedType(ResultSet columnMetaData, ColumnMetadata metadata,
            Connection cx) throws SQLException {
    	
        String tableName = columnMetaData.getString("TABLE_NAME");
        String columnName = columnMetaData.getString("COLUMN_NAME");
        String schemaName = columnMetaData.getString("TABLE_SCHEM");

        String sql = "SELECT udt_name FROM information_schema.columns " + " WHERE table_schema = '"
                + schemaName + "' " + " AND table_name = '" + tableName + "' "
                + " AND column_name = '" + columnName + "' ";
        LOGGER.fine(sql);

        Statement st = cx.createStatement();
        try {
            ResultSet rs = st.executeQuery(sql);
            try {
                if (rs.next()) {
                    metadata.setTypeName(rs.getString(1));
                }
            } finally {
                dataStore.closeSafe(rs);
            }
        } finally {
            dataStore.closeSafe(st);
        }
    }

    @Override
    public Integer getGeometrySRID(String schemaName, String tableName, String columnName,
            Connection cx) throws SQLException {
        // first attempt, try with the geometry metadata
        Statement statement = null;
        ResultSet result = null;
        Integer srid = null;
        try {
            // try geometry_columns
            try {
                /*String sqlStatement = "SELECT SRID FROM GEOMETRY_COLUMNS WHERE " //
                        + "F_TABLE_SCHEMA = '" + schemaName + "' " //
                        + "AND F_TABLE_NAME = '" + tableName + "' " //
                        + "AND F_GEOMETRY_COLUMN = '" + columnName + "'";*/
            	String sqlStatement = "SELECT SRID FROM geomSRID WHERE " //
                        + "TABLENAME = '" + tableName + "' " //
                        + "AND COLUMNNAME = '" + columnName + "'";
                LOGGER.log(Level.FINE, "Geometry srid check; {0} ", sqlStatement);
                statement = cx.createStatement();
                result = statement.executeQuery(sqlStatement);
                
                if (result.next()) {
                    srid = result.getInt(1);
                	
                }
         
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to retrieve information about " + schemaName
                        + "." + tableName + "." + columnName
                        + " from the geometry_columns table, checking the first geometry instead",
                        e);
            } finally {
                dataStore.closeSafe(result);
            }

        } finally {
            dataStore.closeSafe(result);
            dataStore.closeSafe(statement);
        }

        return srid;
    }

    @Override
    public String getSequenceForColumn(String schemaName, String tableName, String columnName,
            Connection cx) throws SQLException {

        if (columnName.toUpperCase().contains("GEOM") || columnName.toUpperCase().contains("SHAPE")) {
            // Kairos special
            return null;
        }

        Statement st = cx.createStatement();
        try {
            String seqName = "seq_" + tableName + "_" + columnName;
            String sql = "SELECT seqname from syssequence WHERE seqname = '" + seqName + "'";

            dataStore.getLogger().fine(sql);
            ResultSet rs = st.executeQuery(sql);
            try {
                if (rs.next()) {
                    return rs.getString(1);
                }
            } finally {
                dataStore.closeSafe(rs);
            }
        } finally {
            dataStore.closeSafe(st);
        }

        return null;
    }

    @Override
    public Object getNextSequenceValue(String schemaName, String sequenceName, Connection cx)
            throws SQLException {
        Statement st = cx.createStatement();
        try {
            // SELECT seq_building_fid.NEXTVAL FROM DUAL;
            String sql = "SELECT \"" + sequenceName + "\".NEXTVAL FROM DUAL";

            dataStore.getLogger().fine(sql);
            ResultSet rs = st.executeQuery(sql);
            try {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    LOGGER.log(Level.WARNING, "Failed to retrieve sequence from " + sequenceName);
                }
            } finally {
                dataStore.closeSafe(rs);
            }
        } finally {
            dataStore.closeSafe(st);
        }

        return 0;
    }

    @Override
    public boolean lookupGeneratedValuesPostInsert() {
        return true;
    }

    @Override
    public Object getLastAutoGeneratedValue(String schemaName, String tableName, String columnName,
            Connection cx) throws SQLException {

        Statement st = cx.createStatement();
        try {
            String sql = "SELECT lastval()";
            dataStore.getLogger().fine(sql);

            ResultSet rs = st.executeQuery(sql);
            try {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            } finally {
                dataStore.closeSafe(rs);
            }
        } finally {
            dataStore.closeSafe(st);
        }

        return null;
    }

    @Override
    public void registerClassToSqlMappings(Map<Class<?>, Integer> mappings) {
        super.registerClassToSqlMappings(mappings);

        // jdbc metadata for geom columns reports DATA_TYPE=1111=Types.OTHER
        mappings.put(Geometry.class, Types.OTHER);

        // Geometry Type for Kairos
        mappings.put(Point.class, GEOM_POINT);
        //mappings.put(LineString.class, GEOM_LINESTRING);
        mappings.put(Curve.class, GEOM_LINESTRING);
        //mappings.put(Polygon.class, GEOM_POLYGON);
        mappings.put(Ring.class, GEOM_POLYGON);
        mappings.put(Solid.class, GEOM_SOLID);
        mappings.put(MultiPoint.class, GEOM_MULTIPOINT);
        //mappings.put(MultiLineString.class, GEOM_MULTILINESTRING);
        mappings.put(MultiCurve.class, GEOM_MULTILINESTRING);
        //mappings.put(MultiPolygon.class, GEOM_MULTIPOLYGON);
        mappings.put(MultiSurface.class, GEOM_MULTIPOLYGON);
        //mappings.put(GeometryCollection.class, GEOM_GEOMCOLLECTION);
        mappings.put(MultiPrimitive.class, GEOM_GEOMCOLLECTION);
    }

    @Override
    public void registerSqlTypeNameToClassMappings(Map<String, Class<?>> mappings) {
        super.registerSqlTypeNameToClassMappings(mappings);

        mappings.put("GEOMETRY", Geometry.class);
        mappings.put("TEXT", String.class);
        mappings.put("POINT", Point.class);
        mappings.put("POINTM", Point.class);
        mappings.put("POINTZ", Point.class);
        //mappings.put("LINESTRING", LineString.class);
        //mappings.put("LINESTRINGM", LineString.class);
        //mappings.put("LINESTRINGM", LineString.class);
        mappings.put("LINESTRING", Curve.class);
        mappings.put("LINESTRINGM", Curve.class);
        mappings.put("LINESTRINGM", Curve.class);
        //mappings.put("POLYGON", Polygon.class);
        //mappings.put("POLYGONM", Polygon.class);
        //mappings.put("POLYGONZ", Polygon.class);
        mappings.put("POLYGON", Ring.class);
        mappings.put("POLYGONM", Ring.class);
        mappings.put("POLYGONZ", Ring.class);
        mappings.put("SOLID", Solid.class);
        mappings.put("MULTIPOINT", MultiPoint.class);
        mappings.put("MULTIPOINTM", MultiPoint.class);
        mappings.put("MULTIPOINTZ", MultiPoint.class);
        //mappings.put("MULTILINESTRING", MultiLineString.class);
        //mappings.put("MULTILINESTRINGM", MultiLineString.class);
        //mappings.put("MULTILINESTRINGZ", MultiLineString.class);
        mappings.put("MULTILINESTRING", MultiCurve.class);
        mappings.put("MULTILINESTRINGM", MultiCurve.class);
        mappings.put("MULTILINESTRINGZ", MultiCurve.class);
        //mappings.put("MULTIPOLYGON", MultiPolygon.class);
        //mappings.put("MULTIPOLYGONM", MultiPolygon.class);
        //mappings.put("MULTIPOLYGONZ", MultiPolygon.class);
        mappings.put("MULTIPOLYGON", MultiSurface.class);
        mappings.put("MULTIPOLYGONM", MultiSurface.class);
        mappings.put("MULTIPOLYGONZ", MultiSurface.class);
        //mappings.put("GEOMETRYCOLLECTION", GeometryCollection.class);
        //mappings.put("GEOMETRYCOLLECTIONM", GeometryCollection.class);
        //mappings.put("GEOMETRYCOLLECTIONZ", GeometryCollection.class);
        mappings.put("GEOMETRYCOLLECTION", MultiPrimitive.class);
        mappings.put("GEOMETRYCOLLECTIONM", MultiPrimitive.class);
        mappings.put("GEOMETRYCOLLECTIONZ", MultiPrimitive.class);
        mappings.put("BYTEA", byte[].class);
    }

    @Override
    public void registerSqlTypeToSqlTypeNameOverrides(Map<Integer, String> overrides) {
        overrides.put(new Integer(Types.VARCHAR), "VARCHAR");
        overrides.put(new Integer(Types.BOOLEAN), "BOOL");
        overrides.put(new Integer(Types.SMALLINT), "INTEGER");
        overrides.put(new Integer(Types.INTEGER), "INTEGER");
        overrides.put(new Integer(Types.REAL), "REAL");
        overrides.put(new Integer(Types.FLOAT), "DOUBLE");
        overrides.put(new Integer(Types.DOUBLE), "DOUBLE");
        overrides.put(new Integer(Types.DECIMAL), "NUMBER");
        overrides.put(new Integer(Types.NUMERIC), "NUMBER");
    }

    @Override
    public String getGeometryTypeName(Integer type) {
        switch (type) {
        case 1111: 
        	return "ST_SOLID";
        case 4001:
            return "ST_POINTZ";
        case 4002:
            return "ST_LINESTRINGZ";
        case 4003:
            return "ST_POLYGONZ";
        case 4004:
            return "ST_MULTIPOINT";
        case 4005:
            return "ST_MULTILINESTRING";
        case 4006:
            return "ST_MULTIPOLYGON";
        case 4007:
            return "ST_GEOMCOLLECTION";
        }

        return "ST_GEOMETRY";
    }

    @Override
    public void encodePrimaryKey(String column, StringBuffer sql) {
        encodeColumnName(null, column, sql);
        sql.append(" INTEGER PRIMARY KEY");
    }

    /**
     * Creates GEOMETRY_COLUMN registrations and spatial indexes for all geometry columns
     */
    @Override
    public void postCreateTable(String schemaName, SimpleFeatureType featureType, Connection cx)
            throws SQLException {
        String tableName = featureType.getName().getLocalPart();

        Statement st = null;
        try {
            st = cx.createStatement();

            // register all geometry columns in the database
            for (AttributeDescriptor att : featureType.getAttributeDescriptors()) {
                if (att instanceof GeometryDescriptor) {
                    GeometryDescriptor gd = (GeometryDescriptor) att;

                    // lookup or reverse engineer the srid
                    int srid = -1;

                    if (gd.getUserData().get(JDBCDataStore.JDBC_NATIVE_SRID) != null) {
                        srid = (Integer) gd.getUserData().get(JDBCDataStore.JDBC_NATIVE_SRID);
                    } else if (gd.getCoordinateReferenceSystem() != null) {
                        try {
                            Integer result = CRS.lookupEpsgCode(gd.getCoordinateReferenceSystem(),
                                    true);
                            if (result != null) {
                                srid = result;
                            }
                        } catch (Exception e) {
                            LOGGER.log(Level.FINE, "Error looking up the "
                                    + "epsg code for metadata " + "insertion, assuming -1", e);
                        }
                    }

                    // assume 2 dimensions, but ease future customisation
                    int dimensions = 2;

                    // grab the geometry type
                    String geomType = CLASS_TO_TYPE_MAP.get(gd.getType().getBinding());
                    if (geomType == null) {
                        geomType = "GEOMETRY";
                    }

                    // register the geometry type, first remove and eventual
                    // leftover, then write out the real one
                    String sql = null;
                    if (getVersion(cx).compareTo(V_5_5_0) >= 0) {
                        // postgis 2 and up we don't muck with geometry_columns, we just alter the
                        // type directly to set the geometry type and srid
                        //setup the geometry type
                        if(dimensions == 3) {
                            geomType = geomType + "Z";
                        } else if(dimensions == 4) {
                            geomType = geomType + "ZM";
                        } else if(dimensions > 4){
                            throw new IllegalArgumentException("Kairos only supports geometries with 2, 3 and 4 dimensions, current value: " + dimensions);
                        }
                        sql = 
                        		"CREATE TABLE geomSRID(" + 
                                        "TABLENAME varchar(30), " + 
                                        "SRID INT, " +
                                        "COLUMNNAME varchar(30));" ;
                 
                        /*sql = 
                    		"ALTER TABLE \"" + tableName + "\" " + 
                                    "ALTER COLUMN \"" + gd.getLocalName() + "\" " + 
                                    "TYPE geometry (" + geomType + ", " + srid + ");";*/
                            /*"ALTER TABLE \"" + schemaName + "\".\"" + tableName + "\" " + 
                             "ALTER COLUMN \"" + gd.getLocalName() + "\" " + 
                             "TYPE geometry (" + geomType + ", " + srid + ");";*/
                        
                        LOGGER.fine( sql );
                        try{
                        st.execute( sql );
                        }catch(Exception e){
                        	
                        }finally{
                        	sql = 
                            		"INSERT INTO geomSRID(TABLENAME, SRID, COLUMNNAME)"
                            		+ "VALUES('"+tableName+"',"+srid+",'"+gd.getLocalName()+"');";
                        	st.execute( sql );
                        }
                    }
                    else{
                    	System.out.println("not support this version");
                    	/*String sql = "DELETE FROM GEOMETRY_COLUMNS" + " WHERE f_table_catalog =''" //
                                // + " AND f_table_schema = '" + schemaName + "'" //
                                 + " AND F_TABLE_NAME = '" + tableName + "'" //
                                 + " AND F_GEOMETRY_COLUMN = '" + gd.getLocalName() + "'";

                         System.out.println(sql);
                         
                         LOGGER.fine(sql);
                         st.execute(sql);

                         sql = "INSERT INTO GEOMETRY_COLUMNS VALUES (''," //
                                // + "'" + schemaName + "'," //
                                 + "'" + tableName + "'," //
                                 + "'" + gd.getLocalName() + "'," //
                                 + dimensions + "," //
                                 + srid + "," //
                                 + "'" + geomType + "')";
                         LOGGER.fine(sql);
                         st.execute(sql);*/
                    }
                    

                    // add the spatial index
                    // Kairos: CREATE [UNIQUE] [RSTREE] INDEX IndexName ON TableName (GeoColName)
                    // ex) CREATE RSTREE INDEX idx_fishnet_geom ON fishnet(geom);
                    sql = "CREATE RSTREE INDEX \"spatial_" + tableName //
                            + "_" + gd.getLocalName() + "\"" //
                            + " ON " //
                            + "\"" + tableName + "\"" //
                            + " (" //
                            + "\"" + gd.getLocalName() + "\")";
                    LOGGER.fine(sql);
                    //st.execute(sql);

                    // create sequence
                    String sequenceName = getSequenceForColumn(schemaName, tableName, "fid", cx);
                    if (sequenceName != null) {
                        sql = "DROP SEQUENCE \"" + sequenceName + "\"";
                        try {
                            //st.execute(sql);
                        } catch (Exception e) {
                            LOGGER.fine(e.getMessage());
                        }

                        // CREATE SEQUENCE seq_building_fid START WITH 1 INCREMENT BY 1 MINVALUE 1
                        // NOMAXVALUE
                        sql = "CREATE SEQUENCE \"" + sequenceName
                                + "\" START WITH 1 INCREMENT BY 1 MINVALUE 1 NOMAXVALUE";
                        LOGGER.fine(sql);
                        //st.execute(sql);
                    }
                }
            }
            cx.commit();
        } finally {
            dataStore.closeSafe(st);
        }
    }

    @Override
    public void encodeGeometryValue(Geometry value, int dimension, int srid, StringBuffer sql)
            throws IOException {
        if (value == null) {
            sql.append("NULL");
        } else {
            /*if (value instanceof com.vividsolutions.jts.geom.LinearRing) {
                // WKT does not support linear rings
                value = value.getFactory().createLineString(
                        ((com.vividsolutions.jts.geom.LinearRing) value).getCoordinateSequence());
            }*/
            // KAIROS ERROR: ERROR(43003) WKT string is too long. Max length is 4KB
            sql.append("ST_GeomFromText('" + new GeometryToKairosWKTString(false).getString(value) + "', " + srid + ")");
        }
    }

    @Override
    public FilterToSQL createFilterToSQL() {
        KairosFilterToSQL sql = new KairosFilterToSQL(this);
        sql.setLooseBBOXEnabled(looseBBOXEnabled);
        return sql;
    }

    @Override
    public boolean isLimitOffsetSupported() {
        return true;
    }

    @Override
    public void applyLimitOffset(StringBuffer sql, int limit, int offset) {
        if (limit >= 0 && limit < Integer.MAX_VALUE) {
            sql.append(" LIMIT " + limit);
            if (offset > 0) {
                sql.append(" OFFSET " + offset);
            }
        } else if (offset > 0) {
            sql.append(" OFFSET " + offset);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void encodeValue(Object value, Class type, StringBuffer sql) {
        if (byte[].class.equals(type)) {
            // escape the into bytea representation
            StringBuffer sb = new StringBuffer();
            byte[] input = (byte[]) value;
            for (int i = 0; i < input.length; i++) {
                byte b = input[i];
                if (b == 0) {
                    sb.append("\\\\000");
                } else if (b == 39) {
                    sb.append("\\'");
                } else if (b == 92) {
                    sb.append("\\\\134'");
                } else if (b < 31 || b >= 127) {
                    sb.append("\\\\");
                    String octal = Integer.toOctalString(b);
                    if (octal.length() == 1) {
                        sb.append("00");
                    } else if (octal.length() == 2) {
                        sb.append("0");
                    }
                    sb.append(octal);
                } else {
                    sb.append((char) b);
                }
            }
            super.encodeValue(sb.toString(), String.class, sql);
        } else {
            super.encodeValue(value, type, sql);
        }
    }

    @Override
    public int getDefaultVarcharSize() {
        return 255;
    }

    /**
     * Returns the Kairos version
     * 
     * @return
     */
    public Version getVersion(Connection conn) throws SQLException {
        if (version == null) {
        	version = new Version(conn.getMetaData().getDatabaseProductVersion());
        	if(version == null){
        		 version = new Version("V_6_0_0");
        	}
           
        }
        return version;
    }

    /**
     * Returns true if the Kairos version is >= x.x
     */
    boolean supportsGeography(Connection cx) throws SQLException {
        return false; // getVersion(cx).compareTo(V_5_0_0) >= 0;
    }

	/*@Override
	public void encodeGeometryValue(Solid value, int dimension, int srid, StringBuffer sql) throws IOException {
		// TODO Auto-generated method stub
		
	}*/

}