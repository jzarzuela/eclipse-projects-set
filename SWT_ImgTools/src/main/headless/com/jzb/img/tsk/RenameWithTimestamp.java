/**
 * 
 */
package com.jzb.img.tsk;

import java.io.File;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jzb.futil.FileExtFilter;
import com.jzb.futil.FileExtFilter.IncludeFolders;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class RenameWithTimestamp extends BaseTask {

    private Pattern m_checkDateRE = Pattern.compile("\\$[0-9][0-9][0-9][0-9]=[0-9][0-9]=[0-9][0-9] [0-9][0-9]=[0-9][0-9]=[0-9][0-9]\\$\\**=");

    // --------------------------------------------------------------------------------------------------------
    public RenameWithTimestamp(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) {
        super(justChecking, baseFolder, recursive);
    }

    // --------------------------------------------------------------------------------------------------------
    public void addTimeDate(TimeStampShift shiftTimeStamp) {
        try {
            _checkBaseFolder();
            _addTimeDate(m_baseFolder, shiftTimeStamp);
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    public void removeTimeDate() {
        try {
            _checkBaseFolder();
            _removeTimeDate(m_baseFolder);
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private void _addTimeDate(File folder, TimeStampShift shiftTimeStamp) throws Exception {

        Tracer._debug("");
        Tracer._debug("Adding time-date offset" + (m_recursive == RecursiveProcessing.YES ? " * RECURSIVELY * " : " ") + "to file's name in folder: '" + folder + "'");
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
            String newName;

            // Check if was already renamed with TimeDate
            Matcher matcher = m_checkDateRE.matcher(fName);
            String timestampStr = _getExifDateStr(fImg, shiftTimeStamp, false);
            if (matcher.find()) {
                newName = timestampStr + fName.substring(NO_TIME_STR.length());
            } else {
                newName = timestampStr + fName;
            }

            File newFile = new File(fImg.getParentFile(), newName);
            _renameFile(fImg, newFile);
        }

        // ---------------------------------------------
        // Iterate subfolders
        if (m_recursive == RecursiveProcessing.YES) {
            for (File sfolder : subFolders) {
                if (sfolder.isDirectory()) {
                    _addTimeDate(sfolder, shiftTimeStamp);
                }
            }
        }

    }

    // --------------------------------------------------------------------------------------------------------
    private void _removeTimeDate(File folder) throws Exception {

        Tracer._debug("");
        Tracer._debug("Removing time-date offset" + (m_recursive == RecursiveProcessing.YES ? " * RECURSIVELY * " : " ") + "from file's name in folder: '" + folder + "'");
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

            // Check if wasn't already renamed
            Matcher matcher = m_checkDateRE.matcher(fName);
            if (!matcher.find()) {
                Tracer._debug("File '" + fImg.getName() + "' doesn't need to be processed");
                continue;
            }

            String newName = fName.substring(NO_TIME_STR.length());
            File newFile = new File(fImg.getParentFile(), newName);
            _renameFile(fImg, newFile);
        }

        // ---------------------------------------------
        // Iterate subfolders
        if (m_recursive == RecursiveProcessing.YES) {
            for (File sfolder : subFolders) {
                if (sfolder.isDirectory()) {
                    _removeTimeDate(sfolder);
                }
            }
        }

    }

}
