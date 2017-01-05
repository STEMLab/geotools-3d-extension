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
package org.geotools.filter.spatial;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.geotools.geometry.jts.ReferencedEnvelope3D;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Intersects;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.LineSegment;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.Surface;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
//import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 *
 * @source $URL$
 */
public class ISOIntersectsImpl extends ISOAbstractPreparedGeometryFilter implements
        Intersects {

    public ISOIntersectsImpl(Expression e1, Expression e2) {
        super(e1, e2);
    }

    public ISOIntersectsImpl(Expression e1, Expression e2, MatchAction matchAction) {
        super(e1, e2, matchAction);
    }

    @Override
    public boolean evaluateInternal(Geometry left, Geometry right) {
        switch (literals) {
        case BOTH:
            return cacheValue;
        case RIGHT: {
            //return rightPreppedGeom.intersects(left);
        }
        case LEFT: {
            //return leftPreppedGeom.intersects(right);
        }
        default: {
            return basicEvaluate(left, right);
        }
        }
    }

    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    protected final boolean basicEvaluate(Geometry left, Geometry right) {
        //Envelope envLeft = left.getEnvelopeInternal();
        //Envelope envRight = right.getEnvelopeInternal();
    	ReferencedEnvelope3D envLeft = new ReferencedEnvelope3D(left.getEnvelope());
		ReferencedEnvelope3D envRight = new ReferencedEnvelope3D(right.getEnvelope());
		
		ReferencedEnvelope3D empty = new ReferencedEnvelope3D();
		ReferencedEnvelope3D queryResult = envLeft.intersection(envRight);
		if(!empty.equals(queryResult)) {
			if(left instanceof Solid && right instanceof Solid) {
				
				ISOGeometryBuilder builder = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84_3D);
				
				Solid leftSolid = (Solid) left;
				Solid rightSolid = (Solid) right;
				
				Shell leftShell = leftSolid.getBoundary().getExterior();
				Shell rightShell = rightSolid.getBoundary().getExterior();
				
				Collection<? extends Primitive> lsElem = leftShell.getElements();
				Collection<? extends Primitive> rsElem = rightShell.getElements();
				
				PointArray leftSurface = null;
				PointArray rightSurface = null;
				
				for(Primitive p : lsElem) {
					Surface s = (Surface) p;
					Ring r = s.getBoundary().getExterior();
					PointArray pa = positions(r, builder);
					
					double z = Double.NaN;
					int i = 0;
					for(; i < pa.size(); i++) {
						
						if(i == 0) {
							z = pa.get(i).getDirectPosition().getOrdinate(2);
							continue;
						}
						
						if(z != pa.get(i).getDirectPosition().getOrdinate(2)) {
							break;
						}
					}
					
					if(i == pa.size()) {
						leftSurface = pa;
						break;
					}
				}
				
				for(Primitive p : rsElem) {
					Surface s = (Surface) p;
					Ring r = s.getBoundary().getExterior();
					PointArray pa = positions(r, builder);
					
					double z = Double.NaN;
					int i = 0;
					for(; i < pa.size(); i++) {
						
						if(i == 0) {
							z = pa.get(i).getDirectPosition().getOrdinate(2);
							continue;
						}
						
						if(z != pa.get(i).getDirectPosition().getOrdinate(2)) {
							break;
						}
					}
					
					if(i == pa.size()) {
						rightSurface = pa;
						break;
					}
				}
				
				if(leftSurface != null && rightSurface != null) {
					
					if(leftSurface.get(0) != leftSurface.get( leftSurface.size() - 1)) {
						leftSurface.add(leftSurface.get(0));
					}
					
					if(rightSurface.get(0) != rightSurface.get( rightSurface.size() - 1)) {
						rightSurface.add(rightSurface.get(0));
					}
					
					GeometryFactory factory = new GeometryFactory();
					
					Coordinate[] leftJTSCoords = new Coordinate[leftSurface.size()];
					Coordinate[] rightJTSCoords = new Coordinate[rightSurface.size()];
					
					for(int i = 0; i < leftSurface.size(); i++) {
						DirectPosition p = leftSurface.get(i).getDirectPosition();
						double[] coords = p.getCoordinate();
						Coordinate c = new Coordinate(coords[0], coords[1]);
						leftJTSCoords[i] = c;
					}
					
					for(int i = 0; i < rightSurface.size(); i++) {
						DirectPosition p = rightSurface.get(i).getDirectPosition();
						double[] coords = p.getCoordinate();
						Coordinate c = new Coordinate(coords[0], coords[1]);
						rightJTSCoords[i] = c;
					}
					
					
					Polygon leftPolygon = factory.createPolygon(leftJTSCoords);
					Polygon rightPolygon = factory.createPolygon(rightJTSCoords);
					
					boolean filteredResult = leftPolygon.intersects(rightPolygon);
					if(filteredResult) {
						System.out.print("\nlowerbound : (" + queryResult.getMinX() + "," 
								+ queryResult.getMinY() + "," + queryResult.getMinZ() + ")");
								System.out.print(" upperbound : (" + queryResult.getMaxX() + "," 
										+ queryResult.getMaxY() + "," + queryResult.getMaxZ() + ") ");
					}
					return filteredResult;
				}
				return false;
			}
		}
        return false;
        //return envRight.intersects(envLeft) && left.intersects(right);
    }
    
    
    
    public PointArray positions(Curve curve, ISOGeometryBuilder builder) {
        PointArray pa = builder.createPointArray();
        Iterator<? extends CurveSegment> tCurveSegmentIter = curve.getSegments().iterator();
        CurveSegment tSegment = null;

        // Iterate all CurveSegments (= LineStrings)
        while (tCurveSegmentIter.hasNext()) {
                tSegment = tCurveSegmentIter.next();

                
                
                // TODO: This version only handles the CurveSegment type
                // LineString
                LineString tLineString = (LineString) tSegment;

                Iterator<LineSegment> tLineSegmentIter = tLineString
                                .asLineSegments().iterator();
                while (tLineSegmentIter.hasNext()) {
                        LineSegment tLineSegment = tLineSegmentIter.next();
                        // Add new Coordinate, which is the start point of the
                        // actual LineSegment
                        pa.add( tLineSegment.getStartPoint());
                }
        }
        // Add new Coordinate, which is the end point of the last
        // curveSegment
        pa.add( tSegment.getEndPoint());
        return pa;
    }
    
    public PointArray positions(Ring line, ISOGeometryBuilder builder) {
        List<? extends OrientableCurve> curves = line.getGenerators();
        PointArray pa = builder.createPointArray();
        for (int i = 0; i < curves.size(); i++) {
            Curve c = (Curve) curves.get(i);
            PointArray cPa = positions(c, builder);
            pa.addAll(cPa);
        }
    	return pa;
    }
    
}
