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
package org.geotools.geojson.geom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

/**
 * 
 *
 * @source $URL$
 */
public class PolygonHandler extends GeometryHandlerBase<Surface> {

    List<DirectPosition> coordinates;
    List<PointArray> rings;
    
    public PolygonHandler(ISOGeometryBuilder builder) {
        super(builder);
    }
    
    @Override
    public boolean startObjectEntry(String key) throws ParseException, IOException {
        if ("coordinates".equals(key)) {
            rings = new ArrayList();
        }
        return true;
    }
    
    @Override
    public boolean endObject() throws ParseException, IOException {
        if (rings != null) {
            if (rings.isEmpty()) {
                throw new IllegalArgumentException("Polygon specified with no rings.");
            }
            
            Curve c = builder.createCurve(rings.get(0));
            Ring outer = builder.createRing(Arrays.asList(c));
            List<Ring> inner = null;
            if (rings.size() > 1) {
            	inner = new ArrayList<Ring>(rings.size() - 1);
                for (int i = 1; i < rings.size(); i++) {
                	c = builder.createCurve(rings.get(i));
                	inner.add(builder.createRing(Arrays.asList(c)));
                }
            }
            
            SurfaceBoundary sb = builder.createSurfaceBoundary(outer, inner);
            value = builder.createSurface(sb);
            rings = null;
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
            DirectPosition c = coordinate(ordinates);
            coordinates.add(c);
            ordinates = null;
        }
        else if (coordinates != null) {
            rings.add(coordinates(coordinates));
            coordinates = null;
        }
        return true;
    }
}
