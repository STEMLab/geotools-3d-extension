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
package org.geotools.gml3.iso.bindings;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.geotools.gml3.iso.GML;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.PolyhedralSurface;

/**
 * Binding object for the type http://www.opengis.net/gml:PolyhedralSurfaceType.
 * 
 * <p>
 * 
 * <pre>
 *  &lt;code&gt;
 *  &lt;complexType name=&quot;PolyhedralSurfaceType&quot;&gt;
 *      &lt;annotation&gt;
 *          &lt;documentation&gt;
 *              A polyhedral surface is a surface composed of polygon surfaces connected along their common boundary curves.
 *              This differs from the surface type only in the restriction on the types of surface patches acceptable.
 *              This property encapsulates the patches of the polyhedral surface.
 *           &lt;/documentation&gt;
 *      &lt;/annotation&gt;
 *      &lt;complexContent&gt;
 *          &lt;restriction base=&quot;gml:SurfaceType&quot;&gt;
 *              &lt;sequence&gt;
 *                  &lt;group ref=&quot;gml:StandardObjectProperties&quot;/&gt;
 *                  &lt;element ref=&quot;gml:polygonPatches&quot;/&gt;
 *              &lt;/sequence&gt;
 *          &lt;/restriction&gt;
 *      &lt;/complexContent&gt;
 *  &lt;/complexType&gt; 
 * 	
 *   &lt;/code&gt;
 * </pre>
 * 
 * </p>
 * 
 * @generated
 *
 *
 *
 * @source $URL$
 */
public class PolyhedralSurfaceTypeBinding extends AbstractComplexBinding implements Comparable {

	ISOGeometryBuilder gb;
    
    public PolyhedralSurfaceTypeBinding(ISOGeometryBuilder gb) {
        this.gb = gb;
    }
    
    /**
     * @generated
     */
    public QName getTarget() {
        return GML.PolyhedralSurfaceType;
    }
    
    @Override
    public int getExecutionMode() {
        return AFTER;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated modifiable
     */
    public Class getType() {
        return PolyhedralSurface.class;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated modifiable
     */
    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {
    	List polys = node.getChildValues("patches");
    	if(polys.size() == 1) {
        	return gb.createSurface(polys);
    	} else {
        	Map<String, List> patchesMap = (Map<String, List>) polys.get(0);
        	List patches = patchesMap.get("PolygonPatch");
        	return gb.createSurface(patches);
    	}
    }

    public int compareTo(Object o) {
        //JD: HACK here, since we map SurfaceType and MultiSurfaceType to MultiPolygon, there is a 
        // conflict when it comes to encoding where the actual type is not specifically specifid.
        // this comparison is made to ensure backwards compatability and favor MultiSurfaceTypeBinding 
        if ( o instanceof PolyhedralSurfaceTypeBinding ) {
            return 1;
        }
        return 0;
    }

}
