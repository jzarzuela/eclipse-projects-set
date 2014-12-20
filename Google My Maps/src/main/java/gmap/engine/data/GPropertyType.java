/**
 * 
 */
package gmap.engine.data;

import gmap.engine.GMapException;

/**
 * @author jzarzuela
 *
 */
public enum GPropertyType {

    GPT_DIRECTIONS("1"), GPT_GEOMETRY("6"), GPT_GX_METADATA("7"), GPT_STRING("3");

    private String m_strType;

    private GPropertyType(String strType) {
        m_strType = strType;
    }

    public static GPropertyType forTypeName(String strType) throws GMapException {
        switch (strType) {
            case "1":
                return GPT_DIRECTIONS;
            case "3":
                return GPT_STRING;
            case "6":
                return GPT_GEOMETRY;
            case "7":
                return GPT_GX_METADATA;
            default:
                throw new GMapException("Unknow property type name: " + strType);
        }
    }

    /**
     * @return the strType
     */
    public String getStrType() {
        return m_strType;
    }

}
