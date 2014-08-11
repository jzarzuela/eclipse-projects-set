/**
 * 
 */
package com.jzb.iph;

import java.io.File;
import java.util.HashSet;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.jzb.futil.FileExtFilter;
import com.jzb.futil.FileExtFilter.IncludeFolders;
import com.jzb.ipa.ren.NameComposer;

/**
 * @author n63636
 * 
 */
public class ITunesDumpCompare {

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
            ITunesDumpCompare me = new ITunesDumpCompare();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        /*
         * ------------------------------------------------------------------------
         * 
         * 1ce8c1199e35c21ca8f9a6b4322fe614a5855210 Device Name: iPad Mini de JZarzuela
         * 
         * 5f19ba88515b02a37c066ccc74b0d064a1cef4cb Device Name: iPhone de JZarzuela
         * 
         * 63aa83026fc507367f882f009ea404ee23a5f5e3 Device Name: Prados iPhone
         * 
         * 82def3f693ad6959d3ae4528d656e2aeb2809f58 Device Name: iPad1 de JZarzuela
         * 
         * 9960d5f08dac562745742923c11c96120547b7e2 Device Name: iPhone 5s de JZarzuela
         * 
         * ------------------------------------------------------------------------
         */
        File dumpInfoBaseFolder = new File("/Users/jzarzuela/Library/Application Support/MobileSync/Backup");
        File dumpInfoFolder = new File(dumpInfoBaseFolder, "1ce8c1199e35c21ca8f9a6b4322fe614a5855210");
        HashSet<String> dumpIPAs = _readITunesDumpInfo(dumpInfoFolder);

        HashSet<String> fileIPAs = new HashSet<String>();
        File baseFolder = new File("/Users/jzarzuela/Documents/personal/iPhone/IPAs");
        System.out.println("");
        System.out.println("----------------------------------------------------------------");
        System.out.println("Processing IPA folder: " + baseFolder);
        System.out.println("----------------------------------------------------------------");
        System.out.println("");
        _processIPAFolder(fileIPAs, baseFolder);

        _compareInfo(fileIPAs, dumpIPAs);
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private void _compareInfo(HashSet<String> fileIPAs, HashSet<String> dumpIPAs) {

        System.out.println("\n--- IPAs in folder and not installed ---");
        for (String info : fileIPAs) {
            if (!dumpIPAs.contains(info)) {
                System.out.println(info);
            }
        }
        System.out.println("\n--- IPAs installed and not in folder ---");
        for (String info : dumpIPAs) {
            if (!fileIPAs.contains(info)) {
                System.out.println(info);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private HashSet<String> _readITunesDumpInfo(File dumpFolder) throws Exception {

        File dumpInfoFile = new File(dumpFolder, "Info.plist");
        if (!dumpInfoFile.exists()) {
            System.out.println("No Info.plist file found: " + dumpInfoFile);
            System.exit(0);
        }

        HashSet<String> items = new HashSet<String>();

        NSDictionary dict = (NSDictionary) PropertyListParser.parse(dumpInfoFile);

        System.out.println("\n\n------------------------------------------------------------------------");
        System.out.println(dumpInfoFile);

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
                items.add(appName.toString());
            }
        }

        return items;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private void _processIPAFolder(HashSet<String> items, File folder) throws Exception {

        File fList[] = folder.listFiles(new FileExtFilter(IncludeFolders.YES, "ipa"));

        for (File f : fList) {
            if (f.isDirectory())
                _processIPAFolder(items, f);
            else
                _processIPA(items, f);
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private void _processIPA(HashSet<String> items, File ipaFile) throws Exception {

        // Tracer._debug("IPAFile = '%s'", ipaFile.getName());
        String infoName = NameComposer.parsePkg(ipaFile.getName());
        items.add(infoName);

    }

}
