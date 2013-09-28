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
public class RenameWithFolders extends BaseTask {

    // --------------------------------------------------------------------------------------------------------
    public RenameWithFolders(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) {
        super(justChecking, baseFolder, recursive);
    }

    // --------------------------------------------------------------------------------------------------------
    public void renameAsSubfolder() {

        try {
            _checkBaseFolder();
            _renameAsSubfolder(m_baseFolder);
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private void _renameAsSubfolder(File folder) throws Exception {

        Tracer._debug("");
        Tracer._debug("Renaming files" + (m_recursive == RecursiveProcessing.YES ? " * RECURSIVELY * " : " ") + "after their folder in: '" + folder + "'");
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
        if (!folder.equals(m_baseFolder)) {

            for (File fImg : allFiles) {

                String fName = fImg.getName();

                m_nc.parse(fName);

                int n = 1 + m_baseFolder.getAbsolutePath().length();
                String folderPath = folder.getAbsolutePath().substring(n);

                String pathParts[] = folderPath.split(SUBGROUP_MARKER);
                String groupNames[] = pathParts[0].split(File.separator);
                
                m_nc.clearGroupNames();
                for (String name : groupNames) {
                    if (name.length() > 2 && (name.charAt(1) == SUBGROUP_COUNTER_CHAR1 || name.charAt(1) == SUBGROUP_COUNTER_CHAR2)) {
                        name = name.substring(2);
                    }
                    m_nc.addGroupName(name);
                }
                
                
                if (pathParts.length > 1) {
                    String subgroupNames[] = pathParts[1].split(File.separator);
                    m_nc.clearSubgroupNames();
                    for (String name : subgroupNames) {
                        if (name.length() > 2 && (name.charAt(1) == SUBGROUP_COUNTER_CHAR1 || name.charAt(1) == SUBGROUP_COUNTER_CHAR2)) {
                            name = name.substring(2);
                        }
                        if (name.equals(SUBGROUP_NOTHING)) {
                            continue;
                        }
                        m_nc.addSubgroupName(name);
                    }
                }

                String newName = m_nc.compose();

                File newFile = new File(fImg.getParentFile(), newName);
                _renameFile(fImg, newFile);
            }

        }

        // ---------------------------------------------
        // Iterate subfolders
        if (m_recursive == RecursiveProcessing.YES) {
            for (File sfolder : subFolders) {
                if (sfolder.isDirectory()) {
                    _renameAsSubfolder(sfolder);
                }
            }
        }
    }
}
