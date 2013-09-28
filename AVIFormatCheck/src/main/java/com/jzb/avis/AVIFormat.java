/**
 * 
 */
package com.jzb.avis;

import java.awt.Dimension;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.monte.media.avi.AVIReader;

/**
 * @author jzarzuela
 * 
 */
public class AVIFormat {

    private static DecimalFormat s_df = new DecimalFormat("0.000");

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
            AVIFormat me = new AVIFormat();
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

        File baseFolder = new File("/Volumes/ELEMENTS/Videos_DivX");
        _processFolder(baseFolder);
    }

    private void _processFile(File avifile) {

        Dimension dimension = null;
        try {
            AVIReader avireader = new AVIReader(avifile);
            dimension = avireader.getVideoDimension();
            avireader.close();
        } catch (Throwable th) {
            System.out.println("Error (" + th.getClass() + "):" + th.getMessage());
        }

        if (dimension != null) {
            double ratio = dimension.getWidth() / dimension.getHeight();
            String strRatio = s_df.format(ratio);
            System.out.println("  " + (int) dimension.getWidth() + ", " + (int) dimension.getHeight() + ", " + strRatio + ", " + avifile.getAbsolutePath());
        } else {
            System.out.println("  ???, ???, ???, " + avifile.getAbsolutePath());
        }
    }

    private void _processFolder(File folder) throws Exception {

        if(folder.getName().toLowerCase().contains("serie") || folder.getName().toLowerCase().contains("dibus")) {
            return;
        }
        
        ArrayList<File> subfolders = new ArrayList<File>();

        //System.out.println("** Processing folder: " + folder.getName());
        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                subfolders.add(f);
            } else {
                if (f.getName().toLowerCase().endsWith(".avi")) {
                    _processFile(f);
                }
            }
        }

        for (File f : subfolders) {
            if (!f.getName().startsWith(".")) {
                _processFolder(f);
            }
        }
    }
}
