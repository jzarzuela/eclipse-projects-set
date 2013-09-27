/**
 * 
 */
package com.jzb.ttpoi.wl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.jzb.ttpoi.data.TPOIData;

/**
 * @author n63636
 * 
 */
public class OV2FileWriter {

    public static void saveFile(File ov2File, ArrayList<TPOIData> pois) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeRecordT1(baos, pois);
        for (TPOIData poi : pois) {

            if (poi.Is_TT_Cat_POI()) continue;
            
            writeRecordT2(baos, poi);
            
        }
        baos.close();

        FileOutputStream fos = new FileOutputStream(ov2File);
        fos.write(baos.toByteArray());
        fos.close();

    }

    static void setInt(int i, byte[] b, int off) {
        b[off + 0] = (byte) ((i >> 0) & 0xFF);
        b[off + 1] = (byte) ((i >> 8) & 0xFF);
        b[off + 2] = (byte) ((i >> 16) & 0xFF);
        b[off + 3] = (byte) ((i >> 24) & 0xFF);
    }

    private static void writeInt(OutputStream fos, int i) throws Exception {
        byte buffer[] = new byte[4];
        setInt(i, buffer, 0);
        fos.write(buffer);
    }

    private static void writeRecordT1(ByteArrayOutputStream baos, ArrayList<TPOIData> pois) throws Exception {

        double minLat = +Double.MAX_VALUE;
        double maxLat = -Double.MAX_VALUE;
        double minLng = +Double.MAX_VALUE;
        double maxLng = -Double.MAX_VALUE;
        int len = 21;

        for (TPOIData poi : pois) {
            
            if (poi.Is_TT_Cat_POI()) continue;
            
            byte nameANSI[] = ConversionUtil.getStrANSIValue(poi.getName());
            len += 14 + nameANSI.length;
            if (poi.getLat() > maxLat)
                maxLat = poi.getLat();
            if (poi.getLat() < minLat)
                minLat = poi.getLat();
            if (poi.getLng() > maxLng)
                maxLng = poi.getLng();
            if (poi.getLng() < minLng)
                minLng = poi.getLng();
        }

        baos.write(1);
        writeInt(baos, len);
        writeInt(baos, (int) (maxLng * 100000));
        writeInt(baos, (int) (maxLat * 100000));
        writeInt(baos, (int) (minLng * 100000));
        writeInt(baos, (int) (minLat * 100000));
    }

    private static void writeRecordT2(ByteArrayOutputStream baos, TPOIData poi) throws Exception {

        byte nameANSI[] = ConversionUtil.getStrANSIValue(poi.getName());
        baos.write(2);
        writeInt(baos, 1 + 4 + 4 + 4 + 1 + nameANSI.length);
        writeInt(baos, (int) (poi.getLng() * 100000));
        writeInt(baos, (int) (poi.getLat() * 100000));
        writeStr(baos, nameANSI);
    }

    private static void writeStr(OutputStream fos, byte buffer[]) throws Exception {
        fos.write(buffer);
        fos.write(0);
    }

}
