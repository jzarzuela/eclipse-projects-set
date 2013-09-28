/**
 * 
 */
package com.jzb.img.tsk;

import java.io.File;
import java.util.TreeSet;

import com.jzb.futil.FileExtFilter;
import com.jzb.futil.FileExtFilter.IncludeFolders;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class CleanName extends BaseTask {

    public static enum ForceClean {
        NO, YES
    }

    // --------------------------------------------------------------------------------------------------------
    public CleanName(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) {
        super(justChecking, baseFolder, recursive);
    }

    // --------------------------------------------------------------------------------------------------------
    public void cleanName(ForceClean force) {

        try {
            _checkBaseFolder();
            _cleanName(m_baseFolder, force);
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private void _cleanName(File folder, ForceClean force) throws Exception {

        Tracer._debug("");
        Tracer._debug("Cleaning file's names" + (m_recursive == RecursiveProcessing.YES ? " * RECURSIVELY * " : " ") + "in folder: '" + folder + "'");
        Tracer._debug("");

        // Gets just image files and folders
        File fList[] = folder.listFiles(FileExtFilter.imgFilter(IncludeFolders.YES));

        // Split files and folders to process them properly
        TreeSet<File> allFiles = new TreeSet<File>();
        TreeSet<File> subFolders = new TreeSet<File>();
        for (File fImg : fList) {
            if (fImg.isDirectory()) {
                subFolders.add(fImg);
            } else {
                allFiles.add(fImg);
            }
        }

        // ---------------------------------------------
        // Iterate files in subfolder
        for (File fImg : allFiles) {

            String fName = fImg.getName();
            String newName = null;

            if (force == ForceClean.NO) {
                m_nc.parse(fName);
                newName = m_nc.cleanName();
            } else {
                int p1, p2;
                newName = fName;
                p1 = fName.lastIndexOf("[");
                if (p1 >= 0) {
                    p2 = fName.lastIndexOf("]");
                    if (p2 >= 0) {
                        int p3 = fName.indexOf('.', p2);
                        if (p3 >= 0) {
                            newName = fName.substring(p1 + 1, p2) + fName.substring(p3);
                        } else {
                            newName = fName.substring(p1 + 1, p2);
                        }
                    }
                }
            }
            File newFile = new File(fImg.getParentFile(), newName);
            _renameFile(fImg, newFile);
        }

        // ---------------------------------------------
        // Iterate subfolders
        if (m_recursive == RecursiveProcessing.YES) {
            for (File sfolder : subFolders) {
                if (sfolder.isDirectory()) {
                    _cleanName(sfolder, force);
                }
            }
        }

    }
}
