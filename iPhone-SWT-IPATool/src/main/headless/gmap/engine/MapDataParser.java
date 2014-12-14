/**
 * 
 */
package gmap.engine;

import gmap.engine.data.GMap;
import gmap.engine.parser.GMapServiceParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONObject;

/**
 * @author jzarzuela
 *
 */
public class MapDataParser {

    /**
     * 
     */
    public MapDataParser() {
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
            MapDataParser me = new MapDataParser();
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
     * @throws GMapException
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        InputStream is = MapDataParser.class.getClassLoader().getResourceAsStream("gmap/engine/mapData.txt");
        // InputStream is = MapDataParser.class.getClassLoader().getResourceAsStream("gmap/engine/req-dump-1417294741416.txt");

        StringBuilder sb = new StringBuilder();
        char cbuf[] = new char[1024];
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            while (bf.ready()) {
                int len = bf.read(cbuf);
                if (len > 0) {
                    sb.append(cbuf, 0, len);
                }
            }
        }

        String str = sb.toString();

        // System.out.println(str);
        // for (int n = 0; n < 20; n++)
        // System.out.println(n + " --> " + str.charAt(n));

        JSONObject json_info = new JSONObject(str.substring(16));
        // System.out.println(json_info.toString(2));

        str = json_info.getString("mapdataJson");
        GMap map = GMapServiceParser.parseMapDataJson(str);

        System.out.println(map);
    }

}
