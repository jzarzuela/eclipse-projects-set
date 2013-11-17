/**
 * 
 */
package com.jzb.ttpoi.wl;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;

/**
 * @author n63636
 * 
 */
public class BINFileLoader {

    public static TPOIFileData loadFile(File binFile) throws Exception {

        TPOIFileData fileData = new TPOIFileData();
        fileData.setFileName(binFile.getAbsolutePath());
        fileData.setWasKMLFile(false);

        fileData.setName(_getFName(binFile));

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(binFile));
        while (ois.available() > 0) {
            TPOIData poi = _readPoi(ois);
            fileData.addPOI(poi);
        }
        ois.close();

        return fileData;
    }

    private static final String NULL_STR = "NULL_STR";

    private static TPOIData _readPoi(ObjectInputStream ois) throws Exception {

        String sval;
        double dval;

        TPOIData poi = new TPOIData();

        sval = ois.readUTF();
        poi.setCategory(sval.equals(NULL_STR) ? null : sval);

        sval = ois.readUTF();
        poi.setDesc(sval.equals(NULL_STR) ? null : sval);

        sval = ois.readUTF();
        poi.setExtraInfo(sval.equals(NULL_STR) ? null : sval);

        sval = ois.readUTF();
        poi.setIconStyle(sval.equals(NULL_STR) ? null : sval);

        sval = ois.readUTF();
        poi.setName(sval.equals(NULL_STR) ? null : sval);

        dval = ois.readDouble();
        poi.setLat(dval);
        dval = ois.readDouble();
        poi.setLng(dval);

        return poi;
    }

    private static String _getFName(File f) {
        int p1 = f.getName().lastIndexOf('.');
        if (p1 > 0) {
            return f.getName().substring(0, p1);
        } else {
            return f.getName();
        }
    }

}
