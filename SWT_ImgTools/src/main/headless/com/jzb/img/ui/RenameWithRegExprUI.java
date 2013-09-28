/**
 * 
 */
package com.jzb.img.ui;

import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.jzb.img.tsk.NameComposer;
import com.jzb.img.tsk.RenameWithRegExpr;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * @author jzarzuela
 * 
 */
public class RenameWithRegExprUI extends BaseUI {

    private Label m_lblResult;
    private Text  m_txtExample;
    private Text  m_txtRegExpr;
    private Text  m_txtReplacement;

    // --------------------------------------------------------------------------------------------------------
    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public RenameWithRegExprUI(Composite parent, int style) {

        super(parent, style);

        Label lblCounter = new Label(this, SWT.NONE);
        lblCounter.setBounds(10, 5, 56, 14);
        lblCounter.setText("Reg Expr:");

        m_txtRegExpr = new Text(this, SWT.BORDER);
        m_txtRegExpr.addModifyListener(new ModifyListener() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void modifyText(ModifyEvent e) {
                _evaluateRegExpr();
            }
        });
        m_txtRegExpr.setBounds(68, 3, 330, 19);

        Button btnSearch = new Button(this, SWT.NONE);
        btnSearch.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executePatterns();
            }
        });
        btnSearch.setText("Patterns");
        btnSearch.setBounds(177, 50, 112, 28);

        Label lblReplacement = new Label(this, SWT.NONE);
        lblReplacement.setText("Replacement:");
        lblReplacement.setBounds(424, 5, 81, 14);

        m_txtReplacement = new Text(this, SWT.BORDER);
        m_txtReplacement.addModifyListener(new ModifyListener() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void modifyText(ModifyEvent e) {
                _evaluateRegExpr();
            }
        });
        m_txtReplacement.setBounds(503, 3, 330, 19);

        Label lblExample = new Label(this, SWT.NONE);
        lblExample.setText("Example:");
        lblExample.setBounds(10, 27, 56, 14);

        m_txtExample = new Text(this, SWT.BORDER);
        m_txtExample.addModifyListener(new ModifyListener() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void modifyText(ModifyEvent e) {
                _evaluateRegExpr();
            }
        });
        m_txtExample.setBounds(68, 25, 330, 19);

        m_lblResult = new Label(this, SWT.BORDER);
        m_lblResult.setBounds(503, 25, 330, 19);

        Label label_1 = new Label(this, SWT.NONE);
        label_1.setAlignment(SWT.CENTER);
        label_1.setText("-->");
        label_1.setBounds(414, 27, 81, 14);

        Button btnCompound = new Button(this, SWT.NONE);
        btnCompound.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeCompount();
            }
        });
        btnCompound.setText("Try Compound");
        btnCompound.setBounds(68, 50, 112, 28);

        Button btnRegExpr = new Button(this, SWT.NONE);
        btnRegExpr.addSelectionListener(new SelectionAdapter() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                _executeRegExpr();
            }
        });
        btnRegExpr.setText("RegExpr");
        btnRegExpr.setBounds(286, 50, 112, 28);

    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskDescription() {
        String description = "";
        description += "<p><b>Compound:</b> Renames files using the closest compound name.<br/>";
        description += "<b>Patterns:</b> Tries to find patterns in the file's names to help with the renaming process.<br/>";
        description += "<b>RegExpr:</b> Renames files using the given RegExpr <b><i>(@ means 'digit')</i></b> and Replacement values.</p>";
        description += "<table>";
        description += "<tr><td><b>Example:</b></td><td>Example to be used with given RegExpr and Replacement values</td><td>&nbsp;&nbsp;</td><td>Dublin_00005-Calle-IMGP1413</td></tr>";
        description += "<tr><td><b>Reg Expr:</b></td><td>Regular Expresion, with (groups), to be used when parsing file's names.</td><td>&nbsp;&nbsp;</td><td>(^@*)_(@@@@@)-(.*)-(IMG.*@@@@)</td></tr>";
        description += "<tr><td><b>Replacement:</b></td><td>Replacement value, with <b>'\\n'</b>, to be used when parsin file's names.</td><td>&nbsp;&nbsp;</td><td>\\1-#\\2-\\3_[\\4]</td></tr>";
        description += "</table>";
        return description;
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    public String getTaskName() {
        return "Rename with RegExpr";
    }

    // --------------------------------------------------------------------------------------------------------
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    // --------------------------------------------------------------------------------------------------------
    private void _evaluateRegExpr() {
        try {
            String regExpr = m_txtRegExpr.getText().trim();
            String replacement = m_txtReplacement.getText().trim();
            String example = m_txtExample.getText().trim();

            if (regExpr != null && regExpr.length() > 0 && replacement != null && replacement.length() > 0 && example != null && example.length() > 0) {
                String result = null;
                regExpr = regExpr.replaceAll("\\^@", "[\\^0-9]");
                regExpr = regExpr.replaceAll("@", "[0-9]");
                Pattern pt = Pattern.compile(regExpr);
                if (pt.matcher(example).matches()) {
                    result = RenameWithRegExpr.getReplacementName(replacement, pt, example);
                    if (result.indexOf(".") < 0) {
                        result += ".jpg";
                    }
                }
                if (result == null || !NameComposer.isCompoundName(result)) {
                    if (result == null) {
                        m_lblResult.setText("*NOMATCH* " + example);
                    } else {
                        m_lblResult.setText(result);
                    }
                    m_lblResult.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
                } else {
                    m_lblResult.setText(result);
                    m_lblResult.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
                }
            } else if ((regExpr == null || regExpr.length() == 0) && (replacement == null || replacement.length() == 0) && example != null && example.length() > 0) {
                String result = RenameWithRegExpr.tryToCalcCompoundName(example, "").compose();

                m_lblResult.setText(result);
                m_lblResult.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_YELLOW));
            } else {
                m_lblResult.setText("");
                m_lblResult.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
            }
        } catch (Throwable th) {
            m_lblResult.setText("!!!");
            m_lblResult.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executeCompount() {

        final RenameWithRegExpr task = new RenameWithRegExpr(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.renameTryCompoundName();
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executePatterns() {

        final RenameWithRegExpr task = new RenameWithRegExpr(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.searchPatterns();
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }

    // --------------------------------------------------------------------------------------------------------
    private void _executeRegExpr() {

        final String regExpr = m_txtRegExpr.getText().trim();
        final String replacement = m_txtReplacement.getText().trim();

        final RenameWithRegExpr task = new RenameWithRegExpr(getTaskWnd().getJustCheck(), getTaskWnd().getBaseFolder(), getTaskWnd().getRecursiveProcessing());
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                task.renameByRegExpr(regExpr, replacement);
            }
        };
        getTaskWnd().runTask(getTaskName(), runner);
    }

}
