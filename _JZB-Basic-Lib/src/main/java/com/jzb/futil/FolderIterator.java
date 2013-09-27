/**
 * 
 */
package com.jzb.futil;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class FolderIterator {

    private ArrayList<File> m_folders   = new ArrayList<File>();
    private IFileProcessor  m_fProcessor;
    private FIParameters    m_params    = new FIParameters();

    private boolean         m_recursive = true;

    public FolderIterator(IFileProcessor fProcessor, File folder1, File... otherFolders) {
        this(fProcessor, null, folder1, otherFolders);
    }

    public FolderIterator(IFileProcessor fProcessor, FIParameters params, File folder1, File... otherFolders) {
        m_fProcessor = fProcessor;
        if (folder1 != null)
            m_folders.add(folder1);
        for (File f : otherFolders) {
            if (f != null)
                m_folders.add(f);
        }
        if (params != null)
            m_params = params;

        m_fProcessor.setFolderIterator(this);
    }

    public FolderIterator(IFileProcessor fProcessor, FIParameters params, String folder1, String... otherFolders) {
        m_fProcessor = fProcessor;
        if (folder1 != null)
            m_folders.add(new File(folder1));
        for (String str : otherFolders) {
            if (str != null)
                m_folders.add(new File(str));
        }
        if (params != null)
            m_params = params;

        m_fProcessor.setFolderIterator(this);
    }

    public FolderIterator(IFileProcessor fProcessor, String folder1, String... otherFolders) {
        this(fProcessor, null, folder1, otherFolders);
    }

    /**
     * @return the fProcessor
     */
    public IFileProcessor getFileProcessor() {
        return m_fProcessor;
    }

    /**
     * @return the folders
     */
    public File[] getFolders() {
        return m_folders.toArray(new File[0]);
    }

    /**
     * @return the params
     */
    public FIParameters getParams() {
        return m_params;
    }

    /**
     * @return the recursive
     */
    public boolean isRecursive() {
        return m_recursive;
    }

    public void iterate() throws Exception {

        if (m_folders == null)
            return;

        for (File folder : m_folders) {
            _processFolder(true, folder, folder);
        }
    }

    /**
     * @param fProcessor
     *            the fProcessor to set
     */
    public void setFileProcessor(IFileProcessor fProcessor) {
        m_fProcessor = fProcessor;
    }

    /**
     * @param folders
     *            the folders to set
     */
    public void setFolders(File[] folders) {
        m_folders.clear();
        for (File f : folders) {
            m_folders.add(f);
        }
    }

    /**
     * @param folder
     *            the folder to set
     */
    public void setOneFolder(File folder) {
        m_folders.clear();
        m_folders.add(folder);
    }

    /**
     * @param folder
     *            the folder to set
     */
    public void setOneFolder(String strFolder) {
        m_folders.clear();
        m_folders.add(new File(strFolder));
    }

    /**
     * @param recursive
     *            the recursive to set
     */
    public void setRecursive(boolean recurse) {
        m_recursive = recurse;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("\nFolderIterator:");
        pw.println("  folders:              " + _toStrArray(getFolders()));
        pw.println("  recurse:              " + isRecursive());
        pw.println("  fileProcessor:        " + getFileProcessor());
        pw.println("  parameters: \n" + m_params.toString());
        pw.println();
        return sw.getBuffer().toString();
    }

    private boolean _hasToProccessFile(File f) {

        boolean matched;

        if (m_params.getFileExtensionFilters().size() > 0) {
            matched = false;
            for (Pattern p : m_params.m_fileExtensionFilters) {
                Matcher m = p.matcher(f.getName());
                if (m.matches()) {
                    matched = true;
                    break;
                }
            }
            if (!matched)
                return false;
        }

        if (m_params.getFileIncludeFilters().size() > 0) {
            matched = false;
            for (Pattern p : m_params.m_fileIncludeFilters) {
                Matcher m = p.matcher(f.getName());
                if (m.matches()) {
                    matched = true;
                    break;
                }
            }
            if (!matched)
                return false;
        }

        for (Pattern p : m_params.m_fileExcludeFilters) {
            Matcher m = p.matcher(f.getName());
            if (m.matches())
                return false;
        }

        return true;
    }

    private boolean _hasToProccessFolder(File folder) {

        boolean matched;

        if (m_params.m_folderIncludeFilters.size() > 0) {
            matched = false;
            for (Pattern p : m_params.m_folderIncludeFilters) {
                Matcher m = p.matcher(folder.getPath());
                if (m.matches()) {
                    matched = true;
                    break;
                }
                m = p.matcher(folder.getName());
                if (m.matches()) {
                    matched = true;
                    break;
                }
            }
            if (!matched)
                return false;
        }

        for (Pattern p : m_params.m_folderExcludeFilters) {
            Matcher m = p.matcher(folder.getPath());
            if (m.matches())
                return false;
            m = p.matcher(folder.getName());
            if (m.matches())
                return false;
        }

        return true;
    }

    private void _processFolder(boolean isBaseFolder, File folder, File baseFolder) throws Exception {

        Tracer._debug("\n******************************************************************************************************************************");
        if (!isBaseFolder && !_hasToProccessFolder(folder)) {
            Tracer._debug("--- Folder filtered out: '" + folder + "'\n");
            return;
        }

        Tracer._debug("*** Iterating folder: '" + folder + "'\n");
        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                if (m_recursive)
                    _processFolder(false, f, baseFolder);
            } else {
                if (m_fProcessor != null && _hasToProccessFile(f)) {
                    m_fProcessor.processFile(f, baseFolder);
                }
            }
        }
    }

    private StringBuffer _toStrArray(Object[] objs) {

        StringBuffer sb = new StringBuffer();
        boolean first = true;

        sb.append("[");
        if (objs == null) {
            sb.append("null");
        } else {
            for (Object o : objs) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append("'");
                sb.append(o.toString());
                sb.append("'");
                first = false;
            }
        }
        sb.append("]");
        return sb;
    }
}
