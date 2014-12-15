/**
 * 
 */
package gmap.engine.data;

import gmap.engine.GMapException;
import gmap.engine.data.GAsset;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;

/**
 * @author jzarzuela
 *
 */
public class GMap extends GAsset {

    // RFC 3339 formatted date-time value (e.g. 1970-01-01T00:00:00Z)
    private DateTime          m_creationTime;
    private String            m_desc;
    private DateTime          m_lastModifiedTime;
    private ArrayList<GLayer> m_layers = new ArrayList<>();
    private String            m_name;
    // Hace falta para sincronizar ¿¿como se añade un mapa desde cero?
    private String            m_xsrfToken;

    // ----------------------------------------------------------------------------------------------------
    public GMap(String gid) {
        super(gid);
    }

    // ----------------------------------------------------------------------------------------------------
    public GLayer addLayer(String layer_gid) {

        GLayer layer = new GLayer(this, layer_gid);
        m_layers.add(layer);
        return layer;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the creationTime
     */
    public DateTime getCreationTime() {
        return m_creationTime;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the desc
     */
    public String getDesc() {
        return m_desc;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the lastModifiedTime
     */
    public DateTime getLastModifiedTime() {
        return m_lastModifiedTime;
    }

    // ----------------------------------------------------------------------------------------------------
    public GLayer getLayerForStyleID(String style_gid) throws GMapException {

        for (GLayer layer : m_layers) {
            if (style_gid != null && style_gid.equals(layer.getStyleID())) {
                return layer;
            }
        }

        throw new GMapException("Can't find layer for style_gid: " + style_gid);
    }

    // ----------------------------------------------------------------------------------------------------
    public GLayer getLayerForTableID(String table_gid) throws GMapException {

        for (GLayer layer : m_layers) {
            if (table_gid != null && table_gid.equals(layer.getTableID())) {
                return layer;
            }
        }

        throw new GMapException("Can't find layer for table_id: " + table_gid);
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the layers
     */
    public List<GLayer> getLayers() {
        return Collections.unmodifiableList(m_layers);
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the xsrfToken
     */
    public String getXsrfToken() {
        return m_xsrfToken;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param creationTime
     *            the creationTime to set
     */
    public void setCreationTime(DateTime creationTime) {
        m_creationTime = creationTime;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param creationTime
     *            the creationTime to set
     */
    public void setCreationTime(long creationTime) {
        m_creationTime = new DateTime(creationTime);
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param desc
     *            the desc to set
     */
    public void setDesc(String desc) {
        m_desc = desc;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param lastModifiedTime
     *            the lastModifiedTime to set
     */
    public void setLastModifiedTime(DateTime lastModifiedTime) {
        m_lastModifiedTime = lastModifiedTime;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param lastModifiedTime
     *            the lastModifiedTime to set
     */
    public void setLastModifiedTime(long lastModifiedTime) {
        m_lastModifiedTime = new DateTime(lastModifiedTime);
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        m_name = name;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param xsrfToken
     *            the xsrfToken to set
     */
    public void setXsrfToken(String xsrfToken) {
        m_xsrfToken = xsrfToken;
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    protected void printValue(PrintWriter pw, String padding) {

        pw.println(padding + "GMap {");
        pw.println(padding + "  GID: '" + getGID() + "'");
        pw.println(padding + "  name: '" + m_name + "'");
        pw.println(padding + "  desc: '" + m_desc + "'");
        pw.println(padding + "  creationTime: '" + m_creationTime + "'");
        pw.println(padding + "  lastModifiedTime: '" + m_lastModifiedTime + "'");

        pw.println();
        pw.println(padding + "  layers: {");
        for (GLayer layer : m_layers) {
            pw.println();
            layer.printValue(pw, padding + "    ");
        }
        pw.println(padding + "  }");
        pw.println();

        pw.println(padding + "}");
    }
}
