/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GFeature;
import gmap.engine.data.GFeature.EFeatureType;
import gmap.engine.data.GGeometryLine;
import gmap.engine.data.GGeometryPoint;
import gmap.engine.data.GGeometryPolygon;
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
    private static GGeometryPoint _parseCoordinates(ArrayList<Object> coordArray) throws GMapException {

        if (coordArray != null && coordArray.size() >= 2) {
            double lng = _getItemAsDouble("feature.geometry.coord.lng", coordArray, 0);
            double lat = _getItemAsDouble("feature.geometry.coord.lat", coordArray, 1);
            return new GGeometryPoint(lng, lat);
        } else {
            throw new GMapException("Error parsin GCoordinates: " + coordArray);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static HashMap<String, Object> _parseFeaturePropertyInfoArray(GLayer ownerLayer, ArrayList<Object> featurePropertyArray) throws GMapException {

        HashMap<String, Object> properties = new HashMap<String, Object>();

        for (int n = 0; n < featurePropertyArray.size(); n++) {

            ArrayList<Object> onePropInfoArray = _getItemAsArray("feature.propsArray_" + n, featurePropertyArray, n);

            String propName = _getItemAsString("feature.propsArray_" + n + ".name", onePropInfoArray, 0);
            GPropertyType type = ownerLayer.getTypeForPropertyName(propName);
            Object propValue = _parseValue(type, onePropInfoArray);

            properties.put(propName, propValue);
        }

        return properties;

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parseFeatureValuesInfoArray(GLayer ownerLayer, ArrayList<Object> featureValuesArray) throws GMapException {

        String feature_id = _getItemAsString("feature.id", featureValuesArray, 0);

        ArrayList<Object> propsInfoArray = _getItemAsArray("feature.propsArray_" + feature_id, featureValuesArray, 11);
        HashMap<String, Object> properties = _parseFeaturePropertyInfoArray(ownerLayer, propsInfoArray);

        Object geometryValue = properties.get("gme_geometry_");
        if (geometryValue == null || !(geometryValue instanceof GGeometry)) {
            throw new GMapException("Feature should have a 'gme_geometry_' property");
        }

        GFeature feature = null;
        if (geometryValue instanceof GGeometryPoint) {
            feature = ownerLayer.addFeature(EFeatureType.GFPoint, feature_id);
        } else if (geometryValue instanceof GGeometryLine) {
            feature = ownerLayer.addFeature(EFeatureType.GFLine, feature_id);
        } else if (geometryValue instanceof GGeometryPolygon) {
            feature = ownerLayer.addFeature(EFeatureType.GFPolygon, feature_id);
        } else {
            throw new GMapException("Couldn't create feature for geometry: " + geometryValue);
        }

        feature.addAllProperties(properties);
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

        ArrayList<Object> geometryArray = _getItemAsArray("feature.propInfoArray.type.GeometryArray", propInfoArray, 6);

        ArrayList<Object> valueArray;

        valueArray = _getItemAsArray("feature.geometryArray.point", geometryArray, 0, 0);
        if (valueArray != null) {
            return _parseCoordinates(valueArray);
        }

        valueArray = _getItemAsArray("feature.geometryArray.line", geometryArray, 1, 0);
        if (valueArray != null) {
            GGeometryLine geoLine = new GGeometryLine();
            valueArray = _getItemAsArray("feature.geometryArray.line2", valueArray, 0);
            for (Object obj : valueArray) {
                GGeometryPoint geoPoint = _parseCoordinates((ArrayList<Object>) obj);
                geoLine.addGeoPoint(geoPoint);
            }
            return geoLine;
        }

        valueArray = _getItemAsArray("feature.geometryArray.polygon", geometryArray, 2, 0);
        if (valueArray != null) {
            GGeometryPolygon geoPolygon = new GGeometryPolygon();
            valueArray = _getItemAsArray("feature.geometryArray.polygon2", valueArray, 0, 0, 0);
            for (Object obj : valueArray) {
                GGeometryPoint geoPoint = _parseCoordinates((ArrayList<Object>) obj);
                geoPolygon.addGeoPoint(geoPoint);
            }
            return geoPolygon;
        }

        throw new GMapException("Couldn't parse geometry value: " + propInfoArray);
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

}
