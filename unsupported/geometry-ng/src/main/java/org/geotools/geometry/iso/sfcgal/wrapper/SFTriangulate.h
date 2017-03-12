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

#include <SFCGAL/triangulate/triangulatePolygon.h>

#include "SFGeometry.h"
#include "SFTriangulatedSurface.h"

void triangulatePolygon3D(const SFGeometry& g, const SFTriangulatedSurface& triangulatedSurface) {
	SFCGAL::triangulate::triangulatePolygon3D(*(g.get_data()), *(SFCGAL::TriangulatedSurface *)(triangulatedSurface.get_data()));
}
