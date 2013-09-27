/**
 * 
 */
package com.jzb.ttpoi.wl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;

/**
 * @author n63636
 * 
 */
public class KMLFileLoader {

    public static TPOIFileData loadFile(File kmlFile) throws Exception {

        TPOIFileData fileData = new TPOIFileData();
        fileData.setFileName(kmlFile.getAbsolutePath());
        fileData.setWasKMLFile(true);
        
        HashMap<String, String> styleCatMap = ConversionUtil.getDefaultParseCategories();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(new InputSource(new InputStreamReader(new FileInputStream(kmlFile), "UTF8")));

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        String val;

        // Lee el valor de fichero
        val = xpath.evaluate("/kml/Document/name/text()", doc);
        fileData.setName(val);

        // Lee todos los puntos
        NodeList nlist = (NodeList) xpath.evaluate("/kml/Document/Placemark", doc, XPathConstants.NODESET);
        for (int n = 0; n < nlist.getLength(); n++) {

            Node node = nlist.item(n);

            TPOIData poi = new TPOIData();

            // Nombre del punto
            val = xpath.evaluate("name/text()", node);
            val = ConversionUtil.getOV2Text(val);
            poi.setName(val);

            // Descripcion del punto
            val = xpath.evaluate("description/text()", node);
            val = ConversionUtil.getOV2Text(val);
            poi.setDesc(val);

            // Coordenadas del punto. Puede no existir si es una Linea
            // TODO: ¿Qué hacemos con las líneas? *****************************
            val = xpath.evaluate("Point/coordinates/text()", node);
            if (val == null || val.length() == 0)
                continue;

            parseCoordinates(val, poi);

            // Estilo del punto para la categoria
            // Lee la categoria a partir de la URL del estilo
            val = xpath.evaluate("styleUrl/text()", node);
            if (val != null) {
                if (val.charAt(0) == '#')
                    val = val.substring(1);
                val = xpath.evaluate("/kml/Document/Style[@id=\"" + val + "\"]/IconStyle/Icon/href/text()", node);
                if(val.trim().length()==0) {
                    val = "http://maps.gstatic.com/mapfiles/ms2/micons/blue-dot.png";
                }
                poi.setIconStyle(val);
                val = ConversionUtil.getCategoryFromStyle(val);
                poi.setCategory(val);
            }

            if (poi.Is_TT_Cat_POI()) {
                _parseCategories(styleCatMap, poi.getDesc());
                //**** LO VOLVEMOS A AÑADIR ****
                fileData.addPOI(poi);
            } else {
                fileData.addPOI(poi);
            }
        }

        fileData.translateCategories(styleCatMap);

        return fileData;
    }

    private static String _cleanHTML(String txt) {
        txt = txt.replaceAll("\\<[^<>]*\\>", "");
        txt = txt.replaceAll("&lt;","<");
        txt = txt.replaceAll("&gt;",">");
        txt = txt.replaceAll("&nbsp;"," ");
        txt = txt.replaceAll("&amp;","&");
        
        return txt;
    }

    private static void _parseCategories(HashMap<String, String> styleCatMap, String cats) {

        cats = _cleanHTML(cats);
        if(cats.indexOf("<TTInfo>")>0) return;
        
        StringTokenizer st1 = new StringTokenizer(cats, "#");
        while (st1.hasMoreTokens()) {
            String str = st1.nextToken();
            StringTokenizer st2 = new StringTokenizer(str, "=");
            String name = st2.nextToken().trim();
            String val = st2.nextToken().trim();
            styleCatMap.put(name, val);
        }

    }

    private static void parseCoordinates(String val, TPOIData poi) throws Exception {
        String nstr;
        StringTokenizer st = new StringTokenizer(val, ",");
        nstr = st.nextToken();
        poi.setLng(Double.parseDouble(nstr));
        nstr = st.nextToken();
        poi.setLat(Double.parseDouble(nstr));
    }

}
