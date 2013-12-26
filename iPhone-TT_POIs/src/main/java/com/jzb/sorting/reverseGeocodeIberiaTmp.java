/**
 * 
 */
package com.jzb.sorting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.util.KMLDownload;
import com.jzb.ttpoi.wl.KMLFileLoader;
import com.jzb.ttpoi.wl.KMLFileWriter;

/**
 * @author jzarzuela
 * 
 */
public class reverseGeocodeIberiaTmp {

    private static final char   ORIG_CHARS[] = { 'á', 'é', 'í', 'ó', 'ú', 'Á', 'É', 'Í', 'Ó', 'Ú', 'ä', 'ë', 'ï', 'ö', 'ü', 'Ä', 'Ë', 'Ï', 'Ö', 'Ü' };
    private static final char   SUBS_CHARS[] = { 'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U', 'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U' };

    private static final String ICON_URLS[]  = { //
                                             //
            "http://maps.gstatic.com/mapfiles/ms2/micons/blue-dot.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/red-dot.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/green-dot.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/ltblue-dot.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/yellow-dot.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/purple-dot.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/pink-dot.png", //

            "http://maps.gstatic.com/mapfiles/ms2/micons/blue-pushpin.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/red-pushpin.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/grn-pushpin.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/ltblu-pushpin.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/ylw-pushpin.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/purple-pushpin.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/pink-pushpin.png", //

            "http://maps.gstatic.com/mapfiles/ms2/micons/blue.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/red.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/green.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/ltblue.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/yellow.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/purple.png", //
            "http://maps.gstatic.com/mapfiles/ms2/micons/pink.png" //
                                             };

    private HashSet<String>     m_globalCats = new HashSet();

    // ----------------------------------------------------------------------------------------------------
    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            reverseGeocodeIberiaTmp me = new reverseGeocodeIberiaTmp();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        File kmlFolder = new File("/Users/jzarzuela/Downloads/_tmp_/pois/_KMLs_");

        // File kmlFile = KMLDownload.downloadMap(kmlFolder, "PREP_España");
        File kmlFile = new File(kmlFolder, "PREP_España.kml");

        TPOIFileData fileData = KMLFileLoader.loadFile(kmlFile);

        for (TPOIData poi : fileData.getAllPOIs()) {
            _cleanPoiData(poi);
        }
        KMLFileWriter.saveFile(kmlFile, fileData);

        for (TPOIData poi : fileData.getAllPOIs()) {
            if (!_reverseGeocode(poi))
                break;
        }
        KMLFileWriter.saveFile(kmlFile, fileData);

        _sortPois(fileData.getAllPOIs());
        KMLFileWriter.saveFile(kmlFile, fileData);

        _setPoisIcon(fileData.getAllPOIs());
        KMLFileWriter.saveFile(kmlFile, fileData);

        ArrayList<TPOIData> pois = new ArrayList<>();
        for (TPOIData poi : fileData.getAllPOIs()) {
            if (poi.getDesc().contains("Navarra") || poi.getDesc().contains("Castilla y Leon")) {
                pois.add(poi);
            }
        }
        fileData.setAllPOIs(pois);
        kmlFile = new File(kmlFolder, "PREP_Burgos.kml");
        fileData.setName("PREP_Burgos");
        KMLFileWriter.saveFile(kmlFile, fileData);

    }

    // ----------------------------------------------------------------------------------------------------
    private void _setPoisIcon(ArrayList<TPOIData> pois) {

        final String DEFAULT_ICON_URL = "http://maps.gstatic.com/mapfiles/ms2/micons/earthquake.png";

        ArrayList<String> globCats = new ArrayList(m_globalCats);
        Collections.sort(globCats);

        for (TPOIData poi : pois) {

            poi.setIconStyle(DEFAULT_ICON_URL);
            String desc = poi.getDesc();

            for (int n = 0; n < globCats.size(); n++) {
                String cat = globCats.get(n);
                if (desc.contains(cat)) {
                    poi.setIconStyle(ICON_URLS[n % ICON_URLS.length]);
                    break;
                }
            }

            if (poi.getIconStyle().equalsIgnoreCase(DEFAULT_ICON_URL)) {
                System.out.println("* NO ICON FOR: " + poi);
            }

        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _sortPois(ArrayList<TPOIData> pois) {

        Collections.sort(pois, new Comparator<TPOIData>() {

            @Override
            public int compare(TPOIData o1, TPOIData o2) {
                return o1.getDesc().compareTo(o2.getDesc());
            }
        });
    }

    // ----------------------------------------------------------------------------------------------------
    private String _cleanChars(String text) {
        for (int n = 0; n < ORIG_CHARS.length; n++) {
            text = text.replace(ORIG_CHARS[n], SUBS_CHARS[n]);
        }
        return text;
    }

    // ----------------------------------------------------------------------------------------------------
    private String _cleanDigits(String text) {
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < text.length(); n++) {
            char c = text.charAt(n);
            if (!Character.isDigit(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ----------------------------------------------------------------------------------------------------
    private void _cleanPoiData(TPOIData poi) {

        poi.setName(_cleanChars(_cleanDigits(poi.getName())));
        poi.setDesc("");
        // System.out.println(poi);
    }

    // ----------------------------------------------------------------------------------------------------
    private String _findFirstAddressCamponentType(JSONArray results, String typeNames) throws Exception {

        typeNames = "[" + typeNames.replace('\'', '\"') + "]";

        for (int n = 0; n < results.length(); n++) {

            JSONObject item = results.getJSONObject(n);
            JSONArray ac = item.getJSONArray("address_components");
            for (int x = 0; x < ac.length(); x++) {

                JSONObject item2 = ac.getJSONObject(x);
                String itemTypes2 = item2.getString("types");
                if (!itemTypes2.equalsIgnoreCase(typeNames))
                    continue;

                String value = item2.getString("long_name");
                return _cleanChars(value);
            }
        }

        return null;
    }

    // ----------------------------------------------------------------------------------------------------
    private String _findAddressCamponentType(JSONArray results, String typeNames, String subtypeNames) throws Exception {

        typeNames = "[" + typeNames.replace('\'', '\"') + "]";
        if (subtypeNames == null) {
            subtypeNames = typeNames;
        } else {
            subtypeNames = "[" + subtypeNames.replace('\'', '\"') + "]";
        }

        for (int n = 0; n < results.length(); n++) {

            JSONObject item = results.getJSONObject(n);
            String itemTypes = item.getString("types");
            if (!itemTypes.equalsIgnoreCase(typeNames))
                continue;

            JSONArray ac = item.getJSONArray("address_components");
            for (int x = 0; x < ac.length(); x++) {

                JSONObject item2 = ac.getJSONObject(x);
                String itemTypes2 = item2.getString("types");
                if (!itemTypes2.equalsIgnoreCase(subtypeNames))
                    continue;
                String value = item2.getString("long_name");
                return _cleanChars(value);
            }
        }

        return null;
    }

    // ----------------------------------------------------------------------------------------------------
    private String _makeHttpRevGeoRequest(double lat, double lng) throws Exception {

        URL link = new URL("http://maps.google.com/maps/api/geocode/json?language=es&latlng=" + lat + "," + lng + "&sensor=false");

        char buffer[] = new char[1024];
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader isr = new InputStreamReader(link.openStream(), "UTF-8")) {
            for (;;) {
                int len = isr.read(buffer);
                if (len > 0) {
                    sb.append(buffer, 0, len);
                } else {
                    break;
                }
            }
        }
        String jsonRspTxt = sb.toString();

        Thread.sleep(200);
        return jsonRspTxt;
    }

    // ----------------------------------------------------------------------------------------------------
    private void _writeCachedHttpRevGeoRequest(double lat, double lng, String jsonRspTxt) throws Exception {

        File cacheFolder = new File("/Users/jzarzuela/Downloads/_tmp_/pois/_rgeo_cache_");
        File cachedFile = new File(cacheFolder, "lat_" + lat + "_lng_" + lng + ".txt");
        cachedFile.getParentFile().mkdirs();
        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(cachedFile), "UTF-8")) {
            osw.write(jsonRspTxt);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private String _readCachedHttpRevGeoRequest(double lat, double lng) throws Exception {

        File cacheFolder = new File("/Users/jzarzuela/Downloads/_tmp_/pois/_rgeo_cache_");
        File cachedFile = new File(cacheFolder, "lat_" + lat + "_lng_" + lng + ".txt");

        if (cachedFile.exists()) {
            char buffer[] = new char[1024];
            StringBuilder sb = new StringBuilder();
            try (InputStreamReader isr = new InputStreamReader(new FileInputStream(cachedFile), "UTF-8")) {
                for (;;) {
                    int len = isr.read(buffer);
                    if (len > 0) {
                        sb.append(buffer, 0, len);
                    } else {
                        break;
                    }
                }
            }
            String jsonRspTxt = sb.toString();

            return jsonRspTxt;
        } else {
            return null;
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private String _getLocationInfo(TPOIData poi) throws Exception {

        boolean wasCached = true;
        String jsonRspTxt = _readCachedHttpRevGeoRequest(poi.getLat(), poi.getLng());
        if (jsonRspTxt == null) {
            wasCached = false;
            jsonRspTxt = _makeHttpRevGeoRequest(poi.getLat(), poi.getLng());
        }

        JSONObject jsonRsp = new JSONObject(jsonRspTxt);
        if (!jsonRsp.getString("status").equalsIgnoreCase("OK")) {
            throw new Exception("Error parsing reverse geocoding response: " + jsonRspTxt);
        }

        if (!wasCached) {
            _writeCachedHttpRevGeoRequest(poi.getLat(), poi.getLng(), jsonRspTxt);
        }

        JSONArray results = jsonRsp.getJSONArray("results");

        if (poi.getName().equalsIgnoreCase("Ochagavia")) {
            // System.out.println(jsonRspTxt);
        }

        String loc = _findAddressCamponentType(results, "'locality','political'", null);
        if (loc == null)
            loc = _findAddressCamponentType(results, "'political'", "'political'");
        if (loc == null)
            loc = _findFirstAddressCamponentType(results, "'locality','political'");

        String aal1 = _findAddressCamponentType(results, "'administrative_area_level_1','political'", null);
        if (aal1 == null)
            aal1 = _findAddressCamponentType(results, "'locality','political'", "'administrative_area_level_1','political'");
        if (aal1 == null)
            aal1 = _findFirstAddressCamponentType(results, "'administrative_area_level_1','political'");
        if (aal1 == null)
            aal1 = "unknown";

        String aal2 = _findAddressCamponentType(results, "'administrative_area_level_2','political'", null);
        if (aal2 == null)
            aal2 = _findAddressCamponentType(results, "'locality','political'", "'administrative_area_level_2','political'");
        if (aal2 == null)
            aal2 = _findFirstAddressCamponentType(results, "'administrative_area_level_2','political'");

        String aal3 = _findAddressCamponentType(results, "'administrative_area_level_3','political'", null);

        String strLoc = aal1;
        if (aal2 != null && !aal1.contains(aal2)) {
            strLoc += ", " + aal2;
        }
        if (aal3 != null && !aal3.equalsIgnoreCase(aal2)) {
            strLoc += ", " + aal3;
        }

        if (loc != null && !loc.contains(poi.getName())) {
            strLoc += ", [" + loc + "]";
            System.out.println("* Dif name " + poi.getName() + " != " + strLoc);
        }

        m_globalCats.add(aal2 != null ? aal2 : "unknown");

        return strLoc;
    }

    // ----------------------------------------------------------------------------------------------------
    private boolean _reverseGeocode(TPOIData poi) {

        try {
            if (poi.getDesc().length() == 0) {
                String strLoc = _getLocationInfo(poi);
                poi.setDesc(strLoc);
            }
            return true;
        } catch (Throwable th) {
            System.out.println("Error doing reverse geocoding: " + th.toString());
            return false;
        }
    }
}
