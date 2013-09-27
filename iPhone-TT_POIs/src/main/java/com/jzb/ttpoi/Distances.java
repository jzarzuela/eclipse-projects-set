/**
 * 
 */
package com.jzb.ttpoi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.OV2FileLoader;
import com.jzb.util.DefaultHttpProxy;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class Distances {

    private static class DirData {

        public boolean failed = true;

        public String  originName;
        public String  originPoint;
        public String  destinationName;
        public String  destinationPoint;
        public int     distance;        // kilometros
        public int     duration;        // minutos

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {

            String strDur = (duration / 60) + ":" + (duration % 60) + "";
            // return "Distance '" + originName + "(" + originPoint + ")' to '" + destinationName + "(" + destinationPoint + ")' -> Distance:" + distance + "Km, Duration:" + strDur;
            return originName + " - " + destinationName + ", " + distance + ", " + strDur;
        }

        public String getKey() {
            return originName + " - " + destinationName;
        }
    }

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
            Distances me = new Distances();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(-1);
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

        TPOIFileData TestTFD = new TPOIFileData();
        TestTFD.setName("Test");

        File folder = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_OV2_\\HT_NORMANDIA_POIs\\new");
        for (File f : folder.listFiles()) {
            if (f.isDirectory())
                continue;
            TPOIFileData tfd = OV2FileLoader.loadFile(f);
            Collections.sort(tfd.getAllPOIs());
            TestTFD.addAllPOIs(tfd.getAllPOIs());
        }

        ArrayList<TPOIData> pois = new ArrayList<TPOIData>();

        // SON 161 ENTRADAS EN TOTAL

        pois.clear();
        pois.addAll(TestTFD.getAllCategoryPOIs("1_Aste"));
        pois.addAll(TestTFD.getAllCategoryPOIs("2_Loir"));
        _calcDistances(pois);

        pois.clear();
        pois.addAll(TestTFD.getAllCategoryPOIs("2_Loir"));
        pois.addAll(TestTFD.getAllCategoryPOIs("3_Bret_4"));
        pois.addAll(TestTFD.getAllCategoryPOIs("3_Bret_5"));
        _calcDistances(pois);

        pois.clear();
        pois.addAll(TestTFD.getAllCategoryPOIs("3_Bret_4"));
        pois.addAll(TestTFD.getAllCategoryPOIs("3_Bret_5"));
        pois.addAll(TestTFD.getAllCategoryPOIs("4_Dese"));
        _calcDistances(pois);

        pois.clear();
        pois.addAll(TestTFD.getAllCategoryPOIs("4_Dese"));
        pois.addAll(TestTFD.getAllCategoryPOIs("5_Norm_4"));
        pois.addAll(TestTFD.getAllCategoryPOIs("5_Norm_4"));
        _calcDistances(pois);

        pois.clear();
        pois.addAll(TestTFD.getAllCategoryPOIs("5_Norm_4"));
        pois.addAll(TestTFD.getAllCategoryPOIs("5_Norm_4"));
        pois.addAll(TestTFD.getAllCategoryPOIs("1_Aste"));
        _calcDistances(pois);

    }

    private void _calcDistances(ArrayList<TPOIData> pois) throws Exception {

        HashSet<String> keys = _readKeys();

        for (int n = 0; n < pois.size() - 1; n++) {

            TPOIData poi1 = pois.get(n);

            for (int i = n + 1; i < pois.size(); i++) {

                TPOIData poi2 = pois.get(i);

                @SuppressWarnings("synthetic-access")
                DirData info = new DirData();
                info.originName = poi1.getName();
                info.originPoint = poi1.getLat() + "," + poi1.getLng();
                info.destinationName = poi2.getName();
                info.destinationPoint = poi2.getLat() + "," + poi2.getLng();

                if (!keys.contains(info.getKey())) {
                    Tracer._debug("1 Calculating route info : " + info);
                    _calcRoute(info);
                    if (!info.failed) {
                        Tracer._debug("2 Route info calculated OK: " + info);
                        _writeInfo(info);
                    } else {
                        Tracer._warn("9 Route info calculation failed: " + info);
                    }
                    keys.add(info.getKey());
                } else {
                    Tracer._debug("0 Route info calculation skipped: " + info);
                }

            }
        }

    }

    private HashSet<String> _readKeys() {
        HashSet<String> keys = new HashSet<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("C:\\WKSPs\\Consolidado\\TT_POIs\\data\\Distances.txt"));
            while (br.ready()) {
                String s = br.readLine().trim();
                if (s.length() > 0) {
                    int p1 = s.indexOf(',');
                    keys.add(s.substring(0, p1));
                }
            }
            br.close();
        } catch (Exception ex) {
            System.out.println("Error leyendo claves: " + ex);
        }
        return keys;
    }

    private void _writeInfo(DirData info) {
        try {
            FileWriter fw = new FileWriter("C:\\WKSPs\\Consolidado\\TT_POIs\\data\\Distances.txt", true);
            fw.write(info.toString());
            fw.write("\r\n");
            fw.flush();
            fw.close();
        } catch (Exception ex) {
            System.out.println("Error escribiendo informacion (" + info + "): " + ex);
        }
    }

    private long lastTime = 0;

    private void _calcRoute(DirData info) throws Exception {

        // Espera de 20s para no pasar la cuota
        final int TIMEOUT = 20000;
        long difT = System.currentTimeMillis() - lastTime;
        if (difT < TIMEOUT) {
            Thread.sleep(TIMEOUT - difT);
        }

        __calcRoute(info);

        lastTime = System.currentTimeMillis();

    }

    private void __calcRoute(DirData info) throws Exception {

        DefaultHttpClient client = new DefaultHttpClient();
        DefaultHttpProxy.setDefaultProxy(client);

        HttpPost request = new HttpPost("http://maps.googleapis.com/maps/api/directions/xml");
        HttpParams params = new BasicHttpParams();
        params.setParameter("origin", info.originPoint);
        params.setParameter("destination", info.destinationPoint);
        params.setParameter("mode", "driving");
        params.setParameter("alternatives", "false");
        params.setParameter("units", "metric");
        params.setParameter("sensor", "false");
        params.setParameter("language", "es");
        request.setParams(params);
        HttpResponse response = client.execute(request);

        if (response.getStatusLine().getStatusCode() != 200) {
            System.out.println("Error en peticion: ");
            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println(response.getStatusLine());
            return;
        }

        DocumentBuilderFactory dbfact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfact.newDocumentBuilder();
        Document doc = null;

        try {
            doc = docBuilder.parse(response.getEntity().getContent());
        } catch (Exception ex) {
            System.out.println("Error parseando respuesta " + ex);
            return;
        }

        XPathFactory xpFact = XPathFactory.newInstance();
        XPath xp = xpFact.newXPath();

        XPathExpression exp1 = xp.compile("/DirectionsResponse/route/leg/step/distance/value/text()");
        XPathExpression exp2 = xp.compile("/DirectionsResponse/route/leg/step/duration/value/text()");

        XPathExpression exp3 = xp.compile("/DirectionsResponse/status/text()");
        String status = exp3.evaluate(doc);
        if (!status.equals("OK")) {
            System.out.println("Error en codigo de respuesta: " + status);
            return;
        }

        info.distance = 0;
        NodeList nl = (NodeList) exp1.evaluate(doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {
            Node node = nl.item(n);
            info.distance += Integer.parseInt(node.getNodeValue());
        }
        info.distance = info.distance / 1000;

        info.duration = 0;
        nl = (NodeList) exp2.evaluate(doc, XPathConstants.NODESET);
        for (int n = 0; n < nl.getLength(); n++) {
            Node node = nl.item(n);
            info.duration += Integer.parseInt(node.getNodeValue());
        }
        info.duration = info.duration / 60;

        info.failed = false;
    }

}
