package org.geotools.geometry.iso.util;


import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.gml3.iso.GML;
import org.geotools.xml.XSD;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SolidUtil {
	static XSD gml = GML.getInstance();
	public static QName qName(String local) {
        return gml.qName(local);
    }
	   public static Element element(QName name, Document document, Node parent) {
	        Element element = document.createElementNS(name.getNamespaceURI(), name.getLocalPart());

	        if (parent != null) {
	            parent.appendChild(element);
	        }

	        return element;
	    }
public static Element makeFromEnvelope(Document document, Node parent, double[] upper, double[] lower){
	Element compositeSurface = element(qName("CompositeSurface"),document,parent);
	Element member = element(qName("surfaceMember"),document,compositeSurface);
	Element pol1 = element(qName("Polygon"),document,member);
	Element pol1ex = element(qName("exterior"),document,pol1);
	Element lr = element(qName("LinearRing"),document, pol1ex);
	lr.setAttribute("srsDimension", "3");
	
	
	String Point1 = Double.toString(lower[0]) + " " + Double.toString(upper[1]) + " " + Double.toString(lower[2]);
	String Point2 = Double.toString(lower[0]) + " " + Double.toString(lower[1]) + " " + Double.toString(lower[2]);
	String Point3 = Double.toString(upper[0]) + " " + Double.toString(lower[1]) +" " +Double.toString(lower[2]);
	String Point4 = Double.toString(upper[0]) + " " + Double.toString(upper[1]) + " " + Double.toString(lower[2]);
	String Point5 = Double.toString(lower[0]) + " " + Double.toString(upper[1]) + " " + Double.toString(upper[2]);
	String Point6 = Double.toString(lower[0]) + " " + Double.toString(lower[1]) + " " + Double.toString(upper[2]);
	String Point7 = Double.toString(upper[0]) + " " + Double.toString(lower[1]) + " " + Double.toString(upper[2]);
	String Point8 = Double.toString(upper[0]) + " " + Double.toString(upper[1]) + " " + Double.toString(upper[2]);
	

	
	
	
	Element pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point1));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point4));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point3));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point2));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point1));
	
	Element member2 = element(qName("surfaceMember"),document,compositeSurface);
	Element pol2 = element(qName("Polygon"),document,member2);
	Element pol2ex = element(qName("exterior"),document, pol2);
	lr = element(qName("LinearRing"),document, pol2ex);
	lr.setAttribute("srsDimension", "3");
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point3));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point4));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point8));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point7));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point3));
	
	Element member3 = element(qName("surfaceMember"),document,compositeSurface);
	Element pol3 = element(qName("Polygon"),document,member3);
	Element pol3ex = element(qName("exterior"),document,pol3);
	lr = element(qName("LinearRing"),document, pol3ex);
	lr.setAttribute("srsDimension", "3");
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point5));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point6));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point7));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point8));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point5));
	
	Element member4 = element(qName("surfaceMember"),document,compositeSurface);
	Element pol4 = element(qName("Polygon"),document,member4);
	Element pol4ex = element(qName("exterior"),document,pol4);
	lr = element(qName("LinearRing"),document, pol4ex);
	lr.setAttribute("srsDimension", "3");
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point6));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point5));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point1));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point2));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point6));
	
	Element member5 = element(qName("surfaceMember"),document,compositeSurface);
	Element pol5 = element(qName("Polygon"),document,member5);
	Element pol5ex = element(qName("exterior"),document,pol5);
	 lr = element(qName("LinearRing"),document, pol5ex);
	lr.setAttribute("srsDimension", "3");
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point2));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point3));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point7));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point6));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point2));
	
	Element member6 = element(qName("surfaceMember"),document,compositeSurface);
	Element pol6 = element(qName("Polygon"),document,member6);
	Element pol6ex = element(qName("exterior"),document,pol6);
	lr = element(qName("LinearRing"),document, pol6ex);
	lr.setAttribute("srsDimension", "3");
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point1));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point5));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point8));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point4));
	
	pos = element(qName("pos"),document,lr);
	pos.appendChild(document.createTextNode(Point1));
	

	return member;

	
}
public static Solid makeFromEnvelope(ISOGeometryBuilder builder, DirectPosition l, DirectPosition u) {
	    
	    DirectPosition position1 = builder.createDirectPosition(new double[] { l.getOrdinate(0), u.getOrdinate(1), l.getOrdinate(2) }); //LUL
	    DirectPosition position2 = builder.createDirectPosition(new double[] { l.getOrdinate(0), l.getOrdinate(1), l.getOrdinate(2) }); //LLL
	    DirectPosition position3 = builder.createDirectPosition(new double[] { u.getOrdinate(0), l.getOrdinate(1), l.getOrdinate(2) }); //ULL
	    DirectPosition position4 = builder.createDirectPosition(new double[] { u.getOrdinate(0), u.getOrdinate(1), l.getOrdinate(2) }); //UUL
	    DirectPosition position5 = builder.createDirectPosition(new double[] { l.getOrdinate(0), u.getOrdinate(1), u.getOrdinate(2) }); //LUU
	    DirectPosition position6 = builder.createDirectPosition(new double[] { l.getOrdinate(0), l.getOrdinate(1), u.getOrdinate(2) }); //LLU
	    DirectPosition position7 = builder.createDirectPosition(new double[] { u.getOrdinate(0), l.getOrdinate(1), u.getOrdinate(2) }); //ULU
	    DirectPosition position8 = builder.createDirectPosition(new double[] { u.getOrdinate(0), u.getOrdinate(1), u.getOrdinate(2) }); //UUU
	    
	    List<Position> dps1 = new ArrayList<Position>();
            dps1.add(position1);
            dps1.add(position4);
            dps1.add(position3);
            dps1.add(position2);
            dps1.add(position1);

            List<Position> dps2 = new ArrayList<Position>();
            dps2.add(position3);
            dps2.add(position4);
            dps2.add(position8);
            dps2.add(position7);
            dps2.add(position3);

            List<Position> dps3 = new ArrayList<Position>();
            dps3.add(position5);
            dps3.add(position6);
            dps3.add(position7);
            dps3.add(position8);
            dps3.add(position5);

            List<Position> dps4 = new ArrayList<Position>();
            dps4.add(position6);
            dps4.add(position5);
            dps4.add(position1);
            dps4.add(position2);
            dps4.add(position6);

            List<Position> dps5 = new ArrayList<Position>();
            dps5.add(position2);
            dps5.add(position3);
            dps5.add(position7);
            dps5.add(position6);
            dps5.add(position2);

            List<Position> dps6 = new ArrayList<Position>();
            dps6.add(position1);
            dps6.add(position5);
            dps6.add(position8);
            dps6.add(position4);
            dps6.add(position1);

            // create linestring from directpositions
            LineString line1 = builder.createLineString(dps1);
            LineString line2 = builder.createLineString(dps2);
            LineString line3 = builder.createLineString(dps3);
            LineString line4 = builder.createLineString(dps4);
            LineString line5 = builder.createLineString(dps5);
            LineString line6 = builder.createLineString(dps6);

            // create curvesegments from line
            ArrayList<CurveSegment> segs1 = new ArrayList<CurveSegment>();
            segs1.add(line1);
            ArrayList<CurveSegment> segs2 = new ArrayList<CurveSegment>();
            segs2.add(line2);
            ArrayList<CurveSegment> segs3 = new ArrayList<CurveSegment>();
            segs3.add(line3);
            ArrayList<CurveSegment> segs4 = new ArrayList<CurveSegment>();
            segs4.add(line4);
            ArrayList<CurveSegment> segs5 = new ArrayList<CurveSegment>();
            segs5.add(line5);
            ArrayList<CurveSegment> segs6 = new ArrayList<CurveSegment>();
            segs6.add(line6);

            // Create list of OrientableCurves that make up the surface
            OrientableCurve curve1 = builder.createCurve(segs1);
            List<OrientableCurve> orientableCurves1 = new ArrayList<OrientableCurve>();
            orientableCurves1.add(curve1);
            OrientableCurve curve2 = builder.createCurve(segs2);
            List<OrientableCurve> orientableCurves2 = new ArrayList<OrientableCurve>();
            orientableCurves2.add(curve2);
            OrientableCurve curve3 = builder.createCurve(segs3);
            List<OrientableCurve> orientableCurves3 = new ArrayList<OrientableCurve>();
            orientableCurves3.add(curve3);
            OrientableCurve curve4 = builder.createCurve(segs4);
            List<OrientableCurve> orientableCurves4 = new ArrayList<OrientableCurve>();
            orientableCurves4.add(curve4);
            OrientableCurve curve5 = builder.createCurve(segs5);
            List<OrientableCurve> orientableCurves5 = new ArrayList<OrientableCurve>();
            orientableCurves5.add(curve5);
            OrientableCurve curve6 = builder.createCurve(segs6);
            List<OrientableCurve> orientableCurves6 = new ArrayList<OrientableCurve>();
            orientableCurves6.add(curve6);

            // create the interior ring and a list of empty interior rings (holes)
            Ring extRing1 = builder.createRing(orientableCurves1);
            Ring extRing2 = builder.createRing(orientableCurves2);
            Ring extRing3 = builder.createRing(orientableCurves3);
            Ring extRing4 = builder.createRing(orientableCurves4);
            Ring extRing5 = builder.createRing(orientableCurves5);
            Ring extRing6 = builder.createRing(orientableCurves6);

            // create surfaceboundary by rings
            SurfaceBoundary sb1 = builder.createSurfaceBoundary(extRing1, new ArrayList<Ring>());
            SurfaceBoundary sb2 = builder.createSurfaceBoundary(extRing2, new ArrayList<Ring>());
            SurfaceBoundary sb3 = builder.createSurfaceBoundary(extRing3, new ArrayList<Ring>());
            SurfaceBoundary sb4 = builder.createSurfaceBoundary(extRing4, new ArrayList<Ring>());
            SurfaceBoundary sb5 = builder.createSurfaceBoundary(extRing5, new ArrayList<Ring>());
            SurfaceBoundary sb6 = builder.createSurfaceBoundary(extRing6, new ArrayList<Ring>());

            // create the surface
            Surface surface1 = builder.createSurface(sb1);
            Surface surface2 = builder.createSurface(sb2);
            Surface surface3 = builder.createSurface(sb3);
            Surface surface4 = builder.createSurface(sb4);
            Surface surface5 = builder.createSurface(sb5);
            Surface surface6 = builder.createSurface(sb6);

            List<OrientableSurface> surfaces = new ArrayList<OrientableSurface>();
            surfaces.add(surface1);
            surfaces.add(surface2);
            surfaces.add(surface3);
            surfaces.add(surface4);
            surfaces.add(surface5);
            surfaces.add(surface6);

            Shell exteriorShell = builder.createShell(surfaces);
            List<Shell> interiors = new ArrayList<Shell>();

            SolidBoundary solidBoundary = builder.createSolidBoundary(exteriorShell, interiors);
            Solid solid = builder.createSolid(solidBoundary);

            return solid;
	}
	
public static Solid makeSolid(ISOGeometryBuilder builder, ArrayList<DirectPosition> points) {
	DirectPosition position1 = points.get(0);
	DirectPosition position2 = points.get(1);
	DirectPosition position3 = points.get(2);
	DirectPosition position4 = points.get(3);
	DirectPosition position5 = points.get(4);
	DirectPosition position6 = points.get(5);
	DirectPosition position7 = points.get(6);
	DirectPosition position8 = points.get(7);

	// create a list of connected positions
	List<Position> dps1 = new ArrayList<Position>();
	dps1.add(position1);
	dps1.add(position4);
	dps1.add(position3);
	dps1.add(position2);
	dps1.add(position1);

	List<Position> dps2 = new ArrayList<Position>();
	dps2.add(position3);
	dps2.add(position4);
	dps2.add(position8);
	dps2.add(position7);
	dps2.add(position3);

	List<Position> dps3 = new ArrayList<Position>();
	dps3.add(position5);
	dps3.add(position6);
	dps3.add(position7);
	dps3.add(position8);
	dps3.add(position5);

	List<Position> dps4 = new ArrayList<Position>();
	dps4.add(position6);
	dps4.add(position5);
	dps4.add(position1);
	dps4.add(position2);
	dps4.add(position6);

	List<Position> dps5 = new ArrayList<Position>();
	dps5.add(position2);
	dps5.add(position3);
	dps5.add(position7);
	dps5.add(position6);
	dps5.add(position2);

	List<Position> dps6 = new ArrayList<Position>();
	dps6.add(position1);
	dps6.add(position5);
	dps6.add(position8);
	dps6.add(position4);
	dps6.add(position1);

	// create linestring from directpositions
	LineString line1 = builder.createLineString(dps1);
	LineString line2 = builder.createLineString(dps2);
	LineString line3 = builder.createLineString(dps3);
	LineString line4 = builder.createLineString(dps4);
	LineString line5 = builder.createLineString(dps5);
	LineString line6 = builder.createLineString(dps6);

	// create curvesegments from line
	ArrayList<CurveSegment> segs1 = new ArrayList<CurveSegment>();
	segs1.add(line1);
	ArrayList<CurveSegment> segs2 = new ArrayList<CurveSegment>();
	segs2.add(line2);
	ArrayList<CurveSegment> segs3 = new ArrayList<CurveSegment>();
	segs3.add(line3);
	ArrayList<CurveSegment> segs4 = new ArrayList<CurveSegment>();
	segs4.add(line4);
	ArrayList<CurveSegment> segs5 = new ArrayList<CurveSegment>();
	segs5.add(line5);
	ArrayList<CurveSegment> segs6 = new ArrayList<CurveSegment>();
	segs6.add(line6);

	// Create list of OrientableCurves that make up the surface
	OrientableCurve curve1 = builder.createCurve(segs1);
	List<OrientableCurve> orientableCurves1 = new ArrayList<OrientableCurve>();
	orientableCurves1.add(curve1);
	OrientableCurve curve2 = builder.createCurve(segs2);
	List<OrientableCurve> orientableCurves2 = new ArrayList<OrientableCurve>();
	orientableCurves2.add(curve2);
	OrientableCurve curve3 = builder.createCurve(segs3);
	List<OrientableCurve> orientableCurves3 = new ArrayList<OrientableCurve>();
	orientableCurves3.add(curve3);
	OrientableCurve curve4 = builder.createCurve(segs4);
	List<OrientableCurve> orientableCurves4 = new ArrayList<OrientableCurve>();
	orientableCurves4.add(curve4);
	OrientableCurve curve5 = builder.createCurve(segs5);
	List<OrientableCurve> orientableCurves5 = new ArrayList<OrientableCurve>();
	orientableCurves5.add(curve5);
	OrientableCurve curve6 = builder.createCurve(segs6);
	List<OrientableCurve> orientableCurves6 = new ArrayList<OrientableCurve>();
	orientableCurves6.add(curve6);

	// create the interior ring and a list of empty interior rings (holes)
	
	PrimitiveFactoryImpl pmFF = (PrimitiveFactoryImpl) builder.getPrimitiveFactory();
	
	Ring extRing1 = pmFF.createRing(orientableCurves1);
	Ring extRing2 = pmFF.createRing(orientableCurves2);
	Ring extRing3 = pmFF.createRing(orientableCurves3);
	Ring extRing4 = pmFF.createRing(orientableCurves4);
	Ring extRing5 = pmFF.createRing(orientableCurves5);
	Ring extRing6 = pmFF.createRing(orientableCurves6);

	// create surfaceboundary by rings
	SurfaceBoundary sb1 = pmFF.createSurfaceBoundary(extRing1, new ArrayList<Ring>());
	SurfaceBoundary sb2 = pmFF.createSurfaceBoundary(extRing2, new ArrayList<Ring>());
	SurfaceBoundary sb3 = pmFF.createSurfaceBoundary(extRing3, new ArrayList<Ring>());
	SurfaceBoundary sb4 = pmFF.createSurfaceBoundary(extRing4, new ArrayList<Ring>());
	SurfaceBoundary sb5 = pmFF.createSurfaceBoundary(extRing5, new ArrayList<Ring>());
	SurfaceBoundary sb6 = pmFF.createSurfaceBoundary(extRing6, new ArrayList<Ring>());

	// create the surface
	Surface surface1 = pmFF.createSurface(sb1);
	Surface surface2 = pmFF.createSurface(sb2);
	Surface surface3 = pmFF.createSurface(sb3);
	Surface surface4 = pmFF.createSurface(sb4);
	Surface surface5 = pmFF.createSurface(sb5);
	Surface surface6 = pmFF.createSurface(sb6);

	List<OrientableSurface> surfaces = new ArrayList<OrientableSurface>();
	surfaces.add(surface1);
	surfaces.add(surface2);
	surfaces.add(surface3);
	surfaces.add(surface4);
	surfaces.add(surface5);
	surfaces.add(surface6);

	Shell exteriorShell = pmFF.createShell(surfaces);
	List<Shell> interiors = new ArrayList<Shell>();

	SolidBoundary solidBoundary = pmFF.createSolidBoundary(exteriorShell, interiors);
	Solid solid = pmFF.createSolid(solidBoundary);

	return solid;
}
}
