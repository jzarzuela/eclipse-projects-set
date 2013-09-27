/**
 * 
 */
package com.jzb.ttpoi.wl;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;

/**
 * @author n63636
 * 
 */
public class KMLFileWriter {

    public static void saveFile(File kmlFile, TPOIFileData fileData) throws Exception {

        HashMap<String, Integer> styleIndexes = _calcStyleIndexes(fileData);

        PrintWriter pw = new PrintWriter(kmlFile, "UTF-8");

        _printHeader(pw, fileData, styleIndexes);
        for (TPOIData poi : fileData.getAllPOIs()) {
            _printRecord(pw, poi, styleIndexes);
        }
        _printFooter(pw, fileData);

        pw.close();

    }

    private static HashMap<String, Integer> _calcStyleIndexes(TPOIFileData fileData) {

        HashMap<String, Integer> indexes = new HashMap<String, Integer>();

        int index = 1;
        for (String cat : fileData.getCategories()) {
            indexes.put(cat, index);
            index++;
            if (index > s_gStyles.length)
                index = 0;
        }

        index = 200;
        for (TPOIData poi : fileData.getAllPOIs()) {
            if (poi.getIconStyle() != null) {
                indexes.put(poi.getIconStyle(), index++);
            }
        }

        return indexes;
    }

    private static String _escape(String s) {
        s = s.replace("&", "·amp;");
        s = s.replace("\"", "·quot;");
        s = s.replace("<", "·lt;");
        s = s.replace(">", "·gt;");

        s = s.replace('·', '&');

        return s;
    }

    private static void _printRecord(PrintWriter pw, TPOIData poi, HashMap<String, Integer> styleIndexes) throws Exception {

        Integer sIndex = null;

        if (poi.getIconStyle() != null)
            sIndex = styleIndexes.get(poi.getIconStyle());

        if (sIndex == null)
            sIndex = styleIndexes.get(poi.getCategory());

        if (sIndex == null)
            sIndex = 1;

        pw.println("  <Placemark>");
        pw.println("    <name>" + _escape(poi.getName()) + "</name>");
        pw.println("    <description><![CDATA[" + _escape(poi.getDesc()) + "]]></description>");
        pw.println("    <styleUrl>#style" + sIndex.intValue() + "</styleUrl>");
        pw.println("    <Point>");
        pw.println("      <coordinates>" + poi.getLng() + "," + poi.getLat() + ",0.000000</coordinates>");
        pw.println("    </Point>");
        pw.println("  </Placemark>");
    }

    private static void _printFooter(PrintWriter pw, TPOIFileData fileData) throws Exception {
        pw.println("</Document>");
        pw.println("</kml>");
    }

    private static final String s_gStyles[] = { "http://maps.gstatic.com/mapfiles/ms2/micons/blue-dot.png", "http://maps.gstatic.com/mapfiles/ms2/micons/green-dot.png",
            "http://maps.gstatic.com/mapfiles/ms2/micons/ltblue-dot.png", "http://maps.gstatic.com/mapfiles/ms2/micons/pink-dot.png",
            "http://maps.gstatic.com/mapfiles/ms2/micons/purple-dot.png", "http://maps.gstatic.com/mapfiles/ms2/micons/red-dot.png",
            "http://maps.gstatic.com/mapfiles/ms2/micons/yellow-dot.png", "http://maps.gstatic.com/mapfiles/ms2/micons/blue-pushpin.png",
            "http://maps.gstatic.com/mapfiles/ms2/micons/grn-pushpin.png", "http://maps.gstatic.com/mapfiles/ms2/micons/ltblu-pushpin.png",
            "http://maps.gstatic.com/mapfiles/ms2/micons/pink-pushpin.png", "http://maps.gstatic.com/mapfiles/ms2/micons/purple-pushpin.png",
            "http://maps.gstatic.com/mapfiles/ms2/micons/red-pushpin.png", "http://maps.gstatic.com/mapfiles/ms2/micons/ylw-pushpin.png",
            "http://maps.gstatic.com/mapfiles/ms2/micons/blue.png", "http://maps.gstatic.com/mapfiles/ms2/micons/green.png",
            "http://maps.gstatic.com/mapfiles/ms2/micons/lightblue.png", "http://maps.gstatic.com/mapfiles/ms2/micons/pink.png",
            "http://maps.gstatic.com/mapfiles/ms2/micons/purple.png", "http://maps.gstatic.com/mapfiles/ms2/micons/red.png",
            "http://maps.gstatic.com/mapfiles/ms2/micons/yellow.png" };

    private static void _printHeader(PrintWriter pw, TPOIFileData fileData, HashMap<String, Integer> styleIndexes) throws Exception {

        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        pw.println("<kml xmlns=\"http://earth.google.com/kml/2.2\">");
        pw.println("<Document>");
        pw.println("  <name>" + fileData.getName() + "</name>");
        pw.println("  <description><![CDATA[]]></description>");

        for (int n = 0; n < s_gStyles.length; n++) {
            pw.println("  <Style id=\"style" + (n + 1) + "\">");
            pw.println("    <IconStyle>");
            pw.println("      <Icon>");
            pw.println("        <href>" + s_gStyles[n] + "</href>");
            pw.println("      </Icon>");
            pw.println("    </IconStyle>");
            pw.println("  </Style>");
        }

        for (Map.Entry<String, Integer> entry : styleIndexes.entrySet()) {
            if (entry.getValue() >= 200) {
                pw.println("  <Style id=\"style" + entry.getValue() + "\">");
                pw.println("    <IconStyle>");
                pw.println("      <Icon>");
                pw.println("        <href>" + entry.getKey() + "</href>");
                pw.println("      </Icon>");
                pw.println("    </IconStyle>");
                pw.println("  </Style>");
            }
        }

    }
}
