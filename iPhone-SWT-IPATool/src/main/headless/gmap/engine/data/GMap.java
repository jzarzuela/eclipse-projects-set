/**
 * 
 */
package gmap.engine.data;

import java.io.PrintWriter;
import java.util.HashMap;

import org.joda.time.DateTime;

/**
 * @author jzarzuela
 *
 */
public class GMap extends GAsset {

    // RFC 3339 formatted date-time value (e.g. 1970-01-01T00:00:00Z)
    public DateTime                creationTime;                  

    public String                  desc;
    public DateTime                lastModifiedTime;

    // Features
    public HashMap<String, GLayer> layers = new HashMap<>();
    // ---
    public String                  name;

    // --- [d,d,d,d]
    public double[]                x_bbox;

    // --- MapContents???
    public Object                  x_contents;
    // ---
    public String                  x_creatorEmail;

    // --- LatLngBox???
    public Object                  x_defaultViewport;          // array of four numbers (west, south, east, north)

    // ---
    public String                  x_draftAccessList;          // Deprecated

    // ---
    public String                  x_etag;

    public String                  x_lastModifierEmail;

    // --- Enum
    public Object                  x_processingStatus;

    public String                  x_projectId;

    public String                  x_publishedAccessList;
    public Object                  x_publishingStatus;

    // --- Tags??
    public String                  x_tags;
    // --- published??? --> Deprecated
    public Object[]                x_versions;

    // ----
    public boolean                 x_writersCanEditPermissions;
    // Hace falta para sincronizar ¿¿como se añade un mapa desde cero?
    public String                  xsrfToken;

    public GMap(String gid) {
        super(gid);
    }

    @Override
    protected void printValue(PrintWriter pw, String padding) {

        pw.println(padding + "GMap {");
        pw.println(padding + "  GID: '" + GID + "'");
        pw.println(padding + "  name: '" + name + "'");
        pw.println(padding + "  desc: '" + desc + "'");
        pw.println(padding + "  creationTime: '" + creationTime + "'");
        pw.println(padding + "  lastModifiedTime: '" + lastModifiedTime + "'");

        pw.println();
        pw.println(padding + "  layers: {");
        for (GLayer layer : layers.values()) {
            pw.println();
            layer.printValue(pw, padding + "    ");
        }
        pw.println(padding + "  }");
        pw.println();
        pw.println(padding + "}");
    }
}
