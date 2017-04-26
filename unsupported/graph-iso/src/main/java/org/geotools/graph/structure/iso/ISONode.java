package org.geotools.graph.structure.iso;

import org.geotools.graph.structure.Node;
import org.opengis.geometry.DirectPosition;

/**
 * Represents a node in a line network. A node in a line graph has a coordinate
 * associated with it.
 * 
 * @author Hyung-Gyu Ryoo, Pusan National University, hyunggyu.ryoo@gmail.com
 *
 *
 *
 * @source $URL$
 */
public interface ISONode extends Node {
  
  /**
   * Returns the coordinate associated with the node.
   * 
   * @return A coordinate.
   */
  public DirectPosition getCoordinate();
  
  /**
   * Sets the coordinate associated with the node.
   * 
   * @param c A coordinate.
   */
  public void setCoordinate(DirectPosition c);
}
