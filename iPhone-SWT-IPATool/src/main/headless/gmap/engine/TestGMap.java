/**
 * 
 */
package gmap.engine;

import gmap.engine.data.GLayer;
import gmap.engine.data.GMap;

import java.util.ArrayList;
import java.util.HashSet;

import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 *
 */
public class TestGMap {

    /**
     * 
     */
    public TestGMap() {
        // TODO Auto-generated constructor stub
    }

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
            TestGMap me = new TestGMap();
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

        Tracer._debug("Login in GMap service");
        GMapService srvc = new GMapService();

        srvc.login(SimpleStringCipher.decrypt("Sx__lSZGxCewHhBvqP5if1xlCfYliq2KaOvMBH2bqWw"), SimpleStringCipher.decrypt("j0WGxyZacZJaMcakGObY8w"));

        ArrayList<UserMapData> mapList = srvc.getUserMapList();

        /*
         * Tracer._debug("----- User Map List --------------------------------------------"); for (UserMapData umd : mapList) { Tracer._debug(umd.toString()); } Tracer._debug("");
         */

        GMap map2 = srvc.getMapData("zeLPXIl-X_4c.kAZUmasUiKNQ");
        System.out.println(map2);

        HashSet<String> allPropNames = new HashSet<String>();
        for (UserMapData userMapData : mapList) {
            try {
                Tracer._debug("");
                Tracer._debug("Processing map: " + userMapData.getName() + " - id: " + userMapData.getId());
                GMap map = _getMapDataWithThrottleCheck(srvc, userMapData.getId());
                if (map.getName() != null) {
                    for (GLayer layer : map.getLayers()) {
                        allPropNames.addAll(layer.getSchema().keySet());
                    }
                }
            } catch (Throwable th) {
                Tracer._error("** Error processing map: " + userMapData.getName() + " - Exc: " + th.getClass().toString() + " / " + th.getMessage());
            }
        }
        
        Tracer._debug("All property names:");
        for(String propName:allPropNames) {
            Tracer._debug("  "+propName);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private GMap _getMapDataWithThrottleCheck(GMapService srvc, String mapID) throws GMapException {

        final int MAX_ATTEMPS = 2;

        int attemps = MAX_ATTEMPS;
        for (;;) {
            try {
                return srvc.getMapData(mapID);
            } catch (GMapExceptionCode429 ex) {
                attemps--;
                if (attemps <= 0) {
                    Tracer._warn("Too many-many requests in time interval. COULD NOT DOWNLOAD MAP");
                    throw ex;
                }
                Tracer._warn("Too many requests in time interval. Waiting 25s to repeat");
                _safeSleep(25000);
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _safeSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
        }
    }
}
