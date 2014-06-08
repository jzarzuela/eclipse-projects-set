/**
 * 
 */
package com.jzb.ttpoi;

import java.io.File;
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
 * @author n63636
 * 
 */
public class AllTest {

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
            AllTest me = new AllTest();
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

        File kmlFolder = new File("/Users/jzarzuela/Downloads/_tmp_/pois/_KMLs_");
        kmlFolder.mkdirs();
        File ov2Folder = new File("/Users/jzarzuela/Downloads/_tmp_/pois/_OV2s_");
        ov2Folder.mkdirs();

        KMLDownload.downloadAllMaps(kmlFolder);
        FileTransform.transformAllKMLtoOV2(kmlFolder, ov2Folder, true);
    }

    public void doIt2(String[] args) throws Exception {

        File folder = new File("/Users/jzarzuela/Desktop/backup/2014-04-10_11-26-47_GMT+2/");

        File localKml = new File(folder, "HT_Galicia_2014-local.kml");
        File remoteKml = new File(folder, "HT_Galicia_2014-remote.kml");

        TPOIFileData localData = KMLFileLoader.loadFile(localKml);
        TPOIFileData remoteData = KMLFileLoader.loadFile(remoteKml);

        // Comparador para ordenar por nombre
        Comparator<TPOIData> comp = new Comparator<TPOIData>() {

            public int compare(TPOIData o1, TPOIData o2) {
                return o1.getName().compareTo(o2.getName());
            }

        };

        Collections.sort(localData.getAllPOIs(), comp);
        Collections.sort(remoteData.getAllPOIs(), comp);

        for (TPOIData poi : localData.getAllPOIs()) {
            poi.setLat(0);
            poi.setLng(0);
            poi.setDesc("");
        }

        for (TPOIData poi : remoteData.getAllPOIs()) {
            poi.setLat(0);
            poi.setLng(0);
            poi.setDesc("");
        }
        
        KMLFileWriter.saveFile(new File(folder, "PREP_Holanda-local-comp.kml"), localData);
        KMLFileWriter.saveFile(new File(folder, "PREP_Holanda-remote-comp.kml"), remoteData);
    }

    public void doIt4(String[] args) throws Exception {

        File kmlFolder = new File("/Users/jzarzuela/Desktop/pois/_KMLs_");
        kmlFolder.mkdirs();
        File ov2Folder = new File("/Users/jzarzuela/Desktop/pois/_OV2s_");
        ov2Folder.mkdirs();

        File kmlFile = new File(kmlFolder, "BT_Boston_2010_2013.kml");

        HashMap<String, String> styleCatMap = new HashMap<String, String>(ConversionUtil.getDefaultParseCategories());
        styleCatMap.put("green-dot", "Hardvard");
        styleCatMap.put("red", "Salem");
        styleCatMap.put("red-dot", "Salem");
        styleCatMap.put("yellow-dot", "Costa-Sur");
        styleCatMap.put("hiker_maps", "FreedomTrial");
        styleCatMap.put("blue-dot", "Boston");
        styleCatMap.put("ltblue-dot", "Interior");
        styleCatMap.put("purple-dot", "Costa-Norte");

        boolean nameSorted = true;
        FileTransform.transformKMLtoOV2("BSTN", kmlFile, ov2Folder, nameSorted);
    }

    public void doIt3(String[] args) throws Exception {

        boolean nameSorted = true;

        File kmlFolder = new File("/Users/jzarzuela/Desktop/pois/_KMLs_");
        kmlFolder.mkdirs();
        File ov2Folder = new File("/Users/jzarzuela/Desktop/pois/_OV2s_");
        ov2Folder.mkdirs();

        File kmlFile;
        ConversionUtil.getDefaultParseCategories().clear();
        kmlFile = new File(kmlFolder, "BT_SFCO_2008.kml");
        FileTransform.transformKMLtoOV2("SCFCO", kmlFile, ov2Folder, nameSorted);

        ConversionUtil.getDefaultParseCategories().clear();
        kmlFile = new File(kmlFolder, "BT_Miami_2010.kml");
        FileTransform.transformKMLtoOV2("Miami", kmlFile, ov2Folder, nameSorted);

        ConversionUtil.getDefaultParseCategories().clear();
        kmlFile = new File(kmlFolder, "BT_Las Vegas_2008-2011.kml");
        FileTransform.transformKMLtoOV2("Las Vegas", kmlFile, ov2Folder, nameSorted);

    }
}
