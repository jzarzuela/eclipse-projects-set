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
import com.jzb.img.tsk.SplitByDate;
import com.jzb.img.tsk.SplitByDate.GroupByCloseness;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Label;

/**
 * @author jzarzuela
 * 
 */
public class SplitByDateUI extends BaseUI {

    private Button  m_chkGroup;
    private Spinner m_spDays;
    private Spinner m_spHours;
    private Spinner m_spMinutes;
    private Spinner m_spMonths;
    private Spinner m_spSeconds;
    private Spinner m_spYears;

    // --------------------------------------------------------------------------------------------------------
    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public SplitByDateUI(Composite parent, int style) {

        super(parent, style);

        Button btnSplit = new Button(this, SWT.NONE);
        btnSplit.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeTaskSplit();
            }
        });
        btnSplit.setText("Split by date");
        btnSplit.setBounds(340, 36, 100, 28);

        m_chkGroup = new Button(this, SWT.CHECK);
        m_chkGroup.setSelection(true);
        m_chkGroup.setBounds(340, 12, 134, 18);
        m_chkGroup.setText("Group by closeness");

        Label label = new Label(this, SWT.RIGHT);
        label.setText("Time offset:\n(could be negative)");
        label.setBounds(10, 21, 109, 35);

        m_spYears = new Spinner(this, SWT.BORDER);
        m_spYears.setMaximum(99);
        m_spYears.setMinimum(-99);
        m_spYears.setBounds(123, 13, 50, 22);

        Label label_1 = new Label(this, SWT.NONE);
        label_1.setText("Y");
        label_1.setBounds(173, 17, 20, 14);

        m_spMonths = new Spinner(this, SWT.BORDER);
        m_spMonths.setMaximum(12);
        m_spMonths.setMinimum(-12);
        m_spMonths.setBounds(189, 13, 50, 22);

        Label label_2 = new Label(this, SWT.NONE);
        label_2.setText("M");
        label_2.setBounds(239, 17, 20, 14);

        m_spDays = new Spinner(this, SWT.BORDER);
        m_spDays.setMaximum(31);
        m_spDays.setMinimum(-31);
        m_spDays.setBounds(254, 13, 50, 22);

        Label label_3 = new Label(this, SWT.NONE);
        label_3.setText("D");
        label_3.setBounds(304, 17, 20, 14);

        m_spHours = new Spinner(this, SWT.BORDER);
        m_spHours.setMaximum(23);
        m_spHours.setMinimum(-23);
        m_spHours.setBounds(123, 41, 50, 22);

        Label label_4 = new Label(this, SWT.NONE);
        label_4.setText("H");
        label_4.setBounds(173, 45, 20, 14);

        m_spMinutes = new Spinner(this, SWT.BORDER);
        m_spMinutes.setMaximum(59);
        m_spMinutes.setMinimum(-59);
        m_spMinutes.setBounds(189, 41, 50, 22);

        Label label_5 = new Label(this, SWT.NONE);
        label_5.setText("M");
        label_5.setBounds(239, 45, 20, 14);

        m_spSeconds = new Spinner(this, SWT.BORDER);
        m_spSeconds.setMaximum(59);
        m_spSeconds.setMinimum(-59);
        m_spSeconds.setBounds(254, 41, 50, 22);

        Label label_6 = new Label(this, SWT.NONE);
        label_6.setText("S");
        label_6.setBounds(304, 45, 20, 14);

    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskDescription() {
        String description = "";
        description += "<p><b>Split by date:</b> Splits files moving them into subfolders named after the EXIF creation time.</p>";
        description += "<p><b>Group by closeness:</b> Group files by 'date closeness' using the EXIF creation time.</p>";
        description += "<p><b>Time offset:</b> Offset to be added to (or subtracted from) the file's creation date when generating the timestamp.</p>";
        description += "<p><b>Note:</b><i> In image files without EXIF information, file's LAST MODIFICATION time will be used (adding '*' to the name)</i></p>";
        return description;
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskName() {
        return "Split by date";
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executeTaskSplit() {

        int years = _parseInt(m_spYears.getText());
        int months = _parseInt(m_spMonths.getText());
        int days = _parseInt(m_spDays.getText());
        int hours = _parseInt(m_spHours.getText());
        int minutes = _parseInt(m_spMinutes.getText());
        int seconds = _parseInt(m_spSeconds.getText());

        final BaseTask.TimeStampShift timeStampShift = new BaseTask.TimeStampShift(years, months, days, hours, minutes, seconds);

        final GroupByCloseness group = m_chkGroup.getSelection() ? GroupByCloseness.YES : GroupByCloseness.NO;

        final SplitByDate task = new SplitByDate(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.splitByDate(group, timeStampShift);
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }
}
