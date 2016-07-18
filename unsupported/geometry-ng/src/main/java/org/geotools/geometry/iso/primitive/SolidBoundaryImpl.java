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
package org.geotools.geometry.iso.primitive;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.geotools.geometry.iso.io.GeometryToString;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.SolidBoundary;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * The boundary of Solids shall be represented as SolidBoundary.
 * 
 * @author Jackson Roehrig & Sanjay Jena
 * 
 *
 *
 *
 *
 * @source $URL$
 */
public class SolidBoundaryImpl extends PrimitiveBoundaryImpl implements
		SolidBoundary {
    private static final long serialVersionUID = 113485181749545137L;

    /**
	 * SolidBoundaries are similar to SurfaceBoundaries. In normal 3-dimensional
	 * Euclidean space, one shell is distinguished as the exterior. In the more
	 * general case, this is not always possible.
	 * 
	 * SolidBoundary::exterior[0,1] : Shell; SolidBoundary::interior[0..n] :
	 * Shell;
	 * 
	 * NOTE An alternative use of solids with no external shell would be to
	 * define "complements" of finite solids. These infinite solids would have
	 * only interior boundaries. If this standard is extended to 4D Euclidean
	 * space, or if 3D compact manifolds are used (probably not in geographic
	 * information), then other examples of bounded solids without exterior
	 * boundaries are possible.
	 */
	private Shell exterior = null;

	private List<Shell> interior = null; /* ArrayList of Shell */

	/**
	 * Creates a SolidBoundary
	 * @param crs
	 * @param exterior
	 * @param interior
	 */
	public SolidBoundaryImpl(CoordinateReferenceSystem crs, Shell exterior, List<Shell> interior) {
		super(crs);
		this.exterior = exterior;
		this.interior = interior;
	}

	@Override
	public Set<Complex> createBoundary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SolidBoundaryImpl clone() throws CloneNotSupportedException {
	        Shell newExterior = (Shell) this.getExterior().clone();
	        List<Shell> newInteriors = null;
	        Shell[] interiors = this.getInteriors();
	        if(interiors != null) {
	                newInteriors = new ArrayList<Shell>();
	                for (int i = 0; i < interiors.length; i++) {
	                        newInteriors.add((Shell) interiors[i].clone());
	                }
	        }
	        
	        return new SolidBoundaryImpl(getCoordinateReferenceSystem(), newExterior, newInteriors); 
	}

	/**
	 * @return Returns the exterior Shell.
	 */
	public Shell getExterior() {
		return exterior;
	}

	/**
	 * @return Returns the interior Shells.
	 */
	public Shell[] getInteriors() {
		if (interior == null)
		   return null;
		
		Shell[] interiorArr = new Shell[interior.size()];
		interior.toArray(interiorArr);
		
		return interiorArr;
	}

	public boolean isSimple() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param point
	 * @return 3
	 * 
	 */
	@Override
	public int getDimension(DirectPosition point) {
		return point.getDimension();
	}

	@Override
	public Envelope getEnvelope() {
		return this.exterior.getEnvelope();
	}

	@Override
	public DirectPosition getRepresentativePoint() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toString() {
	        return GeometryToString.getString(this);
	}

	@Override
	public int hashCode() {
	        final int PRIME = 31;
	        int result = 1;
	        result = PRIME * result + ((exterior == null) ? 0 : exterior.hashCode());
	        result = PRIME * result + ((interior == null) ? 0 : interior.hashCode());
	        return result;
	}

	@Override
	public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        final SolidBoundaryImpl other = (SolidBoundaryImpl) obj;
	        if (exterior == null) {
	            if (other.exterior != null)
	                return false;
	        } else if (!exterior.equals(other.exterior))
	            return false;
	        if (interior == null) {
	            if (other.interior != null)
	                return false;
	        } else if (!interior.equals(other.interior))
	            return false;
	        return true;
	}
	
}
