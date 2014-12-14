/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GStyleIcon;
import gmap.engine.data.GLayer;
import gmap.engine.data.GStyle;
import gmap.engine.data.GStyleLine;

import java.util.ArrayList;

/**
 * @author jzarzuela
 *
 */
public class GStyleParser extends BaseParser {

    // ----------------------------------------------------------------------------------------------------
    public static void parseAllStylesInfoArray(ParserContext ctx, ArrayList<Object> allStylessInfoArray) throws GMapException {

        for (int n = 0; n < allStylessInfoArray.size(); n++) {

            ArrayList<Object> styleInfoArray = _getItemAsArray("allStylessInfoArray." + n, allStylessInfoArray, n);
            _parseStyleInfoArray(ctx, styleInfoArray);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parseStyleInfoArray(ParserContext ctx, ArrayList<Object> styleInfoArray) throws GMapException {

        String GID = _getItemAsString("style.id", styleInfoArray, 0);
        GLayer ownerLayer = ctx.removeUnlinkedAssetForChildID(GID);
        GStyle style = new GStyle(ownerLayer, GID);

        // Hay un array con los diferentes styles aplicados en la capa (uno por tipo de punto y otro "generico")
        ArrayList<Object> allStylesValuesInfoArray = _getItemAsArray("style.allStyleValuesInfoArray", styleInfoArray, 2);
        for (int n = 0; n < allStylesValuesInfoArray.size(); n++) {
            ArrayList<Object> styleValuesArray = _getItemAsArray("style.allStyleValuesInfoArray." + n, allStylesValuesInfoArray, n);
            _parseStyleValuesInfoArray(ctx, style, styleValuesArray);
        }

        // ----------------------------------------------------------------------------------------------------------------
        // Validaciones sobre campos que no sabemos lo que son para detectar cambios
        _checkItemNullValue("style.unknown.1", styleInfoArray, 1);
    }

    //

    // ----------------------------------------------------------------------------------------------------
    private static void _parseStyleValuesInfoArray(ParserContext ctx, GStyle style, ArrayList<Object> styleValuesInfoArray) throws GMapException {

        if (styleValuesInfoArray.size() != 2) {
            throw new GMapException("styleValuesInfoArray should have size of '2' an it had '" + styleValuesInfoArray.size() + "'");
        }

        if(styleValuesInfoArray.toString().contains("085CBF708252FB91")) {
            System.out.println("ya");
        }
        
        // --- Extrae la informacion del feature con style especifico
        if (styleValuesInfoArray.get(0) instanceof ArrayList) {
            _parseStyle_FeatureInfoArray(ctx, style, styleValuesInfoArray);
        } else {
            _parseStyle_GenericInfoArray(ctx, style, styleValuesInfoArray);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parseStyle_GenericInfoArray(ParserContext ctx, GStyle style, ArrayList<Object> styleValuesInfoArray) throws GMapException {
        try {
            String titlePropName = _getItemAsString("featurePointStyleIconInfo.mainPropertyName", styleValuesInfoArray, 1, 0, 5, 0, 0);
            style.titlePropName = titlePropName;

            GStyleIcon styleIcon = _parseStyleIcon(ctx, style, styleValuesInfoArray);
            style.defStyleIcon = styleIcon;
            
            GStyleLine styleLine = _parseStyleLine(ctx, style, styleValuesInfoArray);
            style.defStyleLine = styleLine;

        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parseStyle_FeatureInfoArray(ParserContext ctx, GStyle style, ArrayList<Object> styleValuesInfoArray) throws GMapException {

        ArrayList<Object> featureStyleInfo = _getItemAsArray("featureStyle.info", styleValuesInfoArray, 1);
        if (featureStyleInfo.size() == 1) {
            _parseStyle_PointInfoArray(ctx, style, styleValuesInfoArray);
        } else if (featureStyleInfo.size() == 2 || featureStyleInfo.size() == 3) {
            _parseStyle_PolyLineInfoArray(ctx, style, styleValuesInfoArray);
        } else {
            System.out.println("que es esto?");
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static GStyleLine _parseStyleLine(ParserContext ctx, GStyle style, ArrayList<Object> styleValuesInfoArray) throws GMapException {

        String width = _getItemAsString("feature.styleLine.width", styleValuesInfoArray, 1, 1, 0);
        String color = _getItemAsString("feature.styleLine.color", styleValuesInfoArray, 1, 1, 1, 0);
        String alpha = _getItemAsString("feature.styleLine.alpha", styleValuesInfoArray, 1, 1, 1, 1);

        GStyleLine styleLine = new GStyleLine(color, alpha, width);

        return styleLine;
    }

    // ----------------------------------------------------------------------------------------------------
    private static GStyleIcon _parseStyleIcon(ParserContext ctx, GStyle style, ArrayList<Object> styleValuesInfoArray) throws GMapException {

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
    private static void _parseStyle_PolyLineInfoArray(ParserContext ctx, GStyle style, ArrayList<Object> styleValuesInfoArray) throws GMapException {

        GStyleLine styleLine = _parseStyleLine(ctx, style, styleValuesInfoArray);

        String feature_GID = _getItemAsString("featurePointStyleIconInfo.feature_gid", styleValuesInfoArray, 0, 1, 2, 1, 0, 4);
        ctx.addUnlinkedFeatureIDToStyle(feature_GID, styleLine);
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parseStyle_PointInfoArray(ParserContext ctx, GStyle style, ArrayList<Object> styleValuesInfoArray) throws GMapException {

        GStyleIcon styleIcon = _parseStyleIcon(ctx, style, styleValuesInfoArray);

        String feature_GID = _getItemAsString("featurePointStyleIconInfo.feature_gid", styleValuesInfoArray, 0, 1, 2, 1, 0, 4);
        ctx.addUnlinkedFeatureIDToStyle(feature_GID, styleIcon);
    }
}
