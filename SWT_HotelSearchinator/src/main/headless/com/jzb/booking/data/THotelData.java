/**
 * 
 */
package com.jzb.booking.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author jzarzuela
 * 
 */
public class THotelData {

    public double               avgRating;
    public String               address;
    public double               distance;
    public String               name;
    public ArrayList<TRoomData> rooms;
    public int                  stars;
    public int                  votes;
    public String               dataLink;
    public int                  numDays;

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String cad = "HotelData = { Name = '" + name + "', Stars = " + stars + ", Rating = " + avgRating + "/" + votes + ", Address = " + address + ", Distance = " + distance;
        if (rooms != null)
            for (TRoomData room : rooms) {
                cad += "\n  " + room.toString();
            }
        cad += "\n}";
        return cad;
    }

    public JSONObject toJSON() throws Exception {

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("avgRating", this.avgRating);
        jsonObj.put("distance", this.distance);
        jsonObj.put("address", this.address);
        jsonObj.put("name", this.name != null ? this.name : "");
        jsonObj.put("stars", this.stars);
        jsonObj.put("votes", this.votes);
        jsonObj.put("dataLink", this.dataLink != null ? this.dataLink : "");
        jsonObj.put("numDays", this.numDays);
        JSONArray hRooms = new JSONArray();
        jsonObj.put("rooms", hRooms);
        if (this.rooms != null) {
            for (TRoomData room : this.rooms) {
                hRooms.put(room.toJSON());
            }
        }
        return jsonObj;
    }

    public static THotelData fromJSON(JSONObject jsonObj) throws Exception {

        THotelData hotel = new THotelData();
        hotel.avgRating = jsonObj.getDouble("avgRating");
        hotel.distance = jsonObj.getDouble("distance");
        hotel.address = jsonObj.getString("address");
        hotel.name = jsonObj.getString("name");
        hotel.stars = jsonObj.getInt("stars");
        hotel.votes = jsonObj.getInt("votes");
        hotel.dataLink = jsonObj.getString("dataLink");
        hotel.numDays = jsonObj.getInt("numDays");
        JSONArray hRooms = jsonObj.getJSONArray("rooms");
        if (hRooms.length() > 0) {
            hotel.rooms = new ArrayList<TRoomData>();
            for (int n = 0; n < hRooms.length(); n++) {
                TRoomData room = TRoomData.fromJSON(hotel, (JSONObject) hRooms.get(n));
                hotel.rooms.add(room);
            }
        }

        return hotel;
    }

    public static ArrayList<THotelData> fromJSONArrayStr(String jsonHotelsStr) throws Exception {
        ArrayList<THotelData> hotels = null;
        JSONArray jsonHotels = new JSONArray(jsonHotelsStr);
        if (jsonHotels.length() > 0) {
            hotels = new ArrayList<THotelData>();
            for (int n = 0; n < jsonHotels.length(); n++) {
                THotelData hotel = THotelData.fromJSON((JSONObject) jsonHotels.get(n));
                hotels.add(hotel);
            }
        }
        return hotels;
    }

    public static String toJSONArrayStr(ArrayList<THotelData> hotels) throws Exception {
        JSONArray jsonHotels = new JSONArray();
        for (THotelData hotel : hotels) {
            jsonHotels.put(hotel.toJSON());
        }
        return jsonHotels.toString(2);
    }

    public static void saveHotelsData(String fileName, ArrayList<THotelData> hotels) throws Exception {

        String jsonData = THotelData.toJSONArrayStr(hotels).toString();
        FileWriter fw = new FileWriter(fileName);
        fw.write(jsonData);
        fw.close();
    }

    public static ArrayList<THotelData> loadHotelsData(String fileName) throws Exception {

        File fin = new File(fileName);
        FileReader fr = new FileReader(fin);
        char buffer[] = new char[(int) fin.length()];
        fr.read(buffer);
        String jsonData = new String(buffer);
        fr.close();

        ArrayList<THotelData> hotels = THotelData.fromJSONArrayStr(jsonData);
        return hotels;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof THotelData)) {
            return false;
        } else {
            THotelData hotel2 = (THotelData) obj;
            if (name != null) {
                return name.equals(hotel2.name);
            } else {
                return false;
            }
        }
    }
}
