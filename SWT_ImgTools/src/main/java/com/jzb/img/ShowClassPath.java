/**
 * 
 */
package com.jzb.img;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author jzarzuela
 * 
 */
public class ShowClassPath {

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
            ShowClassPath me = new ShowClassPath();
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

        URLClassLoader oo = (URLClassLoader) getClass().getClassLoader();
        ArrayList<URL> al = new ArrayList();
        for (URL u : oo.getURLs()) {
            al.add(u);
        }
        Collections.sort(al, new Comparator<URL>() {

            @Override
            public int compare(URL o1, URL o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        
        for (URL u : al) {
            String s = u.toString().replace("file:", "export CLP=$CLP:");
            System.out.println(s);
        }
    }
}
