/**
 * 
 */
package com.jzb.futil;

import java.io.File;


/**
 * @author n63636
 *
 */
public interface IFileProcessor {
    public void setFolderIterator(FolderIterator fi);
    public void processFile(File f, File baseFolder) throws Exception;
}
