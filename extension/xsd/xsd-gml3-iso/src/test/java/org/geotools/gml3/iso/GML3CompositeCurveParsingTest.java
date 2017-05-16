package org.geotools.gml3.iso;

import org.geotools.xml.Parser;
import org.opengis.geometry.Geometry;

public class GML3CompositeCurveParsingTest extends GML3TestSupport {

    @Override
    protected boolean enableExtendedArcSurfaceSupport() {
        return true;
    }

    public void testCompositeCurve() throws Exception {
        GMLConfiguration_ISO gml = new GMLConfiguration_ISO(true);
        Parser p = new Parser(gml);
        Object compositeCurve = p.parse(getClass().getResourceAsStream("v3_2/gml_compositecurve_1.xml"));
        assertFalse(compositeCurve instanceof String);
        // System.out.println(compositeCurve);
        assertTrue("wrong element type", compositeCurve instanceof Geometry);
        Geometry geom = (Geometry) compositeCurve;
        assertEquals("LINESTRING (353148.991 5530600.811, 353151.478 5530602.263)", geom.toText());
        //assertNotNull(l);
    }
}
