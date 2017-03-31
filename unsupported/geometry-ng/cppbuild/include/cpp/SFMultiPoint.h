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

/**
 * @author Donguk Seo
 *
 */

#ifndef JAVACPP_SFCGAL_MultiPoint_H
#define JAVACPP_SFCGAL_MultiPoint_H

#include <SFCGAL/MultiPoint.h>
#include "SFGeometryCollection.h"
#include "SFPoint.h"

class SFMultiPoint : public SFGeometryCollection {
public:	
	SFMultiPoint() : SFGeometryCollection(new SFCGAL::MultiPoint()) { }
	//SFMultiPoint(const SFMultiPoint& other) : SFGeometry(other.data) { }
	SFMultiPoint(const SFCGAL::MultiPoint& other) : SFGeometryCollection(new SFCGAL::MultiPoint(other)) { }
	//SFMultiPoint(SFCGAL::MultiPoint& other) : SFGeometry(new SFCGAL::MultiPoint(other)) { }
	SFMultiPoint(SFCGAL::MultiPoint* other) : SFGeometryCollection(other) { }

	SFMultiPoint& operator=(const SFMultiPoint& other) {
		data = other.data;
		
		return *this;
	}
	
	~SFMultiPoint() { }
	
	
	//--SFCGAL::Geometry
	SFMultiPoint* clone() const {
		return new SFMultiPoint(*this);
	}
	
	std::string geometryType() const {
		return data->geometryType();
	}	
	
	int geometryTypeId() const {
		return data->geometryTypeId();
	}
	

	const SFPoint& pointN( size_t const & n) const {
		return *(new SFPoint(((SFCGAL::MultiPoint *)data)->pointN(n)));
	}
	
	SFPoint& pointN(size_t const& n) {
		return *(new SFPoint(((SFCGAL::MultiPoint *)data)->pointN(n)));
	}
	
};

#endif
