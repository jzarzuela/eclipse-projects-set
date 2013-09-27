/**
 * 
 */
package com.jzb.ttpoi.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import net.sourceforge.jFuzzyLogic.FIS;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileLoader;
import com.jzb.ttpoi.wl.KMLFileWriter;
import com.jzb.ttpoi.wl.OV2FileLoader;

/**
 * @author jzarzuela
 * 
 */
public class CheckDuplicatePOIs {

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
            CheckDuplicatePOIs me = new CheckDuplicatePOIs();
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

        // File baseKmlFolder = new File("/Users/jzarzuela/Downloads/_tmp_/pois/_KMLs_");
        File baseKmlFolder = new File("/Users/jzarzuela/Desktop/pp");
        File baseOv2Folder = new File("/Users/jzarzuela/Downloads/_tmp_/found_ov2s");

        System.out.println("---------------------------------------------------------------------------");
        System.out.println("Reading POI files");
        ArrayList<TPOIData> poisKml = readAllPoiFiles(baseKmlFolder, true);
        //ArrayList<TPOIData> poisOv2 = readAllPoiFiles(baseOv2Folder, false);

        System.out.println();
        System.out.println("---------------------------------------------------------------------------");
        System.out.println("---------------------------------------------------------------------------");
        System.out.println("Checking POIs for duplicates in " + (poisKml.size()) + " pois");
        //checkForDuplicatesTwoFiles(poisOv2, poisKml);
        checkForDuplicatesOneFile(poisKml);

        TPOIFileData fileData = new TPOIFileData();
        ArrayList<TPOIData> allPois = new ArrayList<TPOIData>();

        File duplicatedFile = new File("/Users/jzarzuela/Desktop/duplicatedFile.kml");
        duplicatedFile.delete();
        if (duplicated.size() > 0) {
            fileData.setName("duplicatedFile");
            allPois.addAll(duplicated);
            allPois.clear();
            fileData.setAllPOIs(allPois);
            KMLFileWriter.saveFile(duplicatedFile, fileData);
        }

        File distinctFile = new File("/Users/jzarzuela/Desktop/distinctFile.kml");
        distinctFile.delete();
        if (distinct.size() > 0) {
            fileData.setName("distinctFile");
            allPois.clear();
            allPois.addAll(distinct);
            fileData.setAllPOIs(allPois);
            KMLFileWriter.saveFile(distinctFile, fileData);
        }
    }

    private HashSet<TPOIData> distinct   = new HashSet<TPOIData>();
    private HashSet<TPOIData> duplicated = new HashSet<TPOIData>();

    private void checkForDuplicatesTwoFiles(ArrayList<TPOIData> pois1, ArrayList<TPOIData> pois2) throws Exception {

        long t1 = System.currentTimeMillis();

        FIS fis = loadFuzzyLogic();

        for (int n = 0; n < pois1.size(); n++) {

            long incT = (System.currentTimeMillis() - t1);
            if (n % 50 == 0) {
                System.out.println("---- " + n + " of " + pois1.size() + "  [" + (n > 0 ? (incT * pois1.size() / n) / 60000 : -1) + "]");
            }

            TPOIData poi1 = pois1.get(n);

            String poiName1 = null;
            String poiPrefix1 = null;
            String poifullName1 = poi1.getName().toLowerCase();

            int p1 = poifullName1.indexOf('-');
            if (p1 > 0) {
                poiName1 = poifullName1.substring(p1);
                poiPrefix1 = poifullName1.substring(0, p1);
            } else {
                poiName1 = poifullName1;
                poiPrefix1 = null;
            }

            boolean wasDuplicated = false;
            for (int i = 0; i < pois2.size(); i++) {

                TPOIData poi2 = pois2.get(i);

                String poiName2 = null;
                String poiPrefix2 = null;
                String poifullName2 = poi2.getName().toLowerCase();

                int p2 = poifullName2.indexOf('-');
                if (p2 > 0) {
                    poiName2 = poifullName2.substring(p2);
                    poiPrefix2 = poifullName2.substring(0, p2);
                } else {
                    poiName2 = poifullName2;
                    poiPrefix2 = null;
                }

                String text1, text2;
                if (poiPrefix1 == null || poiPrefix2 == null || poiPrefix1.equals(poiPrefix2)) {
                    text1 = poiName1;
                    text2 = poiName2;
                } else {
                    text1 = poifullName1;
                    text2 = poifullName2;
                }

                // Calcula la "similitud" entre los nombres
                // Y chequea si solo hay un cambio de 2 numeros en la diferencia para ajustarlo
                int distSyn = LevenshteinDistance.compute(text1, text2);
                double nameDist = 100.0 * (double) distSyn / (double) Math.max(text1.length(), text2.length());
                if (distSyn > 0 && distSyn < 3 && checkNumericDiff(text1, text2))
                    nameDist = 50.0; // Regular

                // Calcula la distancia y la ajusta si es mayor que 5Km.
                double distKM = poi1.distance(poi2);
                if (distKM > 5000)
                    distKM = 5000;

                // Evalua las reglas de logica difusa
                fis.setVariable("name", nameDist);
                fis.setVariable("distance", distKM);
                fis.evaluate();
                double ratio = fis.getVariable("duplicated").defuzzify();

                // Muy Parecido < 25. Se pone algo mas restringuido
                if (ratio < 20) {
                    /*
                     * System.out.println("--->\t" + ratio + " \t " + distKM + " \t " + nameDist + " \t " + distSyn); System.out.println("  '" + poi1.getName() + "' \t -> \t" + poi1.getDesc());
                     * System.out.println("  '" + poi2.getName() + "' \t -> \t" + poi2.getDesc()); System.out.println();
                     */
                    wasDuplicated = true;
                    break;
                }
            }

            if (!wasDuplicated) {
                System.out.println("-->  '" + poi1.getName() + "' \t -> \t" + poi1.getDesc());
                distinct.add(poi1);
            }
        }
    }

    private void checkForDuplicatesOneFile(ArrayList<TPOIData> pois) throws Exception {

        FIS fis = loadFuzzyLogic();

        for (int n = 0; n < pois.size(); n++) {
            if (n % 50 == 0) {
                System.out.println("---- " + n + " of " + pois.size());
            }

            TPOIData poi1 = pois.get(n);

            if (duplicated.contains(poi1))
                continue;
            distinct.add(poi1);

            String poiName1 = null;
            String poiPrefix1 = null;
            String poifullName1 = poi1.getName().toLowerCase();

            int p1 = poifullName1.indexOf('-');
            if (p1 > 0) {
                poiName1 = poifullName1.substring(p1);
                poiPrefix1 = poifullName1.substring(0, p1);
            } else {
                poiName1 = poifullName1;
                poiPrefix1 = null;
            }

            for (int i = n + 1; i < pois.size(); i++) {

                TPOIData poi2 = pois.get(i);
                if (duplicated.contains(poi2))
                    continue;

                String poiName2 = null;
                String poiPrefix2 = null;
                String poifullName2 = poi2.getName().toLowerCase();

                int p2 = poifullName2.indexOf('-');
                if (p2 > 0) {
                    poiName2 = poifullName2.substring(p2);
                    poiPrefix2 = poifullName2.substring(0, p2);
                } else {
                    poiName2 = poifullName2;
                    poiPrefix2 = null;
                }

                String text1, text2;
                if (poiPrefix1 == null || poiPrefix2 == null || poiPrefix1.equals(poiPrefix2)) {
                    text1 = poiName1;
                    text2 = poiName2;
                } else {
                    text1 = poifullName1;
                    text2 = poifullName2;
                }

                // Calcula la "similitud" entre los nombres
                // Y chequea si solo hay un cambio de 2 numeros en la diferencia para ajustarlo
                int distSyn = LevenshteinDistance.compute(text1, text2);
                double nameDist = 100.0 * (double) distSyn / (double) Math.max(text1.length(), text2.length());
                if (distSyn > 0 && distSyn < 3 && checkNumericDiff(text1, text2))
                    nameDist = 50.0; // Regular

                // Calcula la distancia y la ajusta si es mayor que 5Km.
                double distKM = poi1.distance(poi2);
                if (distKM > 5000)
                    distKM = 5000;

                // Evalua las reglas de logica difusa
                fis.setVariable("name", nameDist);
                fis.setVariable("distance", distKM);
                fis.evaluate();
                double ratio = fis.getVariable("duplicated").defuzzify();

                // Muy Parecido < 25. Se pone algo mas restringuido
                if (ratio < 20) {
                    System.out.println("--->\t" + ratio + " \t " + distKM + " \t " + nameDist + " \t " + distSyn);
                    System.out.println("  '" + poi1.getName() + "' \t -> \t" + poi1.getDesc());
                    System.out.println("  '" + poi2.getName() + "' \t -> \t" + poi2.getDesc());
                    System.out.println();

                    duplicated.add(poi1);
                    duplicated.add(poi2);
                }

            }
        }
    }

    private boolean checkNumericDiff(String name1, String name2) {
        int len = Math.max(name1.length(), name2.length());
        for (int n = 0; n < len; n++) {
            char c1 = n < name1.length() ? name1.charAt(n) : '0';
            char c2 = n < name2.length() ? name2.charAt(n) : '0';
            if (c1 != c2 && (!Character.isDigit(c1) || !Character.isDigit(c2))) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<TPOIData> readAllPoiFiles(File folder, boolean readKML) throws Exception {

        ArrayList<TPOIData> pois = new ArrayList<TPOIData>();

        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                pois.addAll(readAllPoiFiles(f, true));
            } else {

                if (f.getName().startsWith("@"))
                    continue;

                TPOIFileData fileData = null;

                if (readKML && f.getName().toLowerCase().endsWith(".kml")) {
                    System.out.println(f.getName());
                    fileData = KMLFileLoader.loadFile(f);
                }

                if (!readKML && f.getName().toLowerCase().endsWith(".ov2")) {
                    System.out.println(f.getName());
                    fileData = OV2FileLoader.loadFile(f);
                }

                if (fileData != null) {
                    for (TPOIData poi : fileData.getAllPOIs()) {
                        // poi.setDesc(f.getAbsolutePath());
                        //****poi.setDesc(f.getName());
                        pois.add(poi);
                    }
                }
            }
        }

        return pois;
    }

    private FIS loadFuzzyLogic() throws Exception {
        String fileName = "/Users/jzarzuela/Documents/java-Campus/TT_POIs/src/com/jzb/ttpoi/util/duplicado.fcl";
        FIS fis = FIS.load(fileName, true);
        // Error while loading?
        if (fis == null) {
            throw new Exception("Can't load file: '" + fileName + "'");
        }
        return fis;
    }

}
