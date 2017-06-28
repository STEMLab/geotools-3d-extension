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
package org.geotools.gml3.iso.bindings;

import java.math.BigInteger;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.geotools.geometry.DirectPosition1D;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.DirectPosition3D;
import org.geotools.geometry.jts.coordinatesequence.CoordinateSequences;
import org.geotools.gml3.iso.GML;
import org.geotools.gml3.iso.bindings.GML3ParsingUtils;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Position;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vividsolutions.jts.geom.CoordinateSequence;

/**
 * Binding object for the type
 * http://www.opengis.net/gml:DirectPositionListType.
 * 
 * <p>
 * 
 * <pre>
 *         <code>
 *  &lt;complexType name=&quot;DirectPositionListType&quot;&gt;
 *      &lt;annotation&gt;
 *          &lt;documentation&gt;DirectPositionList instances hold the coordinates for a sequence of direct positions within the same coordinate
 *                          reference system (CRS).&lt;/documentation&gt;
 *      &lt;/annotation&gt;
 *      &lt;simpleContent&gt;
 *          &lt;extension base=&quot;gml:doubleList&quot;&gt;
 *              &lt;attributeGroup ref=&quot;gml:SRSReferenceGroup&quot;/&gt;
 *              &lt;attribute name=&quot;count&quot; type=&quot;positiveInteger&quot; use=&quot;optional&quot;&gt;
 *                  &lt;annotation&gt;
 *                      &lt;documentation&gt;&quot;count&quot; allows to specify the number of direct positions in the list. If the attribute count is present then
 *                                                  the attribute srsDimension shall be present, too.&lt;/documentation&gt;
 *                  &lt;/annotation&gt;
 *              &lt;/attribute&gt;
 *          &lt;/extension&gt;
 *      &lt;/simpleContent&gt;
 *  &lt;/complexType&gt;
 * </code>
 *         </pre>
 * 
 * </p>
 * 
 * @generated
 * @author Hyung-Gyu Ryoo, Pusan National University
 *
 *
 * @source $URL$
 */
public class DirectPositionListTypeBinding extends AbstractComplexBinding {
	ISOGeometryBuilder gBuilder;

    public DirectPositionListTypeBinding(ISOGeometryBuilder gBuilder) {
        this.gBuilder = gBuilder;
    }
    /**
     * @generated
     */
    public QName getTarget() {
        return GML.DirectPositionListType;
    }

    public int getExecutionMode() {
        return AFTER;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated modifiable
     */
    public Class getType() {
        return DirectPosition[].class;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated modifiable
     */
    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {
        int crsDimension = GML3ParsingUtils.dimensions(node);

    	//TODO HACK
    	if(gBuilder.getCoordinateReferenceSystem().getCoordinateSystem().getDimension() != crsDimension) {

    		if(crsDimension == 2) {
    			gBuilder.setCoordinateReferenceSystem(DefaultGeographicCRS.WGS84);
    		} else {
    			gBuilder.setCoordinateReferenceSystem(DefaultGeographicCRS.WGS84_3D);
    		}
    	}
        
        Double[] values = (Double[]) value;
        BigInteger coordinatesCount = (BigInteger) node.getAttributeValue("count");

        if (coordinatesCount == null) {
            coordinatesCount = BigInteger.valueOf(values.length / crsDimension);
        }

        final int coordCount = coordinatesCount.intValue();
        if (coordCount == 0) {
            return gBuilder.createPointArray();
        }

        int dim = values.length / coordCount;

        //if ((dim < 1) || (dim > 2)) {
        if (dim < 1) {
            throw new IllegalArgumentException("dimension must be greater or equal to 1");
        }

        //PointArray pa = gBuilder.createPointArray();
        //Change PointArray to DirectPosition ArrayList because there is no reason to implement this part by PointArray. 
        ArrayList<DirectPosition> pa = new ArrayList<DirectPosition>();
        
        
        if (dim == 1) {
            for (int i = 0; i < coordCount; i++) {
            	DirectPosition dp = gBuilder.createDirectPosition(
            			new double[] { 
            					values[i].doubleValue()}
            			);
            	pa.add(dp);
            }
        } else if(dim == 2){
            int ordinateIdx = 0;
            // HACK: not sure if its correct to assign ordinates 0 to 0 and 1 to
            // 1 or it should be inferred from the crs
            for (int coordIndex = 0; coordIndex < coordCount; coordIndex++) {
            	DirectPosition dp = gBuilder.createDirectPosition(
            			new double[] { 
            					values[ordinateIdx].doubleValue(), 
            					values[ordinateIdx + 1].doubleValue()}
            			);
            	pa.add(dp);
                ordinateIdx += crsDimension;
            }
        } else {
            int ordinateIdx = 0;
            // HACK: not sure if its correct to assign ordinates 0 to 0 and 1 to
            // 1 or it should be inferred from the crs
            
            /*for(int i = 0; i < values.length; i++) {
            	values[i] = values[i] * 10000000d;
            	values[i] = Math.floor(values[i]);
            	//values[i] = Math.round(values[i] * 10000d) / 10000d;
            }*/
            
            for (int coordIndex = 0; coordIndex < coordCount; coordIndex++) {
            	DirectPosition dp = gBuilder.createDirectPosition(
            			new double[] { 
            					values[ordinateIdx].doubleValue(), 
            					values[ordinateIdx + 1].doubleValue(),
            					values[ordinateIdx + 2].doubleValue()}
            			);
            	pa.add(dp);
                ordinateIdx += crsDimension;
            }

        }
        
        DirectPosition[] res = new DirectPosition[pa.size()];
        res = pa.toArray(res);
        //return pa;
        return res;
    }

    /**
     * 
     * @param object a CoordinateSequence
     * 
     * @see org.geotools.xml.AbstractComplexBinding#encode(java.lang.Object, org.w3c.dom.Document, org.w3c.dom.Element)
     */
    public Element encode(Object object, Document document, Element value) throws Exception {
        // TODO: remove this when the parser can do lists
        DirectPosition[] dp = (DirectPosition[]) object;
        StringBuffer sb = new StringBuffer();

        if(dp != null && dp.length > 0) {
        	
        	int dim = dp[0].getCoordinateReferenceSystem().getCoordinateSystem().getDimension();
	        int len = dp.length;
	        int nOrdWithSpace = len * dim - 1;
	        int count = 0;
	        for (int i = 0; i < len; i++) {
	            DirectPosition p = dp[i];
	            double[] coords = p.getDirectPosition().getCoordinate();
	            for(int j = 0; j < coords.length; j++) {
	                sb.append(coords[j]);
	                
	                if (count < nOrdWithSpace) {
	                    sb.append(" ");
	                }
	                count++;
	            }
	        }
	
	        value.appendChild(document.createTextNode(sb.toString()));
        } else {
        	throw new IllegalArgumentException("DirectPosition[] is null or empty");
        }

        return value;
    }
      
}
