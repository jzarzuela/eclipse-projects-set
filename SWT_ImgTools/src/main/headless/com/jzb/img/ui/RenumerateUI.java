/**
 * 
 */
package com.jzb.img.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.jzb.img.tsk.Renumerate;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author jzarzuela
 * 
 */
public class RenumerateUI extends BaseUI {
    private Text   m_txtCounter;

    // --------------------------------------------------------------------------------------------------------
    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public RenumerateUI(Composite parent, int style) {

        super(parent, style);

        Label lblCounter = new Label(this, SWT.NONE);
        lblCounter.setBounds(10, 19, 90, 14);
        lblCounter.setText("Initial index:");

        m_txtCounter = new Text(this, SWT.BORDER);
        m_txtCounter.setText("10");
        m_txtCounter.setBounds(100, 17, 60, 19);

        Button btnSplit = new Button(this, SWT.NONE);
        btnSplit.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeRenumerate();
            }
        });
        btnSplit.setText("Renumerate");
        btnSplit.setBounds(166, 12, 94, 28);
        
        Button btnReset = new Button(this, SWT.NONE);
        btnReset.addSelectionListener(new SelectionAdapter() {
            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeResetIndex();
            }
        });
        btnReset.setText("Set to Zero");
        btnReset.setBounds(289, 12, 94, 28);

    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskDescription() {
        String description = "";
        description += "<p><b>Renumerate:</b> Assigns a sequential index number to each file following their alphabetical order. Files are given a Compound File Name.<br/>";
        description += "<b>Set to zero:</b> Assigns a zero index number to each file. Files are given a Compound File Name.</p>";
        description += "<table><tr><td><b>Initial Counter:</b></td><td>Establishes the initial index to be given.</td></tr>";
        description += "</table>";
        return description;
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskName() {
        return "Renumerate";
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executeRenumerate() {

        final int counter = _parseInt(m_txtCounter.getText());

        final Renumerate task = new Renumerate(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.renumerate(counter);
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }
    
    // --------------------------------------------------------------------------------------------------------
    private void _executeResetIndex() {

        final Renumerate task = new Renumerate(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.setIndexToZero();
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }
}
