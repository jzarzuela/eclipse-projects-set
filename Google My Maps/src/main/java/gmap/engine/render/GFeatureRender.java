/**
 * 
 */
package gmap.engine.render;

import java.util.Map;

import gmap.engine.GMapException;
import gmap.engine.data.GFeature;
import gmap.engine.data.GPropertyType;

/**
 * @author jzarzuela
 *
 */
public class GFeatureRender {

    // ----------------------------------------------------------------------------------------------------
    public static String addMapFeaturesReqText(GFeature... features) throws GMapException {
        return _add_Update_MapFeaturesRequestText(true, features);
    }

    // ----------------------------------------------------------------------------------------------------
    public static String deleteMapFeaturesReqText(GFeature... features) throws GMapException {

        String MAP_ID = features[0].getOwnerLayer().getOwnerMap().getGID();
        String TABLE_ID = features[0].getOwnerLayer().getTableID();

        StringBuilder sb = new StringBuilder();

        sb.append("[\"").append(MAP_ID).append("\", null, ");
        sb.append("[\"").append(TABLE_ID).append("\", [");

        for (int n = 0; n < features.length; n++) {

            if (n > 0) {
                sb.append(',');
            }

            sb.append("\"").append(features[n].getGID()).append("\"");
        }

        sb.append("]]");
        sb.append(", null, null, null, null, null, null, null, null, null, null, null, null, null, [[]]]");

        return sb.toString();
    }

    // ----------------------------------------------------------------------------------------------------
    public static String updateMapFeaturesReqText(GFeature... features) throws GMapException {
        return _add_Update_MapFeaturesRequestText(false, features);
    }

    // ----------------------------------------------------------------------------------------------------
    private static String _add_Update_MapFeaturesRequestText(boolean isAddingFeature, GFeature... features) throws GMapException {

        String MAP_ID = features[0].getOwnerLayer().getOwnerMap().getGID();
        String TABLE_ID = features[0].getOwnerLayer().getTableID();

        StringBuilder sb = new StringBuilder();

        sb.append("[\"").append(MAP_ID).append("\",");

        // EN 'UPDATE' VAN AQUI. EN 'ADD' VAN AL FINAL CON LOS OTROS NULL
        if (!isAddingFeature) {
            sb.append("null,null,null,");
        }

        sb.append("[\"").append(TABLE_ID).append("\",[");

        for (int n = 0; n < features.length; n++) {

            if (n > 0) {
                sb.append(',');
            }

            sb.append("[\"").append(features[n].getGID());
            sb.append("\",null,null,null,null,null,null,null,null,null,null,[");

            boolean isFirstProperty = true;
            for (Map.Entry<String, GPropertyType> entry : features[n].getOwnerLayer().getSchema().entrySet()) {

                if (!isFirstProperty) {
                    sb.append(',');
                }
                isFirstProperty = false;

                GPropertyTypeRender.renderPropertyValue(sb, entry.getKey(), entry.getValue(), features[n]);
            }

            sb.append("]]");
        }

        sb.append("]]");

        if (isAddingFeature) {
            sb.append(",null,null,null");
        }

        sb.append(",null,null,null,null,null,null,null,null,null,null,null,[[]]]");

        return sb.toString();
    }

}
