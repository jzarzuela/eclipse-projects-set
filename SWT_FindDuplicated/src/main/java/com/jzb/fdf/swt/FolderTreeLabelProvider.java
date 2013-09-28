/**
 * 
 */
package com.jzb.fdf.swt;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

import com.jzb.fdf.srvc.SFolder;

/**
 * @author jzarzuela
 * 
 */
public class FolderTreeLabelProvider implements ILabelProvider {

    private Image m_folderDup;
    private Image m_folderOK;

    public FolderTreeLabelProvider() {
        m_folderOK = SWTResourceManager.getImage(FolderTreeLabelProvider.class, "/folder-clean24.png");
        m_folderDup = SWTResourceManager.getImage(FolderTreeLabelProvider.class, "/folder-dup24.png");
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public void addListener(ILabelProviderListener listener) {
        // System.out.println("-- MT --> LabelProvider.addListener(listener)");
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public void dispose() {
        // System.out.println("-- MT --> LabelProvider.dispose()");
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public Image getImage(Object element) {
        // System.out.println("-- MT --> LabelProvider.getImage(element)");
        if (element instanceof SFolder) {
            if (((SFolder) element).hasDuplicatedFiles()) {
                return m_folderDup;
            } else {
                return m_folderOK;
            }
        }
        return null;
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public String getText(Object element) {
        if (element instanceof SFolder) {
            SFolder sfolder = (SFolder) element;
            return sfolder.getName() + " [" + sfolder.getId() + "]";
        } else {
            return null;
        }
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public boolean isLabelProperty(Object element, String property) {
        // System.out.println("-- MT --> LabelProvider.isLabelProperty(element, property)");
        return false;
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public void removeListener(ILabelProviderListener listener) {
        // System.out.println("-- MT --> LabelProvider.removeListener(listener)");
    }

}
