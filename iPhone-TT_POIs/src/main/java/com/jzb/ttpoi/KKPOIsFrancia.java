/**
 * 
 */
package com.jzb.ttpoi;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileLoader;
import com.jzb.ttpoi.wl.OV2FileWriter;

/**
 * @author n63636
 * 
 */
public class KKPOIsFrancia {

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
            KKPOIsFrancia me = new KKPOIsFrancia();
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

    private void _prepareDestFolder(File ov2Folder) throws Exception {
        ov2Folder.mkdirs();
        for (File f : ov2Folder.listFiles()) {
            if (f.getName().toLowerCase().endsWith(".ov2"))
                f.delete();
        }
    }

    public void doIt(String[] args) throws Exception {

        alias.put("parking", "HT_Parking");
        alias.put("rest.", "HT_Restaurantes");
        alias.put("versalles", "HT_Zona Paris");
        alias.put("hotel -", "HT_Hoteles");

        alias.put("Paris", "HT_Zona Paris");
        alias.put("Loira", "HT_Zona Loira");
        alias.put("Dia D", "HT_Zona Dia D");
        alias.put("Otros", "HT_Puntos_Adicionales");

        alias.put("Auray","HT_B_Auray");
        alias.put("Bosque Huelgoat","HT_B_Bosque Huelgoat");
        alias.put("Carnac","HT_B_Carnac");
        alias.put("Concarneau","HT_B_Concarneau");
        alias.put("Dinan","HT_B_Dinan");
        alias.put("Dinard","HT_B_Dinard");
        alias.put("Fougeres","HT_B_Fougeres");
        alias.put("Josselin","HT_B_Josselin");
        alias.put("Locronan","HT_B_Locronan");
        alias.put("Malestroit","HT_B_Malestroit");
        alias.put("Rennes","HT_B_Rennes");
        alias.put("Rochefort en Terre","HT_B_Rochefort en Terre");
        alias.put("Saint Malo","HT_B_Saint Malo");
        alias.put("Vannes","HT_B_Vannes");
        alias.put("Vitre","HT_B_Vitre");
        

        alias.put("Beuvron en Auge","HT_N_Beuvron en Auge");
        alias.put("Deauville","HT_N_Deauville");
        alias.put("Etretat","HT_N_Etretat");
        alias.put("Fecamp","HT_N_Fecamp");
        alias.put("Gerberoy","HT_P_Gerberoy");
        alias.put("Honfleur","HT_N_Honfleur");
        alias.put("Lyons la Foret","HT_N_Lyons la Foret");
        alias.put("Mont Saint Michel","HT_N_Mont Saint Michel");
        alias.put("Rouen","HT_N_Rouen");
        
        File f1 = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_KMLs_\\HT_Francia_2011.kml");
        File ov2Folder = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_OV2_\\HT_Francia_2011\\Nombre");
        _prepareDestFolder(ov2Folder);

        HashMap<String, ArrayList<TPOIData>> ov2POIs = new HashMap<String, ArrayList<TPOIData>>();
        TPOIFileData fData = KMLFileLoader.loadFile(f1);

        _replaceCategories(ov2POIs, fData);

        for (ArrayList<TPOIData> grpData : ov2POIs.values()) {
            File ov2File = new File(ov2Folder, grpData.get(0).getCategory() + ".ov2");
            OV2FileWriter.saveFile(ov2File, grpData);
        }

    }

    /**
     * @param ov2POIs
     * @param fData
     */
    private void _replaceCategories(HashMap<String, ArrayList<TPOIData>> ov2POIs, TPOIFileData fData) {

        for (TPOIData poi : fData.getAllPOIs()) {

            String poiGrp = _getGrpName(poi);

            ArrayList<TPOIData> grpData = ov2POIs.get(poiGrp);
            if (grpData == null) {
                grpData = new ArrayList<TPOIData>();
                ov2POIs.put(poiGrp, grpData);
            }
            poi.setCategory(poiGrp);
            grpData.add(poi);
        }
    }

    /**
     * @param poi
     * @return
     */
    private String _getGrpName(TPOIData poi) {

        String poiGrp;

        poiGrp = _getAlias(poi);
        if (poiGrp == null) {
            int p1 = poi.getName().indexOf('-');
            if (p1 > 0) {
                poiGrp = poi.getName().substring(0, p1 - 1);
            } else {
                poiGrp = poi.getName();
            }
        }

        String s = alias.get(poiGrp);
        if (s != null) {
            poiGrp = s;
        }

        return poiGrp;
    }

    HashMap<String, String> alias = new HashMap<String, String>();

    /**
     * @param fData
     */
    private String _getAlias(TPOIData poi) {

        for (Map.Entry<String, String> entry : alias.entrySet()) {
            if (poi.getName().toLowerCase().contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

}
