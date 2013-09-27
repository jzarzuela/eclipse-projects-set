/**
 * 
 */
package com.jzb.swt.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.jzb.util.Tracer;

/**
 * @author n000013
 * 
 */
public class TabbedTracerImpl extends Tracer {

    public CTabItem m_debugTab;
    public CTabItem m_errorTab;
    public CTabItem m_infoTab;
    public CTabItem m_warnTab;
    public Font     m_errorFont;

    public TabbedTracerImpl() {
        super(true);
    }

    public TabbedTracerImpl(CTabItem debug, CTabItem info, CTabItem warn, CTabItem error) {
        super(true);
        m_debugTab = debug;
        m_infoTab = info;
        m_warnTab = warn;
        m_errorTab = error;
    }

    @Override
    protected boolean _propagateLevels(Level l) {
        if ((l == Level.INFO && getTab(Level.INFO) != getTab(Level.DEBUG)) || (l == Level.ERROR && getTab(Level.ERROR) != getTab(Level.WARN))) {
            return true;
        } else {
            return false;
        }
    }

    protected void _showTraceText(final Level level, final StringBuffer sb) {

        Display.getDefault().asyncExec(new Runnable() {

            @SuppressWarnings("synthetic-access")
            public void run() {

                String fullMsg;

                synchronized (sb) {
                    fullMsg = sb.toString();
                    sb.setLength(0);
                }

                if (fullMsg.length() > 0) {
                    Text control = getTextControl(level);
                    control.append(fullMsg);

                    int lc = control.getLineCount() - 1;
                    control.setTopIndex(lc);
                    setTabTitle(level, lc);

                    if (level == Level.ERROR) {
                        CTabItem tb = getTab(level);
                        tb.getParent().setSelection(tb);
                    }
                }
            }
        });
    }

    protected void _reset() {

        super._reset();

        Display.getDefault().asyncExec(new Runnable() {

            @SuppressWarnings("synthetic-access")
            public void run() {
                setTabTitle(Level.DEBUG, 0);
                setTabTitle(Level.INFO, 0);
                setTabTitle(Level.WARN, 0);
                setTabTitle(Level.ERROR, 0);
                getTextControl(Level.DEBUG).setText("");
                getTextControl(Level.INFO).setText("");
                getTextControl(Level.WARN).setText("");
                getTextControl(Level.ERROR).setText("");
            }
        });

    }

    private CTabItem getTab(Level level) {
        switch (level) {
            case DEBUG:
                return m_debugTab;
            case INFO:
                return m_infoTab;
            case WARN:
                return m_warnTab;
            case ERROR:
                return m_errorTab;
            default:
                // by default we return "debug"
                return m_debugTab;

        }
    }

    private Text getTextControl(Level level) {
        return (Text) getTab(level).getControl();
    }

    private void setTabTitle(Level level, int count) {
        String str;
        switch (level) {
            case DEBUG:
                str = "Debug";
                break;
            case INFO:
                str = "Info";
                break;
            case WARN:
                str = "Warning";
                break;
            case ERROR:
                str = "Error";
                break;
            default:
                str = "Unknown";
                break;
        }
        if (count > 0) {
            str += "(" + count + ")";
        }

        CTabItem tb = getTab(level);
        tb.setText(str);
        if (level == Level.ERROR) {
            if (count == 0) {
                tb.setFont(Display.getDefault().getSystemFont());
            } else {
                tb.setFont(m_errorFont);
            }
        }

    }

    public CTabFolder createTabFolder(Composite cParent) {

        final CTabFolder tabTraces = new CTabFolder(cParent, SWT.BORDER);

        m_debugTab = new CTabItem(tabTraces, SWT.NONE);
        m_debugTab.setText("Debug");
        final Text txtDebug = new Text(tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        txtDebug.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        txtDebug.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtDebug.setEditable(false);
        m_debugTab.setControl(txtDebug);

        m_infoTab = new CTabItem(tabTraces, SWT.NONE);
        m_infoTab.setText("Info");
        final Text txtInfo = new Text(tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        txtInfo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        txtInfo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtInfo.setEditable(false);
        m_infoTab.setControl(txtInfo);

        m_warnTab = new CTabItem(tabTraces, SWT.NONE);
        m_warnTab.setText("Warning");
        final Text txtWarn = new Text(tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        txtWarn.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        txtWarn.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtWarn.setEditable(false);
        m_warnTab.setControl(txtWarn);

        m_errorTab = new CTabItem(tabTraces, SWT.NONE);
        m_errorTab.setText("Error");
        final Text txtError = new Text(tabTraces, SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        txtError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        txtError.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtError.setEditable(false);
        m_errorTab.setControl(txtError);

        m_errorTab.getControl().setFocus();
        tabTraces.setSelection(0);
        tabTraces.setFocus();

        m_errorFont = getFont("Arial", 12, SWT.BOLD, false, false);

        return tabTraces;
    }

    /**
     * Returns a font based on its name, height and style. Windows-specific strikeout and underline flags are also supported.
     * 
     * @param name
     *            String The name of the font
     * @param size
     *            int The size of the font
     * @param style
     *            int The style of the font
     * @param strikeout
     *            boolean The strikeout flag (warning: Windows only)
     * @param underline
     *            boolean The underline flag (warning: Windows only)
     * @return Font The font matching the name, height, style, strikeout and underline
     */
    private static Font getFont(String name, int size, int style, boolean strikeout, boolean underline) {

        Font font;

        FontData fontData = new FontData(name, size, style);
        if (strikeout || underline) {
            try {
                Class<?> logFontClass = Class.forName("org.eclipse.swt.internal.win32.LOGFONT"); //$NON-NLS-1$
                Object logFont = FontData.class.getField("data").get(fontData); //$NON-NLS-1$
                if (logFont != null && logFontClass != null) {
                    if (strikeout) {
                        logFontClass.getField("lfStrikeOut").set(logFont, new Byte((byte) 1)); //$NON-NLS-1$
                    }
                    if (underline) {
                        logFontClass.getField("lfUnderline").set(logFont, new Byte((byte) 1)); //$NON-NLS-1$
                    }
                }
            } catch (Throwable e) {
                System.err.println("Unable to set underline or strikeout" + " (probably on a non-Windows platform). " + e); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        font = new Font(Display.getCurrent(), fontData);

        return font;
    }
}
