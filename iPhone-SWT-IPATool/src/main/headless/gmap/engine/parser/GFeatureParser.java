/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GFeature;
import gmap.engine.data.GStyleBase;
import gmap.engine.data.GPropertyType;
import gmap.engine.data.GTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * @author jzarzuela
 *
 */
public class GFeatureParser extends BaseParser {

    // ----------------------------------------------------------------------------------------------------
    public static void parseAllFeaturesInfoArray(ParserContext ctx, ArrayList<Object> allFeaturesInfoArray) throws GMapException {

        for (int n = 0; n < allFeaturesInfoArray.size(); n++) {

            ArrayList<Object> featuresInfoArray_N = _getItemAsArray("allFeaturesInfoArray." + n, allFeaturesInfoArray, n);

            String ownerTableID = _getItemAsString("featuresInfoArray.table_id." + n, featuresInfoArray_N, 0);
            GTable ownerTable = ctx.removeUnlinkedOwnerAsset(ownerTableID);

            ArrayList<Object> allFeaturesValuesArray = _getItemAsArray("featuresInfoArray.allFeaturesValuesArray." + n, featuresInfoArray_N, 2);
            _parseAllFeatureValuesInfoArray(ctx, ownerTable, n, allFeaturesValuesArray);
            _sortFeaturesByX(ownerTable);

            // ----------------------------------------------------------------------------------------------------------------
            // Validaciones sobre campos que no sabemos lo que son para detectar cambios
            _checkItemNullValue("allFeaturesInfoArray.unknown.3", featuresInfoArray_N, 3);
            _checkItemArraySize("allFeaturesInfoArray.unknown.4", 1, featuresInfoArray_N, 4);
            _checkItemStringValue("allFeaturesInfoArray.unknown.5", "0", featuresInfoArray_N, 5);

            _getItemAsArray("allFeaturesInfoArray.unknown.1", featuresInfoArray_N, 1);

        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parseAllFeatureValuesInfoArray(ParserContext ctx, GTable ownerTable, int n, ArrayList<Object> allFeaturesValuesArray) throws GMapException {

        for (int i = 0; i < allFeaturesValuesArray.size(); i++) {
            ArrayList<Object> featureValuesArray = _getItemAsArray("featuresInfoArray.allFeaturesValuesArray." + n + ".featureValuesArray" + i, allFeaturesValuesArray, i);
            if (featureValuesArray != null) {
                _parseFeatureValuesInfoArray(ctx, ownerTable, featureValuesArray);
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parseFeatureValuesInfoArray(ParserContext ctx, GTable ownerTable, ArrayList<Object> featureValuesArray) throws GMapException {

        String GID = _getItemAsString("feature.id", featureValuesArray, 0);
        GFeature feature = new GFeature(ownerTable, GID);

        ArrayList<Object> propsArray = _getItemAsArray("feature.propsArray", featureValuesArray, 11);
        for (int n = 0; n < propsArray.size(); n++) {

            ArrayList<Object> propInfoArray = _getItemAsArray("feature.propsArray." + n, propsArray, n);
            _parsePropertyArray(feature, propInfoArray);
        }

        GStyleBase style = ctx.removeUnlinkedStyleForFeatureID(GID);
        feature.style = style;
        if (style == null) {
            ArrayList<Object> geometryArray = (ArrayList) feature.properties.get("gme_geometry_");
            Object obj = _getItemAsObject("featureGeometry", geometryArray, 0, 0);
            if (obj instanceof GNull) {
                feature.style = feature.ownerTable.ownerLayer.style.defStyleLine;
            } else {
                feature.style = feature.ownerTable.ownerLayer.style.defStyleIcon;
            }
        }

        // ----------------------------------------------------------------------------------------------------------------
        // Validaciones sobre campos que no sabemos lo que son para detectar cambios
        _checkItemNullValue("feature.unknown.1", featureValuesArray, 1);
        _checkItemNullValue("feature.unknown.2", featureValuesArray, 2);
        _checkItemNullValue("feature.unknown.3", featureValuesArray, 3);
        _checkItemNullValue("feature.unknown.4", featureValuesArray, 4);
        _checkItemNullValue("feature.unknown.5", featureValuesArray, 5);
        _checkItemNullValue("feature.unknown.6", featureValuesArray, 6);
        _checkItemNullValue("feature.unknown.7", featureValuesArray, 7);
        _checkItemNullValue("feature.unknown.8", featureValuesArray, 8);
        _checkItemNullValue("feature.unknown.9", featureValuesArray, 9);
        _checkItemNullValue("feature.unknown.10", featureValuesArray, 10);

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parsePropertyArray(GFeature feature, ArrayList<Object> propInfoArray) throws GMapException {

        String propName = _getItemAsString("feature.propInfoArray.name", propInfoArray, 0);
        GPropertyType type = feature.ownerTable.schema.propertyDefs.get(propName);
        if (type == null) {
            throw new GMapException("No property definition found in schema for name: '" + propName + "'");
        }

        Object propValue = _parseValue(type, propInfoArray);

        feature.properties.put(propName, propValue);
    }

    // ----------------------------------------------------------------------------------------------------
    private static Object _parseValue(GPropertyType type, ArrayList<Object> propInfoArray) throws GMapException {

        switch (type) {

            case GPT_STRING:
                return _parseValueString(propInfoArray);

            case GPT_DIRECTIONS:
                return _parseValueDirections(propInfoArray);

            case GPT_GEOMETRY:
                return _parseValueGeometry(propInfoArray);

            case GPT_GX_METADATA:
                return _parseValueGxMetadata(propInfoArray);
        }

        throw new GMapException("Don't know how to parse property of type: " + type);
    }

    // ----------------------------------------------------------------------------------------------------
    private static Object _parseValueDirections(ArrayList<Object> propInfoArray) throws GMapException {
        Object objValue = _getItemAsArray("feature.propInfoArray.type.Directions", propInfoArray, 6);
        return objValue;
    }

    // ----------------------------------------------------------------------------------------------------
    private static Object _parseValueGeometry(ArrayList<Object> propInfoArray) throws GMapException {
        Object objValue = _getItemAsArray("feature.propInfoArray.type.Geometry", propInfoArray, 6);
        return objValue;
    }

    // ----------------------------------------------------------------------------------------------------
    private static Object _parseValueGxMetadata(ArrayList<Object> propInfoArray) throws GMapException {
        String strValue = _getItemAsString("feature.propInfoArray.type.GxMetadata", propInfoArray, 4);
        // System.out.println("--> _parseValueGxMetadata: " + propInfoArray);
        // como se parsea esto???
        return strValue;
    }

    // ----------------------------------------------------------------------------------------------------
    private static Object _parseValueString(ArrayList<Object> propInfoArray) throws GMapException {
        String strValue = _getItemAsString("feature.propInfoArray.type.String", propInfoArray, 4);
        return strValue;
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _sortFeaturesByX(GTable ownerTable) {

        if (ownerTable.features.size() == 0)
            return;

        ArrayList<Map.Entry<String, GFeature>> entries = new ArrayList<Map.Entry<String, GFeature>>(ownerTable.features.entrySet());

        String feature_order = (String) entries.get(0).getValue().properties.get("feature_order");
        if (feature_order == null || feature_order.length() == 0) {
            return;
        }

        Collections.sort(entries, new Comparator<Map.Entry<String, GFeature>>() {

            @Override
            public int compare(Map.Entry<String, GFeature> a, Map.Entry<String, GFeature> b) {

                String feature_order_a = (String) a.getValue().properties.get("feature_order");
                String feature_order_b = (String) b.getValue().properties.get("feature_order");
                if (feature_order_a == null)
                    feature_order_a = "9999";
                if (feature_order_b == null)
                    feature_order_b = "9999";
                return feature_order_a.compareTo(feature_order_b);
            }
        });

        ownerTable.features.clear();
        for (Map.Entry<String, GFeature> entry : entries) {
            ownerTable.features.put(entry.getKey(), entry.getValue());
        }
    }
}
