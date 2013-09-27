/**
 * 
 */
package com.jzb.ttpoi.util;

import java.io.File;
import java.util.ArrayList;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileLoader;
import com.jzb.ttpoi.wl.KMLFileWriter;

/**
 * @author jzarzuela
 * 
 */
public class MixKMLTogether {

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
            MixKMLTogether me = new MixKMLTogether();
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

        File kmlFile1 = new File("/Users/jzarzuela/Downloads/_tmp_/pois/_KMLs_/PV_SitiosInteres.kml");
        File kmlFile2 = new File("/Users/jzarzuela/Desktop/tmp_pois/#TMP_Varios.kml");
        
        
        ArrayList<TPOIData> allPois = new ArrayList<TPOIData>();
        
        TPOIFileData fileData1 = KMLFileLoader.loadFile(kmlFile1);
        TPOIFileData fileData2 = KMLFileLoader.loadFile(kmlFile2);

        for(TPOIData poi:fileData1.getAllPOIs()) {
            poi.setIconStyle("http://maps.gstatic.com/mapfiles/ms2/micons/blue-dot.png");
            poi.setCategory(fileData1.getName());
        }
        allPois.addAll(fileData1.getAllPOIs());
        
        for(TPOIData poi:fileData2.getAllPOIs()) {
            poi.setIconStyle("http://maps.gstatic.com/mapfiles/ms2/micons/red-dot.png");
            poi.setCategory(fileData2.getName());
        }
        allPois.addAll(fileData2.getAllPOIs());

        File kmlFileOUT = new File(kmlFile2.getParentFile(),"#MIX_"+kmlFile2.getName());
        fileData1.setAllPOIs(allPois);
        KMLFileWriter.saveFile(kmlFileOUT,fileData1);

    }
}
