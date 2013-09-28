/**
 * 
 */
package com.jzb.j2s;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class Lister {

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
            Lister me = new Lister();
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

    private static File s_jsFolder = new File("C:\\JZarzuela\\Java\\eclipse-helios-SDK\\dropins\\J2S\\eclipse\\plugins\\net.sf.j2s.lib_2.0.0\\j2slib");
    private static int s_jsFldrLen = s_jsFolder.getAbsolutePath().length()+1;
    
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        iterateFolder(new File(s_jsFolder,"java"));
    }
    
    private void iterateFolder(File folder) throws Exception {
        
        for(File file:folder.listFiles()) {
            if(file.isDirectory()) {
                iterateFolder(file);
            } else {
                processFile(file);
            }
        }
    }
    
    private void processFile(File file) throws Exception {
        String fname = file.getAbsolutePath();

        if(!fname.toLowerCase().endsWith(".js")) 
            return;
        
        String className = fname.substring(s_jsFldrLen,fname.length()-3).replace('\\','.');
        
        if(className.equals("java.core.z") || 
           className.equals("java.error.z") || 
           className.equals("java.package"))
            return;
        
        System.out.println("        loadClass2(\""+className+"\");");
        
    }
}
