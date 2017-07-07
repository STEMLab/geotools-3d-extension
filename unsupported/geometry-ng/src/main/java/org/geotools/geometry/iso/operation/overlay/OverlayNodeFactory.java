/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *    
 *    (C) 2001-2006  Vivid Solutions
 *    (C) 2001-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.geometry.iso.operation.overlay;

import org.geotools.geometry.iso.topograph.Coordinate;
import org.geotools.geometry.iso.topograph.DirectedEdgeStar;
import org.geotools.geometry.iso.topograph.Node;
import org.geotools.geometry.iso.topograph.NodeFactory;
import org.geotools.geometry.iso.topograph.PlanarGraph;

/**
 * Creates nodes for use in the {@link PlanarGraph}s constructed during overlay
 * operations.
 * 
 *
 *
 *
 *
 * @source $URL$
 * @version 1.7.2
 */
public class OverlayNodeFactory extends NodeFactory {
	public Node createNode(Coordinate coord) {
		return new Node(coord, new DirectedEdgeStar());
	}
}
