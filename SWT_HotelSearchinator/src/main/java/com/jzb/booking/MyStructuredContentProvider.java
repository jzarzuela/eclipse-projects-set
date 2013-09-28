/**
 * 
 */
package com.jzb.booking;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.jzb.booking.data.TRoomData;

/**
 * @author jzarzuela
 * 
 */
public class MyStructuredContentProvider implements IStructuredContentProvider {

    // ----------------------------------------------------------------------------------------------------
    @Override
    public void dispose() {
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public Object[] getElements(Object inputElement) {
        ArrayList<TRoomData> hotelRoomsDataList = (ArrayList<TRoomData>) inputElement;
        return hotelRoomsDataList.toArray();
    }
}
