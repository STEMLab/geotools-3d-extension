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
package org.geotools.gml2.bindings;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.gml2.GML;
import org.geotools.gml2.TEST;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.aggregate.Aggregate;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Collection of static methods for creating mock data for binding unit tests.
 *
 * @author Justin Deoliveira, The Open Planning Project
 *
 *
 *
 *
 * @source $URL$
 */
public class GML2MockData {
    /** factory used to create geometries */
    static GeometryBuilder gb = new GeometryBuilder(DefaultGeographicCRS.WGS84_3D);

    //
    //Geometries
    //
    static DirectPosition coordinate() {
        return gb.createDirectPosition(new double[] {1, 2, 3});
    }

    static Element coordinate(Document document, Node parent) {
        Element coord = element(GML.coord, document, parent);

        Element x = element(new QName(GML.NAMESPACE, "X"), document, coord);
        x.appendChild(document.createTextNode("1.0"));

        Element y = element(new QName(GML.NAMESPACE, "Y"), document, coord);
        y.appendChild(document.createTextNode("2.0"));
        
        Element z = element(new QName(GML.NAMESPACE, "Z"), document, coord);
        z.appendChild(document.createTextNode("3.0"));

        return coord;
    }

    static PointArray coordinates() {
    	PointArray pa = gb.createPointArray();
    	pa.add(
    			gb.createDirectPosition( new double[] {1, 2, 3}));
    	pa.add(
    			gb.createDirectPosition( new double[] {3, 4, 5}));
    	return pa;
    }

    static Element coordinates(Document document, Node parent) {
        Element coordinates = element(GML.coordinates, document, parent);
        coordinates.appendChild(document.createTextNode("1.0,2.0,3.0 3.0,4.0,5.0"));

        return coordinates;
    }

    static Element boundedBy(Document document, Node parent) {
        Element boundedBy = element(GML.boundedBy, document, parent);

        box(document, boundedBy);

        return boundedBy;
    }

    static Element boundedByWithNull(Document document, Node parent) {
        Element boundedBy = element(GML.boundedBy, document, parent);

        nil(document, boundedBy);

        return boundedBy;
    }

    static Element box(Document document, Node parent) {
        Element box = element(GML.Box, document, parent);

        coordinate(document, box);
        coordinate(document, box);

        return box;
    }

    static Element nil(Document document, Node parent) {
        return element(new QName(GML.NAMESPACE, "null"), document, parent);
    }

    static Point point() {
        return gb.createPoint(coordinate());
    }

    static Element point(Document document, Node parent) {
        Element point = element(GML.Point, document, parent);

        coordinate(document, point);

        return point;
    }

    static Element pointProperty(Document document, Node parent) {
        Element pointProperty = element(GML.pointProperty, document, parent);

        point(document, pointProperty);

        return pointProperty;
    }

    static Curve lineString() {
        return gb.createCurve(coordinates());
    }

    static Element lineString(Document document, Node parent) {
        Element lineString = element(GML.LineString, document, parent);

        coordinates(document, lineString);

        return lineString;
    }

    static Ring linearRing() {
    	PointArray pa = gb.createPointArray();
    	pa.add(
    			gb.createDirectPosition( new double[] {1, 1, 1}));
    	pa.add(
    			gb.createDirectPosition( new double[] {2, 2, 2}));
    	pa.add(
    			gb.createDirectPosition( new double[] {3, 3, 3}));
    	pa.add(
    			gb.createDirectPosition( new double[] {1, 1, 1}));
    	Curve c = gb.createCurve(pa);
    	return gb.createRing(Arrays.asList(c));
    }

    static Element lineStringProperty(Document document, Node parent) {
        Element property = element(GML.lineStringProperty, document, parent);

        lineString(document, property);

        return property;
    }

    static Element linearRing(Document document, Node parent) {
        Element linearRing = element(GML.LinearRing, document, parent);

        Element coordinates = element(GML.coordinates, document, linearRing);
        coordinates.appendChild(document.createTextNode("1.0,2.0,3.0 3.0,4.0,5.0 5.0,6.0,7.0 1.0,2.0,3.0"));

        return linearRing;
    }

    static Surface polygon() {
    	SurfaceBoundary sb = gb.createSurfaceBoundary(linearRing());
        return gb.createSurface(sb);
    }

    static Element polygon(Document document, Node parent) {
        Element polygon = element(GML.Polygon, document, parent);

        Element exterior = element(GML.outerBoundaryIs, document, polygon);
        linearRing(document, exterior);

        return polygon;
    }

    static Element polygonProperty(Document document, Node parent) {
        Element property = element(GML.polygonProperty, document, parent);

        polygon(document, property);

        return property;
    }

    static MultiPoint multiPoint() {
    	Set<Point> pSet = new HashSet<Point>(Arrays.asList(
    			gb.createPoint(1, 1, 1),
    			gb.createPoint(2, 2, 2)
    			));
        return gb.createMultiPoint(pSet);
    }

    static Element multiPoint(Document document, Node parent) {
        Element multiPoint = element(GML.MultiPoint, document, parent);

        // 2 pointMember elements
        Element pointMember = element(GML.pointMember, document, multiPoint);
        point(document, pointMember);

        pointMember = element(GML.pointMember, document, multiPoint);
        point(document, pointMember);

        return multiPoint;
    }

    static CoordinateReferenceSystem crs( String srid ) {
        try {
            return CRS.decode( "EPSG:4326" );
        } 
        catch (Exception e) {
            throw new RuntimeException( e );
        }
    }
    
    static Element multiPointProperty(Document document, Node parent) {
        Element multiPointProperty = element(GML.multiPointProperty, document, parent);
        multiPoint(document, multiPointProperty);

        return multiPointProperty;
    }

    static MultiCurve multiLineString() {
    	Set<Curve> lSet = new HashSet<Curve>(Arrays.asList(
    			lineString(), 
    			lineString()
    			));
        return gb.createMultiCurve(lSet);
    }

    static Element multiLineString(Document document, Node parent) {
        Element multiLineString = element(GML.MultiLineString, document, parent);

        Element lineStringMember = element(GML.lineStringMember, document, multiLineString);
        lineString(document, lineStringMember);

        lineStringMember = element(GML.lineStringMember, document, multiLineString);
        lineString(document, lineStringMember);

        return multiLineString;
    }

    static Element multiLineStringProperty(Document document, Node parent) {
        Element multiLineStringProperty = element(GML.multiLineStringProperty, document, parent);
        multiLineString(document, multiLineStringProperty);

        return multiLineStringProperty;
    }

    static MultiSurface multiPolygon() {
    	Set<Surface> sSet = new HashSet<Surface>(Arrays.asList(
    			polygon(), 
    			polygon()
    			));
        return gb.createMultiSurface(sSet);
    }

    static Element multiPolygon(Document document, Node parent) {
        Element multiPolygon = element(GML.MultiPolygon, document, parent);

        Element polygonMember = element(GML.polygonMember, document, multiPolygon);
        polygon(document, polygonMember);

        polygonMember = element(GML.polygonMember, document, multiPolygon);
        polygon(document, polygonMember);

        return multiPolygon;
    }

    static Element multiPolygonProperty(Document document, Node parent) {
        Element multiPolygonProperty = element(GML.multiPolygonProperty, document, parent);
        multiPolygon(document, multiPolygonProperty);

        return multiPolygonProperty;
    }

    static MultiPrimitive multiGeometry() {
    	Set<Primitive> pSet = new HashSet<Primitive>(Arrays.asList(
    			point(), 
    			lineString(), 
    			polygon()
    			));
    	return gb.createMultiPrimitive(pSet);
    }
    //
    // features
    //
    static Element feature(Document document, Node parent) {
        Element feature = element(TEST.TestFeature, document, parent);
        Element geom = element(new QName(TEST.NAMESPACE, "geom"), document, feature);
        point(document, geom);

        Element count = element(new QName(TEST.NAMESPACE, "count"), document, feature);
        count.appendChild(document.createTextNode("1"));

        return feature;
    }

    static SimpleFeature feature() throws Exception {
        SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
        typeBuilder.setName(TEST.TestFeature.getLocalPart());
        typeBuilder.setNamespaceURI(TEST.TestFeature.getNamespaceURI());

        typeBuilder.add("name", String.class);
        typeBuilder.add("description", String.class);
        typeBuilder.add("geom", Point.class);
        typeBuilder.add("count", Integer.class);
        typeBuilder.add("date", Date.class);

        SimpleFeatureType type = (SimpleFeatureType) typeBuilder.buildFeatureType();

        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
        builder.add("theName");
        builder.add("theDescription");
        builder.add(point());
        builder.add(new Integer(1));
        builder.add(new Date());

        return (SimpleFeature) builder.buildFeature("fid.1");
    }

    static Element featureMember(Document document, Node parent) {
        Element featureMember = element(GML.featureMember, document, parent);
        feature(document, featureMember);

        return featureMember;
    }

    static Element element(QName name, Document document, Node parent) {
        Element element = document.createElementNS(name.getNamespaceURI(), name.getLocalPart());

        if (parent != null) {
            parent.appendChild(element);
        }

        return element;
    }
}
