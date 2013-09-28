/**
 * 
 */
package com.jzb.fdf.srvc.impl;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.jzb.fdf.model.MFile;
import com.jzb.fdf.model.MFolder;
import com.jzb.fdf.srvc.IFileSrvc;
import com.jzb.fdf.srvc.SFile;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class FileSrvcImpl extends BaseSrvc implements IFileSrvc {

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx = true)
    public void createInstance(File folder, String name, long length, long lastModified, String hashing) {

        IFileDAO.inst.createInstance(folder, name, length, lastModified, hashing);
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx = true)
    public void filterDuplicateById(long fileId) {

        // Recupera el mfile a filtrar de la lista de duplicados
        EntityManager em = currrentEntMngr();
        MFile mfile = em.find(MFile.class, fileId);
        if (mfile != null) {
            _filterDuplicate(mfile);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx = true)
    public void cleanById(long fileId) {

        // Recupera el mfile a "limpiar" del origen de informacion
        EntityManager em = currrentEntMngr();
        MFile mfile = em.find(MFile.class, fileId);
        if (mfile == null)
            return;
        
        // Se queda con el nombre completo para luego moverlo
        String fname = mfile.getFullName();

        // Le quita la marca de duplicado si la tuviese antes de eliminarlo
        _filterDuplicate(mfile);

        // Lo elimina del modelo
        IFileDAO.inst.deleteInstance(mfile);

        // Mueve el fichero fisicamente para darlo por "cleaned"
        try {
            FSUtils.cleanDuplicated(fname);
        } catch (Exception ex) {
            Tracer._error("FileSrvc - Error cleaning file: '" +fname + "'", ex);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx = false)
    public List<SFile> getAll() {

        EntityManager em = currrentEntMngr();
        Query q = em.createQuery("SELECT e FROM MFile e ORDER BY e.hash, e.name");
        List<MFile> list = q.getResultList();
        return _MFileToSFile_List(list, false);
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx = false)
    public List<SFile> getAllDuplicated() {

        EntityManager em = currrentEntMngr();
        Query q = em.createQuery("SELECT DISTINCT f1 FROM MFile f1, MFile f2 WHERE f1.id!=f2.id AND f1.hashing=f2.hashing ORDER BY f1.hashing, f1.folder.fullName");
        List<MFile> list = q.getResultList();
        return _MFileToSFile_List(list, true);
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx = false)
    public List<SFile> getByHashing(String hashing) {

        List<MFile> list = IFileDAO.inst.getByHashing(hashing);
        return _MFileToSFile_List(list, false);
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx = false)
    public List<SFile> getForFolder(long folderId, boolean justDuplicated) {

        EntityManager em = currrentEntMngr();
        MFolder parent = em.find(MFolder.class, folderId);
        return _MFileToSFile_List(parent.getFiles().values(), justDuplicated);
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx = true)
    private void _filterDuplicate(MFile mfile) {

        if (mfile.isDuplicated()) {

            // Consigue la lista de los otros ficheros que comparten el mismo hashing
            List<MFile> list = IFileDAO.inst.getByHashing(mfile.getHashing());

            // Si solo son dos los desmarca a ambos
            // Si son mas, solo borra la marca del fichero de esta carpeta (los otros se dejan marcados entre si)
            if (list.size() == 2) {
                for (MFile mfile2 : list) {
                    mfile2.unmarkAsDuplicated();
                }
            } else {
                mfile.unmarkAsDuplicated();
            }
        }

    }

}
