/**
 * 
 */
package com.jzb.fdf.srvc;

import java.lang.ref.SoftReference;
import java.util.List;

/**
 * @author jzarzuela
 * 
 */
public class SFolder {

    private String                       m_fullName;

    private boolean                      m_hasDuplicatedFiles;

    private boolean                      m_hasDuplicatedSubfolders;

    private long                         m_id;

    private String                       m_name;

    private int                          m_subfoldersCount = -1;

    private SoftReference<List<SFolder>> m_subFoldersRef;

    public SFolder(long id) {
        m_id = id;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return m_fullName;
    }

    /**
     * @return the id
     */
    public long getId() {
        return m_id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     * @return the subFolders
     */
    public List<SFolder> getSubFolders() {
        List<SFolder> list = null;
        if (m_subFoldersRef != null) {
            list = m_subFoldersRef.get();
        }
        return list;
    }

    /**
     * @return the subfoldersCount
     */
    public int getSubfoldersCount() {
        return m_subfoldersCount;
    }

    /**
     * @return the hasDuplicatedFiles
     */
    public boolean hasDuplicatedFiles() {
        return m_hasDuplicatedFiles;
    }

    /**
     * @return the hasDuplicatedSubfolders
     */
    public boolean hasDuplicatedSubfolders() {
        return m_hasDuplicatedSubfolders;
    }

    /**
     * @param fullName
     *            the fullName to set
     */
    public void setFullName(String fullName) {
        m_fullName = fullName;
    }

    /**
     * @param hasDuplicatedFiles
     *            the hasDuplicatedFiles to set
     */
    public void setHasDuplicatedFiles(boolean hasDuplicatedFiles) {
        m_hasDuplicatedFiles = hasDuplicatedFiles;
    }

    /**
     * @param hasDuplicatedSubfolders
     *            the hasDuplicatedSubfolders to set
     */
    public void setHasDuplicatedSubfolders(boolean hasDuplicatedSubfolders) {
        m_hasDuplicatedSubfolders = hasDuplicatedSubfolders;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * @param subFolders
     *            the subFolders to set
     */
    public void setSubFolders(List<SFolder> subFolders) {

        m_subFoldersRef = new SoftReference(subFolders);
    }

    /**
     * @param subfoldersCount
     *            the subfoldersCount to set
     */
    public void setSubfoldersCount(int subfoldersCount) {
        m_subfoldersCount = subfoldersCount;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SFolder - name ='" + m_name + "', id='" + m_id + "', hasDupFiles='" + m_hasDuplicatedFiles + "', hasDupSFolders='" + m_hasDuplicatedSubfolders + "', fullName = '" + m_fullName + "'";
    }
}
