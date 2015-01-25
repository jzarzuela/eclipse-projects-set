/**
 * 
 */
package gmap.engine;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import gmap.engine.GMapService.UseCache;
import gmap.engine.data.GFeature;
import gmap.engine.data.GGeometryLine;
import gmap.engine.data.GGeometryPoint;
import gmap.engine.data.GGeometryPolygon;
import gmap.engine.data.GLayer;
import gmap.engine.data.GMap;
import gmap.engine.data.GStyleIcon;

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

        /*
        for (Object key : GStyleIcon.s_iconIds.keySet()) {

            try {
                String iconUrl = GStyleIcon.getUrl((String) key);
                URL url = new URL(iconUrl);

                System.out.println("Downloading icon: " + url);

                BufferedInputStream bis = new BufferedInputStream(url.openStream());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("/Users/jzarzuela/gmap/icons/" + key + ".png"));

                byte buffer[] = new byte[1000];
                for (;;) {
                    int len = bis.read(buffer);
                    if (len < 0)
                        break;
                    bos.write(buffer, 0, len);
                }

                bos.close();
                bis.close();
            } catch (Throwable th) {
                System.out.println(th.getMessage());
            }
        }
*/
        
        Tracer._debug("Login in GMap service");
        GMapService srvc = new GMapService(UseCache.NO);

        srvc.login(SimpleStringCipher.decrypt("Sx__lSZGxCewHhBvqP5if1xlCfYliq2KaOvMBH2bqWw"), SimpleStringCipher.decrypt("j0WGxyZacZJaMcakGObY8w"));

        
        GMap map2 = srvc.getMapData("zeLPXIl-X_4c.k8MVjbnOWqqs");
        GLayer layer = map2.getLayers().get(0);
        GFeature feature = layer.getFeatures().get(0);
        GStyleIcon style = (GStyleIcon)feature.getStyle();
        style.setIconID(71);
        srvc.changeMapLayerStyle(layer);

        //
        // _printAllMapList(srvc);

        //
       // _printMapInfo(srvc, "zeLPXIl-X_4c.keVGBsLwePoU");

        //
        // _processAllMaps(srvc);

        //
        // _changeMap(srvc, "zeLPXIl-X_4c.kAZUmasUiKNQ");

        //
        // _changeMapStyle(srvc, "zeLPXIl-X_4c.kAZUmasUiKNQ");
    }

    // ----------------------------------------------------------------------------------------------------
    @SuppressWarnings("unused")
    private void _changeMap(GMapService srvc, String mapID) throws GMapException {

        GMap map2 = srvc.getMapData(mapID);

        //
        GGeometryLine geoLine = new GGeometryLine();
        geoLine.addGeoPoint(new GGeometryPoint(41.3110858, -3.4470624));
        geoLine.addGeoPoint(new GGeometryPoint(41.3113356, -3.4466547));
        geoLine.addGeoPoint(new GGeometryPoint(41.3111019, -3.446542));
        geoLine.addGeoPoint(new GGeometryPoint(41.3112228, -3.4462363));
        geoLine.addGeoPoint(new GGeometryPoint(41.3114928, -3.446306));
        geoLine.addGeoPoint(new GGeometryPoint(41.3116741, -3.4459573));
        geoLine.addGeoPoint(new GGeometryPoint(41.3112067, -3.4456944));
        GFeature featureLine = map2.getLayers().get(0).addFeature(GFeature.generateID(), geoLine);
        featureLine.setProperty("nombre", "nombre_line");
        featureLine.setProperty("descripción", "desc_line");
        featureLine.setProperty("icon", "icon_line");

        //
        GGeometryPolygon geoPolygon = new GGeometryPolygon();
        geoPolygon.addGeoPoint(new GGeometryPoint(41.3117023, -3.4466654));
        geoPolygon.addGeoPoint(new GGeometryPoint(41.3114081, -3.4468478));
        geoPolygon.addGeoPoint(new GGeometryPoint(41.3114243, -3.4463435));
        geoPolygon.addGeoPoint(new GGeometryPoint(41.3112792, -3.4464616));
        geoPolygon.addGeoPoint(new GGeometryPoint(41.3110818, -3.4462577));
        geoPolygon.addGeoPoint(new GGeometryPoint(41.3112268, -3.4459734));
        geoPolygon.addGeoPoint(new GGeometryPoint(41.3116338, -3.4460378));
        GFeature featurePolygon = map2.getLayers().get(0).addFeature(GFeature.generateID(), geoPolygon);
        featurePolygon.setProperty("nombre", "nombre_polygon");
        featurePolygon.setProperty("descripción", "desc_polygon");
        featurePolygon.setProperty("icon", "icon_polygon");

        //
        GFeature feature1 = map2.getLayers().get(0).addFeature(GFeature.generateID(), new GGeometryPoint(41.312, -3.447));
        feature1.setProperty("nombre", "nombre_1");
        feature1.setProperty("descripción", "desc_1");
        feature1.setProperty("icon", "icon_1");

        GFeature feature2 = map2.getLayers().get(0).addFeature(GFeature.generateID(), new GGeometryPoint(41.313, -3.448));
        feature2.setProperty("nombre", "nombre_2");
        feature2.setProperty("descripción", "desc_2");
        feature2.setProperty("icon", "icon_2");

        GFeature feature3 = map2.getLayers().get(0).addFeature(GFeature.generateID(), new GGeometryPoint(41.314, -3.449));
        feature3.setProperty("nombre", "nombre_3");
        feature3.setProperty("descripción", "desc_3");
        feature3.setProperty("icon", "icon_3");

        //
        System.out.println("--- addMapFeatures ---");
        srvc.addMapFeatures(featurePolygon, featureLine, feature1, feature2, feature3);

        //
        featurePolygon.setProperty("nombre", "nombre_polygon_1");
        featurePolygon.setProperty("descripción", "desc_polygon_1");

        featureLine.setProperty("nombre", "nombre_line_1");
        featureLine.setProperty("descripción", "desc_line_1");

        feature1.setProperty("nombre", "nombre_1_1");
        feature1.setProperty("descripción", "desc_1_1");

        feature2.setProperty("nombre", "nombre_2_2");
        feature2.setProperty("descripción", "desc_2_2");
        feature2.setProperty("icon", "icon_2_2");

        feature3.setProperty("nombre", "nombre_3_3");
        feature3.setProperty("descripción", "desc_3_3");
        feature3.setProperty("icon", "icon_3_3");

        //
        System.out.println("--- updateMapFeatures ---");
        srvc.updateMapFeatures(featurePolygon, featureLine, feature1, feature2, feature3);

        //
        System.out.println("--- deleteMapFeatures ---");
        srvc.deleteMapFeatures(featurePolygon, featureLine, feature1, feature3);
    }

    // ----------------------------------------------------------------------------------------------------
    @SuppressWarnings("unused")
    private void _changeMapStyle(GMapService srvc, String mapID) throws GMapException {

        GMap map2 = srvc.getMapData(mapID);
        // System.out.println(map2);

        /*
         * GLayer layer = map2.getLayerByID("zeLPXIl-X_4c.k0rVgcYf3XT4"); GStyleIcon styleIcon = (GStyleIcon)layer.getFeatureByID("087AA3FE26972ED0").getStyle(); styleIcon.setIconID(965);
         * //styleIcon.setIconID(69); srvc.changeMapLayerStyle(layer);
         */

        for (GLayer layer : map2.getLayers()) {
            srvc.changeMapLayerStyle(layer);
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
    @SuppressWarnings("unused")
    private void _printAllMapList(GMapService srvc) throws GMapException {

        ArrayList<UserMapData> mapList = srvc.getUserMapList();

        Tracer._debug("----- User Map List --------------------------------------------");
        for (UserMapData umd : mapList) {
            Tracer._debug(umd.toString());
        }
        Tracer._debug("");

    }

    // ----------------------------------------------------------------------------------------------------
    @SuppressWarnings("unused")
    private void _printMapInfo(GMapService srvc, String mapID) throws GMapException {

        GMap map2 = srvc.getMapData(mapID);
        System.out.println(map2);

    }

    // ----------------------------------------------------------------------------------------------------
    @SuppressWarnings("unused")
    private void _processAllMaps(GMapService srvc) throws GMapException {

        ArrayList<UserMapData> mapList = srvc.getUserMapList();
        for (UserMapData userMapData : mapList) {
            try {
                Tracer._debug("");
                Tracer._debug("Processing map: " + userMapData.getName() + " - id: " + userMapData.getId());
                GMap map = _getMapDataWithThrottleCheck(srvc, userMapData.getId());
                map.toString();
            } catch (Throwable th) {
                Tracer._error("** Error processing map: " + userMapData.getName() + " - Exc: " + th.getClass().toString() + " / " + th.getMessage());
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
