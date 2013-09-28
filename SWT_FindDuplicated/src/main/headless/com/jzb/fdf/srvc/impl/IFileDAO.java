/**
 * 
 */
package com.jzb.fdf.srvc.impl;

import java.io.File;
import java.util.List;

import com.jzb.fdf.model.MFile;

/**
 * @author jzarzuela
 * 
 */
public interface IFileDAO {

    public final IFileDAO inst = new FileDAOImpl();

    public MFile createInstance(File folder, String name, long length, long lastModified, String hashing);

    public void deleteInstance(MFile mfile);

    public List<MFile> getByHashing(String hashing);

}
