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
import com.jzb.ttpoi.data.TPOIPolylineData;
import com.jzb.ttpoi.data.TPOIPolylineData.Coordinate;

/**
 * @author n63636
 * 
 */
public class KMLFileLoader {

    public static TPOIFileData loadFile(File kmlFile) throws Exception {

        TPOIFileData fileData = new TPOIFileData();
        fileData.setFileName(kmlFile.getAbsolutePath());
        fileData.setWasKMLFile(true);

        HashMap<String, String> styleCatMap = new HashMap<String, String>(ConversionUtil.getDefaultParseCategories());
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

            // Si no es un punto lo salta
            val = xpath.evaluate("Point/coordinates/text()", node);
            if (val == null || val.length() == 0)
                continue;

            
            TPOIData poi = new TPOIData();

            // ExtraInfo del punto
            val = xpath.evaluate("comment()", node);
            val = ConversionUtil.getOV2Text(val);
            int p1 = val.indexOf("ExtraInfo:[[");
            int p2 = p1 < 0 ? -1 : val.indexOf("]]", p1);
            if (p1 >= 0 && p2 >= 0) {
                val = val.substring(p1 + 12, p2);
                poi.setExtraInfo(val);
            }

            // Nombre del punto
            val = xpath.evaluate("name/text()", node);
            val = ConversionUtil.getOV2Text(val);
            poi.setName(val);

            // Descripcion del punto
            val = xpath.evaluate("description/text()", node);
            val = ConversionUtil.getOV2Text(val);
            poi.setDesc(val);

            // Coordenadas del punto.
            val = xpath.evaluate("Point/coordinates/text()", node);
            parseCoordinates(val, poi);

            // Estilo del punto para la categoria
            // Lee la categoria a partir de la URL del estilo
            val = xpath.evaluate("styleUrl/text()", node);
            if (val != null) {
                if (val.charAt(0) == '#')
                    val = val.substring(1);
                val = xpath.evaluate("/kml/Document/Style[@id=\"" + val + "\"]/IconStyle/Icon/href/text()", node);
                if (val.trim().length() == 0) {
                    val = "http://maps.gstatic.com/mapfiles/ms2/micons/blue-dot.png";
                }
                poi.setIconStyle(val);
                val = ConversionUtil.getCategoryFromStyle(val);
                poi.setCategory(val);
            }

            if (poi.Is_TT_Cat_POI()) {
                _parseCategories(styleCatMap, poi.getDesc());
                // **** LO VOLVEMOS A AÑADIR ****
                fileData.addPOI(poi);
            } else {
                fileData.addPOI(poi);
            }
        }

        // Lee todos los polylines
        for (int n = 0; n < nlist.getLength(); n++) {

            Node node = nlist.item(n);

            // Si no es una polyline la salta
            val = xpath.evaluate("LineString/coordinates/text()", node);
            if (val == null || val.length() == 0)
                continue;

            TPOIPolylineData polyline = new TPOIPolylineData();

            // Nombre del polyline
            val = xpath.evaluate("name/text()", node);
            val = ConversionUtil.getOV2Text(val);
            polyline.setName(val);

            // Descripcion del polyline
            val = xpath.evaluate("description/text()", node);
            val = ConversionUtil.getOV2Text(val);
            polyline.setDesc(val);

            // Coordenadas del polyline.
            val = xpath.evaluate("LineString/coordinates/text()", node);
            StringTokenizer tokenizer = new StringTokenizer(val, "\n");
            while(tokenizer.hasMoreTokens()) {
                String coordStr = tokenizer.nextToken();
                parseCoordinates(coordStr, polyline);
            }
            
            fileData.getAllPolylines().add(polyline);
        }

        fileData.translateCategories(styleCatMap);

        return fileData;
    }

    private static String _cleanHTML(String txt) {
        txt = txt.replaceAll("\\<[^<>]*\\>", "");
        txt = txt.replaceAll("&lt;", "<");
        txt = txt.replaceAll("&gt;", ">");
        txt = txt.replaceAll("&nbsp;", " ");
        txt = txt.replaceAll("&amp;", "&");

        return txt;
    }

    private static void _parseCategories(HashMap<String, String> styleCatMap, String cats) {

        cats = _cleanHTML(cats);
        if (cats.indexOf("<TTInfo>") > 0)
            return;

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

    private static void parseCoordinates(String val, TPOIPolylineData polyline) throws Exception {
        
        val = val.trim();
        Coordinate coordinate = new Coordinate();
        
        String nstr;
        StringTokenizer st = new StringTokenizer(val, ",");
        if(!st.hasMoreTokens()) return;
        
        nstr = st.nextToken();
        coordinate.lng = Double.parseDouble(nstr);
        nstr = st.nextToken();
        coordinate.lat =Double.parseDouble(nstr);
        
        polyline.getCoordinates().add(coordinate);
    }
}
