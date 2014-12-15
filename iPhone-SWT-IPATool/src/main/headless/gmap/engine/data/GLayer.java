/**
 * 
 */
package gmap.engine.data;

import gmap.engine.GMapException;
import gmap.engine.data.GAsset;
import gmap.engine.data.GFeature.EFeatureType;
import gmap.engine.data.GPropertyType;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

/**
 * @author jzarzuela
 *
 */
public class GLayer extends GAsset {

    // RFC 3339 formatted date-time value (e.g. 1970-01-01T00:00:00Z)
    private DateTime                             m_creationTime;
    private ArrayList<GFeature>                  m_features      = new ArrayList<GFeature>();
    private DateTime                             m_lastModifiedTime;
    private String                               m_name;
    private GMap                                 m_ownerMap;
    private LinkedHashMap<String, GPropertyType> m_schema        = new LinkedHashMap<String, GPropertyType>();
    private String                               m_styleID;
    private String                               m_tableID;
    private String                               m_titlePropName = "";

    // ----------------------------------------------------------------------------------------------------
    protected GLayer(GMap ownerMap, String gid) {
        super(gid);
        if (ownerMap == null) {
            throw new RuntimeException("Layer's ownerMap can't be null");
        }
        m_ownerMap = ownerMap;
    }

    // ----------------------------------------------------------------------------------------------------
    public GFeature addFeature(EFeatureType type, String feature_gid) throws GMapException {

        GFeature feature;
        switch (type) {
            case GFPoint:
                feature = new GFeaturePoint(this, feature_gid);
                break;
            case GFLine:
                feature = new GFeatureLine(this, feature_gid);
                break;
            case GFPolygon:
                feature = new GFeaturePolygon(this, feature_gid);
                break;
            default:
                throw new GMapException("Unknown feature type: " + type);
        }

        m_features.add(feature);
        return feature;
    }

    // ----------------------------------------------------------------------------------------------------
    public void addPropertyToSchema(String propName, GPropertyType propType) {
        m_schema.put(propName, propType);
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the creationTime
     */
    public DateTime getCreationTime() {
        return m_creationTime;
    }

    // ----------------------------------------------------------------------------------------------------
    public GFeature getFeatureByID(String feature_gid) throws GMapException {

        for (GFeature feature : m_features) {
            if (feature_gid != null && feature_gid.equals(feature.getGID())) {
                return feature;
            }
        }

        throw new GMapException("No feature found for id: '" + feature_gid + "'");
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the features
     */
    public List<GFeature> getFeatures() {
        return Collections.unmodifiableList(m_features);
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the lastModifiedTime
     */
    public DateTime getLastModifiedTime() {
        return m_lastModifiedTime;
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
     * @return the ownerMap
     */
    public GMap getOwnerMap() {
        return m_ownerMap;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the schema
     */
    public Map<String, GPropertyType> getSchema() {
        return Collections.unmodifiableMap(m_schema);
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the styleID
     */
    public String getStyleID() {
        return m_styleID;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the tableID
     */
    public String getTableID() {
        return m_tableID;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the titlePropName
     */
    public String getTitlePropName() {
        return m_titlePropName;
    }

    // ----------------------------------------------------------------------------------------------------
    public GPropertyType getTypeForPropertyName(String propName) throws GMapException {

        GPropertyType type = m_schema.get(propName);
        if (type != null) {
            return type;
        } else {
            throw new GMapException("No property type definition found in schema for propName: '" + propName + "'");
        }
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
     * @param styleID
     *            the styleID to set
     */
    public void setStyleID(String styleID) {
        m_styleID = styleID;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param tableID
     *            the tableID to set
     */
    public void setTableID(String tableID) {
        m_tableID = tableID;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param titlePropName
     *            the titlePropName to set
     */
    public void setTitlePropName(String titlePropName) {
        m_titlePropName = titlePropName;
    }

    // ----------------------------------------------------------------------------------------------------
    public void sortFeaturesByOrder() {

        if (m_features.size() == 0)
            return;

        Collections.sort(m_features, new Comparator<GFeature>() {

            @Override
            public int compare(GFeature a, GFeature b) {

                String feature_order_a = (String) a.getPropertyValue("feature_order");
                String feature_order_b = (String) b.getPropertyValue("feature_order");
                if (feature_order_a == null)
                    feature_order_a = "9999";
                if (feature_order_b == null)
                    feature_order_b = "9999";
                return feature_order_a.compareTo(feature_order_b);
            }
        });

    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    protected void printValue(PrintWriter pw, String padding) {

        pw.println(padding + "//===========================================================================================");
        pw.println(padding + "GLayer {");
        pw.println(padding + "  GID: '" + getGID() + "'");
        pw.println(padding + "  name: '" + m_name + "'");
        pw.println(padding + "  creationTime: '" + m_creationTime + "'");
        pw.println(padding + "  lastModifiedTime: '" + m_lastModifiedTime + "'");
        pw.println(padding + "  tableID: '" + m_tableID + "'");
        pw.println(padding + "  styleID: '" + m_styleID + "'");

        pw.println(padding + "  schema {");
        for (Map.Entry<String, GPropertyType> entry : m_schema.entrySet()) {
            pw.println(padding + "    '" + entry.getKey() + "' : " + entry.getValue());
        }
        pw.println(padding + "  }");

        pw.println();
        pw.println(padding + "  features: {");
        for (GFeature feature : m_features) {
            pw.println();
            feature.printValue(pw, padding + "    ");
        }
        pw.println(padding + "  }");
        pw.println();

        pw.println(padding + "}");
    }
}
