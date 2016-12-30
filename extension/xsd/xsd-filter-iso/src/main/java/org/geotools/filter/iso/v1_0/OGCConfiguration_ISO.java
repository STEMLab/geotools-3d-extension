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
package org.geotools.filter.iso.v1_0;

import org.geotools.filter.FilterFactoryImpl;
import org.geotools.filter.ISOFilterFactoryImpl;
import org.geotools.filter.iso.v1_0.OGC;
import org.geotools.filter.iso.v1_0.OGCAddBinding;
import org.geotools.filter.iso.v1_0.OGCAndBinding;
import org.geotools.filter.iso.v1_0.OGCBBOXTypeBinding;
import org.geotools.filter.iso.v1_0.OGCBeyondBinding;
import org.geotools.filter.iso.v1_0.OGCBinaryComparisonOpTypeBinding;
import org.geotools.filter.iso.v1_0.OGCBinaryLogicOpTypeBinding;
import org.geotools.filter.iso.v1_0.OGCBinaryOperatorTypeBinding;
import org.geotools.filter.iso.v1_0.OGCBinarySpatialOpTypeBinding;
import org.geotools.filter.iso.v1_0.OGCContainsBinding;
import org.geotools.filter.iso.v1_0.OGCCrossesBinding;
import org.geotools.filter.iso.v1_0.OGCDWithinBinding;
import org.geotools.filter.iso.v1_0.OGCDisjointBinding;
import org.geotools.filter.iso.v1_0.OGCDistanceBufferTypeBinding;
import org.geotools.filter.iso.v1_0.OGCDistanceTypeBinding;
import org.geotools.filter.iso.v1_0.OGCDivBinding;
import org.geotools.filter.iso.v1_0.OGCEqualsBinding;
import org.geotools.filter.iso.v1_0.OGCExpressionTypeBinding;
import org.geotools.filter.iso.v1_0.OGCFeatureIdTypeBinding;
import org.geotools.filter.iso.v1_0.OGCFilterTypeBinding;
import org.geotools.filter.iso.v1_0.OGCFunctionTypeBinding;
import org.geotools.filter.iso.v1_0.OGCIntersectsBinding;
import org.geotools.filter.iso.v1_0.OGCLiteralTypeBinding;
import org.geotools.filter.iso.v1_0.OGCLowerBoundaryTypeBinding;
import org.geotools.filter.iso.v1_0.OGCMulBinding;
import org.geotools.filter.iso.v1_0.OGCNotBinding;
import org.geotools.filter.iso.v1_0.OGCOrBinding;
import org.geotools.filter.iso.v1_0.OGCOverlapsBinding;
import org.geotools.filter.iso.v1_0.OGCPropertyIsBetweenTypeBinding;
import org.geotools.filter.iso.v1_0.OGCPropertyIsEqualToBinding;
import org.geotools.filter.iso.v1_0.OGCPropertyIsGreaterThanBinding;
import org.geotools.filter.iso.v1_0.OGCPropertyIsGreaterThanOrEqualToBinding;
import org.geotools.filter.iso.v1_0.OGCPropertyIsLessThanBinding;
import org.geotools.filter.iso.v1_0.OGCPropertyIsLessThanOrEqualToBinding;
import org.geotools.filter.iso.v1_0.OGCPropertyIsLikeTypeBinding;
import org.geotools.filter.iso.v1_0.OGCPropertyIsNotEqualToBinding;
import org.geotools.filter.iso.v1_0.OGCPropertyIsNullTypeBinding;
import org.geotools.filter.iso.v1_0.OGCPropertyNameTypeBinding;
import org.geotools.filter.iso.v1_0.OGCSubBinding;
import org.geotools.filter.iso.v1_0.OGCTouchesBinding;
import org.geotools.filter.iso.v1_0.OGCUpperBoundaryTypeBinding;
import org.geotools.filter.iso.v1_0.OGCWithinBinding;
import org.geotools.gml2.iso.GMLConfiguration_ISO;
import org.geotools.xml.Configuration;
import org.opengis.filter.FilterFactory;
import org.picocontainer.MutablePicoContainer;


/**
 * Parser configuration for the filter 1.0 schema.
 *
 * @author Justin Deoliveira, The Open Planning Project
 *
 *
 *
 *
 * @source $URL$
 */
public class OGCConfiguration_ISO extends Configuration {
    /**
     * Adds a dependency on {@link GMLConfiguration_ISO}
     */
    public OGCConfiguration_ISO() {
        super(OGC.getInstance());
        addDependency(new GMLConfiguration_ISO());
    }

    protected void registerBindings(MutablePicoContainer container) {
        //expr.xsd
        container.registerComponentImplementation(OGC.Add, OGCAddBinding.class);
        container.registerComponentImplementation(OGC.BinaryOperatorType,
            OGCBinaryOperatorTypeBinding.class);
        container.registerComponentImplementation(OGC.Div, OGCDivBinding.class);
        container.registerComponentImplementation(OGC.ExpressionType, OGCExpressionTypeBinding.class);
        container.registerComponentImplementation(OGC.FunctionType, OGCFunctionTypeBinding.class);
        container.registerComponentImplementation(OGC.LiteralType, OGCLiteralTypeBinding.class);
        container.registerComponentImplementation(OGC.Mul, OGCMulBinding.class);
        container.registerComponentImplementation(OGC.PropertyNameType,
            OGCPropertyNameTypeBinding.class);
        container.registerComponentImplementation(OGC.Sub, OGCSubBinding.class);

        //filter.xsd
        container.registerComponentImplementation(OGC.And, OGCAndBinding.class);
        container.registerComponentImplementation(OGC.BBOXType, OGCBBOXTypeBinding.class);
        container.registerComponentImplementation(OGC.Beyond, OGCBeyondBinding.class);
        container.registerComponentImplementation(OGC.BinaryComparisonOpType,
            OGCBinaryComparisonOpTypeBinding.class);
        container.registerComponentImplementation(OGC.BinaryLogicOpType,
            OGCBinaryLogicOpTypeBinding.class);
        container.registerComponentImplementation(OGC.BinarySpatialOpType,
            OGCBinarySpatialOpTypeBinding.class);
        container.registerComponentImplementation(OGC.Contains, OGCContainsBinding.class);
        container.registerComponentImplementation(OGC.Crosses, OGCCrossesBinding.class);
        container.registerComponentImplementation(OGC.Disjoint, OGCDisjointBinding.class);
        //container.registerComponentImplementation(OGC.COMPARISONOPSTYPE,OGCComparisonOpsTypeBinding.class);
        container.registerComponentImplementation(OGC.DistanceBufferType,
            OGCDistanceBufferTypeBinding.class);
        container.registerComponentImplementation(OGC.DistanceType, OGCDistanceTypeBinding.class);
        container.registerComponentImplementation(OGC.DWithin, OGCDWithinBinding.class);
        container.registerComponentImplementation(OGC.Equals, OGCEqualsBinding.class);
        container.registerComponentImplementation(OGC.FeatureIdType, OGCFeatureIdTypeBinding.class);
        container.registerComponentImplementation(OGC.FilterType, OGCFilterTypeBinding.class);
        //container.registerComponentImplementation(OGC.LOGICOPSTYPE,OGCLogicOpsTypeBinding.class);
        container.registerComponentImplementation(OGC.Intersects, OGCIntersectsBinding.class);
        container.registerComponentImplementation(OGC.LowerBoundaryType,
            OGCLowerBoundaryTypeBinding.class);
        container.registerComponentImplementation(OGC.Not, OGCNotBinding.class);
        container.registerComponentImplementation(OGC.Or, OGCOrBinding.class);
        container.registerComponentImplementation(OGC.PropertyIsBetweenType,
            OGCPropertyIsBetweenTypeBinding.class);
        container.registerComponentImplementation(OGC.PropertyIsEqualTo,
            OGCPropertyIsEqualToBinding.class);
        container.registerComponentImplementation(OGC.PropertyIsGreaterThan,
            OGCPropertyIsGreaterThanBinding.class);
        container.registerComponentImplementation(OGC.PropertyIsGreaterThanOrEqualTo,
            OGCPropertyIsGreaterThanOrEqualToBinding.class);
        container.registerComponentImplementation(OGC.PropertyIsLessThan,
            OGCPropertyIsLessThanBinding.class);
        container.registerComponentImplementation(OGC.PropertyIsLessThanOrEqualTo,
            OGCPropertyIsLessThanOrEqualToBinding.class);
        container.registerComponentImplementation(OGC.PropertyIsLikeType,
            OGCPropertyIsLikeTypeBinding.class);
        container.registerComponentImplementation(OGC.PropertyIsNullType,
            OGCPropertyIsNullTypeBinding.class);
        container.registerComponentImplementation(OGC.PropertyIsNotEqualTo,
            OGCPropertyIsNotEqualToBinding.class);
        container.registerComponentImplementation(OGC.Overlaps, OGCOverlapsBinding.class);
        container.registerComponentImplementation(OGC.Touches, OGCTouchesBinding.class);
        //container.registerComponentImplementation(OGC.SPATIALOPSTYPE,OGCSpatialOpsTypeBinding.class);
        //container.registerComponentImplementation(OGC.UnaryLogicOpType,
        //    OGCUnaryLogicOpTypeBinding.class);
        container.registerComponentImplementation(OGC.UpperBoundaryType,
            OGCUpperBoundaryTypeBinding.class);
        container.registerComponentImplementation(OGC.Within, OGCWithinBinding.class);
    }

    /**
     * Configures the filter context.
     * <p>
     * The following factories are registered:
     * <ul>
     * <li>{@link FilterFactoryImpl} under {@link FilterFactory}
     * </ul>
     * </p>
     */
    public void configureContext(MutablePicoContainer container) {
        super.configureContext(container);

        container.registerComponentImplementation(FilterFactory.class, ISOFilterFactoryImpl.class);
    }
}
