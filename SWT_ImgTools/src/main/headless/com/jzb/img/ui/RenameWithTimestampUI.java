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
import com.jzb.img.tsk.RenameWithTimestamp;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author jzarzuela
 * 
 */
public class RenameWithTimestampUI extends BaseUI {

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
    public RenameWithTimestampUI(Composite parent, int style) {

        super(parent, style);

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

        Button button = new Button(this, SWT.NONE);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeTaskSetTimeDate();
            }
        });
        button.setText("Set");
        button.setBounds(328, 10, 94, 28);

        Button button_1 = new Button(this, SWT.NONE);
        button_1.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeTaskRemoveTimeDate();
            }
        });
        button_1.setText("Remove");
        button_1.setBounds(328, 38, 94, 28);

    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskDescription() {
        String description = "";
        description += "<p>Adds or removes a timestamp string with the EXIF creation time from the file's names.</p>";
        description += "<p><b>Time offset:</b> Offset to be added to (or subtracted from) the file's creation date when generating the timestamp.</p>";
        description += "<p><b>Note:</b><i> In image files without EXIF information, file's LAST MODIFICATION time will be used (adding '*' to the timestamp)</i></p>";
        return description;
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskName() {
        return "Rename with timestamp";
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executeTaskRemoveTimeDate() {

        final RenameWithTimestamp task = new RenameWithTimestamp(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.removeTimeDate();
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executeTaskSetTimeDate() {

        int years = _parseInt(m_spYears.getText());
        int months = _parseInt(m_spMonths.getText());
        int days = _parseInt(m_spDays.getText());
        int hours = _parseInt(m_spHours.getText());
        int minutes = _parseInt(m_spMinutes.getText());
        int seconds = _parseInt(m_spSeconds.getText());

        final BaseTask.TimeStampShift timeStampShift = new BaseTask.TimeStampShift(years, months, days, hours, minutes, seconds);

        final RenameWithTimestamp task = new RenameWithTimestamp(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.addTimeDate(timeStampShift);
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }

}
