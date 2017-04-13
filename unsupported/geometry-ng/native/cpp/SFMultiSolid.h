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

#ifndef JAVACPP_SFCGAL_MultiSolid_H
#define JAVACPP_SFCGAL_MultiSolid_H

#include <SFCGAL/MultiSolid.h>
#include "SFGeometryCollection.h"
#include "SFSolid.h"

class SFMultiSolid : public SFGeometryCollection {
public:	
	SFMultiSolid() : SFGeometryCollection(new SFCGAL::MultiSolid()) { }
	//SFMultiSolid(const SFMultiSolid& other) : SFGeometry(other.data) { }
	SFMultiSolid(const SFCGAL::MultiSolid& other) : SFGeometryCollection(new SFCGAL::MultiSolid(other)) { }
	//SFMultiSolid(SFCGAL::MultiSolid& other) : SFGeometry(new SFCGAL::MultiSolid(other)) { }
	SFMultiSolid(SFCGAL::MultiSolid* other) : SFGeometryCollection(other) { }

	SFMultiSolid& operator=(const SFMultiSolid& other) {
		data = other.data;
		
		return *this;
	}
	
	~SFMultiSolid() { }
	
	
	//--SFCGAL::Geometry
	SFMultiSolid* clone() const {
		return new SFMultiSolid(*this);
	}
	
	std::string geometryType() const {
		return data->geometryType();
	}	
	
	int geometryTypeId() const {
		return data->geometryTypeId();
	}
	

	const SFSolid& solidN( size_t const & n) const {
		return *(new SFSolid(((SFCGAL::MultiSolid *)data)->solidN(n)));
	}
	
	SFSolid& solidN(size_t const& n) {
		return *(new SFSolid(((SFCGAL::MultiSolid *)data)->solidN(n)));
	}
	
};

#endif
