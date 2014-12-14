import java.io.File;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;

/**
 * 
 */

/**
 * @author jzarzuela
 * 
 */
public class MacApps {

    /**
     * 
     */
    public MacApps() {
        // TODO Auto-generated constructor stub
    }

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
            MacApps me = new MacApps();
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

        _processFolder(new File("/Applications"));

    }

    // ----------------------------------------------------------------------------------------------------
    private void _processFolder(File appsFolder) throws Exception {

        int deepth = -1;
        File f = appsFolder;
        while (f != null) {
            f = f.getParentFile();
            deepth++;
        }
        if (deepth >= 3) {
            System.out.println(appsFolder);
            return;
        }

        File fileList[] = appsFolder.listFiles();
        if (fileList == null)
            return;

        boolean foundApp = false;
        for (File folder : fileList) {
            if (folder.isFile())
                continue;
            if (folder.getName().toLowerCase().endsWith(".app")) {
                _processApp(folder);
                foundApp = true;
            }
        }

        if (foundApp)
            return;

        for (File folder : fileList) {
            if (folder.isFile())
                continue;
            _processFolder(folder);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private void _processApp(File appFolder) throws Exception {

        File plistFile = new File(appFolder, "Contents/Info.plist");

        String appName = appFolder.getAbsolutePath().substring(14, appFolder.getAbsolutePath().length() - 4);

        if (plistFile.exists()) {
            NSDictionary dict = (NSDictionary) PropertyListParser.parse(plistFile);
            String CFBundleVersion = dict.getStrValue("CFBundleVersion");
            String CFBundleShortVersionString = dict.getStrValue("CFBundleShortVersionString");
            String CFBundleIdentifier = dict.getStrValue("CFBundleIdentifier");

            String version;
            if (CFBundleVersion != null && CFBundleShortVersionString != null && CFBundleVersion.equals(CFBundleShortVersionString)) {
                version = CFBundleVersion;
            } else if (CFBundleVersion == null || CFBundleVersion.trim().length() == 0) {
                version = CFBundleShortVersionString;
            } else if (CFBundleShortVersionString == null || CFBundleShortVersionString.trim().length() == 0) {
                version = CFBundleShortVersionString;
            } else {
                version = CFBundleShortVersionString + " (" + CFBundleVersion + ")";
            }

            if (CFBundleIdentifier != null && CFBundleIdentifier.contains("com.apple."))
                return;

            if (CFBundleIdentifier == null || CFBundleIdentifier.trim().length() == 0) {
                System.out.println(appName + " - " + version);
            } else {
                System.out.println(appName + " - " + CFBundleIdentifier + " - " + version);
            }

        } else {
            System.out.println(appFolder);
            System.out.println(appName + " - (*** ??? ***)");
        }

    }
}
