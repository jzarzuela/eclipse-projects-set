/**
 * Rigel Services Model Infrastructure, Version 1.0
 *
 * Copyright (C) 2002 ISBAN.
 * All Rights Reserved.
 *
 **/

package com.jzb.vt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * @author PS00A501
 *
 */
public class HTMLViewer {

   
    /**
     * Open the window
     */
    public void open(String htmlText) {
        final Display display = Display.getDefault();
        final Shell htmlViewerShell = new Shell();
        htmlViewerShell.setImage(SWTResourceManager.getImage(HTMLViewer.class, "/Globe 2.ico"));
        htmlViewerShell.setLayout(new FillLayout());
        Rectangle rect = display.getBounds();
        int w=(int)(((double)rect.width) * 0.8);
        int h=(int)(((double)rect.height) * 0.8);
        htmlViewerShell.setLocation((rect.width - w) / 2, (rect.height - h) / 2);
        htmlViewerShell.setSize(w,h);
        htmlViewerShell.setText("HTML Viewer");
        

        htmlViewerShell.open();

        final Browser m_browser = new Browser(htmlViewerShell, SWT.NONE);
        htmlViewerShell.layout();

        final Menu menu = new Menu(htmlViewerShell, SWT.BAR);
        htmlViewerShell.setMenuBar(menu);

        final MenuItem mnu_print = new MenuItem(menu, SWT.NONE);
        mnu_print.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                m_browser.setUrl("javascript:print()");
            }
        });
        mnu_print.setText("Print");

        m_browser.setText(htmlText);        
        
        while (!htmlViewerShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }

}
