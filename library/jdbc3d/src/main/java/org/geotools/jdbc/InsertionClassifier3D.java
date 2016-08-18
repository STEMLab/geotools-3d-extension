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
package org.geotools.jdbc;

//import com.vividsolutions.jts.geom.Geometry;
import org.geotools.factory.Hints;
import org.geotools.jdbc.InsertionClassifier3D;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.geometry.Geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * PreparedStatement inserts must be split in function of those criteria:
 * - useExisting
 * - type of the geometries
 * <p/>
 * This class allows to do the splitting.
 */
class InsertionClassifier3D {
    public final boolean useExisting;
    public final Map<String, Class<? extends Geometry>> geometryTypes;

    public static Map<InsertionClassifier3D, Collection<SimpleFeature>> classify(
            SimpleFeatureType featureType, Collection<? extends SimpleFeature> features) {
        Map<InsertionClassifier3D, Collection<SimpleFeature>> kinds = new HashMap<>();
        for (SimpleFeature feature : features) {
            InsertionClassifier3D kind = new InsertionClassifier3D(featureType, feature);
            Collection<SimpleFeature> currents = kinds.get(kind);
            if (currents == null) {
                currents = new ArrayList<>();
                kinds.put(kind, currents);
            }
            currents.add(feature);
        }
        return kinds;
    }

    private InsertionClassifier3D(SimpleFeatureType featureType, SimpleFeature feature) {
        useExisting = useExisting(feature);
        geometryTypes = new TreeMap<>();
        for (AttributeDescriptor att : featureType.getAttributeDescriptors()) {
            if (att instanceof GeometryDescriptor) {
                Geometry geometry = (Geometry) feature.getAttribute(att.getName());
                if (geometry == null) {
                    geometryTypes.put(att.getLocalName(), null);
                } else {
                    geometryTypes.put(att.getLocalName(), geometry.getClass());
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InsertionClassifier3D that = (InsertionClassifier3D) o;
        if (useExisting != that.useExisting) {
            return false;
        }
        return geometryTypes.equals(that.geometryTypes);

    }

    @Override
    public int hashCode() {
        int result = (useExisting ? 1 : 0);
        result = 31 * result + geometryTypes.hashCode();
        return result;
    }

    public static boolean useExisting(SimpleFeature feature) {
        return Boolean.TRUE.equals(feature.getUserData().get(Hints.USE_PROVIDED_FID));
    }
}
