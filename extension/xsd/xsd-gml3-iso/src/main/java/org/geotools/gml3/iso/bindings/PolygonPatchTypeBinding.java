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

import javax.xml.namespace.QName;

import org.geotools.gml3.iso.GML;
import org.geotools.gml3.iso.bindings.PolygonTypeBinding;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.geometry.primitive.SurfacePatch;

/**
 * Binding object for the type http://www.opengis.net/gml:PolygonPatchType.
 * 
 * <p>
 * 
 * <pre>
 *  &lt;code&gt;
 *  &lt;complexType name=&quot;PolygonPatchType&quot;&gt;
 *      &lt;annotation&gt;
 *          &lt;documentation&gt;
 *              A PolygonPatch is a surface patch that is defined by
 *              a set of boundary curves and an underlying surface to
 *              which these curves adhere. The curves are coplanar and
 *              the polygon uses planar interpolation in its interior.
 *              Implements GM_Polygon of ISO 19107. 
 *           &lt;/documentation&gt;
 *      &lt;/annotation&gt;
 *      &lt;complexContent&gt;
 *          &lt;extension base=&quot;gml:AbstractSurfacePatchType&quot;&gt;
 *              &lt;sequence&gt;
 *                  &lt;element minOccurs=&quot;0&quot; ref=&quot;gml:exterior&quot;/&gt;
 *                  &lt;element maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot; ref=&quot;gml:interior&quot;/&gt;
 *              &lt;/sequence&gt;
 *              &lt;attribute fixed=&quot;planar&quot; name=&quot;interpolation&quot; type=&quot;gml:SurfaceInterpolationType&quot;&gt;
 *                  &lt;annotation&gt;
 *                      &lt;documentation&gt;
 *                       The attribute &quot;interpolation&quot; specifies the
 *                       interpolation mechanism used for this surface
 *                       patch. Currently only planar surface patches
 *                       are defined in GML 3, the attribute is fixed
 *                       to &quot;planar&quot;, i.e. the interpolation method
 *                       shall return points on a single plane. The
 *                       boundary of the patch shall be contained within
 *                       that plane.
 *                    &lt;/documentation&gt;
 *                  &lt;/annotation&gt;
 *              &lt;/attribute&gt;
 *          &lt;/extension&gt;
 *      &lt;/complexContent&gt;
 *  &lt;/complexType&gt; 
 * 	
 *   &lt;/code&gt;
 * </pre>
 * 
 * </p>
 * 
 * @generated
 * @author Hyung-Gyu Ryoo, Pusan National University
 *
 *
 * @source $URL$
 */
public class PolygonPatchTypeBinding extends AbstractComplexBinding {

    protected ISOGeometryBuilder gBuilder;

    public PolygonPatchTypeBinding(ISOGeometryBuilder gBuilder) {
        this.gBuilder = gBuilder;
    }
    
    /**
     * @generated
     */
    public QName getTarget() {
        return GML.PolygonPatchType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated modifiable
     */
    public Class getType() {
        return Polygon.class;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated modifiable
     */
    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {
        //TODO: schema allows no exterior ring, but what the heck is that all about ?
        Ring exterior = (Ring) node.getChildValue("exterior");
        List<Ring> interiors = null;

        if (node.hasChild("interior")) {
            List list = node.getChildValues("interior");
            interiors = list;
        }

        SurfaceBoundary sb = gBuilder.createSurfaceBoundary(exterior, interiors);
        return gBuilder.createPolygon(sb);
    }

}
