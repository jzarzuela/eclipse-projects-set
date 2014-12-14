/**
 * 
 */
package gmap.engine.data;

import java.io.PrintWriter;

import org.joda.time.DateTime;

/**
 * @author jzarzuela
 *
 */
public class GLayer extends GAsset {

    // RFC 3339 formatted date-time value (e.g. 1970-01-01T00:00:00Z)
    public DateTime creationTime;
    public DateTime lastModifiedTime;

    public String   name;
    // ----
    public GMap     ownerMap;

    // --- VectorStyle --> estructura compleja
    public GStyle   style;
    public GTable   table = null;

    // --- [d,d,d,d]
    public double[] x_bbox;

    // ---
    public String   x_creatorEmail;
    // ---
    public Object   x_datasources;

    // --- Enum: "table", "image"
    public String   x_datasourceType;

    // ----
    public String   x_desc;

    // ---
    public String   x_draftAccessList;          // Deprecated
    // ---
    public String   x_etag;

    public String   x_lastModifierEmail;
    // --- Enum: "vector", "image"
    public String   x_layerType;

    // --- Enum
    public Object   x_processingStatus;
    public String   x_projectId;

    public String   x_publishedAccessList;

    public Object   x_publishingStatus;

    // --- Tags??
    public String   x_tags;

    // ----
    public boolean  x_writersCanEditPermissions;

    public GLayer(GMap ownerMap, String gid) {
        super(gid);
        if (ownerMap == null) {
            throw new RuntimeException("Layer's ownerMap can't be null");
        }
        this.ownerMap = ownerMap;
        ownerMap.layers.put(gid, this);
    }

    @Override
    protected void printValue(PrintWriter pw, String padding) {

        pw.println(padding + "//===========================================================================================");
        pw.println(padding + "GLayer {");
        pw.println(padding + "  GID: '" + GID + "'");
        pw.println(padding + "  name: '" + name + "'");
        pw.println(padding + "  creationTime: '" + creationTime + "'");
        pw.println(padding + "  lastModifiedTime: '" + lastModifiedTime + "'");

        pw.println();
        pw.println(padding + "  style:");
        style.printValue(pw, padding + "    ");
        pw.println();

        pw.println();
        pw.println(padding + "  table:");
        table.printValue(pw, padding + "    ");
        pw.println();

        pw.println(padding + "}");
    }
}
