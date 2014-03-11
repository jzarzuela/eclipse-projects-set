/**
 * 
 */
package com.jzb.booking.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author jzarzuela
 * 
 */
public class HotelDataFilter {

    // ----------------------------------------------------------------------------------------------------
    public static THotelData updatedAndFilter(THotelData hotel) {

        if(_filterHotelByRooms(hotel)==null) {
            return null;
        } 
        
        return _filterHotelByMaxDayPrice(hotel);
        
    }

    // ----------------------------------------------------------------------------------------------------
    private static THotelData _filterHotelByMaxDayPrice(THotelData hotel) {
    
        // No hay filtro
        if(ParserSettings.maxTotalPrice<1) 
            return hotel;
        
        return hotel.ttlPrice<=ParserSettings.maxTotalPrice ? hotel : null;
    }
    
    // ----------------------------------------------------------------------------------------------------
    private static THotelData _filterHotelByRooms(THotelData hotel) {

        ArrayList<TRoomData> filteredRooms = new ArrayList<>();

        // Ordena las habitaciones por precio
        Collections.sort(hotel.rooms, new Comparator<TRoomData>() {

            public int compare(TRoomData room1, TRoomData room2) {
                return Double.compare(room1.calculatedPrice, room2.calculatedPrice);
            }
        });

        // Reestablece la maxima disponibilidad antes de filtrar
        for (TRoomData room : hotel.rooms) {
            room.fAvailability = room.availability;
        }

        // Itera buscando las habitaciones mas baratas que cubren las necesidades
        ArrayList<Integer> roomsNeeded = new ArrayList<>(ParserSettings.minRoomCapacities);
        while (roomsNeeded.size() > 0) {

            int capacityNeeded = roomsNeeded.remove(0);

            for (TRoomData room : hotel.rooms) {
                if (room.fAvailability > 0 && room.capacity >= capacityNeeded) {
                    room.fAvailability--;
                    filteredRooms.add(room);
                    break;
                }
            }
        }

        if (filteredRooms.size() == ParserSettings.minRoomCapacities.size()) {

            // Consigue los valores medios
            hotel.avgCalculatedPrice = 0;
            hotel.avgPrice = 0;
            hotel.ttlPrice = 0;
            hotel.ranking = 0;
            hotel.cancelable = true;
            hotel.withBreakfast = true;

            for (TRoomData room : filteredRooms) {

                hotel.avgCalculatedPrice += room.calculatedPrice;
                hotel.avgPrice += room.price;
                hotel.ttlPrice += room.calculatedPrice;
                hotel.avgDayPrice += room.dayPrice;
                hotel.cancelable &= room.cancelable;
                hotel.withBreakfast &= room.withBreakfast;
            }

            hotel.avgCalculatedPrice = hotel.avgCalculatedPrice / ParserSettings.minRoomCapacities.size();
            hotel.avgPrice = hotel.avgPrice / ParserSettings.minRoomCapacities.size();
            hotel.avgDayPrice = hotel.avgDayPrice / ParserSettings.minRoomCapacities.size();

            // Hotel adecuado
            return hotel;

        } else {

            // Hotel filtrado
            return null;
        }

    }
}
