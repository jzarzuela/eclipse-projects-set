/**
 * 
 */
package com.jzb.booking.data;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import com.jzb.util.Tracer;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import HTTPClient.ModuleException;
import HTTPClient.NVPair;
import HTTPClient.ParseException;

/**
 * @author jzarzuela
 * 
 */
public abstract class BaseDataParser {

    protected static final int SOCKET_TIMEOUT = 5000;
    public static boolean      spanish        = true;

    // ----------------------------------------------------------------------------------------------------
    protected static String _parseElementText(String label, Element doc, String selector) throws Exception {

        Element e = _selectSingleElement(label, doc, selector);
        String value = e.text();
        return value;
    }

    // ----------------------------------------------------------------------------------------------------
    protected static String _parseElementAttr(String label, Element doc, String attr, String selector) throws Exception {

        Element e = _selectSingleElement(label, doc, selector);
        String value = e.attr(attr);
        return value;
    }

    // ----------------------------------------------------------------------------------------------------
    protected static Element _selectSingleElement(String label, Element doc, String selector) throws Exception {

        Elements eList = doc.select(selector);
        if (eList == null || eList.size() == 0) {
            throw new Exception("Error Selecting: Element '" + label + "' NOT FOUND with selector '" + selector + "'");
        }
        if (eList.size() != 1) {
            throw new Exception("Error Selecting: Element '" + label + "' FOUND MORE THAN ONCE with selector '" + selector + "'");
        }

        Element e = eList.get(0);
        return e;
    }

    // ----------------------------------------------------------------------------------------------------
    protected static Elements _selectElementList(String label, Element doc, String selector) throws Exception {

        Elements eList = _selectElementListOptional(label, doc, selector);
        if (eList == null || eList.size() == 0) {
            throw new Exception("Error Selecting: Elements '" + label + "' NOT FOUND with selector '" + selector + "'");
        }
        return eList;
    }

    // ----------------------------------------------------------------------------------------------------
    protected static Elements _selectElementListOptional(String label, Element doc, String selector) {

        Elements eList = doc.select(selector);
        if (eList == null || eList.size() == 0) {
            return null;
        } else {
            return eList;
        }

    }

    // ----------------------------------------------------------------------------------------------------
    protected static Element _selectSingleElementOptional(String label, Element doc, String selector) {

        Elements eList = doc.select(selector);
        if (eList == null || eList.size() == 0) {
            return null;
        } else {
            Element e = eList.get(0);
            return e;
        }

    }

    // ----------------------------------------------------------------------------------------------------
    protected static ArrayList<String> _searchCommentSections(String label, String html, String start, String end) throws Exception {

        ArrayList<String> sections = new ArrayList<>();

        int p1 = 0, p2 = 0;
        for (;;) {

            p1 = html.indexOf(start, p2);
            if (p1 < 0)
                if (sections.size() == 0)
                    throw new Exception("Error searching comment section '" + label + "': Starting comment not found: '" + start + "'");
                else
                    return sections;

            p2 = html.indexOf(end, p1);
            if (p2 < 0)
                throw new Exception("Error searching comment section '" + label + "': Ending comment not found: '" + end + "'");

            String str = html.substring(p1, p2 + end.length());
            sections.add(str);

            p1 = p2;
        }

    }

    // ----------------------------------------------------------------------------------------------------
    protected static String _downloadUrlData(String dataURL) throws Exception {

        for (int n = 0; n < 5; n++) {
            try {
                String str = __downloadUrlData(dataURL);
                return str;
            } catch (SocketTimeoutException ste) {
                // Reintenta 5 veces
                Tracer._warn("Warning SocketTimeoutException captured. Retrying...");
            }
        }

        throw new Exception ("Error: Too many SocketTimeoutException captured");

    }

    // ----------------------------------------------------------------------------------------------------
    protected static String __downloadUrlData(String dataURL) throws Exception {

        try {
            Tracer._debug("  _downloadUrlData: " + dataURL);

            // El HTTPClient tiene un problema con las cookies de Google. Esto no haría falta
            HTTPConnection.removeDefaultModule(HTTPClient.CookieModule.class);

            // Establece un timeout por defecto
            HTTPConnection.setDefaultTimeout(SOCKET_TIMEOUT);

            // Hace la llamada a través del proxy
            HTTPConnection con = new HTTPConnection(new URL("http://www.booking.com"));
            NVPair Accept = new NVPair("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            NVPair UserAgent = new NVPair("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
            con.setDefaultHeaders(new NVPair[] { Accept, UserAgent });
            HTTPResponse rsp = con.Get(dataURL);

            // Se lee el resultado
            if (rsp.getStatusCode() != 200) {
                throw new Exception("Error downloading page: Status code = " + rsp.getStatusCode() + ", reason = '" + rsp.getReasonLine() + "'");
            }
            String html = rsp.getText();
            return html;

        } catch (IOException | ModuleException | ParseException ex) {
            throw new Exception("Error downloading page: Previous exception: " + ex.getClass().getName() + ", message = " + ex.getMessage(), ex);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    protected static String _cleanNotNumbers(String str) {

        StringBuffer sb = new StringBuffer();
        for (int n = 0; n < str.length(); n++) {
            char c = str.charAt(n);
            if (Character.isDigit(c) || (spanish && c == ',') || (!spanish && c == '.'))
                sb.append(c);
        }
        return sb.toString();
    }

    // ----------------------------------------------------------------------------------------------------
    protected static double _parseLat(String geolocation) {

        try {
            int p = geolocation.indexOf(',');
            if (p >= 0) {
                String str = geolocation.substring(p + 1);
                double value = Double.parseDouble(str);
                return value;
            }
            return 0;
        } catch (Throwable th) {
            Tracer._warn("Error parsing latitude from: " + geolocation, th);
            return 0;
        }
    }

    // ----------------------------------------------------------------------------------------------------
    protected static double _parseLng(String geolocation) {

        try {
            int p = geolocation.indexOf(',');
            if (p >= 0) {
                String str = geolocation.substring(0, p);
                double value = Double.parseDouble(str);
                return value;
            }
            return 0;
        } catch (Throwable th) {
            Tracer._warn("Error parsing longitude from: " + geolocation, th);
            return 0;
        }
    }

    // ----------------------------------------------------------------------------------------------------
    protected static double _calcDistance(double lat1, double lng1, double lat2, double lng2) {

        try {
            double rLat1 = lat1 * Math.PI / 180.0;
            double rLng1 = lng1 * Math.PI / 180.0;
            double rLat2 = lat2 * Math.PI / 180.0;
            double rLng2 = lng2 * Math.PI / 180.0;
            double earthRadius = 6378000; // In metres

            double result = Math.acos(Math.cos(rLat1) * Math.cos(rLng1) * Math.cos(rLat2) * Math.cos(rLng2) + Math.cos(rLat1) * Math.sin(rLng1) * Math.cos(rLat2) * Math.sin(rLng2) + Math.sin(rLat1)
                    * Math.sin(rLat2))
                    * earthRadius;

            return result;
        } catch (Throwable th) {
            Tracer._warn("Error calculating distance between points (" + lat1 + "," + lng1 + ")-(" + lat2 + "," + lng2 + ")", th);
            return 0;
        }

    }
}
