/**
 * 
 */
package com.jzb.iph;

import java.io.File;
import java.net.URL;
import java.util.Formatter;
import java.util.TreeMap;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.json.JSONArray;
import org.json.JSONObject;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;

import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class IPASearch {

    private static final int SOCKET_TIMEOUT = 5000;

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("\n***** EXECUTION STARTED *****\n");
            IPASearch me = new IPASearch();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("\n***** EXECUTION FAILED *****\n");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        // https://itunes.apple.com/es/app/xxx/id379300407

        TreeMap<String, JSONObject> items = new TreeMap<>();

        _search(items, "TomTom");

        File fo = new File("/Users/jzarzuela/Desktop/ipasInfo.xls");
        WritableWorkbook owb = Workbook.createWorkbook(fo);
        WritableSheet wsheet = owb.createSheet("IPAs Info", 0);

        _printHeader(wsheet);

        int row = 2;
        for (JSONObject jo : items.values()) {
            _printLine(wsheet, row++, jo);
        }

        owb.write();
        owb.close();

    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private JSONArray _downloadSearchInfo(String name) {

        try {
            // HTTPConnection.setProxyServer("127.0.0.1", 9090);
            HTTPConnection.removeDefaultModule(HTTPClient.CookieModule.class);
            HTTPConnection.setDefaultTimeout(SOCKET_TIMEOUT);

            HTTPConnection con = new HTTPConnection(new URL("http://itunes.apple.com/search?country=es&entity=software&term=telnet"));
            HTTPResponse rsp = con.Get("/search?limit=200&country=es&media=software&entity=software,iPadSoftware&term=" + name);

            if (rsp.getStatusCode() != 200) {
                Tracer._error("Response error[%d]: %s", rsp.getStatusCode(), rsp.getReasonLine());
                return null;
            } else {
                String txtRsp = rsp.getText();
                JSONObject jsonRsp = new JSONObject(txtRsp);
                JSONArray results = jsonRsp.getJSONArray("results");
                if (results.length() <= 0) {
                    return null;
                } else {
                    return results;
                }

            }
        } catch (Throwable th) {
            Tracer._error("Error downloading info", th);
            return null;
        }

    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private void _printHeader(WritableSheet wsheet) throws Exception {

        WritableCellFormat wcf = new WritableCellFormat();
        wcf.setBackground(Colour.GRAY_25);
        wcf.setAlignment(Alignment.CENTRE);
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12);
        wf.setColour(Colour.BLACK);
        wf.setBoldStyle(WritableFont.BOLD);
        wcf.setFont(wf);
        wcf.setBorder(Border.ALL, BorderLineStyle.THIN);

        wsheet.addCell(new Label(1, 1, "universal", wcf));
        wsheet.addCell(new Label(2, 1, "trackId", wcf));
        wsheet.addCell(new Label(3, 1, "trackName", wcf));
        wsheet.addCell(new Label(4, 1, "bundleId", wcf));
        wsheet.addCell(new Label(5, 1, "version", wcf));
        wsheet.addCell(new Label(6, 1, "price", wcf));
        wsheet.addCell(new Label(7, 1, "usrRating", wcf));
        wsheet.addCell(new Label(8, 1, "cntRating", wcf));
        wsheet.addCell(new Label(9, 1, "cvAvgRating", wcf));
        wsheet.addCell(new Label(10, 1, "cvCntRating", wcf));
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private void _printLine(WritableSheet wsheet, int row, JSONObject jo) throws Exception {

        boolean isUniversal = jo.getJSONArray("features").toString().toLowerCase().contains("iosuniversal");

        WritableCellFormat wcf = new WritableCellFormat();
        wcf.setBackground(!isUniversal ? Colour.WHITE : (jo.getString("price").equals("0.0") ? Colour.SKY_BLUE : Colour.AQUA));
        wcf.setAlignment(Alignment.LEFT);
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12);
        wf.setColour(Colour.BLACK);
        wf.setBoldStyle(WritableFont.NO_BOLD);
        wcf.setFont(wf);
        wcf.setBorder(Border.ALL, BorderLineStyle.THIN);

        String name = jo.getString("trackName");
        if (name.length() > 40)
            name = name.substring(0, 40) + "...";

        wsheet.addCell(new Label(1, row, isUniversal ? "yes" : "no", wcf));
        wsheet.addCell(new Label(2, row, jo.getString("trackId"), wcf));
        wsheet.addCell(new Label(3, row, name, wcf));
        wsheet.addCell(new Label(4, row, jo.getString("bundleId"), wcf));
        wsheet.addCell(new Label(5, row, jo.getString("version"), wcf));
        wsheet.addCell(new Label(6, row, jo.getString("price"), wcf));
        wsheet.addCell(new Label(7, row, jo.optString("averageUserRating", "0"), wcf));
        wsheet.addCell(new Label(8, row, jo.optString("userRatingCount", "0"), wcf));
        wsheet.addCell(new Label(9, row, jo.optString("averageUserRatingForCurrentVersion", "0"), wcf));
        wsheet.addCell(new Label(10, row, jo.optString("userRatingCountForCurrentVersion", "0"), wcf));

        wsheet.addCell(new Label(12, row, "https://itunes.apple.com/es/app/xxx/id" + jo.getString("trackId"), wcf));

        wsheet.addCell(new Label(11, row, jo.getString("fileSizeBytes"), wcf));
        
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private void _addIPAItem(TreeMap<String, JSONObject> items, JSONObject jo) throws Exception {

        Formatter fmt = new Formatter();
        fmt.format("%s#%s#%s#%s#%s#%s#%s#%s#%s#%s", jo.getJSONArray("features").toString().toLowerCase().contains("iosuniversal") ? "0" : "1", jo.getString("price"),
                jo.optString("averageUserRating", "0"), jo.optString("userRatingCount", "0"), jo.optString("averageUserRatingForCurrentVersion", "0"),
                jo.optString("userRatingCountForCurrentVersion", "0"), jo.getString("trackId"), jo.getString("trackName"), jo.getString("bundleId"), jo.getString("version"));

        String line = fmt.toString();
        fmt.close();

        line = line.replaceAll("null", "0");
        items.put(line, jo);
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private void _search(TreeMap<String, JSONObject> items, String name) throws Exception {

        Tracer._debug("");
        Tracer._debug("----------------------------------------------------------------");
        Tracer._debug("Searching for IPA name: " + name);
        Tracer._debug("----------------------------------------------------------------");
        Tracer._debug("");

        JSONArray results = _downloadSearchInfo(name.replace(' ', '+'));
        if (results != null) {
            for (int n = 0; n < results.length(); n++) {

                JSONObject jo = results.getJSONObject(n);
                //if (jo.optString("trackName", "").toLowerCase().contains(name.toLowerCase()) || jo.optString("bundleId", "").toLowerCase().contains(name.toLowerCase())) 
                {
                    _addIPAItem(items, jo);
                }
            }
        }
    }

}
