/**
 * 
 */
package com.jzb.fdf.srvc.impl;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import com.jzb.fdf.model.MFolder;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
class FolderDAOImpl extends BaseSrvc implements IFolderDAO {

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx=true)
    public void deleteInstance(MFolder mfolder) {
        
        EntityManager em = currrentEntMngr();
        mfolder.getParent().removeSubfolder(mfolder);
        em.remove(mfolder);
    }

    // ----------------------------------------------------------------------------------------------------
    private static String _getFolderFName(File folder) {
        return folder.getAbsolutePath() + File.separator;
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx=true)
    public MFolder createInstance(File folder) {

        EntityManager em = currrentEntMngr();

        MFolder mfolder = new MFolder();
        mfolder.setName(folder.getName());
        mfolder.setFullName(_getFolderFName(folder));

        MFolder parentFolder = getByFile(folder.getParentFile());
        if (parentFolder != null) {
            parentFolder.addSubfolder(mfolder);
        }

        em.persist(mfolder);

        return mfolder;
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx=false)
    public MFolder getByFile(File folder) {

        EntityManager em = currrentEntMngr();

        // Busca el MFolder indicado
        Query q = em.createQuery("SELECT e FROM MFolder e WHERE e.fullName=:fname");
        String fname = _getFolderFName(folder);
        q.setParameter("fname", fname);
        List<MFolder> list = q.getResultList();

        // Si no hay resultados retorna null
        if (list.size() == 0)
            return null;

        // Debería haber solo una carpeta con ese nombre. De Haber mas es un error
        if (list.size() > 1) {
            String msg = "FolderDAO - Error becaue more than one instance retrieved. Full Name = '" + folder + "'";
            Tracer._warn(msg);
            throw new NonUniqueResultException(msg);
        }

        // Retorna el valor
        return list.get(0);
    }

}
