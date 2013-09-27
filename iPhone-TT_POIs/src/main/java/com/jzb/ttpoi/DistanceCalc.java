/**
 * 
 */
package com.jzb.ttpoi;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileLoader;
import com.jzb.ttpoi.wl.KMLFileWriter;

/**
 * @author n63636
 * 
 */
public class DistanceCalc {

    /**
     * Distancia en kilometros
     * 
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double calc(double lat1, double lng1, double lat2, double lng2) {

        double rLat1 = lat1 * Math.PI / 180.0;
        double rLng1 = lng1 * Math.PI / 180.0;
        double rLat2 = lat2 * Math.PI / 180.0;
        double rLng2 = lng2 * Math.PI / 180.0;
        double earthRadius = 6378000; // In metres

        double result = Math.acos(  Math.cos(rLat1) * Math.cos(rLng1) * Math.cos(rLat2) * Math.cos(rLng2) 
                                  + Math.cos(rLat1) * Math.sin(rLng1) * Math.cos(rLat2) * Math.sin(rLng2) 
                                  + Math.sin(rLat1)* Math.sin(rLat2))
                                  * earthRadius;

        return result;

    }
    
    /**
     * Static Main starting method
     * 
     * @param args command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            DistanceCalc me = new DistanceCalc();
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
     * @param args command line parameters
     * @throws Exception if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {
        
        
        System.out.println(calc(0,0,2.0E-5,2.0E-5));
    }
    
    
    
}
