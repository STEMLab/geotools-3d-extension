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

#ifndef JAVACPP_SFCGAL_Surface_H
#define JAVACPP_SFCGAL_Surface_H

#include <SFCGAL/Surface.h>
#include "SFGeometry.h"

class SFSurface : public SFGeometry {
public:
	SFSurface() : SFGeometry() { }
	//SFSurface(const SFSurface& other) : SFGeometry(other.data) { }
	//SFSurface(SFSurface* other) : SFGeometry(other->data) { }
	//SFSurface(const SFSFCGAL::Surface& other) : SFGeometry(new SFCGAL::Surface(other)) { }
	SFSurface(SFCGAL::Surface* other) : SFGeometry(other) { }

	~SFSurface() { }

	//--SFCGAL::Geometry
	int dimension() const {
		return data->dimension();
	}
};

#endif
