/**
 * 
 */
package com.jzb.test.image.exif;

import java.io.File;
import java.util.Formatter;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("unused")
public class Ren {

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
            Ren me = new Ren();
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
        File baseFolder = new File("");
        renameFolder(baseFolder);
    }

    private void renameFolder(File baseFolder) throws Exception {

        String baseName="";
        int baseCount=9;
        int count=0;
        
        for (File f : baseFolder.listFiles()) {
            if (f.isDirectory()) {
                continue;
            } else {
                int p=f.getName().indexOf('-');
                String bn=f.getName().substring(0,p);
                if(!bn.equals(baseName)) {
                    baseName=bn;
                    baseCount++;
                    count=0;
                }
                
                StringBuffer sb=new StringBuffer();
                Formatter formatter=new Formatter(sb);
                formatter.format("Roma_%02d%03d-%s",baseCount,count,f.getName());
                formatter.close();
                String newName=sb.toString();
                File newFile = new File(baseFolder, newName);
                System.out.println(newFile);
                if (!f.renameTo(newFile)) {
                    System.err.println("Error en: " + f);
                }
                
                count++;
            }
        }

    }

    private void renameFolder3(File rootFolder, File baseFolder) throws Exception {

        for (File f : baseFolder.listFiles()) {
            if (f.isDirectory()) {
                renameFolder3(rootFolder, f);
            } else {
                File newFile = new File(rootFolder, f.getName());
                if (!f.renameTo(newFile)) {
                    System.err.println("Error en: " + f);
                }
            }
        }

    }
    
    private int renameFolder2(File baseFolder, int count) throws Exception {

        System.out.println(count + " - " + baseFolder);
        for (File f : baseFolder.listFiles()) {
            if (f.isDirectory()) {
                count = renameFolder2(f, count + 1);
            } else {
                String name = "Roma_" + count + f.getName().substring(7);
                File newFile = new File(baseFolder, name);
                System.out.println(newFile);
//                if (!f.renameTo(newFile)) {
//                    System.err.println("Error en: " + f);
//                }
            }
        }

        return count;
    }
}
