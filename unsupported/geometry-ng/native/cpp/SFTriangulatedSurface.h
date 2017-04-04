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

#ifndef JAVACPP_SFCGAL_TriangulatedSurface_H
#define JAVACPP_SFCGAL_TriangulatedSurface_H

#include <SFCGAL/TriangulatedSurface.h>
#include "SFPoint.h"
#include "SFSurface.h"
#include "SFTriangle.h"

class SFTriangulatedSurface : public SFSurface {
public:	
	SFTriangulatedSurface() : SFSurface(new SFCGAL::TriangulatedSurface()) { }
	SFTriangulatedSurface(const std::vector< void * > & triangle) {
		std::vector<SFCGAL:: Triangle>* cpp_base_triangle = new std::vector<SFCGAL::Triangle>();

		for(size_t i=0; i<triangle.size(); i++){
			cpp_base_triangle->push_back( *(SFCGAL::Triangle *)(((SFTriangle *)triangle.at(i))->get_data()) );
		}

		data = new SFCGAL::TriangulatedSurface(*cpp_base_triangle);
	}
	
	//TriangulatedSurface(const TriangulatedSurface& other) : Surface(new SFCGAL::TriangulatedSurface(*other.data)) { }
	SFTriangulatedSurface(const SFCGAL::TriangulatedSurface& other) : SFSurface(new SFCGAL::TriangulatedSurface(other)) { }
	SFTriangulatedSurface(SFCGAL::TriangulatedSurface* other) : SFSurface(other) { }
	//SFTriangulatedSurface( const CGAL::TriangulatedSurface_2& other );
	//SFTriangulatedSurface( const CGAL::TriangulatedSurface_3& other );

	SFTriangulatedSurface& operator=(const SFTriangulatedSurface& other) {
		data = other.data;
		
		return *this;
	}
	
	~SFTriangulatedSurface() { }
	
	
	//--SFCGAL::Geometry
	SFTriangulatedSurface* clone() const {
		return new SFTriangulatedSurface(*this);
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

	
	size_t numTriangles() const {
		return ((SFCGAL::TriangulatedSurface *)data)->numTriangles();
	}

	const SFTriangle& triangleN( size_t const& n ) const {
		return *(new SFTriangle(((SFCGAL::TriangulatedSurface *)data)->triangleN(n)));
	}
	
	SFTriangle& triangleN( size_t const& n) {
		return *(new SFTriangle(((SFCGAL::TriangulatedSurface *)data)->triangleN(n)));
	}

	void addTriangle( const SFTriangle& triangle ) {
		((SFCGAL::TriangulatedSurface *)data)->addTriangle(*(SFCGAL::Triangle *)(triangle.get_data()));
	}

	void addTriangle( SFTriangle* triangle ) {
		((SFCGAL::TriangulatedSurface *)data)->addTriangle(*(SFCGAL::Triangle *)(triangle->get_data()));
	}

	
	void addTriangles( SFTriangulatedSurface& other ) {
		((SFCGAL::TriangulatedSurface *)data)->addTriangles(
			*(new SFCGAL::TriangulatedSurface( *((SFCGAL::TriangulatedSurface *)other.data)) )
			);
	}

	size_t numGeometries() const {
		return ((SFCGAL::TriangulatedSurface *)data)->numGeometries();
	}

	/*
	const SFTriangle& geometryN(size_t const& n) const {
		return *(new SFTriangle( (((SFCGAL::TriangulatedSurface *)data)->geometryN(n)).clone() ));
	}
	*/

	SFTriangle& geometryN(size_t const& n) const {
		return *(new SFTriangle( ((SFCGAL::TriangulatedSurface *)data)->geometryN(n) ));
	}

	void reserve(const size_t& n) {
		((SFCGAL::TriangulatedSurface *)data)->reserve(n);
	}

/* Polyhedron -> CGAL::Polyhedron_3
	Polyhedron toPolyhedron_3() const { //virtual std::auto_ptr<Polyehedron> toPolyhedron_3() const;
		//if(data == NULL) return NULL;
		std::auto_ptr<SFCGAL::Polyhedron> ph = ((SFCGAL::TrangulatedSurface *)data)->toPolyhedron_3();

		Polyhedron *polyhedron = new Polyhedron(ph.release);

		return *polyhedron;
	}
*/
	//iterator begin();
	//iterator begin() const;
	//iterator end();
	//iterator end() const;
	
	//Kernel::TriangulatedSurface_2 toTriangulatedSurface_2() const;
	//Kernel::TriangulatedSurface_3 toTriangulatedSurface_3() const;
	

	//void accept(GeometryVisitor& visitor);
	//void accept(ConstGeometryVisitor& visitor) const ;
};

#endif
