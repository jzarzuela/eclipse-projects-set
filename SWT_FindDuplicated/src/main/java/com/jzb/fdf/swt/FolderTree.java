/**
 * 
 */
package com.jzb.fdf.swt;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.jzb.fdf.srvc.IFileSrvc;
import com.jzb.fdf.srvc.IFolderSrvc;
import com.jzb.fdf.srvc.SFile;
import com.jzb.fdf.srvc.SFolder;
import com.jzb.util.Tracer;

import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;

import swing2swt.layout.BorderLayout;

/**
 * @author jzarzuela
 * 
 */
@SuppressWarnings("synthetic-access")
public class FolderTree extends Composite {

    private static enum PROCESSING_TYPE {
        FILTER, CLEAN
    }

    private TableViewer m_tableViewer;
    private TreeViewer  m_treeViewer;
    private Text        m_txtSelectedPath;
    private Composite   m_compSelectedPath;
    private Button      m_btnOpenFolder;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public FolderTree(Composite parent, int style) {
        super(parent, style);
        addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(ControlEvent e) {
                _adjustSize();
            }
        });
        setLayout(null);

        Label lblFolderCmds = new Label(this, SWT.NONE);
        lblFolderCmds.setBounds(10, 10, 59, 14);
        lblFolderCmds.setText("Folder:");

        Button btnFolderFilter = new Button(this, SWT.FLAT);
        btnFolderFilter.setToolTipText("Filter Folder");
        btnFolderFilter.setImage(SWTResourceManager.getImage(FolderTree.class, "/filter.png"));
        btnFolderFilter.setBounds(20, 30, 75, 22);
        btnFolderFilter.setText("Filter");
        btnFolderFilter.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _cmd_processFolder(PROCESSING_TYPE.FILTER);
            }
        });

        Button btnFolderClean = new Button(this, SWT.FLAT);
        btnFolderClean.setToolTipText("Clean Folder");
        btnFolderClean.setText("Clean");
        btnFolderClean.setImage(SWTResourceManager.getImage(FolderTree.class, "/erase.png"));
        btnFolderClean.setBounds(20, 58, 75, 22);
        btnFolderClean.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _cmd_processFolder(PROCESSING_TYPE.CLEAN);
            }
        });

        Label lblFile = new Label(this, SWT.NONE);
        lblFile.setBounds(10, 90, 59, 14);
        lblFile.setText("File:");

        Button btnFileFilter = new Button(this, SWT.FLAT);
        btnFileFilter.setToolTipText("Filter File");
        btnFileFilter.setImage(SWTResourceManager.getImage(FolderTree.class, "/filter.png"));
        btnFileFilter.setText("Filter");
        btnFileFilter.setBounds(20, 110, 75, 22);
        btnFileFilter.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _cmd_processFile(PROCESSING_TYPE.FILTER);
            }
        });

        Button btnFileClean = new Button(this, SWT.FLAT);
        btnFileClean.setToolTipText("Clean File");
        btnFileClean.setImage(SWTResourceManager.getImage(FolderTree.class, "/erase.png"));
        btnFileClean.setText("Clean");
        btnFileClean.setBounds(20, 138, 75, 22);
        btnFileClean.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _cmd_processFile(PROCESSING_TYPE.CLEAN);
            }
        });

        m_treeViewer = new TreeViewer(this, SWT.BORDER | SWT.VIRTUAL);
        m_treeViewer.setUseHashlookup(true);
        Tree tree = m_treeViewer.getTree();
        tree.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _treeItemSelected();
            }
        });
        tree.setBounds(105, 57, 335, 103);

        m_tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        m_tableViewer.setUseHashlookup(true);
        Table table = m_tableViewer.getTable();
        table.setBounds(10, 166, 430, 169);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                _tableDoblecliked();
            }
        });

        TableColumn tcolName = new TableColumn(table, SWT.LEFT);
        tcolName.setWidth(100);
        tcolName.setText("Name");

        TableColumn tcolFolder = new TableColumn(table, SWT.LEFT);
        tcolFolder.setWidth(300);
        tcolFolder.setText("Folder");

        TableColumn tcolHashing = new TableColumn(table, SWT.LEFT);
        tcolHashing.setWidth(100);
        tcolHashing.setText("Hashing");

        // Prepara las columna para ser ordenables
        table.setSortColumn(tcolName);
        table.setSortDirection(SWT.DOWN);

        m_compSelectedPath = new Composite(this, SWT.BORDER);
        m_compSelectedPath.setBounds(105, 30, 296, 22);
        m_compSelectedPath.setLayout(new BorderLayout(1, 1));

        m_txtSelectedPath = new Text(m_compSelectedPath, SWT.NONE);
        m_txtSelectedPath.setEditable(false);
        m_txtSelectedPath.setEnabled(true);
        m_txtSelectedPath.setLayoutData(BorderLayout.CENTER);
        m_txtSelectedPath.setLocation(0, 4);
        m_txtSelectedPath.setSize(300, 14);
        m_txtSelectedPath.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
        m_txtSelectedPath.setText("");

        m_btnOpenFolder = new Button(this, SWT.FLAT);
        m_btnOpenFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _openFolder();
            }
        });
        m_btnOpenFolder.setLayoutData(BorderLayout.EAST);
        m_btnOpenFolder.setText("");
        m_btnOpenFolder.setToolTipText("Open Folder");
        m_btnOpenFolder.setImage(SWTResourceManager.getImage(FolderTree.class, "/openFolder.png"));
        m_btnOpenFolder.setBounds(407, 30, 33, 22);

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
                        table.setSortDirection(SWT.DOWN);
                    }
                    m_tableViewer.refresh();
                }
            });
        }

    }

    // ----------------------------------------------------------------------------------------------------
    public void initProviders() {

        IContentProvider trcprov = new FolderTreeContentProvider(m_treeViewer);
        m_treeViewer.setContentProvider(trcprov);

        ILabelProvider trlprov = new FolderTreeLabelProvider();
        m_treeViewer.setLabelProvider(trlprov);

        IStructuredContentProvider tbcprov = new FileTableContentProvider();
        m_tableViewer.setContentProvider(tbcprov);

        ITableLabelProvider tblprov = new FileTableLabelProvider(m_tableViewer);
        m_tableViewer.setLabelProvider(tblprov);

        ViewerComparator tbComp = new FileTableComparator();
        m_tableViewer.setComparator(tbComp);

        // Consigue los datos iniciales
        _fetchInput();
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    // ----------------------------------------------------------------------------------------------------
    private void _adjustSize() {

        Point thisSize = this.getSize();
        Rectangle treeBnds = m_treeViewer.getTree().getBounds();
        Rectangle tblBnds = m_tableViewer.getTable().getBounds();

        m_treeViewer.getTree().setSize(Math.max(0, thisSize.x - treeBnds.x - 10), treeBnds.height);
        m_tableViewer.getTable().setSize(Math.max(0, thisSize.x - tblBnds.x - 10), Math.max(0, thisSize.y - 10 - tblBnds.y));
        m_btnOpenFolder.setLocation(thisSize.x - m_btnOpenFolder.getSize().x - 10, m_btnOpenFolder.getLocation().y);

        m_compSelectedPath.setSize(m_treeViewer.getTree().getSize().x - m_btnOpenFolder.getSize().x - 4, m_compSelectedPath.getSize().y);

    }

    // ----------------------------------------------------------------------------------------------------

    private void _cmd_processFile(PROCESSING_TYPE processing) {

        TableItem selection[] = m_tableViewer.getTable().getSelection();
        if (selection == null || selection.length == 0)
            return;

        // Se asegura de que se quiere mover el fichero
        if (processing.equals(PROCESSING_TYPE.CLEAN) && !_reallyWantToRemove())
            return;

        // Itera los ficheros seleccionados filtrandolos como duplicados
        for (TableItem item : selection) {
            SFile sfile = (SFile) item.getData();
            if (processing.equals(PROCESSING_TYPE.FILTER)) {
                IFileSrvc.inst.filterDuplicateById(sfile.getId());
            } else {
                IFileSrvc.inst.cleanById(sfile.getId());
            }
        }

        // Recuerda la carpeta que estaba seleccionada
        String folderFName = (String) m_tableViewer.getData("#folderName#");

        // Filtrar un fichero afecta a su carpeta y puede afectar a otras carpetas
        // Luego reestablece la informacion desde el almacen
        _fetchInput();

        // Reselecciona, si sigue existiendo, la misma carpeta que antes lo estaba o algun padre
        Path folderPath = Paths.get(folderFName);
        while (folderPath != null) {
            if (_selectFolderByFName(folderPath.toString()+File.separator))
                break;
            folderPath = folderPath.getParent();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _cmd_processFolder(PROCESSING_TYPE processing) {

        TreeItem selection[] = m_treeViewer.getTree().getSelection();
        if (selection == null || selection.length == 0)
            return;

        // Se asegura de que se quiere mover la carpeta
        if (processing.equals(PROCESSING_TYPE.CLEAN) && !_reallyWantToRemove())
            return;

        // Consigue la carpeta seleccionada
        SFolder sfolder = (SFolder) selection[0].getData();

        // Recuerda el nombre de la carpeta para seleccionar el padre que perdure
        String folderFName = sfolder.getFullName();

        // La filtra como duplicada en el modelo
        if (processing.equals(PROCESSING_TYPE.FILTER)) {
            IFolderSrvc.inst.filterDuplicateById(sfolder.getId());
        } else {
            IFolderSrvc.inst.cleanById(sfolder.getId());
        }

        // Filtrar una carpeta (marcando sus ficheros como no duplicados) puede afectar a otras carpetas
        _fetchInput();

        // Reselecciona, si sigue existiendo, la misma carpeta que antes lo estaba o algun padre
        Path folderPath = Paths.get(folderFName);
        while (folderPath != null) {
            if (_selectFolderByFName(folderPath.toString()+File.separator))
                break;
            folderPath = folderPath.getParent();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private boolean _reallyWantToRemove() {

        MessageBox dialog = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
        dialog.setText("Clean element");
        dialog.setMessage("Cleaning and element will move it to a special folder (_#DupFiles#_) and will remove its information.\n\nDo you really want to do this?");
        boolean value = dialog.open() == SWT.OK;
        return value;
    }

    // ----------------------------------------------------------------------------------------------------
    private void _fetchInput() {

        // Consigue los datos del almacen
        List<SFolder> list = IFolderSrvc.inst.getRootFolders(true);
        m_treeViewer.getTree().setItemCount(list.size());
        m_treeViewer.setInput(list);
        m_txtSelectedPath.setText("");
    }

    // ----------------------------------------------------------------------------------------------------
    private SFolder _searchFolderInTree(TreeItem items[], final String folderName) {

        ILazyTreeContentProvider cp = (ILazyTreeContentProvider) m_treeViewer.getContentProvider();

        // Itera la lista de items pasada buscado aquel que coincide con el nombre indicado
        for (int n = 0; n < items.length; n++) {

            TreeItem item = items[n];

            // Consigue el SFolder asociado, ajustandolo si aun no se habia cargado el nodo
            SFolder sfolder = (SFolder) item.getData();
            if (sfolder == null) {
                if (item.getParentItem() != null && item.getParentItem().getData() != null) {
                    cp.updateElement(item.getParentItem().getData(), n);
                    sfolder = (SFolder) item.getData();
                } else {
                    throw new RuntimeException("Parent TreeNode not expected to be null or without data");
                }
            }

            // Si es el buscado termina
            if (folderName.equals(sfolder.getFullName())) {
                return sfolder;
            }

            // Si es un nodo "padre" del buscado itera por sus nodos hijos
            if (folderName.startsWith(sfolder.getFullName())) {

                // Si aun no se habia cargado, la cuenta del numero de nodos hijo estara mal y hay que establecerla
                if (item.getItemCount() != sfolder.getSubfoldersCount()) {
                    cp.updateChildCount(sfolder, 0);
                }

                return _searchFolderInTree(item.getItems(), folderName);
            }
        }

        // No lo ha encontrado
        return null;
    }

    // ----------------------------------------------------------------------------------------------------
    private boolean _selectFolderByFName(String folderFName) {

        SFolder selectedFolder = null;

        // Busca el elemento solicitado
        if (folderFName != null) {
            TreeItem items[] = m_treeViewer.getTree().getItems();
            selectedFolder = _searchFolderInTree(items, folderFName);
        }

        // Si lo ha encotrado, lo selecciona y ajusta la tabla de ficheros
        if (selectedFolder != null) {
            TreePath tpath = new TreePath(new Object[] { selectedFolder });
            m_treeViewer.setSelection(new TreeSelection(tpath), true);
            _treeItemSelected();
            return true;
        } else {
            // Si no lo encontro deselecciona lo anterior
            m_treeViewer.getTree().deselectAll();
            m_tableViewer.setData("#folderName#", "");
            m_tableViewer.getTable().removeAll();
            return false;
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _tableDoblecliked() {

        // Selecciona la carpeta asociada al fichero indicado
        int rowIndex = m_tableViewer.getTable().getSelectionIndex();
        SFile sfile = (SFile) m_tableViewer.getElementAt(rowIndex);
        _selectFolderByFName(sfile.getFolderName());
    }

    // ----------------------------------------------------------------------------------------------------
    private void _openFolder() {

        String folderName = m_txtSelectedPath.getText();
        if (folderName != null && folderName.length() > 0) {
            try {
                File folder = new File(folderName);
                Runtime.getRuntime().exec("/usr/bin/open .", null, folder);
            } catch (Exception ex) {
                Tracer._error("Error opening folder", ex);
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _treeItemSelected() {

        SFolder folder = null;

        TreeItem[] selection = m_treeViewer.getTree().getSelection();
        if (selection != null && selection.length > 0) {
            folder = (SFolder) selection[0].getData();
        }

        if (folder == null || !folder.hasDuplicatedFiles()) {
            m_tableViewer.setData("#folderName#", "");
            m_tableViewer.setItemCount(0);
            m_tableViewer.setInput(null);
        } else {
            HashSet<SFile> allFiles = new HashSet();
            List<SFile> list = IFileSrvc.inst.getForFolder(folder.getId(), true);
            for (SFile sfile : list) {
                List<SFile> hashingList = IFileSrvc.inst.getByHashing(sfile.getHashing());
                allFiles.addAll(hashingList);
            }
            m_tableViewer.setData("#folderName#", folder.getFullName());
            m_tableViewer.setInput(allFiles);
            m_tableViewer.getTable().deselectAll();
            for (TableColumn column : m_tableViewer.getTable().getColumns())
                column.pack();
        }

        if (folder != null) {
            m_txtSelectedPath.setText(folder.getFullName());
        }

    }
}
