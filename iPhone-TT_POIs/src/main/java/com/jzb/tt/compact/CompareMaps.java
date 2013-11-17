/**
 * 
 */
package com.jzb.tt.compact;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.TreeSet;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileLoader;

/**
 * @author jzarzuela
 * 
 */
public class CompareMaps {

    private static interface IPOICompare {

        public boolean compare(TPOIData poi1, TPOIData poi2);
    }

    private ArrayList<TPOIFileData> m_fileDatas = new ArrayList<>();

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
            CompareMaps me = new CompareMaps();
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

        TPOIFileData.warnIfDuplicated = false;

        final Path fileMap1 = Paths.get("/Users/jzarzuela/Downloads/_tmp_/pois/_KMLs_/BT_Las Vegas_2008-2011.kml");
        final Path fileMap2 = Paths.get("/Users/jzarzuela/Downloads/_tmp_/pois/_KMLs_/Las Vegas - Compartido.kml");

        TPOIFileData fileData1 = KMLFileLoader.loadFile(fileMap1.toFile());
        TPOIFileData fileData2 = KMLFileLoader.loadFile(fileMap2.toFile());

        TreeSet<TPOIData> duplicated = new TreeSet();
        TreeSet<TPOIData> distinct = new TreeSet();

        for (TPOIData poi1 : fileData1.getAllPOIs()) {

            poi1.setExtraInfo(fileData1.getName());
            boolean isDuplicated = false;

            for (TPOIData poi2 : fileData2.getAllPOIs()) {

                poi2.setExtraInfo(fileData2.getName());

                if (poi1.getName().equalsIgnoreCase(poi2.getName())) {
                    isDuplicated = true;
                    duplicated.add(poi2);
                } else if (poi1.distance(poi2) < 1.0) {
                    isDuplicated = true;
                    duplicated.add(poi2);
                }
            }
            if (isDuplicated) {
                duplicated.add(poi1);
            } else {
                distinct.add(poi1);
            }
        }

        for (TPOIData poi2 : fileData2.getAllPOIs()) {

            poi2.setExtraInfo(fileData2.getName());
            boolean isDuplicated = false;

            for (TPOIData poi1 : fileData1.getAllPOIs()) {

                poi1.setExtraInfo(fileData1.getName());

                if (poi2.getName().equalsIgnoreCase(poi1.getName())) {
                    isDuplicated = true;
                    duplicated.add(poi1);
                }
                if (poi2.distance(poi1) < 1.0) {
                    isDuplicated = true;
                    duplicated.add(poi1);
                }
            }
            if (isDuplicated) {
                duplicated.add(poi2);
            } else {
                distinct.add(poi2);
            }
        }

        for (TPOIData poi : distinct) {
            System.out.printf("%20s %s\n", poi.getExtraInfo(), poi);
        }
    }

}
