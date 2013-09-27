/**
 * 
 */
package com.jzb.ibd;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.TreeSet;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.jzb.ipa.ren.NameComposer;

/**
 * @author jzarzuela
 * 
 */
public class CleanApps {

    // ----------------------------------------------------------------------------------------------------
    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        long t1 = 0, t2 = 0;
        try {
            System.out.println("\n***** EXECUTION STARTED *****\n");
            CleanApps me = new CleanApps();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            t2 = System.currentTimeMillis();
            System.out.println("***** EXECUTION FAILED [" + (t2 - t1) + "]*****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {
        TreeSet<String> pkgsIPAs = _readIPAFilesPkgs();
        TreeSet<String> pkgsITunes = _readITunesFilesPkgs();

        System.out.println("\n\n------------------------------------------------------------------------");
        System.out.println("En IPAs y no en iPhone");
        TreeSet<String> pkgsTmp1 = new TreeSet<String>(pkgsIPAs);
        pkgsTmp1.removeAll(pkgsITunes);
        for (String name : pkgsTmp1) {
            System.out.println("  " + name);
        }

        System.out.println("\n\n------------------------------------------------------------------------");
        System.out.println("En iPhone y no en IPAs");
        TreeSet<String> pkgsTmp2 = new TreeSet<String>(pkgsITunes);
        pkgsTmp2.removeAll(pkgsIPAs);
        for (String name : pkgsTmp2) {
            System.out.println("  " + name);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private TreeSet<String> _readITunesFilesPkgs() throws Exception {

        final TreeSet<String> pkgNames = new TreeSet<String>();

        File infoFile = new File("/Users/jzarzuela/Library/Application Support/MobileSync/Backup/63aa83026fc507367f882f009ea404ee23a5f5e3/Info.plist");
        NSDictionary dict = (NSDictionary) PropertyListParser.parse(infoFile);
        NSArray apps = (NSArray) dict.objectForKey("Installed Applications");
        for (int n = 0; n < apps.count(); n++) {
            NSObject appName = apps.objectAtIndex(n);
            pkgNames.add(appName.toString());
        }
        return pkgNames;
    }

    // ----------------------------------------------------------------------------------------------------
    private TreeSet<String> _readIPAFilesPkgs() throws Exception {

        final TreeSet<String> pkgNames = new TreeSet<String>();

        SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                String fileName = file.getFileName().toString();
                if (!fileName.toLowerCase().endsWith(".ipa")) {
                    return FileVisitResult.CONTINUE;
                }

                String pkgName = NameComposer.parsePkg(fileName);
                pkgNames.add(pkgName);

                return FileVisitResult.CONTINUE;
            }
        };

        Files.walkFileTree(Paths.get("/Users/jzarzuela/Documents/personal/iPhone/IPAs/_legal/iPhone"), visitor);
        Files.walkFileTree(Paths.get("/Users/jzarzuela/Documents/personal/iPhone/IPAs/_cracked/iPhone"), visitor);
        Files.walkFileTree(Paths.get("/Users/jzarzuela/Documents/personal/iPhone/IPAs/_legal/iPhone-iPad-mixtas"), visitor);
        Files.walkFileTree(Paths.get("/Users/jzarzuela/Documents/personal/iPhone/IPAs/_cracked/iPhone-iPad-mixtas"), visitor);
        return pkgNames;
    }
}
