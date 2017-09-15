/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2008-2015, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.iso.data.geojson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import org.geotools.data.FeatureWriter;
import org.geotools.data.ISODataUtilities;
import org.geotools.data.Query;
import org.geotools.data3d.store.ContentEntry;
import org.geotools.data3d.store.ContentFeatureStore;
import org.geotools.data3d.store.ContentState;
import org.geotools.factory.Hints;
import org.geotools.feature.ISOFeatureFactoryImpl;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Inserts features in the database. Buffers the insertions until BUFFER_SIZE is reached or
 * the writer is closed.
 *
 * @source $URL$
 */
public class GeoJSONInsertFeatureWriter implements FeatureWriter<SimpleFeatureType, SimpleFeature> {
    /**
     * Grouping elements together in order to have a decent batch size.
     */
	private ContentState state;

    private File temp;

    private GeoJSONWriter geoJSONWriter;

    private GeoJSONFeatureReader delegate;

    private SimpleFeature currentFeature;

    private boolean appending = false;

    private int nextID = 0;
    private SimpleFeatureBuilder builder;
    private SimpleFeatureType schema;
    private File file;
    
    public GeoJSONInsertFeatureWriter(ContentState state, Query query, SimpleFeatureType schema) throws IOException {
    	this.state = state;
    	String typeName = query.getTypeName();
    	URL url = ((GeoJSONDataStore) state.getEntry().getDataStore()).url;
        file = ISODataUtilities.urlToFile(url);
        File directory = file.getParentFile();
        
        if (directory == null) {
            throw new IOException(
                    "Unable to find parent direcotry of file " + file + " from url " + url);
        }
        if (!directory.canWrite()) {
            throw new IOException("Directory " + directory + " is not writable.");
        }
    	this.temp = File.createTempFile(typeName + System.currentTimeMillis(), "geojson",
                directory);
       this.builder = new SimpleFeatureBuilder(schema, new ISOFeatureFactoryImpl());
       this.schema = schema;
        this.geoJSONWriter = new GeoJSONWriter(new FileOutputStream(this.temp));
    }

   

    public boolean hasNext() throws IOException {
        return false;
    }

    public SimpleFeature next() throws IOException {
        //init, setting id to null explicity since the feature is yet to be 
        // inserted
    	 

    	currentFeature = builder.buildFeature( "fid.1" );
        return currentFeature;
    }

    public void remove() throws IOException {
        //noop
    }

    public void write() throws IOException {
    	if (this.currentFeature == null) {
            return; // current feature has been deleted
        }

        geoJSONWriter.write(currentFeature);
        nextID++;
        this.currentFeature = null; // indicate that it has been written
    }

    public void close() throws IOException {
    	if (geoJSONWriter == null) {
            throw new IOException("Writer is already closed");
        }
        if (this.currentFeature != null) {

            this.write();
        }
        // now write out the remaining features
        while (this.hasNext()) {
            next();
            write();
        }
        geoJSONWriter.close();
        geoJSONWriter = null;
        if (delegate != null) {
            delegate.close();
            delegate = null;
        }

        // now copy over the new file onto the old one
        Files.copy(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }



	@Override
	public SimpleFeatureType getFeatureType() {
		// TODO Auto-generated method stub
		return this.schema;
	}
}
