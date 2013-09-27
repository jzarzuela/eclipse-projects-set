/**
 * 
 */
package com.jzb.ttpoi.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileLoader;
import com.jzb.ttpoi.wl.KMLFileWriter;

/**
 * @author jzarzuela
 * 
 */
public class LimpiarPOIs {

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
            LimpiarPOIs me = new LimpiarPOIs();
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

        File distinctFile = new File("/Users/jzarzuela/Desktop/distinctFile_old.kml");
        File distinctFileClean = new File("/Users/jzarzuela/Desktop/distinctFileClean.kml");
        
        String distinctFileName = "/Users/jzarzuela/Desktop/tmp_pois/#TMP_";

        TPOIFileData fileData = KMLFileLoader.loadFile(distinctFile);

        TreeMap<String, ArrayList<TPOIData>> groupedPOIs = new TreeMap<String, ArrayList<TPOIData>>();

        for (TPOIData poi : fileData.getAllPOIs()) {

            String grpName = poi.getDesc();
            int p1 = grpName.indexOf('-');
            if(p1>0) grpName = grpName.substring(p1+1);
            p1 = grpName.indexOf(".ov2");
            if(p1>0) grpName = grpName.substring(0,p1);
            if(grpName.charAt(2)=='_') grpName = grpName.substring(3);
            if(Character.isDigit(grpName.charAt(0)) && grpName.charAt(4)=='_') grpName = grpName.substring(5);
            
            if(grpName.length()>=6) grpName = grpName.substring(0,6);
            
            grpName = grpName.trim();
            if(grpName.equals("Death")) grpName = "Las Ve";
            if(grpName.equals("San Fr")) grpName = "SFCO";
            if(grpName.equals("PN_Zio")) grpName = "Las Ve";
            if(grpName.equals("Sitios")) grpName = "Varios";
            if(grpName.equals("Trabaj")) grpName = "Varios";
            if(grpName.equals("Privad")) grpName = "Varios";
            if(grpName.equals("Casas")) grpName = "Varios";
            if(grpName.equals("")) grpName = "";
            if(grpName.equals("")) grpName = "";
             
            ArrayList<TPOIData> allPois = groupedPOIs.get(grpName);
            if (allPois == null) {
                allPois = new ArrayList<TPOIData>();
                groupedPOIs.put(grpName, allPois);
            }
            allPois.add(poi);
        }
        
        for(String name:groupedPOIs.keySet()) {
            System.out.println("'"+name+"'");
            ArrayList<TPOIData> allPois = groupedPOIs.get(name);
            
            fileData.setName("#TMP_"+allPois.get(0).getDesc());
            fileData.setAllPOIs(allPois);
            File kmlFile = new File(distinctFileName+name+".kml");
            kmlFile.getParentFile().mkdirs();
            KMLFileWriter.saveFile(kmlFile, fileData);
        }

    }
}
