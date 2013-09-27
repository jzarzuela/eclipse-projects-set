/**
 * 
 */
package com.jzb.ttpoi.wl;

import java.io.File;
import java.io.FileInputStream;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;

/**
 * @author n63636
 * 
 */
public class OV2FileLoader {

    public static TPOIFileData loadFile(File ov2File) throws Exception {

        TPOIFileData fileData = new TPOIFileData();
        fileData.setFileName(ov2File.getAbsolutePath());
        fileData.setWasKMLFile(false);

        fileData.setName(_getFName(ov2File));

        FileInputStream fis = new FileInputStream(ov2File);
        while (fis.available() > 0) {
            readRecord(fis, fileData);
        }
        fis.close();

        return fileData;
    }

    static int getInt(byte[] b, int off) {
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

    private static int readInt(FileInputStream fis) throws Exception {
        byte buffer[] = new byte[4];
        fis.read(buffer);
        int i = getInt(buffer, 0);
        return i;
    }

    private static void readRecord(FileInputStream fis, TPOIFileData fileData) throws Exception {

        int type = fis.read();
        switch (type) {
            case 1:
                readRecord_T1(fis, fileData);
                break;
            case 0:
            case 2:
                readRecord_T2(fis, fileData);
                break;
            case 3:
                readRecord_T3(fis, fileData);
                break;
            default:
                throw new Exception("Unknow record type: " + type);
        }

    }

    @SuppressWarnings("unused")
    private static void readRecord_T1(FileInputStream fis, TPOIFileData fileData) throws Exception {

        int len = readInt(fis);
        float lng1 = readInt(fis) / 100000.0f;
        float lat1 = readInt(fis) / 100000.0f;
        float lng2 = readInt(fis) / 100000.0f;
        float lat2 = readInt(fis) / 100000.0f;
    }

    private static void readRecord_T2(FileInputStream fis, TPOIFileData fileData) throws Exception {

        int len = readInt(fis);

        float lng = readInt(fis) / 100000.0f;
        float lat = readInt(fis) / 100000.0f;
        String str = readString(fis, len - 14);

        TPOIData poi = new TPOIData();
        poi.setName(str);
        poi.setLng(lng);
        poi.setLat(lat);
        poi.setDesc("");
        poi.setCategory(fileData.getName());

        fileData.addPOI(poi);
    }

    private static void readRecord_T3(FileInputStream fis, TPOIFileData fileData) throws Exception {
        throw new Exception("readRecord_T3 not supported yet!");
    }

    private static String readString(FileInputStream fis, int len) throws Exception {
        byte buffer[] = new byte[len];
        fis.read(buffer);
        fis.read();
        return new String(buffer);
    }

}
