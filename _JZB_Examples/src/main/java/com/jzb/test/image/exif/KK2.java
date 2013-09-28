/**
 * 
 */
package com.jzb.test.image.exif;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author n000013
 * 
 */
public class KK2 {

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
            KK2 me = new KK2();
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

        File baseFolder = new File("C:\\JZarzuela\\fotos\\Business\\2008_04_06-10_Vegas\\Filtradas_NO\\Vegas");
        TreeMap<String, File> data = new TreeMap<String, File>();
        for (File f : baseFolder.listFiles()) {
            data.put(f.getName(), f);
        }

        int count = 90005;
        DecimalFormat df = new DecimalFormat("00000");
        for (Entry<String, File> entry : data.entrySet()) {
            String name = entry.getValue().getName();
            int i = name.indexOf("_9") + 1;
            String newName = "Vegas_"+df.format(count) + "-"+name.substring(0, i-1) + name.substring(i + 5);
            count += 5;

            File newFile = new File(entry.getValue().getParentFile(), newName);
            System.out.println(name+"   ->   " +newFile.getName());
            if (!entry.getValue().renameTo(newFile)) {
                System.err.println("Error en: " + entry.getValue());
            }
        }
    }

}
