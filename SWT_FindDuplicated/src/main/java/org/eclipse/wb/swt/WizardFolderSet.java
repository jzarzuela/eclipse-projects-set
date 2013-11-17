/**
 * 
 */
package org.eclipse.wb.swt;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;

/**
 * @author jzarzuela
 * 
 */
@SuppressWarnings("synthetic-access")
public class WizardFolderSet extends Composite {

    private Combo m_cmdSetName;
    private Table m_table;
    private Text  m_txtFolder;

    // ----------------------------------------------------------------------------------------------------
    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public WizardFolderSet(Composite parent, int style) {
        super(parent, style);
        setLayout(null);
        setBounds(0, 0, 490, 256);

        Label label = new Label(this, SWT.NONE);
        label.setText("Root Folder:");
        label.setBounds(10, 16, 75, 14);

        m_txtFolder = new Text(this, SWT.BORDER);
        m_txtFolder.setBounds(91, 13, 348, 19);

        Button button = new Button(this, SWT.NONE);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _selectFolderDialog(m_txtFolder);
            }
        });
        button.setText("...");
        button.setBounds(435, 10, 43, 28);

        Label lblSetName = new Label(this, SWT.NONE);
        lblSetName.setBounds(10, 42, 59, 14);
        lblSetName.setText("Set Name:");

        m_cmdSetName = new Combo(this, SWT.NONE);
        m_cmdSetName.setBounds(91, 38, 263, 22);

        Label label_1 = new Label(this, SWT.NONE);
        label_1.setText("Root Folder List:");
        label_1.setBounds(10, 80, 154, 14);

        Button button_1 = new Button(this, SWT.NONE);
        button_1.setText("-");
        button_1.setBounds(396, 73, 43, 28);

        Button button_2 = new Button(this, SWT.NONE);
        button_2.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _addFolder(m_cmdSetName.getText(), m_txtFolder.getText());
            }
        });
        button_2.setText("+");
        button_2.setBounds(435, 73, 43, 28);

        TableViewer tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
        m_table = tableViewer.getTable();
        m_table.setLinesVisible(true);
        m_table.setHeaderVisible(true);
        m_table.setBounds(10, 100, 468, 134);

        TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnSet = tableViewerColumn.getColumn();
        tblclmnSet.setWidth(146);
        tblclmnSet.setText("Set");

        TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnFolder = tableViewerColumn_1.getColumn();
        tblclmnFolder.setWidth(274);
        tblclmnFolder.setText("Folder");

    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    // ----------------------------------------------------------------------------------------------------
    private void _addFolder(String folderSetName, String folderName) {

        // Compruaba que hay datos
        if (folderSetName == null || folderSetName.trim().length() == 0 || folderName == null || folderName.trim().length() == 0) {
            return;
        }

        // Ajusta los valores
        folderSetName = folderSetName.trim();
        folderName = folderName.trim();

        // Solo añade los elementos si son nuevos
        for (TableItem ti : m_table.getItems()) {
            if (ti.getText(0).equalsIgnoreCase(folderSetName) && ti.getText(1).equalsIgnoreCase(folderName)) {
                return;
            }
        }

        // Añade la infomacion
        TableItem ti = new TableItem(m_table, 0);
        ti.setText(new String[] { folderSetName, folderName });
    }

    // ----------------------------------------------------------------------------------------------------
    private void _selectFolderDialog(Text txtFolder) {

        DirectoryDialog dd = new DirectoryDialog(this.getShell());
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
