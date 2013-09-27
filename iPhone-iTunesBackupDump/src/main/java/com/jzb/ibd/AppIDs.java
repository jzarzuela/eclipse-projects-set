/**
 * 
 */
package com.jzb.ibd;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;

import com.jzb.ipa.plist.BinaryPListParser;
import com.jzb.ipa.plist.IPListParser;
import com.jzb.ipa.plist.T_PLDict;
import com.jzb.ipa.plist.XMLPListParser;

/**
 * @author n63636
 * 
 */
public class AppIDs {


    private IPListParser     m_binParser = new BinaryPListParser();
    private IPListParser     m_xmlParser = new XMLPListParser();
    
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
            AppIDs me = new AppIDs();
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

        // File plistFile = new File("C:\\Users\\n63636\\AppData\\Roaming\\Apple Computer\\MobileSync\\Backup\\2e1e42c63ba707a2ab1b9ffb95f9a1e7f38ecfc1\\Manifest.plist");

        File plistFile = new File("/Users/jzarzuela/Library/Application Support/MobileSync/Backup/2e1e42c63ba707a2ab1b9ffb95f9a1e7f38ecfc1/Manifest.plist");
        
        
        byte buffer[] = new byte[(int)plistFile.length()];
        FileInputStream fis = new FileInputStream(plistFile);
        fis.read(buffer);
        fis.close();
        
        
        T_PLDict dict = null;

        if (isBinaryBundle(buffer))
            dict = m_binParser.parsePList(buffer);
        else
            dict = m_xmlParser.parsePList(buffer);
        
        ArrayList<String> lines = new ArrayList<String>(); 
        T_PLDict apps = (T_PLDict) dict.getValue("Applications");
        for(String key:apps.getKeys()) {
            String cfbundle = apps.getStrValue(key+'/'+"CFBundleIdentifier");
            String path = apps.getStrValue(key+'/'+"Path");
            int p1 = "/private/var/mobile/Applications/".length();
            int p2 = path.indexOf('/',p1);
            String guid = path.substring(p1,p2);
            lines.add(guid+" - "+cfbundle);
        }
        
        Collections.sort(lines);
        
        for(String line:lines) {
            System.out.println(line);
        }
        
        /*
        // Comparador para ordenar por categoria y nombre
        Comparator<TDBRecord> comp = new Comparator<TDBRecord>() {

            public int compare(TDBRecord o1, TDBRecord o2) {
                return o1.GUID.compareTo(o2.GUID);
            }

        };

        Collections.sort(records, comp);
         */
        

    }
    
    
    private static final byte BPLIST00[] = { 98, 112, 108, 105, 115, 116, 48, 48 };
    private boolean isBinaryBundle(byte buffer[]) {
        for (int n = 0; n < BPLIST00.length; n++) {
            if (buffer[n] != BPLIST00[n])
                return false;
        }
        return true;
    }

}
