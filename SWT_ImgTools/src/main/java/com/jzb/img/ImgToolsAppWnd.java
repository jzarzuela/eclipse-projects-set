/**
 * 
 */
package com.jzb.img;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.BorderLayout;

import com.jzb.img.tsk.BaseTask.JustCheck;
import com.jzb.img.tsk.BaseTask.RecursiveProcessing;
import com.jzb.img.ui.BaseUI;
import com.jzb.img.ui.ITaskWnd;
import com.jzb.swt.util.TabbedTracerImpl;
import com.jzb.util.AppPreferences;
import com.jzb.util.Tracer;
import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyledText;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("synthetic-access")
public class ImgToolsAppWnd implements ITaskWnd {

    private static final String     APP_NAME       = "ImgTools";

    private static AppPreferences   s_prefs        = new AppPreferences(APP_NAME);
    private static final String     UI_CLASSES[]   = {
                                                   //
            "com.jzb.img.ui.CheckCompoundNamesUI", //
            "com.jzb.img.ui.UndoActionUI", //
            "com.jzb.img.ui.RenumerateUI", //
            "com.jzb.img.ui.SplitByDateUI", //
            "com.jzb.img.ui.RenameWithFoldersUI", //
            "com.jzb.img.ui.MoveFromSubfoldersUI", //
            "com.jzb.img.ui.SplitByCompoundNameUI", //
            "com.jzb.img.ui.RenameWithTimestampUI", //
            "com.jzb.img.ui.CleanNameUI", //
            "com.jzb.img.ui.DeleteEmptyFoldersUI", //
            "com.jzb.img.ui.RenameWithRegExprUI", //
            "com.jzb.img.ui.IPadResizeUI" //
                                                   };
    private Browser                 m_brwDescription;

    private Button                  m_chkJustCheck;
    private Button                  m_chkRecurse;
    private Combo                   m_cmbOperation;
    private Composite               m_compMain;
    private Composite               m_compOperation;

    private Shell                   m_shell;
    private TabbedTracerImpl        m_tabbedTracer = new TabbedTracerImpl();
    private BaseUI                  m_taskUI       = null;

    private HashMap<String, BaseUI> m_taskUIs      = new HashMap<String, BaseUI>();
    private Text                    m_txtBaseFolder;

    // --------------------------------------------------------------------------------------------------------
    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            // En SWT hay que ajustar el tama–o de los fonts en Mac OS
            String OS_Name = System.getProperty("os.name");
            if (OS_Name != null && OS_Name.toLowerCase().contains("mac os")) {
                System.setProperty("org.eclipse.swt.internal.carbon.smallFonts", "true");
            }

            s_prefs.load(true);
            ImgToolsAppWnd window = new ImgToolsAppWnd();
            window.open();
            s_prefs.save();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public File getBaseFolder() {
        return new File(m_txtBaseFolder.getText());
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public JustCheck getJustCheck() {
        if (m_chkJustCheck.getSelection()) {
            return JustCheck.YES;
        } else {
            return JustCheck.NO;
        }
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public RecursiveProcessing getRecursiveProcessing() {
        if (m_chkRecurse.getSelection()) {
            return RecursiveProcessing.YES;
        } else {
            return RecursiveProcessing.NO;
        }
    }

    // --------------------------------------------------------------------------------------------------------
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

        while (!m_shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public void runTask(final String taskName, final Runnable task) {

        Runnable runner = new Runnable() {

            @Override
            public void run() {
                try {
                    Tracer.reset();
                    Tracer._info("Task execution started [" + taskName + "]");
                    _executionStarted();
                    task.run();
                    Tracer._info("Task execution ended [" + taskName + "]");
                } catch (Throwable th) {
                    Tracer._error("Task execution failed [" + taskName + "]", th);
                }
                _executionEnded();
            }
        };

        Thread thread = new Thread(runner, taskName);
        thread.start();
    }

    // --------------------------------------------------------------------------------------------------------
    /**
     * Create contents of the window
     */
    protected void createContents() {

        m_shell = new Shell();
        m_shell.setImage(SWTResourceManager.getImage(ImgToolsAppWnd.class, "/Tools.ico"));
        final BorderLayout borderLayout = new BorderLayout(0, 0);
        borderLayout.setVgap(5);
        m_shell.setLayout(borderLayout);
        m_shell.setSize(900, 640);
        m_shell.setMinimumSize(new Point(900, 400));
        m_shell.setText("ImgTools");

        final Composite composite_1 = new Composite(m_shell, SWT.NONE);
        composite_1.setLayout(new BorderLayout(0, 0));
        composite_1.setLayoutData(BorderLayout.NORTH);
        m_compMain = new Composite(composite_1, SWT.NONE);
        m_compMain.setLayoutData(BorderLayout.NORTH);
        m_compMain.setLayout(null);

        m_txtBaseFolder = new Text(m_compMain, SWT.BORDER);
        m_txtBaseFolder.setBounds(60, 16, 781, 25);
        m_txtBaseFolder.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(final DisposeEvent e) {
                s_prefs.setPref("baseFolder", m_txtBaseFolder.getText());
            }
        });

        final Label folderLabel = new Label(m_compMain, SWT.NONE);
        folderLabel.setBounds(10, 19, 50, 16);
        folderLabel.setText("Folder:");

        final Button btnFolder = new Button(m_compMain, SWT.NONE);
        btnFolder.setBounds(847, 15, 43, 26);
        btnFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                DirectoryDialog dd = new DirectoryDialog(m_shell);
                File folder = new File(m_txtBaseFolder.getText());
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
                    m_txtBaseFolder.setText(newValue);
            }
        });
        btnFolder.setText("...");

        Label lblOperation = new Label(m_compMain, SWT.NONE);
        lblOperation.setBounds(60, 51, 33, 14);
        lblOperation.setText("Cmd:");

        m_cmbOperation = new Combo(m_compMain, SWT.READ_ONLY);
        m_cmbOperation.setBounds(88, 47, 176, 22);
        m_cmbOperation.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _selectTaskUI();
            }
        });

        m_chkJustCheck = new Button(m_compMain, SWT.CHECK | SWT.LEFT);
        m_chkJustCheck.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
        m_chkJustCheck.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD));
        m_chkJustCheck.setBounds(270, 50, 96, 17);
        m_chkJustCheck.setSelection(true);
        m_chkJustCheck.setText("Just check");

        m_chkRecurse = new Button(m_compMain, SWT.CHECK);
        m_chkRecurse.setBounds(358, 50, 110, 17);
        m_chkRecurse.setText("Recurse folders");
        m_chkRecurse.setSelection(true);

        StyledText styledText = new StyledText(m_compMain, SWT.READ_ONLY | SWT.SINGLE);
        styledText.setBackground(SWTResourceManager.getColor(232, 232, 232));
        styledText.setBounds(487, 49, 354, 18);
        styledText.setText("{groupName-}#00000{-subgroupname}{_name}_[ImgName].ext");
        Color bgColor = styledText.getBackground();
        Color sepFColor = SWTResourceManager.getColor(SWT.COLOR_RED);
        StyleRange style1 = new StyleRange(12, 7, SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE), bgColor, SWT.BOLD);
        StyleRange style2 = new StyleRange(40, 10, SWTResourceManager.getColor(SWT.COLOR_DARK_RED), bgColor, SWT.BOLD);
        StyleRange style3 = new StyleRange(50, 4, SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN), bgColor, SWT.BOLD);
        StyleRange style4 = new StyleRange(0, 12, SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN), bgColor, SWT.ITALIC);
        StyleRange style5 = new StyleRange(19, 14, SWTResourceManager.getColor(SWT.COLOR_DARK_MAGENTA), bgColor, SWT.ITALIC);
        StyleRange style6 = new StyleRange(33, 7, SWTResourceManager.getColor(SWT.COLOR_BLUE), bgColor, SWT.ITALIC);
        StyleRange style7 = new StyleRange(10, 1, sepFColor, bgColor, SWT.BOLD);
        style7.font = SWTResourceManager.getFont("Lucida Grande", 16, SWT.BOLD);
        StyleRange style8 = new StyleRange(19, 1, sepFColor, bgColor, SWT.BOLD);
        style8.font = SWTResourceManager.getFont("Lucida Grande", 16, SWT.BOLD);
        StyleRange style9 = new StyleRange(34, 1, sepFColor, bgColor, SWT.BOLD);
        style9.font = SWTResourceManager.getFont("Lucida Grande", 16, SWT.BOLD);
        styledText.setStyleRange(style1);
        styledText.setStyleRange(style2);
        styledText.setStyleRange(style3);
        styledText.setStyleRange(style4);
        styledText.setStyleRange(style5);
        styledText.setStyleRange(style6);
        styledText.setStyleRange(style7);
        styledText.setStyleRange(style8);
        styledText.setStyleRange(style9);

        m_compOperation = new Composite(m_compMain, SWT.BORDER);
        m_compOperation.setBounds(10, 78, 880, 80);
        m_compOperation.setLayout(new FormLayout());

        m_brwDescription = new Browser(m_compMain, SWT.BORDER);
        m_brwDescription.setJavascriptEnabled(false);
        m_brwDescription.setBounds(10, 164, 880, 112);

        final Label label = new Label(m_compMain, SWT.NONE);
        label.setBounds(775, 5, 14, 281);

        final Composite compTraces = new Composite(m_shell, SWT.NONE);
        compTraces.setLayout(new BorderLayout(0, 0));
        compTraces.setLayoutData(BorderLayout.CENTER);

        // ------------------------------------------------------------------------
        // ********** TabbedTracer ***********************************************
        final CTabFolder tabTraces = m_tabbedTracer.createTabFolder(compTraces);
        tabTraces.setLayoutData(BorderLayout.CENTER);
        Tracer.setTracer(m_tabbedTracer);

        // ------------------------------------------------------------------------
        // ********** Combo Operations ********************************************
        for (String ui_class : UI_CLASSES) {
            try {
                Class<BaseUI> clazz = (Class<BaseUI>) Class.forName(ui_class);
                Constructor<BaseUI> c = clazz.getConstructor(Composite.class, int.class);
                BaseUI ui = c.newInstance(m_compOperation, SWT.NONE);
                ui.setPrefs(s_prefs);
                ui.setTaskWnd(this);
                ui.setVisible(false);
                ui.setBounds(0, 0, m_compOperation.getSize().x, m_compOperation.getSize().y);
                m_cmbOperation.add(ui.getTaskName());
                m_cmbOperation.setData(ui.getTaskName());
                m_taskUIs.put(ui.getTaskName(), ui);
            } catch (Exception ex) {
                Tracer._error("Error creating Task UI", ex);
            }

            m_cmbOperation.select(0);
            _selectTaskUI();
        }

    }

    // --------------------------------------------------------------------------------------------------------
    private void _executionEnded() {
        Tracer.flush();
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                _fullEnableControl(m_compMain);
                m_chkJustCheck.setSelection(true);
            }

        });
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executionStarted() {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                _fullDisableControl(m_compMain);
            }

        });
    }

    // --------------------------------------------------------------------------------------------------------
    private void _fullDisableControl(Control control) {
        control.setEnabled(false);
        if (control instanceof Composite) {
            Control children[] = ((Composite) control).getChildren();
            for (Control child : children) {
                _fullDisableControl(child);
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private void _fullEnableControl(Control control) {
        control.setEnabled(true);
        if (control instanceof Composite) {
            Control children[] = ((Composite) control).getChildren();
            for (Control child : children) {
                _fullEnableControl(child);
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private void _initFields() throws Exception {

        m_txtBaseFolder.setText(s_prefs.getPref("baseFolder", ""));

    }

    // --------------------------------------------------------------------------------------------------------
    private void _selectTaskUI() {

        if (m_taskUI != null) {
            m_taskUI.setVisible(false);
        }

        String key = m_cmbOperation.getText();
        m_taskUI = m_taskUIs.get(key);
        if (m_taskUI != null) {
            m_taskUI.setVisible(true);

            String h = "<head><style type=\"text/css\">\nbody, td {font-style:normal;font-size:14px;}\n</style></head>";
            String text = "<html>" + h + "<body bgcolor='#D7D7D7'>" + m_taskUI.getTaskDescription() + "</font></body></html>";
            m_brwDescription.setText(text);
        }

        m_chkJustCheck.setSelection(true);

    }

    // --------------------------------------------------------------------------------------------------------
    private void _setWndPosition() {

        Rectangle r = Display.getDefault().getBounds();

        int w = (80 * r.width) / 100;
        w = m_shell.getSize().x; // NO LE CAMBIA EL ANHO
        int h = (80 * r.height) / 100;
        int x = (int) ((r.width - w) * 0.5);
        int y = (int) ((r.height - h) * 0.35);
        m_shell.setBounds(x, y, w, h);
    }
}