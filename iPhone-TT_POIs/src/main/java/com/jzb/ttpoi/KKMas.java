/**
 * 
 */
package com.jzb.ttpoi;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileLoader;
import com.jzb.ttpoi.wl.KMLFileWriter;
import com.jzb.ttpoi.wl.OV2FileLoader;
import com.jzb.ttpoi.wl.OV2FileWriter;

/**
 * @author n63636
 * 
 */
public class KKMas {

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
            KKMas me = new KKMas();
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

    public void doIt(String[] args) throws Exception {
        TPOIFileData fd= KMLFileLoader.loadFile(new File("/Users/jzarzuela/Desktop/pp/_KMLs_/HT_Bilbao_2012.kml"));
        OV2FileWriter.saveFile(new File("/Users/jzarzuela/Desktop/pp/_OV2s_/HT_Bilbao_2012.ov2"),fd.getAllPOIs());
    }

    public void doIt5(String[] args) throws Exception {

        File f1 = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_KMLs_\\kk.kml");// HT_NORMANDIA_POIs.kml");
        File f2 = new File("C:\\Users\\n63636\\Desktop\\kk_ord.kml");

        TPOIFileData fData = KMLFileLoader.loadFile(f1);

        Comparator<TPOIData> comp = new Comparator<TPOIData>() {

            public int compare(TPOIData o1, TPOIData o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        Collections.sort(fData.getAllPOIs(), comp);

        String ln = "xyxyxy";
        int i = 9;
        for (TPOIData poi : fData.getAllPOIs()) {

            if (poi.getName().charAt(2) == '-') {
                poi.setName(poi.getName().substring(3));
            }

            if (!poi.getName().startsWith(ln)) {
                i++;
                ln = poi.getName();
            }

            if (!poi.Is_TT_Cat_POI()) {
                // poi.setName(i + "-" + poi.getName());
            }

        }

        KMLFileWriter.saveFile(f2, fData);

    }

    public void doIt4(String[] args) throws Exception {

        File f1 = new File("C:\\Users\\n63636\\Desktop\\BT_San Francisco.ov2");
        File f2 = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_OV2_\\BT_SFCO_2008\\BT_SFCO_2008_ALL.ov2");

        TPOIFileData fData1, fData2;

        fData1 = OV2FileLoader.loadFile(f1);
        fData2 = OV2FileLoader.loadFile(f2);

        fData1.getAllPOIs().removeAll(fData2.getAllPOIs());

        Collections.sort(fData1.getAllPOIs());
        File f3 = new File("C:\\Users\\n63636\\Desktop\\kk_ord.kml");
        KMLFileWriter.saveFile(f3, fData1);

    }

    public void doIt3(String[] args) throws Exception {

        File f1 = new File("C:\\Users\\n63636\\Desktop\\kk.kml");
        File f2 = new File("C:\\Users\\n63636\\Desktop\\kk_ord.kml");
        TPOIFileData fData;

        fData = KMLFileLoader.loadFile(f1);

        Collections.sort(fData.getAllPOIs());
        KMLFileWriter.saveFile(f2, fData);

    }

    public void doIt2(String[] args) throws Exception {

        TPOIFileData fData, allData;

        allData = new TPOIFileData();

        fData = KMLFileLoader.loadFile(new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_KMLs_\\PV_SitiosInteres.kml"));
        allData.addAllPOIs(fData.getAllPOIs());
        fData = KMLFileLoader.loadFile(new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_KMLs_\\PV_Casas.kml"));
        allData.addAllPOIs(fData.getAllPOIs());
        fData = KMLFileLoader.loadFile(new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_KMLs_\\PV_Sitios Interesantes.kml"));
        allData.addAllPOIs(fData.getAllPOIs());
        fData = KMLFileLoader.loadFile(new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_KMLs_\\PV_Trabajo.kml"));
        allData.addAllPOIs(fData.getAllPOIs());
        fData = KMLFileLoader.loadFile(new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_KMLs_\\PV_Varios.kml"));
        allData.addAllPOIs(fData.getAllPOIs());

        Collections.sort(allData.getAllPOIs());
        File f2 = new File("C:\\Users\\n63636\\Desktop\\kk_ord.kml");
        KMLFileWriter.saveFile(f2, allData);

    }

    public void doIt1(String[] args) throws Exception {

        TPOIFileData fData, allData;

        allData = new TPOIFileData();

        File folder = new File("C:\\Users\\n63636\\Desktop\\ipBup\\TT_Iberia");
        for (File f : folder.listFiles()) {
            if (!f.getName().startsWith("PV_") || !f.getName().endsWith(".ov2"))
                continue;

            System.out.println("Reading: " + f);
            fData = OV2FileLoader.loadFile(f);
            Collections.sort(fData.getAllPOIs());
            allData.addAllPOIs(fData.getAllPOIs());

        }

        KMLFileWriter.saveFile(new File("C:\\Users\\n63636\\Desktop\\ipBup\\TT_Iberia\\kk_all.kml"), allData);
    }
}
