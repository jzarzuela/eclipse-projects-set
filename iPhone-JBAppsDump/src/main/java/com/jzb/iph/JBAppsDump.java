/**
 * 
 */
package com.jzb.iph;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.Formatter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.jzb.futil.FileExtFilter;
import com.jzb.futil.FileExtFilter.IncludeFolders;
import com.jzb.util.Tracer;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;

/**
 * @author jzarzuela
 * 
 */
public class JBAppsDump {

    private static final int SOCKET_TIMEOUT = 5000;

    private PrintStream      m_psOut;

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
            JBAppsDump me = new JBAppsDump();
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

        m_psOut = new PrintStream(new File("/Users/jzarzuela/Desktop/ipasInfo.csv"));
        _printHeader();

        File baseFolder = new File("/Users/jzarzuela/Documents/personal/iPhone");
        _processIPAFolder(baseFolder);

        // File ipaFile = new File(baseFolder, "_games/_tN[Tap The Frog]_PK[com.mentals.tapthefroghd]_V[1.5.1]_OS[3.2]_D[2012-07-15].ipa");
        // _processIPA(ipaFile);

        m_psOut.close();
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private void _downloadExtraBundleInfo(BundleInfo bundleInfo) {

        try {
            // HTTPConnection.setProxyServer("127.0.0.1", 9090);
            HTTPConnection.removeDefaultModule(HTTPClient.CookieModule.class);
            HTTPConnection.setDefaultTimeout(SOCKET_TIMEOUT);

            HTTPConnection con = new HTTPConnection(new URL("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/wa/wsLookup?id=380293530&&country=ES"));
            HTTPResponse rsp = con.Get("/WebObjects/MZStoreServices.woa/wa/wsLookup?id=" + bundleInfo.itemId + "&country=ES");

            if (rsp.getStatusCode() != 200) {
                Tracer._error("Response error[%d]: %s", rsp.getStatusCode(), rsp.getReasonLine());
            } else {
                String txtRsp = rsp.getText();
                JSONObject jsonRsp = new JSONObject(txtRsp);
                JSONArray results = jsonRsp.getJSONArray("results");
                if (results.length() <= 0) {
                    bundleInfo.lastBundleVersion = "ERROR";
                    bundleInfo.price = "-00.00";
                } else {
                    JSONObject jsonValue = (JSONObject) results.get(0);
                    bundleInfo.lastBundleVersion = jsonValue.getString("version");
                    bundleInfo.price = jsonValue.getString("price").replace('.', ',');
                }

            }
        } catch (Throwable th) {
            bundleInfo.lastBundleVersion = "ERROR";
            bundleInfo.price = "-00.00";
        }

    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private void _printHeader() throws Exception {

        String header = "folder¬id¬playlistName¬type¬bundleVersion¬price¬lastBundleVersion¬cracked¬wanted¬toBePayed";
        Tracer._debug(header);
        m_psOut.println(header);
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private void _printLine(BundleInfo bi) throws Exception {

        Formatter fmt = new Formatter();
        fmt.format("%s¬%s¬%s¬%s¬%s¬%s¬%s¬%s¬NO¬=IF(INDIRECT(ADDRESS(ROW();COLUMN()-1))=\"SI\"; INDIRECT(ADDRESS(ROW();COLUMN()-4));0)", bi.ipaFile.getParentFile().getName(), bi.itemId,
                bi.playlistName, bi.type, bi.bundleVersion, bi.price, bi.lastBundleVersion, bi.isCracked() ? "SI" : "NO");

        String line = fmt.toString();
        fmt.close();
        Tracer._debug("    " + line);
        m_psOut.println(line);
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private void _processIPAFolder(File folder) throws Exception {

        Tracer._debug("");
        Tracer._debug("----------------------------------------------------------------");
        Tracer._debug("Processing IPA folder: " + folder);
        Tracer._debug("----------------------------------------------------------------");
        Tracer._debug("");

        File fList[] = folder.listFiles(new FileExtFilter(IncludeFolders.YES, "ipa"));

        // Procesamos primero archivos
        for (File f : fList) {
            if (f.isDirectory())
                continue;

            _processIPA(f);
        }

        // Procesamos despues los subdirectorios
        for (File f : fList) {
            if (!f.isDirectory())
                continue;

            _processIPAFolder(f);
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private void _processIPA(File ipaFile) throws Exception {

        BundleInfo bi;

        Tracer._debug("IPAFile = '%s'", ipaFile.getName());
        NSDictionary dict = (NSDictionary) PropertyListParser.parse(ipaFile);
        if (dict == null) {
            bi = new BundleInfo(ipaFile);
        } else {
            bi = new BundleInfo(ipaFile, dict);
        }

        _downloadExtraBundleInfo(bi);

        _printLine(bi);
    }
}
