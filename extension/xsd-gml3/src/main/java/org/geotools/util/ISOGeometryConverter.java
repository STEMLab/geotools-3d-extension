/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2016, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.util;

import org.geotools.factory.Hints;
import org.geotools.util.Converter;
import org.geotools.util.ConverterFactory;
import org.opengis.geometry.primitive.Solid;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author hgryoo
 *
 */
public class ISOGeometryConverter implements ConverterFactory {

    public Converter createConverter(Class source, Class target, Hints hints) {
    
            if ( Solid.class.isAssignableFrom( source ) ) {
                    //Geometry to envelope
                    if ( Geometry.class.equals( target ) ) {
                            return new Converter() {
                                    public Object convert(Object source, Class target) throws Exception {
                                            Solid geometry = (Solid) source;
                                            return geometry;
                                    }
                            };
                    }
                    
                    //Geometry to String
                    if ( String.class.equals( target ) ) {
                            return new Converter() {
                                    public Object convert(Object source, Class target) throws Exception {
                                            Solid geometry = (Solid) source;
                                            return geometry.toString();
                                    }
                            };
                    }
            }
            
            return null;
    }
}