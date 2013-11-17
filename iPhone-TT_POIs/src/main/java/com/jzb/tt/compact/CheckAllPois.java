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

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileLoader;

/**
 * @author jzarzuela
 * 
 */
public class CheckAllPois {

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
            CheckAllPois me = new CheckAllPois();
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
        final Path startPath = Paths.get("/Users/jzarzuela/Downloads/_tmp_/pois/_KMLs_");
        _processFolder(startPath);

        // _processAllKmlFiles();

        _crossProcessKmlFiles();

    }

    // ----------------------------------------------------------------------------------------------------
    private void _processFolder(Path folder) throws Exception {

        
        System.out.println("\n************************************************************************************");
        System.out.println("****** Reading KML files from: " + folder);
        Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {

            /**
             * @see java.nio.file.SimpleFileVisitor#visitFileFailed(java.lang.Object, java.io.IOException)
             */
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                Objects.requireNonNull(file);
                System.out.println("*** ERROR processing file ***> " + exc);
                return FileVisitResult.CONTINUE;
            }

            @SuppressWarnings("synthetic-access")
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Objects.requireNonNull(file);
                Objects.requireNonNull(attrs);
                if (file.getFileName().toString().toLowerCase().endsWith(".kml")) {
                    try {
                        System.out.println("  -- Reading: " + file);
                        TPOIFileData fileData = KMLFileLoader.loadFile(file.toFile());
                        m_fileDatas.add(fileData);
                    } catch (Exception ex) {
                        throw new IOException("Error reading KML file: " + file, ex);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });

    }

    // ----------------------------------------------------------------------------------------------------
    private void _processAllKmlFiles() throws Exception {

        for (TPOIFileData fileData : m_fileDatas) {
            _processKmlFile(fileData);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _crossProcessKmlFiles() throws Exception {

        TPOIFileData allFileData = new TPOIFileData();
        for (TPOIFileData fileData : m_fileDatas) {
            for (TPOIData poi : fileData.getAllPOIs()) {
                poi.setExtraInfo(fileData.getName());
                allFileData.addPOI(poi);
            }
        }

        _processKmlFile(allFileData);

    }

    // ----------------------------------------------------------------------------------------------------
    private void _processKmlFile(TPOIFileData fileData) throws Exception {

        System.out.println("\n************************************************************************************");
        System.out.println("****** Processing KML file: " + fileData.getFileName());
        _searchDuplicatedNames(fileData);
        _searchDuplicatedPositions(fileData);
    }

    // ----------------------------------------------------------------------------------------------------
    private void _compareAllPoisTogether(ArrayList<TPOIData> pois, IPOICompare comparator) throws Exception {

        HashSet<TPOIData> duplicated = new HashSet<>();

        for (int n = 0; n < pois.size() - 1; n++) {
            TPOIData poi1 = pois.get(n);
            if (duplicated.contains(poi1))
                continue;
            boolean poi1Shown = false;
            for (int m = n + 1; m < pois.size(); m++) {
                TPOIData poi2 = pois.get(m);
                if (comparator.compare(poi1, poi2)) {
                    if (!poi1Shown) {
                        System.out.printf("             [%20s] %s\n", poi1.getExtraInfo(), poi1);
                        poi1Shown = true;
                    }
                    System.out.printf("  %10.2f [%20s] %s\n", poi1.distance(poi2), poi2.getExtraInfo(), poi2.toString());
                    duplicated.add(poi2);
                }
            }
            if (poi1Shown) {
                System.out.println();
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _searchDuplicatedNames(TPOIFileData fileData) throws Exception {

        System.out.println("\n  -- Searching duplicated names:");
        _compareAllPoisTogether(fileData.getAllPOIs(), new IPOICompare() {

            @Override
            public boolean compare(TPOIData poi1, TPOIData poi2) {
                return poi1.getName().equalsIgnoreCase(poi2.getName().toLowerCase());
            }
        });
    }

    // ----------------------------------------------------------------------------------------------------
    private void _searchDuplicatedPositions(TPOIFileData fileData) throws Exception {

        System.out.println("\n  -- Searching duplicated positions:");
        _compareAllPoisTogether(fileData.getAllPOIs(), new IPOICompare() {

            @Override
            public boolean compare(TPOIData poi1, TPOIData poi2) {
                return poi1.distance(poi2) < 10;
            }
        });

    }

}
