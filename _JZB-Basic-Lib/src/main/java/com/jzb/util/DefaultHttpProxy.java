/**
 * 
 */
package com.jzb.util;

import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author n63636
 * 
 */
public class DefaultHttpProxy {

    private static AppPreferences s_prefs = new AppPreferences("DefaultHttpProxy");

    public static void storeInfo(String proxyHost, int proxyPort, String user, String pwd, String realm) throws Exception {

        Tracer._debug("Storing connection info for default proxy");

        s_prefs.setPref("ProxyHost", proxyHost);
        s_prefs.setPrefLong("ProxyPort", proxyPort);

        s_prefs.setPref("user", _getEncodedText(user != null ? user : ""));
        s_prefs.setPref("pwd", _getEncodedText(pwd != null ? pwd : ""));
        s_prefs.setPref("realm", realm != null ? realm : "");
        s_prefs.save();
    }

    public static void setDefaultJavaProxy() {
        setDefaultProxy(null);
    }

    public static void setDefaultProxy(DefaultHttpClient httpclient) {

        try {
            Tracer._debug("Checking connection with default proxy");

            s_prefs.load(true);

            String proxyHost = s_prefs.getPref("ProxyHost");
            int proxyPort = (int) s_prefs.getPrefLong("ProxyPort", -1);
            String userName = _getClearText(s_prefs.getPref("user"));
            String userPwd = _getClearText(s_prefs.getPref("pwd"));
            // String realm = s_prefs.getPref("realm");

            if (proxyHost == null || proxyPort == -1) {
                Tracer._error("Preferences file doesn't exist or has invalid data: " + s_prefs.getPrefFile());

                s_prefs.setPref("ProxyHost", s_prefs.getPref("ProxyHost", "unknown"));
                s_prefs.setPref("ProxyPort", s_prefs.getPref("ProxyPort", "-1"));
                s_prefs.setPref("user", s_prefs.getPref("user", "cypheredUserName"));
                s_prefs.setPref("pwd", s_prefs.getPref("pwd", "cypheredUserPwd"));
                s_prefs.setPref("realm", s_prefs.getPref("realm", "a"));
                s_prefs.save();
                return;
            }

            Socket cs = new Socket();
            cs.connect(new InetSocketAddress(proxyHost, proxyPort), 200);
            cs.close();

            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", String.valueOf(proxyPort));
            System.setProperty("https.proxyHost", proxyHost);
            System.setProperty("https.proxyPort", String.valueOf(proxyPort));

            if (httpclient != null) {
                HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(userName, userPwd));
                httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            }

            Tracer._debug("Connection checked!. Going through default proxy (" + proxyHost + ":" + proxyPort + ").");

        } catch (Exception ex) {
            Tracer._warn("Cannot connect with the proxy. Using direct connection...");
        }
    }

    private static String _getClearText(String cad) {

        String msg = "";

        int n = 0;
        while (n < cad.length()) {
            String h = "0x";
            h += cad.charAt(n++);
            h += cad.charAt(n++);
            int i = (Integer.decode(h).intValue() ^ 0x00000055) & 0x000000FF;
            char c = (char) i;
            msg += c;
        }

        return msg;
    }

    private static String _getEncodedText(String cad) {

        String msg = "";
        int n = 0;
        while (n < cad.length()) {
            char c = cad.charAt(n++);
            int i = (((int) c) & 0x000000FF) ^ 0x00000055;
            if (i < 10)
                msg += '0';
            msg += Integer.toHexString(i);
        }
        return msg;

    }

}
