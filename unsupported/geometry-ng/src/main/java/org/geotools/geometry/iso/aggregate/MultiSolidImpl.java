/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2015, Spatio-temporal Databases Laboratory (STEMLab)
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
package org.geotools.geometry.iso.aggregate;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Solid;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Donguk Seo
 *
 */
public class MultiSolidImpl extends MultiPrimitiveImpl {
        private static final long serialVersionUID = 3129192182252808017L;

        /**
         * Creates a MultiSolid by a set of Solids.
         * 
         * @param crs
         * @param primitives Set of Solids which shall be contained by the MultiSolid
         */
        public MultiSolidImpl(CoordinateReferenceSystem crs, Set<? extends Primitive> primitives) {
                super(crs, primitives);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.opengis.geometry.coordinate.aggregate.MultiSolid#getArea()
         */
        public double getArea() {
                // TODO Auto-generated method stub
                double area = 0;
                Iterator<Solid> elementIter = (Iterator<Solid>) this.elements.iterator();
                while (elementIter.hasNext()) {
                        area += elementIter.next().getArea();
                }

                return area;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.opengis.geometry.coordinate.aggregate.MultiSolid#getArea()
         */
        public double getVolume() {
                double volume = 0;
                Iterator<Solid> elementIter = (Iterator<Solid>) this.elements.iterator();
                while (elementIter.hasNext()) {
                        volume += elementIter.next().getVolume();
                }

                return volume;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.geotools.geometry.featgeom.aggregate.MultiPrimitiveImpl#getElements()
         */
        @SuppressWarnings("unchecked")
        public Set<Primitive> getElements() {
                // return (Set<OrientableSurface>) super.elements;
                return Collections.checkedSet((Set<Primitive>) super.elements, Primitive.class);

        }
}
