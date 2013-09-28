/**
 * 
 */
package com.jzb.fdf.srvc;

/**
 * @author jzarzuela
 * 
 */
public class SFile {

    private String  m_folderName;

    private String  m_hashing;

    private long    m_id;

    private boolean m_isDuplicated;

    private String  m_name;

    private long    m_version;

    public SFile(long id, long version) {
        m_id = id;
        m_version = version;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SFile) {
            SFile other = (SFile) obj;
            return m_id == other.m_id;
        } else {
            return false;
        }
    }

    /**
     * @return the folderName
     */
    public String getFolderName() {
        return m_folderName;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return m_folderName + m_name;
    }

    /**
     * @return the hashing
     */
    public String getHashing() {
        return m_hashing;
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
     * @return the version
     */
    public long getVersion() {
        return m_version;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return m_name.hashCode();
    }

    /**
     * @return the isDuplicated
     */
    public boolean isDuplicated() {
        return m_isDuplicated;
    }

    /**
     * @param isDuplicated
     *            the isDuplicated to set
     */
    public void setDuplicated(boolean isDuplicated) {
        m_isDuplicated = isDuplicated;
    }

    /**
     * @param folderName
     *            the folderName to set
     */
    public void setFolderName(String folderName) {
        m_folderName = folderName;
    }

    /**
     * @param hashing
     *            the hashing to set
     */
    public void setHashing(String hashing) {
        m_hashing = hashing;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.m_name = name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SFile - name ='" + m_name + "', \tid='" + m_id + "', \thashing='" + m_hashing + "', \tfolder = '" + m_folderName + "'";
    }
}
