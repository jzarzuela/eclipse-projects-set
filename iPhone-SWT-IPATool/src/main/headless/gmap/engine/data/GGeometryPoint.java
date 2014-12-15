/**
 * 
 */
package gmap.engine.data;

import java.io.PrintWriter;

/**
 * @author jzarzuela
 *
 */
public class GGeometryPoint extends GGeometry {

    private double m_lat;
    private double m_lon;

    // ----------------------------------------------------------------------------------------------------
    /**
     * 
     */
    public GGeometryPoint(double lon, double lat) {
        m_lon = lon;
        m_lat = lat;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the lat
     */
    public double getLat() {
        return m_lat;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the lon
     */
    public double getLon() {
        return m_lon;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @see gmap.engine.data.GGeometry#printValue(java.io.PrintWriter, java.lang.String)
     */
    @Override
    protected void printValue(PrintWriter pw, String padding) {
        pw.print("[" + m_lon + ", " + m_lat + "]");
    }

}
