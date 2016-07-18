/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2015, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.geometry.iso.sfcgal.relate;

import org.geotools.geometry.iso.topograph2D.Dimension;
import org.geotools.geometry.iso.topograph2D.Location;

/**
 * @author Donguk Seo
 *
 */
public class IntersectionMatrix3D {
        
        /**
         * Internal representation of this <code>IntersectionMatrix</code>.
         * This matrix doesn't have the exterior element.
         */
        private int[][] matrix;

        /**
         * Creates an <code>IntersectionMatrix</code> with <code>FALSE</code>
         * dimension values.
         */
        public IntersectionMatrix3D() {
                matrix = new int[2][2];
                setAll(Dimension.FALSE);
        }

        /**
         * Creates an <code>IntersectionMatrix</code> with the given dimension
         * symbols.
         * 
         * @param elements
         *            a String of nine dimension symbols in row major order
         */
        public IntersectionMatrix3D(String elements) {
                this();
                set(elements);
        }

        /**
         * Creates an <code>IntersectionMatrix</code> with the same elements as
         * <code>other</code>.
         * 
         * @param other
         *            an <code>IntersectionMatrix</code> to copy
         */
        public IntersectionMatrix3D(IntersectionMatrix3D other) {
                this();
                matrix[Location.INTERIOR][Location.INTERIOR] = other.matrix[Location.INTERIOR][Location.INTERIOR];
                matrix[Location.INTERIOR][Location.BOUNDARY] = other.matrix[Location.INTERIOR][Location.BOUNDARY];
                matrix[Location.BOUNDARY][Location.INTERIOR] = other.matrix[Location.BOUNDARY][Location.INTERIOR];
                matrix[Location.BOUNDARY][Location.BOUNDARY] = other.matrix[Location.BOUNDARY][Location.BOUNDARY];
        }

        /**
         * @param row
         * @param col
         * @param dimensionValue
         */
        public void set(int row, int col, boolean dimensionValue) {
                if (dimensionValue) {
                        set(row, col, Dimension.TRUE);
                } else {
                        set(row, col, Dimension.FALSE);
                }
        }

        /**
         * Changes the value of one of this <code>IntersectionMatrix</code>s
         * elements.
         * 
         * @param row
         *            the row of this <code>IntersectionMatrix</code>, indicating
         *            the interior or boundary of the first
         *            <code>Geometry</code>
         * @param column
         *            the column of this <code>IntersectionMatrix</code>,
         *            indicating the interior or boundary of the second
         *            <code>Geometry</code>
         * @param dimensionValue
         *            the new value of the element
         */
        public void set(int row, int col, int dimensionValue) {
                matrix[row][col] = dimensionValue;
        }

        /**
         * Changes the elements of this <code>IntersectionMatrix</code> to the
         * dimension symbols in <code>dimensionSymbols</code>.
         * 
         * @param dimensionSymbols
         *            nine dimension symbols to which to set this
         *            <code>IntersectionMatrix</code> s elements. Possible values
         *            are <code>{T, F, * , 0, 1, 2, 3}</code>
         */
        public void set(String dimensionSymbols) {
                for (int i = 0; i < dimensionSymbols.length(); i++) {
                        int row = i / 2;
                        int col = i % 2;
                        if(dimensionSymbols.charAt(i) == '3')
                                matrix[row][col] = 3;
                        else
                                matrix[row][col] = Dimension.toDimensionValue(dimensionSymbols.charAt(i));
                }
        }

        /**
         * Changes the elements of this <code>IntersectionMatrix</code> to
         * <code>dimensionValue</code> .
         * 
         * @param dimensionValue
         *            the dimension value to which to set this
         *            <code>IntersectionMatrix</code> s elements. Possible values
         *            <code>{TRUE, FALSE, DONTCARE, 0, 1, 2, 3}</code> .
         */
        public void setAll(int dimensionValue) {
                for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                                matrix[i][j] = dimensionValue;
                        }
                }
        }

        /**
         * Returns true if the dimension value satisfies the dimension symbol.
         * 
         * @param actualDimensionValue
         *            a number that can be stored in the
         *            <code>IntersectionMatrix</code> . Possible values are
         *            <code>{TRUE, FALSE, DONTCARE, 0, 1, 2, 3}</code>.
         * @param requiredDimensionSymbol
         *            a character used in the string representation of an
         *            <code>IntersectionMatrix</code>. Possible values are
         *            <code>{T, F, * , 0, 1, 2, 3}</code>.
         * @return true if the dimension symbol encompasses the dimension value
         */
        public static boolean matches(int actualDimensionValue, char requiredDimensionSymbol) {
                if (requiredDimensionSymbol == '*') {
                        return true;
                }
                if (requiredDimensionSymbol == 'T'
                                && (actualDimensionValue >= 0 || actualDimensionValue == Dimension.TRUE)) {
                        return true;
                }
                if (requiredDimensionSymbol == 'F' && actualDimensionValue == Dimension.FALSE) {
                        return true;
                }
                if (requiredDimensionSymbol == '0' && actualDimensionValue == Dimension.P) {
                        return true;
                }
                if (requiredDimensionSymbol == '1' && actualDimensionValue == Dimension.L) {
                        return true;
                }
                if (requiredDimensionSymbol == '2' && actualDimensionValue == Dimension.A) {
                        return true;
                }
                if (requiredDimensionSymbol == '3' && actualDimensionValue == 3) {
                        return true;
                }
                return false;
        }

        /**
         * Returns whether the elements of this <code>IntersectionMatrix</code>
         * satisfies the required dimension symbols.
         * 
         * @param requiredDimensionSymbols
         *            nine dimension symbols with which to compare the elements of
         *            this <code>IntersectionMatrix</code>. Possible values are
         *            <code>{T, F, * , 0, 1, 2, 3}</code>.
         * @return <code>true</code> if this <code>IntersectionMatrix</code>
         *         matches the required dimension symbols
         */
        public boolean matches(String requiredDimensionSymbols) {
                if (requiredDimensionSymbols.length() != 4) {
                        throw new IllegalArgumentException("Should be length 4: "
                                        + requiredDimensionSymbols);
                }
                for (int ai = 0; ai < 2; ai++) {
                        for (int bi = 0; bi < 2; bi++) {
                                if (!matches(matrix[ai][bi],
                                                requiredDimensionSymbols.charAt(2 * ai + bi))) {
                                        return false;
                                }
                        }
                }
                return true;
        }

        /**
         * Returns the value of one of this <code>IntersectionMatrix</code>s
         * elements.
         * 
         * @param row
         *            the row of this <code>IntersectionMatrix</code>, indicating
         *            the interior or boundary of the first
         *            <code>Geometry</code>
         * @param column
         *            the column of this <code>IntersectionMatrix</code>,
         *            indicating the interior or boundary of the second
         *            <code>Geometry</code>
         * @return the dimension value at the given matrix position.
         */
        public int get(int row, int col) {
                return matrix[row][col];
        }
}
