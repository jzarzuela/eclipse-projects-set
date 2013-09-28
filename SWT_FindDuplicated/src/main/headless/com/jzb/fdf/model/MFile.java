/**
 * 
 */
package com.jzb.fdf.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * @author jzarzuela
 * 
 */
@Entity
@Table(name = "MFile", indexes = { @Index(columnList = "hashing, isDuplicated") })
public class MFile implements Serializable {

    private static final long serialVersionUID = 8659210893097442219L;

    @ManyToOne(fetch = FetchType.LAZY)
    private MFolder           folder;

    private String            hashing;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long              id;
    private boolean           isDuplicated     = false;
    private long              lastModified     = -1;
    private long              lengh            = -1;
    private String            name;

    @Version
    private long              version;

    // ----------------------------------------------------------------------------------------------------
    public MFile() {
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the folder
     */
    public MFolder getFolder() {
        return folder;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return folder.getFullName() + name;
    }

    /**
     * @return the hashing
     */
    public String getHashing() {
        return hashing;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @return the lastModified
     */
    public long getLastModified() {
        return lastModified;
    }

    /**
     * @return the lengh
     */
    public long getLengh() {
        return lengh;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the version
     */
    public long getVersion() {
        return version;
    }

    /**
     * @return the isDuplicated
     */
    public boolean isDuplicated() {
        return isDuplicated;
    }

    // ----------------------------------------------------------------------------------------------------
    public void markAsDuplicated() {

        if (!isDuplicated) {
            isDuplicated = true;
            folder.incrementDuplicatedFilesCount();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param folder
     *            the folder to set
     */
    public void setFolder(MFolder folder) {
        this.folder = folder;
    }

    /**
     * @param hashing
     *            the hashing to set
     */
    public void setHashing(String hashing) {
        this.hashing = hashing;
    }

    /**
     * @param lastModified
     *            the lastModified to set
     */
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @param lengh
     *            the lengh to set
     */
    public void setLengh(long lengh) {
        this.lengh = lengh;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    // ----------------------------------------------------------------------------------------------------
    public void unmarkAsDuplicated() {

        if (isDuplicated) {
            isDuplicated = false;
            folder.decrementDuplicatedFilesCount();
        }
    }
}
