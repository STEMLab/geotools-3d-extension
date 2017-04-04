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

#ifndef JAVACPP_SFCGAL_Geometry_H
#define JAVACPP_SFCGAL_Geometry_H

#include <SFCGAL/Geometry.h>
#include "SFEnvelope.h"

class SFPoint;
class SFLineString;
class SFPolygon;
class SFGeometryCollection;
class SFMultiPoint;
class SFMultiLineString;
class SFMultiPolygon;

class SFTriangle;
class SFTriangulateSurface;
class SFPolyhedralSurface;

class SFSolid;
class SFMultiSolid;
//class Grid;

class SFEnvelope;

class SFGeometry{
protected:
	SFCGAL::Geometry* data;
public:
	typedef SFCGAL::Geometry cpp_base;
	const cpp_base* get_data() const { return data; }
	cpp_base* get_data() { return data; }

	SFGeometry() : data(NULL) { }
	SFGeometry(const SFGeometry& other) : data(other.data) { }
	SFGeometry(SFCGAL::Geometry& other) : data(&other){ }
	//SFGeometry(const SFCGAL::Geometry& other) : data(&other){ }
	//SFGeometry(const SFCGAL::Geometry* other) : data(other){ }
	SFGeometry(SFCGAL::Geometry* other) : data(other){ }
	
	virtual ~SFGeometry() {
		if(data != NULL) delete data;
	}
	
	//--SFCGAL::Geometry
	virtual SFGeometry* clone() const {
		if(data == NULL) return NULL;

		return new SFGeometry(data->clone());
	}

	virtual std::string geometryType() const {
		if(data == NULL) return "";

		return data->geometryType();
	}
	
	virtual int geometryTypeId() const {
		if(data == NULL) return -1;

		return data->geometryTypeId();
	}
	
	virtual int dimension() const {
		if(data == NULL) return -1;

		return data->dimension();
	}
	
	virtual int coordinateDimension() const {
		if(data == NULL) return -1;

		return data->coordinateDimension();
	}

	virtual bool isEmpty() const {
		if(data == NULL) return false;
		
		return data->isEmpty();
	}


	virtual bool is3D() const {
		if(data == NULL) return false;

		return data->is3D();
	}
	
	virtual bool isMeasured() const {
		if(data == NULL) return false;

		return data->isMeasured();
	}

	std::string asText(const int& numDecimals = -1) const {
		if(data == NULL) return "";

		return data->asText(numDecimals);
	}

	SFEnvelope& envelope() const {
		return *(new SFEnvelope(data->envelope()));
	}
		
	virtual SFGeometry& boundary() const {	//virtual std::auto_ptr<Geometry> boundary() const;
		//if(data == NULL) return NULL;
		std::auto_ptr<SFCGAL::Geometry> p = data->boundary();

		SFGeometry *geometry = new SFGeometry(p.release());

		return *geometry;
	}

	double distance(const SFGeometry& other) const {
		if(data == NULL) return -1;

		return data->distance(*(other.get_data()));
	}

	double distance3D(const SFGeometry& other) const {
		if(data == NULL) return -1;

		return data->distance3D(*(other.get_data()));
	}

	void round(const long& scale = 1) {
		if(data == NULL) return ;

		data->round(scale);
	}

	virtual size_t numGeometries() const {
		//if(data == NULL) return -1;

		return data->numGeometries();
	}
	
	virtual const SFGeometry& geometryN(size_t const& n) const {
		return *(new SFGeometry(data->geometryN(n)));
	}

	virtual SFGeometry& geometryN(size_t const &n) {
		return *(new SFGeometry(data->geometryN(n)));
	}

	//template<typename Derived>
	//bool is() const;

	//template<typename Derived>
	//Derived& as();
	/*
	virtual void accept(SFCGAL::GeometryVisitor& visitor) {
		if(data == NULL) return;

		data->accept(visitor);
	}

	virtual void accept(SFCGAL::ConstGeometryVisitor& visitor) {
		if(data == NULL) return;

		data->accept(visitor);
	}
	*/

	bool operator==(const SFGeometry& other){
		return ( *data == *(other.data) );
	}
};

#endif
