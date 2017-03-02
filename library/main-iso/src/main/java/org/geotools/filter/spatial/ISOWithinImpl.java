/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope3D;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Within;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Geometry;

/**
 * 
 * @author Soojin Kim, Pusan National University
 * @source $URL$
 */
public class ISOWithinImpl extends ISOAbstractPreparedGeometryFilter implements Within {

    public ISOWithinImpl(Expression e1, Expression e2) {
        super(e1, e2);
    }

    public ISOWithinImpl(Expression e1, Expression e2, MatchAction matchAction) {
        super(e1, e2, matchAction);
    }
	
	@Override
        public boolean evaluateInternal(Geometry left, Geometry right) {
		
        switch (literals) {
        case BOTH:
            return cacheValue;
        case RIGHT: {
        	// if the right contains left then left is within right
            //return rightPreppedGeom.contains(left);
        }
        case LEFT: {
        	// since within does not have an optimization with prepared geometries
        	// there is nothing to be gained in this case so use the normal check
            //return basicEvaluate(leftPreppedGeom.getGeometry(), right);
        	return basicEvaluate(left, right);
        }
        default: {
            return basicEvaluate(left, right);
        }
        }
	}
	
	public Object accept(FilterVisitor visitor, Object extraData) {
		return visitor.visit(this,extraData);
	}

	@Override
	protected boolean basicEvaluate(Geometry left, Geometry right) {
	    ReferencedEnvelope envLeft = ReferencedEnvelope.reference(left.getEnvelope());
	    ReferencedEnvelope envRight = ReferencedEnvelope.reference(right.getEnvelope());
	        
	    ReferencedEnvelope empty = new ReferencedEnvelope();
	    ReferencedEnvelope queryResult = envRight.intersection(envLeft);
	    if(!empty.equals(queryResult)) {
	        return right.contains(left);
	    }
	    return false;
	}

}
