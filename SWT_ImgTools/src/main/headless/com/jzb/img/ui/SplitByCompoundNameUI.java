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
import com.jzb.img.tsk.SplitByCompoundName;

/**
 * @author jzarzuela
 * 
 */
public class SplitByCompoundNameUI extends BaseUI {
    
    private Button m_btnStopAtFirst;
    private Button m_btnKeepOrder;

    // --------------------------------------------------------------------------------------------------------
    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public SplitByCompoundNameUI(Composite parent, int style) {

        super(parent, style);

        Button btnSplit = new Button(this, SWT.NONE);
        btnSplit.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeTask();
            }
        });
        btnSplit.setText("Split");
        btnSplit.setBounds(267, 6, 94, 28);
        
        m_btnStopAtFirst = new Button(this, SWT.CHECK);
        m_btnStopAtFirst.setBounds(109, 10, 152, 18);
        m_btnStopAtFirst.setText("Stop At First Subgroup");
        
        m_btnKeepOrder = new Button(this, SWT.CHECK);
        m_btnKeepOrder.setSelection(true);
        m_btnKeepOrder.setBounds(10, 10, 93, 18);
        m_btnKeepOrder.setText("Keep Order");

    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskDescription() {
        String description = "";
        description += "<p>Splits files moving them into subfolders named after the parts of the compound name.</p>";
        description += "<p><b>Stop At First Subgroup:</b> Will stop creating subfolders at the first subgroup part.</p>";
        description += "<p><b>Note 1:</b><i> No recursive processing is done and files without a compound name won't be moved.</i><br/>";
        description += "<b>Note 2:</b><i> Subgroup names will be ordered using a counter followed by <font color='red'><b>'" + BaseTask.SUBGROUP_COUNTER_CHAR1 + "'</b></font>.</i><br/>";
        description += "<b>Note 3:</b><i> It could be necessary to create folders named <font color='red'><b>'" + BaseTask.SUBGROUP_NOTHING + "'</b></font> to keep the order</i></p>";
        return description;
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskName() {
        return "Split by CompoundName";
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executeTask() {
        
        final boolean keepOrder = m_btnKeepOrder.getSelection();
        final boolean stopAtFirstSubgroup = m_btnStopAtFirst.getSelection();
        
        final SplitByCompoundName task = new SplitByCompoundName(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.splitByCompoundName(keepOrder, stopAtFirstSubgroup);
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }
}
