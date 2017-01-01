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
package org.geotools.gml3.iso;

import java.util.Set;

import javax.xml.namespace.QName;

import org.geotools.gml3.iso.GML;
import org.geotools.xml.XSD;


/**
 * 
 *
 * @source $URL$
 */
public final class TESTSOLID extends XSD {
    private static TESTSOLID instance = new TESTSOLID();
    public static String NAMESPACE = "http://www.geotools.org/test";

    //types
    public static QName TestFeatureType = new QName(NAMESPACE, "TestFeatureType");
    public static QName TestFeatureCollectionType = new QName(NAMESPACE, "TestFeatureCollectionType");

    //elements
    public static QName TestFeature = new QName(NAMESPACE, "TestFeature");
    public static QName TestFeatureCollection = new QName(NAMESPACE, "TestFeatureCollection");

    private TESTSOLID() {
    }

    public static TESTSOLID getInstance() {
        return instance;
    }

    protected void addDependencies(Set dependencies) {
        dependencies.add(GML.getInstance());
    }

    public String getNamespaceURI() {
        return NAMESPACE;
    }

    public String getSchemaLocation() {
        return getClass().getResource("testSolid.xsd").toString();
    }
}
