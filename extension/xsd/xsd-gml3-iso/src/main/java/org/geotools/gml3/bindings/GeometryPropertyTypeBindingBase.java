package org.geotools.gml3.bindings;

import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.gml2.bindings.GML2EncodingUtils;
import org.geotools.gml3.XSDIdRegistry;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 *
 * @source $URL$
 */
public abstract class GeometryPropertyTypeBindingBase extends AbstractComplexBinding {

    private XSDIdRegistry idSet;

    private boolean makeEmpty = false;

    private GML3EncodingUtils encodingUtils;

    public GeometryPropertyTypeBindingBase(GML3EncodingUtils encodingUtils, XSDIdRegistry idRegistry) {
        this.idSet = idRegistry;
        this.encodingUtils = encodingUtils;
    }

    public Class getType() {
        return getGeometryType();
    }

    public Class<? extends Geometry> getGeometryType() {
        return Geometry.class;
    }
    
    /**
     * for Parsing ISOGeometry
     * @return
     */
    public Class<? extends org.geotools.geometry.iso.root.GeometryImpl> getISOGeometryType() {
        return org.geotools.geometry.iso.root.GeometryImpl.class;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated modifiable
     */
    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {
        //return node.getChildValue(getGeometryType());
        Object object = node.getChildValue(getGeometryType());
        if (object != null) {
            return object;
        }
        
        object = node.getChildValue(getISOGeometryType());
        return object;
    }

    /**
     * @see org.geotools.xml.AbstractComplexBinding#encode(java.lang.Object, org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    public Element encode(Object object, Document document, Element value) throws Exception { 
        // It is necessary to check whether object is instance of JTS or ISO
        //checkExistingId((Geometry) object);
        checkExistingId(object);
        return value;
    }

    public Object getProperty(Object object, QName name) throws Exception {
        // TODO: Move implementation to GML3EncodingUtils
        if (object instanceof Geometry) {
            return encodingUtils.GeometryPropertyType_GetProperty((Geometry) object, name, makeEmpty);
        }
        if (name.getLocalPart().equals("_Solid") || name.getLocalPart().equals("AbstractSolid")) { // 3.1(_Solid) and 3.2(AbstractSolid)
            return object;
        }
        return null;
    }

    public List getProperties(Object object) throws Exception {
        if (object instanceof Geometry) {
            return encodingUtils.GeometryPropertyType_GetProperties((Geometry) object);
        }
        return null;
    }

    /**
     * Check if the geometry contains a feature which id is pre-existing in the document. If it's
     * true, make the geometry empty and add xlink:href property
     * 
     * @param value
     *            The complex attribute value
     * @param att
     *            The complex attribute itself
     */
    private void checkExistingId(Geometry geom) {
        if (geom != null) {
            String id = GML2EncodingUtils.getID(geom);

            if (id != null && idSet.idExists(id)) {
                // make geometry empty, href will added by getproperty
                makeEmpty = true;

            } else if (id != null) {

                idSet.add(id);
            }
        }
        return;
    }
    
    private void checkExistingId(Object geom) {
        if (geom instanceof Geometry) {
            checkExistingId((Geometry) geom);
        } else if (geom instanceof org.geotools.geometry.iso.root.GeometryImpl) {
            if (geom != null) {
                String id = GML3EncodingUtils.getID(geom);

                if (id != null && idSet.idExists(id)) {
                    // make geometry empty, href will added by getproperty
                    makeEmpty = true;

                } else if (id != null) {

                    idSet.add(id);
                }
            }
        }
    }

}
