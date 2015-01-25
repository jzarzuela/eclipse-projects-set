/**
 * 
 */
package gmap.engine.render;

import gmap.engine.GMapException;
import gmap.engine.data.GFeature;
import gmap.engine.data.GGeometryLine;
import gmap.engine.data.GGeometryPoint;
import gmap.engine.data.GGeometryPolygon;
import gmap.engine.data.GPropertyType;
import gmap.engine.parser.BaseParser;

/**
 * @author jzarzuela
 *
 */
public class GPropertyTypeRender extends BaseParser {

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
    private static void _renderGeoPoint(StringBuilder sb, GGeometryPoint geoPoint) throws GMapException {
        sb.append('[').append(geoPoint.getLon()).append(", ").append(geoPoint.getLat()).append(']');
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _renderValueTypeDirections(StringBuilder sb, Object value) throws GMapException {

        // [is_directions, <null>, 0, <null>, <null>, <null>, <null>, 0]
        sb.append(", null, 0, null, null, null, null, true");
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _renderValueTypeGeometry(StringBuilder sb, Object value) throws GMapException {

        sb.append(", null, null, null, null, null, [");
        if (value instanceof GGeometryPoint) {
            GGeometryPoint geoPoint = (GGeometryPoint) value;
            sb.append("[");
            _renderGeoPoint(sb, geoPoint);
            sb.append("], [], []");

        } else if (value instanceof GGeometryLine) {
            GGeometryLine geoLine = (GGeometryLine) value;
            sb.append("[], [[[");
            boolean firstElement = true;
            for (GGeometryPoint geoPoint : geoLine.getPoints()) {
                if (!firstElement)
                    sb.append(", ");
                firstElement = false;
                _renderGeoPoint(sb, geoPoint);
            }
            sb.append("]]], []");
        } else if (value instanceof GGeometryPolygon) {
            GGeometryPolygon geoPolygon = (GGeometryPolygon) value;
            sb.append("[], [], [[[[[");
            boolean firstElement = true;
            for (GGeometryPoint geoPoint : geoPolygon.getPoints()) {
                if (!firstElement)
                    sb.append(", ");
                firstElement = false;
                _renderGeoPoint(sb, geoPoint);
            }
            sb.append("]]]]]");
        } else {
            throw new GMapException("Can't render geometry type: " + value.getClass() + ", value = " + value);
        }
        sb.append("], true");

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _renderValueTypeGxMetadata(StringBuilder sb, Object value) throws GMapException {

        // [gx_metadata, <null>, <null>, <null>, <null>, <null>, <null>, 0]
        sb.append(", null, null, null, null, null, null, true");
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _renderValueTypeString(StringBuilder sb, Object value) throws GMapException {

        sb.append(", null, null, null, \"" + value.toString() + "\", null, null, true");
    }

}
