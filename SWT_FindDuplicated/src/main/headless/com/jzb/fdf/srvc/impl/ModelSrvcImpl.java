/**
 * 
 */
package com.jzb.fdf.srvc.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.jzb.fdf.as.PersistenceAspect;
import com.jzb.fdf.model.MFile;
import com.jzb.fdf.srvc.IModelSrvc;
import com.jzb.fdf.srvc.impl.NeedsEntityManager.EM_Type;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class ModelSrvcImpl extends BaseSrvc implements IModelSrvc {

    // ----------------------------------------------------------------------------------------------------
    public void done() {
        PersistenceAspect.done();
    }

    // ----------------------------------------------------------------------------------------------------
    public void init(boolean deleteDB) {
        PersistenceAspect.init(deleteDB);
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx=true)
    public void processDuplicatedFiles() throws Exception {

        _cleanFilesDuplicateInfo();
        _cleanFoldersDuplicateInfo();

        _generateFilesAndFoldersDuplicateInfo();
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(value=EM_Type.newOne, needsTrx=true)
    private void _cleanFilesDuplicateInfo() throws Exception {

        Tracer._debug("ModelSrvcImpl - Processing duplicated files - Cleaning mark on all files");

        EntityManager em = currrentEntMngr();
        Query q = em.createQuery("UPDATE MFile SET isDuplicated = FALSE WHERE isDuplicated = TRUE");
        int updateCount = q.executeUpdate();

        Tracer._debug("ModelSrvcImpl - Processing duplicated files - Nume files cleaned: " + updateCount);
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(value=EM_Type.newOne, needsTrx=true)
    private void _cleanFoldersDuplicateInfo() throws Exception {

        Tracer._debug("ModelSrvcImpl - Processing duplicated files - Cleaning mark on all folders");

        EntityManager em = currrentEntMngr();
        Query q = em.createQuery("UPDATE MFolder SET duplicatedFilesCount = 0, duplicatedSubfoldersCount = 0 WHERE duplicatedFilesCount != 0 OR duplicatedSubfoldersCount != 0");
        int updateCount = q.executeUpdate();

        Tracer._debug("ModelSrvcImpl - Processing duplicated files - Nume folders cleaned: " + updateCount);
    }

    // ----------------------------------------------------------------------------------------------------
    // El iterador gestiona la transaccion
    @NeedsEntityManager(value=EM_Type.newOne, needsTrx=false)
    private void _generateFilesAndFoldersDuplicateInfo() throws Exception {

        Tracer._debug("ModelSrvcImpl - Processing duplicated files - Setting mark on duplicating files");

        EntityManager em = currrentEntMngr();
        Query q = em.createQuery("SELECT DISTINCT f1 FROM MFile f1, MFile f2 WHERE f1.id!=f2.id AND f1.hashing=f2.hashing");
        List<MFile> list = q.getResultList();

        // El iterador gestiona la transaccion
        MListIterator.processList(em, 1000, list, new IItemProcessor<MFile>() {

            public void processItem(MFile file) throws Exception {

                // Marcar el fichero se propaga a las carpetas
                file.markAsDuplicated();
            }
        });
    }

}
