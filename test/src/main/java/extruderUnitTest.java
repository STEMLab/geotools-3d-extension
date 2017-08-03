import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.iso.util.SolidUtil;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.ISOGeometryBuilder;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.Surface;

public class extruderUnitTest {

	@Test
	public void createSolidWithHeightForTest(){
		Solid s;
		double h = 10;
		
		List<DirectPosition>points = new ArrayList<DirectPosition>();
		ISOGeometryBuilder gb3D = new ISOGeometryBuilder(DefaultGeographicCRS.WGS84_3D);
		//DirectPosition p1 = gb.createDirectPosition(new double[]{0,0});
		//DirectPosition p2 = gb.createDirectPosition(new double[]{10,0});
		//DirectPosition p3 = gb.createDirectPosition(new double[]{10,10});
		//DirectPosition p4 = gb.createDirectPosition(new double[]{0,10});
		
		DirectPosition p1 = gb3D.createDirectPosition(new double[]{5,0,0});
		DirectPosition p2 = gb3D.createDirectPosition(new double[]{2,5,0});
		DirectPosition p3 = gb3D.createDirectPosition(new double[]{7,10,0});
		DirectPosition p4 = gb3D.createDirectPosition(new double[]{12,5,0});
		DirectPosition p5 = gb3D.createDirectPosition(new double[]{10,0,0});
		
		
		points.add(p5);
		points.add(p4);
		points.add(p3);
		points.add(p2);
		points.add(p1);
		points.add(p5);
	
		Surface sf = SolidUtil.createSurface(gb3D, points);
		List<DirectPosition>pointFromSurface = SolidUtil.makeDirectPositionsFromSurface(gb3D, sf);
		Solid s1 = SolidUtil.createSolid(gb3D, SolidUtil.makeSurfaces(gb3D, pointFromSurface, h));
		//List<Surface>sfList = SolidUtil.makeSurfaces(gb3D,points,h);
		//s = SolidUtil.createSolid(gb3D,sfList);
					
		//return s;

	}

}
