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

//import org.geotools.filter.GeometryFilterImpl;
import org.geotools.filter.ISOGeometryFilterImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope3D;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Equals;
import org.opengis.geometry.Geometry;

//import com.vividsolutions.jts.geom.Envelope;
//import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 *
 * @source $URL$
 */
public class ISOEqualsImpl extends ISOGeometryFilterImpl implements Equals {

    public ISOEqualsImpl(Expression e1, Expression e2) {
        super(e1, e2);
    }

    public ISOEqualsImpl(Expression e1, Expression e2, MatchAction matchAction) {
        super(e1, e2, matchAction);
    }

	@Override
        public boolean evaluateInternal(Geometry left, Geometry right) {
	    ReferencedEnvelope envLeft = ReferencedEnvelope.reference(left.getEnvelope());
            ReferencedEnvelope envRight = ReferencedEnvelope.reference(right.getEnvelope());
            
            if(envLeft.equals(envRight)) {
                return left.equals(right);
            }
            return false;
	}

	public Object accept(FilterVisitor visitor, Object extraData) {
		return visitor.visit(this, extraData);
	}
}
