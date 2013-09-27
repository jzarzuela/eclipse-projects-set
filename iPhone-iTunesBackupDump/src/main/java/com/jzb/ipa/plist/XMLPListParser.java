/**
 * 
 */
package com.jzb.ipa.plist;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author n000013
 * 
 */
public class XMLPListParser implements IPListParser {

    public T_PLDict parsePList(byte  buffer[]) throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://xml.org/sax/features/validation", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(new ByteArrayInputStream(buffer));

        NodeList nl = doc.getDocumentElement().getChildNodes();
        for (int n = 0; n < nl.getLength(); n++) {
            Node node = nl.item(n);
            if (node.getNodeName().equals("dict")) {
                return _parseDict(node);
            }
        }

        throw new Exception("No DICTIONARY element found!");

    }


    private String _getNodeValue(Node node) {
        if (node.getFirstChild() != null) {
            return node.getFirstChild().getNodeValue();
        } else {
            return "";
        }
    }


    private T_PLArray _parseArray(Node root) throws Exception {
        ArrayList objectTable = new ArrayList();
        T_PLArray array = new T_PLArray();
        array.objectTable = objectTable;

        ArrayList<Integer> objrefs = new ArrayList<Integer>();
        
        NodeList nl = root.getChildNodes();
        for (int n = 0; n < nl.getLength(); n++) {
            Node node = nl.item(n);

            String nodeName = node.getNodeName();
            String nodeValue = _getNodeValue(node);

            if (nodeName.equals("#text")) {
                continue;
            } else if (nodeName.equals("string")) {
                objrefs.add(objectTable.size());
                objectTable.add(nodeValue);
            } else if (nodeName.equals("integer")) {
                objrefs.add(objectTable.size());
                objectTable.add(Long.parseLong(nodeValue));
            } else if (nodeName.equals("true")) {
                objrefs.add(objectTable.size());
                objectTable.add(Boolean.TRUE);
            } else if (nodeName.equals("false")) {
                objrefs.add(objectTable.size());
                objectTable.add(Boolean.FALSE);
            } else if (nodeName.equals("array")) {
                objrefs.add(objectTable.size());
                objectTable.add("false");
            } else if (nodeName.equals("dict")) {
                objrefs.add(objectTable.size());
                objectTable.add(_parseDict(node));
            } else {
                throw new Exception("Unknown plist node type '" + nodeName + "'");
            }

        }


        array.objref = new int[objrefs.size()];
        for (int n = 0; n < array.objref.length; n++)
            array.objref[n] = objrefs.get(n);

        return array;
    }
    
    private T_PLDict _parseDict(Node root) throws Exception {

        ArrayList objectTable = new ArrayList();
        T_PLDict dict = new T_PLDict();
        dict.objectTable = objectTable;
        objectTable.add(dict);

        ArrayList<Integer> keyrefs = new ArrayList<Integer>();
        ArrayList<Integer> objrefs = new ArrayList<Integer>();

        NodeList nl = root.getChildNodes();
        for (int n = 0; n < nl.getLength(); n++) {
            Node node = nl.item(n);

            String nodeName = node.getNodeName();
            String nodeValue = _getNodeValue(node);

            if (nodeName.equals("#text")) {
                continue;
            } else if (nodeName.equals("key")) {
                keyrefs.add(objectTable.size());
                objectTable.add(nodeValue);
            } else if (nodeName.equals("string")) {
                objrefs.add(objectTable.size());
                objectTable.add(nodeValue);
            } else if (nodeName.equals("integer")) {
                objrefs.add(objectTable.size());
                objectTable.add(nodeValue);
            } else if (nodeName.equals("true")) {
                objrefs.add(objectTable.size());
                objectTable.add("true");
            } else if (nodeName.equals("false")) {
                objrefs.add(objectTable.size());
                objectTable.add("false");
            } else if (nodeName.equals("array")) {
                objrefs.add(objectTable.size());
                objectTable.add(_parseArray(node));
            } else if (nodeName.equals("dict")) {
                objrefs.add(objectTable.size());
                objectTable.add(_parseDict(node));
            } else if (nodeName.equals("date")) {
                objrefs.add(objectTable.size());
                objectTable.add(nodeValue);
            } else {
                throw new Exception("Unknown plist node type '" + nodeName + "'");
            }

        }

        dict.keyref = new int[keyrefs.size()];
        for (int n = 0; n < dict.keyref.length; n++)
            dict.keyref[n] = keyrefs.get(n);

        dict.objref = new int[objrefs.size()];
        for (int n = 0; n < dict.objref.length; n++)
            dict.objref[n] = objrefs.get(n);

        return dict;
    }
}
