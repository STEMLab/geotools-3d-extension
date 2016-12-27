/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2015, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gml2.iso.simple;

import org.geotools.gml2.iso.GML;
import org.geotools.xml.Encoder;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.Ring;
import org.xml.sax.helpers.AttributesImpl;
/**
 * Encodes a GML2 multi line string
 * 
 * @author Justin Deoliveira, OpenGeo
 * @author Andrea Aime - GeoSolutions
 */

class MultiLineStringEncoder extends GeometryEncoder<MultiCurve> {

    static final QualifiedName MULTI_LINE_STRING = new QualifiedName(GML.NAMESPACE,
            "MultiLineString", "gml");

    static final QualifiedName LINE_STRING_MEMBER = new QualifiedName(GML.NAMESPACE,
            "lineStringMember", "gml");

    LineStringEncoder lse;

    LinearRingEncoder lre;

    QualifiedName multiLineString;

    QualifiedName lineStringMember;

    protected MultiLineStringEncoder(Encoder encoder, String gmlPrefix) {
        super(encoder);
        lse = new LineStringEncoder(encoder, gmlPrefix);
        lre = new LinearRingEncoder(encoder, gmlPrefix);
        multiLineString = MULTI_LINE_STRING.derive(gmlPrefix);
        lineStringMember = LINE_STRING_MEMBER.derive(gmlPrefix);
    }

    @Override
    public void encode(MultiCurve geometry, AttributesImpl atts, GMLWriter handler)
            throws Exception {
        handler.startElement(multiLineString, atts);

        for (OrientableCurve c : geometry.getElements()) {
            handler.startElement(lineStringMember, null);
            if (c instanceof Ring) {
                lre.encode((Ring) c, null, handler);
            } else {
                lse.encode((Curve) c, null, handler);
            }
            handler.endElement(lineStringMember);
        }

        handler.endElement(multiLineString);
    }

}