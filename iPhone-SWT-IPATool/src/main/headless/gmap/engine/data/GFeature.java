/**
 * 
 */
package gmap.engine.data;

import java.io.PrintWriter;
import java.util.HashMap;

/**
 * @author jzarzuela
 *
 */
public class GFeature extends GAsset {

    public GStyleBase              style;

    public GTable                  ownerTable;

    // --- GeoJsonProperties
    public HashMap<String, Object> properties = new HashMap<String, Object>();

    // --- GeoJsonGeometry (union del type)
    public Object                  x_geometry;

    // --- "Point"[lon,lat,{alt}], "MultiPoint"[[lon,lat,{alt}]], "LineString"[[lon,lat,{alt}]],
    // "MultiLineString"[[[lon,lat,{alt}]]], "Polygon"[[[lon,lat,{alt}]]], "MultiPolygon"[[[[lon,lat,{alt}]]]]
    // "GeometryCollection"(object)
    public String                  x_type;

    public GFeature(GTable ownerTable, String gid) {
        super(gid);
        if (ownerTable == null) {
            throw new RuntimeException("Feature's ownerTable can't be null");
        }
        this.ownerTable = ownerTable;
        ownerTable.features.put(gid, this);
    }

    @Override
    protected void printValue(PrintWriter pw, String padding) {

        pw.println(padding + "GFeature {");
        pw.println(padding + "  GID: '" + GID + "'");
        pw.print(padding + "  Style: ");
        if (style != null) {
            style.printValue(pw, "");
        } else {
            pw.println("********** without icon **********");
        }

        for (String key : ownerTable.schema.propertyDefs.keySet()) {
            pw.println(padding + "  '" + key + "' : '" + properties.get(key) + "'");
        }
        pw.println(padding + "}");
    }

}
