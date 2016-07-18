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

#ifndef JAVACPP_SFCGAL_LineString_H
#define JAVACPP_SFCGAL_LineString_H

#include <SFCGAL/LineString.h>
#include "SFGeometry.h"
#include "SFPoint.h"

class SFLineString : public SFGeometry {
public:	
	SFLineString() : SFGeometry(new SFCGAL::LineString()) { }
	SFLineString(const std::vector< void * > & points) {
		std::vector<SFCGAL::Point>* cpp_base_points = new std::vector<SFCGAL::Point>();

		for(size_t i=0; i<points.size(); i++){
			cpp_base_points->push_back( *(SFCGAL::Point *)(((SFPoint *)points.at(i))->get_data()) );
		}

		data = new SFCGAL::LineString(*cpp_base_points);
	}

	SFLineString(const SFPoint& startPoint, const SFPoint& endPoint)
	 : SFGeometry(new SFCGAL::LineString(	*(SFCGAL::Point *)(startPoint.get_data()),
						*(SFCGAL::Point *)(endPoint.get_data()) )) { }

	//LineString(const LineString& other) : Geometry(other.data) { }
	SFLineString(const SFCGAL::LineString& other) : SFGeometry(new SFCGAL::LineString(other)) { }
	//SFLineString(SFCGAL::LineString& other) : SFGeometry(new SFCGAL::LineString(other)) { }
	SFLineString(SFCGAL::LineString* other) : SFGeometry(other) { }

	SFLineString& operator=(const SFLineString& other) {
		data = other.data;
		
		return *this;
	}
	
	~SFLineString() { }
	
	
	//--SFCGAL::Geometry
	SFLineString* clone() const {
		return new SFLineString(*this);
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



	void clear() {
		((SFCGAL::LineString *)data)->clear();
	}
	
	void reverse() {
		((SFCGAL::LineString *)data)->reverse();
	}

	size_t numPoints() const {
		return ((SFCGAL::LineString *)data)->numPoints();
	}

	size_t numSegments() const {
		return ((SFCGAL::LineString *)data)->numSegments();
	}

	const SFPoint& pointN( size_t const & n) const {
		return *(new SFPoint(((SFCGAL::LineString *)data)->pointN(n)));
	}
	
	SFPoint& pointN(size_t const& n) {
		return *(new SFPoint(((SFCGAL::LineString *)data)->pointN(n)));
	}
	

	const SFPoint& startPoint() const {
		return *(new SFPoint(((SFCGAL::LineString *)data)->startPoint()));
	}
	
	SFPoint& startPoint() {
		return *(new SFPoint(((SFCGAL::LineString *)data)->startPoint()));
	}

	const SFPoint& endPoint() const {
		return *(new SFPoint(((SFCGAL::LineString *)data)->endPoint()));
	}

	SFPoint& endPoint() {
		return *(new SFPoint(((SFCGAL::LineString *)data)->endPoint()));
	}

	void addPoint(const SFPoint& p){
		((SFCGAL::LineString *)data)->addPoint( *(SFCGAL::Point *)(p.get_data()) );
	}

	void addPoint(SFPoint* p){
		((SFCGAL::LineString *)data)->addPoint( *(SFCGAL::Point *)(p->get_data()) );
	}

	bool isClosed() const {
		return ((SFCGAL::LineString *)data)->isClosed();
	}	
	
	// iterator begin() ;
	// iterator end() ;

	void reserve(const size_t& n){
		((SFCGAL::LineString *)data)->reserve(n);
	}

	//Point_2_const_iterator points_2_begin() const ;
	//POint_2_const_iterator points_2_end() const ;
	//std::pair<Point_2_const_iterator, Point_2_const_iterator > points_2() const;

	//Point_3_const_iterator points_3_begin() const ;
	//POint_3_const_iterator points_3_end() const ;
	//std::pair<Point_3_const_iterator, Point_2_const_iterator > points_3() const;

	//CGAL::Polygon_2<Kernel> toPolygon_2(bool fixOrientation = true) const;

	//void accept(GeometryVisitor& visitor);
	//void accept(ConstGeometryVisitor& visitor) const ;

};

#endif
