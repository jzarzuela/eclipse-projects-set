/**
 * 
 */
package com.jzb.ttpoi.util;

import java.io.File;
import java.util.ArrayList;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileLoader;

/**
 * @author jzarzuela
 * 
 */
public class FindDuplicated {

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
            FindDuplicated me = new FindDuplicated();
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

        File kmlFile1 = new File("/Users/jzarzuela/Downloads/_tmp_/pois/_KMLs_/BT_SFCO_2008.kml");
        File kmlFile2 = new File("/Users/jzarzuela/Desktop/tmp_pois/#TMP_SFCO.kml");
        
        
        ArrayList<TPOIData> allPois = new ArrayList<TPOIData>();
        ArrayList<TPOIData> distinct = new ArrayList<TPOIData>();
        ArrayList<TPOIData> duplicated = new ArrayList<TPOIData>();
        
        TPOIFileData fileData1 = KMLFileLoader.loadFile(kmlFile1);
        TPOIFileData fileData2 = KMLFileLoader.loadFile(kmlFile2);
        
        allPois.addAll(fileData1.getAllPOIs());
        allPois.addAll(fileData2.getAllPOIs());
        
        DuplicatedPOIsChecker.checkDuplicated(true, allPois, distinct, duplicated);
        
        System.out.println("--- Duplicated POIs ---");
        for(TPOIData poi:duplicated) {
            System.out.println(poi.getName());
            
            //if(fileData1.getAllPOIs().contains(poi)) {
            //    System.out.println("File that contains duplicated POI: "+fileData1.getFileName());
            //}
            
            if(fileData2.getAllPOIs().contains(poi)) {
                System.out.println("File that contains duplicated POI: "+fileData2.getFileName());
            }
        }
        
        

    }
}
