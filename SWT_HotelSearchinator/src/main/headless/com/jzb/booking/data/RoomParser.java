/**
 * 
 */
package com.jzb.booking.data;

import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class RoomParser extends BaseParser {

    public static boolean excludeNonAvailable = true;
    public static int     MAX_AVAILABILITY    = 9;

    // ----------------------------------------------------------------------------------------------------
    public RoomParser(String baseURL, boolean  spanishNumbers) {
        super(baseURL, spanishNumbers);
    }

    // ----------------------------------------------------------------------------------------------------
    public ArrayList<TRoomData> parseRooms(THotelData ownerHotel, DomNode htmlHotelInfo, int minRoomCapacity, int numDays) throws Exception {

        ArrayList<TRoomData> roomDataList = new ArrayList<TRoomData>();

        Tracer._debug("    ----- Parsing rooms list -----");
        List<HtmlElement> rooms = (List<HtmlElement>) htmlHotelInfo.getByXPath(".//table[contains(@class, 'featuredRooms sr_table_undesign')]/tbody/tr[contains(@class, 'roomrow')]");
        if(rooms.size()==0) {
            rooms = (List<HtmlElement>) htmlHotelInfo.getByXPath(".//table[contains(@class, 'sr_room_table')]/tbody/tr[contains(@class, 'roomrow')]");
        }
        for (HtmlElement room : rooms) {

            Tracer._debug("      ----- Parsing room data -----");
            String strValue;
            TRoomData roomData = new TRoomData(ownerHotel);

            // Tipo de habitacion
            roomData.type = _getXPathValueStr(room, "Room Type", "./td/div//a[contains(@class, 'room_link')]", "", null);

            // Ocupacion maxima de la habitacion
            // roomData.capacity = _getXPathValueInt(room, "Room Capacity", ".//td[contains(@class, 'maxPersons')]//span[@class='hideme']", null, null);
            String capacityStr = _getXPathValueStr(room, "Room Capacity", ".//td[contains(@class, 'maxPersons')]", "0", null);
            roomData.capacity = _parseIntValue(capacityStr, "Room Capacity");
            if (capacityStr.indexOf('+') >= 0) {
                String kidsCapacity = _getXPathValueStr(room, "Room Capacity", ".//td[contains(@class, 'maxPersons')]//img[contains(@class, 'occsprite maxkids')]", "0", "class");
                int kids = _parseIntValue(kidsCapacity, "Room Capacity-Kids");
                roomData.capacity += kids;
            }

            // Indicación de si hay disponibilida para la habitacion
            strValue = _getXPathValueStr(room, "Room Availability", ".//td[contains(@class, 'roomAvailability')]", null, null);
            strValue = strValue.toLowerCase();
            if (strValue.contains("disponible") || strValue.contains("available")) {
                roomData.availability = MAX_AVAILABILITY;
            } else {
                int p1 = strValue.indexOf("solo queda");
                if (p1 < 0)
                    p1 = strValue.indexOf("only");
                if (p1 >= 0) {
                    strValue = _cleanNonDigits(strValue.substring(p1), false);
                    int intVal = Integer.valueOf(strValue);
                    roomData.availability = intVal;
                } else {
                    roomData.availability = 0;
                }
            }

            // Precio de la habitacion
            roomData.price = _getXPathValueCurrency(room, "Room price", ".//strong[contains(@class, 'price')]", null, null);
            roomData.calculatedPrice = roomData.price;

            // Siguientes campos basados en texto
            // Indicacion de si se puede cancelar gratuitamente
            String roomText = room.asText().toLowerCase();
            roomData.cancelable = roomText.contains("cancelación gratuita") || roomText.contains("free cancellation");
            roomData.withBreakfast = roomText.contains("desayuno incluido") || roomText.contains("breakfast included");

            // Ajusta el precio segun estos dos ultimos datos (EN EL ORDEN QUE ESTAN)
            if (!roomData.cancelable) {
                roomData.calculatedPrice *= ParserSettings.NonCancelableIncrement;
            }
            if (!roomData.withBreakfast) {
                roomData.calculatedPrice += ParserSettings.FamilyBreakfastCostPerDay * (double) numDays;
            }

            // Añade la informacion de la habitacion a la lista
            // Siempre que no haya que filtrarla
            if (!excludeNonAvailable) {
                roomDataList.add(roomData);
            } else {
                // Solo añade las habitaciones que nos valen
                if (roomData.capacity >= minRoomCapacity) {
                    roomDataList.add(roomData);
                }
            }
        }

        return roomDataList;
    }
}
