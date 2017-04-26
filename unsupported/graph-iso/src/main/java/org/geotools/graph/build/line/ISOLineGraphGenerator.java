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

import org.geotools.graph.build.GraphGenerator;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Node;
import org.opengis.geometry.DirectPosition;

/**
 * Builds a graph representing a line network.
 *
 *
 * @source $URL$
 */
public interface ISOLineGraphGenerator extends GraphGenerator {
    /**
     * Look up a Node for the provided coordinate.
     * 
     * @param coordinate
     * @return Node
     */
    public Node getNode(DirectPosition coordinate);

    /**
     * Retrieve edge between the two coordinates.
     * 
     * @param coordinate1
     * @param coordinate2
     * @return Edge
     */
    public Edge getEdge(DirectPosition coordinate1, DirectPosition coordinate2);

}
