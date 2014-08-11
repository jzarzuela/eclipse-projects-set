/**
 * 
 */
package com.jzb.test;

import java.io.File;
import java.util.HashMap;

/**
 * @author jzarzuela
 * 
 */
public class FindDuplicates {

    private HashMap<String, String> m_processedFiles = new HashMap<>();
    private long                    m_savedTileSize  = 0;

    /**
     * 
     */
    public FindDuplicates() {
        // TODO Auto-generated constructor stub
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        long t1 = 0, t2 = 0;
        try {
            System.out.println("\n***** EXECUTION STARTED *****\n");
            FindDuplicates me = new FindDuplicates();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            t2 = System.currentTimeMillis();
            System.out.println("***** EXECUTION FAILED [" + (t2 - t1) + "]*****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        File sourceFolders[] = { new File("/Users/jzarzuela/Downloads/_tmp_/pois/_offline_maps_/HT_Escocia_2014") };

        FileHashing fh = new FileHashing();

        for (File folder : sourceFolders) {
            _processFolder(folder, fh);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private void _processFolder(File folder, FileHashing fh) throws Exception {

        if (!folder.isDirectory() || !folder.exists()) {
            System.out.println("Warning, Argument passed is not a folder or doesn't exist: " + folder);
            return;
        } else {
            // System.out.println("Processing folder: " + folder);
        }

        for (File file : folder.listFiles()) {

            if (file.isDirectory()) {
                _processFolder(file, fh);
            } else {
                String hashing = fh.processFile(file);

                String prevFile = m_processedFiles.get(hashing);
                if (prevFile != null) {
                    m_savedTileSize += file.length();
                    System.out.println("Warning, Files duplicated. Saved bytes: " + m_savedTileSize);
                    System.out.println("    " + prevFile);
                    System.out.println("    " + file.getAbsolutePath());
                } else {
                    m_processedFiles.put(hashing, file.getAbsolutePath());
                }
            }
        }
    }

}
