/**
 * 
 */
package org.geotools.iso.tutorial;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.jaxb.CityGMLBuilder;
import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;
import org.citygml4j.model.gml.geometry.primitives.DirectPosition;
import org.citygml4j.model.gml.geometry.primitives.LinearRing;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;
import org.citygml4j.model.module.Modules;
import org.citygml4j.model.module.citygml.CityGMLModuleType;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLInputFilter;
import org.citygml4j.xml.io.reader.CityGMLReader;
import org.citygml4j.xml.io.reader.FeatureReadMode;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @author hgryoo
 *
 */
public class CityGML4JConvertorTest {
	
	Map<String, Building> buildings = new HashMap<String, Building>(); 
	Map<String, AbstractBoundarySurface> boundaries = new HashMap<String, AbstractBoundarySurface>();
	Map<String, List<String>> placeHolder = new HashMap<String, List<String>>();
	
	Map<String, MultiPolygon> buildingMap = new HashMap<String, MultiPolygon>();
	Building building;
	
	public void importCityGML() throws Exception {
		
		CityGMLContext ctx = CityGMLContext.getInstance();
		CityGMLBuilder builder = ctx.createCityGMLBuilder();
		CityGMLInputFactory in = builder.createCityGMLInputFactory();
		in.setProperty(CityGMLInputFactory.FEATURE_READ_MODE, FeatureReadMode.SPLIT_PER_FEATURE);

		URL url = CityGML4JConvertorTest.class.getResource("DA1_3D_Buildings_Merged.gml");
		CityGMLReader reader = in.createCityGMLReader(new File(url.getPath()));
		reader = in.createFilteredCityGMLReader(reader, new CityGMLInputFilter() {
			// return true if you want to consume the CityGML feature
						// of the given qualified XML name, false otherwise
			public boolean accept(QName name) {
				return Modules.isModuleNamespace(name.getNamespaceURI(), CityGMLModuleType.BUILDING)
						&& (name.getLocalPart().equals("Building") || name.getLocalPart().contains("Surface"));
			}
		});
		
		int num = 0;
		while (reader.hasNext()) {
			Object obj = reader.nextFeature();
			
			if(obj instanceof Building) {
				Building b = (Building) obj;
				String id = b.getId();
				buildings.put(id, b);
			} else {
				AbstractBoundarySurface abs = (AbstractBoundarySurface) obj;
				String id = abs.getId();
				boundaries.put(id, abs);
			}
		}
		
		for(Building b : buildings.values()) {
			List<BoundarySurfaceProperty> list = b.getBoundedBySurface();
			List<AbstractBoundarySurface> surfaces = new ArrayList<AbstractBoundarySurface>();
			
			for(BoundarySurfaceProperty bsp : list) {
				String href = bsp.getHref();
				AbstractBoundarySurface surface = boundaries.get(href.replace("#", ""));
				surfaces.add(surface);
			}
			
			MultiPolygon mp = CityGML2JTS.toMultiPolygon(surfaces);
			buildingMap.put(b.getId(), mp);
		}
		
		System.out.println("number of buildings : " + num);
		System.out.println(buildingMap);
		reader.close();
	}
	
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		CityGML4JConvertorTest test = new CityGML4JConvertorTest();
		test.importCityGML();
	}

}
