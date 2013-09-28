/**
 * 
 */
package com.jzb.fdf.swt;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

import com.jzb.fdf.srvc.SFile;

/**
 * @author jzarzuela
 * 
 */
public class FileTableLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

    private Image       m_folderDup;
    private Image       m_folderOK;
    private TableViewer m_tableViewer;

    public FileTableLabelProvider(TableViewer tableViewer) {
        m_folderOK = SWTResourceManager.getImage(FolderTreeLabelProvider.class, "/folder-clean24.png");
        m_folderDup = SWTResourceManager.getImage(FolderTreeLabelProvider.class, "/folder-dup24.png");
        m_tableViewer = tableViewer;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {

        SFile sfile = (SFile) element;
        String folderName = (String) m_tableViewer.getData("#folderName#");

        if (columnIndex == 0) {
            return sfile.getFolderName().equals(folderName) ? m_folderOK : m_folderDup;
        } else {
            return null;
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {

        if (!(element instanceof SFile))
            return "";

        SFile sfile = (SFile) element;
        switch (columnIndex) {
            case 0:
                return sfile.getName() + " [" + sfile.getId() + "]";
            case 1:
                return sfile.getFolderName();
            case 2:
                return sfile.getHashing();
            default:
                return "????";
        }
    }

}
