/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2015, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gml3.bindings;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.xsd.XSDElementDeclaration;
import org.geotools.feature.NameImpl;
import org.geotools.geometry.jts.LiteCoordinateSequence;
import org.geotools.geometry.jts.SingleCurvedGeometry;
import org.geotools.geometry.jts.coordinatesequence.CoordinateSequences;
import org.geotools.gml2.GMLConfiguration;
import org.geotools.gml2.SrsSyntax;
import org.geotools.gml2.bindings.GML2EncodingUtils;
import org.geotools.gml2.bindings.GMLEncodingUtils;
import org.geotools.gml3.GML;
import org.geotools.gml3.XSDIdRegistry;
import org.geotools.util.Converters;
import org.geotools.xlink.XLINK;
import org.geotools.xml.ComplexBinding;
import org.geotools.xml.Configuration;
import org.geotools.xml.SchemaIndex;
import org.geotools.xml.XSD;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

/**
 * Utility class for gml3 encoding.
 * 
 * @author Justin Deoliveira, The Open Planning Project, jdeolive@openplans.org
 * @author Ben Caradoc-Davies, CSIRO Exploration and Mining
 * 
 *
 *
 * @source $URL$
 *         http://svn.osgeo.org/geotools/trunk/modules/extension/xsd/xsd-gml3/src/main/java/org
 *         /geotools/gml3/bindings/GML3EncodingUtils.java $
 */
public class GML3EncodingUtils {

    public static GML3EncodingUtils INSTANCE = new GML3EncodingUtils();

    XSD gml;

    GMLEncodingUtils e;

    public GML3EncodingUtils() {
        this(GML.getInstance());
    }

    public GML3EncodingUtils(XSD gml) {
        this.gml = gml;
        e = new GMLEncodingUtils(gml);
    }

    static CoordinateSequence positions(LineString line) {
        if (line instanceof SingleCurvedGeometry<?>) {
            SingleCurvedGeometry<?> curved = (SingleCurvedGeometry<?>) line;
            return new LiteCoordinateSequence(curved.getControlPoints());
        } else {
            return line.getCoordinateSequence();
        }
    }


    public static URI toURI(CoordinateReferenceSystem crs, SrsSyntax srsSyntax) {
        if (crs == null) {
            return null;
        }

        try {
            String crsCode = GML2EncodingUtils.toURI(crs, srsSyntax);

            if (crsCode != null) {
                return new URI(crsCode);
            } else {
                return null;
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    static CoordinateReferenceSystem getCRS(Geometry g) {
        return GML2EncodingUtils.getCRS(g);
    }
    
    /**
     * Get uomLabels for the geometry if set in app-schema mapping configuration.
     */
    public static String getUomLabels(Geometry g) {
        Object userData = g.getUserData();
        if (userData != null && userData instanceof Map) {
            Object attributes = ((Map) userData).get(Attributes.class);
            if (attributes != null && attributes instanceof Map) {
                Name attribute = new NameImpl("uomLabels");
                Object uomLabels = ((Map) attributes).get(attribute);
                if (uomLabels != null) {
                    return uomLabels.toString();
                }
            }
        }
        return null;
    }

    /**
     * Get axisLabels for the geometry if set in app-schema mapping configuration.
     */
    public static String getAxisLabels(Geometry g) {
        Object userData = g.getUserData();
        if (userData != null && userData instanceof Map) {
            Object attributes = ((Map) userData).get(Attributes.class);
            if (attributes != null && attributes instanceof Map) {
                Name attribute = new NameImpl("axisLabels");
                Object axisLabels = ((Map) attributes).get(attribute);
                if (axisLabels != null) {
                    return axisLabels.toString();
                }
            }
        }
        return null;
    }

    public static String getID(Geometry g) {
        return GML2EncodingUtils.getID(g);
    }

    static void setID(Geometry g, String id) {
        GML2EncodingUtils.setID(g, id);
    }

    static String getName(Geometry g) {
        return GML2EncodingUtils.getName(g);
    }

    static void setName(Geometry g, String name) {
        GML2EncodingUtils.setName(g, name);
    }

    static String getDescription(Geometry g) {
        return GML2EncodingUtils.getDescription(g);
    }

    static void setDescription(Geometry g, String description) {
        GML2EncodingUtils.setDescription(g, description);
    }
    
    /**
     * 
     */
    public static CoordinateReferenceSystem getCRS(Object g) {
        if (g instanceof Geometry) {
            return getCRS((Geometry) g);
        } else if (g instanceof org.geotools.geometry.iso.root.GeometryImpl) {
            org.geotools.geometry.iso.root.GeometryImpl geometry = 
                    (org.geotools.geometry.iso.root.GeometryImpl) g;
            
            return geometry.getCoordinateReferenceSystem();
        }
        
        return null;
    }
    
    public static Integer getGeometryDimension(Object g, Configuration config) {
        if (g instanceof Geometry) {
            return GML2EncodingUtils.getGeometryDimension((Geometry) g, config);
        } else if (g instanceof org.geotools.geometry.iso.root.GeometryImpl) {
            org.geotools.geometry.iso.root.GeometryImpl geometry = 
                    (org.geotools.geometry.iso.root.GeometryImpl) g;
            // do a check for the case when geometry is empty
            //if (geometry) { 
            //    return null;
            //}
    
            // check if srsDimension is turned off
            if (config.hasProperty(GMLConfiguration.NO_SRS_DIMENSION)) {
                return null;
            }
            
            return geometry.getDimension(null);
        }
        
        return null;
    }
    
    public static String getID(Object g) {
        if (g instanceof Geometry) {
            return getID((Geometry) g);
        } else if (g instanceof org.geotools.geometry.iso.root.GeometryImpl) {
            org.geotools.geometry.iso.root.GeometryImpl geometry = 
                    (org.geotools.geometry.iso.root.GeometryImpl) g;
            return getMetaData(geometry, "gml:id");
        }
        
        return null;
    }
    
    public static String getName(Object g) {
        if (g instanceof Geometry) {
            return getName((Geometry) g);
        } else if (g instanceof org.geotools.geometry.iso.root.GeometryImpl) {
            org.geotools.geometry.iso.root.GeometryImpl geometry = 
                    (org.geotools.geometry.iso.root.GeometryImpl) g;
            return getMetaData(geometry, "gml:name");
        }
        
        return null;
    }
    
    public static String getDescription(Object g) {
        if (g instanceof Geometry) {
            return getDescription((Geometry) g);
        } else if (g instanceof org.geotools.geometry.iso.root.GeometryImpl) {
            org.geotools.geometry.iso.root.GeometryImpl geometry = 
                    (org.geotools.geometry.iso.root.GeometryImpl) g;
            return getMetaData(geometry, "gml:description");
        }
        
        return null;
    }
    
    public static String getMetaData(org.geotools.geometry.iso.root.GeometryImpl g,
            String metadata) {
        if (g.getUserData() instanceof Map) {
            Map userData = (Map) g.getUserData();
            return (String) userData.get(metadata);
        }        
        return null;
    }
    
    
    /**
     * Set a synthetic gml:id on each child of a multigeometry. If the multigeometry has no gml:id,
     * this method has no effect. The synthetic gml:id of each child is constructed from that of the
     * parent by appending "." and then an integer starting from one for the first child.
     * 
     * @param multiGeometry
     *            parent multigeometry containing the children to be modified
     */
    static void setChildIDs(Geometry multiGeometry) {
        String id = getID(multiGeometry);
        if (id != null) {
            for (int i = 0; i < multiGeometry.getNumGeometries(); i++) {
                StringBuilder builder = new StringBuilder(id);
                builder.append(".");  // separator
                builder.append(i + 1);  // synthetic gml:id suffix one-based
                GML2EncodingUtils.setID(multiGeometry.getGeometryN(i), builder.toString());
            }
        }
    }

    /**
     * Helper method used to implement {@link ComplexBinding#getProperty(Object, QName)} for
     * bindings of geometry reference types:
     * <ul>
     * <li>GeometryPropertyType
     * <li>PointPropertyType
     * <li>LineStringPropertyType
     * <li>PolygonPropertyType
     * </ul>
     */
    public Object GeometryPropertyType_GetProperty(Geometry geometry, QName name) {
        return e.GeometryPropertyType_getProperty(geometry, name);
    }

    /**
     * Helper method used to implement {@link ComplexBinding#getProperty(Object, QName)} for
     * bindings of geometry reference types:
     * <ul>
     * <li>GeometryPropertyType
     * <li>PointPropertyType
     * <li>LineStringPropertyType
     * <li>PolygonPropertyType
     * </ul>
     */
    public Object GeometryPropertyType_GetProperty(Geometry geometry, QName name, boolean makeEmpty) {
        return e.GeometryPropertyType_getProperty(geometry, name, true, makeEmpty);
    }
    
    public Object GeometryPropertyType_getProperty(Object g, QName name,
            boolean includeAbstractGeometry, boolean makeEmpty) {
        if (g instanceof Geometry) {
            GeometryPropertyType_GetProperty((Geometry) g, name, makeEmpty);
        } else if (!(g instanceof org.geotools.geometry.iso.root.GeometryImpl)) {
            return null;
        }
        
        org.geotools.geometry.iso.root.GeometryImpl geometry =
                (org.geotools.geometry.iso.root.GeometryImpl) g;
        if (name.equals(gml.qName("_Solid")) || name.equals(gml.qName("AbstractSolid"))) {
            // if the geometry is null, return null
            if (makeEmpty) {
                return null;
            }

            return geometry;
        }

        /*
        if (geometry.getUserData() instanceof Map) {
            Map<Name, Object> clientProperties = (Map<Name, Object>) ((Map) geometry.getUserData())
                    .get(Attributes.class);

            Name cname = e.toTypeName(name);
            if (clientProperties != null && clientProperties.keySet().contains(cname))
                return clientProperties.get(cname);
        }
        */

        if (XLINK.HREF.equals(name)) {
            // only process if geometry is empty and ID exists
            String id = getID(geometry);
            if ((makeEmpty) && id != null) {
                return "#" + id;
            }
        }

        return null;

    }

    /**
     * @deprecated use {@link #GeometryPropertyType_GetProperty(Geometry, QName)}
     */
    public static Object getProperty(Geometry geometry, QName name) {
        return INSTANCE.GeometryPropertyType_GetProperty(geometry, name);
    }

    /**
     * Helper method used to implement {@link ComplexBinding#getProperties(Object)} for bindings of
     * geometry reference types:
     * <ul>
     * <li>GeometryPropertyType
     * <li>PointPropertyType
     * <li>LineStringPropertyType
     * <li>PolygonPropertyType
     * </ul>
     */

    public List GeometryPropertyType_GetProperties(Geometry geometry) {
        return e.GeometryPropertyType_getProperties(geometry);
    }

    /**
     * @deprecated use {@link #GeometryPropertyType_GetProperties(Geometry)}
     */
    public static List getProperties(Geometry geometry) {
        return INSTANCE.GeometryPropertyType_GetProperties(geometry);
    }

    public Element AbstractFeatureTypeEncode(Object object, Document document, Element value,
            XSDIdRegistry idSet) {
        Feature feature = (Feature) object;
        String id = null;
        FeatureId identifier = feature.getIdentifier();
        if (identifier != null) {
            id = identifier.getRid();
        }
        
        Name typeName;
        if (feature.getDescriptor() == null) {
            // no descriptor, assume WFS feature type name is the same as the name of the content
            // model type
            typeName = feature.getType().getName();
        } else {
            // honour the name set in the descriptor
            typeName = feature.getDescriptor().getName();
        }
        Element encoding = document.createElementNS(typeName.getNamespaceURI(),
                typeName.getLocalPart());
        if (id != null) {
            if (!(feature instanceof SimpleFeature) && idSet != null) {
                if (idSet.idExists(id)) {
                    // XSD type ids can only appear once in the same document, otherwise the
                    // document is
                    // not schema valid. Attributes of the same ids should be encoded as xlink:href
                    // to
                    // the existing attribute.
                    encoding.setAttributeNS(XLINK.NAMESPACE, XLINK.HREF.getLocalPart(), "#"
                            + id.toString());
                    // make sure the attributes aren't encoded
                    feature.setValue(Collections.emptyList());
                    return encoding;
                } else {
                    idSet.add(id);
                }
            }
            encoding.setAttributeNS(gml.getNamespaceURI(), "id", id);
        }
        encodeClientProperties(feature, encoding);

        return encoding;
    }

    /**
     * @deprecated use {@link #AbstractFeatureTypeEncode(Object, Document, Element, XSDIdRegistry)}
     */
    public static Element AbstractFeatureType_encode(Object object, Document document,
            Element value, XSDIdRegistry idSet) {
        return INSTANCE.AbstractFeatureTypeEncode(object, document, value, idSet);
    }

    public List AbstractFeatureTypeGetProperties(Object object, XSDElementDeclaration element,
            SchemaIndex schemaIndex, Configuration configuration) {
        return e.AbstractFeatureType_getProperties(
                object,
                element,
                schemaIndex,
                new HashSet<String>(Arrays.asList("name", "description", "boundedBy", "location",
                        "metaDataProperty")), configuration);
    }

    /**
     * @deprecated use
     *             {@link #AbstractFeatureTypeGetProperties(Object, XSDElementDeclaration, SchemaIndex, Configuration)

     */
    public static List AbstractFeatureType_getProperties(Object object,
            XSDElementDeclaration element, SchemaIndex schemaIndex, Configuration configuration) {
        return INSTANCE.AbstractFeatureTypeGetProperties(object, element, schemaIndex,
                configuration);
    }

    /**
     * Encode any client properties (XML attributes) found in the UserData map of a ComplexAttribute
     * as XML attributes of the element.
     * 
     * @param complex
     *            the ComplexAttribute to search for client properties
     * @param element
     *            the element to which XML attributes should be added
     */
    @SuppressWarnings("unchecked")
    public static void encodeClientProperties(Property complex, Element element) {
        Map<Name, Object> clientProperties = (Map<Name, Object>) complex.getUserData().get(
                Attributes.class);
        if (clientProperties != null) {
            for (Name name : clientProperties.keySet()) {
                if (clientProperties.get(name) != null) {
                    element.setAttributeNS(name.getNamespaceURI(), name.getLocalPart(),
                            clientProperties.get(name).toString());
                }
            }
        }
    }

    /**
     * Encode the simpleContent property of a ComplexAttribute (if any) as an XML text node.
     * 
     * <p>
     * 
     * A property named simpleContent is a convention for representing XSD complexType with
     * simpleContent in GeoAPI.
     * 
     * @param complex
     *            the ComplexAttribute to be searched for simpleContent
     * @param document
     *            the containing document
     * @param element
     *            the element to which text node should be added
     */
    public static void encodeSimpleContent(ComplexAttribute complex, Document document,
            Element element) {
        Object value = getSimpleContent(complex);
        encodeAsText(document, element, value);
    }

    public static void encodeAsText(Document document, Element element, Object value) {
        if (value != null) {
            Text text = document.createTextNode(Converters.convert(value, String.class));
            element.appendChild(text);
        }
    }

    /**
     * Return the simple content of a {@link ComplexAttribute} if it represents a complexType with
     * simpleContent, otherwise null.
     * 
     * @param complex
     * @return
     */
    public static Object getSimpleContent(ComplexAttribute complex) {
        Property simpleContent = complex.getProperty(new NameImpl("simpleContent"));
        if (simpleContent == null) {
            return null;
        } else {
            return simpleContent.getValue();
        }
    }

}
