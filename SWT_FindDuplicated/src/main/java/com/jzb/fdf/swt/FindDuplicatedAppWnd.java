/**
 * 
 */
package com.jzb.fdf.swt;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import swing2swt.layout.BorderLayout;

import com.jzb.swt.util.SWTAsyncCallbackProxy;
import com.jzb.swt.util.TabbedTracerImpl;
import com.jzb.util.AppPreferences;
import com.jzb.util.Tracer;

import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("synthetic-access")
public class FindDuplicatedAppWnd {

    // ----------------------------------------------------------------------------------------------------
    private class MyCallback implements DuplicatedService.ICallBack {

        public MyCallback() {
        }

        @Override
        public void processingEnded() {
            __executionEnded();
        }

        @Override
        public void updateProgress(String completedStr, String pendingStr, String endingTimeStr) {
            m_lblProcessedCount.setText(completedStr);
            m_lblPendingCount.setText(pendingStr);
            m_lblEndingTime.setText(endingTimeStr);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static final String         APP_NAME            = "SWT Find Duplicated";

    private static AppPreferences       s_prefs             = new AppPreferences(APP_NAME);

    private Button                      m_btnAddFolder;
    private Button                      m_btnCancel;
    private Button                      m_btnFindFolder;
    private Button                      m_btnProcess;
    private Button                      m_btnRemoveFolder;
    private DuplicatedService.ICallBack m_callBack          = SWTAsyncCallbackProxy.createProxy(new MyCallback(), DuplicatedService.ICallBack.class);
    private Button                      m_chkEraseDB;
    private DuplicatedService           m_duplicatedService = new DuplicatedService();
    private FolderTree                  m_ftree;
    private Label                       m_label;
    private Label                       m_lblEndingTime;
    private Label                       m_lblPendingCount;
    private Label                       m_lblProcessedCount;
    private List                        m_lstRootFolders;

    private Shell                       m_shell;
    private TabbedTracerImpl            m_tabbedTracer      = new TabbedTracerImpl();
    private TabFolder                   m_tabFolder;
    private TabItem                     m_tbtmBrowser;
    private Text                        m_txtRootFolder;

    // ----------------------------------------------------------------------------------------------------
    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            // En SWT hay que ajustar el tamaño de los fonts en Mac OS
            String OS_Name = System.getProperty("os.name");
            if (OS_Name != null && OS_Name.toLowerCase().contains("mac os")) {
                System.setProperty("org.eclipse.swt.internal.carbon.smallFonts", "true");
            }

            s_prefs.load(true);
            FindDuplicatedAppWnd window = new FindDuplicatedAppWnd();
            window.open();
            s_prefs.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Open the window
     */
    public void open() throws Exception {

        final Display display = Display.getDefault();
        createContents();
        m_shell.open();
        m_shell.layout();

        __setWndPosition();
        __initFields();

        m_shell.addShellListener(new ShellAdapter() {

            @Override
            public void shellClosed(ShellEvent e) {
                __updatePrefs();
                System.exit(0);
            }
        });

        while (!m_shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Create contents of the window
     */
    protected void createContents() {

        m_shell = new Shell();

        m_shell.setImage(SWTResourceManager.getImage(FindDuplicatedAppWnd.class, "/Doofenshmirtz-256.png"));
        m_shell.setSize(800, 400);
        m_shell.setMinimumSize(new Point(800, 400));
        m_shell.setText("Booking Search-inator");
        m_shell.setLayout(new BorderLayout(0, 0));

        SashForm sashForm = new SashForm(m_shell, SWT.VERTICAL);
        sashForm.setLayoutData(BorderLayout.CENTER);

        m_tabFolder = new TabFolder(sashForm, SWT.NONE);

        m_tbtmBrowser = new TabItem(m_tabFolder, SWT.NONE);
        m_tbtmBrowser.setText("Browser");

        Composite composite = new Composite(m_tabFolder, SWT.NONE);
        m_tbtmBrowser.setControl(composite);
        composite.setLayout(new BorderLayout(0, 0));

        m_ftree = new FolderTree(composite, SWT.BORDER);
        m_ftree.setLocation(0, 0);
        m_ftree.setSize(454, 302);

        /******
         * m_lstRootFolders = new List(composite, SWT.BORDER); m_lstRootFolders = new List(composite, SWT.BORDER); m_lstRootFolders.addMouseListener(new MouseAdapter() {
         * 
         * @Override public void mouseDoubleClick(MouseEvent e) { if (m_lstRootFolders.getSelectionIndex() >= 0) {
         *           m_txtRootFolder.setText(m_lstRootFolders.getItem(m_lstRootFolders.getSelectionIndex())); } } }); m_lstRootFolders.setBounds(10, 65, 468, 83);
         * 
         *           Label lblRootFolder = new Label(composite, SWT.NONE); lblRootFolder.setBounds(10, 10, 75, 14); lblRootFolder.setText("Root Folder:");
         * 
         *           m_txtRootFolder = new Text(composite, SWT.BORDER); m_txtRootFolder.setBounds(91, 7, 348, 19);
         * 
         *           m_btnFindFolder = new Button(composite, SWT.NONE); m_btnFindFolder.addSelectionListener(new SelectionAdapter() {
         * @Override public void widgetSelected(SelectionEvent e) { _selectFolderDialog(m_txtRootFolder); } }); m_btnFindFolder.setBounds(435, 4, 43, 28); m_btnFindFolder.setText("...");
         * 
         *           Label lblRootFolderList = new Label(composite, SWT.NONE); lblRootFolderList.setBounds(10, 45, 154, 14); lblRootFolderList.setText("Root Folder List:");
         * 
         *           m_btnRemoveFolder = new Button(composite, SWT.NONE); m_btnRemoveFolder.addSelectionListener(new SelectionAdapter() {
         * @Override public void widgetSelected(SelectionEvent e) { int index = m_lstRootFolders.getSelectionIndex(); if (index >= 0) { m_lstRootFolders.remove(index); } } });
         *           m_btnRemoveFolder.setBounds(396, 38, 43, 28); m_btnRemoveFolder.setText("-");
         * 
         *           m_btnAddFolder = new Button(composite, SWT.NONE); m_btnAddFolder.addSelectionListener(new SelectionAdapter() {
         * @Override public void widgetSelected(SelectionEvent e) { _addFolderToList(m_txtRootFolder.getText(), m_lstRootFolders); } }); m_btnAddFolder.setBounds(435, 38, 43, 28);
         *           m_btnAddFolder.setText("+");
         * 
         *           m_btnProcess = new Button(composite, SWT.NONE); m_btnProcess.addSelectionListener(new SelectionAdapter() {
         * @Override public void widgetSelected(SelectionEvent e) { _processFolderSet(); } }); m_btnProcess.setBounds(384, 162, 94, 28); m_btnProcess.setText("Process");
         * 
         *           m_chkEraseDB = new Button(composite, SWT.CHECK); m_chkEraseDB.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
         *           m_chkEraseDB.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD)); m_chkEraseDB.setBounds(10, 167, 154, 18); m_chkEraseDB.setText("Erase previous data");
         * 
         *           m_btnCancel = new Button(composite, SWT.NONE); m_btnCancel.addSelectionListener(new SelectionAdapter() {
         * @Override public void widgetSelected(SelectionEvent e) { _cancelProcessFolderSet(); } }); m_btnCancel.setEnabled(false); m_btnCancel.setBounds(284, 162, 94, 28);
         *           m_btnCancel.setText("Cancel");
         * 
         *           m_label = new Label(composite, SWT.NONE); m_label.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD)); m_label.setBounds(10, 209, 94, 14);
         *           m_label.setText("Progress Status:");
         * 
         *           Composite composite_1 = new Composite(composite, SWT.BORDER); composite_1.setBounds(10, 230, 468, 35);
         * 
         *           Label lblProcessed = new Label(composite_1, SWT.NONE); lblProcessed.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD)); lblProcessed.setBounds(4, 10, 80, 14);
         *           lblProcessed.setText("Processed:");
         * 
         *           m_lblProcessedCount = new Label(composite_1, SWT.NONE); m_lblProcessedCount.setBounds(72, 10, 70, 14); m_lblProcessedCount.setText("0");
         * 
         *           Label lblPending = new Label(composite_1, SWT.NONE); lblPending.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD)); lblPending.setBounds(160, 10, 80, 14);
         *           lblPending.setText("Pending:");
         * 
         *           m_lblPendingCount = new Label(composite_1, SWT.NONE); m_lblPendingCount.setText("0"); m_lblPendingCount.setBounds(216, 10, 70, 14);
         * 
         *           Label lblNewLabel = new Label(composite_1, SWT.NONE); lblNewLabel.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD)); lblNewLabel.setBounds(312, 10, 80, 14);
         *           lblNewLabel.setText("Ending Time:");
         * 
         *           m_lblEndingTime = new Label(composite_1, SWT.NONE); m_lblEndingTime.setText("??:??:??"); m_lblEndingTime.setBounds(394, 10, 70, 14);
         *****/

        Composite compDownSide = new Composite(sashForm, SWT.BORDER);
        compDownSide.setLayout(new BorderLayout(0, 0));
        sashForm.setWeights(new int[] { 80, 20 });

        // ------------------------------------------------------------------------
        // ********** TabbedTracer ***********************************************
        final CTabFolder tabTraces = m_tabbedTracer.createTabFolder(compDownSide);
        tabTraces.setLayoutData(BorderLayout.CENTER);
        Tracer.setTracer(m_tabbedTracer);
    }

    // ----------------------------------------------------------------------------------------------------
    private void __disableControls() {
        m_btnFindFolder.setEnabled(false);
        m_btnAddFolder.setEnabled(false);
        m_btnRemoveFolder.setEnabled(false);
        m_btnProcess.setEnabled(false);
        m_btnCancel.setEnabled(true);

        m_chkEraseDB.setEnabled(false);
        m_txtRootFolder.setEnabled(false);
        m_lstRootFolders.setEnabled(false);
    }

    // ----------------------------------------------------------------------------------------------------
    private void __enableControls() {
        m_btnFindFolder.setEnabled(true);
        m_btnAddFolder.setEnabled(true);
        m_btnRemoveFolder.setEnabled(true);
        m_btnProcess.setEnabled(true);
        m_btnCancel.setEnabled(false);

        m_chkEraseDB.setEnabled(true);
        m_txtRootFolder.setEnabled(true);
        m_lstRootFolders.setEnabled(true);
    }

    // ----------------------------------------------------------------------------------------------------
    private void __executionEnded() {
        Tracer.flush();
        __enableControls();
        m_chkEraseDB.setSelection(false);
    }

    // ----------------------------------------------------------------------------------------------------
    private void __executionStarted() {
        Tracer.reset();
        m_lblProcessedCount.setText("0");
        m_lblPendingCount.setText("0");
        m_lblEndingTime.setText("??:??:??");
        __disableControls();
    }

    // ----------------------------------------------------------------------------------------------------
    private void __initFields() throws Exception {

        String folderList = s_prefs.getPref("folderList");
        if (folderList != null) {
            String folders[] = folderList.split(",");
            for (String folder : folders) {
                _addFolderToList(folder, m_lstRootFolders);
            }
        }

        m_ftree.initProviders();
    }

    // ----------------------------------------------------------------------------------------------------
    private void __setWndPosition() {
        Rectangle r = Display.getDefault().getBounds();
        int w = (90 * r.width) / 100;
        int h = (80 * r.height) / 100;
        int x = (r.width - w) / 2;
        int y = (r.height - h) / 4;
        m_shell.setBounds(x, y, w, h);
    }

    // ----------------------------------------------------------------------------------------------------
    private void __updatePrefs() {
        try {

            StringBuffer sb = new StringBuffer();
            for (String folderStr : m_lstRootFolders.getItems()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(folderStr);
            }
            s_prefs.setPref("folderList", sb.toString());

            s_prefs.save();
        } catch (Exception ex) {
            Tracer._error("Error saving preferences", ex);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _addFolderToList(String folderStr, List list) {
        /*
         * folderStr = folderStr.trim(); if (folderStr.length() > 0) { for (String name : m_lstRootFolders.getItems()) { if (name.equalsIgnoreCase(folderStr)) { return; } } list.add(folderStr); }
         */
    }

    // ----------------------------------------------------------------------------------------------------
    private void _cancelProcessFolderSet() {
        m_btnCancel.setEnabled(false);
        m_duplicatedService.cancelProcessing();
    }

    // ----------------------------------------------------------------------------------------------------
    private void _processFolderSet() {
        __executionStarted();
        m_duplicatedService.doProcessing(m_chkEraseDB.getSelection(), m_lstRootFolders.getItems(), m_callBack);
    }

    // ----------------------------------------------------------------------------------------------------
    private void _selectFolderDialog(Text txtFolder) {

        DirectoryDialog dd = new DirectoryDialog(m_shell, SWT.SHEET);
        File folder = new File(txtFolder.getText());
        while (folder != null) {
            if (folder.exists())
                break;
            folder = folder.getParentFile();
        }
        if (folder == null) {
            folder = new File(System.getProperty("user.home") + File.separator + "Documents");
            if (!folder.exists()) {
                folder = folder.getParentFile();
            }
        }
        dd.setFilterPath(folder.getAbsolutePath());
        String newValue = dd.open();
        if (newValue != null)
            txtFolder.setText(newValue);
    }
}