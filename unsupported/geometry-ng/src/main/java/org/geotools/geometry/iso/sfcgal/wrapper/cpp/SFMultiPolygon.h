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

#ifndef JAVACPP_SFCGAL_MultiPolygon_H
#define JAVACPP_SFCGAL_MultiPolygon_H

#include <SFCGAL/MultiPolygon.h>
#include "SFGeometryCollection.h"
#include "SFPolygon.h"

class SFMultiPolygon : public SFGeometryCollection {
public:	
	SFMultiPolygon() : SFGeometryCollection(new SFCGAL::MultiPolygon()) { }
	//SFMultiPolygon(const SFMultiPolygon& other) : SFGeometry(other.data) { }
	SFMultiPolygon(const SFCGAL::MultiPolygon& other) : SFGeometryCollection(new SFCGAL::MultiPolygon(other)) { }
	//SFMultiPolygon(SFCGAL::MultiPolygon& other) : SFGeometry(new SFCGAL::MultiPolygon(other)) { }
	SFMultiPolygon(SFCGAL::MultiPolygon* other) : SFGeometryCollection(other) { }

	SFMultiPolygon& operator=(const SFMultiPolygon& other) {
		data = other.data;
		
		return *this;
	}
	
	~SFMultiPolygon() { }
	
	
	//--SFCGAL::Geometry
	SFMultiPolygon* clone() const {
		return new SFMultiPolygon(*this);
	}
	
	std::string geometryType() const {
		return data->geometryType();
	}	
	
	int geometryTypeId() const {
		return data->geometryTypeId();
	}
	

	const SFPolygon& polygonN( size_t const & n) const {
		return *(new SFPolygon(((SFCGAL::MultiPolygon *)data)->polygonN(n)));
	}
	
	SFPolygon& polygonN(size_t const& n) {
		return *(new SFPolygon(((SFCGAL::MultiPolygon *)data)->polygonN(n)));
	}
	
};

#endif
