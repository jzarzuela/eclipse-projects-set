import java.io.File;

import com.jzb.ipa.ren.NameComposer;

/**
 * 
 */

/**
 * @author jzarzuela
 * 
 */
public class Tests {

    /**
     * 
     */
    public Tests() {
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
            Tests me = new Tests();
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

        String name = "$uN[Outlook-Mail+]_PK[com.mspecht.OWA]_V[201406182314]_OS[6.1]_D[2014-06-19].ipa";
        NameComposer.parseVerCanonical(name);

        _processFolder(new File("/Users/jzarzuela/Documents/personal/iPhone/IPAs"));
    }

    // ----------------------------------------------------------------------------------------------------
    private void _processFolder(File folder) throws Exception {

        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                _processFolder(file);
            } else {
                if (file.getName().toLowerCase().endsWith(".ipa")) {
                    _processIPAFile(file);
                }
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static final char T_LEGAL   = '$';
    private static final char T_CRACKED = '#';

    private void _processIPAFile(File ipaFile) throws Exception {

        char type = ipaFile.getName().charAt(0);
        String path = ipaFile.getAbsolutePath().toLowerCase();

        if (type != T_LEGAL && type != T_CRACKED) {
            System.out.println("UNKNOWN TYPE:");
            System.out.println("    " + ipaFile.getAbsolutePath());
        } else if ((type == T_LEGAL && !path.contains("_legal")) || (type != T_LEGAL && path.contains("_legal"))) {
            System.out.println("MISPLACED FILE:");
            System.out.println("    " + ipaFile.getAbsolutePath());
        }
    }
}
