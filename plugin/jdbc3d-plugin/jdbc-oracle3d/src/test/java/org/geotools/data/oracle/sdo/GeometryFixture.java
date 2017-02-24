/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    Refractions Research Inc. Can be found on the web at:
 *    http://www.refractions.net/
 */
package org.geotools.data.oracle.sdo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.util.Elements;

import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.geometry.iso.primitive.SurfaceImpl;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
/**
 * Construct geometry used by test cases.
 * <p>
 * Several examples are from the the Oracle Spatial Geometry Spec.
 * </p>
 * 
 * @see net.refractions.jspatial.jts
 * @author jgarnett, Refractions Reasearch Inc.
 * @author Taehoon Kim, Pusan National University
 * 
 * @source $URL$
 * @version CVS Version
 */
public class GeometryFixture {
    ISOGeometryBuilder gf = null;
    /**
     * Geometry Example "2.3.1 Rectangle".
     * <p>
     * A simple rectangle as used with CAD applications
     * </p>
     * <code><pre>
     * (1,7)         (5,7)
     *   +-------------+
     *   |             |
     *   |             |
     *   +-------------+
     * (1,1)          (5,1)
     * </pre><code>
     */
    public Surface rectangle;

    /** Polygon used for testing */
    public Surface polygon;

    /**
     * Geometry Example "2.3.2 Polygon with Hole".
     * <p>
     * A Polygon with a Hole as follows:
     * </p>
     * <code><pre>
     *   5,13+-------------+   11,13
     *      /               \
     * 2,11+                 \
     *     | 7,10+----+10,10  \
     *     |     |    |       +13,9
     *     |     |    |       |
     *     |     |    |       |
     *     |  7,5+----+10,5   +13,5
     *  2,4+                  /
     *      \                /
     *   4,3+---------------+10,3
     * </pre></code>
     */
    public Surface polygonWithHole;

    /**
     * Geometry Example "2.3.5 Point".
     * <p>
     * Simple Point used to test POINT_TYPE array use.
     * </p>
     * <code><pre>
     *   +   12,14
     * </pre></code>
     */
    public Point point;

    /** Curve used for testing */
    public Curve lineString;
    
    /** Solid used for testing */
    public Solid solid;

    /** MultiPoint used for testing */
    public MultiPoint multiPoint;

    /** MultiCurve used for testing */
    public MultiCurve multiLineString;

    /** MultiSurface used for testing */
    public MultiSurface multiPolygon;

    /** MultiSurface used for testing */
    public MultiSurface multiPolygonWithHole;

    /** GeometryCollection used for testing */
    public MultiPrimitive geometryCollection;

    /**
     * Construct Fixture for use with default GeometryFactory.
     */
    public GeometryFixture() {
    	this(new ISOGeometryBuilder(new Hints(Hints.GEOMETRY_VALIDATE, false, Hints.CRS, DefaultGeographicCRS.WGS84)));
    }

    /**
     * Construct Fixture for use with provided <code>GeometryFactory</code>.
     */
    public GeometryFixture(ISOGeometryBuilder geometryFactory) {
        gf = geometryFactory;
        rectangle = createRectangle();
        polygon = createPolygon();
        polygonWithHole = createPolygonWithHole();
        point = createPoint();
        lineString = createLineString();
        solid = createSolid();
        multiPoint = createMultiPoint();
        multiLineString = createMultiLineString();
        multiPolygon = createMultiPolygon();
        multiPolygonWithHole = createMultiPolygonWithHole();
        geometryCollection = createGeometryCollection();
    }

    /**
     * Construct a rectangle according to Geometry Examples "2.3.1 Rectangle".
     * <p>
     * A simple rectangle as used with CAD applications
     * </p>
     * <code><pre>
     * (1,7)         (5,7)
     *   +-------------+
     *   |             |
     *   |             |
     *   +-------------+
     * (1,1)          (5,1)
     * </pre><code>
     * <p>The polygon is not consturcted with an SRID (ie srid == -1)</p>
     * A Rectangle with expected encoding:</p>
     * <ul>
     * <li><b>SDO_GTYPE:</b><code>2003</code><br/>
     * 2 dimensional polygon </li> <li><b>SDO_SRID:</b><code>NULL</code></li> <li><b>SDO_POINT:</b>NULL></li> <li><b>SDO_ELEM_INFO:</b>
     * <code>(1,1003,3)</code><br/>
     * 03 indicates this is a rectangle</li> <li><b>SDO_ORDINATES:</b><code>(1,1,5,7)</code><br/>
     * bottom left and upper right</li> </ul>
     * <p>
     * SQL:
     * </p>
     * <code><pre>
     * MDSYS.SDO_GEOMETRY(
     *   2003,
     *   NULL,
     *   NULL,
     *   MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,3),
     *   MDSYS.SDO_ORDINATE_ARRAY(1,1,5,7)
     * )
     * </pre></code>
     * 
     * @see GeometryFixture.rectangle
     */
    protected Surface createRectangle() {
    	PointArray points = gf.createPointArray();
    	points.add(gf.createDirectPosition(new double[] {1, 1}));
    	points.add(gf.createDirectPosition(new double[] {5, 1}));
    	points.add(gf.createDirectPosition(new double[] {5, 7}));
    	points.add(gf.createDirectPosition(new double[] {1, 7}));
    	points.add(gf.createDirectPosition(new double[] {1, 1}));
    	SurfaceBoundary surfaceBoundary = gf.createSurfaceBoundary(points);
    	Surface surface = gf.createSurface(surfaceBoundary);
        ((SurfaceImpl)surface).setUserData(-1); // don't have an SRID number
        return surface;
    }

    /**
     * Construct a polygon of a triangle.
     * <p>
     * Used to illustrate polyugon encoding.
     * </p>
     * <code><pre>
     *        +11,8
     *       / \
     *      /   \
     *     /     \
     * 9,5+-------+13,5
     * </pre></code>
     * <p>
     * A Rectangle with expected encoding:
     * </p>
     * <ul>
     * <li><b>SDO_GTYPE:</b><code>2003</code><br/>
     * 2 dimensional polygon</li>
     * <li><b>SDO_SRID:</b><code>NULL</code></li>
     * <li><b>SDO_POINT:</b>NULL></li>
     * <li><b>SDO_ELEM_INFO:</b><code>(1,1003,1)</code><br/>
     * 1000 for external, 03 for polygon, 1 indicates this polygon uses strait edges</li>
     * <li><b>SDO_ORDINATES:</b><code>(1,1,5,7)</code><br/>
     * bottom left and upper right</li>
     * </ul>
     * <p>
     * SQL:
     * </p>
     * <code><pre>
     * MDSYS.SDO_GEOMETRY(
     *   2003,
     *   NULL,
     *   NULL,
     *   MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,1),
     *   MDSYS.SDO_ORDINATE_ARRAY(9,5, 13,5, 11,8, 9,5)
     * )
     * </pre></code>
     */
    protected Surface createPolygon() {
    	PointArray points = gf.createPointArray();
    	points.add(gf.createDirectPosition(new double[] {9, 5}));
    	points.add(gf.createDirectPosition(new double[] {13, 5}));
    	points.add(gf.createDirectPosition(new double[] {11, 8}));
    	points.add(gf.createDirectPosition(new double[] {9, 5}));
    	SurfaceBoundary surfaceBoundary = gf.createSurfaceBoundary(points);
    	Surface surface = gf.createSurface(surfaceBoundary);
        ((SurfaceImpl)surface).setUserData(-1); // don't have an SRID number
        return surface;
    }

    /**
     * Construct a polygon with hole according to Geometry Examples 2.3.2.
     * <p>
     * Polygon examples used to illustrate compound encoding.
     * </p>
     * <code><pre>
     *   5,13+-------------+   11,13
     *      /               \
     * 2,11+                 \
     *     | 7,10+----+10,10  \
     *     |     |    |       +13,9
     *     |     |    |       |
     *     |     |    |       |
     *     |  7,5+----+10,5   +13,5
     *  2,4+                  /
     *      \                /
     *   4,3+---------------+10,3
     * </pre></code>
     * <p>
     * A Polygon with expected encoding:
     * </p>
     * <ul>
     * <li><b>SDO_GTYPE:</b><code>2003</code><br/>
     * 2 dimensional polygon, 3 for polygon</li>
     * <li><b>SDO_SRID:</b><code>NULL</code></li>
     * <li><b>SDO_POINT:</b>NULL></li>
     * <li><b>SDO_ELEM_INFO:</b><code>(1,1003,1,19,2003,1)</code><br/>
     * Two triplets
     * <ul>
     * <li>(1,1003,1): exterior polygon ring starting at 1</li>
     * 
     * <li>(19,2003,1): interior polygon ring starting at 19</li>
     * </ul>
     * </li>
     * <li><b>SDO_ORDINATES:</b> <code><pre>
     *        (2,4, 4,3, 10,3, 13,5, 13,9, 11,13, 5,13, 2,11, 2,4,
     *         7,5, 7,10, 10,10, 10,5, 7,5)
     *     </code>
     * 
     * <pre/></li>
     * </ul>
     * <p>
     * SQL:
     * </p>
     * <code><pre>
     * MDSYS.SDO_GEOMETRY(
     *   2003,
     *   NULL,
     *   NULL,
     *   MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,1, 19,2003,1),
     *   MDSYS.SDO_ORDINATE_ARRAY(2,4, 4,3, 10,3, 13,5, 13,9, 11,13, 5,13, 2,11, 2,4,
     *       7,5, 7,10, 10,10, 10,5, 7,5)
     * )
     * </pre></code>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected Surface createPolygonWithHole() {
    	PointArray exRingPoints = gf.createPointArray();
    	exRingPoints.add(gf.createDirectPosition(new double[] {2, 4}));
    	exRingPoints.add(gf.createDirectPosition(new double[] {4, 3}));
    	exRingPoints.add(gf.createDirectPosition(new double[] {10, 3}));
    	exRingPoints.add(gf.createDirectPosition(new double[] {13, 5}));
    	exRingPoints.add(gf.createDirectPosition(new double[] {13, 9}));
    	exRingPoints.add(gf.createDirectPosition(new double[] {11, 13}));
    	exRingPoints.add(gf.createDirectPosition(new double[] {5, 13}));
    	exRingPoints.add(gf.createDirectPosition(new double[] {2, 11}));
    	exRingPoints.add(gf.createDirectPosition(new double[] {2, 4}));
    	Curve exCurve = gf.createCurve(exRingPoints);
    	List exCurves = new ArrayList<>();
    	exCurves.add(exCurve);
    	Ring exteriorRing = gf.createRing(exCurves);
    	
    	PointArray inRingPoints = gf.createPointArray();
    	inRingPoints.add(gf.createDirectPosition(new double[] {7, 5}));
    	inRingPoints.add(gf.createDirectPosition(new double[] {7, 10}));
    	inRingPoints.add(gf.createDirectPosition(new double[] {10, 10}));
    	inRingPoints.add(gf.createDirectPosition(new double[] {10, 5}));
    	inRingPoints.add(gf.createDirectPosition(new double[] {7, 5}));
    	Curve inCurve = gf.createCurve(inRingPoints);
    	List inCurves = new ArrayList<>();
    	inCurves.add(inCurve);
    	Ring interiorRing = gf.createRing(inCurves);
    	ArrayList interiorRings = new ArrayList();
    	interiorRings.add(interiorRing);
    	
    	SurfaceBoundary surfaceBoundary = gf.createSurfaceBoundary(exteriorRing, interiorRings);
    	Surface surface = gf.createSurface(surfaceBoundary);
        ((SurfaceImpl)surface).setUserData(-1); // don't have an SRID number
        return surface;
    }

    /**
     * Geometry Example "2.3.5 Point".
     * <p>
     * Simple Point used to test POINT_TYPE array use.
     * </p>
     * <code><pre>
     *   +   12,14
     * </pre></code>
     * <p>
     * Expected Encoding:
     * </p>
     * <ul>
     * <li><b>SDO_GTYPE:</b><code>2001</code><br/>
     * 2 dimensional, 0 measures, 01 for point</li>
     * <li><b>SDO_SRID:</b><code>NULL</code></li>
     * <li><b>SDO_POINT:</b><code>(12,14,NULL)</code></li>
     * <li><b>SDO_ELEM_INFO:</b><code>(1,1,1)</code></li>
     * <li><b>SDO_ORDINATES:</b><code>(12,14)</code></li>
     * </ul>
     * <p>
     * SQL:
     * </p>
     * <code><pre>
     * MDSYS.SDO_GEOMETRY(
     *   2001,
     *   NULL,
     *   MDSYS.SDO_POINT_TYPE(12, 14, NULL),
     *   NULL,
     *   NULL
     * )
     * </pre></code>
     */
    protected Point createPoint() {
        Point point = gf.createPoint(gf.createDirectPosition(new double[] { 12, 14 }));
        return point;
    }

    /**
     * LineString geometry for testing fixture. <code><pre>
     *        +4,7
     *        |
     *        |
     *        |
     * 1,2+   +4,2
     *    \   /
     *  2,1+-+3,1
     * </pre></code>
     * <p>
     * Expected Encoding:
     * </p>
     * <ul>
     * <li><b>SDO_GTYPE:</b><code>2002</code><br/>
     * 2 dimensional, 0 measures, 02 for Line</li>
     * <li><b>SDO_SRID:</b><code>NULL</code></li>
     * <li><b>SDO_POINT:</b><code>NULL</code></li>
     * <li><b>SDO_ELEM_INFO:</b><code>(1,2,5)</code></li>
     * <li><b>SDO_ORDINATES:</b><code>(1,2, 2,1, 3,1, 4,2 4,7)</code></li>
     * </ul>
     * <p>
     * SQL:
     * </p>
     * <code><pre>
     * MDSYS.SDO_GEOMETRY(
     *   2002,
     *   NULL,
     *   NULL,
     *   (1,2,5),
     *   (1,2, 2,1, 3,1, 4,2 4,7)
     * )
     * </pre></code>
     */
    protected Curve createLineString() {
    	PointArray points = gf.createPointArray();
    	points.add(gf.createDirectPosition(new double[] {1, 2}));
    	points.add(gf.createDirectPosition(new double[] {2, 1}));
    	points.add(gf.createDirectPosition(new double[] {3, 1}));
    	points.add(gf.createDirectPosition(new double[] {4, 2}));
    	points.add(gf.createDirectPosition(new double[] {4, 7}));
    	Curve curve = gf.createCurve(points);
    	
        return curve;
    }

    /**
     * MultiPoint geometry for testing fixture. <code><pre>
     * 
     *      5,5+
     * 
     *    3,3+
     * 
     *  2,2+
     * 1,1+
     * </pre></code>
     * <p>
     * Expected Encoding:
     * </p>
     * <ul>
     * <li><b>SDO_GTYPE:</b><code>2004</code><br/>
     * 2 dimensional, 0 measures, 05 for MultiPoint</li>
     * <li><b>SDO_SRID:</b><code>NULL</code></li>
     * <li><b>SDO_POINT:</b><code>NULL</code></li>
     * <li><b>SDO_ELEM_INFO:</b><code>(1,1,4)</code></li>
     * <li><b>SDO_ORDINATES:</b><code>(1,1, 2,2, 3,3, 5,5)</code></li>
     * </ul>
     * <p>
     * SQL:
     * </p>
     * <code><pre>
     * MDSYS.SDO_GEOMETRY(
     *   2005,
     *   NULL,
     *   NULL,
     *   (1,1,4),
     *   (1,1, 2,2, 3,3, 5,5)
     * )
     * </pre></code>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected MultiPoint createMultiPoint() {
        Point point1 = gf.createPoint(gf.createDirectPosition(new double[] { 1, 1 }));
        Point point2 = gf.createPoint(gf.createDirectPosition(new double[] { 2, 2 }));
        Point point3 = gf.createPoint(gf.createDirectPosition(new double[] { 3, 3 }));
        Point point4 = gf.createPoint(gf.createDirectPosition(new double[] { 5, 5 }));
        
        MultiPrimitive multiPrimitive = gf.createMultiPrimitive();
        Set elements = multiPrimitive.getElements();
        elements.add(point1);
        elements.add(point2);
        elements.add(point3);
        elements.add(point4);
        MultiPoint multiPoint = gf.createMultiPoint(elements);
        
        return multiPoint;
    }

    /**
     * MultiLineString geometry for testing fixture. <code><pre>
     *  2,7+==+==+5,7
     *        |4,7
     *        |
     *        |
     * 1,2+   +4,2
     *    \   /
     *  2,1+-+3,1
     * </pre></code>
     * <p>
     * Expected Encoding:
     * </p>
     * <ul>
     * <li><b>SDO_GTYPE:</b><code>2005</code><br/>
     * 2 dimensional, 0 measures, 05 for MultiLine</li>
     * <li><b>SDO_SRID:</b><code>NULL</code></li>
     * <li><b>SDO_POINT:</b><code>NULL</code></li>
     * <li><b>SDO_ELEM_INFO:</b><code>(1,2,1,11,2,1)</code><br/>
     * Two triplets
     * <ul>
     * <li>(1,2,1): linestring(2) of straight lines(1) starting at 1</li>
     * 
     * <li>(11,2,1): linestring(2) of straight lines(1) starting at 1</li>
     * </ul>
     * </li>
     * <li><b>SDO_ORDINATES:</b><code>(1,2, 2,1, 3,1, 4,2 4,7,
     *                                 2,7, 4,7, 5,7)</code></li>
     * </ul>
     * <p>
     * SQL:
     * </p>
     * <code><pre>
     * MDSYS.SDO_GEOMETRY(
     *   2005,
     *   NULL,
     *   NULL,
     *   (1,2,1,11,2,1),
     *   (1,2, 2,1, 3,1, 4,2 4,7, 2,7, 4,7, 5,7)
     * )
     * </pre></code>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected MultiCurve createMultiLineString() {
    	PointArray points1 = gf.createPointArray();
    	points1.add(gf.createDirectPosition(new double[] {1, 2}));
    	points1.add(gf.createDirectPosition(new double[] {2, 1}));
    	points1.add(gf.createDirectPosition(new double[] {3, 1}));
    	points1.add(gf.createDirectPosition(new double[] {4, 2}));
    	points1.add(gf.createDirectPosition(new double[] {4, 7}));
    	Curve curve1 = gf.createCurve(points1);
    	
    	PointArray points2 = gf.createPointArray();
    	points2.add(gf.createDirectPosition(new double[] {2, 7}));
    	points2.add(gf.createDirectPosition(new double[] {4, 7}));
    	points2.add(gf.createDirectPosition(new double[] {5, 7}));
    	Curve curve2 = gf.createCurve(points2);
    	
    	MultiPrimitive multiPrimitive = gf.createMultiPrimitive();
        Set elements = multiPrimitive.getElements();
    	elements.add(curve1);
    	elements.add(curve2);
    	MultiCurve multiCurve = gf.createMultiCurve(elements);

        return multiCurve;
    }

    /**
     * Construct a multipolyugon with a square and a triangle.
     * <p>
     * Used to illustrate multi polyugon encoding.
     * </p>
     * <code><pre>
     * 
     * 2,9+------+7,9   
     *    |      |      +11,8
     *    |      |     / \
     *    |      |    /   \
     *    |      |9,5-----+13,5
     *    |      |
     * 2,3+------+7,3
     * </pre></code>
     * <p>
     * A MultiPolygon with expected encoding:
     * </p>
     * <ul>
     * <li><b>SDO_GTYPE:</b><code>2006</code><br/>
     * 2 dimensional polygon, 6 for multi polygon</li>
     * <li><b>SDO_SRID:</b><code>NULL</code></li>
     * <li><b>SDO_POINT:</b>NULL></li>
     * <li><b>SDO_ELEM_INFO:</b><code>(1,1003,1,11,1003,1)</code><br/>
     * Three triplets
     * <ul>
     * <li>(1,1003,1): exterior(1000) polygon(3) starting at 1 with straight edges(1)</li>
     * 
     * <li>(11,1003,1): exterior(1000) polygon(3) starting at 11 with straight edges(1)</li>
     * </ul>
     * </li>
     * <li><b>SDO_ORDINATES:</b> <code><pre>
     *        (2,3, 7,3, 7,9, 2,9, 2,3,
     *         9,5, 13,5, 11,5, 9,5)
     *     </code>
     * 
     * <pre/></li>
     * </ul>
     * <p>
     * SQL:
     * </p>
     * <code><pre>
     * MDSYS.SDO_GEOMETRY(
     *   2006,
     *   NULL,
     *   NULL,
     *   MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,1, 11,1003,1),
     *   MDSYS.SDO_ORDINATE_ARRAY(2,3, 7,3, 7,9, 2,9, 2,3,
     *         9,5, 13,5, 11,5, 9,5)
     * )
     * </pre></code>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected MultiSurface createMultiPolygon() {
        PointArray points1 = gf.createPointArray();
    	points1.add(gf.createDirectPosition(new double[] {2, 3}));
    	points1.add(gf.createDirectPosition(new double[] {7, 3}));
    	points1.add(gf.createDirectPosition(new double[] {7, 9}));
    	points1.add(gf.createDirectPosition(new double[] {2, 9}));
    	points1.add(gf.createDirectPosition(new double[] {2, 3}));
    	SurfaceBoundary surfaceBoundary1 = gf.createSurfaceBoundary(points1);
    	Surface surface1 = gf.createSurface(surfaceBoundary1);
        
        PointArray points2 = gf.createPointArray();
    	points2.add(gf.createDirectPosition(new double[] {9, 5}));
    	points2.add(gf.createDirectPosition(new double[] {13, 5}));
    	points2.add(gf.createDirectPosition(new double[] {11, 8}));
    	points2.add(gf.createDirectPosition(new double[] {9, 5}));
    	SurfaceBoundary surfaceBoundary2 = gf.createSurfaceBoundary(points2);
    	Surface surface2 = gf.createSurface(surfaceBoundary2);
        
    	MultiPrimitive multiPrimitive = gf.createMultiPrimitive();
        Set elements = multiPrimitive.getElements();
    	elements.add(surface1);
    	elements.add(surface2);
    	MultiSurface multiSurface = gf.createMultiSurface(elements);
        
        return multiSurface;
    }

    /**
     * Construct a multipolyugon with a square with a hole and a triangle.
     * <p>
     * Used to illustrate multi polyugon encoding.
     * </p>
     * <code><pre>
     * 
     * 2,9+-------+7,9   
     *    |3,8 6,8|        +11,8
     *    | +---+ |      / \
     *    | |  /  |     /   \
     *    | | /   |    /     \
     *    | +     |9,5+-------+13,5
     *    |3,4    |
     * 2,3+-------+7,3
     * </pre></code>
     * <p>
     * A MultiPolygon with expected encoding:
     * </p>
     * <ul>
     * <li><b>SDO_GTYPE:</b><code>2007</code><br/>
     * 2 dimensional, 6 for multipolygon</li>
     * <li><b>SDO_SRID:</b><code>0</code></li>
     * <li><b>SDO_POINT:</b>NULL></li>
     * <li><b>SDO_ELEM_INFO:</b><code>(1,1003,1,11,2003,1,19,1003,1)</code><br/>
     * Two triplets
     * <ul>
     * <li>(1,1003,1): exterior(1000) polygon(3) starting at 1 with straight edges(1)
     * <ul>
     * <li>(1,2003,1): interior(2000) polygon(3) starting at 11 with straight edges(1)</li>
     * </ul>
     * </li>
     * 
     * <li>(11,1003,1): exterior(1000) polygon(3) starting at 19 with straight edges(1)</li>
     * </ul>
     * </li>
     * <li><b>SDO_ORDINATES:</b> <code><pre>
     *        (2,3, 7,3, 7,9, 2,9, 2,3,
     *         3,4, 3,8, 6,8, 3,4,
     *         9,5, 13,5, 11,8, 9,5)
     *     </code>
     * 
     * <pre/></li>
     * </ul>
     * <p>
     * SQL:
     * </p>
     * <code><pre>
     * MDSYS.SDO_GEOMETRY(
     *   2006,
     *   NULL,
     *   NULL,
     *   MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,1,11,2003,1,19,1003,1),
     *   MDSYS.SDO_ORDINATE_ARRAY(2,3, 7,3, 7,9, 2,9, 2,3,
     *         3,4, 3,8, 6,8, 3,4, 
     *         9,5, 13,5, 11,8, 9,5)
     * )
     * </pre></code>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected MultiSurface createMultiPolygonWithHole() {
    	PointArray exRingPoints = gf.createPointArray();
    	exRingPoints.add(gf.createDirectPosition(new double[] {2, 3}));
    	exRingPoints.add(gf.createDirectPosition(new double[] {7, 3}));
    	exRingPoints.add(gf.createDirectPosition(new double[] {7, 9}));
    	exRingPoints.add(gf.createDirectPosition(new double[] {2, 9}));
    	exRingPoints.add(gf.createDirectPosition(new double[] {2, 3}));
    	Curve exCurve = gf.createCurve(exRingPoints);
    	List exCurves = new ArrayList<>();
    	exCurves.add(exCurve);
    	Ring exteriorRing = gf.createRing(exCurves);
    	
    	PointArray inRingPoints = gf.createPointArray();
    	inRingPoints.add(gf.createDirectPosition(new double[] {3, 4}));
    	inRingPoints.add(gf.createDirectPosition(new double[] {6, 8}));
    	inRingPoints.add(gf.createDirectPosition(new double[] {3, 8}));
    	inRingPoints.add(gf.createDirectPosition(new double[] {3, 4}));
    	Curve inCurve = gf.createCurve(inRingPoints);
    	List inCurves = new ArrayList<>();
    	inCurves.add(inCurve);
    	Ring interiorRing = gf.createRing(inCurves);
    	ArrayList interiorRings = new ArrayList();
    	interiorRings.add(interiorRing);
    	
    	SurfaceBoundary surfaceBoundary = gf.createSurfaceBoundary(exteriorRing, interiorRings);
    	Surface surface1 = gf.createSurface(surfaceBoundary);
    	Surface surface2 = createPolygon();
        
    	MultiPrimitive multiPrimitive = gf.createMultiPrimitive();
        Set elements = multiPrimitive.getElements();
    	elements.add(surface1);
    	elements.add(surface2);
        MultiSurface multiSurface = gf.createMultiSurface(elements);
    	
        return multiSurface;
    }

    /**
     * General Geometry Collection - with point, line, polygon, and a polygonWithHole. <code><pre>
     *                          
     *                5,5+-------+9,5
     *                   |  +6,4/
     *                   | /|  /
     *                   |/ | / 
     *  2,3 +---+3,3  5,3+--+/6,3
     *      |2,2|        |  /
     * 1,2+ +---+3,2     | / 
     *     \             |/
     * 1,1+ +2,1      5,1+
     * </pre></code> A GeometryCollection with expected encoding:</p>
     * <ul>
     * <li><b>SDO_GTYPE:</b><code>2004</code><br/>
     * 2000 dimensional polygon, 000 for no LRS, 4 for geometry collection</li>
     * <li><b>SDO_SRID:</b><code>0</code></li>
     * <li><b>SDO_POINT:</b>NULL></li>
     * <li><b>SDO_ELEM_INFO:</b> <code>(1,1,1, 3,2,1, 7,1003,1, 15,1003,1, 23,2003,1)</code><br/>
     * Two triplets
     * <ul>
     * <li>(1,1,1): starting at 1, a point(1) (single(1))</li>
     * <li>(3,2,1): starting at 3, a line(2) with straight segments(1)</li>
     * <li>(7,1003,1): starting at 5, an exterior(1000), polygon(3)</li>
     * 
     * <li>(15,1003,1, 23,2003,1) polygon with:
     * <ul>
     * <li>starting at 15 and exterior(1003) and straight edges 1</li>
     * <li>starting at 23 and interior(2003) and straight edges 1</li>
     * </ul>
     * </li>
     * </ul>
     * <li><b>SDO_ORDINATES:</b> <code><pre>
     *        (1,1,
     *         1,2, 2,1,
     *         2,2, 3,2, 3,3, 2,3, 2,2
     *         5,1, 5,5, 9,5, 5,1,
     *         5,3, 6,4, 6,3, 5,3)
     *     </code>
     * 
     * <pre/></li>
     * </ul>
     * <p>
     * SQL:
     * </p>
     * <code><pre>
     * MDSYS.SDO_GEOMETRY(
     *   2004,
     *   NULL,
     *   NULL,
     *   MDSYS.SDO_ELEM_INFO_ARRAY(1,1,1, 3,2,1, 7,1003,1, 17,1003,1, 25,2003,1),
     *   MDSYS.SDO_ORDINATE_ARRAY(
     *         1,1,
     *         1,2, 2,1,
     *         2,2, 3,2, 3,3, 2,3, 2,2,
     *         5,1, 5,5, 9,5, 5,1,
     *         5,3, 6,4, 6,3, 5,3
     *   )
     * )
     * </pre></code>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected MultiPrimitive createGeometryCollection() {
    	Point point = gf.createPoint(gf.createDirectPosition(new double[] { 1, 1 }));
    	
    	PointArray points = gf.createPointArray();
    	points.add(gf.createDirectPosition(new double[] {1, 2}));
    	points.add(gf.createDirectPosition(new double[] {2, 1}));
    	Curve curve = gf.createCurve(points);
    	
    	points = gf.createPointArray();
    	points.add(gf.createDirectPosition(new double[] {2, 2}));
    	points.add(gf.createDirectPosition(new double[] {3, 2}));
    	points.add(gf.createDirectPosition(new double[] {3, 3}));
    	points.add(gf.createDirectPosition(new double[] {2, 3}));
    	points.add(gf.createDirectPosition(new double[] {2, 2}));
    	SurfaceBoundary surfaceBoundary = gf.createSurfaceBoundary(points);
    	Surface surface = gf.createSurface(surfaceBoundary);
    	
    	PointArray exRingPoints = gf.createPointArray();	
    	exRingPoints.add(gf.createDirectPosition(new double[] {5, 1}));
    	exRingPoints.add(gf.createDirectPosition(new double[] {9, 5}));
    	exRingPoints.add(gf.createDirectPosition(new double[] {5, 5}));
    	exRingPoints.add(gf.createDirectPosition(new double[] {5, 1}));
    	Curve exCurve = gf.createCurve(exRingPoints);
    	List exCurves = new ArrayList<>();
    	exCurves.add(exCurve);
    	Ring exteriorRing = gf.createRing(exCurves);
    	
    	PointArray inRingPoints = gf.createPointArray();	
    	inRingPoints.add(gf.createDirectPosition(new double[] {5, 3}));
    	inRingPoints.add(gf.createDirectPosition(new double[] {6, 4}));
    	inRingPoints.add(gf.createDirectPosition(new double[] {6, 3}));
    	inRingPoints.add(gf.createDirectPosition(new double[] {5, 3}));
    	Curve inCurve = gf.createCurve(inRingPoints);
    	List inCurves = new ArrayList<>();
    	inCurves.add(inCurve);
    	Ring interiorRing = gf.createRing(inCurves);
    	ArrayList interiorRings = new ArrayList();
    	interiorRings.add(interiorRing);
    	
    	SurfaceBoundary surfaceBoundarywithhole = gf.createSurfaceBoundary(exteriorRing, interiorRings);
    	Surface surfacewithhole = gf.createSurface(surfaceBoundarywithhole);
    	
    	MultiPrimitive multiPrimitive = gf.createMultiPrimitive();
        Set elements = multiPrimitive.getElements();
    	elements.add(point);
    	elements.add(curve);
    	elements.add(surface);
    	elements.add(surfacewithhole);
    	
    	return gf.createMultiPrimitive(elements);
    }


    protected Solid createSolid() {
    	Hints hints = GeoTools.getDefaultHints();
        hints.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D);
        hints.put(Hints.GEOMETRY_VALIDATE, false);
        ISOGeometryBuilder builder = new ISOGeometryBuilder(hints);

        DirectPosition position1 = builder.createDirectPosition(new double[] { 2,0,2 });
        DirectPosition position2 = builder.createDirectPosition(new double[] { 4,0,2 });
        DirectPosition position3 = builder.createDirectPosition(new double[] { 4,0,4 });
        DirectPosition position4 = builder.createDirectPosition(new double[] { 2,0,4 });
        DirectPosition position5 = builder.createDirectPosition(new double[] { 2,2,2 });
        DirectPosition position6 = builder.createDirectPosition(new double[] { 4,2,2 });
        DirectPosition position7 = builder.createDirectPosition(new double[] { 4,2,4 });
        DirectPosition position8 = builder.createDirectPosition(new double[] { 2,2,4 });
        
        List<DirectPosition> dps1 = new ArrayList<DirectPosition>();
        dps1.add(position7);
        dps1.add(position2);
        dps1.add(position1);
        dps1.add(position5);
        dps1.add(position7);
        
        List<DirectPosition> dps2 = new ArrayList<DirectPosition>();
        dps2.add(position4);
        dps2.add(position3);
        dps2.add(position7);
        dps2.add(position8);
        dps2.add(position4);

        List<DirectPosition> dps3 = new ArrayList<DirectPosition>();
        dps3.add(position2);
        dps3.add(position6);
        dps3.add(position7);
        dps3.add(position3);
        dps3.add(position2);

        List<DirectPosition> dps4 = new ArrayList<DirectPosition>();
        dps4.add(position8);
        dps4.add(position5);
        dps4.add(position1);
        dps4.add(position4);
        dps4.add(position8);

        List<DirectPosition> dps5 = new ArrayList<DirectPosition>();
        dps5.add(position3);
        dps5.add(position4);
        dps5.add(position1);
        dps5.add(position2);
        dps5.add(position3);

        List<DirectPosition> dps6 = new ArrayList<DirectPosition>();
        dps6.add(position5);
        dps6.add(position8);
        dps6.add(position7);
        dps6.add(position6);
        dps6.add(position5);
        
        PrimitiveFactoryImpl pmFF = (PrimitiveFactoryImpl) builder.getPrimitiveFactory();
        
        Surface surface1 = pmFF.createSurfaceByDirectPositions(dps1);
        Surface surface2 = pmFF.createSurfaceByDirectPositions(dps2);
        Surface surface3 = pmFF.createSurfaceByDirectPositions(dps3);
        Surface surface4 = pmFF.createSurfaceByDirectPositions(dps4);
        Surface surface5 = pmFF.createSurfaceByDirectPositions(dps5);
        Surface surface6 = pmFF.createSurfaceByDirectPositions(dps6);
        
        List<OrientableSurface> surfaces = new ArrayList<OrientableSurface>();
        surfaces.add(surface1);
        surfaces.add(surface2);
        surfaces.add(surface3);
        surfaces.add(surface4);
        surfaces.add(surface5);
        surfaces.add(surface6);

        Shell exteriorShell = builder.createShell(surfaces);
        List<Shell> interiors = new ArrayList<Shell>();

        SolidBoundary solidBoundary = builder.createSolidBoundary(exteriorShell, interiors);
        Solid solid = builder.createSolid(solidBoundary);

        return solid;
    }
}
