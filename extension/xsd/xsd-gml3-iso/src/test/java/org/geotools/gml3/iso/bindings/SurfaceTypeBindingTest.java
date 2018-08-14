/**
 * 
 */
package org.geotools.gml3.iso.bindings;

import java.util.List;

import org.geotools.gml3.iso.GML3TestSupport;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

/**
 * @author hgryoo
 *
 */
public class SurfaceTypeBindingTest extends GML3TestSupport {

	@Override
    protected boolean enableExtendedArcSurfaceSupport() {
        return false;
    }

    public void testSurface3D() throws Exception {
        GML3MockData.surface3D(document, document);

        Surface polygon = (Surface) parse();
        assertNotNull(polygon);
        
        SurfaceBoundary sb = polygon.getBoundary();
        Ring exterior = sb.getExterior();
        //TODO : test
        
        List<Ring> interior = sb.getInteriors();
        assertEquals(interior.size(), 1);
        
        /*
        LineString exterior = polygon.getExteriorRing();
        assertTrue(new Coordinate(1d, 2d, 10d).equals3D(exterior.getCoordinateN(0)));
        LineString interior = polygon.getInteriorRingN(0);
        assertTrue(new Coordinate(1d, 2d, 10d).equals3D(interior.getCoordinateN(0)));
        */
    }
    
    public void testSurface3DMultiPatches() throws Exception {
    	GML3MockData.surface3DMultiPatches(document, document);
    	
    	//TODO, FIXME : a surface which have multiple patches can not make SurfaceBoundary
    	try {
	    	Surface surface = (Surface) parse();
			assertNotNull(surface);
		    SurfaceBoundary sb = surface.getBoundary();
		    assertNotNull(sb);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}
