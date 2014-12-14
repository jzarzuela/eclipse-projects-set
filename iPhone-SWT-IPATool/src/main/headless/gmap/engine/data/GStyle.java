/**
 * 
 */
package gmap.engine.data;

import java.io.PrintWriter;

/**
 * @author jzarzuela
 *
 */
public class GStyle extends GAsset {

    public GLayer     ownerLayer;
    public String     titlePropName;
    public GStyleIcon defStyleIcon = new GStyleIcon("22", "000000", "1.0");
    public GStyleLine defStyleLine = new GStyleLine("0000FF", "0.5", "5000");

    public GStyle(GLayer ownerLayer, String gid) {
        super(gid);
        if (ownerLayer == null) {
            throw new RuntimeException("Style's ownerLayer can't be null");
        }
        this.ownerLayer = ownerLayer;
        ownerLayer.style = this;
    }

    @Override
    protected void printValue(PrintWriter pw, String padding) {

        pw.println(padding + "GStyle {");
        pw.println(padding + "  GID: '" + GID + "'");
        pw.println(padding + "  titlePropName: '" + titlePropName + "'");
        pw.println(padding + "}");
    }

}
