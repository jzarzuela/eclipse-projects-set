/**
 * 
 */
package com.jzb.booking;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import com.jzb.booking.data.THotelData;
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
    private int _compCalculatedPrice(THotelData hotel1, THotelData hotel2) {

        int value = Double.compare((int) hotel1.avgCalculatedPrice, (int) hotel2.avgCalculatedPrice);
        if (value == 0) {
            value = -Double.compare(hotel1.avgRating, hotel2.avgRating);
            if (value == 0) {
                value = -Double.compare(hotel1.ranking, hotel2.ranking);
                if (value == 0) {
                    value = -Double.compare(hotel1.stars, hotel2.stars);
                }
            }
        }
        return value;
    }

    // ----------------------------------------------------------------------------------------------------
    public int compare(Viewer viewer, Object e1, Object e2) {

        THotelData hotel1 = (THotelData) e1;
        THotelData hotel2 = (THotelData) e2;

        TableViewer tableViewer = (TableViewer) viewer;
        Integer sortIndex = (Integer) tableViewer.getTable().getSortColumn().getData();

        int value;
        switch (sortIndex) {
            case 0:
                value = Double.compare(hotel1.ranking, hotel2.ranking);
                if (value == 0) {
                    value = -_compCalculatedPrice(hotel1, hotel2);
                }
                break;

            case 1:
                value = Double.compare(hotel1.avgDayPrice, hotel2.avgDayPrice);
                if (value == 0) {
                    value = -Double.compare(hotel1.avgRating, hotel2.avgRating);
                    if (value == 0) {
                        value = -Double.compare(hotel1.ranking, hotel2.ranking);
                    }
                }
                break;

            case 2:
                value = Double.compare((int) hotel1.avgCalculatedPrice, (int) hotel2.avgCalculatedPrice);
                if (value == 0) {
                    value = -Double.compare(hotel1.avgRating, hotel2.avgRating);
                    if (value == 0) {
                        value = -Double.compare(hotel1.ranking, hotel2.ranking);
                    }
                }
                break;

            case 3:
                value = Double.compare((int) hotel1.avgPrice, (int) hotel2.avgPrice);
                if (value == 0) {
                    value = -Double.compare(hotel1.avgRating, hotel2.avgRating);
                    if (value == 0) {
                        value = -Double.compare(hotel1.ranking, hotel2.ranking);
                    }
                }
                break;

            case 4:
                value = Double.compare((int) hotel1.ttlPrice, (int) hotel2.ttlPrice);
                if (value == 0) {
                    value = -Double.compare(hotel1.avgRating, hotel2.avgRating);
                    if (value == 0) {
                        value = -Double.compare(hotel1.ranking, hotel2.ranking);
                    }
                }
                break;

            case 5:
                value = hotel1.name.compareTo(hotel2.name);
                if (value == 0) {
                    value = _compCalculatedPrice(hotel1, hotel2);
                }
                break;

            case 6:
                value = Double.compare(hotel1.avgRating, hotel2.avgRating);
                if (value == 0) {
                    value = -_compCalculatedPrice(hotel1, hotel2);
                }
                break;

            case 7:
                value = Double.compare(hotel1.votes, hotel2.votes);
                if (value == 0) {
                    value = -_compCalculatedPrice(hotel1, hotel2);
                }
                break;

            case 8:
                value = Double.compare(hotel1.stars, hotel2.stars);
                if (value == 0) {
                    value = -_compCalculatedPrice(hotel1, hotel2);
                }
                break;

            case 9:
                value = -Double.compare(hotel1.distance, hotel2.distance);
                if (value == 0) {
                    value = -_compCalculatedPrice(hotel1, hotel2);
                }
                break;
                
            case 10:
                value = (hotel1.cancelable == hotel2.cancelable ? 0 : (hotel1.cancelable ? 1 : -1));
                break;
                
            case 11:
                value = (hotel1.withBreakfast == hotel2.withBreakfast ? 0 : (hotel1.withBreakfast ? 1 : -1));
                break;
                
            default:
                Tracer._warn("Column index greater than 10");
                value = 0;
                break;
        }

        return tableViewer.getTable().getSortDirection() == SWT.UP ? value : -value;
    }
}
