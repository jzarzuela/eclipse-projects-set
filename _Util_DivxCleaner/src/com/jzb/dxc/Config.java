/**
 * 
 */
package com.jzb.dxc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author jzarzuela
 * 
 */
public class Config {

    private static File            s_dstFolder;

    private static HashSet<String> s_exts       = new HashSet();

    private static ArrayList<File> s_srcFolders = new ArrayList();

    /**
     * @return the dstFolder
     */
    public static File getDstFolder() {
        return s_dstFolder;
    }

    /**
     * @return the exts
     */
    public static HashSet<String> getExts() {
        return s_exts;
    }

    /**
     * @return the srcFolders
     */
    public static ArrayList<File> getSrcFolders() {
        return s_srcFolders;
    }

    public static void loadConfig(File configFile) throws Exception {

        if (configFile == null || !configFile.exists()) {
            throw new Exception("Passed configuration XML file doesn't exist: '" + configFile + "'");
        }

        InputStream is = new FileInputStream(configFile);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(new InputSource(is));

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        String val = xpath.evaluate("/config/process/exts/text()", doc);
        StringTokenizer st = new StringTokenizer(val, ";");
        while (st.hasMoreElements()) {
            s_exts.add("." + st.nextToken().trim().toLowerCase());
        }

        val = xpath.evaluate("/config/process/dstPath/text()", doc);
        s_dstFolder = new File(val);
        if (!s_dstFolder.exists()) {
            throw new Exception("Error, destination folder doesn't exist: " + val);
        }

        Tracer.init(new File(Config.getDstFolder(), "_traces"));

        NodeList nlist = (NodeList) xpath.evaluate("/config/process/srcPath/text()", doc, XPathConstants.NODESET);
        for (int n = 0; n < nlist.getLength(); n++) {
            Node node = nlist.item(n);
            val = node.getNodeValue();
            File of = new File(val);
            if (!of.exists())
                System.out.println("Warning, origin folder doesn't exist: " + val);
            else {
                s_srcFolders.add(of);
            }

        }

        nlist = (NodeList) xpath.evaluate("/config/filterRules/filter", doc, XPathConstants.NODESET);
        for (int n = 0; n < nlist.getLength(); n++) {
            Node node = nlist.item(n);
            NamedNodeMap attrs = node.getAttributes();
            GenericFilterRule.MODE mode = GenericFilterRule.getMode(attrs.getNamedItem("mode").getNodeValue());
            String regexp = attrs.getNamedItem("regexp").getNodeValue();
            String data = attrs.getNamedItem("data").getNodeValue();
            GenericFilterRule filter = new GenericFilterRule(mode, regexp, data);
            Filters.addFilter(filter);
        }
        

    }
}
