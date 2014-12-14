/**
 * 
 */
package com.jzb.ipa.bundle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.jzb.ipa.bundle.T_BundleData.LEGAL_TYPE;

/**
 * @author n63636
 * 
 */
public class BundleReader {

    private SimpleDateFormat m_sdf = new SimpleDateFormat("yyyy-MM-dd");

    public BundleReader() {
    }

    public T_BundleData readInfo(File afile) throws Exception {

        NSDictionary iTunesMetadata_Dict = null;
        NSDictionary data_Dict = null;

        T_BundleData data = new T_BundleData();

        boolean plistProcessed = false, imageProcessed = false, idProcessed = false;

        ZipFile zf = new ZipFile(afile);
        Enumeration en = zf.getEntries();
        while (en.hasMoreElements()) {

            ZipEntry zentry = (ZipEntry) en.nextElement();

            if (!idProcessed && zentry.getName().endsWith("iTunesMetadata.plist")) {
                byte buffer[] = _readBuffer((int) zentry.getSize(), zf.getInputStream(zentry));
                iTunesMetadata_Dict = (NSDictionary) PropertyListParser.parse(buffer);

                // Es legal si existe una de los dos
                String appleId1 = iTunesMetadata_Dict.getStrValue("appleId");
                String appleId2 = iTunesMetadata_Dict.getStrCompoundValue("com.apple.iTunesStore.downloadInfo/accountInfo/AppleID");
                if ((appleId1 != null && appleId1.toLowerCase().contains("jzarzuela")) || (appleId2 != null && appleId2.toLowerCase().contains("jzarzuela"))) {
                    data.isLegal = LEGAL_TYPE.LEGAL;
                } else {
                    data.isLegal = LEGAL_TYPE.CRACKED;
                }

                idProcessed = true;
            }

            if (!plistProcessed && zentry.getName().endsWith(".app/Info.plist")) {
                byte buffer[] = _readBuffer((int) zentry.getSize(), zf.getInputStream(zentry));
                data_Dict = (NSDictionary) PropertyListParser.parse(buffer);
                data.fdate = m_sdf.format(new Date(zentry.getTime()));
                plistProcessed = true;
            }

            if (!imageProcessed && zentry.getName().endsWith("iTunesArtwork")) {
                data.img = _readBuffer((int) zentry.getSize(), zf.getInputStream(zentry));
                data.imgExt = ".jpg";
                imageProcessed = true;
            }
            
            int imgPos = zentry.getName().indexOf("iTunesArtwork@2x.");
            if (!imageProcessed && imgPos!=-1) {
                data.img = _readBuffer((int) zentry.getSize(), zf.getInputStream(zentry));
                data.imgExt = zentry.getName().substring(imgPos+16);
                imageProcessed = true;
            }
            
            imgPos = zentry.getName().indexOf("iTunesArtwork.");
            if (!imageProcessed && imgPos!=-1) {
                data.img = _readBuffer((int) zentry.getSize(), zf.getInputStream(zentry));
                data.imgExt = zentry.getName().substring(imgPos+13);
                //imageProcessed = true;
            }
            

            if (plistProcessed && imageProcessed && idProcessed) {
                break;
            }
        }

        // Faltaba el archivo "iTunesMetadata.plist"
        if (!idProcessed) {
            data.isLegal = LEGAL_TYPE.UNKNOWN;
        }

        zf.close();

        if (iTunesMetadata_Dict != null) {
            data.dict = iTunesMetadata_Dict;
            if (data_Dict != null)
                data.dict.putAll(data_Dict);
        } else {
            data.dict = data_Dict;
        }

        return data;
    }

    private byte[] _readBuffer(long size, InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[(int) size];
        while (is.available() > 0) {
            int lread = is.read(buffer);
            if (lread != -1) {
                baos.write(buffer, 0, lread);
            }
        }
        baos.close();
        return baos.toByteArray();
    }

}
