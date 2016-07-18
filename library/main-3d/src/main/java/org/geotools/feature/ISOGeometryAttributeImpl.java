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
package org.geotools.feature;

import org.geotools.util.Utilities;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.primitive.Solid;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * The GeometryAttribute Implementation providing geometry model based on ISO19107 specification.
 * @author HyungGyu Ryoo (Pusan National University)
 *
 */
public class ISOGeometryAttributeImpl extends AttributeImpl implements GeometryAttribute {
    
    protected BoundingBox bounds;
    
    public ISOGeometryAttributeImpl(Object content, GeometryDescriptor descriptor, Identifier id) {
        super(content, descriptor, id);
    }

    @Override 
    public GeometryType getType() {
        return (GeometryType) super.getType();
    }

    @Override
    public GeometryDescriptor getDescriptor() {
        return (GeometryDescriptor) super.getDescriptor();
    }
    
    @Override
    public void setValue(Object newValue) throws IllegalArgumentException, IllegalStateException {
        if(newValue instanceof Solid) {
            super.setValue( (Solid) newValue );
        }
        else if(newValue instanceof Geometry) {
            super.setValue( (Geometry) newValue );
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Object getValue() {
        return super.getValue();
    }

    @Override
    public synchronized BoundingBox getBounds() {
        if(bounds == null) {
            //TODO
        }
        return bounds;
    }

    @Override
    public synchronized void setBounds(BoundingBox bbox) {
        this.bounds = bbox;
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        
            if (!(o instanceof ISOGeometryAttributeImpl)) {
                    return false;
            }
            
            ISOGeometryAttributeImpl att = (ISOGeometryAttributeImpl) o;

            if (!Utilities.equals(descriptor, att.descriptor))
                    return false;

            if (!Utilities.equals(id, att.id))
                    return false;

            //TODO
            if ( value != null && att.value != null ) {
                // another lovley jts thing... comparing geometry collections that 
                // arent multi point/line/poly throws an exception, so we need to 
                // do that comparison
                
                /*
                if ( att.value instanceof GeometryCollection && 
                !(att.value instanceof MultiPoint) && 
                !(att.value instanceof MultiLineString) &&
                !(att.value instanceof MultiPolygon) ) {
                    
                    if ( value instanceof GeometryCollection ) {
                        //compare the two collections 
                        GeometryCollection c1 = (GeometryCollection) value;
                        GeometryCollection c2 = (GeometryCollection) att.value;
                        
                        if ( c1.getNumGeometries() !=  c2.getNumGeometries() ) {
                            return false;
                        }
                        
                        for ( int i = 0; i < c1.getNumGeometries(); i++ ) {
                            Geometry g1 = c1.getGeometryN(i);
                            Geometry g2 = c2.getGeometryN(i);
                            
                            if ( !g1.equalsExact(g2) ) {
                                return false;
                            }
                        }
                        
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                */
                if ( !((Geometry)value).equals((Geometry)att.value)) {
                            return false;
                    }
            }
            else {
                return Utilities.deepEquals(value, this.value);    
            }
            
            return true;
    }

    @Override
    public int hashCode() {
        int hash = descriptor.hashCode(); 
        
        if ( id != null ) {
            hash += 37 * id.hashCode();
        }
        
        return hash;
}
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName()).append(":");
        sb.append(getDescriptor().getName().getLocalPart());
        CoordinateReferenceSystem crs = getDescriptor().getType().getCoordinateReferenceSystem();
        if(!getDescriptor().getName().getLocalPart().equals(getDescriptor().getType().getName().getLocalPart()) ||
                id != null || crs != null){
            sb.append("<");
            sb.append(getDescriptor().getType().getName().getLocalPart());
            if( id != null ){
                sb.append( " id=");
                sb.append( id );
            }
            if( crs != null ){
                sb.append( " crs=");
                sb.append( crs );
            }
        if( id != null ){
            sb.append( " id=");
            sb.append( id );
        }
        sb.append(">");
        }
        sb.append("=");
        sb.append(value);
        return sb.toString();
    }
    
}
