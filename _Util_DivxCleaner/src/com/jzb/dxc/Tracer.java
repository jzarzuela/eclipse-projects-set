/**
 * 
 */
package com.jzb.dxc;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author n63636
 * 
 */
public class Tracer {

    private static PrintStream s_out;
    private static PrintStream s_err;
    private static File        s_outFolder = null;
    private static String      s_dateFmt;

    public static void init(File outFolder) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd__hh_mm_ss");
        s_dateFmt = sdf.format(new Date());
        s_outFolder = outFolder;
        s_outFolder.mkdirs();
        getOut();
    }

    private static PrintStream getOut() {
        try {
            if (s_out == null) {
                if (s_outFolder == null)
                    throw new Exception("Error, Traces is not initialized");
                s_out = new PrintStream(new File(s_outFolder, "traces_" + s_dateFmt + ".txt"));
                s_out.println("Output traces " + s_dateFmt);
                s_out.println();
            }
            return s_out;
        } catch (Exception ex) {
            throw new RuntimeException("Error opening output trace file", ex);
        }
    }

    private static PrintStream getErr() {
        try {
            if (s_err == null) {
                if (s_outFolder == null)
                    throw new Exception("Error, Traces is not initialized");
                s_err = new PrintStream(new File(s_outFolder, "error_" + s_dateFmt + ".txt"));
                s_err.println("Error traces " + s_dateFmt);
                s_err.println();
            }
            return s_err;
        } catch (Exception ex) {
            throw new RuntimeException("Error opening error trace file", ex);
        }
    }

    public static void trace(String msg, Object... args) {
        getOut().printf(msg, args);
        getOut().println();
        
        System.out.printf(msg, args);
        System.out.println();
    }

    public static void error(String msg, Object... args) {
        getErr().printf(msg, args);
        getErr().println();
        
        System.out.printf(msg, args);
        System.out.println();
    }

    public static void error(String msg, Throwable th) {
        getErr().println(msg);
        th.printStackTrace(getErr());

        System.out.printf(msg);
        th.printStackTrace(System.out);
    }

    public static boolean isInit() {
        return s_out != null;
    }
}
