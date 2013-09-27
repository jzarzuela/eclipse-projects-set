/**
 * 
 */
package com.jzb.ipa.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.BorderLayout;

import com.jzb.swt.util.IProgressMonitor;
import com.jzb.swt.util.TabbedTracerImpl;
import com.jzb.util.AppPreferences;
import com.jzb.util.Tracer;
import com.swtdesigner.SWTResourceManager;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("synthetic-access")
public class IPAToolAppWnd {

    private class ProgressMonitor implements IProgressMonitor {

        /**
         * @see com.jzb.swt.util2.tools.IProgressMonitor#processingEnded(boolean)
         */
        public void processingEnded(boolean failed, Object result) {

            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    _executionEnded();
                }
            });
        }

    }

    private static final String   APP_NAME       = "IPATool2";

    private static AppPreferences s_prefs        = new AppPreferences(APP_NAME);

    private ProgressMonitor       m_monitor;
    private Shell                 m_shell;
    private TabbedTracerImpl      m_tabbedTracer = new TabbedTracerImpl();
    private Button                m_btnRenameIPAs;
    private Button                m_chkRecurseFolders;
    private Text                  m_txtUpdFolder;
    private Text                  m_txtExistingFolder;
    private Text                  m_txtITunesFolder;
    private Button                m_btnUpdateIPAs;
    private Button                m_chkUseAllFolders;
    private Label                 m_lblExFolder;
    private Label                 m_lbliTunes;
    private Button                m_btnCleanJPGs;
    private Text                  m_txtIPhoneFolder;
    private Text                  m_txtIPadFolder;
    private Text                  m_txtMixedFolder;
    private Button                m_btnCheck;

    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            // En SWT hay que ajustar el tama�o de los fonts en Mac OS
            String OS_Name = System.getProperty("os.name");
            if (OS_Name != null && OS_Name.toLowerCase().contains("mac os")) {
                System.setProperty("org.eclipse.swt.internal.carbon.smallFonts", "true");
            }

            s_prefs.load(true);
            IPAToolAppWnd window = new IPAToolAppWnd();
            window.open();
            s_prefs.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the window
     */
    public void open() throws Exception {

        final Display display = Display.getDefault();
        createContents();
        m_shell.open();
        m_shell.layout();

        _setWndPosition();
        _initFields();

        m_shell.addShellListener(new ShellAdapter() {

            @Override
            public void shellClosed(ShellEvent e) {
                _updatePrefs();
                System.exit(0);
            }
        });

        while (!m_shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

    }

    private void _selectFolder(Text control) {
        DirectoryDialog dd = new DirectoryDialog(m_shell);
        dd.setFilterPath(control.getText());
        String newValue = dd.open();
        if (newValue != null)
            control.setText(newValue);
    }

    /**
     * Create contents of the window
     */
    protected void createContents() {

        m_shell = new Shell();

        m_shell.setImage(SWTResourceManager.getImage(IPAToolAppWnd.class, "/Properties.ico"));
        final BorderLayout borderLayout = new BorderLayout(0, 0);
        borderLayout.setVgap(5);
        m_shell.setLayout(borderLayout);
        m_shell.setSize(800, 400);
        m_shell.setMinimumSize(new Point(800, 400));
        m_shell.setText("IPATool2");

        Composite composite;
        composite = new Composite(m_shell, SWT.NONE);
        {
            TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
            tabFolder.setBounds(10, 10, 748, 200);

            {
                TabItem tbtmTools = new TabItem(tabFolder, SWT.NONE);
                tbtmTools.setText("Tools");

                Composite composite_1 = new Composite(tabFolder, SWT.NONE);
                tbtmTools.setControl(composite_1);

                m_txtUpdFolder = new Text(composite_1, SWT.BORDER);
                m_txtUpdFolder.setText("");
                m_txtUpdFolder.setBounds(122, 14, 564, 21);

                Button btnUpdFolder = new Button(composite_1, SWT.NONE);
                btnUpdFolder.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        _selectFolder(m_txtUpdFolder);

                    }
                });
                btnUpdFolder.setText("...");
                btnUpdFolder.setBounds(692, 12, 38, 25);

                m_txtExistingFolder = new Text(composite_1, SWT.BORDER);
                m_txtExistingFolder.setText("");
                m_txtExistingFolder.setBounds(122, 45, 564, 21);

                Button btnExFolder = new Button(composite_1, SWT.NONE);
                btnExFolder.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        _selectFolder(m_txtExistingFolder);

                    }
                });
                btnExFolder.setText("...");
                btnExFolder.setBounds(692, 43, 38, 25);

                m_txtITunesFolder = new Text(composite_1, SWT.BORDER);
                m_txtITunesFolder.setText("");
                m_txtITunesFolder.setBounds(122, 76, 564, 21);

                Button btnitunesFolder = new Button(composite_1, SWT.NONE);
                btnitunesFolder.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        _selectFolder(m_txtITunesFolder);
                    }
                });
                btnitunesFolder.setText("...");
                btnitunesFolder.setBounds(692, 74, 38, 25);

                m_lbliTunes = new Label(composite_1, SWT.NONE);
                m_lbliTunes.setText("iTunes IPAs Folder:");
                m_lbliTunes.setBounds(10, 79, 119, 15);

                m_lblExFolder = new Label(composite_1, SWT.NONE);
                m_lblExFolder.setText("Current IPAs Folder:");
                m_lblExFolder.setBounds(10, 48, 119, 15);

                Label lblUpdFolder = new Label(composite_1, SWT.NONE);
                lblUpdFolder.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
                lblUpdFolder.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
                lblUpdFolder.setText(" IPAs Folder:");
                lblUpdFolder.setBounds(10, 17, 119, 15);
                {
                    m_chkRecurseFolders = new Button(composite_1, SWT.CHECK);
                    m_chkRecurseFolders.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(SelectionEvent e) {
                        }
                    });
                    m_chkRecurseFolders.setLocation(122, 103);
                    m_chkRecurseFolders.setSize(107, 16);
                    m_chkRecurseFolders.setSelection(true);
                    m_chkRecurseFolders.setText("Recurse folders");
                }
                {
                    m_btnRenameIPAs = new Button(composite_1, SWT.NONE);
                    m_btnRenameIPAs.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
                    m_btnRenameIPAs.setLocation(120, 137);
                    m_btnRenameIPAs.setSize(94, 25);
                    m_btnRenameIPAs.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            renameIPAs();
                        }
                    });
                    m_btnRenameIPAs.setText("Rename IPAs");
                }

                m_btnUpdateIPAs = new Button(composite_1, SWT.NONE);
                m_btnUpdateIPAs.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        updateIPAs();
                    }
                });
                m_btnUpdateIPAs.setBounds(216, 137, 94, 25);
                m_btnUpdateIPAs.setText("Update IPAs");

                m_chkUseAllFolders = new Button(composite_1, SWT.CHECK);
                m_chkUseAllFolders.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        if (m_chkUseAllFolders.getSelection()) {
                            m_lblExFolder.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
                            m_lbliTunes.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
                        } else {
                            m_lblExFolder.setForeground(null);
                            m_lbliTunes.setForeground(null);
                        }
                    }
                });
                m_chkUseAllFolders.setBounds(244, 103, 107, 16);
                m_chkUseAllFolders.setText("Use all folders");

                m_btnCleanJPGs = new Button(composite_1, SWT.NONE);
                m_btnCleanJPGs.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
                m_btnCleanJPGs.setBounds(310, 137, 94, 25);
                m_btnCleanJPGs.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        cleanJPEGs();
                    }
                });
                m_btnCleanJPGs.setText("Clean JPEGs");

            }

            TabItem tbtmCheckIPAs = new TabItem(tabFolder, SWT.NONE);
            tbtmCheckIPAs.setText("Check");

            Composite composite_1 = new Composite(tabFolder, SWT.NONE);
            tbtmCheckIPAs.setControl(composite_1);

            m_txtIPhoneFolder = new Text(composite_1, SWT.BORDER);
            m_txtIPhoneFolder.setText("");
            m_txtIPhoneFolder.setBounds(122, 14, 564, 21);

            Button btnIPhoneFolder = new Button(composite_1, SWT.NONE);
            btnIPhoneFolder.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    _selectFolder(m_txtIPhoneFolder);
                }
            });
            btnIPhoneFolder.setText("...");
            btnIPhoneFolder.setBounds(692, 12, 38, 25);

            m_txtIPadFolder = new Text(composite_1, SWT.BORDER);
            m_txtIPadFolder.setText("");
            m_txtIPadFolder.setBounds(122, 45, 564, 21);

            Button btnIPadFolder = new Button(composite_1, SWT.NONE);
            btnIPadFolder.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    _selectFolder(m_txtIPadFolder);
                }
            });
            btnIPadFolder.setText("...");
            btnIPadFolder.setBounds(692, 43, 38, 25);

            m_txtMixedFolder = new Text(composite_1, SWT.BORDER);
            m_txtMixedFolder.setText("");
            m_txtMixedFolder.setBounds(122, 76, 564, 21);

            Button btnMixedFolder = new Button(composite_1, SWT.NONE);
            btnMixedFolder.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    _selectFolder(m_txtMixedFolder);
                }
            });
            btnMixedFolder.setText("...");
            btnMixedFolder.setBounds(692, 74, 38, 25);

            Label lblBothIpasFolder = new Label(composite_1, SWT.NONE);
            lblBothIpasFolder.setText("mixed Folder:");
            lblBothIpasFolder.setBounds(10, 79, 119, 15);

            Label lblIpadIpasFolder = new Label(composite_1, SWT.NONE);
            lblIpadIpasFolder.setText("iPad Folder:");
            lblIpadIpasFolder.setBounds(10, 48, 119, 15);

            Label lblIphoneFolder = new Label(composite_1, SWT.NONE);
            lblIphoneFolder.setText("iPhone Folder:");
            lblIphoneFolder.setBounds(10, 17, 119, 15);

            m_btnCheck = new Button(composite_1, SWT.NONE);
            m_btnCheck.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    checkIPAs();
                }
            });
            m_btnCheck.setText("Check IPAs");
            m_btnCheck.setBounds(122, 122, 90, 25);
        }
        composite.setLayoutData(BorderLayout.NORTH);

        final Label lblX = new Label(composite, SWT.NONE);
        lblX.setBounds(10, 10, 14, 211);
        final TabFolder m_tabTraces = new TabFolder(m_shell, SWT.NONE);
        m_tabTraces.setLayoutData(BorderLayout.CENTER);

        final Composite composite_1 = new Composite(m_shell, SWT.NONE);
        composite_1.setLayout(new BorderLayout(0, 0));
        composite_1.setLayoutData(BorderLayout.CENTER);

        // ------------------------------------------------------------------------
        // ********** TabbedTracer ***********************************************
        final CTabFolder tabTraces = m_tabbedTracer.createTabFolder(composite_1);
        tabTraces.setLayoutData(BorderLayout.CENTER);
        Tracer.setTracer(m_tabbedTracer);

    }

    private void _disableButtons() {
        m_btnRenameIPAs.setEnabled(false);
        m_btnUpdateIPAs.setEnabled(false);
        m_btnCleanJPGs.setEnabled(false);
        m_btnCheck.setEnabled(false);
    }

    private void _enableButtons() {
        m_btnRenameIPAs.setEnabled(true);
        m_btnUpdateIPAs.setEnabled(true);
        m_btnCleanJPGs.setEnabled(true);
        m_btnCheck.setEnabled(true);
    }

    private void _executionEnded() {
        _enableButtons();
    }

    private void _executionStarted() {
        Tracer.reset();
        _disableButtons();
    }

    private void _initFields() throws Exception {

        m_monitor = new ProgressMonitor();
        m_txtUpdFolder.setText(s_prefs.getPref("updateBaseFolder", ""));
        m_txtExistingFolder.setText(s_prefs.getPref("existingBaseFolder", ""));
        m_txtITunesFolder.setText(s_prefs.getPref("iTunesBaseFolder", ""));

        m_txtIPhoneFolder.setText(s_prefs.getPref("iPhoneFolder", ""));
        m_txtIPadFolder.setText(s_prefs.getPref("iPadFolder", ""));
        m_txtMixedFolder.setText(s_prefs.getPref("mixedFolder", ""));
    }

    private void _setWndPosition() {
        Rectangle r = Display.getDefault().getBounds();
        int w = (80 * r.width) / 100;
        int h = (80 * r.height) / 100;
        int x = (r.width - w) / 2;
        int y = (r.height - h) / 2;
        m_shell.setBounds(x, y, w, h);
    }

    private void _updatePrefs() {
        try {
            s_prefs.setPref("updateBaseFolder", m_txtUpdFolder.getText());
            s_prefs.setPref("existingBaseFolder", m_txtExistingFolder.getText());
            s_prefs.setPref("iTunesBaseFolder", m_txtITunesFolder.getText());

            s_prefs.setPref("iPhoneFolder", m_txtIPhoneFolder.getText());
            s_prefs.setPref("iPadFolder", m_txtIPadFolder.getText());
            s_prefs.setPref("mixedFolder", m_txtMixedFolder.getText());

            s_prefs.save();
        } catch (Exception ex) {
        }
    }

    private void renameIPAs() {
        _executionStarted();
        RenameWorker rw = new RenameWorker(m_monitor);
        if (m_chkUseAllFolders.getSelection()) {
            rw.rename(m_txtExistingFolder.getText(), m_txtUpdFolder.getText(), m_txtITunesFolder.getText(), m_chkRecurseFolders.getSelection());
        } else {
            rw.rename(null, m_txtUpdFolder.getText(), null, m_chkRecurseFolders.getSelection());
        }
    }

    private void cleanJPEGs() {
        _executionStarted();
        RenameWorker rw = new RenameWorker(m_monitor);
        if (m_chkUseAllFolders.getSelection()) {
            rw.cleanJPEGs(m_txtExistingFolder.getText(), m_txtUpdFolder.getText(), m_txtITunesFolder.getText(), m_chkRecurseFolders.getSelection());
        } else {
            rw.cleanJPEGs(null, m_txtUpdFolder.getText(), null, m_chkRecurseFolders.getSelection());
        }
    }

    private void updateIPAs() {
        _executionStarted();
        UpdateWorker uw = new UpdateWorker(m_monitor);
        uw.update(m_txtExistingFolder.getText(), m_txtUpdFolder.getText(), m_txtITunesFolder.getText());
    }

    private void checkIPAs() {
        _executionStarted();
        CheckerWorker cw = new CheckerWorker(m_monitor);
        cw.check(m_txtIPhoneFolder.getText(), m_txtIPadFolder.getText(), m_txtMixedFolder.getText());
    }
}