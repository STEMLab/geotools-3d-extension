/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.data.postgis3d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.FilterCapabilities;
import org.geotools.filter.IllegalFilterException;
import org.geotools.filter.visitor.ClientTransactionAccessor;
import org.geotools.filter.visitor.ISOPostPreProcessFilterSplittingVisitor;
import org.geotools.jdbc.iso.SQLDialect;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.And;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Id;
import org.opengis.filter.IncludeFilter;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNil;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.BinaryExpression;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.BBOX3D;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.AnyInteracts;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.Begins;
import org.opengis.filter.temporal.BegunBy;
import org.opengis.filter.temporal.BinaryTemporalOperator;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.EndedBy;
import org.opengis.filter.temporal.Ends;
import org.opengis.filter.temporal.Meets;
import org.opengis.filter.temporal.MetBy;
import org.opengis.filter.temporal.OverlappedBy;
import org.opengis.filter.temporal.TContains;
import org.opengis.filter.temporal.TEquals;
import org.opengis.filter.temporal.TOverlaps;
import org.opengis.geometry.Geometry;


/**
 * Determines what queries can be processed server side and which can be processed client side.
 * 
 * IMPLEMENTATION NOTE:
 * This class is implemented as a stack processor.  If you're curious how it works, compare it with
 * the old SQLUnpacker class, which did the same thing using recursion in a more straightforward
 * way.
 * 
 * Here's a non-implementors best-guess at the algorithm:
 *  Starting at the top of the filter, split each filter into
 *  its constituent parts.  If the given FilterCapabilities
 *  support the given operator, then keep checking downwards.
 *  
 * The key is in knowing whether or not something "down the tree"
 *  from you wound up being supported or not.  This is where the
 *  stacks come in.  Right before handing off to accept() the sub-
 *  filters, we count how many things are currently on the "can
 *  be proccessed by the underlying datastore" stack (the preStack)
 *  and we count how many things are currently on the "need to be post-
 *  processed" stack.
 * 
 * After the accept() call returns, we look again at the preStack.size()
 *  and postStack.size().  If the postStack has grown, that means that there
 *  was stuff down in the accept()-ed filter that wasn't supportable.
 *  Usually this means that our filter isn't supportable, but not always.
 * 
 * In some cases a sub-filter being unsupported isn't necessarily bad,
 *  as we can 'unpack' OR statements into AND statements
 *  (DeMorgans rule/modus poens) and still
 *  see if we can handle the other side of the OR.  Same with NOT and
 *  certain kinds of AND statements.
 * 
 * In addition this class supports the case where we're doing an split
 * in the middle of a client-side transaction.  I.e. imagine doing a
 * <Transaction> against a WFS-T where you have to filter against
 * actions that happened previously in the transaction.  That's what
 * the ClientTransactionAccessor interface does, and this class splits
 * filters while respecting the information about deletes and updates
 * that have happened previously in the Transaction.  I can't say with
 * certainty exactly how the logic for that part of this works, but
 * the test suite does seem to test it and the tests do pass.
 * 
 * @author soojin kim
 *
 *
 * @source $URL$
 * @deprecated use {@link CapabilitiesFilterSplitter} instead for geoapi FilterCapabilities
 */
public class PostPreProcessPostGISFilterSplittingVisitor extends ISOPostPreProcessFilterSplittingVisitor {
		private static final Logger logger=org.geotools.util.logging.Logging.getLogger("org.geotools.filter");
 
	    protected SQLDialect dialect = null;


	    private PostPreProcessPostGISFilterSplittingVisitor() {
	    	super();
	    }
	
	    /**
	     * Create a new instance.
	     * @param fcs The FilterCapabilties that describes what Filters/Expressions the server can process.
	     * @param parent The FeatureType that this filter involves.  Why is this needed?
	     * @param transactionAccessor If the transaction is handled on the client and not the server then different filters
	     * must be sent to the server.  This class provides a generic way of obtaining the information from the transaction.
	     */

	    public PostPreProcessPostGISFilterSplittingVisitor(FilterCapabilities fcs, SimpleFeatureType parent,  SQLDialect dialect) {
	    	super(fcs, parent);
	        this.dialect = dialect;
	    }
  
        public Object visit(Intersects filter, Object notUsed) {

        	Literal lt = null;

        	Expression leftGeometry = ((BinarySpatialOperator)filter).getExpression1();
        	Expression rightGeometry = ((BinarySpatialOperator)filter).getExpression2();
        	if(leftGeometry instanceof Literal) {
        		lt = (Literal)leftGeometry;
        	}else if(rightGeometry instanceof Literal) {
        		lt = (Literal)rightGeometry;
        	}
        	Geometry g = (Geometry) lt.getValue();
        	
        	if(PostGISDialect.CLASS_TO_TYPE_MAP.containsKey(g.getClass())) {
        		visitBinarySpatialOperator(filter);
        	}else {
        		postStack.push(filter);
        	}
            
            return null;
        }


}
