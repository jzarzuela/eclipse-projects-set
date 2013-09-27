/**
 * 
 */
package com.jzb.futil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * @author n63636
 * 
 */
public class FIParameters {

    protected ArrayList<Pattern> m_fileExcludeFilters   = new ArrayList<Pattern>();
    protected ArrayList<Pattern> m_fileExtensionFilters = new ArrayList<Pattern>();
    protected ArrayList<Pattern> m_fileIncludeFilters   = new ArrayList<Pattern>();
    protected ArrayList<Pattern> m_folderExcludeFilters = new ArrayList<Pattern>();
    protected ArrayList<Pattern> m_folderIncludeFilters = new ArrayList<Pattern>();

    public FIParameters() {
    }

    public void addFileExcludeFilter(String filter) {
        m_fileExcludeFilters.add(Pattern.compile(filter, Pattern.CASE_INSENSITIVE));
    }

    public void addFileExtensionFilter(String filter) {
        m_fileExtensionFilters.add(Pattern.compile(filter, Pattern.CASE_INSENSITIVE));
    }

    public void addFileIncludeFilter(String filter) {
        m_fileIncludeFilters.add(Pattern.compile(filter, Pattern.CASE_INSENSITIVE));
    }

    public void addFolderExcludeFilter(String filter) {
        m_folderExcludeFilters.add(Pattern.compile(filter, Pattern.CASE_INSENSITIVE));
    }

    public void addFolderIncludeFilter(String filter) {
        m_folderIncludeFilters.add(Pattern.compile(filter, Pattern.CASE_INSENSITIVE));
    }

    /**
     * @return the fileExcludeFilters
     */
    public ArrayList<String> getFileExcludeFilters() {
        ArrayList<String> filters = new ArrayList<String>();
        for (Pattern p : m_fileExcludeFilters) {
            filters.add(p.pattern());
        }
        return filters;
    }

    /**
     * @return the fileExtensionFilters
     */
    public ArrayList<String> getFileExtensionFilters() {
        ArrayList<String> filters = new ArrayList<String>();
        for (Pattern p : m_fileExtensionFilters) {
            filters.add(p.pattern());
        }
        return filters;
    }

    /**
     * @return the fileIncludeFilters
     */
    public ArrayList<String> getFileIncludeFilters() {
        ArrayList<String> filters = new ArrayList<String>();
        for (Pattern p : m_fileIncludeFilters) {
            filters.add(p.pattern());
        }
        return filters;
    }

    /**
     * @return the folderExcludeFilters
     */
    public ArrayList<String> getFolderExcludeFilters() {
        ArrayList<String> filters = new ArrayList<String>();
        for (Pattern p : m_folderExcludeFilters) {
            filters.add(p.pattern());
        }
        return filters;
    }

    /**
     * @return the folderIncludeFilters
     */
    public ArrayList<String> getFolderIncludeFilters() {
        ArrayList<String> filters = new ArrayList<String>();
        for (Pattern p : m_folderIncludeFilters) {
            filters.add(p.pattern());
        }
        return filters;
    }

    /**
     * @param fileExcludeFilter
     *            the fileExcludeFilter to set
     */
    public void setFileExcludeFilter(String filter) {
        if (filter != null)
            setFileExcludeFilters(new String[] { filter });
    }

    /**
     * @param fileExcludeFilters
     *            the fileExcludeFilters to set
     */
    public void setFileExcludeFilters(String strFilters[]) {
        m_fileExcludeFilters.clear();
        if (strFilters != null)
            for (String filter : strFilters) {
                m_fileExcludeFilters.add(Pattern.compile(filter, Pattern.CASE_INSENSITIVE));
            }
    }

    /**
     * @param fileExtensionFilter
     *            the fileExtensionFilter to set
     */
    public void setFileExtensionFilter(String filter) {
        if (filter != null)
            setFileExtensionFilters(new String[] { filter });
    }

    /**
     * @param fileExtensionFilters
     *            the fileExtensionFilters to set
     */
    public void setFileExtensionFilters(String strFilters[]) {
        m_fileExtensionFilters.clear();
        if (strFilters != null)
            for (String str : strFilters) {
                String filter = ".*\\." + str + "$";
                m_fileExtensionFilters.add(Pattern.compile(filter, Pattern.CASE_INSENSITIVE));
            }
    }

    /**
     * @param fileIncludeFilter
     *            the fileIncludeFilter to set
     */
    public void setFileIncludeFilter(String filter) {
        if (filter != null)
            setFileIncludeFilters(new String[] { filter });
    }

    /**
     * @param fileIncludeFilters
     *            the fileIncludeFilters to set
     */
    public void setFileIncludeFilters(String strFilters[]) {
        m_fileIncludeFilters.clear();
        if (strFilters != null)
            for (String filter : strFilters) {
                m_fileIncludeFilters.add(Pattern.compile(filter, Pattern.CASE_INSENSITIVE));
            }
    }

    /**
     * @param folderExcludeFilter
     *            the folderExcludeFilter to set
     */
    public void setFolderExcludeFilter(String filter) {
        if (filter != null)
            setFolderExcludeFilters(new String[] { filter });
    }

    /**
     * @param folderExcludeFilters
     *            the folderExcludeFilters to set
     */
    public void setFolderExcludeFilters(String strFilters[]) {
        m_folderExcludeFilters.clear();
        if (strFilters != null)
            for (String filter : strFilters) {
                m_folderExcludeFilters.add(Pattern.compile(filter, Pattern.CASE_INSENSITIVE));
            }
    }

    /**
     * @param folderIncludeFilter
     *            the folderIncludeFilter to set
     */
    public void setFolderIncludeFilter(String filter) {
        if (filter != null)
            setFolderIncludeFilters(new String[] { filter });
    }

    /**
     * @param folderIncludeFilters
     *            the folderIncludeFilters to set
     */
    public void setFolderIncludeFilters(String strFilters[]) {
        m_folderIncludeFilters.clear();
        if (strFilters != null)
            for (String filter : strFilters) {
                m_folderIncludeFilters.add(Pattern.compile(filter, Pattern.CASE_INSENSITIVE));
            }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("    folderIncludeFilters: " + _toStrArray(getFolderIncludeFilters()));
        pw.println("    folderExcludeFilters: " + _toStrArray(getFolderExcludeFilters()));
        pw.println("    fileExtensionFilters: " + _toStrArray(getFileExtensionFilters()));
        pw.println("    fileIncludeFilters:   " + _toStrArray(getFileIncludeFilters()));
        pw.println("    fileExcludeFilters:   " + _toStrArray(getFileExcludeFilters()));
        return sw.getBuffer().toString();
    }

    private StringBuffer _toStrArray(ArrayList objs) {
        return _toStrArray(objs.toArray());
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
