/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GFeature;
import gmap.engine.data.GFeatureLine;
import gmap.engine.data.GFeaturePoint;
import gmap.engine.data.GFeaturePolygon;
import gmap.engine.data.GLayer;
import gmap.engine.data.GMap;
import gmap.engine.data.GStyleIcon;
import gmap.engine.data.GStyleLine;
import gmap.engine.data.GStylePolygon;

import java.util.ArrayList;

/**
 * @author jzarzuela
 *
 */
public class GStyleParser extends BaseParser {

    // ----------------------------------------------------------------------------------------------------
    public static void parseAllStylesInfoArray(GMap ownerMap, ArrayList<Object> allStylessInfoArray) throws GMapException {

        for (int n = 0; n < allStylessInfoArray.size(); n++) {

            ArrayList<Object> styleInfoArray = _getItemAsArray("styleInfoArray_." + n, allStylessInfoArray, n);
            _parseStyleInfoArray(ownerMap, styleInfoArray);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parseStyle_FeatureInfoArray(GFeature feature, ArrayList<Object> styleValuesInfoArray) throws GMapException {

        if (feature instanceof GFeaturePoint) {
            GStyleIcon styleIcon = _parseStyleIcon(styleValuesInfoArray);
            feature.setStyle(styleIcon);
        } else if (feature instanceof GFeatureLine) {
            GStyleLine styleLine = _parseStyleLine(styleValuesInfoArray);
            feature.setStyle(styleLine);
        } else if (feature instanceof GFeaturePolygon) {
            GStylePolygon stylePolygon = _parseStylePolygon(styleValuesInfoArray);
            feature.setStyle(stylePolygon);
        } else {
            throw new GMapException("Unknown feature to be applied an style");
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parseStyle_GenericInfoArray(GLayer ownerLayer, ArrayList<Object> styleValuesInfoArray) throws GMapException {

        String titlePropName = _getItemAsString("style.generic.titlePropName", styleValuesInfoArray, 1, 0, 5, 0, 0);
        ownerLayer.setTitlePropName(titlePropName);

        for (GFeature feature : ownerLayer.getFeatures()) {
            if (feature.getStyle() == null) {
                _parseStyle_FeatureInfoArray(feature, styleValuesInfoArray);
            }
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static GStyleIcon _parseStyleIcon(ArrayList<Object> styleValuesInfoArray) throws GMapException {

        // --- Extrae la informacion del icono
        String icon_id = "999999"; // Marcador azul con punto es el icono por defecto
        String icon_color = "0000FF";
        String icon_scale = "1.0";

        ArrayList<Object> iconArrayInfo = _getItemAsArray("feature.styleIcon.array", styleValuesInfoArray, 1, 0);
        if (iconArrayInfo != null) {
            icon_id = _getItemAsString("feature.styleIcon.array.icon_id", iconArrayInfo, 0);
            if (_getItemAsObject("feature.styleIcon.array.colorAndStyle", iconArrayInfo, 3) instanceof ArrayList) {
                icon_color = _getItemAsString("feature.styleIcon.array.colorAndStyle.color", iconArrayInfo, 3, 0);
                icon_scale = _getItemAsString("feature.styleIcon.array.colorAndStyle.scale", iconArrayInfo, 3, 1);
            }
        }
        GStyleIcon icon = new GStyleIcon(icon_id, icon_color, icon_scale);

        return icon;
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parseStyleInfoArray(GMap ownerMap, ArrayList<Object> styleInfoArray) throws GMapException {

        GLayer ownerLayer = ownerMap.getLayerForStyleID(_getItemAsString("style.id", styleInfoArray, 0));

        // Hay un array con los diferentes styles aplicados en la capa (uno por tipo de punto y otro "generico")
        ArrayList<Object> genericStyleValuesArray = null;
        ArrayList<Object> allStylesValuesInfoArray = _getItemAsArray("style.allStyleValuesInfoArray", styleInfoArray, 2);
        for (int n = 0; n < allStylesValuesInfoArray.size(); n++) {

            ArrayList<Object> styleValuesArray = _getItemAsArray("style.allStyleValuesInfoArray_" + n, allStylesValuesInfoArray, n);
            if (styleValuesArray.size() < 2) {
                throw new GMapException("styleValuesArray should have size >= '2' an it had '" + styleValuesArray.size() + "'");
            }

            if (styleValuesArray.get(0) instanceof ArrayList) {
                String feature_gid = _getItemAsString("featureStyleInfo.feature_gid", styleValuesArray, 0, 1, 2, 1, 0, 4);
                GFeature feature = ownerLayer.getFeatureByID(feature_gid);
                _parseStyle_FeatureInfoArray(feature, styleValuesArray);
            } else {
                genericStyleValuesArray = styleValuesArray;
            }
        }

        _parseStyle_GenericInfoArray(ownerLayer, genericStyleValuesArray);

    }

    // ----------------------------------------------------------------------------------------------------
    private static GStyleLine _parseStyleLine(ArrayList<Object> styleValuesInfoArray) throws GMapException {

        String width = _getItemAsString("feature.styleLine.width", styleValuesInfoArray, 1, 1, 0);
        String color = _getItemAsString("feature.styleLine.color", styleValuesInfoArray, 1, 1, 1, 0);
        String alpha = _getItemAsString("feature.styleLine.alpha", styleValuesInfoArray, 1, 1, 1, 1);

        GStyleLine styleLine = new GStyleLine(color, alpha, width);

        return styleLine;
    }

    // ----------------------------------------------------------------------------------------------------
    private static GStylePolygon _parseStylePolygon(ArrayList<Object> styleValuesInfoArray) throws GMapException {

        String borderColor = _getItemAsString("feature.stylePolygon.borderColor", styleValuesInfoArray, 1, 2, 2, 0);
        String borderWidth = _getItemAsString("feature.stylePolygon.borderWidth", styleValuesInfoArray, 1, 2, 1);
        String fillColor = _getItemAsString("feature.stylePolygon.fillColor", styleValuesInfoArray, 1, 2, 0, 0);
        String fillAlpha = _getItemAsString("feature.stylePolygon.fillAlpha", styleValuesInfoArray, 1, 2, 0, 1);

        GStylePolygon stylePolygon = new GStylePolygon(borderColor, borderWidth, fillColor, fillAlpha);
        return stylePolygon;
    }

}
