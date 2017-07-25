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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.parser.ParseException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

/**
 * 
 *
 * @source $URL$
 */
public class MultiPolygonHandler extends GeometryHandlerBase<MultiSurface> {

    List<DirectPosition> coordinates;
    List<PointArray> rings;
    List<List<PointArray>> polys;
    
    public MultiPolygonHandler(ISOGeometryBuilder builder) {
        super(builder);
    }
    
    @Override
    public boolean startObjectEntry(String key) throws ParseException, IOException {
        if ("coordinates".equals(key)) {
            polys = new ArrayList();
        }
        
        return true;
    }
    
    @Override
    public boolean startArray() throws ParseException, IOException {
        if (rings == null) {
            rings = new ArrayList();
        }
        else if (coordinates == null) {
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
            rings.add(coordinates(coordinates));
            coordinates = null;
        }
        else if (rings != null) {
            polys.add(rings);
            rings = null;
        }
        
        return true;
    }
    
    @Override
    public boolean endObject() throws ParseException {
        if (polys != null) {
            Set<Surface> surfaces = new HashSet<Surface>(polys.size());
            for (int i = 0; i < polys.size(); i++) {
            	
                List<PointArray> rings = polys.get(i);
                if (rings.isEmpty()) {
                    continue;
                }

                Curve c = builder.createCurve(rings.get(0));
                Ring outer = builder.createRing(Arrays.asList(c));
                List<Ring> inner = null;
                if (rings.size() > 1) {
                	inner = new ArrayList<Ring>(rings.size() - 1);
                    for (int j = 1; j < rings.size(); j++) {
                    	c = builder.createCurve(rings.get(j));
                    	inner.add(builder.createRing(Arrays.asList(c)));
                    }
                }
                
                SurfaceBoundary sb = builder.createSurfaceBoundary(outer, inner);
                surfaces.add(builder.createSurface(sb));
            }
            
            value = builder.createMultiSurface(surfaces);
            polys = null;
        }

        return true;
    }
}
