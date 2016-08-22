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
package org.geotools.gml3.v3_2.bindings;

import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.util.UnsupportedImplementationException;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.primitive.PrimitiveFactory;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;

import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Binding object for the type http://www.opengis.net/gml:SurfaceType.
 * 
 * <p>
 * 
 * <pre>
 *  &lt;code&gt;
 *  &lt;complexType name=&quot;SurfaceType&quot;&gt;
 *      &lt;annotation&gt;
 *          &lt;documentation&gt;
 *              A Surface is a 2-dimensional primitive and is composed
 *              of one or more surface patches. The surface patches are
 *              connected to one another.
 *              The orientation of the surface is positive (&quot;up&quot;). The
 *              orientation of a surface chooses an &quot;up&quot; direction
 *              through the choice of the upward normal, which, if the
 *              surface is not a cycle, is the side of the surface from
 *              which the exterior boundary appears counterclockwise.
 *              Reversal of the surface orientation reverses the curve
 *              orientation of each boundary component, and interchanges
 *              the conceptual &quot;up&quot; and &quot;down&quot; direction of the surface.
 *              If the surface is the boundary of a solid, the &quot;up&quot;
 *              direction is usually outward. For closed surfaces, which
 *              have no boundary, the up direction is that of the surface
 *              patches, which must be consistent with one another. Its
 *              included surface patches describe the interior structure
 *              of the Surface.
 *           &lt;/documentation&gt;
 *      &lt;/annotation&gt;
 *      &lt;complexContent&gt;
 *          &lt;extension base=&quot;gml:AbstractSurfaceType&quot;&gt;
 *              &lt;sequence&gt;
 *                  &lt;element ref=&quot;gml:patches&quot;&gt;
 *                      &lt;annotation&gt;
 *                          &lt;documentation&gt;
 *                          This element encapsulates the patches of the
 *                          surface.
 *                       &lt;/documentation&gt;
 *                      &lt;/annotation&gt;
 *                  &lt;/element&gt;
 *              &lt;/sequence&gt;
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
 *
 *
 *
 * @source $URL$
 */
public class SolidTypeBinding extends org.geotools.gml3.bindings.SolidTypeBinding {
    
    public SolidTypeBinding(GeometryFactory gf, PrimitiveFactory pf) {
        super(gf, pf);
    }

    @Override
    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {
        if (!(pf instanceof PrimitiveFactoryImpl)) {
            throw new UnsupportedImplementationException("This binding class depends on org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl");
        }
        PrimitiveFactoryImpl pfImpl = (PrimitiveFactoryImpl) pf;
        
        Shell exteriorShell = (Shell) node.getChild("exterior").getChildValue("Shell");
        Node interiorNode = node.getChild("interior");
        List interiorShells = null;
        if(interiorNode != null) {
            interiorShells = interiorNode.getChildValues("Shell");
        }
        
        // Create a SolidBoundary and a Solid Object of ISOGeometry
        SolidBoundary solidBoundary = pfImpl.createSolidBoundary(exteriorShell, interiorShells);
        Solid solid = pfImpl.createSolid(solidBoundary);
        
        return solid;
    }

    @Override
    public Object getProperty(Object object, QName name) throws Exception {
        Solid solid = (Solid) object;
        SolidBoundary boundary = solid.getBoundary();
        
        if ("exterior".equals(name.getLocalPart())) {
            return boundary.getExterior();
        }
        
        if ("interior".equals(name.getLocalPart())) {
            return boundary.getInteriors();
        }
        
        return null;
    }    
}
