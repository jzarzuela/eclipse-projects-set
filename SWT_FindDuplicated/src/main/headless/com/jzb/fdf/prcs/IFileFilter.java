/**
 * 
 */
package com.jzb.fdf.prcs;

import java.io.File;

/**
 * @author jzarzuela
 * 
 */
public interface IFileFilter {

    public boolean isFiltered(File file);
}
