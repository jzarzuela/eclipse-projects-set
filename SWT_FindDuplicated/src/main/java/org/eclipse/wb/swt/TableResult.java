/**
 * 
 */
package org.eclipse.wb.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ComboViewer;

/**
 * @author jzarzuela
 * 
 */
public class TableResult extends Composite {

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public TableResult(Composite parent, int style) {
        super(parent, style);

        Combo combo = new Combo(this, SWT.READ_ONLY);
        combo.setBounds(29, 32, 261, 22);

        TreeViewer treeViewer = new TreeViewer(this, SWT.BORDER);
        Tree tree = treeViewer.getTree();
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);
        tree.setBounds(29, 75, 261, 81);

        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        TreeColumn trclmnNewColumn = treeViewerColumn.getColumn();
        trclmnNewColumn.setWidth(100);
        trclmnNewColumn.setText("New Column");

        ComboViewer comboViewer = new ComboViewer(this, SWT.NONE);
        Combo combo_1 = comboViewer.getCombo();
        combo_1.setBounds(337, 32, 292, 22);

        TreeViewer treeViewer_1 = new TreeViewer(this, SWT.BORDER);
        Tree tree_1 = treeViewer_1.getTree();
        tree_1.setLinesVisible(true);
        tree_1.setHeaderVisible(true);
        tree_1.setBounds(337, 75, 292, 81);

    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
