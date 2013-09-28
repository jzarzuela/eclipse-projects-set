/**
 * 
 */
package com.jzb.img.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.jzb.img.tsk.CleanName;
import com.jzb.img.tsk.CleanName.ForceClean;

/**
 * @author jzarzuela
 * 
 */
public class CleanNameUI extends BaseUI {

    private Button m_btnCleanName;
    private Button m_chkForce;

    // --------------------------------------------------------------------------------------------------------
    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public CleanNameUI(Composite parent, int style) {

        super(parent, style);

        m_btnCleanName = new Button(this, SWT.NONE);
        m_btnCleanName.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeTask();
            }
        });
        m_btnCleanName.setBounds(79, 6, 94, 28);
        m_btnCleanName.setText("Clean Name");

        m_chkForce = new Button(this, SWT.CHECK);
        m_chkForce.setBounds(10, 11, 63, 18);
        m_chkForce.setText("Force");

    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskDescription() {
        String description = "";
        description += "<p>Renames files keeping just the \"Image File Name\" part (the original name) from the compound file's names.</p>";
        description += "<b>Force:</b> Tries to clean the name even though it wasn't detected as a proper compound name. It will use anything found between brakets as the original name.";
        return description;
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskName() {
        return "Clean Names";
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executeTask() {

        final ForceClean force = m_chkForce.getSelection() ? ForceClean.YES : ForceClean.NO;
        final CleanName task = new CleanName(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.cleanName(force);
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }
}
