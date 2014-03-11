/**
 * 
 */
package com.jzb.booking.data;

/**
 * @author jzarzuela
 * 
 */
public class RoomDownload {

    /**
     * 
     */
    public RoomDownload() {
        // TODO Auto-generated constructor stub
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        long t1 = 0, t2 = 0;
        try {
            System.out.println("\n***** EXECUTION STARTED *****\n");
            RoomDownload me = new RoomDownload();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            t2 = System.currentTimeMillis();
            System.out.println("***** EXECUTION FAILED [" + (t2 - t1) + "]*****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        String roomUrl = "http://www.booking.com/hotel/us/hyatt-regency-jersey-city.es.html?sid=8f9bd61f940061cc80055ff7ea7e4668;dcid=1;checkin=2014-04-26;checkout=2014-05-03;group_adults=2;group_children=2;age=12;age=10;group_rooms=18144602_83222868_0_42_4_0_0;srfid=08af1fd15029966454f046ec1783b9d618678abeX190";

        THotelData hotel = new THotelData();
        hotel.dataLink = roomUrl;
        hotel.name = "nose";

        HotelDataParser.parse(hotel,3, 0.0, 0.0);
        System.out.println(hotel.toString());

    }
}
