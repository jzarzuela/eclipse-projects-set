/**
 * 
 */
package gmap.engine.data;

import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * @author jzarzuela
 *
 */
public class GGeoLine extends GGeometry {

    private ArrayList<GGeoPoint> m_geoPoints = new ArrayList<GGeoPoint>();

    // ----------------------------------------------------------------------------------------------------
    /**
     * 
     */
    public GGeoLine() {
    }

    // ----------------------------------------------------------------------------------------------------
    public void addGeoPoint(GGeoPoint geoPoint) {
        m_geoPoints.add(geoPoint);
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @see gmap.engine.data.GGeometry#printValue(java.io.PrintWriter, java.lang.String)
     */
    @Override
    protected void printValue(PrintWriter pw, String padding) {
        pw.print("[");
        for (int n = 0; n < m_geoPoints.size(); n++) {
            if (n > 0) {
                pw.print(", ");
            }
            GGeoPoint geoPoint = m_geoPoints.get(n);
            geoPoint.printValue(pw, "");
        }
        pw.print("]");
    }

}
