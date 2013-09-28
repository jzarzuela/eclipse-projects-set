/**
 * 
 */
package com.jzb.fdf.srvc;

import java.io.File;
import java.util.List;

import com.jzb.fdf.srvc.impl.FileSrvcImpl;

/**
 * @author jzarzuela
 * 
 */
public interface IFileSrvc {

    public final IFileSrvc inst = new FileSrvcImpl();

    public void createInstance(File folder, String name, long length, long lastModified, String hashing);

    public void filterDuplicateById(long fileId);
    
    public void cleanById(long fileId);

    public List<SFile> getAll();

    public List<SFile> getAllDuplicated();

    public List<SFile> getByHashing(String hashing);

    public List<SFile> getForFolder(long folderId, boolean justDuplicated);

}
