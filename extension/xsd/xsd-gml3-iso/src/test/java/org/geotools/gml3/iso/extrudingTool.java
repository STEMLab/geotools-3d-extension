package org.geotools.gml3.iso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.geotools.gml3.iso.GML;
import org.geotools.gml3.iso.GML3TestSupport;

public class extrudingTool {
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;
    public static String TEST_XML_STRING =
        "<?xml version=\"1.0\" ?><test attrib=\"moretest\">Turn this to JSON</test>";

    public static void main(String[] args) throws IOException {
    	//System.out.println(System.getProperty("user.dir"));
    	InputStream in = extrudingTool.class.getResourceAsStream("/bostonMap.osm");
    	 	
        try {
        	
        	StringBuilder builder = new StringBuilder();
        	int ptr = 0;  
            while ((ptr = in.read()) != -1 ) {  
                builder.append((char) ptr); 
              //  System.out.println(ptr);
            }  
            String xml  = builder.toString();  
            JSONObject jsonObj = XML.toJSONObject(xml);   

            //JSONObject xmlJSONObj = XML.toJSONObject(TEST_XML_STRING);
            JSONObject osm = jsonObj.getJSONObject("osm");
            JSONArray way = osm.getJSONArray("way");
            JSONArray node = osm.getJSONArray("node");
            String jsonPrettyPrintString = jsonObj.toString(PRETTY_PRINT_INDENT_FACTOR);
            for(int i = 0 ; i < way.length(); i++){
            	JSONObject temp = way.getJSONObject(i);
            	boolean isBuilding = false;
            	if(temp.has("tag")){
                 	JSONArray tag = temp.getJSONArray("tag");
                 	
                 	for(int j = 0 ; j < tag.length(); j++){
                 		
                 		if(tag.getJSONObject(i).has("v")){                 			
                 			String v = (String)tag.getJSONObject(i).get("v");
                 			
                 			if(v == "building"){
                 				isBuilding = true;
                 				break;
                 			 
                 			}
                 		}
                 	}
            	}
            	if(isBuilding == true){
            		//JSONObject
            		JSONArray nd = temp.getJSONArray("nd");
            		//ArrayList<Integer>nd_rf_id = new ArrayList<Integer>();
            		ArrayList<Float>point_list = new ArrayList<Float>();
            		float h = randomHeight();
            		ArrayList<Float>lat = new ArrayList();
            		ArrayList<Float>lon = new ArrayList();
            		for(int k = 0 ; k < nd.length(); k++){
            			JSONObject t = (JSONObject) nd.get(k);            			
            			int ref_id = (Integer)t.get("ref");            			
            			for(int j = 0 ; j < node.length(); j++){
            				JSONObject single_node = (JSONObject) node.get(j);
            				
            				if((Integer)single_node.get("id") == ref_id){
            					lat.add((float)single_node.get("lat"));
            					lon.add((float)single_node.get("lon"));
            					break;
 
            				}
            			}
            		}
            		
            		
            		
            	}
   
            	
            }
            System.out.println(jsonPrettyPrintString);
        } catch (JSONException je) {
            System.out.println(je.toString());
        }
    }
    public static float randomHeight(){
    	Random generator = new Random(); 
    	
    	int num1;
    	float num2;
    	
    	num2 = generator.nextFloat()*100;
    	num2 += 5;
    	
    	
    	
    	return num2;
    }
}

