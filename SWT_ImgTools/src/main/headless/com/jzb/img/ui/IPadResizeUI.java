/**
 * 
 */
package com.jzb.img.ui;

import java.io.File;
import java.util.Map.Entry;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.jzb.img.tsk.IPadResize;
import com.jzb.util.AppPreferences;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author jzarzuela
 * 
 */
public class IPadResizeUI extends BaseUI {

    private Button m_btnCleanName;
    private Text   m_txtSkipScan;
    private Text   m_txtSkipCreate;
    private Text   m_txtDestFolder;

    // --------------------------------------------------------------------------------------------------------
    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public IPadResizeUI(Composite parent, int style) {

        super(parent, style);

        Label lblDestFolder = new Label(this, SWT.NONE);
        lblDestFolder.setBounds(10, 10, 79, 14);
        lblDestFolder.setText("Dest Folder:");

        m_txtDestFolder = new Text(this, SWT.BORDER);
        m_txtDestFolder.setBounds(95, 8, 511, 19);
        m_txtDestFolder.addDisposeListener(new DisposeListener() {

            @SuppressWarnings("synthetic-access")
            @Override
            public void widgetDisposed(final DisposeEvent e) {
                getPrefs().setPref("ipadDestFolder", m_txtDestFolder.getText());
            }
        });

        final Button btnFolder = new Button(this, SWT.NONE);
        btnFolder.setBounds(612, 5, 43, 26);
        btnFolder.addSelectionListener(new SelectionAdapter() {

            @SuppressWarnings("synthetic-access")
            @Override
            public void widgetSelected(final SelectionEvent e) {
                DirectoryDialog dd = new DirectoryDialog(getParent().getDisplay().getActiveShell());
                
                for(Entry entry:System.getProperties().entrySet()) {
                    System.out.println("'"+entry.getKey()+"' - '"+entry.getValue()+"'");
                }
                
                File folder = new File(m_txtDestFolder.getText());
                while(folder!=null) {
                    if(folder.exists()) break;
                    folder = folder.getParentFile();
                }
                if(folder==null) {
                    folder = new File(System.getProperty("user.home")+File.separator+"Documents");
                    if(!folder.exists()) {
                        folder=folder.getParentFile();
                    }
                }
                dd.setFilterPath(folder.getAbsolutePath());

                String newValue = dd.open();
                if (newValue != null)
                    m_txtDestFolder.setText(newValue);
            }
        });
        btnFolder.setText("...");

        m_txtSkipScan = new Text(this, SWT.BORDER);
        m_txtSkipScan.setText("Filtradas_NO");
        m_txtSkipScan.setBounds(95, 33, 204, 19);
        
                Label label = new Label(this, SWT.NONE);
                label.setText("Skip scan:");
                label.setBounds(10, 35, 79, 14);

        m_txtSkipCreate = new Text(this, SWT.BORDER);
        m_txtSkipCreate.setText("Organizadas");
        m_txtSkipCreate.setBounds(404, 33, 204, 19);
        
                Label label2 = new Label(this, SWT.NONE);
                label2.setText("Skip create:");
                label2.setBounds(319, 35, 79, 14);

        m_btnCleanName = new Button(this, SWT.NONE);
        m_btnCleanName.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeResizeTask();
            }
        });
        m_btnCleanName.setBounds(90, 54, 94, 28);
        m_btnCleanName.setText("Resize");

        Button button = new Button(this, SWT.NONE);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeCheckEXIFTask();
            }
        });
        button.setText("Check EXIF");
        button.setBounds(185, 54, 94, 28);

    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public void setPrefs(AppPreferences prefs) {
        super.setPrefs(prefs);
        m_txtDestFolder.setText(getPrefs().getPref("ipadDestFolder", ""));
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskDescription() {
        String description = "";
        description += "<p>Resize files to a convenient iPad resolution and with a much smaller size.</p>";
        description += "<b>Dest Folder:</b> Where the resulting files will be place.<br/>";
        description += "<b>Skip scan:</b> Origin folder's name which content should be skipped.<br/>";
        description += "<b>Skip create:</b> Destination folder's name which creation should be skipped.";
        return description;
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskName() {
        return "iPad Resize";
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executeResizeTask() {

        final String skipScan = m_txtSkipScan.getText();
        final String skipCreate = m_txtSkipCreate.getText();
        final File destFolder = new File(m_txtDestFolder.getText());

        final IPadResize task = new IPadResize(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.resize(destFolder, skipScan, skipCreate);
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executeCheckEXIFTask() {

        final String skip = m_txtSkipScan.getText();

        final IPadResize task = new IPadResize(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.checkExif(skip);
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
        
    }
}
