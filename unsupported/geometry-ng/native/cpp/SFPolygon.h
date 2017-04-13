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

#ifndef JAVACPP_SFCGAL_Polygon_H
#define JAVACPP_SFCGAL_Polygon_H

#include <SFCGAL/Polygon.h>
#include "SFGeometry.h"
#include "SFLineString.h"
#include "SFSurface.h"
#include "SFTriangle.h"

class SFPolygon : public SFSurface {
public:	
	SFPolygon() : SFSurface(new SFCGAL::Polygon()) { }
	SFPolygon(const std::vector< void * > & rings) {
		std::vector<SFCGAL::LineString>* cpp_base_rings = new std::vector<SFCGAL::LineString>();

		for(size_t i=0; i<rings.size(); i++){
			cpp_base_rings->push_back( *(SFCGAL::LineString *)(((SFLineString *)rings.at(i))->get_data()) );
		}

		data = new SFCGAL::Polygon(*cpp_base_rings);
	}
	SFPolygon(const SFLineString& exteriorRing) : SFSurface(new SFCGAL::Polygon(*(SFCGAL::LineString *)(exteriorRing.get_data()))) { }
	//SFPolygon(SFLineString* exteriorRing) : SFSurface(new SFCGAL::Polygon(*(SFCGAL::LineString *)(exteriorRing->get_data()))) { }
	//SFPolygon(const SFCGAL::LineString& exteriorRing) : SF(new SFCGAL::Polygon(exteriorRing)) { }
	//SFPolygon(SFCGAL::LineString* exteriorRing) : SFSurface(new SFCGAL::Polygon(*exteriorRing)) { }

	SFPolygon(SFTriangle& triangle) : SFSurface(new SFCGAL::Polygon(*(SFCGAL::Triangle *)(triangle.get_data()))) { }
	//SFPolygon(const SFCGAL::Triangle& triangle);

	//SFPolygon(const SFPolygon& other) : SFSurface(new SFCGAL::Polygon(*(SFCGAL::Polygon *)(other.data))) { }
	//SFPolygon(SFPolygon& other) : SFSurface(new SFCGAL::Polygon(*(SFCGAL::Polygon *)(other.data))) { }
	SFPolygon(const SFCGAL::Polygon& other) : SFSurface(new SFCGAL::Polygon(other)) { }
	SFPolygon(SFCGAL::Polygon* other) : SFSurface(other) { }

	//SFPolygon( const CGAL::Polygon_2< Kernel >& other );
	//SFPolygon( const CGAL::Polygon_with_holes_2< Kernel >& other );

	SFPolygon& operator=(const SFPolygon& other) {
		data = other.data;
		
		return *this;
	}
	
	~SFPolygon() { }
	
	
	//--SFCGAL::Geometry
	SFPolygon* clone() const {
		return new SFPolygon(*this);
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



	bool isCounterClockWiseOriented() {
		return ((SFCGAL::Polygon *)data)->isCounterClockWiseOriented();
	}
	
	void reverse() {
		((SFCGAL::Polygon *)data)->reverse();
	}

	
	SFLineString& exteriorRing() const { // not used
		return *(new SFLineString(((SFCGAL::Polygon *)data)->exteriorRing()));
	}

	SFLineString& exteriorRing() {
		return *(new SFLineString(((SFCGAL::Polygon *)data)->exteriorRing()));
	}

	void setExteriorRing(const SFLineString& ring) {
		((SFCGAL::Polygon *)data)->setExteriorRing(*(SFCGAL::LineString *)(ring.get_data()));
	}
	//void setExteriorRing(const SFCGAL::LineString& ring);
	
	bool hasInteriorRings() const {
		return ((SFCGAL::Polygon *)data)->hasInteriorRings();
	}

	size_t numInteriorRings() const {
		return ((SFCGAL::Polygon *)data)->numInteriorRings();
	}

	const SFLineString& interiorRingN(const size_t& n) const {
		return *(new SFLineString(((SFCGAL::Polygon *)data)->interiorRingN(n)));
	}

	SFLineString& interiorRingN(const size_t& n) {
		return *(new SFLineString(((SFCGAL::Polygon *)data)->interiorRingN(n)));
	}

	size_t numRings() const {
		return ((SFCGAL::Polygon *)data)->numRings();
	}

	const SFLineString& ringN( size_t const & n) const {
		return *(new SFLineString(((SFCGAL::Polygon *)data)->ringN(n)));
	}
	
	SFLineString& ringN( size_t const & n) {
		return *(new SFLineString(((SFCGAL::Polygon *)data)->ringN(n)));
	}
	
	
	void addInteriorRing(const SFLineString& ls){
		((SFCGAL::Polygon *)data)->addInteriorRing(*(SFCGAL::LineString *)(ls.get_data()));
	}

	void addInteriorRing(SFLineString* ls){
		((SFCGAL::Polygon *)data)->addInteriorRing(*(SFCGAL::LineString *)(ls->get_data()));
	}
	//void addInteriorRing(SFCGAL::LineString* ls);

	void addRing( const SFLineString& ls ) {
		((SFCGAL::Polygon *)data)->addRing(*(SFCGAL::LineString *)(ls.get_data()));
	}

	void addRing( SFLineString* ls ) {
		((SFCGAL::Polygon *)data)->addRing(*(SFCGAL::LineString *)(ls->get_data()));
	}

	
	// iterator begin() ;
	// const_iterator begin();
	// iterator end() ;
	// const_iterator end();

	//CGAL::Polygon_2<Kernel> toPolygon_2(bool fixOrientation = true) const;
	//CGAL::Polygon_2<Kernel> toPolygon_with_holes_2(bool fixOrientation = true) const;

	//void accept(GeometryVisitor& visitor);
	//void accept(ConstGeometryVisitor& visitor) const ;
};

#endif
