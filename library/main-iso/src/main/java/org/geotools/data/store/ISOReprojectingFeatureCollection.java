/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2016, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.data.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.FeatureReader;
import org.geotools.data.ISODataUtilities;
import org.geotools.data.collection.DelegateFeatureReader;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureTypes;
import org.geotools.feature.SchemaException;
import org.geotools.feature.collection.ISODecoratingSimpleFeatureCollection;
import org.geotools.feature.visitor.CountVisitor;
import org.geotools.feature.visitor.FeatureAttributeVisitor;
import org.geotools.filter.ISOFilterAttributeExtractor;
import org.geotools.filter.ISOFilterFactoryImpl;
import org.geotools.filter.spatial.ISODefaultCRSFilterVisitor;
import org.geotools.filter.spatial.ISOReprojectingFilterVisitor;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * SimpleFeatureCollection decorator that reprojects the default geometry.
 * 
 * @author Justin
 * @author Hyung-Gyu Ryoo, Pusan National University
 *
 *
 * @source $URL$
 */
public class ISOReprojectingFeatureCollection extends ISODecoratingSimpleFeatureCollection {
	
	//TODO uses CommonFactoryFinder
    static final FilterFactory2 FF = new ISOFilterFactoryImpl();

    /**
     * The transform to the target coordinate reference system
     */
    MathTransform transform;

    /**
     * The schema of reprojected features
     */
    SimpleFeatureType schema;

    /**
     * The target coordinate reference system
     */
    CoordinateReferenceSystem target;
    
    /**
     * Transformer used to transform geometries;
     */
    GeometryCoordinateSequenceTransformer transformer;
    
    public ISOReprojectingFeatureCollection(
            FeatureCollection<SimpleFeatureType, SimpleFeature> delegate,
            CoordinateReferenceSystem target) {
        this(ISODataUtilities.simple(delegate), target);
    }
    public ISOReprojectingFeatureCollection(SimpleFeatureCollection delegate,
            CoordinateReferenceSystem target) {
        this( delegate, delegate.getSchema().getGeometryDescriptor().getCoordinateReferenceSystem(), target );
    }
    
    public ISOReprojectingFeatureCollection(FeatureCollection<SimpleFeatureType, SimpleFeature> delegate,
            CoordinateReferenceSystem source, CoordinateReferenceSystem target) {
        this(ISODataUtilities.simple(delegate), source, target);
    }
    
    public ISOReprojectingFeatureCollection(
            SimpleFeatureCollection delegate,
            CoordinateReferenceSystem source, CoordinateReferenceSystem target) {
        super(delegate);
        this.target = target;
        SimpleFeatureType schema = delegate.getSchema();
        this.schema = reType(schema, target);
       
        if (source == null) {
            throw new NullPointerException("source crs");
        }
        if ( target == null ) {
        	throw new NullPointerException("destination crs");
        }
        
        this.transform = transform(source, target);
        transformer = new GeometryCoordinateSequenceTransformer();
    }

    public void setTransformer(GeometryCoordinateSequenceTransformer transformer) {
		this.transformer = transformer;
	}  

    private MathTransform transform(CoordinateReferenceSystem source,
            CoordinateReferenceSystem target) {
        try {
            return CRS.findMathTransform(source, target, true);
        } catch (FactoryException e) {
            throw new IllegalArgumentException(
            		"Could not create math transform", e);
        }
    }

    private SimpleFeatureType reType(SimpleFeatureType type,
            CoordinateReferenceSystem target) {
        try {
            return FeatureTypes.transform(type, target);
        } catch (SchemaException e) {
            throw new IllegalArgumentException(
                    "Could not transform source schema", e);
        }
    }

    public  FeatureReader<SimpleFeatureType, SimpleFeature> reader() throws IOException {
        return new DelegateFeatureReader<SimpleFeatureType, SimpleFeature>(getSchema(), features());
    }

    public SimpleFeatureIterator features() {
        try {
            return new ISOReprojectingFeatureIterator(delegate.features(), transform, schema, transformer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SimpleFeatureType getSchema() {
        return this.schema;
    }

    public SimpleFeatureCollection subCollection(Filter filter) {
        // reproject the filter to the delegate native crs
        CoordinateReferenceSystem crs = getSchema().getCoordinateReferenceSystem();
        CoordinateReferenceSystem crsDelegate = delegate.getSchema().getCoordinateReferenceSystem();
        if(crs != null) {
            ISODefaultCRSFilterVisitor defaulter = new ISODefaultCRSFilterVisitor(FF, crs);
            filter = (Filter) filter.accept(defaulter, null);
            if(crsDelegate != null && !CRS.equalsIgnoreMetadata(crs, crsDelegate)) {
                ISOReprojectingFilterVisitor reprojector = new ISOReprojectingFilterVisitor(FF, delegate.getSchema());
                filter = (Filter) filter.accept(reprojector, null);
            }
        }
        
        return new ISOReprojectingFeatureCollection(delegate.subCollection(filter), target);
    }

    public SimpleFeatureCollection sort(SortBy order) {
        // return new ReprojectingFeatureList( delegate.sort( order ), target );
        throw new UnsupportedOperationException("Not yet");
    }

    public Object[] toArray() {
        return toArray(new Object[size()]);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        List<T> list = new ArrayList<T>();
        SimpleFeatureIterator i = features();
        try {
            while (i.hasNext()) {
                list.add((T) i.next());
            }
            return list.toArray(a);
        } finally {
            i.close();
        }
    }

    public boolean add(SimpleFeature o) {
        // must back project any geometry attributes
        throw new UnsupportedOperationException("Not yet");
        // return delegate.add( o );
    }

    /**
     * This method computes reprojected bounds the hard way, but computing them
     * feature by feature. This method could be faster if computed the
     * reprojected bounds by reprojecting the original feature bounds a Shape
     * object, thus getting the true shape of the reprojected envelope, and then
     * computing the minimum and maximum coordinates of that new shape. The
     * result would not a true representation of the new bounds.
     * 
     * @see org.geotools.data.FeatureResults#getBounds()
     */
    public ReferencedEnvelope getBounds() {
        SimpleFeatureIterator r = features();
        try {
        	ReferencedEnvelope newBBox = ReferencedEnvelope.create(target);
            Envelope internal;
            SimpleFeature feature;

            while (r.hasNext()) {
                feature = r.next();
                final Geometry geom = ((Geometry)feature.getDefaultGeometry());
                if(geom != null) {
                    internal = geom.getEnvelope();
                    ReferencedEnvelope internalRef = ReferencedEnvelope.reference(internal);
                    newBBox.include(internalRef);
                }
            }
            return ReferencedEnvelope.create(newBBox, target);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Exception occurred while computing reprojected bounds", e);
        } finally {
            r.close();
        }
    }

    @Override
    protected boolean canDelegate(FeatureVisitor visitor) {
        return isGeometryless(visitor, schema);
    }
    
    /**
     * Returns true if the visitor is geometryless, that is, it's not accessing a geometry field in the target schema
     * @param visitor
     * @return
     */
    public static boolean isGeometryless(FeatureVisitor visitor, SimpleFeatureType schema) {
        if (visitor instanceof FeatureAttributeVisitor) {
            //pass through unless one of the expressions requires the geometry attribute
            ISOFilterAttributeExtractor extractor = new ISOFilterAttributeExtractor(schema);
            for (Expression e : ((FeatureAttributeVisitor) visitor).getExpressions()) {
                e.accept(extractor, null);
            }

            for (PropertyName pname : extractor.getPropertyNameSet()) {
                AttributeDescriptor att = (AttributeDescriptor) pname.evaluate(schema);
                if (att instanceof GeometryDescriptor) {
                    return false;
                }
            }
            return true;
        } else if(visitor instanceof CountVisitor) {
            return true;
        }
        return false;
    }
}
