/**
 * 
 */
package com.jzb.img.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.jzb.img.tsk.BaseTask;
import com.jzb.img.tsk.RenameWithFolders;

/**
 * @author jzarzuela
 * 
 */
public class RenameWithFoldersUI extends BaseUI {

    // --------------------------------------------------------------------------------------------------------
    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public RenameWithFoldersUI(Composite parent, int style) {

        super(parent, style);

        Button btnSplit = new Button(this, SWT.NONE);
        btnSplit.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeTask();
            }
        });
        btnSplit.setText("Rename");
        btnSplit.setBounds(10, 10, 94, 28);

    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskDescription() {
        String description = "";
        description += "<p>Renames image files using folder's names as Group and Subgroup Names</p>";
        description += "<p><b><font color='red'>Warning:</font></b><i> Current compound name parts will be replaced with folders' information. Just 'Name' part will remain unchanged.</i></p>";
        description += "<p><b>Note 1:</b><i> Subgroup Names will start (optional) with a folder which name starts with '" + BaseTask.SUBGROUP_MARKER + "'.</i><br/>";
        description += "<b>Note 2:</b><i> Folders can be ordered prefixing their names with a counter followed by <font color='red'><b>'" + BaseTask.SUBGROUP_COUNTER_CHAR1
                + "'</b></font> or <font color='red'><b>'" + BaseTask.SUBGROUP_COUNTER_CHAR2 + "'</b></font>.</i><br/>";
        description += "<b>Note 3:</b><i> Special folder's name <font color='red'><b>'" + BaseTask.SUBGROUP_NOTHING + "'</b></font> can be used</i></p>";

        return description;
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskName() {
        return "Rename with subfolder";
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executeTask() {

        final RenameWithFolders task = new RenameWithFolders(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.renameAsSubfolder();
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }
}
