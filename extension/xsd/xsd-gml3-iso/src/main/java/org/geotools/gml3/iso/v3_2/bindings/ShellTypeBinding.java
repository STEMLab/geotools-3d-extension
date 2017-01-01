/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2016, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gml3.iso.v3_2.bindings;

import javax.xml.namespace.QName;

import org.geotools.gml3.iso.v3_2.GML;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.primitive.Shell;

/**
 * @author Donguk
 *
 */
public class ShellTypeBinding extends AbstractComplexBinding {

    ISOGeometryBuilder gb;
    
    public ShellTypeBinding(ISOGeometryBuilder gb) {
    	this.gb = gb;
    }
    
    /**
     * @generated
     */
    public QName getTarget() {
        return GML.ShellType;
    }
    
    @Override
    /**
     * In gml 3.2 Shell does not extend from AbstractGeometryType... so we change BEFORE to 
     * OVERRIDE
     */
    public int getExecutionMode() {
        return OVERRIDE;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated modifiable
     */
    public Class getType() {
        return Shell.class;
    }
    
    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {
        /*List multiPolygons = node.getChildValues(MultiPolygon.class);
        List polygons = new ArrayList<Polygon>();
        
        for (Object object : multiPolygons) {
            MultiPolygon multiPolygon = (MultiPolygon) object;
            
            for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                polygons.add((Polygon) multiPolygon.getGeometryN(i));
            }
        }
        
        *//**
         * Convert a JTSPolygon to a surface of ISOGeometry
         *//*
        if (!(pf instanceof PrimitiveFactoryImpl)) {
            throw new UnsupportedImplementationException("This binding class depends on org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl");
        }
        PrimitiveFactoryImpl pfImpl = (PrimitiveFactoryImpl) pf;
        
        CoordinateReferenceSystem crs = null;
        Object userData = ((com.vividsolutions.jts.geom.Polygon) polygons.get(0)).getUserData();
        if (userData != null) {
            if (userData instanceof CoordinateReferenceSystem) {
                crs = (CoordinateReferenceSystem) userData;
            } else if (userData instanceof CoordinateReferenceSystem) {
                crs = (CoordinateReferenceSystem) ((Map) userData).get(CoordinateReferenceSystem.class);
            }
        }
        if (crs == null) {
            crs = (CoordinateReferenceSystem) DefaultGeographicCRS.WGS84_3D;
        }
        
        List<OrientableSurface> surfaces = new ArrayList<OrientableSurface>();
        for (int i = 0; i < polygons.size(); i++) {         
            Surface surface = JTSUtilsNG.polygonToSurface((com.vividsolutions.jts.geom.Polygon) polygons.get(i), crs);
            surfaces.add(surface);
        }
        Shell shell = pfImpl.createShell(surfaces);*/
        
        return null;
    }

    @Override
    public Object getProperty(Object object, QName name) throws Exception {
        Shell shell = (Shell) object;
        
        /*if ("surfaceMember".equals(name.getLocalPart())) {
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
            //com.vividsolutions.jts.geom.MultiPolygon multiPolygon =
                    gf.createMultiPolygon(polygonMembers);
            return polygonMembers;
        }*/
        
        return null;
    }
}
