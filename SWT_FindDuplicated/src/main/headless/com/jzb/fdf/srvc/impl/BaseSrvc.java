/**
 * 
 */
package com.jzb.fdf.srvc.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import com.jzb.fdf.model.MFile;
import com.jzb.fdf.model.MFolder;
import com.jzb.fdf.srvc.SFile;
import com.jzb.fdf.srvc.SFolder;

/**
 * @author jzarzuela
 * 
 */
public abstract class BaseSrvc {

    // ----------------------------------------------------------------------------------------------------
    public BaseSrvc() {
    }

    // ----------------------------------------------------------------------------------------------------
    protected List<SFile> _MFileToSFile_List(Collection<MFile> list, boolean justDuplicated) {

        ArrayList<SFile> sfileList = new ArrayList();
        for (MFile mfile : list) {
            if (mfile.isDuplicated() || !justDuplicated) {
                SFile sfile = new SFile(mfile.getId(), mfile.getVersion());
                sfile.setName(mfile.getName());
                sfile.setFolderName(mfile.getFolder().getFullName());
                sfile.setHashing(mfile.getHashing());
                sfile.setDuplicated(mfile.isDuplicated());
                sfileList.add(sfile);
            }
        }
        return sfileList;
    }

    // ----------------------------------------------------------------------------------------------------
    protected List<SFolder> _MFolderToSFolder_List(Collection<MFolder> list, boolean justDuplicated) {

        ArrayList<SFolder> sfolderList = new ArrayList();
        if (list != null) {
            for (MFolder mfolder : list) {
                if (mfolder.getDuplicatedSubfoldersCount() > 0 || mfolder.getDuplicatedFilesCount() > 0 || !justDuplicated) {
                    SFolder sfolder = new SFolder(mfolder.getId());
                    sfolder.setName(mfolder.getName());
                    sfolder.setFullName(mfolder.getFullName());
                    sfolder.setHasDuplicatedFiles(mfolder.getDuplicatedFilesCount() > 0);
                    sfolder.setHasDuplicatedSubfolders(mfolder.getDuplicatedSubfoldersCount() > 0);
                    if (!justDuplicated) {
                        sfolder.setSubfoldersCount(mfolder.getSubFolders().size());
                    } else {
                        int count = 0;
                        for (MFolder subfolder : mfolder.getSubFolders().values()) {
                            if (subfolder.getDuplicatedFilesCount() > 0 || subfolder.getDuplicatedSubfoldersCount() > 0) {
                                count++;
                            }
                        }
                        sfolder.setSubfoldersCount(count);
                    }
                    sfolderList.add(sfolder);
                }
            }
        }
        return sfolderList;
    }

    // ----------------------------------------------------------------------------------------------------
    protected final EntityManager currrentEntMngr() {

        throw new PersistenceException("Persistence Manager was not set for this method call");
    }
}
