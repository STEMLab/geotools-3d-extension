import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.gml2.simple.GMLWriter;
import org.geotools.gml3.iso.GML;
import org.geotools.gml3.iso.GMLConfiguration_ISO;
import org.geotools.gml3.iso.simple.GML3FeatureCollectionEncoderDelegate;
import org.geotools.xml.Encoder;
import org.w3c.dom.Document;
import org.xml.sax.helpers.AttributesImpl;

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
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Encoder encoder = new Encoder(new GMLConfiguration_ISO());
    }

    public static Document encode(SimpleFeatureCollection features, Encoder encoder) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // create the document serializer
        SAXTransformerFactory txFactory = (SAXTransformerFactory) SAXTransformerFactory
                .newInstance();

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
        xmls.setResult(new StreamResult(out));

        /*GMLWriter handler = new GMLWriter(xmls, encoder.getNamespaces(), 6, false, "gml");
        handler.startDocument();
        handler.startPrefixMapping("gml", GML.NAMESPACE);
        handler.endPrefixMapping("gml");
*/
        GML3FeatureCollectionEncoderDelegate en = new GML3FeatureCollectionEncoderDelegate(features, encoder);
        en.encode(xmls);
        //handler.endDocument();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        DOMResult result = new DOMResult();
        Transformer tx = TransformerFactory.newInstance().newTransformer();
        tx.transform(new StreamSource(in), result);
        Document d = (Document) result.getNode();
        return d;
    }
    
}
