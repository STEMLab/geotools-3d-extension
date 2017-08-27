/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.iso.geojson.geom;

import java.io.IOException;

import org.geotools.iso.geojson.DelegatingHandler;
import org.geotools.iso.geojson.RecordingHandler;
import org.json.simple.parser.ParseException;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * 
 *
 * @source $URL$
 */
public class ISOGeometryHandler extends DelegatingHandler<Geometry> {

	CoordinateReferenceSystem crs;
    ISOGeometryBuilder builder;
    RecordingHandler proxy;

    public ISOGeometryHandler(CoordinateReferenceSystem crs) {
        this.crs = crs;
        this.builder = new ISOGeometryBuilder(crs);
    }
    
    public ISOGeometryHandler(ISOGeometryBuilder builder) {
        this.builder = builder;
    }
    
    public void setCrs(CoordinateReferenceSystem crs) {
    	this.crs = crs;
    	this.builder = new ISOGeometryBuilder(crs);
    }
    
    @Override
    public boolean startObjectEntry(String key) throws ParseException, IOException {
        if ("type".equals(key) && (delegate == NULL || delegate == proxy)) {
            delegate = UNINITIALIZED;
            return true;
        }
        else if ("coordinates".equals(key) && delegate == NULL) {
            //case of specifying coordinates before the actual geometry type, create a proxy 
            // handler that will simply track calls until the type is actually specified
            proxy = new RecordingHandler();
            delegate = proxy;
            return super.startObjectEntry(key);
        }
        else if ("geometries".equals(key) && delegate == NULL) {
            // geometry collection without type property first
            delegate = new MultiPrimitiveHandler(builder);
            return super.startObjectEntry(key);
        }
        else {
            return super.startObjectEntry(key);
        }
    }
    
    @Override
    public boolean primitive(Object value) throws ParseException, IOException {
        if (delegate == UNINITIALIZED) {
            delegate = createDelegate(lookupDelegate(value.toString()), new Object[]{builder});
            if (proxy != null) {
                proxy.replay(delegate);
                proxy = null;
            }
            return true;
        }
        else {
            return super.primitive(value);
        }
    }
}
