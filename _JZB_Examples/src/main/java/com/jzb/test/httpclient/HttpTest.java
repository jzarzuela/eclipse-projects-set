package com.jzb.test.httpclient;

import java.net.URL;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import HTTPClient.NVPair;

/**
 * 
 */

/**
 * @author jzarzuela
 * 
 */
public class HttpTest {

    private static final int SOCKET_TIMEOUT = 4000;

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("\n***** EXECUTION STARTED *****\n");
            HttpTest me = new HttpTest();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("\n***** EXECUTION FAILED *****\n");
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

        // Se establece el proxy por defecto para cualquier conexión.
        // Se puede hacer al nivel de cada conexión
        // HTTPConnection.setProxyServer("172.31.219.10",8080);

        // Se añade la información de autenticación básica que pide el proxy
        // No se por qué el "realm" es "a"...
        // AuthorizationInfo.addBasicAuthorization("172.31.219.10",8080,"a","n63636","kk");

        // El HTTPClient tiene un problema con las cookies de Google. Esto no haría falta
        HTTPConnection.removeDefaultModule(HTTPClient.CookieModule.class);

        // Establece un timeout por defecto
        HTTPConnection.setDefaultTimeout(SOCKET_TIMEOUT);

        // Hace la llamada a través del proxy
        HTTPConnection con = new HTTPConnection(new URL("http://www.google.com"));
        HTTPResponse rsp = con.Get("/");

        // Se lee el resultado
        System.out.println(rsp.getStatusCode());
        System.out.println(rsp.getReasonLine());
        System.out.println(rsp.getText());

        // ----------------------------------------------------------------
        // Para hacer una llamada por POST con parametros tendríamos
        NVPair params[] = { new NVPair("param1", "value1") };
        URL apiURL = new URL("http://api.apptrackr.org/");
        HTTPConnection con2 = new HTTPConnection(apiURL);
        // Se puede poner un timeout específico
        con.setTimeout(SOCKET_TIMEOUT);
        HTTPResponse rsp2 = con2.Post("/", params);
        System.out.println(rsp2.getStatusCode());

    }
}
