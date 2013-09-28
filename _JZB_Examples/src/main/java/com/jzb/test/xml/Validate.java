/**
 * 
 */
package com.jzb.test.xml;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;

import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("restriction")
public class Validate {

    public void parseDoc(String fileName) throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setXIncludeAware(true);
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(fileName));

        StringWriter sw = new StringWriter();
        XMLSerializer xs = new XMLSerializer(sw, null);
        xs.serialize(doc);
        System.out.println(sw.getBuffer());

        Schema sch = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema").newSchema(new File("D:/WKSPs/WS_XSLT/XMLExamples/MIO/MySchema.xsd"));
        StreamSource ss = new StreamSource(new StringReader(sw.getBuffer().toString()));
        sch.newValidator().validate(ss);
    }
}
