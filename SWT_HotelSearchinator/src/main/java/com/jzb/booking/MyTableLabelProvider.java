/**
 * 
 */
package com.jzb.booking;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.jzb.booking.data.TRoomData;

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
        TRoomData room = (TRoomData) element;
        switch (columnIndex) {
            case 0:
                return "" + room.ranking;
            case 1:
                return "" + (int) (room.calculatedPrice / (double) room.ownerHotel.numDays);
            case 2:
                return "" + (int) room.calculatedPrice;
            case 3:
                return "" + (int) room.price;
            case 4:
                return room.ownerHotel.name;
            case 5:
                return "" + room.ownerHotel.avgRating;
            case 6:
                return "" + room.ownerHotel.votes;
            case 7:
                return "" + room.ownerHotel.stars;
            case 8:
                return "" + room.ownerHotel.address;
            case 9:
                return "" + room.ownerHotel.distance;
            case 10:
                return room.cancelable ? "Sí" : "";
            case 11:
                return room.withBreakfast ? "Sí" : "";
            case 12:
                return room.type;
            case 13:
                return "" + room.availability;
            default:
                return "????";
        }
    }

}
