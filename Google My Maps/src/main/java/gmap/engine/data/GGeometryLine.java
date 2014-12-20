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
public class GGeometryLine extends GGeometry {

    private ArrayList<GGeometryPoint> m_geoPoints = new ArrayList<GGeometryPoint>();

    // ----------------------------------------------------------------------------------------------------
    /**
     * 
     */
    public GGeometryLine() {
    }

    // ----------------------------------------------------------------------------------------------------
    public void addGeoPoint(GGeometryPoint geoPoint) {
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
            GGeometryPoint geoPoint = m_geoPoints.get(n);
            geoPoint.printValue(pw, "");
        }
        pw.print("]");
    }

}
