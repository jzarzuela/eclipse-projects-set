/**
 * 
 */
package gmap.engine.data;

import gmap.engine.GMapException;

import java.io.PrintWriter;
import java.util.HashMap;

/**
 * @author jzarzuela
 *
 */
public abstract class GFeature extends GAsset {

    private GLayer                  m_ownerLayer;
    private HashMap<String, Object> m_properties = new HashMap<String, Object>();
    private GStyle              m_style;

    // ----------------------------------------------------------------------------------------------------
    public GFeature(GLayer ownerLayer, String feature_gid) {
        super(feature_gid);
        if (ownerLayer == null) {
            throw new RuntimeException("Feature's owner layer can't be null");
        }
        m_ownerLayer = ownerLayer;
    }

    // ----------------------------------------------------------------------------------------------------
    public void addAllProperties(HashMap<String, Object> properties) {
        m_properties.putAll(properties);
    }

    // ----------------------------------------------------------------------------------------------------
    public void addProperty(String propName, Object propValue) {
        m_properties.put(propName, propValue);
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the geometry
     */
    public GGeometry getGeometry() {
        return (GGeometry) m_properties.get("gme_geometry_");
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the ownerLayer
     */
    public GLayer getOwnerLayer() {
        return m_ownerLayer;
    }

    // ----------------------------------------------------------------------------------------------------
    public Object getPropertyValue(String propName) {

        return m_properties.get(propName);
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the style
     */
    public GStyle getStyle() {
        return m_style;
    }

    public String getTitle() {
        String title = (String) m_properties.get(m_ownerLayer.getTitlePropName());
        return title != null ? title : "";
    }

    // ----------------------------------------------------------------------------------------------------
    public abstract void setGeometry(GGeometry geometry) throws GMapException;

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param style
     *            the style to set
     */
    public abstract void setStyle(GStyle style) throws GMapException;

    // ----------------------------------------------------------------------------------------------------
    protected abstract String _sub_featureType();

    // ----------------------------------------------------------------------------------------------------
    protected abstract void _sub_printValue(PrintWriter pw, String padding);

    // ----------------------------------------------------------------------------------------------------
    protected void _sub_setGeometry(GGeometry geometry) {
        m_properties.put("gme_geometry_", geometry);
    }

    // ----------------------------------------------------------------------------------------------------
    protected void _sub_setStyle(GStyle style) {
        m_style = style;
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    protected void printValue(PrintWriter pw, String padding) {

        pw.println(padding + "GFeature (" + _sub_featureType() + ") {");
        pw.println(padding + "  GID: '" + getGID() + "'");
        pw.println(padding + "  Title: '" + getTitle() + "'");
        pw.print(padding + "  Style: ");
        if (m_style != null) {
            m_style.printValue(pw, "");
            pw.println();
        } else {
            pw.println("********** without style **********");
        }
        pw.print(padding + "  Geometry: ");
        if (getGeometry() != null) {
            getGeometry().printValue(pw, "");
            pw.println();
        } else {
            pw.println("********** without geometry!! **********");
        }
        pw.println(padding + "  Properties: ");
        for (String key : m_ownerLayer.getSchema().keySet()) {
            pw.println(padding + "    '" + key + "' = '" + m_properties.get(key) + "'");
        }
        _sub_printValue(pw, padding + "  ");
        pw.println(padding + "}");
    }

}
