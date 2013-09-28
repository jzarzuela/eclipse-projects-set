/**
 * 
 */
package com.jzb.fdf.prcs;

import java.io.File;
import java.nio.file.Files;

/**
 * @author jzarzuela
 * 
 */
public class DefaultFileFilter implements IFileFilter {

    // ----------------------------------------------------------------------------------------------------
    /**
     * @see com.jzb.fdf.prcs.IFileFilter#_isFiltered(java.io.File)
     */
    @Override
    public boolean isFiltered(File file) {

        String name = file.getName();

        if (name.startsWith("."))
            return true;

        if (name.endsWith(".class"))
            return true;

        if (name.endsWith(".lproj"))
            return true;

        if (name.equalsIgnoreCase("bin"))
            return true;

        if (name.equalsIgnoreCase(".tmp"))
            return true;

        if (name.equals("Icon\r"))
            return true;

        if (name.equalsIgnoreCase("readme.txt"))
            return true;

        if (Files.isSymbolicLink(file.toPath()))
            return true;

        return false;
    }

}
