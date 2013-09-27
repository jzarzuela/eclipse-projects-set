/**
 * 
 */
package com.jzb.ttpoi;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileLoader;
import com.jzb.ttpoi.wl.KMLFileWriter;

/**
 * @author n63636
 * 
 */
public class OrderKML {

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
            OrderKML me = new OrderKML();
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

        File kmlInFile = new File("/Users/jzarzuela/Downloads/_tmp_/pois/_KMLs_/HT_Belgica_2013.kml");
        File kmlOutFile = new File("/Users/jzarzuela/Downloads/_tmp_/pois/_KMLs_/HT_Belgica_2013_2.kml");

        TPOIFileData poisData = KMLFileLoader.loadFile(kmlInFile);

        ArrayList<TPOIData> allPOIs = poisData.getAllPOIs();

        Collections.sort(allPOIs, new Comparator<TPOIData>() {

            @SuppressWarnings("synthetic-access")
            public int compare(TPOIData poi1, TPOIData poi2) {
                int cat1 = _getCategoryOrdinal(poi1);
                int cat2 = _getCategoryOrdinal(poi2);

                if (cat1 == cat2) {
                    return poi1.getName().compareTo(poi2.getName());
                } else {
                    return cat1 - cat2;
                }
            }
        });
        _printNames(allPOIs);

        poisData.setAllPOIs(allPOIs);
        KMLFileWriter.saveFile(kmlOutFile, poisData);
    }

    private void _printNames(ArrayList<TPOIData> allPOIs) {

        String lGroupName = "*";
        for (TPOIData poi : allPOIs) {
            String cGroupName = _getPortugalCategoryAlias(poi);
            if (!lGroupName.equals(cGroupName)) {
                lGroupName = cGroupName;
                System.out.println();
                System.out.println(lGroupName);
                System.out.println("-----------------------------------------------");
            }
            System.out.println("  " + poi.getName() + "  " + _getCategoryStars(poi));
        }
    }

    private String _getPortugalCategoryAlias(TPOIData poi) {

        String cat = poi.getCategory().toLowerCase();

        if (cat.equals("water")) {
            return "Playas";
        }

        if (cat.equals("pink")) {
            return "Algarve";
        }
        if (cat.equals("pink-pushpin")) {
            return "Algarve";
        }

        if (cat.equals("purple")) {
            return "Alentejo";
        }
        if (cat.equals("purple-pushpin")) {
            return "Alentejo";
        }

        if (cat.equals("yellow")) {
            return "Extremadura / Ribatejo";
        }
        if (cat.equals("ylw-pushpin")) {
            return "Extremadura / Ribatejo";
        }

        if (cat.equals("ltblue-dot")) {
            return "Las Beiras";
        }
        if (cat.equals("lightblue")) {
            return "Las Beiras";
        }
        if (cat.equals("ltblu-pushpin")) {
            return "Las Beiras";
        }

        if (cat.equals("green-dot")) {
            return "Norte / Minho";
        }
        if (cat.equals("green")) {
            return "Norte / Minho";
        }
        if (cat.equals("grn-pushpin")) {
            return "Norte / Minho";
        }

        if (cat.equals("red-dot")) {
            return "Litoral Lisboeta";
        }
        if (cat.equals("red")) {
            return "Litoral Lisboeta";
        }
        if (cat.equals("red-pushpin")) {
            return "Litoral Lisboeta";
        }

        return "????? -> "+cat;
    }

    private String _getCategoryStars(TPOIData poi) {

        String cat = poi.getCategory().toLowerCase();

        if (cat.equals("water")) {
            return "";
        }

        if (cat.equals("pink")) {
            return "**";
        }
        if (cat.equals("pink-pushpin")) {
            return "*";
        }

        if (cat.equals("purple")) {
            return "**";
        }
        if (cat.equals("purple-pushpin")) {
            return "*";
        }

        if (cat.equals("yellow")) {
            return "**";
        }
        if (cat.equals("ylw-pushpin")) {
            return "*";
        }

        if (cat.equals("ltblue-dot")) {
            return "***";
        }
        if (cat.equals("lightblue")) {
            return "**";
        }
        if (cat.equals("ltblu-pushpin")) {
            return "*";
        }

        if (cat.equals("green-dot")) {
            return "***";
        }
        if (cat.equals("green")) {
            return "**";
        }
        if (cat.equals("grn-pushpin")) {
            return "*";
        }

        if (cat.equals("red-dot")) {
            return "***";
        }
        if (cat.equals("red")) {
            return "**";
        }
        if (cat.equals("red-pushpin")) {
            return "*";
        }

        return "????? -> "+cat;
    }

    private int _getCategoryOrdinal(TPOIData poi) {

        String cat = poi.getCategory().toLowerCase();

        if (cat.equals("water")) {
            return 10;
        }

        if (cat.equals("pink")) {
            return 20;
        }
        if (cat.equals("pink-pushpin")) {
            return 21;
        }

        if (cat.equals("purple")) {
            return 30;
        }
        if (cat.equals("purple-pushpin")) {
            return 31;
        }

        if (cat.equals("yellow")) {
            return 40;
        }
        if (cat.equals("ylw-pushpin")) {
            return 41;
        }

        if (cat.equals("ltblue-dot")) {
            return 50;
        }
        if (cat.equals("lightblue")) {
            return 51;
        }
        if (cat.equals("ltblu-pushpin")) {
            return 52;
        }

        if (cat.equals("green-dot")) {
            return 60;
        }
        if (cat.equals("green")) {
            return 61;
        }
        if (cat.equals("grn-pushpin")) {
            return 62;
        }

        if (cat.equals("red-dot")) {
            return 70;
        }
        if (cat.equals("red")) {
            return 71;
        }
        if (cat.equals("red-pushpin")) {
            return 72;
        }

        return 0;
    }

}
