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
import org.geotools.xml.Encoder;
import org.opengis.geometry.complex.CompositeSurface;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Surface;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Encodes a GML3 multi polygon
 * 
 * @author Justin Deoliveira, OpenGeo
 * @author Andrea Aime - GeoSolutions
 */
class CompositeSurfaceEncoder extends GeometryEncoder<CompositeSurface> {

    static final QualifiedName COMPOSITE_SURFACE = new QualifiedName(GML.NAMESPACE, "CompositeSurface",
            "gml");

    static final QualifiedName SURFACE_MEMBER = new QualifiedName(GML.NAMESPACE, "surfaceMember",
            "gml");

    QualifiedName compositeSurface;

    QualifiedName surfaceMember;

    PolygonEncoder pe;

    protected CompositeSurfaceEncoder(Encoder encoder, String gmlPrefix, String gmlUri) {
        super(encoder);
        pe = new PolygonEncoder(encoder, gmlPrefix, gmlUri);
        compositeSurface = COMPOSITE_SURFACE.derive(gmlPrefix, gmlUri);
        surfaceMember = SURFACE_MEMBER.derive(gmlPrefix, gmlUri);
    }

    @Override
    public void encode(CompositeSurface geometry, AttributesImpl atts, GMLWriter handler)
            throws Exception {
        handler.startElement(compositeSurface, atts);

        for(OrientableSurface s : geometry.getGenerators()) {
            Surface surface = (Surface) s;
            handler.startElement(surfaceMember, null);
            pe.encode(surface, null, handler);
            handler.endElement(surfaceMember);
        }

        handler.endElement(compositeSurface);
    }
    

}