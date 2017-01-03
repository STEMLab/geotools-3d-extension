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
import org.geotools.gml3.iso.simple.LinearRingEncoder;
import org.geotools.gml3.iso.simple.RingEncoder;
import org.geotools.xml.Encoder;
import org.opengis.geometry.complex.CompositeSurface;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Encodes a GML3 polygon
 * 
 * @author Justin Deoliveira, OpenGeo
 * @author Andrea Aime - GeoSolutions
 */
class SolidEncoder extends GeometryEncoder<Solid> {
    static final QualifiedName SOLID = new QualifiedName(GML.NAMESPACE, "Solid", "gml");

    static final QualifiedName EXTERIOR = new QualifiedName(GML.NAMESPACE, "exterior", "gml");

    static final QualifiedName INTERIOR = new QualifiedName(GML.NAMESPACE, "interior", "gml");

    QualifiedName solid;

    QualifiedName exterior;

    QualifiedName interior;

    CompositeSurfaceEncoder cse;

    RingEncoder re;

    protected SolidEncoder(Encoder encoder, String gmlPrefix, String gmlUri) {
        super(encoder);
        solid = SOLID.derive(gmlPrefix, gmlUri);
        exterior = EXTERIOR.derive(gmlPrefix, gmlUri);
        interior = INTERIOR.derive(gmlPrefix, gmlUri);
        cse = new CompositeSurfaceEncoder(encoder, gmlPrefix, gmlUri);
        //re = new RingEncoder(encoder, gmlPrefix, gmlUri);
    }
    
    @Override
    public void encode(Solid geometry, AttributesImpl atts, GMLWriter handler)
            throws Exception {
        handler.startElement(solid, atts);
        
        SolidBoundary boundary = geometry.getBoundary();
        
        handler.startElement(exterior, null);
        encodeCompositeSurface(boundary.getExterior(), handler);
        handler.endElement(exterior);
        
        for ( int i = 0; i < boundary.getInteriors().length; i++ ) {
            handler.startElement(interior, null);
            encodeCompositeSurface(boundary.getInteriors()[i], handler);
            handler.endElement(interior);
        }
        
        handler.endElement(solid);
    }

    private void encodeCompositeSurface(CompositeSurface compositeSurface, GMLWriter handler) throws Exception {
        /*if (ring instanceof CurvedGeometry) {
            re.encode(ring, null, handler);
        } else {
            lre.encode(ring, null, handler);
        }*/
    	cse.encode(compositeSurface, null, handler);
    }
    
}