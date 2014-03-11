/**
 * 
 */
package com.jzb.booking.data;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jzb.util.Tracer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author jzarzuela
 * 
 */
public class HotelDataParser extends BaseDataParser {

    // ----------------------------------------------------------------------------------------------------
    public static THotelData parse(THotelData hotel, int numDays, double origLat, double origLng) throws Exception {

        try {
            return _parse(hotel, numDays, origLat, origLng);
        } catch (Throwable th) {
            Tracer._error("Error parsing hotel URL = " + hotel.dataLink, th);
            throw new Exception("Error parsing hotel URL = " + hotel.dataLink, th);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    public static THotelData _parse(THotelData hotel, int numDays, double origLat, double origLng) throws Exception {

        Tracer._debug("Parsing hotel info (" + hotel.name + ")");

        HotelDataParser.spanish = hotel.dataLink.contains(".es.html");

        String html = _downloadUrlData(hotel.dataLink);

        java.io.PrintWriter pw = new java.io.PrintWriter("/Users/jzarzuela/Desktop/imgs/p.html");
        pw.println(html);
        pw.close();

        ArrayList<String> sections = _searchCommentSections("Hotel Data", html, "<!-- hotel_header.inc -->", "<!-- end hotel_description.inc -->");
        if (sections.size() != 1) {
            throw new Exception("Error searching Hotel Data comment section. More than one found");
        }


        Tracer._debug("  Parsing hotel's basic info");
        Document hotelDoc = Jsoup.parse(sections.get(0));

        hotel.name = _parseHotelName(hotelDoc);
        hotel.avgRating = _parseHotelAvgRating(hotelDoc);
        hotel.stars = _parseHotelStars(hotelDoc);
        hotel.votes = _parseHotelVotes(hotelDoc);
        hotel.address = _parseHotelAddress(hotelDoc);

        String geolocation = _parseHotelGeolocation(hotelDoc);
        hotel.lat = _parseLat(geolocation);
        hotel.lng = _parseLng(geolocation);

        hotel.distance = _calcDistance(hotel.lat, hotel.lng, origLat, origLng);

        Tracer._debug("  Parsing hotel's rooms info");

        sections = _searchCommentSections("Hotel Data", html, "<!-- start: roomAvailability_roomType -->", "<!-- end: roomAvailability_roomDetails -->");
        for (String roomHtml : sections) {

            Document roomDoc = Jsoup.parse(roomHtml);

            TRoomData room = new TRoomData(hotel);

            room.availability = _parseRoomAvilability(roomDoc);
            room.fAvailability = room.availability;
            room.cancelable = _parseRoomCancelable(roomDoc);
            room.capacity = _parseRoomCapacity(roomDoc);
            room.price = _parseRoomPrice(roomDoc);
            room.calculatedPrice = room.price;
            room.type = _parseRoomType(roomDoc);
            room.withBreakfast = _parseRoomWithBreakfast(roomHtml);

            // Ajusta el precio total con el desayuno y la cancelacion
            if (!room.cancelable) {
                room.calculatedPrice *= ParserSettings.NonCancelableIncrement;
            }
            if (!room.withBreakfast) {
                room.calculatedPrice += ParserSettings.FamilyBreakfastCostPerDay * (double) numDays;
            }

            // Calcule el precio por dia
            room.dayPrice = room.calculatedPrice / numDays;
            
            hotel.rooms.add(room);
        }

        Tracer._debug("  Done parsing hotel!");
        return hotel;
    }

    // ----------------------------------------------------------------------------------------------------
    private static int _parseRoomAvilability(Element doc) throws Exception {

        Element e = __selectPriceSelect("Room Avilability", doc);
        Elements children = e.children();
        int number = children.size();
        return number - 1;
    }

    // ----------------------------------------------------------------------------------------------------
    private static boolean _parseRoomCancelable(Element doc) throws Exception {

        // ¿existe siempre?
        Element e = _selectSingleElementOptional("Room Cancelable", doc, "span.exp-hp_rt_pay_when_you_stay");
        return e != null;
    }

    // ----------------------------------------------------------------------------------------------------
    private static int _parseRoomCapacity(Element doc) throws Exception {

        Element selectField = __selectPriceSelect("Room Capacity", doc);
        Element div = selectField.parent();

        Elements eList = null;
        while (div != null) {
            eList = _selectElementListOptional("Room Capacity", div, "img.occsprite");
            if (eList != null)
                break;
            div = div.previousElementSibling();
        }

        if (eList == null) {
            throw new Exception("Error parsin Room Capacity. Nodes not found");
        }

        int value = 0;
        Pattern p = Pattern.compile("max([0-9]+)|maxkids([0-9]+)");
        for (Element img : eList) {
            String str = img.attr("class");
            Matcher m = p.matcher(str);
            if (!m.find()) {
                throw new Exception("Error parsin Room Capacity. Class 'max?? attribute not found'");
            }
            for (int n = 1; n <= m.groupCount(); n++) {
                str = m.group(n);
                value += str != null ? Integer.parseInt(str) : 0;
            }
        }

        return value;
    }

    // ----------------------------------------------------------------------------------------------------
    private static double _parseRoomPrice(Element doc) throws Exception {

        Element e = __selectPriceSelect("Room Price", doc).children().get(1);
        String str = e.attr("data-price-without-addons");
        str = _cleanNotNumbers(str);
        str = str.replace(',', '.');
        double value = Double.parseDouble(str);
        return value;
    }

    // ----------------------------------------------------------------------------------------------------
    private static String _parseRoomType(Element doc) throws Exception {

        String str = _parseElementText("Room Type", doc, "a.togglelink");
        return str;
    }

    // ----------------------------------------------------------------------------------------------------
    private static boolean _parseRoomWithBreakfast(String html) throws Exception {

        boolean value = html.contains("Desayuno incluido") || html.contains("Breakfast included");
        return value;
    }

    // ----------------------------------------------------------------------------------------------------
    private static Element __selectPriceSelect(String label, Element doc) throws Exception {

        Elements eList = _selectElementListOptional(label, doc, "select.b_room_selectbox.b_free_cancel");
        if (eList == null) {
            eList = _selectElementListOptional(label, doc, "select.b_room_selectbox");
        }
        if (eList == null || eList.size() < 1 || eList.get(0).children().size() < 2) {
            throw new Exception("Error parsing '" + label + "': No adecuate SELECT field found");
        }
        return eList.get(0);
    }

    // ----------------------------------------------------------------------------------------------------
    private static String _parseHotelName(Element doc) throws Exception {

        String value = _parseElementText("Hotel Name", doc, "span#hp_hotel_name");
        return value;
    }

    // ----------------------------------------------------------------------------------------------------
    private static double _parseHotelAvgRating(Element doc) throws Exception {

        Element e = _selectSingleElementOptional("Hotel Avg Rating", doc, "span.average");
        if(e==null) return 0;
        
        String str = e.text();
        str = str.replace(',', '.');
        double value = Double.parseDouble(str);
        return value;
    }

    // ----------------------------------------------------------------------------------------------------
    private static int _parseHotelStars(Element doc) throws Exception {

        Element e = _selectSingleElementOptional("Hotel Starts", doc, "span.stars");
        if (e != null) {
            String str = e.attr("title");
            str = _cleanNotNumbers(str);
            int value = Integer.parseInt(str);
            return value;
        } else {
            return 0;
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static int _parseHotelVotes(Element doc) throws Exception {

        Element e = _selectSingleElementOptional("Hotel Votes", doc, "strong.count");
        if(e==null)
            return 0;
        
        String str = e.text();
        str = _cleanNotNumbers(str);
        int value = Integer.parseInt(str);
        return value;
    }

    // ----------------------------------------------------------------------------------------------------
    private static String _parseHotelAddress(Element doc) throws Exception {

        Element e = _selectSingleElement("Hotel Address", doc, "p.address");
        e = _selectSingleElement("Hotel Address", e, "span.jq_tooltip");
        String str = e.text();
        return str;
    }

    // ----------------------------------------------------------------------------------------------------
    private static String _parseHotelGeolocation(Element doc) throws Exception {

        Element e = _selectSingleElement("Hotel Address", doc, "p.address");
        e = _selectSingleElement("Hotel Address", e, "span.jq_tooltip");
        String coords = e.attr("data-coords");
        return coords;
    }
}
