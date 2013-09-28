/**
 * 
 */
package com.jzb.booking;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import swing2swt.layout.BorderLayout;

import com.jzb.booking.data.ParserSettings;
import com.jzb.booking.data.THotelData;
import com.jzb.booking.data.TRoomData;
import com.jzb.booking.worker.ParserWorker;
import com.jzb.booking.worker.ParserWorkerStatus;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.swt.util.TabbedTracerImpl;
import com.jzb.util.AppPreferences;
import com.jzb.util.Tracer;
import com.swtdesigner.SWTResourceManager;

import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("synthetic-access")
public class BookingAppWnd {

    private class ProgressMonitor implements IProgressMonitor {

        /**
         * @see com.jzb.swt.util2.tools.IProgressMonitor#processingEnded(boolean)
         */
        public void processingEnded(final boolean failed, final Object result) {

            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    _executionEnded(failed, result);
                }
            });
        }

    }

    private static final String   APP_NAME                = "SWTBooking";

    private static AppPreferences s_prefs                 = new AppPreferences(APP_NAME);

    private ArrayList<TRoomData>  m_allHotelRoomsDataList = new ArrayList<TRoomData>();
    private boolean               m_autoNavigation        = false;
    private Browser               m_browser;
    private ProgressMonitor       m_monitor;
    private ParserWorker          m_parserWorker;
    private ProgressBar           m_progressBar;
    private Shell                 m_shell;
    private boolean               m_showProgress          = false;
    private TabbedTracerImpl      m_tabbedTracer          = new TabbedTracerImpl();
    private ToolItem              m_tltmExtractInfo;
    private ToolItem              m_tltmHome;
    private TabFolder             m_tabFolder;
    private TabItem               m_tbtmBrowser;
    private TabItem               m_tbtmData;
    private TableViewer           m_tableViewer;
    private TabItem               m_tbtmSettings;
    private Text                  m_txtNumReqRooms;
    private Text                  m_txtNonCancelableIncrement;
    private Text                  m_txtBreakfastCostPerDay;

    private ToolItem              m_tltmAllRooms;
    private ToolItem              m_tltmFiltered;
    private ToolItem              m_tltmNonCancelable;
    private ToolItem              m_tltmWithoutRating;
    private Label                 m_lblNumReqRooms;
    private Label                 m_lblNonCcncelableIncrement;
    private Label                 m_lblFamilyBreakfastCost;
    private Text                  m_txtMinRoomCapacity;
    private Label                 m_lblMinRoomCapacity;

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
            BookingAppWnd window = new BookingAppWnd();
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

    // ----------------------------------------------------------------------------------------------------
    /**
     * Create contents of the window
     */
    protected void createContents() {

        m_shell = new Shell();

        m_shell.setImage(SWTResourceManager.getImage(BookingAppWnd.class, "/Doofenshmirtz-256.png"));
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

        ToolBar toolBar = new ToolBar(composite, SWT.BORDER | SWT.FLAT | SWT.RIGHT);
        toolBar.setLayoutData(BorderLayout.NORTH);

        m_tltmHome = new ToolItem(toolBar, SWT.NONE);
        m_tltmHome.setSelection(false);
        m_tltmHome.setEnabled(true);
        m_tltmHome.setText("Home");
        m_tltmHome.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _btnHomePressed();
            }
        });

        m_tltmExtractInfo = new ToolItem(toolBar, SWT.NONE);
        m_tltmExtractInfo.setEnabled(false);
        m_tltmExtractInfo.setText("Extract Info");

        ToolItem toolItem = new ToolItem(toolBar, SWT.SEPARATOR);
        toolItem.setText("");

        ToolItem tltmLoadTestInfo = new ToolItem(toolBar, SWT.NONE);
        tltmLoadTestInfo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _loadHotelsDebuggingInfo();
            }
        });
        tltmLoadTestInfo.setText("Load test info");
        m_tltmExtractInfo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _btnExtractInfoPressed();
            }
        });

        m_browser = new Browser(composite, SWT.NONE);
        m_browser.addLocationListener(new LocationAdapter() {

            @Override
            public void changing(LocationEvent event) {
                if (event.location.toLowerCase().contains("http://www.booking.com")) {
                    m_showProgress = true;
                }
            }
        });
        m_browser.addProgressListener(new ProgressAdapter() {

            @Override
            public void changed(ProgressEvent event) {
                if (m_showProgress) {
                    m_progressBar.setSelection(100 * event.current / event.total);
                }
            }

            @Override
            public void completed(ProgressEvent event) {
                Tracer._debug("+ ProgressEvent - Page loading completed ");
                m_showProgress = false;
                m_progressBar.setSelection(0);
                if (m_autoNavigation) {
                    _extractHotelsInfoFromPage();
                }

            }

        });

        m_browser.setLayoutData(BorderLayout.CENTER);

        m_progressBar = new ProgressBar(composite, SWT.NONE);
        m_progressBar.setLayoutData(BorderLayout.SOUTH);

        m_tbtmData = new TabItem(m_tabFolder, SWT.NONE);
        m_tbtmData.setText("Data");

        Composite composite_1 = new Composite(m_tabFolder, SWT.NONE);
        m_tbtmData.setControl(composite_1);
        composite_1.setLayout(new BorderLayout(0, 0));

        m_tableViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
        Table table = m_tableViewer.getTable();
        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                _tableDoblecliked();
            }
        });
        table.setLayoutData(BorderLayout.CENTER);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableColumn tcolRanking = new TableColumn(table, SWT.CENTER);
        tcolRanking.setWidth(50);
        tcolRanking.setText("Ranking");

        TableColumn tcolDayPrice = new TableColumn(table, SWT.CENTER);
        tcolDayPrice.setWidth(50);
        tcolDayPrice.setText("Day Price");

        TableColumn tcolCalcPrice = new TableColumn(table, SWT.CENTER);
        tcolCalcPrice.setWidth(60);
        tcolCalcPrice.setText("Calc Price");

        TableColumn tcolPrice = new TableColumn(table, SWT.CENTER);
        tcolPrice.setWidth(60);
        tcolPrice.setText("Price");

        TableColumn tcolName = new TableColumn(table, SWT.NONE);
        tcolName.setWidth(200);
        tcolName.setText("Name");

        TableColumn tcolRating = new TableColumn(table, SWT.CENTER);
        tcolRating.setWidth(50);
        tcolRating.setText("Rating");

        TableColumn tcolVotes = new TableColumn(table, SWT.CENTER);
        tcolVotes.setWidth(50);
        tcolVotes.setText("Votes");

        TableColumn tcolStars = new TableColumn(table, SWT.CENTER);
        tcolStars.setWidth(50);
        tcolStars.setText("Stars");

        TableColumn tcolAddress = new TableColumn(table, SWT.CENTER);
        tcolAddress.setWidth(150);
        tcolAddress.setText("Address");

        TableColumn tcolDistance = new TableColumn(table, SWT.CENTER);
        tcolDistance.setWidth(50);
        tcolDistance.setText("Dist.");

        TableColumn tcolCancelable = new TableColumn(table, SWT.CENTER);
        tcolCancelable.setWidth(50);
        tcolCancelable.setText("Cancelable");

        TableColumn tcolBreakfast = new TableColumn(table, SWT.CENTER);
        tcolBreakfast.setWidth(50);
        tcolBreakfast.setText("Breakfast");

        TableColumn tcolRoomType = new TableColumn(table, SWT.NONE);
        tcolRoomType.setWidth(300);
        tcolRoomType.setText("Room Type");

        TableColumn tcolAvailability = new TableColumn(table, SWT.CENTER);
        tcolAvailability.setWidth(70);
        tcolAvailability.setText("Availability");

        Composite composite_3 = new Composite(composite_1, SWT.NONE);
        composite_3.setLayoutData(BorderLayout.NORTH);
        composite_3.setLayout(new BorderLayout(0, 0));

        Label lblNewLabel = new Label(composite_3, SWT.CENTER);
        lblNewLabel.setForeground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
        lblNewLabel.setFont(org.eclipse.wb.swt.SWTResourceManager.getFont("Lucida Grande", 12, SWT.BOLD));
        lblNewLabel.setLayoutData(BorderLayout.WEST);
        lblNewLabel.setText("Data Filters: ");

        ToolBar toolBar_1 = new ToolBar(composite_3, SWT.BORDER | SWT.FLAT | SWT.RIGHT);
        toolBar_1.setLayoutData(BorderLayout.CENTER);

        m_tltmAllRooms = new ToolItem(toolBar_1, SWT.RADIO);
        m_tltmAllRooms.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _showTableInfo(0);
            }
        });
        m_tltmAllRooms.setText("All Rooms");

        m_tltmFiltered = new ToolItem(toolBar_1, SWT.RADIO);
        m_tltmFiltered.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _showTableInfo(1);
            }
        });
        m_tltmFiltered.setSelection(true);
        m_tltmFiltered.setText("Filtered");

        m_tltmNonCancelable = new ToolItem(toolBar_1, SWT.RADIO);
        m_tltmNonCancelable.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _showTableInfo(2);
            }
        });
        m_tltmNonCancelable.setText("Non Cancelable");

        m_tltmWithoutRating = new ToolItem(toolBar_1, SWT.RADIO);
        m_tltmWithoutRating.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _showTableInfo(3);
            }
        });
        m_tltmWithoutRating.setText("Without Rating");

        Composite compDownSide = new Composite(sashForm, SWT.BORDER);
        compDownSide.setLayout(new BorderLayout(0, 0));
        sashForm.setWeights(new int[] { 80, 20 });

        // Prepara las columna para ser ordenables
        int colIndex = 0;
        for (TableColumn tcol2 : table.getColumns()) {
            tcol2.setData(colIndex++);
            tcol2.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {

                    TableColumn col = (TableColumn) e.widget;
                    Table table = col.getParent();
                    TableColumn sortingCol = table.getSortColumn();

                    if (col == sortingCol) {
                        table.setSortDirection(table.getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP);
                    } else {
                        table.setSortColumn(col);
                        Integer colIndex = (Integer) col.getData();
                        if (colIndex == 0 || colIndex == 5 || colIndex == 6 || colIndex == 7 || colIndex == 9 || colIndex == 10)
                            table.setSortDirection(SWT.DOWN);
                        else
                            table.setSortDirection(SWT.UP);
                    }
                    m_tableViewer.refresh();
                }
            });
        }

        m_tbtmSettings = new TabItem(m_tabFolder, SWT.NONE);
        m_tbtmSettings.setText("Settings ");
        Composite composite_2 = new Composite(m_tabFolder, SWT.NONE);
        m_tbtmSettings.setControl(composite_2);
        composite_2.setLayout(null);

        m_lblMinRoomCapacity = new Label(composite_2, SWT.NONE);
        m_lblMinRoomCapacity.setBounds(10, 10, 111, 14);

        m_lblMinRoomCapacity.setText("Min Room Capacity:");

        m_txtMinRoomCapacity = new Text(composite_2, SWT.BORDER);
        m_txtMinRoomCapacity.setBounds(198, 10, 64, 19);
        m_txtMinRoomCapacity.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                _markSettingsAsModified(m_lblMinRoomCapacity);
            }
        });

        m_lblNumReqRooms = new Label(composite_2, SWT.NONE);
        m_lblNumReqRooms.setBounds(10, 35, 123, 14);
        m_lblNumReqRooms.setText("Num required rooms: ");

        m_txtNumReqRooms = new Text(composite_2, SWT.BORDER);
        m_txtNumReqRooms.setBounds(198, 35, 64, 19);
        m_txtNumReqRooms.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                _markSettingsAsModified(m_lblNumReqRooms);
            }
        });

        m_lblNonCcncelableIncrement = new Label(composite_2, SWT.NONE);
        m_lblNonCcncelableIncrement.setBounds(10, 63, 168, 14);
        m_lblNonCcncelableIncrement.setText("Non cancelable increment (%): ");

        m_txtNonCancelableIncrement = new Text(composite_2, SWT.BORDER);
        m_txtNonCancelableIncrement.setBounds(198, 60, 64, 19);
        m_txtNonCancelableIncrement.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                _markSettingsAsModified(m_lblNonCcncelableIncrement);
            }
        });

        m_lblFamilyBreakfastCost = new Label(composite_2, SWT.NONE);
        m_lblFamilyBreakfastCost.setBounds(10, 88, 175, 14);
        m_lblFamilyBreakfastCost.setText("Family breakfast cost (per day): ");

        m_txtBreakfastCostPerDay = new Text(composite_2, SWT.BORDER);
        m_txtBreakfastCostPerDay.setBounds(198, 85, 64, 19);
        m_txtBreakfastCostPerDay.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                _markSettingsAsModified(m_lblFamilyBreakfastCost);
            }
        });

        Button btnSetValues = new Button(composite_2, SWT.NONE);
        btnSetValues.setBounds(185, 120, 87, 28);
        btnSetValues.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _updatePrefs();
            }
        });
        btnSetValues.setText("Set Values");

        // ------------------------------------------------------------------------
        // ********** TabbedTracer ***********************************************
        final CTabFolder tabTraces = m_tabbedTracer.createTabFolder(compDownSide);
        tabTraces.setLayoutData(BorderLayout.CENTER);
        Tracer.setTracer(m_tabbedTracer);
    }

    // ----------------------------------------------------------------------------------------------------
    private void _tableDoblecliked() {

        int rowIndex = m_tableViewer.getTable().getSelectionIndex();
        TRoomData room = (TRoomData) m_tableViewer.getElementAt(rowIndex);
        if (room != null && room.ownerHotel.dataLink != null) {
            HotelDetailWnd.openNew(room.ownerHotel);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _btnExtractInfoPressed() {

        // Pone la barra de progreso a cero
        m_progressBar.setSelection(0);

        // Aborta y limpia cualquier tarea previa
        m_parserWorker.reset();

        // Limpia las trazas
        Tracer.reset();

        // Borra los datos previos de los hoteles
        m_allHotelRoomsDataList.clear();

        // Inhabilita el botón temporalmente
        m_tltmExtractInfo.setEnabled(false);

        // Inicia un nuevo trabajo
        _extractHotelsInfoFromPage();
    }

    // ----------------------------------------------------------------------------------------------------
    private void _btnHomePressed() {

        // Pone la barra de progreso a cero
        m_progressBar.setSelection(0);

        // Aborta y limpia cualquier tarea previa
        m_parserWorker.reset();

        // Borra los datos previos de los hoteles
        m_allHotelRoomsDataList.clear();

        // Habilita el botón de extraccion
        m_tltmExtractInfo.setEnabled(true);

        // Navega a la pagina inicial
        _navigateToURL("http://www.booking.com", false);
    }

    // ----------------------------------------------------------------------------------------------------
    private void _executionEnded(final boolean failed, final Object result) {

        if (result instanceof ParserWorkerStatus) {

            ParserWorkerStatus pws = (ParserWorkerStatus) result;
            switch (pws.type) {
                case ParseNextPage:
                    URL nextURL = _getAbsoluteURL(pws.nextPageUrl);
                    _navigateToURL(nextURL.toString(), true);
                    break;

                case PageEnded:
                    for (THotelData hotel : pws.hotelDataList) {
                        Tracer._debug(hotel.toString());
                        m_allHotelRoomsDataList.addAll(hotel.rooms);
                    }
                    break;

                case AllPagesEnded:
                    for (THotelData hotel : pws.hotelDataList) {
                        Tracer._debug(hotel.toString());
                        m_allHotelRoomsDataList.addAll(hotel.rooms);
                    }
                    m_autoNavigation = false;
                    _updateRankingInfo(true);
                    _showTableInfo(1);
                    m_tltmExtractInfo.setEnabled(true);
                    break;
            }

        } else {
            // Ha habido un error casi seguro
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _extractHotelsInfoFromPage() {

        if (m_browser.getUrl().toLowerCase().contains("booking.com")) {
            String htmlText = m_browser.getText();
            m_parserWorker.parseHtmlText(m_browser.getUrl(), htmlText);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _loadHotelsDebuggingInfo() {
        try {
            String fileName = _getDebugtDataFile();
            ArrayList<THotelData> hotels = THotelData.loadHotelsData(fileName);
            ArrayList<TRoomData> hotelRoomsDataList = new ArrayList<TRoomData>();
            for (THotelData hotel : hotels) {
                hotelRoomsDataList.addAll(hotel.rooms);
            }
            m_allHotelRoomsDataList = hotelRoomsDataList;
            _updateRankingInfo(false);
            _showTableInfo(1);
        } catch (Exception ex) {
            Tracer._error("Error loading JSON debugging info", ex);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private String _getDebugtDataFile() {
        File workingFolder = new File(".");
        File debugDataFile = new File(workingFolder, "/src/main/java/com/jzb/booking/hotelsInfo.json");
        return debugDataFile.getAbsolutePath();
    }

    // ----------------------------------------------------------------------------------------------------
    private void _updateRankingInfo(boolean saveDebuggingInfo) {

        // Guarda la informacion para depuracion
        try {
            if (saveDebuggingInfo) {
                HashSet<THotelData> hotels = new HashSet<THotelData>();
                for (TRoomData room : m_allHotelRoomsDataList) {
                    hotels.add(room.ownerHotel);
                }
                String fileName = _getDebugtDataFile();
                THotelData.saveHotelsData(fileName, new ArrayList<THotelData>(hotels));
            }
        } catch (Exception ex) {
            Tracer._error("Error saving JSON debugging info", ex);
        }

        // Antes de mostrar la informacion les asigna un ranking atendiendo a la formula creada
        try {
            FuzzyLogicRanking flr = new FuzzyLogicRanking();
            for (TRoomData room : m_allHotelRoomsDataList) {
                flr.adjustRanking(room);
            }
        } catch (Exception ex) {
            Tracer._error("Error loading FuzzyLogicRanking", ex);
            return;
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _showTableInfo(int filter) {

        m_tltmAllRooms.setSelection(filter == 0);
        m_tltmFiltered.setSelection(filter == 1);
        m_tltmNonCancelable.setSelection(filter == 2);
        m_tltmWithoutRating.setSelection(filter == 3);

        ArrayList<TRoomData> hotelRoomsDataList = new ArrayList<TRoomData>();
        if (m_allHotelRoomsDataList != null) {
            for (TRoomData room : m_allHotelRoomsDataList) {
                switch (filter) {
                    case 0:
                        hotelRoomsDataList.add(room);
                        break;
                    case 1:
                        if (room.ownerHotel.avgRating > 0 && room.ownerHotel.votes > 0 && room.cancelable) {
                            hotelRoomsDataList.add(room);
                        }
                        break;
                    case 2:
                        if (room.ownerHotel.avgRating > 0 && room.ownerHotel.votes > 0 && !room.cancelable) {
                            hotelRoomsDataList.add(room);
                        }
                        break;
                    case 3:
                        if (room.ownerHotel.avgRating == 0 || room.ownerHotel.votes <= 0) {
                            hotelRoomsDataList.add(room);
                        }
                        break;
                }
            }
        }
        __showTableInfo(hotelRoomsDataList);
    }

    // ----------------------------------------------------------------------------------------------------
    private void __showTableInfo(ArrayList<TRoomData> hotelRoomsDataList) {

        // Muestra la informacion
        Table table = m_tableViewer.getTable();
        table.setSortColumn(table.getColumn(0));
        table.setSortDirection(SWT.DOWN);

        m_tableViewer.setContentProvider(new MyStructuredContentProvider());
        m_tableViewer.setLabelProvider(new MyTableLabelProvider());
        m_tableViewer.setComparator(new MyViewerComparator());

        m_tableViewer.setInput(hotelRoomsDataList);

        m_tabFolder.setSelection(1);
    }

    // ----------------------------------------------------------------------------------------------------
    private URL _getAbsoluteURL(String nextPageURL) {

        try {
            URL currentUrl = new URL(m_browser.getUrl());
            URL nextUrl = new URL(currentUrl, nextPageURL);
            return nextUrl;
        } catch (Throwable th) {
            Tracer._error("Error creating absolute URL for: " + nextPageURL, th);
            return null;
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _navigateToURL(String url, boolean autoNavigation) {
        m_autoNavigation = autoNavigation;
        m_showProgress = true;
        m_browser.setUrl(url);
    }

    // ----------------------------------------------------------------------------------------------------
    private void _setWndPosition() {
        Rectangle r = Display.getDefault().getBounds();
        int w = (90 * r.width) / 100;
        int h = (80 * r.height) / 100;
        int x = (r.width - w) / 2;
        int y = (r.height - h) / 4;
        m_shell.setBounds(x, y, w, h);
    }

    // ----------------------------------------------------------------------------------------------------
    private void _initFields() throws Exception {

        m_monitor = new ProgressMonitor();
        m_parserWorker = new ParserWorker(m_monitor);
        m_txtMinRoomCapacity.setText("" + ParserSettings.minRoomCapacity);
        m_txtNumReqRooms.setText("" + ParserSettings.numReqRooms);
        m_txtNonCancelableIncrement.setText("" + (int) (100 * (ParserSettings.NonCancelableIncrement - 1)));
        m_txtBreakfastCostPerDay.setText("" + (int) ParserSettings.FamilyBreakfastCostPerDay);

        _updatePrefs();
    }

    // ----------------------------------------------------------------------------------------------------
    private int _parseUInt(String value, int defValue, int maxValue) {
        try {
            int vInt = Integer.parseInt(value);
            int vInt2 = vInt > 0 ? vInt : 0;
            return vInt2 <= maxValue ? vInt2 : maxValue;
        } catch (NumberFormatException nfex) {
            return defValue;
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _updatePrefs() {
        try {

            ParserSettings.minRoomCapacity = _parseUInt(m_txtMinRoomCapacity.getText(), 1, 9);
            m_txtMinRoomCapacity.setText("" + ParserSettings.minRoomCapacity);
            s_prefs.setPref("MinRoomCapacity", m_txtMinRoomCapacity.getText());

            ParserSettings.numReqRooms = _parseUInt(m_txtNumReqRooms.getText(), 1, 9);
            m_txtNumReqRooms.setText("" + ParserSettings.numReqRooms);
            s_prefs.setPref("NumReqRooms", m_txtNumReqRooms.getText());

            int value = _parseUInt(m_txtNonCancelableIncrement.getText(), 20, 100);
            ParserSettings.NonCancelableIncrement = (100.0 + value) / 100.0;
            m_txtNonCancelableIncrement.setText("" + value);
            s_prefs.setPref("NonCancelableIncrement", m_txtNonCancelableIncrement.getText());

            ParserSettings.FamilyBreakfastCostPerDay = _parseUInt(m_txtBreakfastCostPerDay.getText(), 25, 200);
            m_txtBreakfastCostPerDay.setText("" + (int) ParserSettings.FamilyBreakfastCostPerDay);
            s_prefs.setPref("BreakfastCostPerDay", m_txtBreakfastCostPerDay.getText());

            s_prefs.save();
            _markSettingsAsSaved();
        } catch (Exception ex) {
            Tracer._error("Error saving preferences", ex);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _markSettingsAsModified(Label lblControl) {
        lblControl.setFont(org.eclipse.wb.swt.SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD));
        lblControl.setForeground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_RED));
    }

    // ----------------------------------------------------------------------------------------------------
    private void _markSettingsAsSaved() {
        m_lblMinRoomCapacity.setFont(org.eclipse.wb.swt.SWTResourceManager.getFont("Lucida Grande", 11, SWT.NONE));
        m_lblMinRoomCapacity.setForeground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
        m_lblNumReqRooms.setFont(org.eclipse.wb.swt.SWTResourceManager.getFont("Lucida Grande", 11, SWT.NONE));
        m_lblNumReqRooms.setForeground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
        m_lblNonCcncelableIncrement.setFont(org.eclipse.wb.swt.SWTResourceManager.getFont("Lucida Grande", 11, SWT.NONE));
        m_lblNonCcncelableIncrement.setForeground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
        m_lblFamilyBreakfastCost.setFont(org.eclipse.wb.swt.SWTResourceManager.getFont("Lucida Grande", 11, SWT.NONE));
        m_lblFamilyBreakfastCost.setForeground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
    }
}