/**
 * 
 */
package com.jzb.ttpoi;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileWriter;
import com.jzb.ttpoi.wl.OV2FileLoader;

/**
 * @author n63636
 * 
 */
public class POICompare {

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
            POICompare me = new POICompare();
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

        File src1 = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\kk.ov2");
        File src2 = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_OV2_\\BT_Boston_2010\\BT_Boston_2010_ALL.ov2");

        ArrayList<File> srcs1 = new ArrayList<File>();
        srcs1.add(src1);
        ArrayList<File> srcs2 = new ArrayList<File>();
        srcs2.add(src2);

        ArrayList<TPOIData> pois1 = _loadPOIS(srcs1);
        ArrayList<TPOIData> pois2 = _loadPOIS(srcs2);
        _compare(pois1, pois2);

        /**/
        TPOIFileData fdata = new TPOIFileData();
        fdata.setName("difPOIs");
        for (TPOIData poi : pois1) {
            if (!poi.getName().contains("TT_CAT"))
                fdata.addPOI(poi);
        }
        for (TPOIData poi : pois2) {
            if (!poi.getName().contains("TT_CAT"))
                fdata.addPOI(poi);
        }

        KMLFileWriter.saveFile(new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\dif.kml"), fdata);
        /**/

    }

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt2(String[] args) throws Exception {

        File src1Folder = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\src");
        TreeMap<String, ArrayList<File>> source1Files = _getSource1POIs(src1Folder);

        for (Map.Entry<String, ArrayList<File>> entry : source1Files.entrySet()) {
            File src2Folder = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_OV2_\\" + entry.getKey());
            ArrayList<File> source2Files = _getSource2POIs(src2Folder);

            if (source2Files != null) {
                System.out.println();
                System.out.println("----------------------------------------------------------------------------------");
                ArrayList<TPOIData> pois1 = _loadPOIS(entry.getValue());
                ArrayList<TPOIData> pois2 = _loadPOIS(source2Files);
                _compare(pois1, pois2);
            }
        }

    }

    private ArrayList<File> _getSource2POIs(File baseFolder) throws Exception {

        if (!baseFolder.exists()) {
            System.out.println("Folder doesn't exist:" + baseFolder);
            return null;
        }

        ArrayList<File> files = new ArrayList<File>();
        for (File f : baseFolder.listFiles()) {
            if (!f.getName().endsWith("ALL.ov2"))
                continue;
            files.add(f);
        }

        return files;
    }

    private TreeMap<String, ArrayList<File>> _getSource1POIs(File baseFolder) throws Exception {
        TreeMap<String, ArrayList<File>> files = new TreeMap<String, ArrayList<File>>();
        for (File f : baseFolder.listFiles()) {
            if (!f.getName().endsWith(".ov2"))
                continue;

            String bn = f.getName();

            int p1 = bn.indexOf("_20", 9);
            if (p1 > 0)
                p1 = bn.indexOf('_', p1 + 1);
            if (p1 < 0)
                p1 = bn.indexOf('.');

            bn = bn.substring(0, p1);

            ArrayList<File> lf = files.get(bn);
            if (lf == null) {
                lf = new ArrayList<File>();
                files.put(bn, lf);
            }
            lf.add(f);
        }
        return files;
    }

    private void _compare(ArrayList<TPOIData> pois1, ArrayList<TPOIData> pois2) {

        Iterator<TPOIData> iter1, iter2;

        iter1 = pois1.iterator();
        while (iter1.hasNext()) {
            TPOIData poi1 = iter1.next();

            iter2 = pois2.iterator();
            while (iter2.hasNext()) {
                TPOIData poi2 = iter2.next();

                if (poi1.getName().equals(poi2.getName())) {
                    double dif1 = Math.abs(poi1.getLat() - poi2.getLat());
                    double dif2 = Math.abs(poi1.getLng() - poi2.getLng());
                    if (dif1 > 2.0E-5 || dif2 > 2.0E-5) {
                        System.out.println("POI found with same name but diferent data:");
                        System.out.println("  " + poi1.getName());
                        System.out.println("  dif1=" + dif1 + ", dif2=" + dif2);
                    }

                    iter1.remove();
                    iter2.remove();
                    break;
                }
            }

        }

        iter2 = pois2.iterator();
        while (iter2.hasNext()) {
            TPOIData poi2 = iter2.next();
            iter1 = pois1.iterator();
            while (iter1.hasNext()) {
                TPOIData poi1 = iter1.next();

                if (poi1.getName().equals(poi2.getName())) {
                    double dif1 = Math.abs(poi1.getLat() - poi2.getLat());
                    double dif2 = Math.abs(poi1.getLng() - poi2.getLng());
                    if (dif1 > 2.0E-5 || dif2 > 2.0E-5) {
                        System.out.println("POI found with same name but diferent data:");
                        System.out.println("  " + poi1.getName());
                        System.out.println("  dif1=" + dif1 + ", dif2=" + dif2);
                    }
                    iter1.remove();
                    iter2.remove();
                    break;
                }
            }
        }

        System.out.println("POIs mismatched in Source1:");
        for (TPOIData poi : pois1) {
            System.out.println("  " + poi);
        }

        System.out.println("POIs mismatched in Source2:");
        for (TPOIData poi : pois2) {
            System.out.println("  " + poi);
        }
    }

    private ArrayList<TPOIData> _loadPOIS(ArrayList<File> files) throws Exception {

        ArrayList<TPOIData> pois = new ArrayList<TPOIData>();

        for (File ov2File : files) {
            System.out.println("Processing file: " + ov2File);
            TPOIFileData info = OV2FileLoader.loadFile(ov2File);
            pois.addAll(info.getAllPOIs());
        }
        return pois;

    }
}
