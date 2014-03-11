/**
 * 
 */
package com.jzb.booking.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.jzb.util.Tracer;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author jzarzuela
 * 
 */
public class PageDataParser extends BaseDataParser {

    // ----------------------------------------------------------------------------------------------------
    public static TPageData parse(String pageHtml) throws Exception {

        Tracer._debug("Parsing page info (hotels, location, next page");

        TPageData pageData = new TPageData();

        Document pageDoc = Jsoup.parse(pageHtml);

        pageData.hotels = _parsePageHotelsData(pageDoc);

        pageData.nextPageUrl = _parsePageNextPageUrl(pageDoc);

        String geolocationBox = _parseSearchGeolocation(pageDoc);
        __calcPageCenterLatLng(pageData, geolocationBox);
        Tracer._info("MAP CENTRE URL => http://maps.google.com/maps?z=11&q=" + pageData.lat + "," + pageData.lng);

        pageData.numDays = __searchNumDays(pageHtml);

        return pageData;
    }

    // ----------------------------------------------------------------------------------------------------
    private static void __calcPageCenterLatLng(TPageData pageData, String geolocationBox) throws Exception {

        try {
            String token;
            StringTokenizer st = new StringTokenizer(geolocationBox, ",");

            token = st.nextToken();
            double lng1 = Double.parseDouble(token.trim());
            token = st.nextToken();
            double lat1 = Double.parseDouble(token.trim());
            token = st.nextToken();
            double lng2 = Double.parseDouble(token.trim());
            token = st.nextToken();
            double lat2 = Double.parseDouble(token.trim());

            pageData.lat = lat1 + (lat2 - lat1) / 2;
            pageData.lng = lng1 + (lng2 - lng1) / 2;
        } catch (Throwable th) {
            Tracer._warn("Error parsing page geolocation: " + geolocationBox, th);
            pageData.lat = pageData.lng = 0;
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static String _parseSearchGeolocation(Element doc) throws Exception {

        String value = _parseElementAttr("Search Geolocation", doc, "data-bounding-box", "a#swap_map");
        return value;
    }

    // ----------------------------------------------------------------------------------------------------
    private static String _parsePageNextPageUrl(Element doc) throws Exception {

        Element e = _selectSingleElementOptional("Next Page URL", doc, "a.paging-next");
        if (e == null)
            return null;

        String nextUrl = "http://www.booking.com" + e.attr("href");
        return nextUrl;
    }

    // ----------------------------------------------------------------------------------------------------
    private static ArrayList<THotelData> _parsePageHotelsData(Element doc) throws Exception {

        ArrayList<THotelData> hotels = new ArrayList<>();

        Elements eList = _selectElementList("Hotel Links", doc, "a.hotel_name_link");
        for (Element e : eList) {

            THotelData hotel = new THotelData();
            hotel.dataLink = "http://www.booking.com" + e.attr("href");
            hotel.name = e.text();

            hotels.add(hotel);
        }
        return hotels;
    }

    // ----------------------------------------------------------------------------------------------------
    private static int __searchNumDays(String htmlText) {

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
