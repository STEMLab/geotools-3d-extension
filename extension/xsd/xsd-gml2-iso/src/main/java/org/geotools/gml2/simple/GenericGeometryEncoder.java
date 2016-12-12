/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2016, Open Source Geospatial Foundation (OSGeo). 
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
package org.geotools.gml2.simple;

import org.geotools.gml2.simple.GMLWriter;
import org.geotools.gml2.simple.GeometryEncoder;
import org.geotools.xml.Encoder;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Helper class that encodes the geometries within GeometryCollection
 * 
 * @author 
 */
public class GenericGeometryEncoder extends GeometryEncoder<Geometry> {

    Encoder encoder;
    String gmlPrefix;

    public GenericGeometryEncoder(Encoder encoder) {
        super(encoder);
        this.encoder = encoder;
    }

 /**
 *
 * @param encoder
 * @param gmlPrefix
 */
    public GenericGeometryEncoder(Encoder encoder, String gmlPrefix) {
        super(encoder);
        this.encoder = encoder;
        this.gmlPrefix = gmlPrefix;
    }

    @Override
    public void encode(Geometry geometry, AttributesImpl atts, GMLWriter handler) throws Exception {
        if (geometry instanceof Curve) {
            LineStringEncoder lineString = new LineStringEncoder(encoder,
                LineStringEncoder.LINE_STRING);
            lineString.encode((Curve) geometry, atts, handler);
        } else if (geometry instanceof Point) {
            PointEncoder pt = new PointEncoder(encoder, gmlPrefix == null? "gml": gmlPrefix);
            pt.encode((Point) geometry, atts, handler);
        } else if (geometry instanceof Surface) {
            PolygonEncoder polygon = new PolygonEncoder(encoder, gmlPrefix);
            polygon.encode((Surface) geometry, atts, handler);
        } else if (geometry instanceof MultiCurve) {
            MultiLineStringEncoder multiLineString = new MultiLineStringEncoder(
                                                        encoder, gmlPrefix);
            multiLineString.encode((MultiCurve) geometry, atts, handler);
        } else if (geometry instanceof MultiPoint) {
            MultiPointEncoder multiPoint = new MultiPointEncoder(encoder,
                                            gmlPrefix);
            multiPoint.encode((MultiPoint) geometry, atts, handler);
        } else if (geometry instanceof MultiSurface) {
            MultiPolygonEncoder multiPolygon = new MultiPolygonEncoder(encoder,
                                                gmlPrefix);
            multiPolygon.encode((MultiSurface) geometry, atts, handler);
        } else if (geometry instanceof Ring) {
            LinearRingEncoder linearRing = new LinearRingEncoder(encoder,
                                                gmlPrefix);
            linearRing.encode((Ring) geometry, atts, handler);
        } else {
            throw new Exception("Unsupported geometry " + geometry.toString());
        }
    }
}

