/**
 * 
 */
package com.jzb.fdf.srvc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jzb.fdf.srvc.impl.FolderSrvcImpl;

/**
 * @author jzarzuela
 * 
 */
public interface IFolderSrvc {

    public final IFolderSrvc inst = new FolderSrvcImpl();

    public ArrayList<File> compareAndCleanFolder(File folder, HashMap<String, File> mappedFolderContent);

    public void filterDuplicateById(long folderId);
    
    public void cleanById(long folderId);

    public List<SFolder> getRootFolders(boolean justDuplicated);

    public List<SFolder> getSubfoldersForID(long id, boolean justDuplicated);
}
