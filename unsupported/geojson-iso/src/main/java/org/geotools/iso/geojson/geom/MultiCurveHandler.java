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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.parser.ParseException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;

/**
 * 
 *
 * @source $URL$
 */
public class MultiCurveHandler extends GeometryHandlerBase<MultiCurve> {

    List<DirectPosition> coordinates;
    List<PointArray> lines;
    
    public MultiCurveHandler(ISOGeometryBuilder builder) {
        super(builder);
        
    }
    
    @Override
    public boolean startObjectEntry(String key) throws ParseException, IOException {
        if ("coordinates".equals(key)) {
            lines = new ArrayList();
        }
        return true;
    }
    
    @Override
    public boolean startArray() throws ParseException, IOException {
        if (coordinates == null) {
            coordinates = new ArrayList();
        }
        else if (ordinates == null) {
            ordinates = new ArrayList();
        }
        return true;
    }
    
    @Override
    public boolean endArray() throws ParseException, IOException {
        if (ordinates != null) {
            coordinates.add(coordinate(ordinates));
            ordinates = null;
        }
        else if (coordinates != null) {
            lines.add(coordinates(coordinates));
            coordinates = null;
        }
        
        return true;
    }
    
    @Override
    public boolean endObject() throws ParseException, IOException {
        if (lines != null) {
            Set<Curve> curves = new HashSet<Curve>(lines.size());
            for (int i = 0; i < lines.size(); i++) {
                curves.add(builder.createCurve(lines.get(i)));
            }
            value = builder.createMultiCurve(curves);
            lines = null;
        }
        return true;
    }
}

