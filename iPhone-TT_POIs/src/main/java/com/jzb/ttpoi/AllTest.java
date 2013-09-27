/**
 * 
 */
package com.jzb.ttpoi;

import java.io.File;
import java.util.HashMap;

import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.util.FileTransform;
import com.jzb.ttpoi.util.KMLDownload;
import com.jzb.ttpoi.wl.ConversionUtil;
import com.jzb.ttpoi.wl.KMLFileLoader;

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

        //KMLDownload.downloadAllMaps(kmlFolder);
        FileTransform.transformAllKMLtoOV2(kmlFolder, ov2Folder, true);
    }

    
    public void doIt2(String[] args) throws Exception {
        
        File kmlFolder = new File("/Users/jzarzuela/Downloads/_tmp_/pois/_KMLs_");
        kmlFolder.mkdirs();
        File ov2Folder = new File("/Users/jzarzuela/Downloads/_tmp_/pois/_OV2s_");
        ov2Folder.mkdirs();
        
        File kmlFile = new File(kmlFolder, "HT_Belgica_2013.kml");

        
        HashMap<String, String> styleCatMap = ConversionUtil.getDefaultParseCategories();
        styleCatMap.put("blue-dot", "Gante");
        styleCatMap.put("homegardenbusiness", "Gante");
        
        
        styleCatMap.put("green-dot", "Brujas");
        styleCatMap.put("ferry", "Brujas");
        styleCatMap.put("green", "Brujas");
        
        styleCatMap.put("yellow-dot", "Amberes");
        styleCatMap.put("ylw-pushpin", "Amberes");
        
        styleCatMap.put("red-dot", "Bruselas");
        styleCatMap.put("red", "Bruselas");
        styleCatMap.put("plane", "Bruselas");
        styleCatMap.put("caution", "Bruselas");
        
        
        /*
        styleCatMap.put("blue-pushpin", "Upper West");
        styleCatMap.put("ltblue-dot", "LtItaly-ChTown");
        styleCatMap.put("pink-dot", "Lower Manhattan");
        styleCatMap.put("ferry", "Lower Manhattan");
        styleCatMap.put("red", "East Village");
        styleCatMap.put("hiker", "Seaport-Civic Center");
        styleCatMap.put("ylw-pushpin", "Brooklyn");
        styleCatMap.put("camera", "NY_Otros");
        styleCatMap.put("tree", "Central Park");
        styleCatMap.put("rail", "Brooklyn");
        */
        
        
        
        boolean nameSorted = true;
        FileTransform.transformKMLtoOV2("BLG", kmlFile, ov2Folder, nameSorted);
    }

    public void doIt4(String[] args) throws Exception {
        
        File kmlFolder = new File("/Users/jzarzuela/Desktop/pois/_KMLs_");
        kmlFolder.mkdirs();
        File ov2Folder = new File("/Users/jzarzuela/Desktop/pois/_OV2s_");
        ov2Folder.mkdirs();
        
        File kmlFile = new File(kmlFolder, "BT_Boston_2010_2013.kml");

        HashMap<String, String> styleCatMap = ConversionUtil.getDefaultParseCategories();
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
