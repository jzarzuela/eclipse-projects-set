/**
 * 
 */
package com.jzb.ttpoi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.OV2FileWriter;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class MapSettings {

    private static final byte RECORD_HEADER[] = { 0x04, 0x00, 0x03, 0x00, 0x00, 0x00, 0x04, 0x00, 0x03, 0x00, 0x00, 0x00, 0x08, 0x00 };

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
            MapSettings me = new MapSettings();
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

    static int _getInt(byte[] b, int off) {
        return ((b[off + 0] & 0xFF) << 0) + ((b[off + 1] & 0xFF) << 8) + ((b[off + 2] & 0xFF) << 16) + ((b[off + 3]) << 24);
    }

    private static String _getFName(File f) {
        int p1 = f.getName().lastIndexOf('.');
        if (p1 > 0) {
            return f.getName().substring(0, p1);
        } else {
            return f.getName();
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

        byte buffer[];
        File mapSettingsFile = new File("C:\\Users\\n63636\\Desktop\\xIPAs\\mapsettings.cfg");

        buffer = _readBuffer(mapSettingsFile);

        TPOIFileData fileData = new TPOIFileData();
        fileData.setName(_getFName(mapSettingsFile));

        _processBuffer(buffer, fileData);

        File mapSettingsOV2File = new File("C:\\Users\\n63636\\Desktop\\xIPAs\\mapsettings.ov2");
        OV2FileWriter.saveFile(mapSettingsOV2File, fileData.getAllPOIs());

    }

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
                System.out.print(" · ");
            else
                System.out.print(" " + (char) c + " ");
        }
        System.out.println();
    }

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

        String str = _filterStr(new String(buffer, pos + 34, n));

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
