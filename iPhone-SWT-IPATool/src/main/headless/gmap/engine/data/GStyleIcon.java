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

    private String             m_color;
    private String             m_iconID;
    private String             m_scale;

    // ----------------------------------------------------------------------------------------------------
    public GStyleIcon(String iconID, String color, String scale) {
        m_iconID = iconID;
        m_color = color;
        m_scale = scale;
    }

    // ----------------------------------------------------------------------------------------------------
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

    // ----------------------------------------------------------------------------------------------------
    private static String _getUrl(String iconID) {

        String text = s_iconIds.getProperty(iconID);
        if (text != null) {
            return ICON_BASE_URL_0 + iconID + "-" + text + ".png";
        } else {
            return null;
        }
    }

    // ----------------------------------------------------------------------------------------------------
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

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the color
     */
    public String getColor() {
        return m_color;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the iconID
     */
    public String getIconID() {
        return m_iconID;
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the scale
     */
    public String getScale() {
        return m_scale;
    }

    
    // ----------------------------------------------------------------------------------------------------
    public String getUrl() {

        String text = s_iconIds.getProperty(m_iconID);
        if (text != null) {
            return ICON_BASE_URL_1 + m_iconID + "-" + text + ".png";
        } else {
            text = s_coloredIconIds.getProperty(m_iconID);
            if (text != null) {
                return ICON_BASE_URL_1 + m_iconID + "-#" + m_color + "-" + text + ".png";
            } else {
                return "NO_ICON_URL";
            }
        }
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        m_color = color;
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @param iconID the iconID to set
     */
    public void setIconID(String iconID) {
        m_iconID = iconID;
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @param scale the scale to set
     */
    public void setScale(String scale) {
        m_scale = scale;
    }

    
    // ----------------------------------------------------------------------------------------------------
    @Override
    protected void printValue(PrintWriter pw, String padding) {
        pw.print(padding + "GStyleIcon { iconID: " + m_iconID + ", color: #" + m_color + ", scale: " + m_scale + ", url: " + getUrl() + "}");
    }

}
