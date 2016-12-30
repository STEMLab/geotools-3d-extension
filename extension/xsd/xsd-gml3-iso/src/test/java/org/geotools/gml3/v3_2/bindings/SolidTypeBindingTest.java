/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2016, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gml3.v3_2.bindings;

import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.gml3.bindings.GML3SolidMockData;
import org.geotools.gml3.v3_2.GML;
import org.geotools.gml3.v3_2.GML32TestSupport;
import org.geotools.gml3.v3_2.GMLConfiguration;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.Configuration;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.primitive.PrimitiveFactory;
import org.opengis.geometry.primitive.Solid;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author Donguk
 *
 */
public class SolidTypeBindingTest extends GML32TestSupport {

    @Override
    protected boolean enableArcSurfaceSupport() {
        return true;
    }

    @Override
    protected Configuration createConfiguration() {
        Hints hints = GeoTools.getDefaultHints();
        hints.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D);
        hints.put(Hints.GEOMETRY_VALIDATE, false);
        //GeometryBuilder geometryBuilder = new GeometryBuilder(hints);
        //PrimitiveFactory primitiveFactory = geometryBuilder.getPrimitiveFactory();
        PrimitiveFactory primitiveFactory = new PrimitiveFactoryImpl(hints);
        
        GMLConfiguration configuration = (GMLConfiguration) super.createConfiguration();
        configuration.setPrimitiveFactory(primitiveFactory);
        
        return configuration;
    }
    
    public void testParse() throws Exception {
        GML3SolidMockData.solid(document, document, true);
        Solid solid = (Solid) parse();
        
        assertNotNull(solid);
        System.out.println(solid.toString());
        
        //double volume = solid.getVolume();
        //System.out.println(volume);
    }
    
    public void testEncode() throws Exception {
        GML3SolidMockData.solid(document, document, true);
        Solid solid = (Solid) parse();
        Geometry geometry = (Geometry) solid;
        Document dom = encode(geometry, GML.solidProperty);
        
        NodeList children = dom.getElementsByTagNameNS(GML.NAMESPACE, GML.Solid.getLocalPart());
        assertEquals(1, children.getLength());
        assertEquals(6, dom.getElementsByTagNameNS(GML.NAMESPACE, "surfaceMember").getLength());
        
        print(dom);
    }
}
