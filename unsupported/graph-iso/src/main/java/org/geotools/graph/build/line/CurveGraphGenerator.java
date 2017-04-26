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
package org.geotools.graph.build.line;

import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Graphable;
import org.geotools.graph.structure.Node;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.coordinate.LineSegment;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builds a graph representing a line network in which edges in the network are
 * represented by LineString geometries. This implementation is a wrapper around
 * a LineGraphGenerator which sets underlying edge objects to be LineString
 * objects, and underlying Node objects to be Point objects. While generating
 * the graph, the generator uses the visited flag of created components to 
 * determine when to create underlying objects. For this reason it is not recommended 
 * to modify the visited flag of any graph components. 
 * 
 * 
 * @author Justin Deoliveira, Refractions Research Inc, jdeolive@refractions.net
 * @author Anders Bakkevold, Bouvet AS, bakkedev@gmail.com
 *
 * @source $URL$
 */
public class CurveGraphGenerator extends BasicLineGraphGenerator {

  private static ISOGeometryBuilder gBuilder;

  public CurveGraphGenerator(double tolerance) {
      super(tolerance);
  }

  public CurveGraphGenerator() {
  }

  public Graphable add(Object obj) {
    Curve c = null;
    if (obj instanceof MultiCurve) {
        c = (Curve) ((MultiCurve) obj).getElements().iterator().next();
    }
    else {
        c = (Curve) obj;
    }
    
    DirectPosition start = c.getStartPoint();
    DirectPosition end = c.getEndPoint();

    //parent class expects a line segment
    Edge e = (Edge)super.add(
      gBuilder.createLineSegment(start, end)
    );
    //check if the LineSegment has been changed
    if (useTolerance()) {
      LineSegment lineSegment = (LineSegment) e.getObject();
      List<DirectPosition> coordinateList = Arrays.asList(coordinates);
      // list from asList does not support add(index,object), must make an arraylist
      List<DirectPosition> nCoordinateList = new ArrayList<DirectPosition>(coordinateList);
      if (!c.getStartPoint().equals(lineSegment.getStartPoint())) {
        nCoordinateList.add(0, lineSegment.getStartPoint());
      } else if (!c.getEndPoint().equals(lineSegment.getEndPoint())){
        nCoordinateList.add(lineSegment.getEndPoint());
      }
      c = gBuilder.createCurve(nCoordinateList);
    }
    //over write object to be the linestring
    e.setObject(c);
    return(e);
  }

  protected LineSegment alterLine(LineSegment line, Node n1, Node n2) {
    Point c1added = ((Point) n1.getObject());
    Point c2added = ((Point) n2.getObject());
    if (!c1added.getCoordinate().equals(line.p0) || c2added.getCoordinate().equals(line.p1)) {
      line = new LineSegment(c1added.getCoordinate(), c2added.getCoordinate());
    }
    return line;
  }

  public Graphable remove(Object obj) {
    LineString ls = (LineString)obj;
    
    //parent ecpexts a line segment
    return(
      super.remove(
        new LineSegment(
          ls.getCoordinateN(0), ls.getCoordinateN(ls.getNumPoints()-1)
        )  
      )
    );
  }

  public Graphable get(Object obj) {
    Curve ls = (Curve)obj;
      
    //parent ecpexts a line segment
    return(
      super.get(
        new LineSegment(
          ls.getCoordinateN(0), ls.getCoordinateN(ls.getNumPoints()-1)
        )  
      )
    );
  }

  protected void setObject(Node n, Object obj) {
    //set underlying object to be point instead of coordinate
    DirectPosition c = (DirectPosition)obj;
    n.setObject(gBuilder.createPoint(c));
  }
}
