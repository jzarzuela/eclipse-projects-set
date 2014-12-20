/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GFeature;
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
    private static void _renderValueTypeString(StringBuilder sb, Object value) throws GMapException {

        sb.append(", null, null, null, \"" + value.toString() + "\", null, null, true");
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _renderValueTypeDirections(StringBuilder sb, Object value) throws GMapException {

        // [is_directions, <null>, 0, <null>, <null>, <null>, <null>, 0]
        sb.append(", null, 0, null, null, null, null, true");
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _renderValueTypeGeometry(StringBuilder sb, Object value) throws GMapException {

        // sb.append("[\"gme_g \", null, null, null,  null, null, [[[41.3113276, -3.4494281]], [], []], true],");
        sb.append(", null, null, null, null, null, ");
        if (value instanceof GGeometryPoint) {
            GGeometryPoint geoPoint = (GGeometryPoint) value;
            sb.append("[[[" + geoPoint.getLon() + ", " + geoPoint.getLat() + "]], [], []]");
        } else {
            throw new GMapException("Can't render geometry type: " + value.getClass() + ", value = " + value);
        }
        sb.append(", true");

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _renderValueTypeGxMetadata(StringBuilder sb, Object value) throws GMapException {

        // [gx_metadata, <null>, <null>, <null>, <null>, <null>, <null>, 0]
        sb.append(", null, null, null, null, null, null, true");
    }

    // ----------------------------------------------------------------------------------------------------
    public static void renderPropertyValue(StringBuilder sb, String propName, GPropertyType propType, GFeature feature) throws GMapException {

        sb.append("[\"").append(propName).append("\"");

        Object value = feature.getPropertyValue(propName);

        if (value != null) {
            switch (propType) {

                case GPT_STRING:
                    _renderValueTypeString(sb, value);
                    break;

                case GPT_DIRECTIONS:
                    _renderValueTypeDirections(sb, value);
                    break;

                case GPT_GEOMETRY:
                    _renderValueTypeGeometry(sb, value);
                    break;

                case GPT_GX_METADATA:
                    _renderValueTypeGxMetadata(sb, value);
                    break;

                default:
                    throw new GMapException("Don't know how to parse property of type: " + propType);
            }
        }

        sb.append(']');

    }

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

        // String s = propInfoArray.toString();
        // if (!s.equals("[is_directions, <null>, 0, <null>, <null>, <null>, <null>, 0]")) {
        // System.out.println("different GxMetadata Value!");
        // }

        // ¿De donde sacamos el valor y que es?
        Object objValue = _getItemAsArray("feature.propInfoArray.type.Directions", propInfoArray, 6);
        return objValue;
    }

    // ----------------------------------------------------------------------------------------------------
    private static Object _parseValueTypeGeometry(ArrayList<Object> propInfoArray) throws GMapException {

        ArrayList<Object> valueArray;

        ArrayList<Object> geometryArray = _getItemAsArray("feature.propInfoArray.type.GeometryArray", propInfoArray, 6);

        // Si el PRIMER array no esta vacio es un PUNTO
        valueArray = _getItemAsArray("feature.geometryArray.point", geometryArray, 0, 0);
        if (valueArray != null) {
            return _parseValueCoordinates(valueArray);
        }

        // Si el SEGUNDO array no esta vacio es una LINEA
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

        // Si el TERCER array no esta vacio es un POLIGONO
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

        // TENEMOS UN ERROR
        throw new GMapException("Couldn't parse geometry value: " + propInfoArray);
    }

    // ----------------------------------------------------------------------------------------------------
    private static Object _parseValueTypeGxMetadata(ArrayList<Object> propInfoArray) throws GMapException {

        // String s = propInfoArray.toString();
        // if(!s.equals("[gx_metadata, <null>, <null>, <null>, <null>, <null>, <null>, 0]")) {
        // System.out.println("different GxMetadata Value!");
        // }

        // ¿De donde sacamos el valor y que es?
        String strValue = _getItemAsString("feature.propInfoArray.type.GxMetadata", propInfoArray, 4);
        return strValue;
    }

    // ----------------------------------------------------------------------------------------------------
    private static Object _parseValueTypeString(ArrayList<Object> propInfoArray) throws GMapException {

        // if (!gmap.engine.data.GNull.GNULL.equals(propInfoArray.get(1)) //
        // || !gmap.engine.data.GNull.GNULL.equals(propInfoArray.get(2)) //
        // || !gmap.engine.data.GNull.GNULL.equals(propInfoArray.get(3)) //
        // || !gmap.engine.data.GNull.GNULL.equals(propInfoArray.get(5)) //
        // || !gmap.engine.data.GNull.GNULL.equals(propInfoArray.get(6)) //
        // || !"0".equals(propInfoArray.get(7))) {
        // System.out.println("ya");
        // }

        String strValue = _getItemAsString("feature.propInfoArray.type.String", propInfoArray, 4);
        return strValue;
    }
}
