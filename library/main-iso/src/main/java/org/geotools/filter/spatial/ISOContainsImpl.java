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
import org.opengis.filter.spatial.Contains;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;

//import com.vividsolutions.jts.geom.Envelope;
//import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * @author Soojin Kim, Pusan National University
 * @source $URL$
 */
public class ISOContainsImpl extends ISOAbstractPreparedGeometryFilter implements Contains {

    public ISOContainsImpl(Expression e1, Expression e2) {
        super(e1, e2);
    }

    public ISOContainsImpl(Expression e1, Expression e2, MatchAction matchAction) {
        super(e1, e2, matchAction);
    }
	
	@Override
	public boolean evaluateInternal(Geometry left, Geometry right) {
		
        switch (literals) {
        case BOTH:
            return cacheValue;
        case RIGHT: {
        	// since it is left contains right there is no
        	// benefit of having a prepared geometry for the right side
            return basicEvaluate(left, right);
        }
        case LEFT: {
            //return leftPreppedGeom.contains(right);
        }
        default: {
            return basicEvaluate(left, right);
        }
        }
	}
	
	@Override
	protected boolean basicEvaluate(Geometry left, Geometry right) {
		//Envelope envLeft = left.getEnvelopeInternal();
		//Envelope envRight = right.getEnvelopeInternal();
		ReferencedEnvelope3D envLeft = new ReferencedEnvelope3D(left.getEnvelope());
		ReferencedEnvelope3D envRight = new ReferencedEnvelope3D(right.getEnvelope());
		
		if(envLeft.contains((BoundingBox)envRight)) {
            //TODO previous code HACK!! sfcgal is so slow : return left.contains(right);
			//return left.contains(right);
			return true;
		}
        
        return false;
	}
	public Object accept(FilterVisitor visitor, Object extraData) {
		return visitor.visit(this,extraData);
	}
}
