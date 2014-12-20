/**
 * 
 */
package gmap.engine;

import java.util.Date;

import org.json.JSONObject;

/**
 * @author jzarzuela
 *
 */
public class UserMapData {

    private String m_editUrl;
    private String m_id;
    private long   m_lastEditedUtc;
    private String m_name;

    public UserMapData() {
    }

    public UserMapData(JSONObject json_map) throws Exception {

        try {
            m_name = json_map.getString("name");
            m_id = json_map.getString("id");
            m_lastEditedUtc = json_map.getLong("lastEditedUtc");
            m_editUrl = json_map.getString("url");
        } catch (Throwable th) {
            throw new Exception("Error creating an UserMapData instance from json object: " + json_map, th);
        }
    }

    /**
     * @return the editUrl
     */
    public String getEditUrl() {
        return m_editUrl;
    }

    /**
     * @return the id
     */
    public String getId() {
        return m_id;
    }

    /**
     * @return the lastEditedUtc
     */
    public long getLastEditedUtc() {
        return m_lastEditedUtc;
    }

    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     * @param editUrl
     *            the editUrl to set
     */
    public void setEditUrl(String editUrl) {
        m_editUrl = editUrl;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        m_id = id;
    }

    /**
     * @param lastEditedUtc
     *            the lastEditedUtc to set
     */
    public void setLastEditedUtc(long lastEditedUtc) {
        m_lastEditedUtc = lastEditedUtc;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("UserMapData { name = '").append(m_name).append(", '");
        sb.append("id = '").append(m_id).append("', ");
        //sb.append("lastEditedUtc = ").append(m_lastEditedUtc).append(", ");
        sb.append("lastEditedUtc = ").append(new Date(m_lastEditedUtc)).append(", ");
        sb.append("editUrl = '").append(m_editUrl).append("' }");
        return sb.toString();
    }

}
