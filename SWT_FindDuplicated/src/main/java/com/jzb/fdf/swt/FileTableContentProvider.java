/**
 * 
 */
package com.jzb.fdf.swt;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author jzarzuela
 * 
 */
public class FileTableContentProvider implements IStructuredContentProvider {

    // ----------------------------------------------------------------------------------------------------
    @Override
    public void dispose() {
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof Collection) {
            return ((Collection) inputElement).toArray();
        } else {
            return new Object[0];
        }
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
}
