/**
 * 
 */
package com.jzb.ttpoi;

import java.io.File;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.util.KMLDownload;
import com.jzb.ttpoi.wl.KMLFileLoader;

/**
 * @author n63636
 * 
 */
public class GMapsToPList {

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
            GMapsToPList me = new GMapsToPList();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }

    // ----------------------------------------------------------------------------------------------------
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

        File plistFolder = new File("/Users/jzarzuela/Downloads/_tmp_/pois/_PLISTs_");
        plistFolder.mkdirs();

        _cleanPrevFiles(kmlFolder);
        _cleanPrevFiles(plistFolder);

        KMLDownload.downloadAllMaps(kmlFolder);

        _transformKmlFiles(kmlFolder, plistFolder);
    }

    // ----------------------------------------------------------------------------------------------------
    private void _cleanPrevFiles(File kmlFolder) throws Exception {

        for (File file : kmlFolder.listFiles()) {
            file.delete();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _transformKmlFiles(File kmlFolder, File plistFolder) throws Exception {

        for (File kmlFile : kmlFolder.listFiles()) {

            if (!kmlFile.getName().toLowerCase().endsWith(".kml"))
                continue;

            _transformKMLtoPList(kmlFile, plistFolder, false);

        }

    }

    // ----------------------------------------------------------------------------------------------------
    private void _transformKMLtoPList(File kmlFile, File plistFolder, boolean nameSorted) throws Exception {

        System.out.println("Processing map: " + kmlFile);

        TPOIFileData info = KMLFileLoader.loadFile(kmlFile);

        // Comparador para ordenar por nombre si se pidio
        if (nameSorted) {
            Collections.sort(info.getAllPOIs(), new Comparator<TPOIData>() {

                public int compare(TPOIData o1, TPOIData o2) {
                    return o1.getName().compareTo(o2.getName());
                }

            });
        }

        // Graba el fichero equivalente
        int p1 = kmlFile.getName().lastIndexOf('.');
        String mapName = kmlFile.getName().substring(0, p1);
        File plistFile = new File(plistFolder, mapName + ".plist");
        _writePListMap(plistFile, info);

    }

    // ----------------------------------------------------------------------------------------------------
    private void _writePListMap(File plistFile, TPOIFileData mapInfo) throws Exception {

        PrintStream ps = new PrintStream(plistFile, "UTF-8");
        
        ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        ps.println("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
        ps.println("<plist version=\"1.0\">");
        ps.println("<dict>");
        ps.println("  <key>mapName</key>");
        ps.println("  <string>"+mapInfo.getName()+"</string>");
        ps.println("  <key>pois</key>");
        ps.println("  <array>");
        int index = 1;
        for(TPOIData poi:mapInfo.getAllPOIs()) {
            _writePListMapPOI(ps, poi, index++);
        }
        ps.println("  </array>");
        _writePListMapTagsSection(ps, mapInfo.getName());
        ps.println("</dict>");
        ps.println("</plist>");
    }

    // ----------------------------------------------------------------------------------------------------
    private void _writePListMapPOI(PrintStream ps, TPOIData poi, int index) throws Exception {

        ps.println("    <dict>");
        ps.println("      <key>index</key>");
        ps.println("      <integer>"+index+"</integer>");
        ps.println("      <key>name</key>");
        ps.println("      <string><![CDATA["+poi.getName()+"]]></string>");
        ps.println("      <key>desc</key>");
        ps.println("      <string><![CDATA["+poi.getDesc()+"]]></string>");
        ps.println("      <key>icon</key>");
        ps.println("      <string><![CDATA["+poi.getIconStyle()+"]]></string>");
        ps.println("      <key>lat</key>");
        ps.println("      <real>"+poi.getLat()+"</real>");
        ps.println("      <key>lng</key>");
        ps.println("      <real>"+poi.getLng()+"</real>");
        ps.println("    </dict>");
    }
    
    // ----------------------------------------------------------------------------------------------------
    private void _writePListMapTagsSection(PrintStream ps, String map_name) throws Exception {
        
        ps.println("  <key>tags</key>");
        ps.println("  <array>");
        
        ps.println("    <dict>");
        ps.println("      <key>index</key>");
        ps.println("      <integer>1</integer>");
        ps.println("      <key>name</key>");
        ps.println("      <string>tag_1_"+map_name+"</string>");
        ps.println("      <key>poiIndexes</key>");
        ps.println("      <array>");
        ps.println("        <string>1</string>");
        ps.println("        <string>2-4</string>");
        ps.println("      </array>");
        ps.println("      <key>tagIndexes</key>");
        ps.println("      <array>");
        ps.println("          <string>2</string>");
        ps.println("          <string>4-5</string>");
        ps.println("      </array>");
        ps.println("    </dict>");
        
        ps.println("    <dict>");
        ps.println("      <key>index</key>");
        ps.println("      <integer>2</integer>");
        ps.println("      <key>name</key>");
        ps.println("      <string>tag_2_"+map_name+"</string>");
        ps.println("      <key>poiIndexes</key>");
        ps.println("      <array>");
        ps.println("        <string>5-7</string>");
        ps.println("      </array>");
        ps.println("    </dict>");
        
        ps.println("    <dict>");
        ps.println("      <key>index</key>");
        ps.println("      <integer>3</integer>");
        ps.println("      <key>name</key>");
        ps.println("      <string>tag_3_"+map_name+"</string>");
        ps.println("      <key>poiIndexes</key>");
        ps.println("      <array>");
        ps.println("        <string>8-10</string>");
        ps.println("      </array>");
        ps.println("    </dict>");
        
        ps.println("    <dict>");
        ps.println("      <key>index</key>");
        ps.println("      <integer>4</integer>");
        ps.println("      <key>name</key>");
        ps.println("      <string>tag_4_"+map_name+"</string>");
        ps.println("      <key>poiIndexes</key>");
        ps.println("      <array>");
        ps.println("        <string>11-13</string>");
        ps.println("      </array>");
        ps.println("    </dict>");
        
        ps.println("    <dict>");
        ps.println("      <key>index</key>");
        ps.println("      <integer>5</integer>");
        ps.println("      <key>name</key>");
        ps.println("      <string>tag_5_"+map_name+"</string>");
        ps.println("      <key>poiIndexes</key>");
        ps.println("      <array>");
        ps.println("        <string>14-16</string>");
        ps.println("      </array>");
        ps.println("    </dict>");
        
        ps.println("  </array>");
    }

}
