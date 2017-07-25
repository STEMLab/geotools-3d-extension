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
package org.geotools.jdbc.iso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.feature.ISOFeatureFactoryImpl;
import org.geotools.feature.simple.ISOSimpleFeatureBuilder;
import org.geotools.feature.simple.ISOSimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.coordinate.LineSegment;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Point;

/**
 * Tests the ability of the datastore to cope with 3D data
 * 
 * @author Andrea Aime - OpenGeo
 * @author Martin Davis - OpenGeo
 * 
 * 
 * 
 * @source $URL$
 */
public abstract class JDBC3DOnlineTest extends JDBCGeneric3DOnlineTest {

    protected abstract JDBC3DTestSetup createTestSetup();

    @Override
    protected int getEpsgCode() {
        return 4326;
    }
    
    @Override
    protected String getLine3d() {
        return "line3d";
    }

    @Override
    protected String getPoint3d() {
        return "point3d";
    }

    @Override
    protected String getPoly3d() {
        return "poly3d";
    }
    
    @Override
    protected String getLine3d_Write()
	{
		return "line3d_write";
	}

    @Override
    protected String getPoint3d_Write(){
		return "point3d_write";
	}

    @Override
    protected String getPoly3d_Write(){
    	return "poly3d_write";
    }
    

	@Override
	protected String getSolid() {
		// TODO Auto-generated method stub
		return "solid";
	}

	@Override
	protected String getSolid_Write() {
		// TODO Auto-generated method stub
		return "solid_write";
	}
	

}
