/**
 * 
 */
package com.jzb.ttpoi.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.ConversionUtil;
import com.jzb.ttpoi.wl.KMLFileLoader;
import com.jzb.ttpoi.wl.KMLFileWriter;
import com.jzb.ttpoi.wl.OV2FileLoader;
import com.jzb.ttpoi.wl.OV2FileWriter;

/**
 * @author jzarzuela
 * 
 */
public class FileTransform {

    private static boolean s_categoriesShown = false;

    // ---------------------------------------------------------------------------------------------------------
    public static void transformAllKMLtoOV2(File kmlFolder, File ov2Folder, boolean nameSorted) throws Exception {

        _cleanExtensionFromFolder(ov2Folder, ".ov2");

        for (File kmlFile : kmlFolder.listFiles()) {

            if (!kmlFile.getName().toLowerCase().endsWith(".kml"))
                continue;

            transformKMLtoOV2(null, kmlFile, ov2Folder, nameSorted);

        }
    }

    // ---------------------------------------------------------------------------------------------------------
    public static void transformAllOV2toKML(File ov2Folder, File kmlFolder, boolean nameSorted) throws Exception {

        _cleanExtensionFromFolder(ov2Folder, ".kml");

        for (File ov2File : ov2Folder.listFiles()) {

            if (!ov2File.getName().toLowerCase().endsWith(".ov2"))
                continue;

            transformOV2toKML(ov2File, kmlFolder, nameSorted);
        }
    }

    // ---------------------------------------------------------------------------------------------------------
    public static void transformKMLtoOV2(String shortMapName, File kmlFile, File ov2Folder, boolean nameSorted) throws Exception {

        System.out.println("Processing map: " + kmlFile);

        int p1 = kmlFile.getName().lastIndexOf('.');
        String mapName = kmlFile.getName().substring(0, p1);
        if (shortMapName == null)
            shortMapName = mapName;

        File mapFolder = new File(ov2Folder, mapName);
        if (mapFolder.exists()) {
            for (File f : mapFolder.listFiles()) {
                if (f.isFile() && f.getName().endsWith(".ov2")) {
                    if (!f.delete()) {
                        System.out.println("Warning: Can't delete previous OV2 file '" + f + "'");
                    }
                }
                if (f.isFile() && f.getName().endsWith(".png")) {
                    if (!f.delete()) {
                        System.out.println("Warning: Can't delete previous PNG file '" + f + "'");
                    }
                }
            }
        }

        TPOIFileData info = KMLFileLoader.loadFile(kmlFile);

        _showCategories();

        // Comparador para ordenar por nombre
        Comparator<TPOIData> comp = new Comparator<TPOIData>() {

            public int compare(TPOIData o1, TPOIData o2) {
                return o1.getName().compareTo(o2.getName());
            }

        };

        if (nameSorted) {
            Collections.sort(info.getAllPOIs(), comp);
        }

        File ov2File = new File(ov2Folder, mapName + File.separator + shortMapName + "_ALL.ov2");
        ov2File.getParentFile().mkdirs();
        OV2FileWriter.saveFile(ov2File, info.getAllPOIs());
        _saveIcon(null, ov2File);

        if (info.getCategorizedPOIs().size() > 1) {
            for (Map.Entry<String, ArrayList<TPOIData>> entry : info.getCategorizedPOIs().entrySet()) {

                String catName = entry.getKey();

                if (entry.getValue().size() <= 0 || (entry.getValue().size() == 1 && entry.getValue().get(0).Is_TT_Cat_POI()))
                    continue;

                if (catName.equals(TPOIData.UNDEFINED_CATEGORY))
                    ov2File = new File(ov2Folder, mapName + File.separator + shortMapName + ".ov2");
                else
                    ov2File = new File(ov2Folder, mapName + File.separator + shortMapName + "_" + catName + ".ov2");

                ov2File.getParentFile().mkdirs();
                OV2FileWriter.saveFile(ov2File, entry.getValue());

                _saveIcon(entry.getValue(), ov2File);

            }
        }
    }

    // ---------------------------------------------------------------------------------------------------------
    private static void _saveIcon(ArrayList<TPOIData> items, File ov2File) throws Exception {

        String iconStyle = "DEFAULT";

        if (items != null) {
            HashMap<String, Integer> styles = new HashMap<String, Integer>();
            for (TPOIData poi : items) {
                Integer value = styles.get(poi.getIconStyle());
                if (value == null)
                    value = 0;
                styles.put(poi.getIconStyle(), value + 1);
            }

            int maxValue = -1;
            for (Entry<String, Integer> entry : styles.entrySet()) {
                if (maxValue < entry.getValue()) {
                    maxValue = entry.getValue();
                    iconStyle = entry.getKey();
                }
            }
        }

        String iconName = "GMI_" + ConversionUtil.getCategoryFromStyle(iconStyle) + ".bmp";
        InputStream is = FileTransform.class.getClassLoader().getResourceAsStream(iconName);
        if (is == null) {
            System.out.println("  * Warning: There is no icon file for: '" + iconStyle + "'");
            iconName = "GMI_DEFAULT.bmp";
            is = FileTransform.class.getClassLoader().getResourceAsStream(iconName);
            if (is == null)
                return;
        }
        String iconFileName = ov2File.getName().substring(0, ov2File.getName().length() - 3) + "png"; // "bmp"
        File iconFile = new File(ov2File.getParentFile(), iconFileName);
        if (iconFile.exists()) {
            return;
        }

        byte buffer[] = new byte[60000];
        FileOutputStream fos = new FileOutputStream(iconFile);
        for (;;) {
            int len = is.read(buffer);
            if (len < 0)
                break;
            fos.write(buffer, 0, len);
        }
        fos.close();
        is.close();
    }

    // ---------------------------------------------------------------------------------------------------------
    public static void transformOV2toKML(File ov2File, File kmlFolder, boolean nameSorted) throws Exception {

        System.out.println("Processing OV2: " + ov2File);
        int p1 = ov2File.getName().lastIndexOf('.');
        String kmlName = ov2File.getName().substring(0, p1) + ".kml";

        TPOIFileData info = OV2FileLoader.loadFile(ov2File);

        // ÀLimpieza del nomnbre que no recuerdo?
        for (TPOIData poi : info.getAllPOIs()) {
            if (Character.isDigit(poi.getName().charAt(0))) {
                int n = 0;
                while (Character.isDigit(poi.getName().charAt(n)) || Character.isWhitespace(poi.getName().charAt(n))) {
                    n++;
                }
                poi.setName(poi.getName().substring(n));
                poi.toString();
            }
        }

        // Comparador para ordenar por nombre
        Comparator<TPOIData> comp = new Comparator<TPOIData>() {

            public int compare(TPOIData o1, TPOIData o2) {
                return o1.getName().compareTo(o2.getName());
            }

        };

        if (nameSorted) {
            Collections.sort(info.getAllPOIs(), comp);
        }

        File kmlFile = new File(kmlFolder, kmlName);
        kmlFile.getParentFile().mkdirs();
        KMLFileWriter.saveFile(kmlFile, info);

    }

    private static void _showCategories() {
        if (!s_categoriesShown) {
            System.out.println();
            System.out.println();
            System.out.println("---- Default categories mapping ---");
            System.out.println("Sin categoria --> " + TPOIData.UNDEFINED_CATEGORY);
            for (Map.Entry<String, String> entry : ConversionUtil.getDefaultParseCategories().entrySet()) {
                System.out.println(entry.getKey() + " => " + entry.getValue());
            }
            System.out.println("-----------------------------------");
            System.out.println();
            System.out.println();
            s_categoriesShown = true;
        }
    }

    private static void _cleanExtensionFromFolder(File folder, String extension) throws Exception {

        if (!folder.exists())
            return;

        for (File f : folder.listFiles()) {
            if (f.isFile() && f.getName().toLowerCase().endsWith(extension))
                f.delete();
        }
        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                _cleanExtensionFromFolder(f, extension);
                f.delete();
            }

        }
    }

}
