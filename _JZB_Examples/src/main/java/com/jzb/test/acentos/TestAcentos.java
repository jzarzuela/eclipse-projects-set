/**
 * 
 */
package com.jzb.test.acentos;

import java.io.File;
import java.util.Map.Entry;

/**
 * @author jzarzuela
 * 
 */
public class TestAcentos {

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
            TestAcentos me = new TestAcentos();
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

        File basePath;

        
        basePath = new File("./target/classes");
        basePath = new File("/Users/jzarzuela/Documents/personal/iPhone/IPAs/_legal/iPhone/Varios/");

        System.out.println(basePath.getAbsolutePath());
        for (File f : basePath.listFiles()) {
            System.out.println(f.exists() + " - " + f.getAbsolutePath());
        }
        _printEnvSettings();
        _printSysProperties();
    }

    // ----------------------------------------------------------------------------------------------------
    private void _printEnvSettings() {
        
        System.out.println();
        System.out.println("--- EnvSettings ------------------------------------------------------------------------------------");
        for (Entry<String, String> entry : System.getenv().entrySet()) {
            System.out.println(entry.getKey() + " - '" + entry.getValue() + "'");
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    // ----------------------------------------------------------------------------------------------------
    private void _printSysProperties() {
        
        // sun.jnu.encoding - 'US-ASCII'
        // file.encoding - 'UTF-8'

        System.out.println();
        System.out.println("--- SysProperties ----------------------------------------------------------------------------------");
        for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
            System.out.println(entry.getKey() + " - '" + entry.getValue() + "'");
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println();
    }
}
