/**
 * 
 */
package com.jzb.futil;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class FileUtils {

    /**
     * @return "gif","png", etc... En minusculas y sin el punto
     */
    public static String getExtension(File file) {
        String fname = file.getName();
        int p1 = fname.lastIndexOf('.');
        if (p1 > 0) {
            return fname.substring(p1+1).toLowerCase();
        } else {
            return "";
        }
    }
}
