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
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.xml.sax.helpers.AttributesImpl;

import com.vividsolutions.jts.geom.Polygon;

/**
 * Encodes a GML2 polygon
 * 
 * @author Justin Deoliveira, OpenGeo
 * @author Andrea Aime - GeoSolutions
 */
class PolygonEncoder extends GeometryEncoder<Surface> {

    static final QualifiedName POLYGON = new QualifiedName(GML.NAMESPACE, "Polygon", "gml");

    static final QualifiedName OUTER_BOUNDARY = new QualifiedName(GML.NAMESPACE, "outerBoundaryIs",
            "gml");

    static final QualifiedName INNER_BOUNDARY = new QualifiedName(GML.NAMESPACE, "innerBoundaryIs",
            "gml");

    QualifiedName polygon;

    QualifiedName outerBoundary;

    QualifiedName innerBoundary;

    LinearRingEncoder lre;

    protected PolygonEncoder(Encoder encoder, String gmlPrefix) {
        super(encoder);
        lre = new LinearRingEncoder(encoder, gmlPrefix);
        polygon = POLYGON.derive(gmlPrefix);
        outerBoundary = OUTER_BOUNDARY.derive(gmlPrefix);
        innerBoundary = INNER_BOUNDARY.derive(gmlPrefix);
    }

    @Override
    public void encode(Surface geometry, AttributesImpl atts, GMLWriter handler) throws Exception {
        handler.startElement(polygon, atts);

        handler.startElement(outerBoundary, null);
        SurfaceBoundary sb = geometry.getBoundary();
        lre.encode(sb.getExterior(), null, handler);
        handler.endElement(outerBoundary);

        for (Ring r: sb.getInteriors()) {
            handler.startElement(innerBoundary, null);
            lre.encode(r, null, handler);
            handler.endElement(innerBoundary);
        }

        handler.endElement(polygon);
    }

}