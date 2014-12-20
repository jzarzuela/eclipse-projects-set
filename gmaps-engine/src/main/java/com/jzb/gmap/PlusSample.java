/**
 * 
 */
package com.jzb.gmap;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin.Response;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.plus.model.Person;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Yaniv Inbar
 */
public class PlusSample {

    /** Global instance of the HTTP transport. */
    private static final HttpTransport  HTTP_TRANSPORT   = new NetHttpTransport();

    public static final String          PLUS_ME          = "https://www.googleapis.com/auth/plus.me";

    public static final String          SCOPE_GMAP       = "https://www.googleapis.com/auth/mapsengine";
    public static final String          SCOPE_FTABLES    = "https://www.googleapis.com/auth/fusiontables";

    /**
     * Be sure to specify the name of your application. If the application name is {@code null} or blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
     */
    private static final String         APPLICATION_NAME = "iTravelPOIs";

    /** Directory to store user credentials. */
    private static final java.io.File   DATA_STORE_DIR   = new java.io.File(System.getProperty("user.home"), ".store/plus_sample");

    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single globally shared instance across your application.
     */
    private static FileDataStoreFactory dataStoreFactory;

    /** Global instance of the HTTP transport. */
    private static HttpTransport        httpTransport;

    /** Global instance of the JSON factory. */
    private static final JsonFactory    JSON_FACTORY     = JacksonFactory.getDefaultInstance();

    /** Authorizes the installed application to access user's protected data. */
    private static Credential authorize() throws Exception {
        // load client secrets
        String clientId = "264180420569-hpjqb1bvmq3ncoo3ns3e35j8eot662b9.apps.googleusercontent.com";
        String clientSecret = "xhPBGaWKtGY286-tRS64rpfu";
        // set up authorization code flow

        GoogleAuthorizationCodeFlow flow = //
        new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, //
                clientId, clientSecret, Arrays.asList(PLUS_ME, SCOPE_GMAP, SCOPE_FTABLES)).setDataStoreFactory(dataStoreFactory).build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static void main(String[] args) {
        try {
            
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
            // authorization
            Credential credential = authorize();
            System.out.println(credential);
             _getMaps(credential);

            // set up global Plus instance
            Plus plus = new Plus.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

            Person person = plus.people().get("me").execute();

            System.out.println("id: " + person.getId());
            System.out.println("name: " + person.getDisplayName());
            System.out.println("image url: " + person.getImage().getUrl());
            System.out.println("profile url: " + person.getUrl());

            return;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(1);
    }

    /*
    private static void _getMaps(Credential credential) {

        HttpTransport transport = GoogleTransport.create();
        transport.addParser(new JsonCParser());
        try { // authenticate with ClientLogin ClientLogin authenticator = new ClientLogin();
            authenticator.authTokenType = "ndev";
            authenticator.username = "...";
            authenticator.password = "...";
            authenticator.authenticate().setAuthorizationHeader(transport); // make query request
            HttpRequest request = transport.buildGetRequest();
            request.setUrl("https://www.googleapis.com/bigquery/v1/query");
            request.url.put("q", "select count(*) from [bigquery/samples/shakespeare];");
            System.out.println(request.execute().parseAsString());
        } catch (HttpResponseException e) {
            System.err.println(e.response.parseAsString());
            throw e;
        }

    }
    */


    private static String _kk3(String cad) {
        
        StringBuffer sb = new StringBuffer();
        for(int n=0;n<cad.length();n++) {
            char c = cad.charAt(n);
            c -= '0';
            sb.append(c);
        }
        return sb.toString();
    }
    
    private static String _kk4(String cad) {
        
        StringBuffer sb = new StringBuffer();
        for(int n=0;n<cad.length();n++) {
            char c = cad.charAt(n);
            c -= 'd';
            sb.append(c);
        }
        return sb.toString();
    }
    
    
    private static void _getMaps(Credential credential) {

        HttpURLConnection connection = null;
        try {
            ClientLogin cl = new ClientLogin();
            cl.transport = httpTransport;
            cl.accountType = "ClientLogin";

            cl.accountType = "HOSTED_OR_GOOGLE";
            cl.applicationName = "iTravelPOIs";
            cl.username = _kk4("ÎÞÅÖÞÙÉÐÅ¤ËÑÅÍÐÇÓÑ");
            cl.password = _kk3("S§§aiga");
            cl.authTokenType = "local";

            Response r = cl.authenticate();
            System.out.println(r);

            // Make a request to list all maps in a particular project.
            URL url = new URL("https://www.googleapis.com/mapsengine/v1/maps/zsgh0tp2Sarc.kBQbusypxkCU");
            URL url2 = new URL("https://www.googleapis.com/mapsengine/v1/tables");

            GenericUrl u = new GenericUrl(url);
            HttpRequest req = httpTransport.createRequestFactory().buildGetRequest(u);

            // r.initialize(req);
            // req.setInterceptor(credential.getClientAuthentication());

            // HttpResponse resp = req.execute();
            // System.out.println(resp);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
             connection.setRequestProperty("Authorization", "Bearer " + credential.getAccessToken());
            //connection.setRequestProperty("Authorization", r.getAuthorizationHeaderValue());

            connection.connect();
            InputStream is = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(connection);
        }
        System.exit(1);
    }

}
