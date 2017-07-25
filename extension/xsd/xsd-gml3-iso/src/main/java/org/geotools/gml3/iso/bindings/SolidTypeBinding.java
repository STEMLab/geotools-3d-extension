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

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.gml3.iso.GML;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.complex.CompositeSurface;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;

/**
 * Binding object for the type http://www.opengis.net/gml:SolidType.
 *
 * @author Donguk Seo
 *
 * @source $URL$
 */
public class SolidTypeBinding extends AbstractComplexBinding {
    protected ISOGeometryBuilder gBuilder;

    public SolidTypeBinding(ISOGeometryBuilder gBuilder) {
        this.gBuilder = gBuilder;
    }
    
    /**
     * @generated
     */
    public QName getTarget() {
        return GML.SolidType;
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
        return Solid.class;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated modifiable
     */
    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {

    	CompositeSurface exterior = (CompositeSurface) node.getChild("exterior").getChildValue(0);
    	Shell exteriorShell = compositeSurfaceToShell(exterior);
    	
    	List interiorNodes = node.getChildren("interior");
    	
    	List<CompositeSurface> interiors = null;
    	List<Shell> interiorShells = null;
    	if (node.hasChild("interior")) {
    		int numInteriors = node.getChildren("interior").size();
    		interiors = new ArrayList<CompositeSurface>();
        	for(int i = 0 ; i < numInteriors ; i++){
        		Node temp = (Node) interiorNodes.get(i);
        		interiors.add((CompositeSurface) temp.getChildValue(0));
        	}
        }
    	if(interiors != null && interiors.size() > 0) {
    		interiorShells = new ArrayList<Shell>();
	    	for(CompositeSurface cs : interiors) {
	    		Shell intShell = compositeSurfaceToShell(cs);
	    		interiorShells.add(intShell);
	    	}
    	}
        
        SolidBoundary solidBoundary = gBuilder.createSolidBoundary(exteriorShell, interiorShells);
    	return gBuilder.createSolid(solidBoundary);
    }

    @Override
    public Object getProperty(Object object, QName name) throws Exception {
        Solid solid = (Solid) object;
        SolidBoundary boundary = solid.getBoundary();

        if ("exterior".equals(name.getLocalPart())) {
        	Shell extShell = boundary.getExterior();
        	List<OrientableSurface> surfaces = new ArrayList<OrientableSurface>(extShell.getGenerators());
        	CompositeSurface sf = gBuilder.createCompositeSurface(surfaces);
            return sf;
        }

        if ("interior".equals(name.getLocalPart())) {
            return boundary.getInteriors();
        }

        return null;
    }
    
    protected Shell compositeSurfaceToShell(CompositeSurface cSurface) {
       	List<OrientableSurface> surfaces = new ArrayList<OrientableSurface>();
    	for(Primitive p : cSurface.getElements()) {
    		surfaces.add((OrientableSurface) p);
    	}
    	
    	return gBuilder.createShell(surfaces);
    }
}
