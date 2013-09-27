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

        T_BundleData data = new T_BundleData();

        boolean plistProcessed = false, imageProcessed = false, imdProcessed = false;

        ZipFile zf = new ZipFile(afile);
        Enumeration en = zf.getEntries();
        while (en.hasMoreElements()) {

            ZipEntry zentry = (ZipEntry) en.nextElement();

            if (!imdProcessed && zentry.getName().endsWith("iTunesMetadata.plist")) {
                byte buffer[] = _readBuffer((int) zentry.getSize(), zf.getInputStream(zentry));
                NSDictionary dict2 = (NSDictionary) PropertyListParser.parse(buffer);

                // Es legal si existe una de los dos
                String appleId1 = dict2.getStrValue("appleId");
                String appleId2 = dict2.getStrCompoundValue("com.apple.iTunesStore.downloadInfo/accountInfo/AppleID");
                if ((appleId1 != null && appleId1.toLowerCase().contains("jzarzuela")) || (appleId2 != null && appleId2.toLowerCase().contains("jzarzuela"))) {
                    data.isLegal = LEGAL_TYPE.LEGAL;
                } else {
                    data.isLegal = LEGAL_TYPE.CRACKED;
                }

                imdProcessed = true;
            }

            if (!plistProcessed && zentry.getName().endsWith(".app/Info.plist")) {
                byte buffer[] = _readBuffer((int) zentry.getSize(), zf.getInputStream(zentry));
                data.dict = (NSDictionary) PropertyListParser.parse(buffer);
                data.fdate = m_sdf.format(new Date(zentry.getTime()));
                plistProcessed = true;
            }

            if (!imageProcessed && zentry.getName().endsWith("iTunesArtwork")) {
                data.img = _readBuffer((int) zentry.getSize(), zf.getInputStream(zentry));
                imageProcessed = true;
            }

            if (plistProcessed && imageProcessed && imdProcessed) {
                break;
            }
        }

        // Faltaba el archivo "iTunesMetadata.plist"
        if (!imdProcessed) {
            data.isLegal = LEGAL_TYPE.UNKNOWN;
        }

        zf.close();

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
