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

#ifndef JAVACPP_SFCGAL_Envelope_H
#define JAVACPP_SFCGAL_Envelope_H

#include <SFCGAL/Envelope.h>
#include "SFCoordinate.h"

class SFLineString;
class SFPolygon;
class SFSolid;

class SFEnvelope{
	SFCGAL::Envelope data;
public:
	typedef SFCGAL::Envelope cpp_base;
	const cpp_base& get_data() const { return data; }
	cpp_base& get_data() { return data; }
	
	SFEnvelope() : data() { }
	SFEnvelope(
		const double& xmin, const double& xmax,
		const double& ymin, const double& ymax) : data(xmin, xmax, ymin, ymax) { }
	SFEnvelope(
		const double& xmin, const double& xmax,
		const double& ymin, const double& ymax,
		const double& zmin, const double& zmax) : data(xmin, xmax, ymin, ymax, zmin, zmax) { }
	SFEnvelope(const SFCoordinate& p) : data(p.get_data()) { }
	SFEnvelope(const SFCoordinate& p1, const SFCoordinate& p2) : data(p1.get_data(), p2.get_data()) { }
	//SFEnvelope(const Kernel::Point2& other) : data(other) { }
	//SFEnvelope(const Kernel::Point3& other) : data(other) { }
	SFEnvelope(const SFEnvelope& other) : data(other.data) { }
	SFEnvelope(const SFCGAL::Envelope& other) : data(other) { }
	
	SFEnvelope& operator=(const SFEnvelope& other) {
		data = other.data;
		
		return *this;
	}
	
	~SFEnvelope() { }
	

	bool isEmpty() const {
		return data.isEmpty();
	}

	bool is3D() const {
		return data.is3D();
	}

	void expandToInclude(const SFCoordinate& coordinate) {
		data.expandToInclude(coordinate.get_data());
	}

	const double& xMin() const {
		return data.xMin();
	}

	const double& yMin() const {
		return data.yMin();
	}

	const double& zMin() const {
		return data.zMin();
	}

	const double& xMax() const {
		return data.xMax();
	}

	const double& yMax() const {
		return data.yMax();
	}

	const double& zMax() const {
		return data.zMax();
	}

	//detail::interval boundsN(const size_t& n);
	//const detail::Interval boundsN(const size_t& n) const;

	//CGAL::Bbox_2 toBbox_2() const;
	//CGAL::Bbox_3 toBbox_3() const;

	static bool contains(const SFEnvelope& a, const SFEnvelope& b) {
		return SFCGAL::Envelope::contains(a.data, b.data);
	}

	static bool overlaps(const SFEnvelope& a, const SFEnvelope& b) {
		return SFCGAL::Envelope::overlaps(a.data, b.data);
	}

	SFLineString& toRing() const;
	/*
	LineString& toRing() const {
		std::auto_ptr<SFCGAL::LineString> p = data.toRing();
		
		LineString *lineString = new LineString(p.release());

		return *lineString;
	}
	*/

	SFPolygon& toPolygon() const;
	/*
	Polygon& toPolygon() const {
		std::auto_ptr<SFCGAL::Polygon> p = data.toPolygon();

		Polygon *polygon = new Polygon(p.release());

		return *polygon;
	}
	*/

	SFSolid& toSolid() const;
	/*	
	Solid& toSolid() const {
		std::auto_ptr<SFCGAL::Solid> p = data.toSolid();

		Solid *solid = new Solid(p.release());

		return *solid;
	}
	*/

	bool operator==(const SFEnvelope& other) {
		return (this->data == other.data);
	}
};

#endif
