/**
 * 
 */
package com.jzb.futil;

import java.io.File;
import java.io.FileFilter;

/**
 * @author n000013
 * 
 */
public class FileExtFilter implements FileFilter {

    static public enum IncludeFolders {
        YES, NO
    }

    public static FileExtFilter imgFilter(IncludeFolders includeFolders) {
        return new FileExtFilter(includeFolders, "jpg", "gif", "png", "bmp", "cr2");
    }

    public static FileExtFilter folderFilter() {
        return new FileExtFilter(IncludeFolders.YES, (String[]) null);
    }

    private String         m_allowedExts[] = {};
    private IncludeFolders m_includeFolders;

    public FileExtFilter(IncludeFolders includeFolders, String... allowedExts) {
        m_includeFolders = includeFolders;

        m_allowedExts = allowedExts;
    }

    /**
     * @see java.io.FileFilter#accept(java.io.File)
     */
    public boolean accept(File file) {
        return (m_includeFolders == IncludeFolders.YES && file.isDirectory()) || checkFileExt(file);
    }

    private boolean checkFileExt(File file) {

        if (m_allowedExts == null || m_allowedExts.length == 0)
            return false;

        String ext = getFileExt(file);
        if (ext.length() == 0)
            return false;

        for (String s : m_allowedExts) {
            if (s.equalsIgnoreCase(ext))
                return true;
        }

        return false;
    }

    private String getFileExt(File file) {
        int pos = file.getName().lastIndexOf('.');
        if (pos > 0) {
            return file.getName().substring(pos + 1).toLowerCase();
        } else {
            return "";
        }
    }
}
