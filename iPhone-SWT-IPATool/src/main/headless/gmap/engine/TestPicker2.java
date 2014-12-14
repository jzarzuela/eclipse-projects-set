/**
 * 
 */
package gmap.engine;

import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * @author jzarzuela
 *
 */
public class TestPicker2 {

    /**
     * 
     */
    public TestPicker2() {
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
            TestPicker2 me = new TestPicker2();
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

        DefaultHttpClient client = new DefaultHttpClient();

        HttpHost proxy = new HttpHost("127.0.0.1", 8888);
        client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);


        CookieStore cookieStore = new BasicCookieStore();
        _addCookie(cookieStore, "GAPS", "1:ARnCGrxE9JjCmdiHM6HuSRJJubx-Mw:jCERyBarmkB6RWXF");
        _addCookie(cookieStore, "GALX", "jlLzmg-Z7SY");
        client.setCookieStore(cookieStore);

        HttpPost request = new HttpPost("https://accounts.google.com/ServiceLoginAuth");
        
        ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("GALX", "jlLzmg-Z7SY"));
        nvps.add(new BasicNameValuePair("continue", "https://mapsengine.google.com/map/splash"));
        nvps.add(new BasicNameValuePair("followup", "https://mapsengine.google.com/map/splash"));
        nvps.add(new BasicNameValuePair("service", "mapsengine"));
        nvps.add(new BasicNameValuePair("_utf8", "â"));
        nvps.add(new BasicNameValuePair("bgresponse", ""));
        nvps.add(new BasicNameValuePair("pstMsg", "1"));
        nvps.add(new BasicNameValuePair("dnConn", ""));
        nvps.add(new BasicNameValuePair("checkConnection", ""));
        nvps.add(new BasicNameValuePair("checkedDomains", "youtube"));
        nvps.add(new BasicNameValuePair("Email", "jzarzuela@gmail.com"));
        nvps.add(new BasicNameValuePair("Passwd", "xxxxx"));
        nvps.add(new BasicNameValuePair("signIn", "Sign+in"));
        nvps.add(new BasicNameValuePair("PersistentCookie", "yes"));
        nvps.add(new BasicNameValuePair("rmShown", "1"));
        request.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

        request.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/600.1.17 (KHTML, like Gecko) Version/7.1 Safari/537.85.10");
        request.setHeader("Accept-Language", "en-US,en;q=0.5");
        request.setHeader("Accept", "*/*,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Cache-Control", "no-cache");
        request.setHeader("Pragma", "no-cache");
        request.setHeader("X-Same-Domain", "1");

        HttpResponse response = client.execute(request);

        if (response.getStatusLine().getStatusCode() != 200) {
            System.out.println("Error en peticion: ");
            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println(response.getStatusLine());
        } else {
            System.out.println("--- getAllHeaders ---");
            Header[] readers = response.getAllHeaders();
            for (Header header : readers) {
                System.out.println(header.getName() + ":" + header.getValue());
            }

            System.out.println("--- body ---");
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                System.out.println("EMPTY RESPONSE");
            } else {

                String responseBody = EntityUtils.toString(entity);
                System.out.println(responseBody);
            }
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

        DefaultHttpClient client = new DefaultHttpClient();

        HttpHost proxy = new HttpHost("127.0.0.1", 8888);
        client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        HttpGet request = new HttpGet("https://accounts.google.com/ServiceLogin?service=mapsengine&passive=1209600&continue=https://mapsengine.google.com/map/splash&followup=https://mapsengine.google.com/map/splash");

        request.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/600.1.17 (KHTML, like Gecko) Version/7.1 Safari/537.85.10");
        request.setHeader("Accept-Language", "en-US,en;q=0.5");
        request.setHeader("Accept", "*/*,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Cache-Control", "no-cache");
        request.setHeader("Pragma", "no-cache");
        request.setHeader("X-Same-Domain", "1");

        HttpResponse response = client.execute(request);

        if (response.getStatusLine().getStatusCode() != 200) {
            System.out.println("Error en peticion: ");
            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println(response.getStatusLine());
        } else {
            System.out.println("--- getAllHeaders ---");
            Header[] readers = response.getAllHeaders();
            for (Header header : readers) {
                System.out.println(header.getName() + ":" + header.getValue());
            }

            System.out.println("--- body ---");
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                System.out.println("EMPTY RESPONSE");
            } else {

                String responseBody = EntityUtils.toString(entity);
                System.out.println(responseBody);
            }
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
    public void doIt3(String[] args) throws Exception {

        // System.setProperty("https.proxyHost","127.0.0.1");
        // System.setProperty("https.proxyPort","8888");
        // System.setProperty("javax.net.ssl.trustStore","/iPhone-SWT-IPATool/src/main/headless/com/jzb/ipa/keystore");

        DefaultHttpClient client = new DefaultHttpClient();

        HttpHost proxy = new HttpHost("127.0.0.1", 8888);
        // client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        CookieStore cookieStore = new BasicCookieStore();

        _addCookie(cookieStore, "SSID", "AX1fPIAZaYfY1xpel");
        _addCookie(cookieStore, "HSID", "AhC6QOgb2ZYeRnhtz");
        _addCookie(
                cookieStore,
                "SID",
                "DQAAAO4AAAAlGGd6TuX1ICwgTC5mKS3NMTy3Dkq8vRgdnzKGPpVCSwMg9LPTSKnyD3WKyLSNHRGqMIt5UdLUx8A-KMIpZ7M6KN7bpoiqzuqPp02FjVuiiF7cHlQxgtHlGw_AHz6i1122VmLHl3XV1aQZcm5-m0z42FuQyUt3TmBYaRNpny5GRc2_GewfXFKyTCczKsMKBuCMDTfT_TOTj0l4qJY6WekY-I4q1DtoqWg4rA4uYCiSlGkXLd_lHoSU1ciHbao_9smuiUiHUNrhWTuZ0heranVP6tTwQMvbgnQrRR1bmYx6ihhgwZTv4RgqtmzzmhIMVdA");

        client.setCookieStore(cookieStore);

        HttpPost request = new HttpPost("https://docs.google.com/picker/pvr?hl=es&hostId=MapsPro");

        String _auth = "DQAAAPAAAADy4LVnXCsWHsurAYsIYqyQZIt-_C8rmu50vj-kkhlU_nKUdc2L7evy_gUPV64GoivnPNP_IKIQ6WQ81iFxit6ONQbj57PGmoF-ZHKzwEC-2K8wRvQQ_YfYNSOBcH0PoeJ12WPMvJcixenb8GvEXsOCrQX_wv5sdzniXZyrw8B0ooBATMjTsNDha0DKaNhQ2Fbd890x-ejjAk4Ycm6oKbA_F6aIF7GGTi7sxd7zq21vaSz9vmJeb7L4VrZ6lYjaC780AW3PSn-FsYeZ6nGnZH4kSl4vQPv12d3uZ8EwKHonnE6DwxaSetRd4hvrKb4Shfk";
        request.setHeader("Authorization", "GoogleLogin auth=" + _auth);

        ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("start", "0"));
        nvps.add(new BasicNameValuePair("numResults", "50"));
        nvps.add(new BasicNameValuePair("sort", "3"));
        nvps.add(new BasicNameValuePair("desc", "true"));
        nvps.add(new BasicNameValuePair("cursor", ""));
        nvps.add(new BasicNameValuePair("service", "mapspro"));
        nvps.add(new BasicNameValuePair("type", "owned"));
        nvps.add(new BasicNameValuePair("options", "null"));
        nvps.add(new BasicNameValuePair("token", "Q30EAkoBAAA.GC6vL5oliYpdHO-y1NlwPA.FQvyqmSZdMdP5RoNZn9wsw"));
        nvps.add(new BasicNameValuePair("version", "4"));
        nvps.add(new BasicNameValuePair("subapp", "5"));
        nvps.add(new BasicNameValuePair("app", "2"));
        nvps.add(new BasicNameValuePair("clientUser", "13510860289450940306"));
        nvps.add(new BasicNameValuePair("authuser", "0"));
        request.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        request.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/600.1.17 (KHTML, like Gecko) Version/7.1 Safari/537.85.10");
        request.setHeader("Accept-Language", "en-US,en;q=0.5");
        request.setHeader("Accept", "*/*,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Cache-Control", "no-cache");
        request.setHeader("Pragma", "no-cache");
        request.setHeader("X-Same-Domain", "1");

        HttpResponse response = client.execute(request);

        if (response.getStatusLine().getStatusCode() != 200) {
            System.out.println("Error en peticion: ");
            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println(response.getStatusLine());
        } else {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                System.out.println("EMPTY RESPONSE");
            } else {

                String responseBody = EntityUtils.toString(entity);
                System.out.println(responseBody);

                JSONObject json_response = new JSONObject(responseBody.substring(11));
                System.out.println();
                System.out.println(json_response);
            }
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private void _addCookie(CookieStore cookieStore, String name, String value) {

        BasicClientCookie c = new BasicClientCookie(name, value);
        c.setPath("/");
        c.setDomain(".google.com");
        cookieStore.addCookie(c);

    }
}
