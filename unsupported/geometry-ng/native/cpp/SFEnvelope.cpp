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

#include "SFEnvelope.h"
#include "SFLineString.h"
#include "SFPolygon.h"
#include "SFSolid.h"

SFLineString& SFEnvelope::toRing() const {
	std::auto_ptr<SFCGAL::LineString> p = data.toRing();
	
	SFLineString *lineString = new SFLineString(p.release());

	return *lineString;
}

SFPolygon& SFEnvelope::toPolygon() const {
	std::auto_ptr<SFCGAL::Polygon> p = data.toPolygon();

	SFPolygon *polygon = new SFPolygon(p.release());

	return *polygon;
}

SFSolid& SFEnvelope::toSolid() const {
	std::auto_ptr<SFCGAL::Solid> p = data.toSolid();

	SFSolid *solid = new SFSolid(p.release());

	return *solid;
}
