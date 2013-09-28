/**
 * 
 */
package com.jzb.fdf;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jzb.fdf.prcs.FolderProcessor;
import com.jzb.fdf.srvc.IFileSrvc;
import com.jzb.fdf.srvc.IModelSrvc;
import com.jzb.fdf.srvc.SFile;
import com.jzb.util.Tracer;
import com.jzb.util.Tracer.Level;

/**
 * @author jzarzuela
 * 
 */
public class Tester {

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
            System.out.println("\n***** EXECUTION STARTED (" + new Date() + ") *****\n");
            Tester me = new Tester();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            Tracer.flush();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "] (" + new Date() + ") *****\n");
            System.exit(1);
        } catch (Throwable th) {
            t2 = System.currentTimeMillis();
            Tracer.flush();
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
    public void doIt2(String[] args) throws Exception {

        FolderProcessor._init();
        IModelSrvc.inst.init(false);

        Tracer._debug("***** Processing duplicated files *****");
        IModelSrvc.inst.processDuplicatedFiles();

        /**
         * Tracer._debug("***** Listing duplicated files *****"); List<SFile> duplicated = IFileSrvc.inst.getAllDuplicated(); String lastHashing = ""; for (SFile file : duplicated) { if
         * (!lastHashing.equals(file.getHashing())) { lastHashing = file.getHashing(); System.out.println(); } System.out.println(file.getFullName() + " - " + file.getId() + " - " +
         * file.isDuplicated()); }
         **/

        IModelSrvc.inst.done();
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

        // File folder = new File("/Users/jzarzuela/Downloads/_tmp_/_extracted_IPAs_");
        // File folder = new File("/Users/jzarzuela/Downloads/_tmp_/_extracted_IPAs_/_iconos_varios_/ios-iphone-tab-bar-icons/PNG30x30-black");
        File folder = new File("/Users/jzarzuela/Documents/personal");
        Tracer.setTracer(new AsyncTracer());
        Tracer.setLevelEnabled(Level.DEBUG, true);

        boolean useCopy = false;
        if (useCopy) {
            Files.copy(Paths.get("/Users/jzarzuela/ddbb/findDuplicates.h2.db.copy"), Paths.get("/Users/jzarzuela/ddbb/findDuplicates.h2.db"), StandardCopyOption.REPLACE_EXISTING);

            Files.copy(Paths.get("/Users/jzarzuela/ddbb/findDuplicates.trace.db.copy"), Paths.get("/Users/jzarzuela/ddbb/findDuplicates.trace.db"), StandardCopyOption.REPLACE_EXISTING);
        }

        FolderProcessor._init();
        IModelSrvc.inst.init(true);

        // ****************************************************************************************************
        // ****************************************************************************************************
        // chequear a procesar una carpeta y luego, sin borrar, procesar su carpeta padre
        // ****************************************************************************************************
        // ****************************************************************************************************

        System.out.println("Press enter");
        // System.in.read();

        Tracer._info("Starting global processing");
        FolderProcessor.spawnNewProcessor(folder);

        int cancelCountDown = -1; // -1

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        long t1 = System.currentTimeMillis();
        while (FolderProcessor.pendingProcessorsCount() > 0) {

            Thread.sleep(2000);

            cancelCountDown--;
            if (cancelCountDown == 0) {
                FolderProcessor.cancel();
            }

            double t_diff = System.currentTimeMillis() - t1;
            double completed = FolderProcessor.completedProcessorsCount();
            double pending = FolderProcessor.pendingProcessorsCount();

            String endingTimeStr = "unknown";
            long remainingTime = -1;
            if (completed > 0) {
                remainingTime = (long) (pending * (t_diff / completed));
                long endingTime = System.currentTimeMillis() + remainingTime;
                endingTimeStr = sdf.format(new Date(endingTime));
            }
            Tracer._debug("***** " + completed + " | " + pending + " | " + remainingTime / 1000 + " | " + endingTimeStr + " ************************************");
        }

        FolderProcessor.awaitTermination();
        Tracer._info("Finished global processing");
        Tracer.flush();

        /*
         * System.out.println("------------------------------------------------------------------"); List<SFile> list = MFileSystem.listFiles(); int n = 10; for (SFile sfile : list) {
         * System.out.println(n + " " + sfile.getHashing() + ", " + sfile.getFullName()); n++; }
         */

        IModelSrvc.inst.done();

        System.out.println("Press enter");
        // System.in.read();

    }
}
