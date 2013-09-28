/**
 * 
 */
package com.jzb.booking;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.jzb.booking.data.THotelData;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.graphics.Rectangle;

/**
 * @author jzarzuela
 * 
 */
@SuppressWarnings("synthetic-access")
public class HotelDetailWnd {

    private Shell       m_shell;

    private THotelData  m_hotelData;
    private ProgressBar m_progressBar;
    private ToolItem    m_tltmRefresh;
    private Browser     m_browser;
    private boolean     m_showProgress = false;

    // ----------------------------------------------------------------------------------------------------
    /**
     * @wbp.parser.entryPoint
     */
    public static void openNew(THotelData hotelData) {
        HotelDetailWnd wnd = new HotelDetailWnd(hotelData);
        wnd.open();
    }

    // ----------------------------------------------------------------------------------------------------
    public HotelDetailWnd(THotelData hotelData) {
        m_hotelData = hotelData;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Open the window.
     */
    public void open() {
        Display display = Display.getDefault();
        createContents();
        m_shell.open();
        m_shell.layout();
        _setWndPosition();
        
        while (!m_shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Create contents of the window.
     */
    protected void createContents() {
        m_shell = new Shell();
        m_shell.setSize(450, 300);
        m_shell.setText("Hotel details: " + m_hotelData.name);
        m_shell.setLayout(new BorderLayout(0, 0));

        ToolBar toolBar = new ToolBar(m_shell, SWT.FLAT | SWT.RIGHT);
        toolBar.setLayoutData(BorderLayout.NORTH);

        m_tltmRefresh = new ToolItem(toolBar, SWT.NONE);
        m_tltmRefresh.setText("Refresh");

        m_progressBar = new ProgressBar(m_shell, SWT.NONE);
        m_progressBar.setLayoutData(BorderLayout.SOUTH);

        m_browser = new Browser(m_shell, SWT.NONE);
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
                m_showProgress = false;
                m_progressBar.setSelection(0);
            }
        });
        m_browser.setLayoutData(BorderLayout.CENTER);
        m_browser.setUrl(m_hotelData.dataLink);
    }
    
    // ----------------------------------------------------------------------------------------------------
    private void _setWndPosition() {
        Rectangle r = Display.getDefault().getBounds();
        int w = (90 * r.width) / 100;
        int h = (80 * r.height) / 100;
        int x = (r.width - w) / 2;
        int y = (r.height - h) / 2;
        m_shell.setBounds(x, y, w, h);
    }
}
