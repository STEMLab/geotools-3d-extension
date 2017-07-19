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
    	List tempinteriors = node.getChildren("interior");
    	
    	
    	CompositeSurface[] interiors = null;
    	//CompositeSurface[] list = null;
    	
    	
    	List<OrientableSurface> surfaces = new ArrayList<OrientableSurface>();
    	for(Primitive p : exterior.getElements()) {
    		surfaces.add((OrientableSurface) p);
    	}
    	
    	Shell exteriorShell = gBuilder.createShell(surfaces);
    	List<Shell> interiorShells = null;
    	if (node.hasChild("interior")) {
    		
    		int interior_num = node.getChildren("interior").size();
    	
    		ArrayList<CompositeSurface> temp_list = new ArrayList<CompositeSurface>();
        	for(int i = 0 ; i < interior_num ; i++){
        		Node temp = (Node) tempinteriors.get(i);
        		temp_list.add((CompositeSurface) temp.getChildValue(0));
        	}
            
        	interiors = (CompositeSurface[])temp_list.toArray(new CompositeSurface[interior_num]);
        	
            //List list = node.getChildValues("interior");
            //interiors = (CompositeSurface[]) list.toArray(new CompositeSurface[list.size()]);
        }
    	if(interiors != null) {
    		
	    	for(CompositeSurface cs : interiors) {
	    		if(interiorShells == null) {
	    			interiorShells = new ArrayList<Shell>();
	    		}
	    		
	        	surfaces = new ArrayList<OrientableSurface>();
	        	for(Primitive p : cs.getElements()) {
	        		surfaces.add((OrientableSurface) p);
	        	}
	    		
	    		Shell intShell = gBuilder.createShell(surfaces);
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
    
    /*private com.vividsolutions.jts.geom.MultiPolygon shellToPolygons(Shell shell) {
        List elements = (List) shell.getElements();
        
        List polygons = new ArrayList();
        Iterator iter = elements.iterator();
        while (iter.hasNext()) {
            OrientableSurface surface = (OrientableSurface) iter.next();
            Object geometry = JTSUtilsNG.surfaceToPolygon((Surface) surface);
            
            if (geometry instanceof com.vividsolutions.jts.geom.Polygon) {
                polygons.add(geometry);
            } else if (geometry instanceof com.vividsolutions.jts.geom.MultiPolygon) {
                com.vividsolutions.jts.geom.MultiPolygon multiPolygon =
                        (com.vividsolutions.jts.geom.MultiPolygon) geometry;
                
                for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                    polygons.add(multiPolygon.getGeometryN(i));
                }
            }
        }
        
        com.vividsolutions.jts.geom.Polygon[] polygonMembers =
                new com.vividsolutions.jts.geom.Polygon[polygons.size()];
        polygons.toArray(polygonMembers);
        com.vividsolutions.jts.geom.MultiPolygon multiPolygon =
                gf.createMultiPolygon(polygonMembers);
        return multiPolygon;
    }*/
}
