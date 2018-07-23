/**
 * 
 */
package org.geotools.iso.tutorial;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;
import org.citygml4j.model.gml.geometry.primitives.DirectPosition;
import org.citygml4j.model.gml.geometry.primitives.LinearRing;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @author hgryoo
 * from https://github.com/rchr/citygmldiff/blob/master/src/main/java/de/igg/citygmldiff/citygmldiff/util/CityGMLToJTS.java
 */
public class CityGML2JTS {

    private static GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * Converts given {@link org.citygml4j.model.gml.geometry.primitives.Envelope} into an {@link com.vividsolutions.jts.geom.Envelope}.
     *
     * @param envelope
     * @return
     */
    public static Envelope toJtsEnvelope(org.citygml4j.model.gml.geometry.primitives.Envelope envelope) {
        DirectPosition lowerCorner = envelope.getLowerCorner();
        DirectPosition upperCorner = envelope.getUpperCorner();
        Coordinate lowerCoordinate = toCoordinate(lowerCorner);
        Coordinate upperCoordinate = toCoordinate(upperCorner);
        Envelope jtsEnvelope = new Envelope(lowerCoordinate, upperCoordinate);

        return jtsEnvelope;
    }

    /**
     * Converts given {@link org.citygml4j.model.gml.geometry.primitives.DirectPosition} into an {@link com.vividsolutions.jts.geom.Coordinate}.
     *
     * @param directPosition
     * @return
     */
    public static Coordinate toCoordinate(DirectPosition directPosition) {
        List<Double> positions = directPosition.getValue();
        return new Coordinate(positions.get(1), positions.get(0), positions.get(2));
    }

    public static MultiPolygon toMultiPolygon(List<AbstractBoundarySurface> list) {
    	int size = list.size();
    	List<Polygon> polygons = new ArrayList<Polygon>();
    	for(int i = 0; i < size; i++) {
    		List<Polygon> polys = toPolygon(list.get(i));
    		polygons.addAll(polys);
    	}
    	Polygon[] array = polygons.toArray(new Polygon[polygons.size()]);
    	return geometryFactory.createMultiPolygon(array);
    }
    
    /**
     * Transforms the given {@link org.citygml4j.model.citygml.building.BoundarySurfaceProperty} into a {@link com.vividsolutions.jts.geom.Polygon}.
     *
     * @param boundarySurfaceProperty
     * @return
     */
    public static List<Polygon> toPolygon(AbstractBoundarySurface boundarySurface) {

        if (boundarySurface == null) {
            return null;
        }
        
        MultiSurfaceProperty multiSurfaceProperty = boundarySurface.getLod2MultiSurface();
        if (multiSurfaceProperty == null) {
            multiSurfaceProperty = boundarySurface.getLod3MultiSurface();
        }
        if (multiSurfaceProperty == null) {
            multiSurfaceProperty = boundarySurface.getLod4MultiSurface();
        }
        if (multiSurfaceProperty == null) {
            return null;
        }
        MultiSurface multiSurface = multiSurfaceProperty.getGeometry();
        if (multiSurface == null) {
            return null;
        }
        List<SurfaceProperty> surfaceMember = multiSurface.getSurfaceMember();
        if (surfaceMember == null) {
            return null;
        }

        List<Polygon> polygons = new ArrayList<Polygon>();
        int polygonIndex = 0;
        for (SurfaceProperty surfaceProp : surfaceMember) {
            if (surfaceProp.getObject() instanceof org.citygml4j.model.gml.geometry.primitives.Polygon) {
                org.citygml4j.model.gml.geometry.primitives.Polygon p = (org.citygml4j.model.gml.geometry.primitives.Polygon) surfaceProp.getObject();
                LinearRing linearRing = (LinearRing) p.getExterior().getObject();
                List<Double> linearRingCoordinated = linearRing.getPosList().getValue();
                Coordinate[] coords = new Coordinate[linearRingCoordinated.size() / 3];
                int index = 0;
                for (int i = 0; i <= linearRingCoordinated.size() - 3; i += 3) {
                    double y = linearRingCoordinated.get(i);
                    double x = linearRingCoordinated.get(i + 1);
                    double z = linearRingCoordinated.get(i + 2);
                    Coordinate c = new Coordinate(x, y, z);
                    coords[index] = c;
                    index++;
                }

                com.vividsolutions.jts.geom.LinearRing linearRing1 = geometryFactory.createLinearRing(coords);
                com.vividsolutions.jts.geom.Polygon polygon = geometryFactory.createPolygon(linearRing1);
                polygons.add(polygon);
                polygonIndex++;
            }
        }
        
        //MultiPolygon multiPolygon1 = geometryFactory.createMultiPolygon(polygons);
        // We have to buffer to prevent JTS' topology side exceptions.
        //return (Polygon) multiPolygon1.buffer(0);
        
        return polygons;
    }

    public static Geometry toPolygon(List<LinearRing> linearRings) {
        int ringIndex = 0;
        Polygon[] polygons = new Polygon[linearRings.size()];
        for (LinearRing ring : linearRings) {
            List<Double> linearRingCoordinated = ring.getPosList().getValue();
            Coordinate[] coords = new Coordinate[linearRingCoordinated.size() / 3];
            int index = 0;
            for (int i = 0; i <= linearRingCoordinated.size() - 3; i += 3) {
                double y = linearRingCoordinated.get(i);
                double x = linearRingCoordinated.get(i + 1);
                double z = linearRingCoordinated.get(i + 2);
                Coordinate c = new Coordinate(x, y, z);
                coords[index] = c;
                index++;
            }
            com.vividsolutions.jts.geom.LinearRing linearRing1 = geometryFactory.createLinearRing(coords);
            com.vividsolutions.jts.geom.Polygon polygon = geometryFactory.createPolygon(linearRing1);
            polygons[ringIndex] = polygon;
            ringIndex++;
        }
        MultiPolygon multiPolygon1 = geometryFactory.createMultiPolygon(polygons);
        // We have to buffer to prevent JTS' topology side exceptions.
        //return multiPolygon1.buffer(0);
        return multiPolygon1;
    }

}