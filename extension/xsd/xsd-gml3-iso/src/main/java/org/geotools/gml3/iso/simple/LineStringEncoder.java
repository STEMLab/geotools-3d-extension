/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2015 - 2016, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gml3.iso.simple;

import org.geotools.gml2.iso.simple.GMLWriter;
import org.geotools.gml2.iso.simple.GeometryEncoder;
import org.geotools.gml2.iso.simple.QualifiedName;
import org.geotools.gml3.iso.GML;
import org.geotools.gml3.iso.bindings.GML3EncodingUtils;
import org.geotools.xml.Encoder;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.primitive.Curve;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Encodes a GML3 line string
 * 
 * @author Justin Deoliveira, OpenGeo
 * @author Andrea Aime - GeoSolutions
 */
class LineStringEncoder extends GeometryEncoder<Curve> {

    static final QualifiedName LINE_STRING = new QualifiedName(GML.NAMESPACE, "LineString", "gml");

    QualifiedName element;

    protected LineStringEncoder(Encoder encoder, String gmlPrefix, String gmlUri) {
        this(encoder, LINE_STRING.derive(gmlPrefix, gmlUri));
    }

    protected LineStringEncoder(Encoder encoder, QualifiedName element) {
        super(encoder);
        this.element = element;
    }

    public void encode(Curve geometry, AttributesImpl atts, GMLWriter handler)
            throws Exception {
        handler.startElement(element, atts);
        ISOGeometryBuilder builder = new ISOGeometryBuilder(geometry.getCoordinateReferenceSystem());
        handler.posList(GML3EncodingUtils.positions(geometry, builder));
        handler.endElement(element);
    }
}