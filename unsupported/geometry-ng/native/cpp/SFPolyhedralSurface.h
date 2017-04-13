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

#ifndef JAVACPP_SFCGAL_PolyhedralSurface_H
#define JAVACPP_SFCGAL_PolyhedralSurface_H

#include <SFCGAL/PolyhedralSurface.h>
#include "SFPoint.h"
#include "SFSurface.h"
#include "SFPolygon.h"
#include "SFTriangulatedSurface.h"

class SFPolyhedralSurface : public SFSurface {
public:	
	SFPolyhedralSurface() : SFSurface(new SFCGAL::PolyhedralSurface()) { }
	SFPolyhedralSurface(const std::vector< void * > & polygons) {
		std::vector<SFCGAL:: Polygon>* cpp_base_polygon = new std::vector<SFCGAL::Polygon>();

		for(size_t i=0; i<polygons.size(); i++){
			cpp_base_polygon->push_back( *(SFCGAL::Polygon *)(((SFPolygon *)polygons.at(i))->get_data()) );
		}

		data = new SFCGAL::PolyhedralSurface(*cpp_base_polygon);
	}
	
	//SFPolyhedralSurface(const SFPolyhedralSurface& other) : SFSurface(new SFCGAL::PolyhedralSurface(*other.data)) { }
	SFPolyhedralSurface(const SFCGAL::PolyhedralSurface& other) : SFSurface(new SFCGAL::PolyhedralSurface(other)) { }
	SFPolyhedralSurface(SFCGAL::PolyhedralSurface* other) : SFSurface(other) { }
	//SFPolyhedralSurface(const detail::MarkedPolyhedron& poly);	

	//SFPolyhedralSurface( const CGAL::PolyhedralSurface_2& other );
	//SFPolyhedralSurface( const CGAL::PolyhedralSurface_3& other );

	SFPolyhedralSurface& operator=(const SFPolyhedralSurface& other) {
		data = other.data;
		
		return *this;
	}
	
	~SFPolyhedralSurface() { }
	
	
	//--SFCGAL::Geometry
	SFPolyhedralSurface* clone() const {
		return new SFPolyhedralSurface(*this);
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


	SFTriangulatedSurface& toTriangulatedSurface() const{
		return *(new SFTriangulatedSurface(((SFCGAL::PolyhedralSurface *)data)->toTriangulatedSurface()));
	}
	
	size_t numPolygons() const {
		return ((SFCGAL::PolyhedralSurface *)data)->numPolygons();
	}

	const SFPolygon& polygonN( size_t const & n) const {
		return *(new SFPolygon(((SFCGAL::PolyhedralSurface *)data)->polygonN(n)));
	}
	
	SFPolygon& polygonN( size_t const & n) {
		return *(new SFPolygon(((SFCGAL::PolyhedralSurface *)data)->polygonN(n)));
	}

	void addPolygon( const SFPolygon& polygon ) {
		((SFCGAL::PolyhedralSurface *)data)->addPolygon(*(SFCGAL::Polygon *)(polygon.get_data()));
	}

	void addPolygon( SFPolygon* polygon ) {
		((SFCGAL::PolyhedralSurface *)data)->addPolygon(*(SFCGAL::Polygon *)(polygon->get_data()));
	}
	
	void addPolygons( SFPolyhedralSurface& other ) {
		((SFCGAL::PolyhedralSurface *)data)->addPolygons(
			*(new SFCGAL::PolyhedralSurface( *((SFCGAL::PolyhedralSurface *)other.data)) )
			);
	}

	size_t numGeometries() const {
		return ((SFCGAL::PolyhedralSurface *)data)->numGeometries();
	}
/*
	const SFTriangle& geometryN(size_t const& n) const {
		return *(new SFTriangle( ((SFCGAL::PolyhedralSurface *)data)->geometryN(n) ));
	}
*/
	SFPolygon& geometryN(size_t const& n) const {
		return *(new SFPolygon( ((SFCGAL::PolyhedralSurface *)data)->geometryN(n) ));
	}

/*	Polyhedron -> CGAL::Polyhedron_3
	Polyhedron toPolyhedron_3() const { //virtual std::auto_ptr<Polyehedron> toPolyhedron_3() const;
		//if(data == NULL) return NULL;
		std::auto_ptr<SFCGAL::Polyhedron> ph = ((SFCGAL::PolyhedralSurface *)data)->toPolyhedron_3();

		Polyhedron *polyhedron = new Polyhedron(ph.release());

		return *polyhedron;
	}
*/
	//iterator begin();
	//iterator begin() const;
	//iterator end();
	//iterator end() const;
	
	//Kernel::PolyhedralSurface_2 toPolyhedralSurface_2() const;
	//Kernel::PolyhedralSurface_3 toPolyhedralSurface_3() const;
	

	//void accept(GeometryVisitor& visitor);
	//void accept(ConstGeometryVisitor& visitor) const ;
};

#endif
