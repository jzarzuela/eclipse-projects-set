/**
 * 
 */
package gmap.engine;

import gmap.engine.data.GMap;
import gmap.engine.parser.GMapServiceParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 *
 */
public class GMapService {

    private HashMap<String, Cookie> m_googleLoginCookies;

    // ----------------------------------------------------------------------------------------------------
    /**
     * 
     */
    public GMapService() {
    }

    // ----------------------------------------------------------------------------------------------------
    public GMap getMapData(String mapID) throws GMapException {

        try {

            // Se debe estar logado para poder pedir la lista de URLs de mapas
            _checkLoginCookies();

            // Consigue la informacion de los mapas

            JSONObject json_response = _readMapData(mapID);
            if (json_response == null) {
                json_response = _fetchMapData(mapID, m_googleLoginCookies);
                _saveMapData(mapID, json_response);
            }

            String mapdataJsonStr = json_response.getString("mapdataJson");
            GMap map = GMapServiceParser.parseMapDataJson(mapdataJsonStr);

            map.xsrfToken = json_response.getString("xsrfToken");

            return map;

        } catch (GMapException ex) {
            throw ex;
        } catch (Throwable th) {
            throw new GMapException("Error getting data for map ID: " + mapID, th);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private void _saveMapData(String mapID, JSONObject json_response) {

        File mapDataFile = new File(System.getProperty("user.home") + "/gmap/map_json_data/" + mapID + ".txt");
        mapDataFile.getParentFile().mkdirs();

        try (PrintWriter pw = new PrintWriter(mapDataFile)) {

            pw.println(json_response.toString(2));

        } catch (Throwable th) {
            Tracer._warn("Error saving file with cached map json response", th);
            mapDataFile.delete();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private JSONObject _readMapData(String mapID) {

        File mapDataFile = new File(System.getProperty("user.home") + "/gmap/map_json_data/" + mapID + ".txt");
        if (!mapDataFile.exists()) {
            Tracer._warn("Cached map json data file doesn't exist: "+mapID);
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(mapDataFile))) {

            StringBuilder sb = new StringBuilder();
            for (;;) {
                String line = br.readLine();
                if (line == null)
                    break;
                sb.append(line);
            }

            JSONObject json_response = new JSONObject(sb.toString());
            return json_response;

        } catch (Throwable th) {
            // Si algo pasa borra lo que tuviese
            Tracer._warn("Error reading file with cached map json response", th);
            mapDataFile.delete();
            return null;
        }
    }

    // ----------------------------------------------------------------------------------------------------
    public ArrayList<UserMapData> getUserMapList() throws GMapException {

        try {

            // Se debe estar logado para poder pedir la lista de URLs de mapas
            _checkLoginCookies();

            // Consigue la informacion de los mapas
            JSONArray json_allMaps = _fetchMapUrlList(m_googleLoginCookies);

            ArrayList<UserMapData> allMaps = new ArrayList<UserMapData>();
            for (int n = 0; n < json_allMaps.length(); n++) {
                JSONObject json_map = json_allMaps.getJSONObject(n);
                UserMapData umd = new UserMapData(json_map);
                allMaps.add(umd);
            }

            return allMaps;

        } catch (GMapException ex) {
            throw ex;
        } catch (Throwable th) {
            throw new GMapException("Error getting user map list", th);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    public void login(String email, String password) throws GMapException {

        try {

            Tracer._debug("GMapService::Login - " + email);

            // Logarse es conseguir TODAS las cookies que haran falta en sucesivas llamadas
            m_googleLoginCookies = readCookieJar(email);
            if (m_googleLoginCookies != null) {

                Tracer._debug("GoogleUserLogin cookies read from cookieJar file");

            } else {

                HashMap<String, Cookie> firstGoogleCookies = _fetchGoogleAppCookies();
                HashMap<String, Cookie> loginGoogleCookies = _fetchGoogleLoginCookies(firstGoogleCookies, email, password);

                m_googleLoginCookies = new HashMap<String, Cookie>();
                m_googleLoginCookies.putAll(firstGoogleCookies);
                m_googleLoginCookies.putAll(loginGoogleCookies);

                persistCookieJar(email, m_googleLoginCookies);

            }

        } catch (GMapException ex) {
            throw ex;
        } catch (Throwable th) {
            throw new GMapException("Error login user: " + email, th);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    public void logout() throws GMapException {

        // Salir consiste en borrar TODAS las cookies
        m_googleLoginCookies = null;
    }

    // ----------------------------------------------------------------------------------------------------
    private void _checkLoginCookies() throws Exception {

        if (m_googleLoginCookies == null) {
            throw new GMapException("Not logged in");
        }

        for (Cookie cookie : m_googleLoginCookies.values()) {
            long now = System.currentTimeMillis();
            Date expiry = cookie.getExpiryDate();
            if (expiry != null && expiry.getTime() < now) {
                throw new GMapException("Session has expired. Need to log in again.");
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private HashMap<String, Cookie> _fetchGoogleAppCookies() throws Exception {

        Tracer._debug("GMapService::GetGoogleAppCookies");

        CloseableHttpClient httpClient = createHttpClient();
        HttpClientContext ctx = createHttpContext();

        URIBuilder builder = new URIBuilder("https://accounts.google.com/ServiceLogin");
        builder.setParameter("service", "mapsengine");
        builder.setParameter("passive", "1209600");
        builder.setParameter("continue", "https://mapsengine.google.com/map/splash");
        builder.setParameter("followup", "https://mapsengine.google.com/map/splash");

        HttpGet request = new HttpGet(builder.build());

        addDefaultHeaders(request);

        CloseableHttpResponse response = httpClient.execute(request, ctx);

        checkResponseStatusOK(response);

        HashMap<String, Cookie> allCookies = extractCookies(response);

        checkMandatoryCookies(allCookies, "GAPS", "GALX");

        return allCookies;
    }

    // ----------------------------------------------------------------------------------------------------
    private HashMap<String, Cookie> _fetchGoogleLoginCookies(HashMap<String, Cookie> firstGoogleCookies, String email, String password) throws Exception {

        Tracer._debug("GMapService::GetGoogleLoginCookies");

        CloseableHttpClient httpClient = createHttpClient();
        HttpClientContext ctx = createHttpContext();

        HttpPost request = new HttpPost("https://accounts.google.com/ServiceLoginAuth");

        addDefaultHeaders(request);

        addCookies(ctx, firstGoogleCookies);

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

        checkResponseStatusOK(response);

        HashMap<String, Cookie> allCookies = extractCookies(response);

        checkMandatoryCookies(allCookies, "SID", "SSID", "HSID");

        return allCookies;
    }

    // ----------------------------------------------------------------------------------------------------
    private JSONObject _fetchMapData(String mapID, HashMap<String, Cookie> googleLoginCookies) throws Exception {

        CloseableHttpClient httpClient = createHttpClient();
        HttpClientContext ctx = createHttpContext();

        URIBuilder builder = new URIBuilder("https://www.google.com/maps/d/edit");
        builder.setParameter("mid", mapID);
        builder.setParameter("authuser", "0");
        builder.setParameter("hl", "en");

        HttpGet request = new HttpGet(builder.build());

        addDefaultHeaders(request);

        addCookies(ctx, googleLoginCookies);

        CloseableHttpResponse response = httpClient.execute(request, ctx);

        checkResponseStatusOK(response);

        String responseBody = getResponseBodyText(response);

        int p1 = responseBody.indexOf("var _pageData = {");
        if (p1 < 0) {
            throw new GMapException("Invalid JSON response found");
        } else {

            int p2 = responseBody.indexOf("};</script>", p1);
            if (p2 < 0) {
                throw new GMapException("Invalid JSON response found");
            }

            JSONObject json_response = new JSONObject(responseBody.substring(p1 + 16, p2 + 2));

            if (json_response.getBoolean("isPartialResult")) {
                throw new GMapException("Invalid JSON response found: isPartialResult=true");
            }

            // System.out.println(json_response.toString(2));

            String fieldsToCheck[] = { "mapdataJson", "userToken", "xsrfToken" };
            for (String field : fieldsToCheck) {
                if (json_response.opt(field) == null) {
                    throw new GMapException("Invalid JSON response found: " + field + "=null");
                }
            }

            return json_response;
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private HashMap<String, String> _fetchMapListTokenAndClientUser(HashMap<String, Cookie> googleLoginCookies) throws Exception {

        CloseableHttpClient httpClient = createHttpClient();
        HttpClientContext ctx = createHttpContext();

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

        addDefaultHeaders(request);

        addCookies(ctx, googleLoginCookies);

        CloseableHttpResponse response = httpClient.execute(request, ctx);

        checkResponseStatusOK(response);

        String responseBody = getResponseBodyText(response);

        if (responseBody.indexOf("rawConfig") < 0) {
            throw new GMapException("Invalid response: 'rawConfig' info expected");
        }

        String token = null;
        int tokenP1 = responseBody.indexOf("token:'");
        if (tokenP1 >= 0) {
            int tokenP2 = responseBody.indexOf("'", tokenP1 + 7);
            token = responseBody.substring(tokenP1 + 7, tokenP2);
        } else {
            throw new GMapException("Invalid response: 'rawConfig:token' info expected");
        }

        String clientUser = null;
        int clientUserP1 = responseBody.indexOf("clientUser:'");
        if (clientUserP1 >= 0) {
            int clientUserP2 = responseBody.indexOf("'", clientUserP1 + 12);
            clientUser = responseBody.substring(clientUserP1 + 12, clientUserP2);
        } else {
            throw new GMapException("Invalid response: 'rawConfig:clientUser' info expected");
        }

        HashMap<String, String> values = new HashMap<String, String>();
        values.put("token", token);
        values.put("clientUser", clientUser);

        return values;
    }

    // ----------------------------------------------------------------------------------------------------
    private JSONArray _fetchMapUrlList(HashMap<String, Cookie> googleLoginCookies) throws Exception {

        // Se necesita cierta informacion de la pagina previa para pedir la lista de documentos
        HashMap<String, String> tokenAndClientUser = _fetchMapListTokenAndClientUser(googleLoginCookies);
        String token = tokenAndClientUser.get("token");
        String clientUser = tokenAndClientUser.get("clientUser");

        CloseableHttpClient httpClient = createHttpClient();
        HttpClientContext ctx = createHttpContext();

        HttpPost request = new HttpPost("https://docs.google.com/picker/pvr?hl=es&hostId=MapsPro");

        addDefaultHeaders(request);

        addCookies(ctx, googleLoginCookies);

        ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("start", "0"));
        nvps.add(new BasicNameValuePair("numResults", "999999"));
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

        checkResponseStatusOK(response);

        String responseBody = getResponseBodyText(response);
        int p2 = responseBody.indexOf("{");
        if (p2 < 0) {
            throw new GMapException("Invalid JSON response found");
        } else {
            JSONObject json_response = new JSONObject(responseBody.substring(p2));
            json_response = json_response.optJSONObject("response");
            if (json_response == null || !json_response.optBoolean("success", false) || json_response.optJSONArray("docs") == null) {
                throw new GMapException("Invalid JSON response found");
            }

            return json_response.getJSONArray("docs");
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private void addCookies(HttpClientContext ctx, HashMap<String, Cookie> allCookies) {

        for (Cookie cookie : allCookies.values()) {
            ctx.getCookieStore().addCookie(cookie);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void addDefaultHeaders(HttpRequestBase request) {

        request.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/600.1.25 (KHTML, like Gecko) Version/8.0 Safari/600.1.25");
        request.setHeader("Accept-Language", "en-us");
        request.setHeader("Accept", "*/*,text/html,application/xhtml+xml,application/xml,application/vnd.google.map;q=0.9,*/*;q=0.8");
        request.setHeader("Cache-Control", "no-cache");
        request.setHeader("Pragma", "no-cache");
        request.setHeader("X-Same-Domain", "1");
    }

    // ----------------------------------------------------------------------------------------------------
    private void checkMandatoryCookies(HashMap<String, Cookie> allCookies, String... cookieNames) throws Exception {

        ArrayList<String> namesNotFound = new ArrayList<String>();
        for (String name : cookieNames) {
            if (!allCookies.containsKey(name)) {
                namesNotFound.add(name);
            }
        }

        if (namesNotFound.size() > 0) {
            throw new GMapException("Missing mandatory cookies in response: " + namesNotFound);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void checkResponseStatusOK(CloseableHttpResponse response) throws Exception {

        int sc = response.getStatusLine().getStatusCode();

        // 429 es un error indicando que se han realizado demasiadas peticiones seguidas
        if (sc == 429) {
            throw new GMapExceptionCode429("Too many request in time interval");
        } else if (sc >= 400) {
            String errorMsg = "Error: Response StatusCode : " + response.getStatusLine().getStatusCode() + " / StatusLine : " + response.getStatusLine();
            Tracer._error(errorMsg);

            dumpResponseBodyText(response);

            throw new GMapException(errorMsg);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private CloseableHttpClient createHttpClient() {

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
    private HttpClientContext createHttpContext() {

        CookieStore cookieStore = new BasicCookieStore();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        return context;
    }

    // ----------------------------------------------------------------------------------------------------
    private void dumpResponseBodyText(CloseableHttpResponse response) throws Exception {
        String bodyText = getResponseBodyText(response);
        Tracer._debug(bodyText);
    }

    // ----------------------------------------------------------------------------------------------------
    private HashMap<String, Cookie> extractCookies(CloseableHttpResponse response) throws Exception {

        HashMap<String, Cookie> allCookies = new HashMap<String, Cookie>();

        BrowserCompatSpec cookieParser = new BrowserCompatSpec();
        CookieOrigin origin = new CookieOrigin(".google.com", 80, "/", false);

        Header[] headers = response.getHeaders("Set-Cookie");
        if (headers != null) {
            for (Header h : headers) {
                List<Cookie> cookies = cookieParser.parse(h, origin);
                for (Cookie gc : cookies) {
                    allCookies.put(gc.getName(), gc);
                }
            }
        }

        return allCookies;

    }

    // ----------------------------------------------------------------------------------------------------
    private String getResponseBodyText(CloseableHttpResponse response) throws Exception {

        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return "";
        } else {
            String responseBody = EntityUtils.toString(entity);
            return responseBody;
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void persistCookieJar(String email, HashMap<String, Cookie> googleLoginCookies) {

        File cookieJar = new File(System.getProperty("user.home") + "/gmap/cookieJar.txt");
        cookieJar.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(cookieJar)) {

            // Primero escribe el email al que pertenecen las cookies
            pw.println(email);

            // Luego almacena todas las cookies
            for (Cookie cookie : googleLoginCookies.values()) {

                StringBuilder sb = new StringBuilder();
                sb.append("name=").append(cookie.getName()).append(";");
                sb.append("value=").append(cookie.getValue()).append(";");
                sb.append("path=").append(cookie.getPath()).append(";");
                sb.append("domain=").append(cookie.getDomain()).append(";");
                sb.append("expiry=").append(cookie.getExpiryDate() != null ? cookie.getExpiryDate().getTime() : -1).append(";");

                pw.println(sb.toString());
            }

        } catch (Throwable th) {
            Tracer._warn("Error persisting cookieJar", th);
            cookieJar.delete();
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private HashMap<String, Cookie> readCookieJar(String email) {

        File cookieJar = new File(System.getProperty("user.home") + "/gmap/cookieJar.txt");
        if (!cookieJar.exists()) {
            Tracer._warn("CookieJar file doesn't exist.");
            return null;
        }

        HashMap<String, Cookie> googleLoginCookies = new HashMap<String, Cookie>();

        try (BufferedReader br = new BufferedReader(new FileReader(cookieJar))) {

            // Comprueba el email al que pertenecen esas cookies
            String line = br.readLine();
            if (line == null || !line.equalsIgnoreCase(email)) {
                throw new GMapException("Login email doesn't match CookieJar's email");
            }

            // Lee todas las cookies
            for (;;) {
                line = br.readLine();
                if (line == null)
                    break;

                HashMap<String, String> attributes = new HashMap<String, String>();
                for (String pnv : line.split(";")) {
                    int p = pnv.indexOf('=');
                    if (p <= 0) {
                        throw new GMapException("Error cookieJar already expired");
                    }
                    attributes.put(pnv.substring(0, p), pnv.substring(p + 1));
                }

                long expiry = Long.parseLong(attributes.get("expiry"));
                if (expiry != -1 && expiry <= System.currentTimeMillis()) {
                    throw new GMapException("Error cookieJar already expired");
                }

                BasicClientCookie cookie = new BasicClientCookie(attributes.get("name"), attributes.get("value"));
                cookie.setDomain(attributes.get("domain"));
                cookie.setPath(attributes.get("path"));
                cookie.setExpiryDate(expiry > 0 ? new Date(expiry) : null);

                googleLoginCookies.put(cookie.getName(), cookie);

            }

            checkMandatoryCookies(googleLoginCookies, "GAPS", "GALX", "SID", "SSID", "HSID");

            return googleLoginCookies;

        } catch (Throwable th) {
            // Si algo pasa borra lo que tuviese
            Tracer._warn("Error reading cookieJar", th);
            cookieJar.delete();
            return null;
        }

    }
}
