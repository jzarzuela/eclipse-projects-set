/**
 * 
 */
package com.jzb.vt;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.jzb.vt.model.VItemList;


/**
 * @author JZarzuela
 */
public class VCard {

    private Text m_TypedText;
    private Shell m_shell;

    private StyledText m_translatedText;

    private StyledText m_nativeText;

    private Button m_btnShow;

    private Button m_btnNextOK;

    private Button m_btnNextFailed;
    
    private Label m_lblTheme;

    private Color m_RED;

    private Font m_textFONT;

    private VItemList m_itemList;

    public VCard(VItemList itemList) {
        m_itemList=itemList;
    }

    public void open() {

        Display display = new Display();
        m_shell = createShell(display);

        showNextDataItem();

        while (!m_shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

        display.dispose();
    }

    private Shell createShell(Display display) {
        Shell shell = new Shell(SWT.MIN | SWT.CLOSE | SWT.TITLE | SWT.BORDER);
        shell.setImage(SWTResourceManager.getImage(VCard.class, "/Globe 2.ico"));
        shell.setSize(716, 330);
        Rectangle rect = display.getBounds();
        shell.setLocation((rect.width - shell.getSize().x) / 2, (rect.height - shell.getSize().y) / 2);
        shell.setText("VCard remainder");
        shell.open();

        m_RED = new Color(display, new RGB(255, 0, 0));
        m_textFONT = SWTResourceManager.getFont("Arial", 14, SWT.NONE);

        m_nativeText = new StyledText(shell, SWT.BORDER | SWT.WRAP);
        m_nativeText.setFont(m_textFONT);
        m_nativeText.setEnabled(false);
        m_nativeText.setEditable(false);
        m_nativeText.setWordWrap(true);
        m_nativeText.setBounds(13, 44, 686, 71);

        m_translatedText = new StyledText(shell, SWT.BORDER);
        m_translatedText.setFont(m_textFONT);
        m_translatedText.setEnabled(false);
        m_translatedText.setEditable(false);
        m_translatedText.setWordWrap(true);
        m_translatedText.setBounds(13, 125, 686, 71);

        m_btnShow = new Button(shell, SWT.NONE);
        m_btnShow.setText("Show");
        m_btnShow.setBounds(13, 260, 59, 23);
        m_btnShow.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                showSolution();
            }
        });

        m_btnNextOK = new Button(shell, SWT.NONE);
        m_btnNextOK.setEnabled(false);
        m_btnNextOK.setText("OK >");
        m_btnNextOK.setBounds(80, 260, 70, 23);
        m_btnNextOK.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                showNextDataItem(false);
            }
        });

        m_btnNextFailed = new Button(shell, SWT.NONE);
        m_btnNextFailed.setEnabled(false);
        m_btnNextFailed.setText("Failed >");
        m_btnNextFailed.setBounds(160, 260, 70, 23);
        m_btnNextFailed.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                showNextDataItem(true);
            }
        });

        shell.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent arg0) {
                m_RED.dispose();
                m_textFONT.dispose();
            }
        });

        final Button cancelButton = new Button(shell, SWT.NONE);
        cancelButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                m_shell.close();
            }
        });
        cancelButton.setText("Exit");
        cancelButton.setBounds(647, 260, 52, 23);

        final Button cbkDirectTranslation = new Button(shell, SWT.CHECK);
        cbkDirectTranslation.setSelection(m_itemList.isDirectTranslation());
        cbkDirectTranslation.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                m_itemList.setDirectTranslation(cbkDirectTranslation.getSelection());
                hideSolution();
                setText(m_nativeText, m_itemList.getDirectText());
            }
        });
        cbkDirectTranslation.setText("Reverse Translation");
        cbkDirectTranslation.setBounds(511, 263, 130, 16);

        m_TypedText = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP);
        m_TypedText.setBackground(SWTResourceManager.getColor(255, 255, 128));
        m_TypedText.setBounds(80, 207, 619, 37);

        final Label typeHereLabel = new Label(shell, SWT.NONE);
        typeHereLabel.setText("Type here:");
        typeHereLabel.setBounds(13, 219, 70, 16);

        final Label themeLabel = new Label(shell, SWT.NONE);
        themeLabel.setText("Theme:");
        themeLabel.setBounds(13, 12, 44, 16);

        m_lblTheme = new Label(shell, SWT.BORDER);
        m_lblTheme.setFont(SWTResourceManager.getFont("Arial", 14, SWT.BOLD));
        m_lblTheme.setBounds(70, 6, 629, 30);
        shell.layout();
        return shell;
    }

    private void showNextDataItem() {
        if (m_itemList.hasNextVitem()) {
            m_itemList.moveOnNextVItem();
            hideSolution();
            setText(m_nativeText, m_itemList.getDirectText());
            m_lblTheme.setText(m_itemList.getTheme());
            m_TypedText.setText("");
            m_TypedText.setFocus();
        } else {
            m_shell.close();
        }
    }

    private void showNextDataItem(boolean failed) {
        if (failed) {
            m_itemList.increaseFailed();
        }
        showNextDataItem();
    }

    private void hideSolution() {
        setText(m_translatedText, "");
        m_btnShow.setEnabled(true);
        m_btnNextOK.setEnabled(false);
        m_btnNextFailed.setEnabled(false);
    }

    private void showSolution() {
        setText(m_translatedText, m_itemList.getInvText());
        m_btnShow.setEnabled(false);
        m_btnNextOK.setEnabled(true);
        m_btnNextFailed.setEnabled(true);
        m_itemList.markAsRead();
    }

    private void setText(StyledText control, String text) {

        ArrayList list = new ArrayList();
        boolean marking = false;
        int p1 = 0;
        char buffer[] = new char[text.length()];
        text.getChars(0, buffer.length, buffer, 0);
        StringBuffer sb = new StringBuffer(buffer.length);
        for (int n = 0, p2 = 0; n < buffer.length; n++) {
            if (buffer[n] != '|') {
                p2++;
                sb.append(buffer[n]);
            } else {
                if (marking) {
                    list.add(new StyleRange(p1, p2 - p1, m_RED, null, SWT.BOLD));
                    marking = false;
                } else {
                    marking = true;
                    p1 = p2;
                }
            }
        }
        control.setText(sb.toString());
        StyleRange ranges[] = (StyleRange[]) list.toArray(new StyleRange[list.size()]);
        control.setStyleRanges(ranges);
    }
}
