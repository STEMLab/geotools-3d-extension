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

#ifndef JAVACPP_SFCGAL_Solid_H
#define JAVACPP_SFCGAL_Solid_H

#include <SFCGAL/Solid.h>
#include "SFPolyhedralSurface.h"

class SFSolid : public SFGeometry {
public:	
	SFSolid() : SFGeometry(new SFCGAL::Solid()) { }
	
	SFSolid(const std::vector< void * > & shells) {
		std::vector<SFCGAL::PolyhedralSurface>* cpp_base_shell = new std::vector<SFCGAL::PolyhedralSurface>();

		for(size_t i=0; i<shells.size(); i++){
			cpp_base_shell->push_back( *(SFCGAL::PolyhedralSurface *)(((SFPolyhedralSurface *)shells.at(i))->get_data()) );
		}

		data = new SFCGAL::Solid(*cpp_base_shell);
	}
	
	SFSolid(const SFPolyhedralSurface& exteriorShell) : SFGeometry(new SFCGAL::Solid(*(SFCGAL::PolyhedralSurface *)(exteriorShell.get_data()))) { }
	//Polygon(PolyhedralSurface* exteriorShell) : Surface(new SFCGAL::Solid(*(SFCGAL::PolyhedralSurface *)(exteriorShell->get_data()))) { }
	//SFSolid(const SFSolid& other) : SFSurface(new SFCGAL::Solid(*other.data)) { }
	
	SFSolid(const SFCGAL::Solid& other) : SFGeometry(new SFCGAL::Solid(other)) { }
	
	SFSolid(SFCGAL::Solid* other) : SFGeometry(other) { }

	SFSolid& operator=(const SFSolid& other) {
		data = other.data;
		
		return *this;
	}
	
	~SFSolid() { }
	
	
	//--SFCGAL::Geometry
	SFSolid* clone() const {
		return new SFSolid(*this);
	}
	
	std::string geometryType() const {
		return data->geometryType();
	}	
	
	int geometryTypeId() const {
		return data->geometryTypeId();
	}
	
	int dimension() const {
		return data->dimension();
	}
	
	int coordinateDimension() const {
		return data->coordinateDimension();
	}

	bool isEmpty() const {
		return data->isEmpty();
	}

	bool is3D() const {
		return data->is3D();
	}
	
	bool isMeasured() const {
		return data->isMeasured();
	}

	
	SFPolyhedralSurface& exteriorShell() const {
		return *(new SFPolyhedralSurface(((SFCGAL::Solid *)data)->exteriorShell()));
	}

	SFPolyhedralSurface& exteriorShell() {
		return *(new SFPolyhedralSurface(((SFCGAL::Solid *)data)->exteriorShell()));
	}

	size_t numInteriorShells() const {
		return ((SFCGAL::Solid *)data)->numInteriorShells();
	}

	const SFPolyhedralSurface& interiorShellN( size_t const & n) const {
		return *(new SFPolyhedralSurface(((SFCGAL::Solid *)data)->interiorShellN(n)));
	}
	
	SFPolyhedralSurface& interiorShellN( size_t const & n) {
		return *(new SFPolyhedralSurface(((SFCGAL::Solid *)data)->interiorShellN(n)));
	}

	void addInteriorShell( const SFPolyhedralSurface& shell ) {
		((SFCGAL::Solid *)data)->addInteriorShell(*(SFCGAL::PolyhedralSurface *)(shell.get_data()));
	}

	void addInteriorShell( SFPolyhedralSurface* shell ) {
		((SFCGAL::Solid *)data)->addInteriorShell(*(SFCGAL::PolyhedralSurface *)(shell->get_data()));
	}

	size_t numShells() const {
		return ((SFCGAL::Solid *)data)->numShells();
	}

	SFPolyhedralSurface& shellN( size_t const & n) const {
		return *(new SFPolyhedralSurface(((SFCGAL::Solid *)data)->shellN(n)));
	}

	SFPolyhedralSurface& shellN( size_t const & n) {
		return *(new SFPolyhedralSurface(((SFCGAL::Solid *)data)->shellN(n)));
	}

	//iterator begin();
	//iterator begin() const;
	//iterator end();
	//iterator end() const;	

	//void accept(GeometryVisitor& visitor);
	//void accept(ConstGeometryVisitor& visitor) const ;
};

#endif
