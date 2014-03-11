/**
 * 
 */
package com.jzb.booking.old_data;

import org.json.JSONObject;

/**
 * @author jzarzuela
 * 
 */
public class TRoomData {

    public THotelData ownerHotel;
    public int        ranking;
    public int        availability;
    public boolean    cancelable;
    public int        capacity;
    public double     price;
    public double     calculatedPrice;
    public String     type;
    public boolean    withBreakfast;

    public TRoomData(THotelData ownerHotel) {
        this.ownerHotel = ownerHotel;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RoomData = { Ranking = "+ranking+", Calculated Price = " + calculatedPrice + ", Price = " + price + ", Capacity = " + capacity + ", Cancelable = " + cancelable + ", Breakfast = " + withBreakfast
                + ", Availability = " + availability + ", Type = '" + type + "' }";
    }

    public JSONObject toJSON() throws Exception {

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("ranking", this.ranking);
        jsonObj.put("availability", this.availability);
        jsonObj.put("cancelable", this.cancelable);
        jsonObj.put("capacity", this.capacity);
        jsonObj.put("price", this.price);
        jsonObj.put("calculatedPrice", this.calculatedPrice);
        jsonObj.put("type", this.type != null ? this.type : "");
        jsonObj.put("withBreakfast", this.withBreakfast);
        return jsonObj;
    }

    public static TRoomData fromJSON(THotelData ownerHotel, JSONObject jsonObj) throws Exception {

        TRoomData room = new TRoomData(ownerHotel);
        room.ranking = jsonObj.getInt("ranking");
        room.availability = jsonObj.getInt("availability");
        room.cancelable = jsonObj.getBoolean("cancelable");
        room.capacity = jsonObj.getInt("capacity");
        room.price = jsonObj.getDouble("price");
        room.calculatedPrice = jsonObj.getDouble("calculatedPrice");
        room.type = jsonObj.getString("type");
        room.withBreakfast = jsonObj.getBoolean("withBreakfast");
        return room;
    }
    
}
