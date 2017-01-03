import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.gml3.iso.GML;
import org.geotools.xml.Encoder;

/*
 * Indoor Moving Objects Generator
 * Copyright (c) 2017 Pusan National University
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */

/**
 * @author hgryoo
 *
 */
public class FeatureCollectionEncode {

    static final String INDENT_AMOUNT_KEY =
            "{http://xml.apache.org/xslt}indent-amount";

    public static ByteArrayOutputStream encode(SimpleFeatureCollection features, Encoder encoder) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // create the document serializer
        SAXTransformerFactory txFactory = (SAXTransformerFactory) SAXTransformerFactory
                .newInstance();
        Transformer transformer = txFactory.newTransformer();
        
        TransformerHandler xmls;
        try {
            xmls = txFactory.newTransformerHandler();
        } catch (TransformerConfigurationException e) {
            throw new IOException(e);
        }
        Properties outputProps = new Properties();
        outputProps.setProperty(INDENT_AMOUNT_KEY, "2");
        xmls.getTransformer().setOutputProperties(outputProps);
        xmls.getTransformer().setOutputProperty(OutputKeys.METHOD, "xml");
        xmls.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
        xmls.setResult(new StreamResult(out));
        encoder.setIndenting(true);
        encoder.encode( features, GML.FeatureCollection, out);
        return out;
    }
    
    public static void saveAsFile(File file, ByteArrayOutputStream stream) throws IOException {
    	FileOutputStream fos = null;
    	try {
    		fos = new FileOutputStream (file); 

    		stream.writeTo(fos);
    	} catch(IOException ioe) {
    	    // Handle exception here
    	    ioe.printStackTrace();
    	} finally {
    		if(fos != null) {
    			fos.close();
    		}
    	}
    }
    
    
    
}
