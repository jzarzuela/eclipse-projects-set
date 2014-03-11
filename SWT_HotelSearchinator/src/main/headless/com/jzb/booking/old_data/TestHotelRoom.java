/**
 * 
 */
package com.jzb.booking.old_data;

import java.util.ArrayList;


/**
 * @author jzarzuela
 * 
 */
public class TestHotelRoom {

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("\n***** EXECUTION STARTED *****\n");
            TestHotelRoom me = new TestHotelRoom();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("\n***** EXECUTION FAILED *****\n");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        THotelData hotel = new THotelData();
        TRoomData room = new TRoomData(hotel);
        hotel.rooms = new ArrayList<TRoomData>();
        hotel.rooms.add(room);
        ArrayList<THotelData> hotels = new ArrayList<THotelData>();
        hotels.add(hotel);

        String json = THotelData.toJSONArrayStr(hotels);
        System.out.println(json);

        ArrayList<THotelData> hotels2 = THotelData.fromJSONArrayStr(json);
        System.out.println(hotels2);

        String fileName = TestHotelRoom.getTestDataFile();
        THotelData.saveHotelsData(fileName, hotels);
        ArrayList<THotelData> hotels3 = THotelData.loadHotelsData(fileName);
        System.out.println(hotels3);
    }

    public static String getTestDataFile() {
        String fname = "/Users/jzarzuela/Documents/WKSP/Consolidado/SWT_Booking/src_ParsingCode/com/jzb/booking/data/testData.json";
        return fname;
    }
}
