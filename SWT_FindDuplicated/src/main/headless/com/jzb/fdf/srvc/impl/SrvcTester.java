/**
 * 
 */
package com.jzb.fdf.srvc.impl;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.jzb.fdf.srvc.IFolderSrvc;
import com.jzb.fdf.srvc.IModelSrvc;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class SrvcTester {

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
            SrvcTester me = new SrvcTester();
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

        FSUtils.cleanDuplicated("/Volumes/Disco_2/Prueba 20120910-1425/nodo04/Logs_10092012.tar");

        IModelSrvc.inst.init(true);
        IFolderSrvc.inst.getRootFolders(false);
        Tracer._debug("");

    }
}
