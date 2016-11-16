package org.geotools.util;

import org.opengis.geometry.Geometry;

import com.vividsolutions.jts.geom.Coordinate;

public class Util {
	static public Coordinate[] getCoordinateArray(Geometry geom) {
		double[] lowercorner = geom.getEnvelope().getLowerCorner().getCoordinate();
		double[] uppercorner = geom.getEnvelope().getUpperCorner().getCoordinate();
		Coordinate[] coordinates = new Coordinate[2];
		if(geom.getEnvelope().getDimension() == 2) {
			coordinates[0] = new Coordinate(lowercorner[0], lowercorner[1]);
			coordinates[1] = new Coordinate(uppercorner[0], uppercorner[1]);
		}
		else {
			coordinates[0] = new Coordinate(lowercorner[0], lowercorner[1], lowercorner[2]);
			coordinates[1] = new Coordinate(uppercorner[0], uppercorner[1], uppercorner[2]);
		}
		return coordinates;
	}
}
