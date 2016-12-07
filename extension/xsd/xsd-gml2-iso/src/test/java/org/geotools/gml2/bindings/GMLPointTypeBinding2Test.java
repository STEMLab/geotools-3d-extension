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

import org.geotools.gml2.GML;
import org.geotools.xml.Binding;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.primitive.Point;
import org.w3c.dom.Document;


/**
 * 
 *
 * @source $URL$
 */
public class GMLPointTypeBinding2Test extends GMLTestSupport {
    public void testType() {
        assertEquals(Point.class, binding(GML.PointType).getType());
    }

    public void testExecutionMode() {
        assertEquals(Binding.AFTER, binding(GML.PointType).getExecutionMode());
    }

    public void testParse() throws Exception {
        GML2MockData.point(document, document);

        Point p = (Point) parse();
        DirectPosition dp = p.getDirectPosition();
        assertEquals(1.0, dp.getOrdinate(0), 0.0);
        assertEquals(2.0, dp.getOrdinate(1), 0.0);
        assertEquals(3.0, dp.getOrdinate(2), 0.0);
    }

    public void testEncode() throws Exception {
        Document doc = encode(GML2MockData.point(), GML.Point);

        assertEquals(1,
            doc.getElementsByTagNameNS(GML.NAMESPACE, GML.coord.getLocalPart()).getLength());
    }
}
