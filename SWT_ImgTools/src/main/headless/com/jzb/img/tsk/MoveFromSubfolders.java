/**
 * 
 */
package com.jzb.img.tsk;

import java.io.File;

import com.jzb.futil.FileExtFilter;
import com.jzb.futil.FileExtFilter.IncludeFolders;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class MoveFromSubfolders extends BaseTask {

    public static enum DeleteEmpty {
        NO, YES
    }

    // --------------------------------------------------------------------------------------------------------
    public MoveFromSubfolders(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) {
        super(justChecking, baseFolder, recursive);
    }

    // --------------------------------------------------------------------------------------------------------
    public void moveFromSubfolder(DeleteEmpty deleteEmpty) {
        try {
            _checkBaseFolder();
            _moveFromSubfolder(m_baseFolder, deleteEmpty);
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private void _moveFromSubfolder(File folder, DeleteEmpty deleteEmpty) throws Exception {

        // ---------------------------------------------
        // Iterate subfolders
        if (m_recursive == RecursiveProcessing.YES || folder.equals(m_baseFolder)) {
            File fList[] = folder.listFiles(FileExtFilter.folderFilter());
            for (File sfolder : fList) {
                _moveFromSubfolder(sfolder, deleteEmpty);
            }
        }

        Tracer._debug("");
        Tracer._debug("Moving files" + (m_recursive == RecursiveProcessing.YES ? " * RECURSIVELY * " : " ") + "from subfolders in folder: '" + folder + "'");
        Tracer._debug("");

        // ---------------------------------------------
        // Iterate files in subfolder
        if (!folder.equals(m_baseFolder)) {
            File fList[] = folder.listFiles(FileExtFilter.imgFilter(IncludeFolders.NO));
            for (File fImg : fList) {
                String fName = fImg.getName();
                String newName = ".." + File.separator + fName;
                File newFile = new File(fImg.getParentFile(), newName);
                _renameFile(fImg, newFile);
            }

            if (deleteEmpty == DeleteEmpty.YES) {
                _deleteMacDotFiles(folder);
                if (folder.listFiles() != null && folder.listFiles().length == 0) {
                    _deleteFolder(folder, m_justChecking);
                }
            }
        }

    }

}
