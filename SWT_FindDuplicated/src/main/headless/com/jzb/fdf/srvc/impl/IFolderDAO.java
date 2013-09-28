/**
 * 
 */
package com.jzb.fdf.srvc.impl;

import java.io.File;

import com.jzb.fdf.model.MFolder;

/**
 * @author jzarzuela
 * 
 */
interface IFolderDAO {

    public final IFolderDAO inst = new FolderDAOImpl();

    public MFolder createInstance(File folder);

    public void deleteInstance(MFolder mfolder);
    
    public MFolder getByFile(File folder);

}
