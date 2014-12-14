/**
 * 
 */
package gmap.engine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author jzarzuela
 *
 */
public class TestPicker {

    /**
     * 
     */
    public TestPicker() {
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
            TestPicker me = new TestPicker();
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

        String email = "jzarzuela@gmail.com";
        String password = "#webweb1971";
        HashMap<String, BasicClientCookie> googleLoginCookies = __googleUserLogin(email, password);

        JSONObject json_response = __getMapList(googleLoginCookies);
        JSONArray json_docs = json_response.getJSONObject("response").getJSONArray("docs");
        for(int n=0;n<json_docs.length();n++) {
            JSONObject json_doc =json_docs.getJSONObject(n);
            String lastEditedUtc = json_doc.getString("lastEditedUtc");
            DateTime dt = new DateTime(Long.parseLong(lastEditedUtc));
            json_doc.put("lastEditedUtc", dt.toString());
        }
        System.out.println(json_response.toString(2));

        // String mapURL ="https://www.google.com/maps/d/edit?mid=zeLPXIl-X_4c.krZeTaDSPZ40";
        String mapURL = "https://mapsengine.google.com/map/edit?mid=zeLPXIl-X_4c.kAZUmasUiKNQ&authuser=0&hl=en";
        //__getMapData(googleLoginCookies, mapURL);

        // __addMapFeature(googleLoginCookies);
    }

    // ----------------------------------------------------------------------------------------------------
    private void __addMapFeature(HashMap<String, BasicClientCookie> googleLoginCookies) throws Exception {

        // String mapURL = "https://mapsengine.google.com/map/save?cid=mp&cv=4SPgkO0Ya4s.en.&_reqid=339422&rt=j";
        String mapURL = "https://mapsengine.google.com/map/save?cid=mp&cv=xxxxxxxxxxx.en.&_reqid=123456&rt=j";

        CloseableHttpClient httpClient = _createHttpClient();
        HttpClientContext ctx = _createHttpContext();

        HttpPost request = new HttpPost(mapURL);

        _addDefaultHeaders(request);

        _addCookies(ctx, googleLoginCookies);

        ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("f.req", _generateAddMapFeatureText()));
        nvps.add(new BasicNameValuePair("at", "AAX3J7Cg6ErmCKKe76G6AEezpPTB8mMP_A:1417349387242"));
        request.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

        CloseableHttpResponse response = httpClient.execute(request, ctx);

        _checkResponseStatusOK(response);

        String responseBody = _getResponseBodyText(response);
        System.out.println(responseBody);

    }

    // ----------------------------------------------------------------------------------------------------
    private JSONObject __getMapData(HashMap<String, BasicClientCookie> googleLoginCookies, String mapURL) throws Exception {

        CloseableHttpClient httpClient = _createHttpClient();
        HttpClientContext ctx = _createHttpContext();

        URIBuilder builder = new URIBuilder(mapURL);
        builder.setParameter("hl", "en");

        HttpGet request = new HttpGet(builder.build());

        _addDefaultHeaders(request);

        _addCookies(ctx, googleLoginCookies);

        CloseableHttpResponse response = httpClient.execute(request, ctx);

        _checkResponseStatusOK(response);

        _dumpResponse(response);

        return null;
    }

    // ----------------------------------------------------------------------------------------------------
    private JSONObject __getMapList(HashMap<String, BasicClientCookie> googleLoginCookies) throws Exception {

        HashMap<String, String> tokenAndClientUser = _getMapListTokenAndClientUser(googleLoginCookies);
        String token = tokenAndClientUser.get("token");
        String clientUser = tokenAndClientUser.get("clientUser");

        CloseableHttpClient httpClient = _createHttpClient();
        HttpClientContext ctx = _createHttpContext();

        HttpPost request = new HttpPost("https://docs.google.com/picker/pvr?hl=es&hostId=MapsPro");

        _addDefaultHeaders(request);

        _addCookies(ctx, googleLoginCookies);

        ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("start", "0"));
        nvps.add(new BasicNameValuePair("numResults", "99999"));
        nvps.add(new BasicNameValuePair("sort", "3"));
        nvps.add(new BasicNameValuePair("desc", "true"));
        nvps.add(new BasicNameValuePair("cursor", ""));
        nvps.add(new BasicNameValuePair("service", "mapspro"));
        nvps.add(new BasicNameValuePair("type", "owned"));
        nvps.add(new BasicNameValuePair("options", "null"));
        nvps.add(new BasicNameValuePair("token", token));
        nvps.add(new BasicNameValuePair("version", "4"));
        nvps.add(new BasicNameValuePair("subapp", "5"));
        nvps.add(new BasicNameValuePair("app", "2"));
        nvps.add(new BasicNameValuePair("clientUser", clientUser));
        nvps.add(new BasicNameValuePair("authuser", "0"));
        request.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

        CloseableHttpResponse response = httpClient.execute(request, ctx);

        _checkResponseStatusOK(response);

        String responseBody = _getResponseBodyText(response);
        int p2 = responseBody.indexOf("{");
        if (p2 >= 0) {
            JSONObject json_response = new JSONObject(responseBody.substring(p2));
            return json_response;
        } else {
            throw new Exception("No JSON response found");
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private HashMap<String, BasicClientCookie> __googleUserLogin(String email, String password) throws Exception {

        HashMap<String, BasicClientCookie> firstGoogleCookies = _getGoogleAppCookies();
        HashMap<String, BasicClientCookie> loginGoogleCookies = _getGoogleLoginCookies(firstGoogleCookies, email, password);

        HashMap<String, BasicClientCookie> allGoogleCookies = new HashMap<String, BasicClientCookie>();
        allGoogleCookies.putAll(firstGoogleCookies);
        allGoogleCookies.putAll(loginGoogleCookies);
        return allGoogleCookies;
    }

    // ----------------------------------------------------------------------------------------------------
    private void _addCookies(HttpClientContext ctx, HashMap<String, BasicClientCookie> allCookies) {

        for (BasicClientCookie cookie : allCookies.values()) {
            ctx.getCookieStore().addCookie(cookie);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _addDefaultHeaders(HttpRequestBase request) {

        request.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/600.1.25 (KHTML, like Gecko) Version/8.0 Safari/600.1.25");
        request.setHeader("Accept-Language", "en-us");
        request.setHeader("Accept", "*/*,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        // request.setHeader("Cache-Control", "no-cache");
        // request.setHeader("Pragma", "no-cache");
        request.setHeader("X-Same-Domain", "1");
        request.setHeader("If-Modified-Since", DateUtils.formatDate(new Date(), "EEE, d MMM yyyy HH:mm:ss 'GMT'"));
    }

    // ----------------------------------------------------------------------------------------------------
    private void _checkMandatoryCookies(HashMap<String, BasicClientCookie> allCookies, String... cookieNames) throws Exception {

        ArrayList<String> namesNotFound = new ArrayList<String>();
        for (String name : cookieNames) {
            if (!allCookies.containsKey(name)) {
                namesNotFound.add(name);
            }
        }

        if (namesNotFound.size() > 0) {
            System.out.println("--- Missing mandatory cookies in response: " + namesNotFound);
            throw new Exception("Missing mandatory cookies in response: " + namesNotFound);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _checkResponseStatusOK(CloseableHttpResponse response) throws Exception {

        System.out.println("StatusCode : " + response.getStatusLine().getStatusCode());
        System.out.println("StatusLine : " + response.getStatusLine());
        if (response.getStatusLine().getStatusCode() >= 400) {
            System.out.println("Error en peticion (SC >= 400)");
            String bodyText = _getResponseBodyText(response);
            System.out.println(bodyText);
            throw new Exception("Error en peticion (SC >= 400)");
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private CloseableHttpClient _createHttpClient() {

        RequestConfig defaultRequestConfig = RequestConfig.custom() //
                .setSocketTimeout(5000) //
                .setConnectTimeout(5000) //
                .setConnectionRequestTimeout(5000) //
                .setStaleConnectionCheckEnabled(true) //
                .build();

        CloseableHttpClient client = HttpClients.custom() //
                .useSystemProperties() //
                .setDefaultRequestConfig(defaultRequestConfig) //
                // .setProxy(new HttpHost("127.0.0.1", 8888)) //
                // .setProxy(new HttpHost("127.0.0.1", 9090)) //
                .build();

        return client;
    }

    // ----------------------------------------------------------------------------------------------------
    private HttpClientContext _createHttpContext() {

        CookieStore cookieStore = new BasicCookieStore();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        return context;
    }

    // ----------------------------------------------------------------------------------------------------
    private void _dumpResponse(CloseableHttpResponse response) throws Exception {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("--- StatusCode ---");
        pw.println("StatusCode : " + response.getStatusLine().getStatusCode());
        pw.println("StatusLine : " + response.getStatusLine());

        pw.println("--- getAllHeaders ---");
        Header[] readers = response.getAllHeaders();
        for (Header header : readers) {
            pw.println(header.getName() + ":" + header.getValue());
        }

        pw.println("--- body ---");
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            pw.println("EMPTY RESPONSE");
        } else {
            String responseBody = EntityUtils.toString(entity);
            pw.println(responseBody);
        }

        System.out.println(sw.toString());

        /*
         * String fileName = "/Users/jzarzuela/Documents/Java/github-repos/eclipse-projects-set/iPhone-SWT-IPATool/src/main/headless/com/jzb/ipa/req-dump-" + System.currentTimeMillis() + ".txt";
         * FileWriter fw = new FileWriter(fileName); fw.write(sw.toString()); fw.close();
         */

    }

    // ----------------------------------------------------------------------------------------------------
    private HashMap<String, BasicClientCookie> _extractCookies(CloseableHttpResponse response) {

        HashMap<String, BasicClientCookie> allCookies = new HashMap<String, BasicClientCookie>();

        Header[] headers = response.getHeaders("Set-Cookie");
        if (headers != null) {
            for (Header h : headers) {
                List<HttpCookie> httpCookies = HttpCookie.parse(h.getValue());
                for (HttpCookie hc : httpCookies) {
                    BasicClientCookie gc = new BasicClientCookie(hc.getName(), hc.getValue());
                    gc.setPath("/");
                    gc.setDomain(".google.com");
                    allCookies.put(gc.getName(), gc);
                }
            }
        }
        return allCookies;

    }

    // ----------------------------------------------------------------------------------------------------
    private void _generate_AMFT_Layer(StringBuilder sb) {

        String ID_LAYEx = "zeLPXIl-X_4c.ksbz440A_Qdo";
        String ID_LAYER = "zeLPXIl-X_4c.kdDjirERvJeM";

        sb.append('[');

        sb.append('"').append(ID_LAYER).append('"');
        sb.append(',');

        sb.append('[');

        _generate_AMFT_POI(sb, "F4A0059325B934C0", "POI-X-" + System.currentTimeMillis(), "poi desc update");
        boolean firstFeature = false;

        Random rnd = new Random(System.currentTimeMillis());
        for (long n = 0; n < 0; n++) {

            if (!firstFeature) {
                sb.append(',');
            }
            firstFeature = false;

            long lt = ((System.currentTimeMillis() << 20) & 0xFFFFFFFFFFF00000L) + (rnd.nextLong() << 4 & 0x00000000000FFFF0L) + n;
            String poi_id = Long.toHexString(lt).toUpperCase();

            _generate_AMFT_POI(sb, poi_id, "POI-" + n, "poi desc " + n);
        }

        sb.append(']');

        sb.append(']');
    }

    // ----------------------------------------------------------------------------------------------------
    private void _generate_AMFT_Map(StringBuilder sb) {

        String ID_MAP = "zeLPXIl-X_4c.kAZUmasUiKNQ";

        sb.append('[');

        sb.append('"').append(ID_MAP).append('"');
        sb.append(',');

        // EN 'UPDATE' VAN AQUI. EN 'ADD' VAN AL FINAL CON LOS OTROS NULL
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");

        _generate_AMFT_Layer(sb);
        sb.append(',');

        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("[[]]");

        sb.append(']');
    }

    // ----------------------------------------------------------------------------------------------------
    private void _generate_AMFT_POI(StringBuilder sb, String poi_id, String poi_name, String poi_desc) {

        sb.append('[');

        sb.append('"').append(poi_id).append('"');
        sb.append(',');

        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");
        sb.append("null,");

        sb.append('[');

        sb.append("[\"gme_geometry_\", null, null, null, null, null, [[[41.3113276, -3.4494281]], [], []], true],");
        sb.append("[\"nombre\", null, null, null, \"" + poi_name + "\", null, null, true],");
        sb.append("[\"descripción\", null, null, null, \"" + poi_desc + "\", null, null, true],");
        sb.append("[\"icon\", null, null, null,\"icon-value\", null, null, true],");
        sb.append("[\"place_ref\"],");
        sb.append("[\"feature_order\"],");
        sb.append("[\"gx_metafeatureid\"],");
        sb.append("[\"gx_routeinfo\"],");
        sb.append("[\"gx_image_links\"]");

        sb.append(']');

        sb.append(']');
    }

    // ----------------------------------------------------------------------------------------------------
    private String _generateAddMapFeatureText() {

        StringBuilder sb = new StringBuilder();

        _generate_AMFT_Map(sb);

        System.out.println(sb);
        return sb.toString();
    }

    // ----------------------------------------------------------------------------------------------------
    private HashMap<String, BasicClientCookie> _getGoogleAppCookies() throws Exception {

        CloseableHttpClient httpClient = _createHttpClient();
        HttpClientContext ctx = _createHttpContext();

        URIBuilder builder = new URIBuilder("https://accounts.google.com/ServiceLogin");
        builder.setParameter("service", "mapsengine");
        builder.setParameter("passive", "1209600");
        builder.setParameter("continue", "https://mapsengine.google.com/map/splash");
        builder.setParameter("followup", "https://mapsengine.google.com/map/splash");

        HttpGet request = new HttpGet(builder.build());

        _addDefaultHeaders(request);

        CloseableHttpResponse response = httpClient.execute(request, ctx);

        _checkResponseStatusOK(response);

        HashMap<String, BasicClientCookie> allCookies = _extractCookies(response);

        _checkMandatoryCookies(allCookies, "GAPS", "GALX");

        return allCookies;
    }

    // ----------------------------------------------------------------------------------------------------
    private HashMap<String, BasicClientCookie> _getGoogleLoginCookies(HashMap<String, BasicClientCookie> firstGoogleCookies, String email, String password) throws Exception {

        CloseableHttpClient httpClient = _createHttpClient();
        HttpClientContext ctx = _createHttpContext();

        HttpPost request = new HttpPost("https://accounts.google.com/ServiceLoginAuth");

        _addDefaultHeaders(request);

        _addCookies(ctx, firstGoogleCookies);

        ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("GALX", firstGoogleCookies.get("GALX").getValue()));
        nvps.add(new BasicNameValuePair("continue", "https://mapsengine.google.com/map/splash"));
        nvps.add(new BasicNameValuePair("followup", "https://mapsengine.google.com/map/splash"));
        nvps.add(new BasicNameValuePair("service", "mapsengine"));
        nvps.add(new BasicNameValuePair("_utf8", "â"));
        nvps.add(new BasicNameValuePair("bgresponse", ""));
        nvps.add(new BasicNameValuePair("pstMsg", "1"));
        nvps.add(new BasicNameValuePair("dnConn", ""));
        nvps.add(new BasicNameValuePair("checkConnection", ""));
        nvps.add(new BasicNameValuePair("checkedDomains", "youtube"));
        nvps.add(new BasicNameValuePair("Email", email));
        nvps.add(new BasicNameValuePair("Passwd", password));
        nvps.add(new BasicNameValuePair("signIn", "Sign+in"));
        nvps.add(new BasicNameValuePair("PersistentCookie", "yes"));
        nvps.add(new BasicNameValuePair("rmShown", "1"));
        request.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

        CloseableHttpResponse response = httpClient.execute(request, ctx);

        _checkResponseStatusOK(response);

        HashMap<String, BasicClientCookie> allCookies = _extractCookies(response);

        _checkMandatoryCookies(allCookies, "SID", "SSID", "HSID");

        return allCookies;
    }

    // ----------------------------------------------------------------------------------------------------
    private HashMap<String, String> _getMapListTokenAndClientUser(HashMap<String, BasicClientCookie> googleLoginCookies) throws Exception {

        CloseableHttpClient httpClient = _createHttpClient();
        HttpClientContext ctx = _createHttpContext();

        URIBuilder builder = new URIBuilder("https://docs.google.com/picker");
        builder.setParameter("protocol", "gadgets");
        builder.setParameter("origin", "https://mapsengine.google.com");
        builder.setParameter("relayUrl", "https://mapsengine.google.com");
        builder.setParameter("authuser", "0");
        builder.setParameter("hl", "en");
        builder.setParameter("title", "Open+map");
        builder.setParameter("hostId", "MapsPro");
        builder.setParameter("ui", "2");
        builder.setParameter("nav",
                "({root:(null,\"My+Maps\"),items:((\"maps-pro\",\"Created\",{\"type\":\"owned\"}),(\"maps-pro\",\"Shared with me\",{\"type\":\"shared\"})),options:{\"collapsible\":\"expanded\"}})");
        builder.setParameter("rpctoken", "xxxx");
        builder.setParameter("rpcService", "xxxx");
        builder.setParameter("ppli", "2");
        HttpGet request = new HttpGet(builder.build());

        _addDefaultHeaders(request);

        _addCookies(ctx, googleLoginCookies);

        CloseableHttpResponse response = httpClient.execute(request, ctx);

        _checkResponseStatusOK(response);

        String responseBody = _getResponseBodyText(response);

        if (responseBody.indexOf("rawConfig") < 0) {
            throw new Exception("Invalid response: 'rawConfig' info expected");
        }

        String token = null;
        int tokenP1 = responseBody.indexOf("token:'");
        if (tokenP1 >= 0) {
            int tokenP2 = responseBody.indexOf("'", tokenP1 + 7);
            token = responseBody.substring(tokenP1 + 7, tokenP2);
        } else {
            throw new Exception("Invalid response: 'rawConfig:token' info expected");
        }

        String clientUser = null;
        int clientUserP1 = responseBody.indexOf("clientUser:'");
        if (clientUserP1 >= 0) {
            int clientUserP2 = responseBody.indexOf("'", clientUserP1 + 12);
            clientUser = responseBody.substring(clientUserP1 + 12, clientUserP2);
        } else {
            throw new Exception("Invalid response: 'rawConfig:clientUser' info expected");
        }

        HashMap<String, String> values = new HashMap<String, String>();
        values.put("token", token);
        values.put("clientUser", clientUser);

        return values;
    }

    // ----------------------------------------------------------------------------------------------------
    private String _getResponseBodyText(CloseableHttpResponse response) throws Exception {

        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return "";
        } else {
            String responseBody = EntityUtils.toString(entity);
            return responseBody;
        }
    }
}
