/**
 * 
 */
package com.jzb.test;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author jzarzuela
 * 
 */
public class CheckCopy {

    private HashSet<String> m_processedFiles = new HashSet<>();

    private File            m_srcFolder      = new File("/Users/jzarzuela/Documents/personal/Viajes/");
    private File            m_dstFolder      = new File("/Volumes/My Passport/Viajes/");

    private int             m_srcFolderLen   = m_srcFolder.getAbsolutePath().length();
    private int             m_dstFolderLen   = m_dstFolder.getAbsolutePath().length();

    /**
     * 
     */
    public CheckCopy() {
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
            CheckCopy me = new CheckCopy();
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

        System.out.println();
        System.out.println("----- SRC Folder -----");
        _processSrcFolder(m_srcFolder);

        //System.out.println();
        //System.out.println("----- DST Folder -----");
        //_processDstFolder(m_dstFolder);

    }

    // ----------------------------------------------------------------------------------------------------
    private int m_dots1 = 0;
    private int m_dots2 = 0;

    private void _printDots() {

        m_dots1++;
        if (m_dots1 > 10) {
            m_dots1=0;
            System.out.print(".");
            m_dots2++;
            if (m_dots2 > 120) {
                System.out.println();
                m_dots2 = 0;
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _printError(String errorStr) {
        System.out.println();
        m_dots1 = 0;
        m_dots2 = 0;
        System.out.println("!!!!! --->  "+errorStr);
    }

    // ----------------------------------------------------------------------------------------------------
    private void _processSrcFolder(File folder) throws Exception {

        if (folder.getParentFile().getAbsolutePath().equals(m_srcFolder.getAbsolutePath())) {
            System.out.println("\n **> " + folder);
        } else {
            _printDots();
        }

        for (File file : folder.listFiles()) {

            if (file.isDirectory()) {
                _processSrcFolder(file);
            } else {

                String relPath = file.getAbsolutePath().substring(m_srcFolderLen);
                File dstFile = new File(m_dstFolder, relPath);

                if (!dstFile.exists()) {
                    _printError("Error, destination file doesn't exist: " + dstFile);
                }

                // m_processedFiles.add(relPath);
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _processDstFolder(File folder) throws Exception {

        if (folder.getParentFile().getAbsolutePath().equals(m_dstFolder.getAbsolutePath())) {
            System.out.println(" **> " + folder);
        } else {
            _printDots();
        }

        for (File file : folder.listFiles()) {

            if (file.isDirectory()) {
                _processDstFolder(file);
            } else {

                String relPath = file.getAbsolutePath().substring(m_dstFolderLen);
                File srcFile = new File(m_srcFolder, relPath);

                if (!srcFile.exists()) {
                    _printError("Error, source file doesn't exist: " + srcFile);
                }

                // m_processedFiles.add(relPath);
            }
        }
    }
}
