/**
 * 
 */
package com.jzb.ttpoi.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gdata.client.maps.MapsService;
import com.google.gdata.data.maps.MapEntry;
import com.google.gdata.data.maps.MapFeed;
import com.jzb.util.DefaultHttpProxy;
import com.jzb.util.Des3Encrypter;

/**
 * @author n63636
 * 
 */
public class KMLDownload {

    private static boolean s_proxyChecked = false;

    private static void _checkProxy() throws Exception {
        if (!s_proxyChecked) {
            DefaultHttpProxy.setDefaultJavaProxy();
            s_proxyChecked = true;
        }
    }

    public static void downloadAllMaps(File baseFolder) throws Exception {
        downloadMap(baseFolder, null);
    }

    public static File downloadMap(File baseFolder, String mapName) throws Exception {

        File kmlFile = null;
        
        _checkProxy();
        baseFolder.mkdirs();
        HashMap<String, URL> mapList = _getMapLinks();
        for (Map.Entry<String, URL> entry : mapList.entrySet()) {
            String name = entry.getKey();
            URL link = entry.getValue();
            if (mapName == null || mapName.equalsIgnoreCase(name)) {
                kmlFile = _downloadMap(baseFolder, name, link);
            }
        }
        
        return kmlFile;

    }

    private static File _downloadMap(File baseFolder, String mapName, URL link) throws Exception {

        File kmlFile = new File(baseFolder, mapName + ".kml");
        System.out.println();
        System.out.println("Downloading map '" + mapName + "' from '" + link + "'");

        byte buffer[] = new byte[65536];
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(kmlFile));
        BufferedInputStream bis = new BufferedInputStream(link.openStream());
        for (;;) {
            int len = bis.read(buffer);
            if (len > 0) {
                bos.write(buffer, 0, len);
            } else {
                break;
            }
        }
        bis.close();
        bos.close();

        return kmlFile;
    }

    private static HashMap<String, URL> _getMapLinks() throws Exception {

        HashMap<String, URL> mapList = new HashMap<String, URL>();

        MapsService myService = new MapsService("listAllMaps");
        myService.setUserCredentials(Des3Encrypter.decryptStr("PjN1Jb0t6CYNTbO/xEgJIjCPPPfsmPez"), Des3Encrypter.decryptStr("8ivdMeBQiyQtSs1BFkf+mw=="));

        final URL feedUrl = new URL("http://maps.google.com/maps/feeds/maps/default/full");

        MapFeed resultFeed = myService.getFeed(feedUrl, MapFeed.class);
        for (int i = 0; i < resultFeed.getEntries().size(); i++) {

            MapEntry entry = resultFeed.getEntries().get(i);
            String mapName = entry.getTitle().getPlainText();
            String selfLink = entry.getSelfLink().getHref();

            int p1 = selfLink.lastIndexOf("/maps/");
            int p2 = selfLink.indexOf("/full/", p1);

            String part1 = selfLink.substring(p1 + 6, p2);
            String part2 = selfLink.substring(p2 + 6);

            String mapURL = "http://maps.google.es/maps/ms?hl=es&ie=UTF8&vps=3&jsv=304e&oe=UTF8&msa=0&msid=" + part1 + "." + part2 + "&output=kml";

            System.out.println(mapName + " => " + mapURL);
            mapList.put(mapName, new URL(mapURL));
        }

        return mapList;
    }

}
