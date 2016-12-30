/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2003-2005, Open Geospatial Consortium Inc.
 *    
 *    All Rights Reserved. http://www.opengis.org/legal/
 */
package org.opengis.geometry.primitive;

import java.util.List;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.annotation.UML;
import org.opengis.annotation.Extension;

import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;


/**
 * A extension factory interface of {@linkplain PrimitiveFactory} creating {@linkplain SolidBoundary} geometric objects.
 *
 * @source $URL$
 * @version <A HREF="http://www.opengeospatial.org/standards/as">ISO 19107</A>
 * @author Soojin Kim, Pusan National University
 */
public interface PrimitiveFactory2 extends PrimitiveFactory{
  
	@Extension
    Shell createShell(List<OrientableSurface> orientableSurfaces) 
    		throws MismatchedReferenceSystemException, MismatchedDimensionException;
	
    @Extension
    SolidBoundary createSolidBoundary(Shell exterior, List<Shell> interiors)
            throws MismatchedReferenceSystemException, MismatchedDimensionException;

   
}
