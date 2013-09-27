/**
 * 
 */
package com.jzb.ttpoi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author jzarzuela
 * 
 */
public class FindOV2 {

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("\n***** EXECUTION STARTED *****\n");
            FindOV2 me = new FindOV2();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("\n***** EXECUTION FAILED *****\n");
            th.printStackTrace(System.out);
            System.exit(-1);
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
        dstFolder.mkdirs();
        // _findOV2(new File("/Users/jzarzuela"));
        _findOV2(new File("/Volumes/Seagate Expansion Drive/"));

    }

    static final File dstFolder = new File("/Users/jzarzuela/Downloads/_tmp_/found_ov2s");

    private void _findOV2(File folder) {

        if (folder.getAbsolutePath().contains("found_ov2s") || folder.getAbsolutePath().contains("/Data/Downloads/"))
            return;

        File[] fileList = folder.listFiles();
        if (fileList != null)
            for (File f : fileList) {

                if (f.getName().startsWith("."))
                    continue;

                if (f.isDirectory()) {
                    _findOV2(f);
                } else if (f.getName().toLowerCase().endsWith(".ov2")) {
                    _copyOV2File(f);
                }
            }
    }

    static int counter = 500;

    private void _copyOV2File(File sourceFile) {

        System.out.println("Copying file: " + sourceFile);

        FileChannel source = null;
        FileChannel destination = null;

        try {
            File destFile = new File(dstFolder, "#" + counter + "-" + sourceFile.getName());
            counter++;

            if (!destFile.exists()) {
                destFile.createNewFile();
            }

            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } catch (Throwable th) {
            System.out.println("ERROR in file copy: '" + sourceFile + "' - " + th.getClass().getName() + " / " + th.getMessage());
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException ex) {
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (IOException ex) {
                }
            }
        }
    }
}
