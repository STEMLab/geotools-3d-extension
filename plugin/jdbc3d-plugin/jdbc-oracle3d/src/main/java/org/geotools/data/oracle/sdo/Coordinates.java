/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.data.oracle.sdo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.iso.PrecisionModel;
import org.geotools.geometry.iso.coordinate.PointArrayImpl;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Position;


/** 
 * HelperClass for dealing with PointArray(ISO geometry).
 * 
 * <p>
 * JTS14 does not supply suffiecnt API to allow the modification of
 * CoordinateSequence in a lossless manner. To make full use of this class
 * your CoordinateSequence will need to support the additional methods
 * outlined in CoordinateAccess.
 * </p>
 *
 * @author bowens , Refractions Research, Inc.
 * @author $Author: jgarnett $ (last modification)
 * @author Taehoon Kim, Pusan National University
 *
 * @source $URL$
 * @version $Id$
 */
public class Coordinates {
    private Coordinates() {
        // utility class do not instantiate
    }

    /**
     * Sublist opperation for CoordinateSequence.
     * 
     * <p>
     * Opperates in the same manner as corresponding java util List method.
     * </p>
     *
     * @param geometryFactory factory used to manage sequence
     * @param coords coordinate sequence
     * @param fromIndex - low endpoint (inclusive) of the subList.
     * @param toIndex - high endpoint (exclusive) of the subList.
     *
     * @return sublist of sequence (start,end] as provided by factory
     */
    
	public static PointArray subList(
        ISOGeometryBuilder geometryFactory, PointArray coords,
        int fromIndex, int toIndex) {
        if ((fromIndex == 0) && (toIndex == coords.size())) {
            return coords; // same list so just return it
        }

        if (coords instanceof ArrayList) {
        	List<Position> sublist = coords.subList(fromIndex, toIndex);
        	return new PointArrayImpl(sublist);
        }
        
        return null;
        /*
        if (coords instanceof CoordinateAccess) {
            CoordinateAccess access = (CoordinateAccess) coords;
            double[][] coordArray = access.toOrdinateArrays();
            Object[] attributeArray = access.toAttributeArrays();

            double[][] subCoordArray = new double[access.getDimension()][];
            Object[][] subAttributeArray = new Object[access.getNumAttributes()][];

            //							System.out.println("Dimension = " + access.getDimension());
            //							System.out.println("coordArray.length = " + coordArray.length);
            //							System.out.println("fromIndex= " + fromIndex + ", toIndex= " + toIndex);
            //							System.out.println("coordArray: ");
            //							System.out.print("X   ");
            //							for (int p=0; p<coordArray[0].length; p++)
            //								System.out.print(coordArray[0][p] + " ");
            //							System.out.print("\nY   ");
            //							for (int p=0; p<coordArray[1].length; p++)
            //								System.out.print(coordArray[1][p] + " ");
            //							System.out.println("");
            //								
            //							System.out.println("Num attributes = " + access.getNumAttributes());
            //							System.out.println("attributeArray.length = " + attributeArray.length);
            //							System.out.println("attributeArray: ");
            //							System.out.print("Z   ");
            //							for (int p=0; p<attributeArray[0].length; p++)
            //								System.out.print(attributeArray[0][p] + " ");
            //							System.out.print("\nT   ");
            //							for (int p=0; p<attributeArray[1].length; p++)
            //								System.out.print(attributeArray[1][p] + " ");
            //							System.out.println("");
            //			try
            //			{
            for (int i = 0; i < access.getDimension(); i++) {
                subCoordArray[i] = new OrdinateList(coordArray[i], 0, 1,
                        fromIndex, toIndex).toDoubleArray();
            }

            //			}
            //			catch (ArrayIndexOutOfBoundsException e)
            //			{
            //				e.printStackTrace();
            //				System.out.println("Dimension = " + access.getDimension());
            //				System.out.println("coordArray.length = " + coordArray.length);
            //				System.out.println("fromIndex= " + fromIndex + ", toIndex= " + toIndex);
            //				System.out.println("coordArray: ");
            //				System.out.print("X   ");
            //				for (int p=0; p<coordArray[0].length; p++)
            //					System.out.print(coordArray[0][p] + " ");
            //				System.out.print("\nY   ");
            //				for (int p=0; p<coordArray[1].length; p++)
            //					System.out.print(coordArray[1][p] + " ");
            //				System.out.println("");
            //			}
            for (int i = 0; i < access.getNumAttributes(); i++) {
                subAttributeArray[i] = new AttributeList(attributeArray[i], 0,
                        1, fromIndex, toIndex).toObjectArray();
            }

            System.out.println("subCoordArray.length = " + subCoordArray.length);
            System.out.println("subCoordArray: ");
            System.out.print("X   ");

            for (int p = 0; p < subCoordArray[0].length; p++)
                System.out.print(subCoordArray[0][p] + " ");

            System.out.print("\nY   ");

            for (int p = 0; p < subCoordArray[1].length; p++)
                System.out.print(subCoordArray[1][p] + " ");

            System.out.println("");

            System.out.println("subAttributeArray.length = "
                + subAttributeArray.length);
            System.out.println("subAttributeArray: ");
            System.out.print("Z   ");

            for (int p = 0; p < subAttributeArray[0].length; p++)
                System.out.print(subAttributeArray[0][p] + " ");

            System.out.print("\nT   ");

            for (int p = 0; p < subAttributeArray[1].length; p++)
                System.out.print(subAttributeArray[1][p] + " ");

            System.out.println("");

            CoordinateAccess c = (CoordinateAccess) ((CoordinateAccessFactory) geometryFactory)
                .create(subCoordArray, subAttributeArray);

            return c;
        }
        

        // handle coordinate sequence dimension correctly
        int size = toIndex - fromIndex;
        CoordinateSequence newSeq = geometryFactory.create(size, coords.getDimension());
        CoordinateSequences.copy(coords, fromIndex, newSeq, 0, size);
        return newSeq;
        */
     }

    /**
     * DOCUMENT ME!
     *
     * @param factory
     * @param sequence
     *
     */
    public static PointArray reverse(
    		ISOGeometryBuilder factory, PointArray pa) {
    	/*
        if (sequence instanceof CoordinateAccess) {
            CoordinateAccess access = (CoordinateAccess) sequence;
            double[][] coordArray = access.toOrdinateArrays();
            Object[] attributeArray = access.toAttributeArrays();

            double[][] subCoordArray = new double[access.getDimension()][];
            Object[][] subAttributeArray = new Object[access.getNumAttributes()][];

            for (int i = 0; i < access.getDimension(); i++) {
                subCoordArray[i] = new OrdinateList(coordArray[i], 0, 1,
                        access.size() - 1, -1).toDoubleArray();
            }

            for (int i = 0; i < access.getNumAttributes(); i++) {
                subAttributeArray[i] = new AttributeList(attributeArray[i], 0,
                        1, access.size() - 1, -1).toObjectArray();
            }

            CoordinateAccess c = (CoordinateAccess) ((CoordinateAccessFactory) factory)
                .create(subCoordArray, subAttributeArray);

            return c;
        } else // else CoordinateSequence
        {
            // handle coordinate sequence dimension correctly
            CoordinateSequence revSeq = factory.create(sequence);
            CoordinateSequences.reverse(revSeq);
            return revSeq;
        }
        */
    	PointArray revArray = factory.createPointArray();
    	for(int i = pa.size() - 1; i >= 0; i-- ){
    		revArray.add(pa.get(i));
    	}
    	return revArray;
    }

    public static String toString(PointArray pa, int coordinate,
        NumberFormat nf) {
        StringBuffer buf = new StringBuffer();
        append(buf, pa, coordinate, nf);

        return buf.toString();
    }

    public static void append(StringBuffer buf, PointArray pa,
        int coordinate, NumberFormat nf) {
        if (pa instanceof CoordinateAccess) {
            CoordinateAccess ca = (CoordinateAccess) pa;
            append(buf, ca, coordinate, LEN(ca), nf);
        } else {
            append(buf, pa, coordinate, LEN(pa), nf);
        }
    }

    public static void append(StringBuffer buf, PointArray pa,
        int coordinate, int LEN, NumberFormat nf) {
    	Position p = pa.get(coordinate);
        buf.append(nf.format(p.getDirectPosition().getOrdinate(0)));
        buf.append(" ");
        buf.append(nf.format(p.getDirectPosition().getOrdinate(1)));

        if (LEN == 3) {
            buf.append(" ");
            buf.append(nf.format(p.getDirectPosition().getOrdinate(2)));
        }
    }

    public static void append(StringBuffer buf, CoordinateAccess ca,
        int coordinate, int LEN, NumberFormat nf) {
        buf.append(nf.format(ca.getOrdinate(coordinate, 0)));

        for (int i = 1; i < LEN; i++) {
            buf.append(" ");
            buf.append(nf.format(ca.getOrdinate(coordinate, i)));
        }
    }

    public static int LEN(PointArray cs) {
        return D(cs) + L(cs);
    }

    public static int D(PointArray pa) {
        if (pa instanceof CoordinateAccess) {
            return ((CoordinateAccess) pa).getDimension();
        }

        if (pa.size() > 0) {
            return pa.getDimension();
        }

        return 3;
    }

    public static int L(PointArray pa) {
        if (pa instanceof CoordinateAccess) {
            return ((CoordinateAccess) pa).getNumAttributes();
        }

        return 0;
    }

    public static NumberFormat format(PrecisionModel pm) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setNaN("NaN");

        DecimalFormat f = new DecimalFormat();
        f.setDecimalFormatSymbols(symbols);

        if (pm == null) {
            f.setMaximumFractionDigits(0);

            return f;
        }

        f.setMinimumFractionDigits(0);
        f.setMaximumFractionDigits(pm.getMaximumSignificantDigits());

        return f;
    }

    public static String toString(PointArray pa, PrecisionModel pm) {
        StringBuffer buf = new StringBuffer();
        append(buf, pa, format(pm));

        return buf.toString();
    }

    public static void append(StringBuffer buf, PointArray pa,
        NumberFormat nf) {
        if (pa instanceof CoordinateAccess) {
            append(buf, (CoordinateAccess) pa, nf);
        } else {
            int LEN = LEN(pa); // 2 or 3

            if (pa.size() == 0) {
                return;
            }

            append(buf, pa, 0, LEN, nf);

            if (pa.size() == 1) {
                return;
            }

            for (int i = 1; i < pa.size(); i++) {
                buf.append(", ");
                append(buf, pa, i, LEN, nf);
            }
        }
    }

    public static void append(StringBuffer buf, CoordinateAccess ca,
        NumberFormat nf) {
        int LEN = LEN(ca);

        if (ca.size() == 0) {
            return;
        }

        append(buf, ca, 0, LEN, nf);

        if (ca.size() == 1) {
            return;
        }

        for (int i = 1; i < ca.size(); i++) {
            buf.append(", ");
            append(buf, ca, i, LEN, nf);
        }
    }
}
