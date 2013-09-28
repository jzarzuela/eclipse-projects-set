/**
 * 
 */
package com.jzb.booking.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebResponseData;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class PageRequestParser {

    private String   m_baseURL;
    private boolean  m_spanishNumbers;
    private HtmlPage m_page;
    private int      m_numDays;

    // ----------------------------------------------------------------------------------------------------
    public PageRequestParser() {
    }

    // ----------------------------------------------------------------------------------------------------
    public ArrayList<THotelData> extractHotelInfo() throws Exception {

        Tracer._debug("***** Extracting hotels info ***********************************************");
        Tracer._debug("");

        // ----- Parsea los hoteles ---------------------------------------------------------------
        try {
            HotelParser hotelParser = new HotelParser(m_baseURL, m_spanishNumbers);
            ArrayList<THotelData> hotels = hotelParser.parseHotels(m_page, ParserSettings.minRoomCapacity, m_numDays, ParserSettings.numReqRooms);
            return hotels;
        } catch (Exception ex) {
            _dumpForDebugging();
            throw ex;
        }
    }

    // ----------------------------------------------------------------------------------------------------
    public void initPage(String baseURL, String htmlText) throws Exception {

        Tracer._debug("***** Parsing page HTML info ***********************************************");
        WebRequest wreq = new WebRequest(new URL("http://www.booking.com"));

        // ----- Busca el numero de dias en la pagina ---------------------------------------------
        m_numDays = _searchNumDays(htmlText);

        // ----- Busca el idioma utilizado en la pagina -------------------------------------------
        m_spanishNumbers = _spanishNumbers(htmlText);

        // ----- Prepara la peticion --------------------------------------------------------------
        ArrayList<NameValuePair> responseHeaders = new ArrayList<NameValuePair>();
        responseHeaders.add(new NameValuePair("", ""));
        byte[] wbody = htmlText.getBytes("UTF-8");
        WebResponseData wdata = new WebResponseData(wbody, 200, "OK", responseHeaders);
        WebResponse wresp = new WebResponse(wdata, wreq, 0);
        WebWindow ww = _getWebClient().getCurrentWindow().getTopWindow();

        // ----- Parsea la pagina -----------------------------------------------------------------
        m_baseURL = baseURL;
        m_page = HTMLParser.parseXHtml(wresp, ww);
        // _dumpForDebugging();
    }

    // ----------------------------------------------------------------------------------------------------
    public String searchNextPageLink() {

        Tracer._debug("***** Searching next page link *********************************************");
        Tracer._debug("");

        // ----- Busca enlace a la siguiente pagina -----------------------------------------------
        List<HtmlAnchor> morePagesLinks = (List<HtmlAnchor>) m_page.getByXPath(".//div[contains(@class,'results-paging')]/a[contains(@class, 'paging-next')]");
        if (morePagesLinks != null && morePagesLinks.size() > 0) {
            return morePagesLinks.get(0).getHrefAttribute();
        } else {
            return null;
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private void _dumpForDebugging() {

        // ------ PARA DEPURAR --------------------------------------------------------------------
        FileWriter fw = null;
        try {

            File destFolder = new File("./");
            destFolder.mkdirs();

            File outInfoFile = new File(destFolder, "page_dump_" + Thread.currentThread().getName() + ".html");
            fw = new FileWriter(outInfoFile);
            fw.write(m_page.asXml());

        } catch (Throwable th) {
            Tracer._error("Error dumping info for debugging", th);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException th) {
                    Tracer._error("Error dumping info for debugging", th);
                }
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private WebClient _getWebClient() {

        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "fatal");
        System.setProperty("log4j.rootCategory", "FATAL");

        WebClient webClient = new WebClient();

        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setTimeout(20000);
        webClient.getOptions().setCssEnabled(false);
        CookieManager cm = new CookieManager();
        webClient.setCookieManager(cm);

        return webClient;
    }

    // ----------------------------------------------------------------------------------------------------
    private boolean _spanishNumbers(String htmlText) {

        int p1 = htmlText.indexOf("uc_language");
        if (p1 < 0) {
            Tracer._warn("No uc_language found in html text");
            return false;
        }
        int p2 = htmlText.indexOf("</a>", p1);
        if (p2 < 0) {
            Tracer._warn("No uc_language ending found in html text");
            return false;
        }
        String langStr = htmlText.substring(p1, p2);
        int p3 = langStr.indexOf("es.png");
        int p4 = langStr.indexOf("/es/");
        return p3 >= 0 && p4 >= 0;
    }

    // ----------------------------------------------------------------------------------------------------
    private int _searchNumDays(String htmlText) {

        try {
            // ---- Date In --------------------------------------------
            int p1 = htmlText.indexOf("date_in: '");
            if (p1 < 0) {
                Tracer._warn("No date_in found in html text");
                return 0;
            }
            int p2 = htmlText.indexOf("'", p1 + 11);
            if (p2 < 0) {
                Tracer._warn("No date_in found in html text");
                return 0;
            }
            String dateInStr = htmlText.substring(p1 + 11, p2);

            // ---- Date Out --------------------------------------------
            int p3 = htmlText.indexOf("date_out: '", p2);
            if (p3 < 0) {
                Tracer._warn("No date_out found in html text");
                return 0;
            }
            int p4 = htmlText.indexOf("'", p3 + 12);
            if (p4 < 0) {
                Tracer._warn("No date_out found in html text");
                return 0;
            }
            String dateOutStr = htmlText.substring(p3 + 12, p4);

            // ---- Calc difference --------------------------------------------
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            DateTime dateIn = new DateTime(sdf.parse(dateInStr));
            DateTime dateOut = new DateTime(sdf.parse(dateOutStr));
            Days days = Days.daysBetween(dateIn, dateOut);
            int value = days.getDays();
            return value;

        } catch (Throwable th) {
            Tracer._warn("Error calculating number of days in search", th);
            return 0;
        }
    }

}
