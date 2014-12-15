/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GGeometryLine;
import gmap.engine.data.GGeometryPoint;
import gmap.engine.data.GGeometryPolygon;
import gmap.engine.data.GPropertyType;

import java.util.ArrayList;

/**
 * @author jzarzuela
 *
 */
public class GPropertyTypeParser extends BaseParser {

    // ----------------------------------------------------------------------------------------------------
    public static Object parsePropertyValueArray(GPropertyType type, ArrayList<Object> propertyValueArray) throws GMapException {

        switch (type) {

            case GPT_STRING:
                return _parseValueTypeString(propertyValueArray);

            case GPT_DIRECTIONS:
                return _parseValueTypeDirections(propertyValueArray);

            case GPT_GEOMETRY:
                return _parseValueTypeGeometry(propertyValueArray);

            case GPT_GX_METADATA:
                return _parseValueTypeGxMetadata(propertyValueArray);
        }

        throw new GMapException("Don't know how to parse property of type: " + type);
    }

    // ----------------------------------------------------------------------------------------------------
    private static GGeometryPoint _parseValueCoordinates(ArrayList<Object> coordArray) throws GMapException {

        if (coordArray != null && coordArray.size() >= 2) {
            double lng = _getItemAsDouble("feature.geometry.coord.lng", coordArray, 0);
            double lat = _getItemAsDouble("feature.geometry.coord.lat", coordArray, 1);
            return new GGeometryPoint(lng, lat);
        } else {
            throw new GMapException("Error parsin GCoordinates: " + coordArray);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static Object _parseValueTypeDirections(ArrayList<Object> propInfoArray) throws GMapException {
        Object objValue = _getItemAsArray("feature.propInfoArray.type.Directions", propInfoArray, 6);
        return objValue;
    }

    // ----------------------------------------------------------------------------------------------------
    private static Object _parseValueTypeGeometry(ArrayList<Object> propInfoArray) throws GMapException {

        ArrayList<Object> geometryArray = _getItemAsArray("feature.propInfoArray.type.GeometryArray", propInfoArray, 6);

        ArrayList<Object> valueArray;

        valueArray = _getItemAsArray("feature.geometryArray.point", geometryArray, 0, 0);
        if (valueArray != null) {
            return _parseValueCoordinates(valueArray);
        }

        valueArray = _getItemAsArray("feature.geometryArray.line", geometryArray, 1, 0);
        if (valueArray != null) {
            GGeometryLine geoLine = new GGeometryLine();
            valueArray = _getItemAsArray("feature.geometryArray.line2", valueArray, 0);
            for (Object obj : valueArray) {
                GGeometryPoint geoPoint = _parseValueCoordinates((ArrayList<Object>) obj);
                geoLine.addGeoPoint(geoPoint);
            }
            return geoLine;
        }

        valueArray = _getItemAsArray("feature.geometryArray.polygon", geometryArray, 2, 0);
        if (valueArray != null) {
            GGeometryPolygon geoPolygon = new GGeometryPolygon();
            valueArray = _getItemAsArray("feature.geometryArray.polygon2", valueArray, 0, 0, 0);
            for (Object obj : valueArray) {
                GGeometryPoint geoPoint = _parseValueCoordinates((ArrayList<Object>) obj);
                geoPolygon.addGeoPoint(geoPoint);
            }
            return geoPolygon;
        }

        throw new GMapException("Couldn't parse geometry value: " + propInfoArray);
    }

    // ----------------------------------------------------------------------------------------------------
    private static Object _parseValueTypeGxMetadata(ArrayList<Object> propInfoArray) throws GMapException {

        String strValue = _getItemAsString("feature.propInfoArray.type.GxMetadata", propInfoArray, 4);
        // System.out.println("--> _parseValueGxMetadata: " + propInfoArray);
        // como se parsea esto???
        return strValue;
    }

    // ----------------------------------------------------------------------------------------------------
    private static Object _parseValueTypeString(ArrayList<Object> propInfoArray) throws GMapException {
        String strValue = _getItemAsString("feature.propInfoArray.type.String", propInfoArray, 4);
        return strValue;
    }
}
