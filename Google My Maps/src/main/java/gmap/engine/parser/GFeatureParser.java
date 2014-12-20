/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GFeature;
import gmap.engine.data.GGeometry;
import gmap.engine.data.GLayer;
import gmap.engine.data.GMap;
import gmap.engine.data.GPropertyType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author jzarzuela
 *
 */
public class GFeatureParser extends BaseParser {

    // ----------------------------------------------------------------------------------------------------
    public static void parseAllFeaturesInfoArray(GMap ownerMap, ArrayList<Object> allFeaturesInfoArray) throws GMapException {

        for (int n = 0; n < allFeaturesInfoArray.size(); n++) {

            ArrayList<Object> featuresInfoArray_N = _getItemAsArray("featureInfoArray_." + n, allFeaturesInfoArray, n);

            GLayer ownerLayer = ownerMap.getLayerForTableID(_getItemAsString("feature.table_id." + n, featuresInfoArray_N, 0));

            ArrayList<Object> allFeaturesValuesArray = _getItemAsArray("feature.allFeaturesValuesArray." + n, featuresInfoArray_N, 2);
            for (int i = 0; i < allFeaturesValuesArray.size(); i++) {

                ArrayList<Object> oneFeatureValuesArray = _getItemAsArray("feature.allFeaturesValuesArray." + n + ".featureValuesArray_" + i, allFeaturesValuesArray, i);
                if (oneFeatureValuesArray != null) {
                    _parseFeatureValuesInfoArray(ownerLayer, oneFeatureValuesArray);
                }
            }

            ownerLayer.sortFeaturesByOrder();
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parseFeatureValuesInfoArray(GLayer ownerLayer, ArrayList<Object> featureValuesArray) throws GMapException {

        String feature_id = _getItemAsString("feature.id", featureValuesArray, 0);

        ArrayList<Object> propsInfoArray = _getItemAsArray("feature.propsArray_" + feature_id, featureValuesArray, 11);
        HashMap<String, Object> properties = _parsePropertyInfoArray(ownerLayer, propsInfoArray);

        Object geometryValue = properties.get("gme_geometry_");
        if (geometryValue == null || !(geometryValue instanceof GGeometry)) {
            throw new GMapException("Feature should have a 'gme_geometry_' property");
        }

        GFeature feature = ownerLayer.addFeature(feature_id, (GGeometry) geometryValue);
        feature.addAllProperties(properties);
    }

    // ----------------------------------------------------------------------------------------------------
    private static HashMap<String, Object> _parsePropertyInfoArray(GLayer ownerLayer, ArrayList<Object> featurePropertyArray) throws GMapException {

        HashMap<String, Object> properties = new HashMap<String, Object>();

        for (int n = 0; n < featurePropertyArray.size(); n++) {

            ArrayList<Object> onePropInfoArray = _getItemAsArray("feature.propsArray_" + n, featurePropertyArray, n);

            String propName = _getItemAsString("feature.propsArray_" + n + ".name", onePropInfoArray, 0);
            GPropertyType type = ownerLayer.getTypeForPropertyName(propName);
            Object propValue = GPropertyTypeParser.parsePropertyValueArray(type, onePropInfoArray);

            properties.put(propName, propValue);
        }

        return properties;

    }

}
