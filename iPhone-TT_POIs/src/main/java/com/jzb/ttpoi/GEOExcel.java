/**
 * 
 */
package com.jzb.ttpoi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileLoader;
import com.jzb.util.DefaultHttpProxy;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * @author jzarzuela
 * 
 */
public class GEOExcel {

    private static boolean s_proxyChecked = false;

    /**
     * 
     */
    public GEOExcel() {
        // TODO Auto-generated constructor stub
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        long t1 = 0, t2 = 0;
        try {
            System.out.println("\n***** EXECUTION STARTED *****\n");
            GEOExcel me = new GEOExcel();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            t2 = System.currentTimeMillis();
            System.out.println("***** EXECUTION FAILED [" + (t2 - t1) + "]*****");
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

        ArrayList<TPOIData> pois = _readPOIData();

        ArrayList<HashMap<String, String>> poisExcelInfo = new ArrayList<>();
        for (TPOIData poi : pois) {
            Thread.sleep(1000);
            HashMap<String, String> poiGeoData = _geoDataForPOI(poi);
            
            System.out.println(poi.getName() + " - [" + poiGeoData.get("fullGeoName2")+ "] - [" + poiGeoData.get("fullGeoName1")+"]");

            poiGeoData.put("poi_name", poi.getName());
            poisExcelInfo.add(poiGeoData);
        }
        
        _createExcel(poisExcelInfo);
    }

    // ----------------------------------------------------------------------------------------------------
    private ArrayList<TPOIData> _readPOIData() throws Exception {

        File kmlFile = new File("/Users/jzarzuela/Downloads/_browsers_/HT_Escocia_2014.kml");
        TPOIFileData kmlData = KMLFileLoader.loadFile(kmlFile);
        return kmlData.getAllPOIs();
    }

    // ----------------------------------------------------------------------------------------------------
    private HashMap<String, String> _geoDataForPOI(TPOIData poi) throws Exception {

        URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + poi.getLat() + "," + poi.getLng() + "&sensor=false");
        String jsonData = _downloadJSONData(url);
        HashMap<String, String> parsedData = _parseJSONData(jsonData);
        return parsedData;
    }

    // ----------------------------------------------------------------------------------------------------
    private HashMap<String, String> _parseJSONData(String jsonData) throws Exception {

        HashMap<String, String> parsedData = new HashMap();

        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        builder.serializeNulls();
        builder.setPrettyPrinting();

        Gson gson = builder.create();
        HashMap<String, Object> data = gson.fromJson(jsonData, HashMap.class);

        String status = (String) data.get("status");
        if (status == null || !status.equals("OK")) {
            // Algo fallo
            return parsedData;
        }

        ArrayList<Map<String, ArrayList>> results = (ArrayList) data.get("results");
        if (results != null) {
            for (Map<String, ArrayList> resultItem : results) {

                ArrayList<Map<String, Object>> addressItem = resultItem.get("address_components");

                for (Map<String, Object> addressComp : addressItem) {

                    ArrayList<String> types = (ArrayList) addressComp.get("types");
                    if (types != null) {
                        String typesStr = types.toString().toLowerCase();
                        if (typesStr.contains("postal_town"))
                            parsedData.put("postal_town", (String) addressComp.get("long_name"));
                        if (typesStr.contains("locality"))
                            parsedData.put("locality", (String) addressComp.get("long_name"));
                        if (typesStr.contains("administrative_area_level_2"))
                            parsedData.put("administrative_area_level_2", (String) addressComp.get("long_name"));
                    }
                }

            }
        }

        String fullGeoName1 = "";
        String fullGeoName2 = "";
        
        if (parsedData.get("postal_town") != null) {
            fullGeoName1 = parsedData.get("postal_town");
        }

        if (parsedData.get("locality") != null) {
            if (fullGeoName1.length() > 0)
                fullGeoName1 += "|";
            fullGeoName1 += parsedData.get("locality");
            fullGeoName2 = parsedData.get("locality");
        }
   
        if (parsedData.get("administrative_area_level_2") != null) {
            if (fullGeoName1.length() > 0)
                fullGeoName1 += "|";
            if (fullGeoName2.length() > 0)
                fullGeoName2 += "|";
            fullGeoName1 += parsedData.get("administrative_area_level_2");
            fullGeoName2 += parsedData.get("administrative_area_level_2");
        }

        parsedData.put("fullGeoName1", fullGeoName1);
        parsedData.put("fullGeoName2", fullGeoName2);

        return parsedData;
    }

    // ----------------------------------------------------------------------------------------------------
    private String _downloadJSONData(URL link) throws Exception {

        _checkProxy();

        Charset utf8 = Charset.forName("UTF-8");
        StringBuilder sb = new StringBuilder();

        byte buffer[] = new byte[65536];
        InputStream bis = new BufferedInputStream(link.openStream());
        for (;;) {
            int len = bis.read(buffer);
            if (len > 0) {
                String str = new String(buffer, 0, len, utf8);
                sb.append(str);
            } else {
                break;
            }
        }
        bis.close();

        return sb.toString();
    }

    // ----------------------------------------------------------------------------------------------------
    private void _createExcel(ArrayList<HashMap<String, String>> poisExcelInfo) throws Exception {

        File fo = new File("/Users/jzarzuela/Downloads/_browsers_/HT_Escocia_2014.xls");
        WritableWorkbook owb = Workbook.createWorkbook(fo);
        WritableSheet wsheet = owb.createSheet("POIs info", 0);

        int r = 1;
        for (HashMap<String, String> rowData : poisExcelInfo) {

            String value;
           r++;
           
            value = rowData.get("poi_name");
            if (value != null) {
                Label cell = new Label(2, r, value);
                wsheet.addCell(cell);
            }

            value = rowData.get("fullGeoName1");
            if (value != null) {
                Label cell = new Label(3, r, value);
                wsheet.addCell(cell);
            }

            value = rowData.get("fullGeoName2");
            if (value != null) {
                Label cell = new Label(4, r, value);
                wsheet.addCell(cell);
            }

            value = rowData.get("postal_town");
            if (value != null) {
                Label cell = new Label(5, r, value);
                wsheet.addCell(cell);
            }

            value = rowData.get("locality");
            if (value != null) {
                Label cell = new Label(6, r, value);
                wsheet.addCell(cell);
            }

            value = rowData.get("administrative_area_level_2");
            if (value != null) {
                Label cell = new Label(7, r, value);
                wsheet.addCell(cell);
            }
        }

        owb.write();
        owb.close();

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _checkProxy() throws Exception {
        if (!s_proxyChecked) {
            DefaultHttpProxy.setDefaultJavaProxy();
            s_proxyChecked = true;
        }
    }
}
