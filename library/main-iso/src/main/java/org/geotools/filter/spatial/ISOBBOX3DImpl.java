/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2005, Open Geospatial Consortium Inc.
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
package org.geotools.filter.spatial;

import org.geotools.filter.IllegalFilterException;
import org.geotools.geometry.iso.topograph2D.TopologyException;
import org.geotools.geometry.jts.ReferencedEnvelope3D;
import org.geotools.referencing.CRS;
import org.geotools.util.Converters;
import org.geotools.util.Util;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.BBOX3D;
import org.opengis.geometry.BoundingBox3D;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

import com.vividsolutions.jts.geom.Coordinate;
//import com.vividsolutions.jts.geom.Geometry;
//import com.vividsolutions.jts.geom.GeometryFactory;
//import com.vividsolutions.jts.geom.LinearRing;
//import com.vividsolutions.jts.geom.Polygon;
//import com.vividsolutions.jts.geom.TopologyException;

/**
 * 
 * A 3D BBOX Filter Implementation Supports filtering with BBOXes that have 3D
 * coordinates including a minimum and maximum for the z-axis.
 * 
 * @author Niels Charlier
 * @author Soojin Kim, Pusan National University
 */

public class ISOBBOX3DImpl implements BBOX3D {

	PropertyName property;
	ReferencedEnvelope3D envelope;
	FilterFactory factory;

	public ISOBBOX3DImpl(PropertyName propertyName, ReferencedEnvelope3D env, FilterFactory factory) {
		this.property = propertyName;
		this.envelope = env;
		this.factory = factory;
	}

	public double getMaxX() {
		return envelope.getMaxX();
	}

	public double getMaxY() {
		return envelope.getMaxY();
	}

	public double getMinX() {
		return envelope.getMinX();
	}

	public double getMinY() {
		return envelope.getMinY();
	}

	public double getMinZ() {
		return envelope.getMinX();
	}

	public double getMaxZ() {
		return envelope.getMaxZ();
	}

	public PropertyName getProperty() {
		return property;
	}

	public String getPropertyName() {
		return property.getPropertyName();
	}

	public String getSRS() {
		return CRS.toSRS(envelope.getCoordinateReferenceSystem());
	}

	public BoundingBox3D getBounds() {
		return envelope;
	}

	public Expression getExpression1() {
		return property;
	}

	public Expression getExpression2() {
		// in this case, the 3D BBOX falls back to regular 2D bbox behaviour
		// (until there is more support for 3D geometries)
		// 3DBBOX must be run as a post-filter in order to support the third
		// coordinate.

		// Coordinate[] coords = new Coordinate[5];
		double[] coords = new double[24];
		for(int i=0;i<coords.length;i++) {
			
		}
		
		// LinearRing ring = null;
		Curve curve = null;

		ISOGeometryBuilder gfac = new ISOGeometryBuilder(envelope.getCoordinateReferenceSystem());
		try {
			curve = gfac.createCurve(gfac.createPointArray(coords));// createLinearRing(coords);
		} catch (TopologyException tex) {
			throw new IllegalFilterException(tex.toString());
		}

		// Polygon polygon = gfac.createPolygon(ring, null);
		SurfaceBoundary surfaceboundary = gfac.createSurfaceBoundary(curve);
		Surface surface = gfac.createSurface(surfaceboundary);
		if (envelope instanceof ReferencedEnvelope3D) {
			ReferencedEnvelope3D refEnv = (ReferencedEnvelope3D) envelope;
			// polygon.setUserData(refEnv.getCoordinateReferenceSystem());

		}

		return factory.literal(surface);
	}

	public Object accept(FilterVisitor visitor, Object context) {
		return visitor.visit(this, context);
	}

	public ReferencedEnvelope3D get3DEnvelope(Geometry geom) {
		//Coordinate[] coordinates = geom.getCoordinates();
		Coordinate[] coordinates = Util.getCoordinateArray(geom);
		
		ReferencedEnvelope3D env = new ReferencedEnvelope3D();
		for (Coordinate coordinate : coordinates) {
			env.expandToInclude(coordinate);
		}
		return env;
	}

	public boolean evaluate(Object feature) {

		Geometry other = Converters.convert(property.evaluate(feature), Geometry.class);
		if (other == null)
			return false;

		return get3DEnvelope(other).intersects(envelope);
	}

	// THIS GARGABE IS HERE TO ALLOW OLD DATASTORES NOT USING PROPER OGC FILTERS
	// TO WORK
	// WILL BE REMOVED WHEN THERE IS NOTHING LEFT USING THEM

	public boolean isMatchingCase() {
		return false;
	}

	public boolean contains(SimpleFeature feature) {
		return evaluate((Object) feature);
	}

	public boolean evaluate(SimpleFeature feature) {
		return evaluate((Object) feature);
	}

	public MatchAction getMatchAction() {
		return MatchAction.ANY;
	}

	@Override
	public String toString() {
		return "BBOX3D [property=" + property + ", envelope=" + envelope + "]";
	}

}
