/**
 * 
 */
package com.jzb.ttpoi.wl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.jzb.ttpoi.data.TPOIData;

/**
 * @author n63636
 * 
 */
public class BINFileWriter {

    public static void saveFile(File binFile, ArrayList<TPOIData> pois) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        for (TPOIData poi : pois) {

            if (poi.Is_TT_Cat_POI())
                continue;
            
            _writePoi(oos, poi);
        }
        oos.close();
        baos.close();

        FileOutputStream fos = new FileOutputStream(binFile);
        fos.write(baos.toByteArray());
        fos.close();

    }

    private static final String NULL_STR = "NULL_STR";
    
    private static void _writePoi(ObjectOutputStream oos, TPOIData poi) throws Exception {
        oos.writeUTF(poi.getCategory()==null?NULL_STR:poi.getCategory());
        oos.writeUTF(poi.getDesc()==null?NULL_STR:poi.getDesc());
        oos.writeUTF(poi.getExtraInfo()==null?NULL_STR:poi.getExtraInfo());
        oos.writeUTF(poi.getIconStyle()==null?NULL_STR:poi.getIconStyle());
        oos.writeUTF(poi.getName()==null?NULL_STR:poi.getName());
        oos.writeDouble(poi.getLat());
        oos.writeDouble(poi.getLng());
    }

}
