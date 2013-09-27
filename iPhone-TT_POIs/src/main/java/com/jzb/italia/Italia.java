/**
 * 
 */
package com.jzb.italia;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.util.FileTransform;
import com.jzb.ttpoi.util.KMLDownload;
import com.jzb.ttpoi.wl.ConversionUtil;
import com.jzb.ttpoi.wl.KMLFileLoader;
import com.jzb.ttpoi.wl.KMLFileWriter;

/**
 * @author jzarzuela
 * 
 */
public class Italia {

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
            Italia me = new Italia();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
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

        File kmlFolder = new File("/Users/jzarzuela/Desktop/pois/kmls");
        File ov2Folder = new File("/Users/jzarzuela/Desktop/pois/ov2s");

        // Se baja y carga en memoria el mapa
        File kmlFile = KMLDownload.downloadMap(kmlFolder, "HT_Roma_2012");
        // File kmlFile = new File(kmlFolder, "HT_SICILIA_2012.kml");
        TPOIFileData info = KMLFileLoader.loadFile(kmlFile);
/*
        // Itera los puntos buscando los "principales" (segun categoria) y el "resto"
        // Pone cada tipo en la lista adecuada
        ArrayList<TPOIData> rootPoints = new ArrayList<TPOIData>();
        ArrayList<TPOIData> points = new ArrayList<TPOIData>();
        HashMap<TPOIData, ArrayList<TPOIData>> subPoints = new HashMap<TPOIData, ArrayList<TPOIData>>();

        for (TPOIData poi : info.getAllPOIs()) {
            // System.out.println(poi.getName() + " ==> " + poi.getCategory());
            if (poi.getCategory().equals("blue-dot") || poi.getCategory().equals("red-dot") || poi.getCategory().equals("green-dot") || poi.getCategory().equals("fallingrocks")) {

                rootPoints.add(poi);
                subPoints.put(poi, new ArrayList<TPOIData>());
            } else {
                points.add(poi);
            }
        }

        // A–ade los puntos "no principales" a la sublista del principal MAS CERCANO
        for (TPOIData subpoi : points) {

            TPOIData rootpoi = null;
            double distance = Double.MAX_VALUE;
            for (TPOIData poi : rootPoints) {
                double poiDist = subpoi.distance(poi);
                if (poiDist < distance) {
                    distance = poiDist;
                    rootpoi = poi;
                }
            }

            subPoints.get(rootpoi).add(subpoi);
        }

        // Comparador para ordenar por categoria y nombre
        Comparator<TPOIData> catComp = new Comparator<TPOIData>() {

            public int getCatOrdinal(TPOIData o1) {
                String cat = o1.getCategory();
                if (cat.equals("red-dot"))
                    return 1;
                if (cat.equals("blue-dot"))
                    return 2;
                if (cat.equals("green-dot"))
                    return 3;
                if (cat.equals("fallingrocks"))
                    return 3;
                return Integer.MAX_VALUE;
            }

            public int compare(TPOIData o1, TPOIData o2) {
                int ordC1 = getCatOrdinal(o1);
                int ordC2 = getCatOrdinal(o2);
                if (ordC1 == ordC2) {
                    return o1.getName().compareTo(o2.getName());
                    
                     if (o1.getCategory().equals(o2.getCategory())) { return o1.getName().compareTo(o2.getName()); } else { return o1.getCategory().compareTo(o2.getCategory()); }
                     
                } else {
                    return ordC1 - ordC2;
                }
            }

        };

        // Ordena los puntos "principales"
        Collections.sort(rootPoints, catComp);

        // A–ade los puntos "principales" en el orden anterior y, detras de cada uno,
        // los "cercanos" tambien ordenados
        ArrayList<TPOIData> allPoints = new ArrayList<TPOIData>();
        for (TPOIData poi : rootPoints) {
            ArrayList<TPOIData> subpoints = subPoints.get(poi);
            Collections.sort(subpoints, catComp);
            allPoints.add(poi);
            allPoints.addAll(subpoints);
        }

////////
 
        ArrayList<TPOIData> allPoints2 = new ArrayList<TPOIData>();
        for (TPOIData poi : info.getAllPOIs()) {

            //@formatter:off
            if (poi.getCategory().equals("green-dot") | 
                poi.getCategory().equals("green") | 
                poi.getCategory().equals("info") | 
                poi.getCategory().equals("POI") | 
                poi.getCategory().equals("yellow-dot") | 
                poi.getCategory().equals("yellow") | 
                poi.getCategory().equals("yellow")) {

                System.out.println(poi);
                allPoints2.add(poi);
            }
            //@formatter:on
        }

        // Escribe el resultado de vuelta en un fichero KML
        File outKmlFile = new File(kmlFolder, "sicilia.kml");
        TPOIFileData fileData = new TPOIFileData();
        fileData.setName("sicilia");
        fileData.setAllPOIs(allPoints2);
        KMLFileWriter.saveFile(outKmlFile, fileData);
*/
        
        // Escribe el resultado de vuelta en un fichero OV2
        // Antes establece categorias nuevas especificas

        _deleteFolderContent(ov2Folder);

        HashMap<String, String> styleCatMap = ConversionUtil.getDefaultParseCategories();
        styleCatMap.put("hospitals", "Iglesias");
        styleCatMap.put("red-dot", "Otros");
        styleCatMap.put("yellow-dot", "Roma");
        FileTransform.transformKMLtoOV2(null,kmlFile, ov2Folder, false);
    }

    private void _deleteFolderContent(File folder) {
        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                _deleteFolderContent(f);
            }
            f.delete();
        }
    }
}
