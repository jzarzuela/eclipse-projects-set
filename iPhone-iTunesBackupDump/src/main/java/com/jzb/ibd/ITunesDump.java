/**
 * 
 */
package com.jzb.ibd;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;


import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class ITunesDump {

    private static class TDBRecord implements Comparable<TDBRecord> {

        public String GUID;

        public String dataHash;
        public String domain;
        public int    fileLength;

        public int    flag;
        public int    fmode;
        public int    groupId;
        public String linkTarget;
        public String path;
        public int    propCount;
        public String propNames[];
        public String propValues[];
        public int    time1;
        public int    time2;
        public int    time3;
        public int    unknown32_1;
        public int    unknown32_2;
        public String unknownStr;
        public int    userId;

        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(TDBRecord obj) {

            String fn1 = domain + path;
            String fn2 = obj.domain + obj.path;
            return fn1.compareTo(fn2);
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {

            String s = "TDBRecord {";
            s += "\n  GUID:" + GUID;
            s += "\n  Domain:" + domain;
            s += "\n  Path:" + path;
            s += "\n  LinkTarget:" + linkTarget;
            s += "\n  DataHash:" + dataHash;
            s += "\n  UnknownStr:" + unknownStr;
            s += "\n  Fmode:" + Integer.toHexString(0x00FFFF & fmode);
            s += "\n  Unknown32_1:" + Integer.toHexString(0x00FFFFFFFF & unknown32_1);
            s += "\n  Unknown32_2:" + Integer.toHexString(0x00FFFFFFFF & unknown32_2);
            s += "\n  UserId:" + Integer.toHexString(0x00FFFFFFFF & userId);
            s += "\n  GroupId:" + Integer.toHexString(0x00FFFFFFFF & groupId);
            s += "\n  Time1:" + Integer.toHexString(0x00FFFFFFFF & time1);
            s += "\n  Time2:" + Integer.toHexString(0x00FFFFFFFF & time2);
            s += "\n  Time3:" + Integer.toHexString(0x00FFFFFFFF & time3);
            s += "\n  FileLength:" + fileLength;
            s += "\n  Flag:" + Integer.toHexString(0x00FF & flag);
            s += "\n  PropertyCount:" + propCount;
            if (propCount > 0) {
                for (int n = 0; n < propCount; n++) {
                    s += "\n    " + propNames[n] + "=" + propValues[n];
                }
            }
            s += "\n}";

            return s;
        }
    }

    private static class DomainDumpInfo {

        int                totalSize = 0;
        TreeSet<TDBRecord> records   = new TreeSet<TDBRecord>();
    }

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
            ITunesDump me = new ITunesDump();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
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
        
        File bbddFolder = new File("/Users/jzarzuela/Library/Application Support/MobileSync/Backup/");
        for (File f : bbddFolder.listFiles()) {
            if (f.isDirectory()) {
                File info = new File(f, "Info.plist");
                if (!info.exists()) {
                    System.out.println("No Info.plist file found in: " + f);
                    continue;
                }

                NSDictionary dict = (NSDictionary) PropertyListParser.parse(info);
                //if (!"013264007877837".equals(dict.getStrValue("IMEI"))) {
                if (dict.getStrValue("IMEI")!=null) {
                    //continue;
                }

                System.out.println("\n\n------------------------------------------------------------------------");
                System.out.println(info);

                System.out.println("Device Name: " + dict.getStrValue("Device Name"));
                System.out.println("Display Name: " + dict.getStrValue("Display Name"));
                System.out.println("Build Version: " + dict.getStrValue("Build Version"));
                System.out.println("GUID: " + dict.getStrValue("GUID"));
                System.out.println("ICCID: " + dict.getStrValue("ICCID"));
                System.out.println("IMEI: " + dict.getStrValue("IMEI"));

                NSArray apps = (NSArray) dict.objectForKey("Installed Applications");
                if (apps == null) {
                    System.out.println("** No apps found! **");
                } else {
                    for (int n = 0; n < apps.count(); n++) {
                        NSObject appName = apps.objectAtIndex(n);
                        System.out.println("    " + appName);
                    }
                }
            }
        }
    }

    public void doIt2(String[] args) throws Exception {

        // iPad = 82def3f693ad6959d3ae4528d656e2aeb2809f58
        // iPad Mini = 1ce8c1199e35c21ca8f9a6b4322fe614a5855210
        // iPhone = 2e1e42c63ba707a2ab1b9ffb95f9a1e7f38ecfc1
        // iPhone 5s = 9960d5f08dac562745742923c11c96120547b7e2
        File bbddFile = new File("/Users/jzarzuela/Library/Application Support/MobileSync/Backup/9960d5f08dac562745742923c11c96120547b7e2//Manifest.mbdb");
        TreeMap<String, DomainDumpInfo> records = _readBackupInfo(bbddFile);

        // ******************************************
        // ******************************************
        // ******************************************
        String toFilter = "fragger";
        toFilter = "";

        _printInfo(bbddFile.getParentFile(), records, toFilter);

        // File destFolder = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\kk");
        // _makeCopy(bbddFile.getParentFile(), destFolder, records, toFilter);

    }

    private void _printInfo(File itunesBackupFolder, TreeMap<String, DomainDumpInfo> sortedRecords, String toFilter) throws Exception {

        long totalSize = 0;
        
        DecimalFormat df = new DecimalFormat("000,000,000");
        if (toFilter != null)
            toFilter = toFilter.toLowerCase();

        for (Map.Entry<String, DomainDumpInfo> entry : sortedRecords.entrySet()) {
            String domain = entry.getKey();
            int size = entry.getValue().totalSize;

            totalSize+=size;
            
            if (size > 0) {

                if (toFilter.length() > 0 && !domain.toLowerCase().contains(toFilter)) {
                    continue;
                }

                System.out.println();
                System.out.println(df.format(size) + " -- " + domain + "(" + df.format(size) + ") ------------------------------------------------------------------");
                // for (TDBRecord rec : entry.getValue().records) {
                // if (rec.fileLength > 0) {
                // File ofile = new File(itunesBackupFolder, rec.GUID);
                // if (!ofile.exists()) {
                // System.out.println("Source file not found: " + ofile);
                // } else {
                // System.out.println("  " + rec.GUID + "  ---  " + rec.path);
                // }
                //
                // }
                // }
            }
        }

        System.out.println("All apps total size: "+df.format(totalSize));

        
    }

    private void _makeCopy(File itunesBackupFolder, File destFolder, TreeMap<String, DomainDumpInfo> sortedRecords, String toFilter) throws Exception {

        DecimalFormat df = new DecimalFormat("000,000,000,000");

        if (toFilter != null)
            toFilter = toFilter.toLowerCase();

        for (Map.Entry<String, DomainDumpInfo> entry : sortedRecords.entrySet()) {
            String domain = entry.getKey();
            int size = entry.getValue().totalSize;

            if (size > 0) {

                if (toFilter.length() > 0 && !domain.toLowerCase().contains(toFilter)) {
                    continue;
                }

                String strSize = df.format(size) + " # ";

                for (TDBRecord rec : entry.getValue().records) {

                    if (rec.fileLength == 0)
                        continue;

                    File ofile = new File(itunesBackupFolder, rec.GUID);
                    if (!ofile.exists()) {
                        System.out.println("Source file not found: " + ofile);
                    } else {
                        File dfile = new File(destFolder, strSize + domain + "/" + rec.path);
                        // File dfile = new File(destFolder, rec.domain + "/" + rec.path);
                        dfile.getParentFile().mkdirs();
                        _fileCopy(ofile, dfile);
                    }
                }
            }
        }
    }

    private void _fileCopy(File of, File df) {

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            System.out.println("Copying '" + of.getName() + "' to '" + df.getName() + "'");
            byte buffer[] = new byte[65536];
            bis = new BufferedInputStream(new FileInputStream(of), 65536);
            bos = new BufferedOutputStream(new FileOutputStream(df, false), 65536);
            for (;;) {
                int len = bis.read(buffer);
                if (len == -1)
                    break;
                bos.write(buffer, 0, len);
            }
        } catch (Throwable th) {
            System.out.println("Error copying file: ");
            th.printStackTrace(System.out);
        } finally {
            try {
                if (bis != null)
                    bis.close();
                if (bos != null)
                    bos.close();
            } catch (Throwable th2) {
            }
        }

    }

    private int offset = 6;

    private TreeMap<String, DomainDumpInfo> _readBackupInfo(File dbfile) throws Exception {

        int len;

        TreeMap<String, DomainDumpInfo> records = new TreeMap<String, DomainDumpInfo>();

        // Read bbdd file info
        byte bd_buffer[] = new byte[(int) dbfile.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(dbfile), 65536);
        len = bis.read(bd_buffer);
        bis.close();
        if (len != bd_buffer.length)
            throw new Exception("Read less info than bbdd file size: " + len);

        // Check signature
        if (bd_buffer[0] != 0x6D || bd_buffer[1] != 0x62 || bd_buffer[2] != 0x64 || bd_buffer[3] != 0x62 || bd_buffer[4] != 0x05 || bd_buffer[5] != 0x00) {
            throw new Exception("Bad bbdd file signature");
        }

        // Set first offset
        offset = 6;

        // Read each index record
        while (offset < len) {

            TDBRecord rec = _readBDRecord(bd_buffer);
            rec.GUID = SHAGenerator.SHA1(rec.domain + "-" + rec.path);

            DomainDumpInfo ddinfo = records.get(rec.domain);
            if (ddinfo == null) {
                ddinfo = new DomainDumpInfo();
                records.put(rec.domain, ddinfo);
            }
            if (rec.fileLength > 0) {
                ddinfo.totalSize += rec.fileLength;
            }
            ddinfo.records.add(rec);

        }

        return records;
    }

    private TDBRecord _readBDRecord(byte bd_buffer[]) throws Exception {

        TDBRecord rec = new TDBRecord();

        // Domain
        rec.domain = _readString(offset, bd_buffer);
        offset += 2 + rec.domain.length();
        // if (rec.domain.startsWith("AppDomain-")) {
        // rec.domain = rec.domain.substring(10);
        // }

        // Path
        rec.path = _readString(offset, bd_buffer);
        offset += 2 + rec.path.length();

        // LinkTarget
        rec.linkTarget = _readString(offset, bd_buffer);
        offset += 2 + rec.linkTarget.length();

        // DataHash
        rec.dataHash = _readString(offset, bd_buffer);
        offset += 2 + rec.dataHash.length();

        // Unknown Str
        rec.unknownStr = _readString(offset, bd_buffer);
        offset += 2 + rec.unknownStr.length();

        // Mode
        rec.fmode = _readUInt16(offset, bd_buffer);
        offset += 2;

        // Unknown 32
        rec.unknown32_1 = _readUInt32(offset, bd_buffer);
        offset += 4;

        // Unknown 32
        rec.unknown32_2 = _readUInt32(offset, bd_buffer);
        offset += 4;

        // UserID
        rec.userId = _readUInt32(offset, bd_buffer);
        offset += 4;

        // GroupID
        rec.groupId = _readUInt32(offset, bd_buffer);
        offset += 4;

        // Time1
        rec.time1 = _readUInt32(offset, bd_buffer);
        offset += 4;

        // Time2
        rec.time2 = _readUInt32(offset, bd_buffer);
        offset += 4;

        // Time3
        rec.time3 = _readUInt32(offset, bd_buffer);
        offset += 4;

        // FileLengh
        rec.fileLength = (int) _readUInt64(offset, bd_buffer);
        offset += 8;

        // Flag
        rec.flag = 0x00FF & bd_buffer[offset];
        offset += 1;

        // PropCount
        rec.propCount = 0x00FF & bd_buffer[offset];
        offset += 1;

        if (rec.propCount > 0) {
            rec.propNames = new String[rec.propCount];
            rec.propValues = new String[rec.propCount];
            for (int n = 0; n < rec.propCount; n++) {
                rec.propNames[n] = _readString(offset, bd_buffer);
                offset += 2 + rec.propNames[n].length();
                rec.propValues[n] = _readString(offset, bd_buffer);
                offset += 2 + rec.propValues[n].length();
            }
        }

        return rec;

    }

    private static final char HEXCHARS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private String _readGUID(int pos, byte buffer[]) {

        StringBuffer sb = new StringBuffer();
        for (int n = 0; n < 20; n++) {
            sb.append(HEXCHARS[0x0F & (buffer[pos + n] >> 4)]);
            sb.append(HEXCHARS[0x0F & buffer[pos + n]]);
        }
        return sb.toString();
    }

    private String _readString(int pos, byte buffer[]) {
        StringBuffer sb = new StringBuffer();

        int size = _readUInt16(pos, buffer);
        pos += 2;

        if (size == 0xFFFF) {
            return "";
        }

        for (int n = 0; n < size; n++) {
            sb.append((char) buffer[pos++]);
        }

        return sb.toString();
    }

    private int _readUInt16(int pos, byte buffer[]) {
        int a0 = (0x00FF & buffer[pos + 0]) << 8;
        int a1 = (0x00FF & buffer[pos + 1]);
        int z = a0 + a1;
        return z;
    }

    private int _readUInt32(int pos, byte buffer[]) {
        int a0 = (0x00FF & buffer[pos + 0]) << 24;
        int a1 = (0x00FF & buffer[pos + 1]) << 16;
        int a2 = (0x00FF & buffer[pos + 2]) << 8;
        int a3 = (0x00FF & buffer[pos + 3]);
        int z = a0 + a1 + a2 + a3;
        return z;
    }

    private long _readUInt64(int pos, byte buffer[]) {
        long a0 = (0x000000FF & buffer[pos + 0]) << 56;
        long a1 = (0x000000FF & buffer[pos + 1]) << 48;
        long a2 = (0x000000FF & buffer[pos + 2]) << 40;
        long a3 = (0x000000FF & buffer[pos + 3]) << 32;
        long a4 = (0x000000FF & buffer[pos + 4]) << 24;
        long a5 = (0x000000FF & buffer[pos + 5]) << 16;
        long a6 = (0x000000FF & buffer[pos + 6]) << 8;
        long a7 = (0x000000FF & buffer[pos + 7]);
        long z = a0 + a1 + a2 + a3 + a4 + a5 + a6 + a7;
        return z;
    }
    
    
    
}
