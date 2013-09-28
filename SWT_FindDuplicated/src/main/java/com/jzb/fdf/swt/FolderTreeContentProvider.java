/**
 * 
 */
package com.jzb.fdf.swt;

import java.util.List;

import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.jzb.fdf.srvc.IFolderSrvc;
import com.jzb.fdf.srvc.SFolder;

/**
 * @author jzarzuela
 * 
 */
public class FolderTreeContentProvider implements ILazyTreeContentProvider {

    private TreeViewer m_treeViewer;

    public FolderTreeContentProvider(TreeViewer viewer) {
        m_treeViewer = viewer;
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public void dispose() {
        // System.out.println("-- MT --> ContentProvider.dispose()");
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public Object getParent(Object element) {
        // System.out.println("-- MT --> ContentProvider.getParent(element)");
        return null;
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        //System.out.println("-- MT --> ContentProvider.inputChanged(viewer, oldInput, newInput)");
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public void updateChildCount(Object element, int currentChildCount) {
        // System.out.println("-- MT --> ContentProvider.updateChildCount(element, currentChildCount)");
        if (element instanceof List) {
            List<SFolder> list = (List<SFolder>) element;
            m_treeViewer.setChildCount(element, list.size());
        } else if (element instanceof SFolder) {
            SFolder folder = (SFolder) element;
            m_treeViewer.setChildCount(element, folder.getSubfoldersCount());
        }
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public void updateElement(Object parent, int index) {

        // System.out.println("-- MT --> ContentProvider.updateElement(parent, index)");
        if (parent instanceof List) {
            List<SFolder> list = (List<SFolder>) parent;
            SFolder element = list.get(index);
            m_treeViewer.replace(parent, index, element);
            m_treeViewer.setHasChildren(element, element.getSubfoldersCount() > 0);
        } else if (parent instanceof SFolder) {

            SFolder folder = (SFolder) parent;
            List<SFolder> subfolders = folder.getSubFolders();
            if (subfolders == null) {
                subfolders = IFolderSrvc.inst.getSubfoldersForID(folder.getId(), true);
                folder.setSubFolders(subfolders);
            }
            SFolder element = subfolders.get(index);
            m_treeViewer.replace(parent, index, element);
            m_treeViewer.setHasChildren(element, element.getSubfoldersCount() > 0);
        }
    }

}
