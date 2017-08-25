/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *    
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.geometry.iso.root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.geotools.geometry.iso.PositionFactoryImpl;
import org.geotools.geometry.iso.PrecisionModel;
import org.geotools.geometry.iso.UnsupportedDimensionException;
import org.geotools.geometry.iso.aggregate.MultiCurveImpl;
import org.geotools.geometry.iso.aggregate.MultiPointImpl;
import org.geotools.geometry.iso.aggregate.MultiPrimitiveImpl;
import org.geotools.geometry.iso.aggregate.MultiSurfaceImpl;
import org.geotools.geometry.iso.complex.ComplexImpl;
import org.geotools.geometry.iso.complex.CompositeCurveImpl;
import org.geotools.geometry.iso.complex.CompositeSolidImpl;
import org.geotools.geometry.iso.complex.CompositeSurfaceImpl;
import org.geotools.geometry.iso.coordinate.EnvelopeImpl;
import org.geotools.geometry.iso.operation.overlay.OverlayOp;
import org.geotools.geometry.iso.operation.relate.RelateOp;
import org.geotools.geometry.iso.primitive.CurveBoundaryImpl;
import org.geotools.geometry.iso.primitive.CurveImpl;
import org.geotools.geometry.iso.primitive.PointImpl;
import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.geometry.iso.primitive.RingImplUnsafe;
import org.geotools.geometry.iso.primitive.SolidImpl;
import org.geotools.geometry.iso.primitive.SurfaceBoundaryImpl;
import org.geotools.geometry.iso.primitive.SurfaceImpl;
import org.geotools.geometry.iso.sfcgal.util.SFCGALAlgorithm;
import org.geotools.geometry.iso.topograph2D.IntersectionMatrix;
import org.geotools.geometry.iso.util.Assert;
import org.geotools.geometry.iso.util.algorithm2D.CentroidArea2D;
import org.geotools.geometry.iso.util.algorithmND.CentroidLine;
import org.geotools.geometry.iso.util.algorithmND.CentroidPoint;
import org.geotools.referencing.CRS;
import org.opengis.geometry.Boundary;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.PositionFactory;
import org.opengis.geometry.Precision;
import org.opengis.geometry.TransfiniteSet;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Solid;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * 
 * GeometryImpl is the root class of the geometric object taxonomy and supports
 * methods common to all geographically referenced geometric objects.
 * GeometryImpl instances are sets of direct positions in a particular
 * coordinate reference system. A GeometryImpl can be regarded as an infinite
 * set of points that satisfies the set operation interfaces for a set of direct
 * positions, TransfiniteSet&lt;DirectPosition&gt;. Since an infinite collection
 * class cannot be implemented directly, a boolean test for inclusion is
 * provided by this class.
 * 
 * NOTE As a type, GeometryImpl does not have a well-defined default state or
 * value representation as a data type. Instantiated subclasses of GeometryImpl
 * will.
 * 
 * 
 *
 *
 *
 *
 * @source $URL$
 * @version <A HREF="http://www.opengis.org/docs/01-101.pdf">Abstract
 *          Specification V5</A>
 * @author Jackson Roehrig & Sanjay Jena
 */

public abstract class GeometryImpl implements Geometry, Serializable  {

	private boolean mutable = true;

	// TODO: Remove this and use positionFactory.getCoordinateReferenceSystem()
	protected final CoordinateReferenceSystem crs;
	
	// TODO: Remove this and use positionFactory.getPrecision()	
	protected final Precision percision;
	
	//protected final PrimitiveFactory primitiveFactory; // for making stuff like curve, point 
	//protected final GeometryFactory geometryFactory; // geometry for Line etc...
	private transient PositionFactory positionFactory; // for position and point array
	//protected final ComplexFactory complexFactory; // surface and friends
		
	public GeometryImpl(CoordinateReferenceSystem crs, Precision pm ){
		this.crs = crs;
		this.percision = pm;
		this.positionFactory = new PositionFactoryImpl(crs, pm);
	}

	public GeometryImpl(CoordinateReferenceSystem coordinateReferenceSystem) {
		this( coordinateReferenceSystem, new PrecisionModel() );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public abstract GeometryImpl clone() throws CloneNotSupportedException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.root.Geometry#getBoundary()
	 */
	public abstract Boundary getBoundary();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.root.Geometry#getDimension(org.opengis.geometry.coordinate.DirectPosition)
	 */
	public abstract int getDimension(DirectPosition point);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.root.Geometry#getEnvelope()
	 */
	public abstract Envelope getEnvelope();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.root.Geometry#getRepresentativePoint()
	 */
	public abstract DirectPosition getRepresentativePoint();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.root.Geometry#isMutable()
	 */
	public boolean isMutable() {
		// TODO semantic JR, SJ
		// TODO implementation
		// TODO test
		// TODO documentation
		return this.mutable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.root.Geometry#toImmutable()
	 */
	public Geometry toImmutable() {
		// TODO semantic JR, SJ
		// TODO implementation
		// TODO test
		// TODO documentation
		if (this.mutable) {
			try {
				GeometryImpl g = this.clone();
				g.mutable = false;
				return g;
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.root.Geometry#getCoordinateReferenceSystem()
	 */
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return crs;
	}
    public Precision getPrecision() {
        return percision;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.root.Geometry#getCoordinateDimension()
	 */
	public int getCoordinateDimension() {
		return crs.getCoordinateSystem().getDimension();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.root.Geometry#transform(org.opengis.referencing.crs.CoordinateReferenceSystem)
	 */
	public Geometry transform(CoordinateReferenceSystem newCRS)
			throws TransformException {
		// create the appropriate math transform and do the transform
		MathTransform transform = null;
		try {
			transform = CRS.findMathTransform(this.getCoordinateReferenceSystem(), newCRS);
		} catch (FactoryException e) {
			Assert.isTrue(false, "Could not find math transform for given CRS objects.");
			//e.printStackTrace();
		}
		return transform(newCRS, transform);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.root.Geometry#transform(org.opengis.referencing.crs.CoordinateReferenceSystem,
	 *      org.opengis.referencing.operation.MathTransform)
	 */
	public Geometry transform(CoordinateReferenceSystem newCRS,
			MathTransform transform) throws MismatchedDimensionException, TransformException {

			// this should be handled in each of the individual geometry classes (ie:
			// PointImpl, CurveImpl, etc), if not it will fall through to here
			throw new UnsupportedOperationException("Transform not implemented for this geometry type yet.");
	}
	
	/**
     * Computes the distance between this and another geometry.  We have
     * to implement the logic of dealing with multiprimtive geometries separately.
	 * 
	 * gdavis - This method should really be broken out into 1 of 2 things:
	 * 		1.  an operator class that figures out the geometry type 
	 * 			and does the operation based on that, or
	 * 		2.  a double dispatch command pattern system that returns a command to perform
	 * 			based on the geometry type and the command of "distance".
	 * 		Currently this implementation works for our needs, but we should 
	 * 		consider re-designing it with one of the above approaches for better
	 * 		scalability.
	 * @see org.opengis.geometry.coordinate.root.Geometry#distance(org.opengis.geometry.coordinate.root.Geometry)
	 */
	public final double distance(Geometry geometry) {
		GeometryImpl geom = GeometryImpl.castToGeometryImpl(geometry);
		
		if(geom instanceof Primitive) {
			return SFCGALAlgorithm.distance(this, geom);
		} else if(geom instanceof MultiPrimitive) {
			return SFCGALAlgorithm.distance(this, geom);
		}
		Assert.isTrue(false, "The distance operation is not defined for this geometry object");
		return Double.NaN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.root.Geometry#getBuffer(double)
	 */
	public Geometry getBuffer(double distance) {
		if(this instanceof Primitive) {
			return SFCGALAlgorithm.extrude(this, distance);
		} else if(this instanceof MultiPrimitive) {
			return SFCGALAlgorithm.extrude(this, distance);
		}
		Assert.isTrue(false, "The buffer operation is not defined for this geometry object");
		return null;
	}

	/**
	 * Return a Primitive which represents the envelope of this Geometry instance
	 * (non-Javadoc)
	 * 
	 * @return primitive representing the envelope of this Geometry
	 * @see org.opengis.geometry.coordinate.root.Geometry#getMbRegion()
	 */
	public Geometry getMbRegion() {
		PrimitiveFactoryImpl primitiveFactory = new PrimitiveFactoryImpl(crs, getPositionFactory());
		return primitiveFactory.createPrimitive( this.getEnvelope() );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.root.Geometry#getCentroid()
	 */
	public DirectPosition getCentroid() {
	
		// Point: the point itself
		// MultiPoint: the average of the contained points
		if (this instanceof PointImpl ||
			this instanceof MultiPointImpl) {
			CentroidPoint cp = new CentroidPoint(this.crs);
			cp.add(this);
			return cp.getCentroid();
		} else
			
		// CurveBoundary: the average of start and end point
		if (this instanceof CurveBoundaryImpl) {
			CentroidPoint cp = new CentroidPoint(this.crs);
			cp.add(((CurveBoundaryImpl)this).getStartPoint());
			cp.add(((CurveBoundaryImpl)this).getEndPoint());
			return cp.getCentroid();
			
		} else
		// Curve: the average of the weighted line segments
		// MultiCurve: the average of the weighted line segments of all contained curves
		// Ring: the average of the weighted line segments of the contained curves
		if (this instanceof CurveImpl ||
			this instanceof MultiCurveImpl ||
			this instanceof RingImplUnsafe) {
			CentroidLine cl = new CentroidLine(this.crs);
			cl.add(this);
			return cl.getCentroid();
		} else
			
		// SurfaceBoundary: the average of the weighted line segments of all curves of the exterior and interior rings
		if (this instanceof SurfaceBoundaryImpl) {
				CentroidLine cl = new CentroidLine(this.crs);
				cl.add(((SurfaceBoundaryImpl)this).getExterior());
				Iterator<Ring> interiors = ((SurfaceBoundaryImpl)this).getInteriors().iterator();
				while (interiors.hasNext()) {
					cl.add((GeometryImpl) interiors.next());
				}
				return cl.getCentroid();
					
		} else
			
		// Surface: the average of the surface (considers holes)
		// MultiSurface: the average of all contained surfaces (considers holes)
		if (this instanceof SurfaceImpl ||
			this instanceof MultiSurfaceImpl) {
			CentroidArea2D ca = new CentroidArea2D(this.crs);
			ca.add(this);
			return ca.getCentroid();
					
		}
		
		// Missing: CompositePoint, CompositeCurve, CompositeSurface

		// - MultiPrimitive
		// The ISO 19107 specs state that the centroid of a colleciton of primitives
		// should only take into consideration the primitives with the largest
		// dimension (ie: if there are points, lines and polygons, it only considers
		// the polygons).
		if (this instanceof MultiPrimitiveImpl) {
			// First figure out what type of primtives should be considered in this
			// multiprimitive
			int maxD = this.getDimension(null);
			
			// get the centroid point of each element in this multiprimitive that matches
			// the maxD dimension and return the average of the centroid points
			CentroidPoint cp = new CentroidPoint(this.crs);
			Set<? extends Primitive> elems = ((MultiPrimitiveImpl)this).getElements();
			Iterator<? extends Primitive> iter = elems.iterator();
			while (iter.hasNext()) {
				Geometry prim = iter.next();
				if (prim.getDimension(null) == maxD) {
					cp.add(new PointImpl(prim.getCentroid()));
				}
			}
			
			// return the average of the centroid points
			return cp.getCentroid();
			
		}
	
		Assert.isTrue(false, "The centroid operation is not defined for this geometry object");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.root.Geometry#getConvexHull()
	 */
	public Geometry getConvexHull() {
		if(this instanceof Primitive) {
			return SFCGALAlgorithm.getConvexHull(this);
		} else if(this instanceof MultiPrimitive) {
			return SFCGALAlgorithm.getConvexHull(this);
		}
		Assert.isTrue(false, "The convex hull operation is not defined for this geometry object");
		return null;
	}
	
	
	// ***************************************************************************
	// ***************************************************************************
	// ******  RELATIONAL BOOLEAN OPERATORS
	// ***************************************************************************
	// ***************************************************************************
	
	/**
	 * Verifies a boolean relation between two geometry objects
	 * 
	 * @version <A HREF="http://www.opengis.org/docs/01-101.pdf">Abstract Specification V5</A>, page 126 (Clementini Operators)
	 * 
	 * @param geom1
	 * @param geom2
	 * @param intersectionPatternMatrix
	 * 
	 * @return TRUE if the Intersection Pattern Matrix describes the topological relation between the two input geomtries correctly, FALSE if not. 
	 * @throws UnsupportedDimensionException
	 */
	public static boolean cRelate(Geometry g1, Geometry g2, String intersectionPatternMatrix) throws UnsupportedDimensionException {
		GeometryImpl geom1 = GeometryImpl.castToGeometryImpl(g1);
		GeometryImpl geom2 = GeometryImpl.castToGeometryImpl(g2);
		
		/* for 3D coordinate geometry */
        int d1 = geom1.getCoordinateDimension();
        int d2 = geom2.getCoordinateDimension();
        
        if(d1 == 3 && d2 == 3) {
        	return SFCGALAlgorithm.relate(geom1, geom2, intersectionPatternMatrix);
        }
	        
		IntersectionMatrix tIM = RelateOp.relate((GeometryImpl) geom1, (GeometryImpl) geom2);
		return tIM.matches(intersectionPatternMatrix);
	}
	
	/**
	 * Verifies a boolean relation between two geometry objects
	 * 
	 * @version <A HREF="http://www.opengis.org/docs/01-101.pdf">Abstract Specification V5</A>, page 126 (Clementini Operators)
	 * 
	 * @param aOther
	 * @param intersectionPatternMatrix
	 * 
	 * @return TRUE if the Intersection Pattern Matrix describes the topological relation between the two input geomtries correctly, FALSE if not. 
	 * @throws UnsupportedDimensionException
	 */
	public boolean relate(Geometry aOther, String intersectionPatternMatrix)
			throws UnsupportedDimensionException {
		GeometryImpl geom = GeometryImpl.castToGeometryImpl(aOther);
		
		/* for 3D coordinate geometry */
        int d1 = getCoordinateDimension();
        int d2 = geom.getCoordinateDimension();
        
        if(d1 == 3 && d2 == 3) {
        	return SFCGALAlgorithm.relate(this, geom, intersectionPatternMatrix);
        }
	        
		IntersectionMatrix tIM = RelateOp.relate(this, geom);
		return tIM.matches(intersectionPatternMatrix);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.TransfiniteSet#contains(org.opengis.geometry.coordinate.TransfiniteSet)
	 */
	public boolean contains(TransfiniteSet pointSet) {
		GeometryImpl geom = GeometryImpl.castToGeometryImpl(pointSet);
		
		/* for 3D coordinate geometry */
        int d1 = getCoordinateDimension();
        int d2 = geom.getCoordinateDimension();
        
        if(d1 == 3 && d2 == 3) {
        	return SFCGALAlgorithm.contains(this, geom);
        }
	        
		// a.Contains(b) = b.within(a)
		return geom.within(this);
	}
	
	/**
	 * This operator tests, whether an object is spatially within this Geometry object
	 *  
	 * @param pointSet Another Object
	 * 
	 * @return TRUE, if the other object is spatially within this object
	 */
	public boolean within(TransfiniteSet pointSet) {
		GeometryImpl geom = GeometryImpl.castToGeometryImpl(pointSet);
		
		// Return false, if the envelopes doesn´t intersect
		if (!((EnvelopeImpl)this.getEnvelope()).intersects(geom.getEnvelope()))
			return false;
		
		/* for 3D coordinate geometry */
        int d1 = getCoordinateDimension();
        int d2 = geom.getCoordinateDimension();
        
        if(d1 == 3 && d2 == 3) {
        	return SFCGALAlgorithm.within(this, geom);
        }
	        
		IntersectionMatrix tIM = null;
		try {
			tIM = RelateOp.relate(this, geom);
		} catch (UnsupportedDimensionException e) {
			e.printStackTrace();
			return false;
		}
		
		boolean rValue = false;
		rValue = tIM.matches("T*F**F***");

		return rValue;	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.TransfiniteSet#contains(org.opengis.geometry.coordinate.DirectPosition)
	 */
	public boolean contains(DirectPosition position) {

		// Return false, if the point doesn´t lie in the envelope of this object
		if (!((EnvelopeImpl)this.getEnvelope()).intersects(position))
			return false;
		
		GeometryImpl point = new PointImpl( position );
		return point.within(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.TransfiniteSet#intersects(org.opengis.geometry.coordinate.TransfiniteSet)
	 */
	public boolean intersects(TransfiniteSet pointSet) {
		// Intersects = !Disjoint
		return !this.disjoint(pointSet);
	}
	
	/**
	 * This operator tests, whether an object is spatially disjoint with this Geometry object
	 * 
	 * @param pointSet The other object
	 * 
	 * @return TRUE, if the other object is disjoint with this object
	 */
	public boolean disjoint(TransfiniteSet pointSet) {
		GeometryImpl geom = GeometryImpl.castToGeometryImpl(pointSet);

		// Return true, if the envelopes doesn´t intersect
		if (!((EnvelopeImpl)this.getEnvelope()).intersects(geom.getEnvelope()))
			return true;
		
		int d1 = getCoordinateDimension();
        int d2 = geom.getCoordinateDimension();
        if(d1 == 3 && d2 == 3) {
            return SFCGALAlgorithm.disjoint(this, geom);
        }
		
		String intersectionPatternMatrix = "FF*FF****";

		try {
			IntersectionMatrix tIM = RelateOp.relate(this, geom);
			boolean rValue = tIM.matches(intersectionPatternMatrix);
			return rValue;
			
		} catch (UnsupportedDimensionException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	@Override
        public boolean equals(Object obj) {
	    if (this == obj)
	        return true;
	    if (obj == null)
	        return false;
	    if (!(obj instanceof TransfiniteSet))
	        return false;
	    
	    TransfiniteSet set = (TransfiniteSet) obj;
	    return this.equals(set);
        }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.TransfiniteSet#equals(org.opengis.geometry.coordinate.TransfiniteSet)
	 */
	public boolean equals(TransfiniteSet pointSet) {
		GeometryImpl geom = GeometryImpl.castToGeometryImpl(pointSet);
		
		int d1 = getCoordinateDimension();
        int d2 = geom.getCoordinateDimension();
        
        if(d1 == 3 && d2 == 3) {
        	return SFCGALAlgorithm.equals(this, geom);
        }
		
		IntersectionMatrix tIM = null;
		try {
			tIM = RelateOp.relate(this, geom);
		} catch (UnsupportedDimensionException e) {
			e.printStackTrace();
			return false;
		}
		
		boolean rValue = false;
		
		// No distinction between primitive and complex (explanation see thesis)
		rValue = tIM.matches("T*F**FFF*");
		return rValue;
	}

	/**
	 * This operator tests, whether an object touches this object in an edge or point
	 * 
	 * @param pointSet The other object
	 * 
	 * @return TRUE, if the other object touches this object
	 */
	public boolean touches(TransfiniteSet pointSet) {
		GeometryImpl geom = GeometryImpl.castToGeometryImpl(pointSet);

		// Return false, if the envelopes doesn´t intersect
		if (!((EnvelopeImpl)this.getEnvelope()).intersects(geom.getEnvelope()))
			return false;

		/* for 3D coordinate geometry */
        int d1 = getCoordinateDimension();
        int d2 = geom.getCoordinateDimension();
        
        if(d1 == 3 && d2 == 3) {
                return SFCGALAlgorithm.touches(this, geom);
        }
		
		IntersectionMatrix tIM = null;
		try {
			tIM = RelateOp.relate(this, geom);
		} catch (UnsupportedDimensionException e) {
			e.printStackTrace();
			return false;
		}
		
		boolean rValue = false;
		rValue = tIM.matches("F***T****")
	  	  || tIM.matches("FT*******")
	  	  || tIM.matches("F**T*****");
		
		return rValue;	
	}
	
	/**
	 * This operator tests, whether an object overlaps with this object.
	 * That is that a part of the object lies within this object and another part lies without this object,
	 * e.g. the other object intersects with the interior, boundary and exterior of this object
	 * 
	 * @param pointSet The other object
	 * 
	 * @return TRUE, if the other object overlaps with this object
	 */
	public boolean overlaps(TransfiniteSet pointSet) {
		GeometryImpl geom = GeometryImpl.castToGeometryImpl(pointSet);
		
		int d1 = geom.getDimension(null);
		int d2 = this.getDimension(null);

		// Overlaps only for Point/Point, Curve/Curve, Surface/Surface, Solid/Solid
		if (d1 != d2) {
			return false;
		}
		// Return false, if the envelopes doesn´t intersect
		if (!((EnvelopeImpl)this.getEnvelope()).intersects(geom.getEnvelope()))
			return false;
		
		/* for 3D coordinate geometry */
        int coordD1 = getCoordinateDimension();
        int coordD2 = geom.getCoordinateDimension();
        
        if(coordD1 == 3 && coordD2 == 3) {
                return SFCGALAlgorithm.overlaps(this, geom);
        }
        /* */
		
		IntersectionMatrix tIM = null;
		try {
			tIM = RelateOp.relate(this, geom);
		} catch (UnsupportedDimensionException e) {
			e.printStackTrace();
			return false;
		}
		
		boolean rValue = false;
		if (d1 == 1)
			rValue = tIM.matches("1*T***T**");
		else
			rValue = tIM.matches("T*T***T**");
	
		return rValue;	
	}
	
	public boolean crosses(TransfiniteSet pointSet) {
		GeometryImpl geom = GeometryImpl.castToGeometryImpl(pointSet);
		
		int d1 = geom.getDimension(null);
		int d2 = this.getDimension(null);

		// Crosses only for Point/Curve, Curve/Curve, Point/Surface, Curve/Surface, Point/Solid, Curve/Solid, Surface/Solid
		if ((d1 == 3 && d2 ==3) || (d1 == 2 && d2 == 2) || (d1 == 0) && (d2 == 0)) {
		        return false;
		}

		// Return false, if the envelopes doesn´t intersect
		if (!((EnvelopeImpl)this.getEnvelope()).intersects(geom.getEnvelope()))
			return false;
		
		/* for 3D coordinate geometry */
	        int coordD1 = getCoordinateDimension();
	        int coordD2 = geom.getCoordinateDimension();
	        
	        if(coordD1 == 3 && coordD2 == 3) {
	                return SFCGALAlgorithm.crosses(this, geom);
	        }
	        /* */
		
		IntersectionMatrix tIM = null;
		try {
			tIM = RelateOp.relate(this, geom);
		} catch (UnsupportedDimensionException e) {
			e.printStackTrace();
			return false;
		}
		
		// No distinction between primitive and complex (explanation see thesis)
		boolean rValue = false;
		
		if (d1 == 1 && d2 == 1)
			rValue = tIM.matches("0********");
		else
			rValue = tIM.matches("T*T******");
		
		return rValue;
		
	}
	
	// ***************************************************************************
	// ***************************************************************************
	// ******  SET OPERATIONS
	// ***************************************************************************
	// ***************************************************************************
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.TransfiniteSet#union(org.opengis.geometry.coordinate.TransfiniteSet)
	 */
	public TransfiniteSet union(TransfiniteSet pointSet) {
		GeometryImpl otherGeom = GeometryImpl.castToGeometryImpl(pointSet);
		
		/* for 3D coordinate geometry */
        int d1 = getCoordinateDimension();
        int d2 = otherGeom.getCoordinateDimension();
        
        if(d1 == 3 && d2 == 3) {
                return SFCGALAlgorithm.union(this, otherGeom);
        }
		
		// Return the result geometry of the Union operation between the input
		// geometries
		try {
			return OverlayOp.overlayOp(this, otherGeom, OverlayOp.UNION);
		} catch (UnsupportedDimensionException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.TransfiniteSet#intersection(org.opengis.geometry.coordinate.TransfiniteSet)
	 */
	public TransfiniteSet intersection(TransfiniteSet pointSet) {
		// Return the result geometry of the Intersection operation between the
		// input geometries
		GeometryImpl otherGeom = GeometryImpl.castToGeometryImpl(pointSet);
		
		/* for 3D coordinate geometry */
        int d1 = getCoordinateDimension();
        int d2 = otherGeom.getCoordinateDimension();
        
        if(d1 == 3 && d2 == 3) {
                return SFCGALAlgorithm.intersection(this, otherGeom);
        }
		
		try {
			return OverlayOp.overlayOp(this, otherGeom, OverlayOp.INTERSECTION);
		} catch (UnsupportedDimensionException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.TransfiniteSet#difference(org.opengis.geometry.coordinate.TransfiniteSet)
	 */
	public TransfiniteSet difference(TransfiniteSet pointSet) {
		// Return the result geometry of the Difference operation between the
		// input geometries
		GeometryImpl otherGeom = GeometryImpl.castToGeometryImpl(pointSet);
		
		/* for 3D coordinate geometry */
        int d1 = getCoordinateDimension();
        int d2 = otherGeom.getCoordinateDimension();
        
        if(d1 == 3 && d2 == 3) {
                return SFCGALAlgorithm.difference(this, otherGeom);
        }
		
		try {
			return OverlayOp.overlayOp(this, otherGeom, OverlayOp.DIFFERENCE);
		} catch (UnsupportedDimensionException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.TransfiniteSet#symmetricDifference(org.opengis.geometry.coordinate.TransfiniteSet)
	 */
	public TransfiniteSet symmetricDifference(TransfiniteSet pointSet) {
		// Return the result geometry of the Symmetric Difference operation
		// between the input geometries
		GeometryImpl otherGeom = GeometryImpl.castToGeometryImpl(pointSet);
		
		/* for 3D coordinate geometry */
        int d1 = getCoordinateDimension();
        int d2 = otherGeom.getCoordinateDimension();
        
        if(d1 == 3 && d2 == 3) {
                return SFCGALAlgorithm.symmetricDifference(this, otherGeom);
        }
		
		try {
			return OverlayOp
					.overlayOp(this, otherGeom, OverlayOp.SYMDIFFERENCE);
		} catch (UnsupportedDimensionException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengis.geometry.coordinate.root.Geometry#getClosure()
	 */
	public Complex getClosure() {		
		if (this instanceof ComplexImpl) {
			// Return this Complex instance, because complexes already contain their boundary
			// CompositePoint, CompositeCurve, CompositeSurface, Ring, CurveBoundary, SurfaceBoundary
			return (Complex) this;
		} else
		if (this instanceof CurveImpl) {
			List<OrientableCurve> cl = new ArrayList<OrientableCurve>();
			cl.add((OrientableCurve) this);
			return new CompositeCurveImpl(cl);
		} else
		if (this instanceof SurfaceImpl) {
			List<OrientableSurface> cs = new ArrayList<OrientableSurface>();
			cs.add( (OrientableSurface) this);
			return new CompositeSurfaceImpl(cs);
		} else
		if (this instanceof SolidImpl) {
			List<Solid> cs = new ArrayList<Solid>();
			cs.add( (Solid) this);
			return new CompositeSolidImpl(cs);
		} else
		if (this instanceof MultiPrimitiveImpl) {
			// TODO
			return null;
		} else

		
		Assert.isTrue(false, "The closure operation is not implemented for this geometry object");
		return null;
		
	}
	
	/* (non-Javadoc)
	 * @see org.opengis.geometry.coordinate.#isCycle()
	 */
	public boolean isCycle() {
		// The object is a cycle, if the boundary is empty: isCycle() = boundary().isEmpty()
		return (this.getBoundary() == null);
	}
	
	
	/**
	 * Use this function to cast Geometry instances to a GeometryImpl instance.
	 * In that way we can control the illegal injection of other instances at a central point.
	 * 
	 * @param g Geometry instance
	 * @return Instance of Geometry Impl
	 */
	protected static GeometryImpl castToGeometryImpl(Geometry g) {
		if (g instanceof GeometryImpl) {
			return (GeometryImpl)g;
		} else {
			throw new IllegalArgumentException("Illegal Geometry instance.");
		}
	}

	/**
	 * Use this function to cast TransfiniteSet instances to a GeometryImpl instance.
	 * In that way we can control the illegal injection of other instances at a central point.
	 * 
	 * @param tf
	 * @return
	 */
	protected static GeometryImpl castToGeometryImpl(TransfiniteSet tf) {
		if (tf instanceof GeometryImpl) {
			return (GeometryImpl)tf;
		} else {
			throw new IllegalArgumentException("TransfiniteSet instance not supported.");
		}
	}

	protected PositionFactory getPositionFactory() {
		if( positionFactory == null ){
			// we must of been transfered over a wire?
			positionFactory = new PositionFactoryImpl(crs, percision );
		}
		return positionFactory;
	}
        
}
