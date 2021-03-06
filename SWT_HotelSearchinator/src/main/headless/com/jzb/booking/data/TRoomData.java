/**
 * 
 */
package com.jzb.booking.data;

import org.json.JSONObject;

/**
 * @author jzarzuela
 * 
 */
public class TRoomData {

    public THotelData ownerHotel;
    public int        availability;
    public boolean    cancelable;
    public int        capacity;
    public double     price;
    public double     calculatedPrice;
    public double     dayPrice;
    public String     type;
    public boolean    withBreakfast;

    public int        fAvailability;

    public TRoomData(THotelData ownerHotel) {
        this.ownerHotel = ownerHotel;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RoomData = { DayPrice = "+dayPrice+", Calculated Price = " + calculatedPrice + ", Price = " + price + ", Capacity = " + capacity + ", Availability = " + availability + ", Cancelable = " + cancelable + ", WithBreakfast = " + withBreakfast
                + ", Type = '" + type + "' }";
    }

    public JSONObject toJSON() throws Exception {

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("availability", this.availability);
        jsonObj.put("cancelable", this.cancelable);
        jsonObj.put("capacity", this.capacity);
        jsonObj.put("price", this.price);
        jsonObj.put("dayPrice", this.dayPrice);
        jsonObj.put("calculatedPrice", this.calculatedPrice);
        jsonObj.put("type", this.type != null ? this.type : "");
        jsonObj.put("withBreakfast", this.withBreakfast);
        return jsonObj;
    }

    public static TRoomData fromJSON(THotelData ownerHotel, JSONObject jsonObj) throws Exception {

        TRoomData room = new TRoomData(ownerHotel);
        room.availability = jsonObj.getInt("availability");
        room.cancelable = jsonObj.getBoolean("cancelable");
        room.capacity = jsonObj.getInt("capacity");
        room.price = jsonObj.getDouble("price");
        room.dayPrice = jsonObj.getDouble("dayPrice");
        room.calculatedPrice = jsonObj.getDouble("calculatedPrice");
        room.type = jsonObj.getString("type");
        room.withBreakfast = jsonObj.getBoolean("withBreakfast");
        return room;
    }
    
}
