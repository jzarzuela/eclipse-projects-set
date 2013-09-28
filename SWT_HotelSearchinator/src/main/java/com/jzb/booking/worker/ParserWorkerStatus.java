/**
 * 
 */
package com.jzb.booking.worker;

import java.util.ArrayList;

import com.jzb.booking.data.THotelData;

/**
 * @author jzarzuela
 * 
 */
public class ParserWorkerStatus {

    public static enum PWStatusType {
        ParseNextPage, PageEnded, AllPagesEnded;
    }

    public PWStatusType          type;
    public ArrayList<THotelData> hotelDataList;
    public String                nextPageUrl;
}
