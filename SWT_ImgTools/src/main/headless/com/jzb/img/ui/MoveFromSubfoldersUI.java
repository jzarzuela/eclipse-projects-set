/**
 * 
 */
package com.jzb.img.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.jzb.img.tsk.MoveFromSubfolders;
import com.jzb.img.tsk.MoveFromSubfolders.DeleteEmpty;

/**
 * @author jzarzuela
 * 
 */
public class MoveFromSubfoldersUI extends BaseUI {

    private Button m_chkDeleteEmpty;

    // --------------------------------------------------------------------------------------------------------
    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public MoveFromSubfoldersUI(Composite parent, int style) {

        super(parent, style);

        Button btnSplit = new Button(this, SWT.NONE);
        btnSplit.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeTask();
            }
        });
        btnSplit.setText("Move");
        btnSplit.setBounds(118, 5, 94, 28);

        m_chkDeleteEmpty = new Button(this, SWT.CHECK);
        m_chkDeleteEmpty.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        m_chkDeleteEmpty.setSelection(true);
        m_chkDeleteEmpty.setBounds(10, 10, 102, 18);
        m_chkDeleteEmpty.setText("Delete Empty");

    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskDescription() {
        String description = "";
        description += "<p>Moves files from within subfolders to the current working folder</p>";
        description += "<p>Delete Empty: Will make all subfolders that don't have any file inside to be deleted.</p>";
        description += "<p><b>Note:</b><i> Recursive processing will move all files from within any subfolder depth.</i></p>";
        return description;
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskName() {
        return "Move from subfolders";
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executeTask() {

        final MoveFromSubfolders task = new MoveFromSubfolders(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        final DeleteEmpty deleteEmpty = m_chkDeleteEmpty.getSelection() ? DeleteEmpty.YES : DeleteEmpty.NO;

        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.moveFromSubfolder(deleteEmpty);
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }

}
