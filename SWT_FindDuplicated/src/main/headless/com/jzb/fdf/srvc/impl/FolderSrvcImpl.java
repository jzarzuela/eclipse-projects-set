/**
 * 
 */
package com.jzb.fdf.srvc.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.jzb.fdf.model.MFile;
import com.jzb.fdf.model.MFolder;
import com.jzb.fdf.srvc.IFolderSrvc;
import com.jzb.fdf.srvc.SFolder;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class FolderSrvcImpl extends BaseSrvc implements IFolderSrvc {

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx = true)
    public void cleanById(long folderId) {
        
        // Recupera el mfolder a "limpiar" del origen de informacion
        EntityManager em = currrentEntMngr();
        MFolder mfolder = em.find(MFolder.class, folderId);
        if (mfolder == null)
            return;
        
        // Se queda con el nombre completo para luego moverlo
        String fname = mfolder.getFullName();

        // Le quita la marca de duplicado si la tuviese antes de eliminarlo
        _filterDuplicate(mfolder);

        // Lo elimina del modelo
        IFolderDAO.inst.deleteInstance(mfolder);

        // Mueve la carpeta fisicamente para darla por "cleaned"
        try {
            FSUtils.cleanDuplicated(fname);
        } catch (Exception ex) {
            Tracer._error("FileSrvc - Error cleaning file: '" + fname + "'", ex);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx = true)
    public ArrayList<File> compareAndCleanFolder(File folder, HashMap<String, File> mappedFolderContent) {

        // Recupera o crea el elemento conectado a la BBDD
        MFolder mfolder = IFolderDAO.inst.getByFile(folder);
        if (mfolder == null) {
            mfolder = IFolderDAO.inst.createInstance(folder);
        }

        // Itera eliminando ficheros que ya no existen o estan obsoletos
        for (MFile mfile : new ArrayList<MFile>(mfolder.getFiles().values())) {
            File file = mappedFolderContent.get(mfile.getName());
            if (file == null || file.length() != mfile.getLengh() || file.lastModified() != mfile.getLastModified()) {
                IFileDAO.inst.deleteInstance(mfile);
            }
        }

        // Itera eliminando subcarpetas que ya no existen
        for (MFolder msubfolder : new ArrayList<MFolder>(mfolder.getSubFolders().values())) {
            File subfolder = mappedFolderContent.get(msubfolder.getName());
            if (subfolder == null || !subfolder.isDirectory()) {
                IFolderDAO.inst.deleteInstance(msubfolder);
            }
        }

        // Itera los subelementos para buscar elementos que sería necesario procesar
        ArrayList<File> listToProcess = new ArrayList();
        for (File file : mappedFolderContent.values()) {

            if (file.isDirectory()) {

                // Apunta la subcarpeta para ser procesada
                listToProcess.add(file);

            } else {

                // Apunta el fichero para ser procesado si no existe [de existir y ser diferente se habria borrado]
                MFile mfile = mfolder.getFiles().get(file.getName());
                if (mfile == null) {
                    listToProcess.add(file);
                }
            }

        }

        // Retorna el resultado
        return listToProcess;
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx = true)
    public void filterDuplicateById(long folderId) {

        // Recupera el mfolder a filtrar de la lista de duplicados
        EntityManager em = currrentEntMngr();
        MFolder mfolder = em.find(MFolder.class, folderId);
        if (mfolder != null) {
            _filterDuplicate(mfolder);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx = false)
    public List<SFolder> getRootFolders(boolean justDuplicated) {

        EntityManager em = currrentEntMngr();
        Query q = em.createQuery("SELECT f FROM MFolder f WHERE f.parent IS NULL ORDER BY f.fullName");
        List<MFolder> list = q.getResultList();
        return _MFolderToSFolder_List(list, justDuplicated);
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx = false)
    public List<SFolder> getSubfoldersForID(long parentId, boolean justDuplicated) {

        EntityManager em = currrentEntMngr();
        MFolder parent = em.find(MFolder.class, parentId);
        return parent == null ? null : _MFolderToSFolder_List(parent.getSubFolders().values(), justDuplicated);
    }

    // ----------------------------------------------------------------------------------------------------
    @NeedsEntityManager(needsTrx = true)
    private void _filterDuplicate(MFolder mfolder) {

        // Itera todos sus subcarpetas eliminando la marca de duplicado de sus ficheros
        // Quitar la marca en el fichero se propaga a las carpetas
        for (MFolder msubfolder: mfolder.getSubFolders().values()) {
            _filterDuplicate(msubfolder);
        }
        
        // Itera todos sus ficheros eliminando la marca de duplicado
        // Quitar la marca en el fichero se propaga a las carpetas
        for (MFile mfile : mfolder.getFiles().values()) {

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

}
