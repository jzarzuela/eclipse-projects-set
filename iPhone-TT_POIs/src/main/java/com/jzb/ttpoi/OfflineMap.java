/**
 * 
 */
package com.jzb.ttpoi;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileLoader;
import com.jzb.util.DefaultHttpProxy;

/**
 * @author jzarzuela
 * 
 */
public class OfflineMap {

    private AtomicInteger                        m_taskCount       = new AtomicInteger(0);
    // private ArrayList<OfflineMapBucket> m_buckets = new ArrayList<>();
    // private int m_nextBucketCount = 0;
    private HashSet<String>                      m_downloadedTiles = new HashSet<>();
    private int                                  m_serverIndex     = 0;
    private ExecutorService                      m_execSrvc;
    private HashMap<Integer, ArrayList<Integer>> m_buckets         = new HashMap<>();
    private long                                 m_totalSize       = 0;
    private long                                 m_numTasks        = 0;

    /**
     * 
     */
    public OfflineMap() {
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
            OfflineMap me = new OfflineMap();
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

        // OpenCycleMap: NSString *template = @"http://b.tile.opencyclemap.org/cycle/{z}/{x}/{y}.png";
        // MapQuest[19]: NSString *template = @"http://otile3.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.jpg";
        // OpenStreetMap: NSString *template = @"http://tile.openstreetmap.org/{z}/{x}/{y}.png";
        // Google Maps[22]: NSString *template = @"http://mt0.google.com/vt/x={x}&y={y}&z={z}";

        String mapName = "TMP3";
        //String mapName = "HT_Francia_2011";
        //String mapName = "BT_Boston_2010_2013";
        //String mapName = "HT_Holanda_2014";

        System.out.printf("--------------------------------------------------------\n");
        System.out.printf("Map name = %s\n",mapName);
        System.out.printf("--------------------------------------------------------\n");
        
        File offlineFolder = new File("/Users/jzarzuela/Downloads/_tmp_/pois/_offline_maps_/" + mapName + "/");
        File kmlFolder = new File("/Users/jzarzuela/Downloads/_tmp_/pois/_KMLs_");
        File kmlFile = new File(kmlFolder, mapName + ".kml");

        checkAlreadyDownloaded(offlineFolder);
        System.out.printf("Already downloaded tiles: %d. Current total size = %,d\n", m_downloadedTiles.size(),  m_totalSize);

        TPOIFileData mapData = KMLFileLoader.loadFile(kmlFile);

        m_execSrvc = Executors.newFixedThreadPool(12);

        DefaultHttpProxy.setDefaultJavaProxy();

        for (TPOIData poi : mapData.getAllPOIs()) {

            if (poi.Is_TT_Cat_POI())
                continue;

            downloadPoiTiles(poi, offlineFolder);

        }

        System.out.println("--- Shutdown ---");
        m_execSrvc.shutdown();
        System.out.println("--- Waiting ---");
        m_execSrvc.awaitTermination(10000, TimeUnit.DAYS);
    }

    // ----------------------------------------------------------------------------------------------------
    private void checkAlreadyDownloaded(File folder) {

        if (folder.listFiles() == null)
            return;

        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                checkAlreadyDownloaded(file);
            } else if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg")) {
                m_totalSize += file.length();
                m_downloadedTiles.add(file.getName());
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private String getTileKey(int tileX, int tileY) {
        return tileX + "_" + tileY + ".jpg";

    }

    // ----------------------------------------------------------------------------------------------------
    // static final int tilesForZoomLevel[] = { 2, 3, 3, 4, 4, 5 };
    //                                       6  7  8  9 10 11 12 13 14 15 16 17
    static final int tilesForZoomLevel[] = { 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 5, 5 };

    private void downloadPoiTiles(TPOIData poi, File offlineFolder) throws Exception {

        for (int n = 0; n < 12; n++) {

            int zoomLevel = 6 + n;
            int aroundTiles = tilesForZoomLevel[n];

            // Calcula el tile base del punto
            double sinLatitude = Math.sin(poi.getLat() * Math.PI / 180.0);
            double pixelX = ((poi.getLng() + 180.0) / 360.0) * Math.pow(2, zoomLevel);
            double pixelY = (0.5 - Math.log((1.0 + sinLatitude) / (1.0 - sinLatitude)) / (4.0 * Math.PI)) * Math.pow(2, zoomLevel);

            downloadPoiTiles(zoomLevel, aroundTiles, (int) pixelX, (int) pixelY, offlineFolder);

        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void downloadPoiTiles(int zoomLevel, int aroundTiles, int poiTileX, int poiTileY, File offlineFolder) throws Exception {

        for (int y = -aroundTiles; y < aroundTiles; y++) {

            int tileY = poiTileY + y;
            if (tileY < 0)
                continue;

            for (int x = -aroundTiles; x < aroundTiles; x++) {

                int tileX = poiTileX + x;
                if (tileX < 0)
                    continue;

                String tileKey = getTileKey(tileX, tileY);

                if (m_downloadedTiles.contains(tileKey)) {
                    // System.out.println("tile already downloaded: " + tileKey);
                    continue;
                }

                m_downloadedTiles.add(tileKey);

                File tileFile = new File(offlineFolder, zoomLevel + "/" + geBucketStr(zoomLevel) + "/" + tileKey);

                URL tileURL = new URL("http://otile" + (1 + m_serverIndex) + ".mqcdn.com/tiles/1.0.0/osm/" + zoomLevel + "/" + tileX + "/" + tileY + ".jpg");
                // URL tileURL = new URL("http://mt" + m_serverIndex + ".google.com/vt/x=" + tileX + "&y=" + tileY + "&z=" + zoomLevel);
                m_serverIndex = (m_serverIndex + 1) % 4;

                tileFile.getParentFile().mkdirs();

                m_numTasks = m_taskCount.incrementAndGet();
                m_execSrvc.submit(new OfflineMapTask(this, tileFile, tileURL));
            }
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private String geBucketStr(int zoomLevel) {

        ArrayList<Integer> zoomBuckets = m_buckets.get(zoomLevel);
        if (zoomBuckets == null) {
            zoomBuckets = new ArrayList<>();
            zoomBuckets.add(0);
            zoomBuckets.add(0);
            zoomBuckets.add(0);
            m_buckets.put(zoomLevel, zoomBuckets);
        }

        String bucketStr = "a" + zoomBuckets.get(0) + "/b" + zoomBuckets.get(1);

        zoomBuckets.set(1, 1 + zoomBuckets.get(1));
        if (zoomBuckets.get(1) >= 16) {
            zoomBuckets.set(1, 0);
        }

        zoomBuckets.set(2, 1 + zoomBuckets.get(2));
        if (zoomBuckets.get(2) >= 128 * 16) {
            zoomBuckets.set(0, 1 + zoomBuckets.get(0));
            zoomBuckets.set(1, 0);
            zoomBuckets.set(2, 0);
        }

        return bucketStr;
    }

    // ----------------------------------------------------------------------------------------------------
    /*
     * static final int MAX_BUCKET_ELEMENTS = 128;
     * 
     * public OfflineMapBucket getBucket() {
     * 
     * OfflineMapBucket bucket;
     * 
     * if (m_buckets.size() > 0) { bucket = m_buckets.get(0); if (bucket.incrementAndGet() >= MAX_BUCKET_ELEMENTS) { m_buckets.remove(0); return getBucket(); }
     * 
     * } else { bucket = new OfflineMapBucket("b" + m_nextBucketCount, 0); m_nextBucketCount++; m_buckets.add(bucket); }
     * 
     * return bucket; }
     */
    // ----------------------------------------------------------------------------------------------------
    public synchronized void taskEnded(int size) {

        m_totalSize += size;
        int value = m_taskCount.decrementAndGet();
        System.out.printf("Downloading task ended. Pending tasks: %d of %d. Total size = %,d\n", value, m_numTasks, m_totalSize);
    }

}
