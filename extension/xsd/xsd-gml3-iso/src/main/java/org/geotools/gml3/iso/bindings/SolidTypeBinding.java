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

import javax.xml.namespace.QName;

import org.geotools.gml3.iso.GML;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.ISOGeometryBuilder;
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
        /*if (!(pf instanceof PrimitiveFactoryImpl)) {
            throw new UnsupportedImplementationException("This binding class depends on org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl");
        }
        PrimitiveFactoryImpl pfImpl = (PrimitiveFactoryImpl) pf;
        
        com.vividsolutions.jts.geom.MultiPolygon exterior = (com.vividsolutions.jts.geom.MultiPolygon) node.getChildValue("exterior");
        com.vividsolutions.jts.geom.MultiPolygon[] interiors = null;
        
        if (node.hasChild("interior")) {
            List list = node.getChildValues("interior");
            interiors = (com.vividsolutions.jts.geom.MultiPolygon[]) list.toArray(new com.vividsolutions.jts.geom.MultiPolygon[list.size()]);
        }
        
        
        CoordinateReferenceSystem crs = (CoordinateReferenceSystem) exterior.getUserData();
        if (crs == null || !(crs instanceof CoordinateReferenceSystem)) {
            crs = (CoordinateReferenceSystem) DefaultGeographicCRS.WGS84_3D;
        }
        
        *//**
         * Convert a Polygon of JTSGeometry to a Surface of ISOGeometry.
         * Create the exterior Shell by the converted surfaces.
         *//*
        List<OrientableSurface> exteriorSurfaces = new ArrayList<OrientableSurface>();
        for (int i = 0; i < exterior.getNumGeometries(); i++) {
            Surface surface = JTSUtilsNG.polygonToSurface((com.vividsolutions.jts.geom.Polygon) exterior.getGeometryN(i), crs);
            exteriorSurfaces.add(surface);
        }
        Shell exteriorShell = pfImpl.createShell(exteriorSurfaces);
        
        // Create the interior Shells
        List<Shell> interiorShells = null;
        if (interiors != null) {
            interiorShells = new ArrayList<Shell>();
            for (int i = 0; i < interiors.length; i++) {
                List<OrientableSurface> interiorSurfaces = new ArrayList<OrientableSurface>();
                com.vividsolutions.jts.geom.MultiPolygon interior = interiors[i];
                for (int j = 0; j < interior.getNumGeometries(); j++) {
                    Surface surface = JTSUtilsNG.polygonToSurface((com.vividsolutions.jts.geom.Polygon) interior.getGeometryN(j), crs);
                    interiorSurfaces.add(surface);
                }
                Shell interiorShell = pfImpl.createShell(interiorSurfaces);
                interiorShells.add(interiorShell);
            }
        }
        
        // Create a SolidBoundary and a Solid Object of ISOGeometry
        SolidBoundary solidBoundary = pfImpl.createSolidBoundary(exteriorShell, interiorShells);
        Solid solid = pfImpl.createSolid(solidBoundary);
        */
        return null;
    }

    @Override
    public Object getProperty(Object object, QName name) throws Exception {
        Solid solid = (Solid) object;
        SolidBoundary boundary = solid.getBoundary();

        if ("exterior".equals(name.getLocalPart())) {
            //return shellToPolygons(boundary.getExterior());
        }

        if ("interior".equals(name.getLocalPart())) {
            Shell[] interiors = boundary.getInteriors();
            if (interiors != null) {
                com.vividsolutions.jts.geom.MultiPolygon[] multiPolygons =
                        new com.vividsolutions.jts.geom.MultiPolygon[interiors.length];
                
                for (int i = 0; i < interiors.length; i++) {
                    //multiPolygons[i] = shellToPolygons(interiors[i]);
                }
                //return multiPolygons;
                return null;
            }
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
