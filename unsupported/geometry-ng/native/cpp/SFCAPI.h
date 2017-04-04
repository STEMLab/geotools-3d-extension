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

#include <SFCGAL/capi/sfcgal_c.h>
#include <string>
#include <iostream>

#include "SFGeometry.h"
#include "SFPreparedGeometry.h"
#include "SFTriangulatedSurface.h"

SFGeometry& SFCGAL_io_read_wkt( const std::string& str, size_t len ) {
	SFCGAL::Geometry* p = (SFCGAL::Geometry *)sfcgal_io_read_wkt(str.c_str(), len);

	SFGeometry *geometry = new SFGeometry(p);

	return *geometry;
}

void SFCGAL_io_write_binary_prepared( SFPreparedGeometry* geom, char* buffer, size_t len ) {
	std::cout << "binary prepared call" << std::endl;
	sfcgal_io_write_binary_prepared(geom, &buffer, &len);
}


SFPreparedGeometry& SFCGAL_io_read_binary_prepared( const std::string& str, size_t len ) {
	SFCGAL::PreparedGeometry* p = (SFCGAL::PreparedGeometry *)sfcgal_io_read_binary_prepared(str.c_str(), len);
	SFGeometry *geometry = new SFGeometry(p->geometry());
	SFPreparedGeometry *pg = new SFPreparedGeometry();
	pg->resetGeometry(geometry);
	pg->SRID() = p->SRID();

	return *pg;
}

SFPreparedGeometry& SFCGAL_io_read_ewkt( const std::string& str, size_t len ) {
	SFCGAL::PreparedGeometry* p = (SFCGAL::PreparedGeometry *)sfcgal_io_read_ewkt(str.c_str(), len);

	SFGeometry *geometry = new SFGeometry(p->geometry());
	SFPreparedGeometry *pg = new SFPreparedGeometry();
	pg->resetGeometry(geometry);
	pg->SRID() = p->SRID();

	return *pg;
}

SFGeometry& SFCGAL_geometry_force_lhr( const SFGeometry& g ) {
	SFCGAL::Geometry* p = (SFCGAL::Geometry *)sfcgal_geometry_force_lhr(g.get_data());

	SFGeometry *geometry = new SFGeometry(p);

	return *geometry;
}

SFGeometry& SFCGAL_geometry_force_rhr( const SFGeometry& g ) {
	SFCGAL::Geometry* p = (SFCGAL::Geometry *)sfcgal_geometry_force_lhr(g.get_data());

	SFGeometry *geometry = new SFGeometry(p);

	return *geometry;
}

SFTriangulatedSurface& SFCGAL_geometry_triangulate_2dz( const SFGeometry& g ) {
	SFCGAL::TriangulatedSurface* p = (SFCGAL::TriangulatedSurface *)sfcgal_geometry_triangulate_2dz(g.get_data());

	SFTriangulatedSurface *surf = new SFTriangulatedSurface(p);

	return *surf;
}
