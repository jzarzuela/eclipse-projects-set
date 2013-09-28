/**
 * 
 */
package com.jzb.fdf.swt;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import com.jzb.fdf.srvc.SFile;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class FileTableComparator extends ViewerComparator {

    // ----------------------------------------------------------------------------------------------------
    public FileTableComparator() {
    }

    // ----------------------------------------------------------------------------------------------------
    public int compare(Viewer viewer, Object e1, Object e2) {

        TableViewer tableViewer = (TableViewer) viewer;
        Integer sortIndex = (Integer) tableViewer.getTable().getSortColumn().getData();

        SFile f1 = (SFile) e1;
        SFile f2 = (SFile) e2;

        int value;
        switch (sortIndex) {
            case 0:
                value = f1.getHashing().compareTo(f2.getHashing());
                if (value != 0) {
                    value = f1.getName().compareTo(f2.getName());
                } else {
                    String folderName = (String) tableViewer.getData("#folderName#");
                    if (f1.getFolderName().equals(folderName)) {
                        value = -1;
                    } else if (f2.getFolderName().equals(folderName)) {
                        value = 1;
                    } else {
                        value = f1.getName().compareTo(f2.getName());
                    }
                }
                break;

            case 1:
                value = f1.getFolderName().compareTo(f2.getFolderName());
                break;

            case 2:
                value = f1.getHashing().compareTo(f2.getHashing());
                break;

            default:
                Tracer._warn("Column index greater than 1");
                value = 0;
                break;
        }

        return tableViewer.getTable().getSortDirection() == SWT.DOWN ? value : -value;
    }
}
