/**
 * 
 */
package gmap.engine.data;

import gmap.engine.GMapException;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

/**
 * @author jzarzuela
 *
 */
public class GStyleIcon extends GStyleBase {

    public static final String ICON_BASE_URL_0  = "http://mt.googleapis.com/vt/icon/name=icons/onion/";
    public static final String ICON_BASE_URL_1  = "http://www.gstatic.com/mapspro/images/stock/";
    private static Properties  s_coloredIconIds = new Properties();

    private static Properties  s_iconIds        = new Properties();
    static {
        _init_icon_ids();
    }

    public String              color;
    public String              id;
    public String              scale;

    public GStyleIcon(String id, String color, String scale) {
        this.id = id;
        this.color = color;
        this.scale = scale;
    }

    public static void iconsHtml() throws GMapException {

        try (PrintWriter pw = new PrintWriter(new FileWriter("/Users/jzarzuela/Documents/Java/github-repos/eclipse-projects-set/iPhone-SWT-IPATool/src/main/headless/gmap/engine/data/icons.html"))) {

            pw.println("<html>");
            pw.println("  <head>");
            pw.println("    <title>GMaps Icons</title>");
            pw.println("  </head>");
            pw.println("  <body>");
            pw.println("    GMaps Icons:<br>");
            pw.println("    <table");

            ArrayList<String> all_ids = new ArrayList<String>();
            for (Object key : s_iconIds.keySet()) {
                String id = (String) key;
                switch (id.length()) {
                    case 1:
                        id = "   " + id;
                        break;
                    case 2:
                        id = "  " + id;
                        break;
                    case 3:
                        id = " " + id;
                        break;
                }
                all_ids.add(id);
            }
            Collections.sort(all_ids);

            for (String id : all_ids) {
                pw.println("      <tr>");
                pw.print("        <td>" + id + "</td><td>" + s_iconIds.getProperty(id.trim()) + "</td>");
                String src = _getUrl(id.trim());
                if (src != null) {
                    pw.println("<td><img src=\"" + src + "\"></td>");
                } else {
                    pw.println("<td>NO-ICON</td>");
                }
                pw.println("      <tr>");
            }

            pw.println("    </table>");
            pw.println("  </body>");
            pw.println("</html>");
        } catch (Throwable th) {
            throw new GMapException("Error generating icons Html", th);
        }

    }

    private static String _getUrl(String iconID) {

        String text = s_iconIds.getProperty(iconID);
        if (text != null) {
            return ICON_BASE_URL_0 + iconID + "-" + text + ".png";
        } else {
            return null;
        }
    }

    private static void _init_icon_ids() {

        try (InputStream is = GStyleIcon.class.getResourceAsStream("icon_ids.properties")) {
            s_iconIds.load(is);
        } catch (Throwable th) {
            throw new RuntimeException("Error reading icon IDs", th);
        }

        try (InputStream is = GStyleIcon.class.getResourceAsStream("colored_icon_ids.properties")) {
            s_coloredIconIds.load(is);
        } catch (Throwable th) {
            throw new RuntimeException("Error reading icon IDs", th);
        }

    }

    public String getUrl() {

        String text = s_iconIds.getProperty(id);
        if (text != null) {
            return ICON_BASE_URL_1 + id + "-" + text + ".png";
        } else {
            text = s_coloredIconIds.getProperty(id);
            if (text != null) {
                return ICON_BASE_URL_1 + id + "-#" + color + "-" + text + ".png";
            } else {
                return "NO_ICON_URL";
            }
        }
    }

    protected void printValue(PrintWriter pw, String padding) {
        pw.println(padding + "GStyleIcon { id:" + id + ", color:" + color + ", scale:" + scale + ", url: " + getUrl() + "}");
    }

}
