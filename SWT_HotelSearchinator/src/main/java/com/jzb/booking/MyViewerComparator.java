/**
 * 
 */
package com.jzb.booking;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import com.jzb.booking.data.TRoomData;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class MyViewerComparator extends ViewerComparator {

    // ----------------------------------------------------------------------------------------------------
    public MyViewerComparator() {
    }

    // ----------------------------------------------------------------------------------------------------
    private int _compCalculatedPrice(TRoomData room1, TRoomData room2) {

        int value = Double.compare((int) room1.calculatedPrice, (int) room2.calculatedPrice);
        if (value == 0) {
            value = -Double.compare(room1.ownerHotel.avgRating, room2.ownerHotel.avgRating);
            if (value == 0) {
                value = -Double.compare(room1.ranking, room2.ranking);
                if (value == 0) {
                    value = -Double.compare(room1.ownerHotel.stars, room2.ownerHotel.stars);
                }
            }
        }
        return value;
    }

    // ----------------------------------------------------------------------------------------------------
    public int compare(Viewer viewer, Object e1, Object e2) {

        TRoomData room1 = (TRoomData) e1;
        TRoomData room2 = (TRoomData) e2;

        TableViewer tableViewer = (TableViewer) viewer;
        Integer sortIndex = (Integer) tableViewer.getTable().getSortColumn().getData();

        int value;
        switch (sortIndex) {
            case 0:
                value = Double.compare(room1.ranking, room2.ranking);
                if (value == 0) {
                    value = -_compCalculatedPrice(room1, room2);
                }
                break;

            case 1:
                int dayPrice1 = (int) (room1.calculatedPrice / (double) room1.ownerHotel.numDays);
                int dayPrice2 = (int) (room2.calculatedPrice / (double) room2.ownerHotel.numDays);
                value = Double.compare(dayPrice1, dayPrice2);
                if (value == 0) {
                    value = -Double.compare(room1.ownerHotel.avgRating, room2.ownerHotel.avgRating);
                    if (value == 0) {
                        value = -Double.compare(room1.ranking, room2.ranking);
                    }
                }
                break;

            case 2:
                value = _compCalculatedPrice(room1, room2);
                break;

            case 3:
                value = Double.compare((int) room1.price, (int) room2.price);
                if (value == 0) {
                    value = -Double.compare(room1.ownerHotel.avgRating, room2.ownerHotel.avgRating);
                    if (value == 0) {
                        value = -Double.compare(room1.ranking, room2.ranking);
                    }
                }
                break;

            case 4:
                value = room1.ownerHotel.name.compareTo(room2.ownerHotel.name);
                if (value == 0) {
                    value = _compCalculatedPrice(room1, room2);
                }
                break;

            case 5:
                value = Double.compare(room1.ownerHotel.avgRating, room2.ownerHotel.avgRating);
                if (value == 0) {
                    value = -_compCalculatedPrice(room1, room2);
                }
                break;
            case 6:
                value = Double.compare(room1.ownerHotel.votes, room2.ownerHotel.votes);
                if (value == 0) {
                    value = -_compCalculatedPrice(room1, room2);
                }
                break;
            case 7:
                value = Double.compare(room1.ownerHotel.stars, room2.ownerHotel.stars);
                if (value == 0) {
                    value = -_compCalculatedPrice(room1, room2);
                }
                break;
            case 8:
                value = room1.ownerHotel.address.compareTo(room2.ownerHotel.address);
                if (value == 0) {
                    value = -_compCalculatedPrice(room1, room2);
                }
                break;
            case 9:
                value = -Double.compare(room1.ownerHotel.distance, room2.ownerHotel.distance);
                if (value == 0) {
                    value = -_compCalculatedPrice(room1, room2);
                }
                break;
            case 10:
                value = (room1.cancelable == room2.cancelable ? 0 : (room1.cancelable ? 1 : -1));
                break;
            case 11:
                value = (room1.withBreakfast == room2.withBreakfast ? 0 : (room1.withBreakfast ? 1 : -1));
                break;
            case 12:
                value = room1.type.compareTo(room2.type);
                break;
            case 13:
                value = Double.compare(room1.availability, room2.availability);
                if (value == 0) {
                    value = -_compCalculatedPrice(room1, room2);
                }
                break;
            default:
                Tracer._warn("Column index greater than 10");
                value = 0;
                break;
        }

        return tableViewer.getTable().getSortDirection() == SWT.UP ? value : -value;
    }
}
