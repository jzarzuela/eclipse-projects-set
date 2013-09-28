/**
 * 
 */
package com.jzb.img.tsk;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jzarzuela
 * 
 */
public class NameComposer {

    //
    // {} --> Optional part
    // {groupName-}#00000{-subgroupname}{_name}_[ImgName].ext
    //
    public static final Pattern          s_compoundNameRE = Pattern.compile("([^-]*-)*(#[0-9][0-9][0-9][0-9][0-9])(-[^-]*)*(_[^_]*)?(_\\[.*\\])\\..+", Pattern.CASE_INSENSITIVE);
    public static final String           s_timestampNONE  = "$0000=00=00 00=00=00$";
    public static final Pattern          s_timestampRE    = Pattern.compile("\\$[0-9][0-9][0-9][0-9]=[0-9][0-9]=[0-9][0-9] [0-9][0-9]=[0-9][0-9]=[0-9][0-9]\\$\\*?=", Pattern.CASE_INSENSITIVE);
    public static final SimpleDateFormat s_timestampSDF   = new SimpleDateFormat("$yyyy=MM=dd HH=mm=ss$");

    private static final DecimalFormat   s_indexDF        = new DecimalFormat("'#'00000");

    private String                       m_fExt           = "";
    private String                       m_forcedName     = null;
    private ArrayList<String>            m_groupNames     = new ArrayList<String>();
    private String                       m_imgFileName    = "";
    
    private int                          m_index          = 0;
    private String                       m_name           = null;
    private ArrayList<String>            m_subgroupNames  = new ArrayList<String>();
    private String                       m_timestamp      = null;

    // --------------------------------------------------------------------------------------------------------
    public static boolean isCompoundName(String name) {

        Matcher ts = s_timestampRE.matcher(name);
        boolean hasTimestamp = ts.find();
        if (hasTimestamp) {
            name = name.substring(0, ts.start()) + name.substring(ts.end());
        }
        boolean compound = s_compoundNameRE.matcher(name).matches();
        return compound;
    }

    // --------------------------------------------------------------------------------------------------------
    public NameComposer() {
        this(null);
    }

    // --------------------------------------------------------------------------------------------------------
    public NameComposer(String forcedName) {
        m_forcedName = forcedName;
    }

    // --------------------------------------------------------------------------------------------------------
    public void addGroupName(String groupName) {
        groupName = _cleanName(groupName);
        if (groupName != null && groupName.length() > 0) {
            m_groupNames.add(groupName);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    public void addSubgroupName(String subgroupName) {
        subgroupName = _cleanName(subgroupName);
        if (subgroupName != null && subgroupName.length() > 0) {
            m_subgroupNames.add(subgroupName);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    public String cleanName() {
        return m_imgFileName + m_fExt;
    }

    // --------------------------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------
    public String compose() {

        if (m_forcedName != null) {
            return m_forcedName;
        }

        StringBuilder sb = new StringBuilder();

        // Composes Timestamp part
        if (m_timestamp != null) {
            sb.append(m_timestamp);
        }

        // Composes GroupNames part
        for (String groupName : m_groupNames) {
            sb.append(groupName).append('-');
        }

        // Composes Index part
        sb.append(s_indexDF.format(m_index));

        // Composes subgroupNames part
        for (String subgroupName : m_subgroupNames) {
            sb.append('-').append(subgroupName);
        }

        // Composes Name part
        if (m_name != null && m_name.length() > 0) {
            sb.append('_').append(m_name);
        }

        // Composes ImgFileName part
        sb.append('_').append("[").append(m_imgFileName).append("]");

        // Composes File Extension part
        sb.append(m_fExt);

        return sb.toString();
    }

    // --------------------------------------------------------------------------------------------------------
    public void emptyName() {
        m_name = null;
    }

    // --------------------------------------------------------------------------------------------------------
    public ArrayList<String> getGroupNames() {
        return m_groupNames;
    }

    // --------------------------------------------------------------------------------------------------------
    public int getIndex() {
        return m_index;
    }

    // --------------------------------------------------------------------------------------------------------
    /**
     * @return the imgFileName
     */
    public String getImgFileName() {
        return m_imgFileName;
    }

    // --------------------------------------------------------------------------------------------------------
    public String getName() {
        return m_name;
    }

    // --------------------------------------------------------------------------------------------------------
    public ArrayList<String> getSubgroupNames() {
        return m_subgroupNames;
    }

    // --------------------------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------
    public void parse(String compoundName) throws ParseException {

        // clear previous values
        resetValues();

        try {

            // Strips out any timestamp that the name could have
            compoundName = _stripTimestamp(compoundName);

            // Processes the name
            if (!isCompoundName(compoundName)) {
                // Finds the File Extension part
                int p1 = compoundName.lastIndexOf('.');
                if (p1 >= 0) {
                    setImgFileName(compoundName.substring(0, p1));
                    setFileExt(compoundName.substring(p1));
                } else {
                    setImgFileName(compoundName);
                }

            } else {

                String strValue;

                // Parses the ImgFileName part
                int p1 = compoundName.lastIndexOf('[');
                int p2 = compoundName.lastIndexOf(']');
                strValue = compoundName.substring(p1 + 1, p2);
                setImgFileName(strValue);

                // Parses the File Extension part
                strValue = compoundName.substring(p2 + 1);
                setFileExt(strValue);

                // Parses Index part
                int p3 = compoundName.indexOf('#');
                strValue = compoundName.substring(p3 + 1, p3 + 6);
                _parseIndex(strValue);

                // Parses GroupNames part
                strValue = compoundName.substring(0, p3);
                _parseGroupNames(strValue);

                // Splits SubgroupNames and Name
                String parts[] = compoundName.substring(p3 + 6, p1).split("_");

                // Parses SubgroupNames part
                if (parts.length > 0) {
                    _parseSubgroupNames(parts[0]);
                }

                // Parses Name part
                if (parts.length > 1) {
                    setName(parts[1]);
                }
            }

        } catch (Exception ex) {
            throw new ParseException("Invalid composed name format ('" + compoundName + "'): " + ex.getClass().getName() + " - " + ex.getMessage(), 0);
        }

    }

    // --------------------------------------------------------------------------------------------------------
    public void resetValues() {
        m_timestamp = null;
        m_index = 0;
        m_groupNames.clear();
        m_subgroupNames.clear();
        m_name = null;
        m_imgFileName = "";
        m_fExt = "";
    }

    // --------------------------------------------------------------------------------------------------------
    public void setFileExt(String fExt) {
        if (fExt == null || fExt.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid file extension. It shouldn't be null or just spaces.");
        } else {
            m_fExt = _cleanName(fExt);
            if (!m_fExt.startsWith(".")) {
                m_fExt = "." + m_fExt;
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------
    public void clearGroupNames() {

        m_groupNames.clear();
    }
    
    // --------------------------------------------------------------------------------------------------------
    public void setGroupNames(String groupNames[]) {

        m_groupNames.clear();
        for (String groupName : groupNames) {
            addGroupName(groupName);
        }

    }

    // --------------------------------------------------------------------------------------------------------
    public void setImgFileName(String imgFileName) {
        if (imgFileName == null || imgFileName.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid img file name. It shouldn't be null or just spaces.");
        } else {
            m_imgFileName = imgFileName;
        }
    }

    // --------------------------------------------------------------------------------------------------------
    public void setIndex(int index) {
        m_index = index >= 0 ? index : 0;
    }

    // --------------------------------------------------------------------------------------------------------
    public void setName(String name) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid name section. It shouldn't be null or just spaces.");
        } else {
            m_name = _cleanName(name);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    public void clearSubgroupNames() {

        m_subgroupNames.clear();
        
    }
    
    // --------------------------------------------------------------------------------------------------------
    public void setSubgroupNames(String subgroupNames[]) {

        m_subgroupNames.clear();
        for (String subgroupName : subgroupNames) {
            addSubgroupName(subgroupName);
        }

    }

    // --------------------------------------------------------------------------------------------------------
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return compose();
    }

    // --------------------------------------------------------------------------------------------------------
    private String _cleanName(String name) {
        if (name == null) {
            return null;
        }
        String cleanedName = name.replace('_', ' ').replace('-', ' ').replace('@', ' ').replace('#', ' ').replace('[', ' ').replace(']', ' ').trim();
        return cleanedName;
    }

    // --------------------------------------------------------------------------------------------------------
    private void _parseGroupNames(String strValue) {
        String groupNames[] = strValue.split("-");
        for (String groupName : groupNames) {
            addGroupName(groupName);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private void _parseIndex(String strValue) throws ParseException {
        int index = Integer.parseInt(strValue);
        setIndex(index);
    }

    // --------------------------------------------------------------------------------------------------------
    private void _parseSubgroupNames(String strValue) {
        String subgroupNames[] = strValue.split("-");
        for (String subgroupName : subgroupNames) {
            addSubgroupName(subgroupName);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private String _stripTimestamp(String name) {

        Matcher ts = s_timestampRE.matcher(name);
        boolean hasTimestamp = ts.find();
        if (hasTimestamp) {
            m_timestamp = name.substring(ts.start(), ts.end());
            name = name.substring(0, ts.start()) + name.substring(ts.end());
        }
        return name;

    }
}
