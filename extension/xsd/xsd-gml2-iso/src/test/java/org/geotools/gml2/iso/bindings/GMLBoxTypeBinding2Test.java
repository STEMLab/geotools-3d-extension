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
package org.geotools.gml2.iso.bindings;

import org.geotools.gml2.iso.GML;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.Binding;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.ISOGeometryBuilder;
import org.w3c.dom.Document;


/**
 * 
 *
 * @source $URL$
 */
public class GMLBoxTypeBinding2Test extends GMLTestSupport {
    public void testType() {
        assertEquals(Envelope.class, binding(GML.BoxType).getType());
    }

    public void testExecutionMode() {
        assertEquals(Binding.OVERRIDE, binding(GML.BoxType).getExecutionMode());
    }

    public void testParse() throws Exception {
        GML2MockData.box(document, document);

        Envelope box = (Envelope) parse();
        assertEquals(box.getLowerCorner().getOrdinate(0), 1d, 0d);
        assertEquals(box.getLowerCorner().getOrdinate(1), 2d, 0d);
        assertEquals(box.getLowerCorner().getOrdinate(2), 3d, 0d);
        
        assertEquals(box.getUpperCorner().getOrdinate(0), 1d, 0d);
        assertEquals(box.getUpperCorner().getOrdinate(1), 2d, 0d);
        assertEquals(box.getUpperCorner().getOrdinate(2), 3d, 0d);
    }

    public void testEncode() throws Exception {
    	ISOGeometryBuilder builder = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84_3D);
    	DirectPosition dp1 = builder.createDirectPosition(new double[] {1.0, 2.0, 3.0});
    	DirectPosition dp2 = builder.createDirectPosition(new double[] {4.0, 5.0, 6.0});
    	
        Document doc = encode(builder.createEnvelope(dp1, dp2), GML.Box);

        assertEquals(2,
            doc.getElementsByTagNameNS(GML.NAMESPACE, GML.coord.getLocalPart()).getLength());
    }
}
