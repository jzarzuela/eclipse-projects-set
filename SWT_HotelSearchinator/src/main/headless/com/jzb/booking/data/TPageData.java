/**
 * 
 */
package com.jzb.booking.data;

import java.util.ArrayList;

/**
 * @author jzarzuela
 * 
 */
public class TPageData {

    public ArrayList<THotelData> hotels;
    public String                nextPageUrl;
    public int                   numDays;
    public double                lat, lng;
}
