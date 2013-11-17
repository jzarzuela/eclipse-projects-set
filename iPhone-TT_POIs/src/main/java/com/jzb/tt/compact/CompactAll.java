/**
 * 
 */
package com.jzb.tt.compact;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.BINFileLoader;
import com.jzb.ttpoi.wl.BINFileWriter;
import com.jzb.ttpoi.wl.KMLFileLoader;
import com.jzb.ttpoi.wl.OV2FileLoader;

/**
 * @author jzarzuela
 * 
 */
public class CompactAll {

    private static final Path         s_allPath    = Paths.get("/Users/jzarzuela/Documents/personal/_POIs_");
    // private static final Path s_allPath = Paths.get("/Users/jzarzuela/Downloads/_tmp_/pois/_KMLs_");

    private HashMap<String, TPOIData> m_allPois    = new HashMap<>();
    private HashMap<String, TPOIData> m_allPoisKML = new HashMap<>();

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
            CompactAll me = new CompactAll();
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

        // System.out.println("\n\n\n\n");
        // _processFolder();
        // System.out.println("\n\n\n\n");
        // _saveAllData("allPois_KML.bin");
        //
        // System.out.println("\nRead All Pois\n");
        // _readAllData("allPois_KML.bin");
        //
        // System.out.println("\nFinding duplicated POIs\n");
        // _findDuplicated();

        _compareAllData();
    }

    // ----------------------------------------------------------------------------------------------------
    private void _compareAllData() throws Exception {

        File allPoisFile = new File(s_allPath.toFile(), "allPois_KML_2.bin");
        TPOIFileData fileData = BINFileLoader.loadFile(allPoisFile);
        for (TPOIData poi : fileData.getAllPOIs()) {
            String key = poi.getName();
            TPOIData prevPoi = m_allPoisKML.put(key, poi);
            if (prevPoi != null) {
                System.out.println("* Warning: Duplicated POI?: " + prevPoi);
            }
        }

        allPoisFile = new File(s_allPath.toFile(), "allPois_2.bin");
        fileData = BINFileLoader.loadFile(allPoisFile);
        for (TPOIData poi : fileData.getAllPOIs()) {
            String key = poi.getName();
            TPOIData prevPoi = m_allPois.put(key, poi);
            if (prevPoi != null) {
                System.out.println("+ Warning: Duplicated POI?: " + prevPoi);
            }
        }

        for (TPOIData poi : m_allPois.values()) {

            String key = poi.getName();
            TPOIData poi2 = m_allPoisKML.get(key);

            if (poi2 == null) {
//                System.out.println("\n\n");
//                System.out.println("POI was missed: " + poi.getName() + " - " + poi.getExtraInfo() + " - " + poi);
                double minMetres = Integer.MAX_VALUE;
                TPOIData closestPoi=null;
                boolean foundOne = false;
                for (TPOIData poi3 : m_allPoisKML.values()) {

                    double metres = poi.distance(poi3);
                    if(metres<minMetres) {
                        minMetres=metres;
                        closestPoi = poi3;
                    }
                    
                    if (metres < 20.0) {
                        foundOne = true;
                        //System.out.println("- " + metres + " - " + poi3.getName() + " - " + poi3.getExtraInfo() + " - " + poi3);
                    }
                }
                if(!foundOne) {
                    System.out.println("\n\n");
                    System.out.println("POI was missed: " + poi.getName() + " - " + poi.getExtraInfo() + " - " + poi);
                    System.out.println("+ " + minMetres + " - " + closestPoi.getName() + " - " + closestPoi.getExtraInfo() + " - " + closestPoi);
                }

            } else {
                // double metres = poi.distance(poi2);
                // if (metres > 2) {
                // System.out.println("POI with different possition: " + metres);
                // System.out.println("-   " + poi2.getName() + " - " + poi2.getExtraInfo() + " - " + poi2);
                // System.out.println("    " + poi.getName() + " - " + poi.getExtraInfo() + " - " + poi);
                // }
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _readAllData(String fname) throws Exception {

        File allPoisFile = new File(s_allPath.toFile(), fname);
        TPOIFileData fileData = BINFileLoader.loadFile(allPoisFile);

        for (TPOIData poi : fileData.getAllPOIs()) {
            String key = poi.getName() + "#" + poi.getLat() + "#" + poi.getLng();
            TPOIData prevPoi = m_allPois.put(key, poi);
            if (prevPoi != null) {
                System.out.println("Warning: Duplicated POI?: " + prevPoi);
            }
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private void _saveAllData(String fname) throws Exception {

        File allPoisFile = new File(s_allPath.toFile(), fname);
        BINFileWriter.saveFile(allPoisFile, new ArrayList<TPOIData>(m_allPois.values()));
    }

    // ----------------------------------------------------------------------------------------------------
    private void _findDuplicated() throws Exception {

        HashMap<String, ArrayList<TPOIData>> poisByName = new HashMap<>();

        for (TPOIData poi : m_allPois.values()) {

            ArrayList<TPOIData> list = poisByName.get(poi.getName());
            if (list == null) {
                list = new ArrayList<TPOIData>();
                poisByName.put(poi.getName(), list);
            }
            list.add(poi);
        }

        m_allPois.clear();

        for (ArrayList<TPOIData> list : poisByName.values()) {
            if (list.size() > 1) {

                TreeMap<String, TPOIData> sortedPois = new TreeMap<>();
                int n = 0;
                for (TPOIData fpoi : list) {
                    double sumMetres = 0.0;
                    for (TPOIData npoi : list) {
                        double metres = fpoi.distance(npoi);
                        sumMetres += metres;
                    }
                    sortedPois.put(sumMetres + " " + n, fpoi);
                    n++;
                }
                TPOIData poi = sortedPois.firstEntry().getValue();
                String key = poi.getName() + "#" + poi.getLat() + "#" + poi.getLng();
                m_allPois.put(key, poi);

            } else {
                TPOIData poi = list.get(0);
                String key = poi.getName() + "#" + poi.getLat() + "#" + poi.getLng();
                m_allPois.put(key, poi);
            }

        }

        _saveAllData("allPois_KML_2.bin");
    }

    // ----------------------------------------------------------------------------------------------------
    private void _processFolder() throws Exception {

        Files.walkFileTree(s_allPath, new SimpleFileVisitor<Path>() {

            @SuppressWarnings("synthetic-access")
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Objects.requireNonNull(file);
                Objects.requireNonNull(attrs);

                if (file.getFileName().toString().toLowerCase().endsWith(".kml") //
                        || file.getFileName().toString().toLowerCase().endsWith(".ov2")) {
                    _processFile(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    // ----------------------------------------------------------------------------------------------------
    private void _processFile(Path file) {

        TPOIFileData info;

        String fileNameStr = file.getFileName().toString();

        try {
            System.out.println("Procesing file: " + file);

            if (file.toString().endsWith(".kml")) {
                info = KMLFileLoader.loadFile(file.toFile());
            } else {
                info = OV2FileLoader.loadFile(file.toFile());
            }

            for (TPOIData poi : info.getAllPOIs()) {

                if (poi.getName().contains("Nanterre-Universit")) {
                    System.out.println("---");
                }
                poi.setExtraInfo(fileNameStr);

                String key = poi.getName() + "#" + poi.getLat() + "#" + poi.getLng();

                TPOIData prevPoi = m_allPois.put(key, poi);
                if (prevPoi != null) {
                    if (poi.getIconStyle() == null && prevPoi.getIconStyle() != null) {
                        m_allPois.put(key, prevPoi);
                        System.out.println("  POI duplicated: " + poi);
                    }
                }
            }

        } catch (Throwable th) {
            System.out.println("Error processing file: " + file);
            System.out.println(th);
            th.printStackTrace();
        }

    }
}
