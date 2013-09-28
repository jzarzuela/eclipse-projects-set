/**
 * 
 */
package com.jzb.dxc;

import java.io.File;

/**
 * @author jzarzuela
 * 
 */
public class TestFilters {

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
            TestFilters me = new TestFilters();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(-1);
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

        Config.loadConfig(new File("/Users/jzarzuela/Documents/java-Campus/_Util_DivxCleaner/resources/config.xml"));
        
        String newName = Filters.filterName("Llama A La Comadrona - Temporada 1 [HDTV][Cap.103][Español Castellano]/LlaALComad103 [www.newpct.com]");
        System.out.println(newName);
    }
}
