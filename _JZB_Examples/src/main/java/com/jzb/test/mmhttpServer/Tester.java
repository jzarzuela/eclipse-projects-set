/**
 * 
 */
package com.jzb.test.mmhttpServer;

import java.io.File;

import mmhttp.server.Server;

/**
 * @author n63636
 * 
 */
public class Tester {

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
            Tester me = new Tester();
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

        File baseResFolder0=new File("/Users/jzarzuela/Downloads/_extension_/");
        File baseResFolder1=new File("./src/main/java/com/jzb/test/mmhttpServer/files");
        File baseResFolder2=new File("./filesMore");
        File baseResFolders[]= {baseResFolder0, baseResFolder1,baseResFolder2};
        
        Server server = new Server(8008);
        
        // Es una expresion regular que acepta todo
        server.register(".*", new FileResponder(baseResFolders));

        // Despues de esto se puede pedir la URL: http://127.0.0.1:8008/index.html
        server.start();
        
        
        System.in.read();
        server.stop();
    }
}
