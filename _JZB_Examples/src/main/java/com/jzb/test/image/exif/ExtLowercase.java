/**
 * 
 */
package com.jzb.test.image.exif;

import java.io.File;

/**
 * @author jzarzuela
 * 
 */
public class ExtLowercase {

    final static int EXIF_DATE_TIME = 0x9004;

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            ExtLowercase me = new ExtLowercase();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {
        File baseFolder = new File("/Users/jzarzuela/Documents/_TMP_/100CANON-PRAGA");
        _renameFiles(baseFolder);
    }

    private void _renameFiles(File folder) throws Exception {
        System.out.println("Renaming files in :'" + folder + "'");
        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                _renameFiles(f);
            } else {
                if (f.getName().endsWith(".JPG")) {
                    String newName = f.getName().replace(".JPG", ".jpg");
                    _renameFile(f,newName);
                } else if (f.getName().endsWith(".CR2")) {
                    String newName = f.getName().replace(".CR2", ".cr2");
                    _renameFile(f,newName);
                }
            }
        }
    }
    
    private void _renameFile(File f, String newName) {
        File newFile = new File(f.getParentFile(),newName);
        if(!f.renameTo(newFile)) {
            System.out.println("    Error renaming: "+f);
        } else {
            System.out.println("    File renamed: "+f);
        }
    }
}
