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

import org.geotools.graph.build.basic.BasicDirectedGraphBuilder;
import org.geotools.graph.structure.Node;
import org.geotools.graph.structure.iso.BasicDirectedISONode;
import org.geotools.graph.structure.line.BasicDirectedXYNode;

/**
 * An implementation of GraphBuilder extended from BasicDirectedGraphBuilder 
 * used to build graphs representing directed line networks.  
 * 
 * @author Hyung-Gyu Ryoo, Pusan National University, hyunggyu.ryoo@gmail.com
 *
 *
 *
 * @source $URL$
 */
public class ISOBasicDirectedLineGraphBuilder extends BasicDirectedGraphBuilder {
 
  /**
   * Returns a node of type BasicDirectedXYNode.
   * 
   */
  public Node buildNode() {
    return(new BasicDirectedISONode());
  }
 
}
