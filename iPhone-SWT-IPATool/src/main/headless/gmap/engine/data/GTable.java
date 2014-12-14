/**
 * 
 */
package gmap.engine.data;

import java.io.PrintWriter;
import java.util.LinkedHashMap;

/**
 * @author jzarzuela
 *
 */
public class GTable extends GAsset {

    public LinkedHashMap<String, GFeature> features = new LinkedHashMap<String, GFeature>();
    public GLayer                          ownerLayer;
    public GSchema                         schema;

    public GTable(GLayer ownerLayer, String gid) {
        super(gid);
        if (ownerLayer == null) {
            throw new RuntimeException("Table's ownerLayer can't be null");
        }
        this.ownerLayer = ownerLayer;
        ownerLayer.table = this;
    }

    @Override
    protected void printValue(PrintWriter pw, String padding) {

        pw.println(padding + "GTable {");
        pw.println(padding + "  GID: '" + GID + "'");
        pw.println(padding + "  schema:");
        schema.printValue(pw, padding + "    ");
        pw.println();
        pw.println(padding + "  //---------------------------------------------");
        pw.println(padding + "  features: {");
        for (GFeature feature : features.values()) {
            feature.printValue(pw, padding + "    ");
        }
        pw.println(padding + "  }");
        pw.println(padding + "}");
    }

}
