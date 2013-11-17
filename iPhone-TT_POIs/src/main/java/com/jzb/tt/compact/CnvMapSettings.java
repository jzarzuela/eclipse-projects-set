/**
 * 
 */
package com.jzb.tt.compact;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileWriter;
import com.jzb.ttpoi.wl.OV2FileLoader;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class CnvMapSettings {

    private static final byte RECORD_HEADER[] = { 0x04, 0x00, 0x03, 0x00, 0x00, 0x00, 0x04, 0x00, 0x03, 0x00, 0x00, 0x00, 0x08, 0x00 };
    private static final Path s_allPath       = Paths.get("/Users/jzarzuela/Documents/personal/_POIs_/_TT_CFG_");

    // ----------------------------------------------------------------------------------------------------
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
            CnvMapSettings me = new CnvMapSettings();
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

    // ----------------------------------------------------------------------------------------------------
    static int _getInt(byte[] b, int off) {
        return ((b[off + 0] & 0xFF) << 0) + ((b[off + 1] & 0xFF) << 8) + ((b[off + 2] & 0xFF) << 16) + ((b[off + 3]) << 24);
    }

    // ----------------------------------------------------------------------------------------------------
    private static String _getFName(File f) {
        int p1 = f.getName().lastIndexOf('.');
        if (p1 > 0) {
            return f.getName().substring(0, p1);
        } else {
            return f.getName();
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

        TPOIFileData.warnIfDuplicated = false;
        _processFolder();

    }

    // ----------------------------------------------------------------------------------------------------
    private void _filterDuplicated(TPOIFileData fileData) {

        System.out.println("------------------------------------------------------");
        TPOIData NULL_POI = new TPOIData();

        for (int n = 0; n < fileData.getAllPOIs().size() - 1; n++) {

            TPOIData poi1 = fileData.getAllPOIs().get(n);
            if (poi1 == NULL_POI)
                continue;

            for (int m = n + 1; m < fileData.getAllPOIs().size(); m++) {
                TPOIData poi2 = fileData.getAllPOIs().get(m);

                double metres = poi1.distance(poi2);
                if (metres == 0) {
                    System.out.println();
                    System.out.println(metres);
                    System.out.println("    " + poi1);
                    System.out.println("    " + poi2);
                    fileData.getAllPOIs().set(m, NULL_POI);
                }
            }
        }

        ArrayList<TPOIData> distPois = new ArrayList<>();
        for (TPOIData poi : fileData.getAllPOIs()) {
            if (poi == NULL_POI)
                continue;
            distPois.add(poi);
        }

        Collections.sort(distPois, new Comparator<TPOIData>() {

            @Override
            public int compare(TPOIData o1, TPOIData o2) {
                return o1.getName().compareTo(o2.getName());
            }

        });
        fileData.setAllPOIs(distPois);

    }

    // ----------------------------------------------------------------------------------------------------
    private String _filterStr(String str) throws Exception {

        int pos = str.indexOf(">>");
        if (pos > 0) {
            str = str.substring(pos + 2);
        }

        StringBuffer sb = new StringBuffer();
        for (int n = 0; n < str.length() / 2; n++) {
            sb.append(str.charAt(n * 2));
        }

        return sb.toString();
    }

    // ----------------------------------------------------------------------------------------------------
    private int _findRecord(byte buffer[], int pos) throws Exception {

        for (int x = pos; x < buffer.length - RECORD_HEADER.length; x++) {

            boolean found = true;
            for (int n = x, i = 0; n < buffer.length && i < RECORD_HEADER.length; n++, i++) {
                if (buffer[n] != RECORD_HEADER[i]) {
                    found = false;
                    break;
                }
            }

            if (found) {
                return x;
            }
        }

        return -1;

    }

    // ----------------------------------------------------------------------------------------------------
    @SuppressWarnings("unused")
    private void _printRecord(byte buffer[], int pos) {

        if (pos == -1)
            return;

        for (int n = 0; n < 50; n++) {
            if ((0x00FF & (int) buffer[pos + n]) < 16)
                System.out.print("0");
            System.out.print(Integer.toHexString(0x00FF & (int) buffer[pos + n]));
            System.out.print(" ");
        }
        System.out.println();
        for (int n = 0; n < 50; n++) {
            int c = (0x00FF & (int) buffer[pos + n]);
            if (n < 34)
                System.out.print("   ");
            else if (c < 32)
                System.out.print(" ? ");
            else
                System.out.print(" " + (char) c + " ");
        }
        System.out.println();
    }

    // ----------------------------------------------------------------------------------------------------
    private void _processBuffer(byte buffer[], TPOIFileData fileData) throws Exception {

        int pos = 0;
        for (;;) {

            pos = _findRecord(buffer, ++pos);
            if (pos == -1)
                break;

            // _printRecord(buffer, pos);

            TPOIData poi = _readRecord(buffer, pos);
            fileData.addPOI(poi);

        }

    }

    // ----------------------------------------------------------------------------------------------------
    private void _processFolder() throws Exception {

        final TPOIFileData fileData = new TPOIFileData();
        fileData.setName("mapSettingsFile");

        Files.walkFileTree(s_allPath, new SimpleFileVisitor<Path>() {

            @SuppressWarnings("synthetic-access")
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Objects.requireNonNull(file);
                Objects.requireNonNull(attrs);

                if (file.getFileName().toString().toLowerCase().endsWith(".ov2")) {
                    _processCfgFile(file, fileData);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        _filterDuplicated(fileData);
        _filterDuplicated(fileData);

        System.out.println("------------------------------------------------------");
        for (TPOIData poi : fileData.getAllPOIs()) {
            System.out.println(poi);
        }

        File mapSettingsKmlFile = new File(s_allPath.toFile(), "mapSettingsFile.kml");
        KMLFileWriter.saveFile(mapSettingsKmlFile, fileData);

    }

    // ----------------------------------------------------------------------------------------------------
    private void _processCfgFile(Path file, TPOIFileData fileData) throws IOException {

        try {
            /*
            byte buffer[];
            File mapSettingsFile = file.toFile();

            buffer = _readBuffer(mapSettingsFile);

            _processBuffer(buffer, fileData);
            */
            TPOIFileData fileData2 = OV2FileLoader.loadFile(file.toFile());
            fileData.addAllPOIs(fileData2.getAllPOIs());

        } catch (Throwable th) {
            throw new IOException("Error processing file: " + file, th);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private byte[] _readBuffer(File mapSettingsFile) throws Exception, IOException {

        byte[] buffer;
        buffer = new byte[(int) mapSettingsFile.length()];
        FileInputStream fms = new FileInputStream(mapSettingsFile);
        if (fms.read(buffer) != buffer.length) {
            Tracer._error("No se ha leido el contenido del fichero adecuadamente");
        }
        fms.close();
        return buffer;
    }

    // ----------------------------------------------------------------------------------------------------
    @SuppressWarnings("unused")
    private TPOIData _readRecord(byte buffer[], int pos) throws Exception {

        if (pos == -1)
            return null;

        float lng = _getInt(buffer, pos + 24) / 100000.0f;
        float lat = _getInt(buffer, pos + 28) / 100000.0f;

        int n = 0;
        while ((0x00FF & (int) buffer[pos + 34 + n]) >= 0x20) {
            n++;
        }

        String str = _filterStr(new String(buffer, pos + 34, n, "ISO-8859-1"));

        TPOIData poi = new TPOIData();
        poi.setName(str);
        poi.setLng(lng);
        poi.setLat(lat);
        poi.setDesc("");
        poi.setCategory("TT-MapSettings");

        System.out.println(poi);

        return poi;

    }

}
