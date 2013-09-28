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
public class CheckCompoundNames extends BaseTask {

    // --------------------------------------------------------------------------------------------------------
    public CheckCompoundNames(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) {
        super(justChecking, baseFolder, recursive);
    }

    // --------------------------------------------------------------------------------------------------------
    public void checkNames() {

        try {
            _checkBaseFolder();
            _checkNames(m_baseFolder);
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private void _checkNames(File folder) throws Exception {

        Tracer._debug("");
        Tracer._debug("Checking file's compound names" + (m_recursive == RecursiveProcessing.YES ? " * RECURSIVELY * " : " ") + "in folder: '" + folder + "'");
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
            
            if(!NameComposer.isCompoundName(fName)) {
                Tracer._warn("File doesn't have a proper compound name: '" + fName + "'");
            }
            
        }

        // ---------------------------------------------
        // Iterate subfolders
        if (m_recursive == RecursiveProcessing.YES) {
            for (File sfolder : subFolders) {
                _checkNames(sfolder);
            }
        }

    }

}
