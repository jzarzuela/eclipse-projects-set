/**
 * 
 */
package com.jzb.fdf.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * @author jzarzuela
 * 
 */
@Entity
@Table(name = "MFolder", indexes = { @Index(columnList = "fullName, duplicatedFilesCount, duplicatedSubfoldersCount") })
public class MFolder implements Serializable {

    private static final long    serialVersionUID = -3218149460205750533L;

    private int                  duplicatedFilesCount;

    private int                  duplicatedSubfoldersCount;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "folder", cascade = CascadeType.ALL)
    @MapKey(name = "name")
    private Map<String, MFile>   files            = new HashMap();

    private String               fullName;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long                 id;

    private String               name;

    @ManyToOne(fetch = FetchType.LAZY)
    private MFolder              parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
    @MapKey(name = "name")
    private Map<String, MFolder> subFolders       = new HashMap();

    @Version
    private long                 version;

    // ----------------------------------------------------------------------------------------------------
    public MFolder() {
    }

    // ----------------------------------------------------------------------------------------------------
    public void addFile(MFile mfile) {
        files.put(mfile.getName(), mfile);
        mfile.setFolder(this);
    }

    public void addSubfolder(MFolder mfolder) {
        subFolders.put(mfolder.getName(), mfolder);
        mfolder.parent = this;
    }

    // ----------------------------------------------------------------------------------------------------
    public int decrementDuplicatedFilesCount() {

        int count = --duplicatedFilesCount;
        if (count == 0) {
            parent.decrementDuplicatedSubfoldersCount();
        }
        if (count < 0) {
            System.out.println("ups!");
        }
        return count;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the duplicatedFilesCount
     */
    public int getDuplicatedFilesCount() {
        return duplicatedFilesCount;
    }

    /**
     * @return the duplicatedSubfoldersCount
     */
    public int getDuplicatedSubfoldersCount() {
        return duplicatedSubfoldersCount;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the files
     */
    public Map<String, MFile> getFiles() {
        return files;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the parent
     */
    public MFolder getParent() {
        return parent;
    }

    /**
     * @return the subFolders
     */
    public Map<String, MFolder> getSubFolders() {
        return subFolders;
    }

    /**
     * @return the version
     */
    public long getVersion() {
        return version;
    }

    // ----------------------------------------------------------------------------------------------------
    public int incrementDuplicatedFilesCount() {

        int prevCount = duplicatedFilesCount;
        duplicatedFilesCount++;

        if (prevCount == 0 && getParent() != null) {
            parent.incrementDuplicatedSubfoldersCount();
        }
        return duplicatedFilesCount;
    }

    // ----------------------------------------------------------------------------------------------------
    public void removeFile(MFile mfile) {
        files.remove(mfile.getName());
        mfile.setFolder(null);
    }

    public void removeSubfolder(MFolder subfolder) {
        subFolders.remove(subfolder.getName());
        subfolder.setParent(null);
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param fullName
     *            the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(MFolder parent) {
        this.parent = parent;
    }

    private int decrementDuplicatedSubfoldersCount() {

        int count = --duplicatedSubfoldersCount;
        if (count == 0 && getParent() != null) {
            parent.decrementDuplicatedSubfoldersCount();
        }
        if (count < 0) {
            System.out.println("ups!");
        }
        return count;
    }

    private int incrementDuplicatedSubfoldersCount() {

        int prevCount = duplicatedSubfoldersCount;
        duplicatedSubfoldersCount++;

        if (prevCount == 0 && getParent() != null) {
            parent.incrementDuplicatedSubfoldersCount();
        }
        return duplicatedSubfoldersCount;
    }
}
