/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gml3.iso.bindings;

import javax.xml.namespace.QName;

import org.geotools.gml3.iso.ArcParameters;
import org.geotools.gml3.iso.Circle;
import org.geotools.gml3.iso.GML;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.primitive.Curve;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;


/**
 *
 * @author Erik van de Pol. B3Partners BV.
 *
 *
 * @source $URL$
 */
public class CircleTypeBinding extends AbstractComplexBinding {
    ISOGeometryBuilder gBuilder;
    ArcParameters arcParameters;

    public CircleTypeBinding(ISOGeometryBuilder gBuilder, ArcParameters arcParameters) {
        this.gBuilder = gBuilder;
        this.arcParameters = arcParameters;
    }

    /**
     * @generated
     */
    public QName getTarget() {
        return GML.CircleType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    public Class getType() {
        return Curve.class;
    }

    @Override
    public int getExecutionMode() {
        return OVERRIDE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    @Override
    public Object parse(ElementInstance instance, Node node, Object value)
        throws Exception {

        /*LineString circleLineString = GML3ParsingUtils.lineString(node, gFactory, csFactory);

        Coordinate[] circleCoordinates = circleLineString.getCoordinates();
        if (circleCoordinates.length != 3) {
            // maybe log this instead and return null
            throw new RuntimeException(
                    "GML3 parser exception: The number of coordinates of a Circle should be 3. It currently is: " + circleCoordinates.length + "; " + circleLineString);
        }

        Coordinate c1 = circleCoordinates[0];
        Coordinate c2 = circleCoordinates[1];
        Coordinate c3 = circleCoordinates[2];

        Circle circle = new Circle(c1, c2, c3);
        double tolerance = arcParameters.getLinearizationTolerance().getTolerance(circle);
        Coordinate[] resultCoordinates = Circle.linearizeCircle(c1, c2, c3, tolerance);

        LineString resultLineString = gFactory.createLineString(resultCoordinates);
*/
        //return resultLineString;
        return null;
    }

}

