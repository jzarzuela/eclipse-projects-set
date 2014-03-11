/**
 * 
 */
package com.jzb.booking;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.jzb.booking.data.THotelData;

/**
 * @author jzarzuela
 * 
 */
public class MyTableLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

    // ----------------------------------------------------------------------------------------------------
    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        THotelData hotel = (THotelData) element;
        switch (columnIndex) {
            case 0:
                return "" + hotel.ranking;
            case 1:
                return "" + (int) hotel.avgDayPrice;
            case 2:
                return "" + (int) hotel.avgCalculatedPrice;
            case 3:
                return "" + (int) hotel.avgPrice;
            case 4:
                return "" + (int) hotel.ttlPrice;
            case 5:
                return hotel.name;
            case 6:
                return "" + hotel.avgRating;
            case 7:
                return "" + hotel.votes;
            case 8:
                return "" + hotel.stars;
            case 9:
                return "" + (int)hotel.distance;
            case 10:
                return hotel.cancelable ? "Sí" : "";
            case 11:
                return hotel.withBreakfast ? "Sí" : "";
            default:
                return "????";
        }
    }

}
