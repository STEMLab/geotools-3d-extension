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
package org.geotools.gml2.iso;

import org.geotools.feature.FeatureCollections;
import org.geotools.geometry.ISOGeometryBuilder;
import org.geotools.xlink.XLINKConfiguration;
import org.geotools.xs.XSConfiguration;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import junit.framework.TestCase;


/**
 * 
 *
 * @source $URL$
 */
public class GMLConfigurationTest extends TestCase {
    GMLConfiguration_ISO configuration;

    protected void setUp() throws Exception {
        super.setUp();

        configuration = new GMLConfiguration_ISO();
    }

    public void testGetNamespaceURI() {
        assertEquals(GML.NAMESPACE, configuration.getNamespaceURI());
    }

    public void testGetSchemaLocation() {
        assertEquals(GMLConfiguration_ISO.class.getResource("feature.xsd").toString(),
            configuration.getSchemaFileURL());
    }

    public void testDependencies() {
        assertEquals(2, configuration.getDependencies().size());
        assertTrue(configuration.getDependencies().contains(new XLINKConfiguration()));
        assertTrue(configuration.getDependencies().contains(new XSConfiguration()));
    }

    public void testSchemaLocationResolver() {
        assertEquals(GMLConfiguration_ISO.class.getResource("feature.xsd").toString(),
            configuration.getSchemaLocationResolver()
                         .resolveSchemaLocation(null, GML.NAMESPACE, "feature.xsd"));
        assertEquals(GMLConfiguration_ISO.class.getResource("geometry.xsd").toString(),
            configuration.getSchemaLocationResolver()
                         .resolveSchemaLocation(null, GML.NAMESPACE, "geometry.xsd"));
    }

    public void testContext() {
        MutablePicoContainer container = new DefaultPicoContainer();
        configuration.configureContext(container);

        assertNotNull(container.getComponentInstanceOfType(FeatureTypeCache.class));
        assertNotNull(container.getComponentInstanceOfType(ISOGeometryBuilder.class));
        assertNotNull(container.getComponentAdapterOfType(FeatureCollections.class));
    }
}
