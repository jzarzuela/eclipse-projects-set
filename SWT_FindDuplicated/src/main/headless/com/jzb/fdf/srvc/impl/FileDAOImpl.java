/**
 * 
 */
package com.jzb.fdf.srvc.impl;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.jzb.fdf.model.MFile;
import com.jzb.fdf.model.MFolder;

/**
 * @author jzarzuela
 * 
 */
public class FileDAOImpl extends BaseSrvc implements IFileDAO {

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx=true)
    public void deleteInstance(MFile mfile) {
        
        EntityManager em = currrentEntMngr();
        mfile.getFolder().removeFile(mfile);
        em.remove(mfile);
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx=true)
    public MFile createInstance(File folder, String name, long length, long lastModified, String hashing) {

        EntityManager em = currrentEntMngr();

        // Busca el MFolder que contiene el fichero.
        // Es obligatorio que exista
        MFolder parentFolder = IFolderDAO.inst.getByFile(folder);
        if (parentFolder == null) {
            throw new NoResultException("FileSrvcImpl - Error on file '" + name + "'. Container folder should exist: " + folder);
        }

        MFile mfile = new MFile();
        mfile.setName(name);
        mfile.setLengh(length);
        mfile.setLastModified(lastModified);
        mfile.setHashing(hashing);
        mfile.setFolder(parentFolder);
        parentFolder.addFile(mfile);

        em.persist(mfile);

        return mfile;
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx=false)
    public List<MFile> getByHashing(String hashing) {

        EntityManager em = currrentEntMngr();
        Query q = em.createQuery("SELECT e FROM MFile e WHERE e.hashing=:hashing ORDER BY e.folder.fullName");
        q.setParameter("hashing", hashing);
        List<MFile> list = q.getResultList();
        return list;
    }

}
