/**
 * 
 */
package com.jzb.booking.data;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class HotelParser extends BaseParser {

    public static boolean excludeNonAvailable    = true;
    public static int     minAvailability        = 1;
    public static double  nonBreakfastExtraPrice = 0.0;

    // ----------------------------------------------------------------------------------------------------
    public HotelParser(String baseURL, boolean  spanishNumbers) {
        super(baseURL, spanishNumbers);
    }

    // ----------------------------------------------------------------------------------------------------
    List<DomNode> _dom_findHotelList(DomNode parentDomNode) throws Exception {
        
        List<DomNode> hotels = (List<DomNode>) parentDomNode.getByXPath("//table[contains(@class, 'hotellist')]/tbody/tr");
        if (hotels == null || hotels.size() == 0) {
            hotels = (List<DomNode>) parentDomNode.getByXPath("//div[contains(@class, 'hotellist')]//div[contains(@class, 'sr_item')]/div[contains(@class, 'sr_item_content')]");
        }
        
        if (hotels == null || hotels.size() == 0) {
            throw new Exception("Hotels don't found!!");
        }
        return hotels;
    }

    // ----------------------------------------------------------------------------------------------------
    public ArrayList<THotelData> parseHotels(DomNode htmlPage, int minRoomCapacity, int numDays, int numReqRooms) throws Exception {

        RoomParser roomParser = new RoomParser(m_baseURL, m_spanishNumbers);

        Tracer._debug("----- Parsing hotels list -----");
        ArrayList<THotelData> hotelDataList = new ArrayList<THotelData>();

        List<DomNode> hotels = _dom_findHotelList(htmlPage);
        for (DomNode hotel : hotels) {

            /*
             * if(hotel.getAttributes().getLength()==0) { Tracer._debug("  ----- TR found without hotel info -----"); continue; }
             */

            Tracer._debug("  ----- Parsing hotel data -----");
            THotelData hotelData = new THotelData();

            // Almacena el numero de dias y de habitaciones requeridas para esta busqueda
            hotelData.numDays = numDays;

            // Nombre del hotel
            hotelData.name = _getXPathValueStr(hotel, "Hotel Name", ".//a[contains(@class, 'hotel_name_link')]", null, null);
            Tracer._debug("          " + hotelData.name);

            // Enlace con los detalles del hotel
            String relURL = _getXPathValueStr(hotel, "Hotel data link", ".//a[contains(@class, 'hotel_name_link')]", null, "href");
            hotelData.dataLink = _getAbsoluteURL(relURL);

            // Votacion media del hotel
            hotelData.avgRating = _getXPathValueDouble(hotel, "Hotel Avg. Rating", ".//span[contains(@class, 'average')]", "0", null);

            // Numero de votos
            hotelData.votes = _getXPathValueInt(hotel, "Hotel Num. Votes", ".//strong[contains(@class, 'count')]", "0", null);

            // Direccion del hotel al punto de busqueda
            String value = _getXPathValueStr(hotel, "Hotel Address", ".//div[contains(@class, 'address')]", "   ", null);
            value = value.replace("Mostrar mapa", "");
            value = value.replace("Show map", "");
            hotelData.address = value.substring(0, value.length() - 3);

            // Distancia del hotel al punto de busqueda
            hotelData.distance = _getXPathValueDouble(hotel, "Hotel Distance", ".//span[contains(@class, 'distfromdest')]", "0.0", null);

            // Estrellas del hotel
            hotelData.stars = _getXPathValueInt(hotel, "Hotel Num. Estrellas", ".//span[contains(@class, 'use_sprites')]", "0", "title");

            // Habitaciones del hotel
            // Dejamos solo las habitaciones (tipos) necesarias para cubrir las necesidades (1,2,... Habitaciones)
            hotelData.rooms = _filterHotelRooms(roomParser.parseRooms(hotelData, hotel, minRoomCapacity, numDays), numReqRooms);

            // Añade el hotel a la lista si tiene habitaciones
            if (hotelData.rooms.size() > 0) {
                hotelDataList.add(hotelData);
            }

            Tracer._debug("");
        }

        return hotelDataList;
    }

    // ----------------------------------------------------------------------------------------------------
    private String _getAbsoluteURL(String relativeURL) {

        try {
            URL baseURL = new URL(m_baseURL);
            URL fullURL = new URL(baseURL, relativeURL);
            return fullURL.toString();
        } catch (Throwable th) {
            Tracer._warn("Error calculating absolute URL for hotel data: " + relativeURL, th);
            return null;
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private ArrayList<TRoomData> _filterHotelRooms(ArrayList<TRoomData> rooms, int numReqRooms) {

        // Ordena las habitaciones por precio
        Collections.sort(rooms, new Comparator<TRoomData>() {

            public int compare(TRoomData room1, TRoomData room2) {
                return Double.compare(room1.calculatedPrice, room2.calculatedPrice);
            }
        });

        // Suma su disponibilidad hasta exceder las requeridas
        int totalAvailability = 0;
        double totalCost = 0;
        ArrayList<TRoomData> filteredRooms = new ArrayList<TRoomData>();
        for (TRoomData room : rooms) {
            filteredRooms.add(room);
            totalCost += room.calculatedPrice;
            totalAvailability += room.availability;
            if (totalAvailability >= numReqRooms)
                break;

        }

        // Les aplica a todas un precio calculado medio
        double avgCost = totalCost / filteredRooms.size();
        for (TRoomData room : filteredRooms) {
            room.calculatedPrice = avgCost;
        }

        // Si se alcanzo la disponibilidad requerida retorna la lista.
        // En otro caso retorna una lista vacia
        if (totalAvailability < numReqRooms) {
            filteredRooms.clear();
        }
        return filteredRooms;
    }

}
