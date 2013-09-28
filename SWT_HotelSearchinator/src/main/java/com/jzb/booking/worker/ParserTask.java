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
public class ParserTask {

    public String                jobID;
    public String                baseURL;
    public String                htmlText;
    public ArrayList<THotelData> hotelDataList;

}
