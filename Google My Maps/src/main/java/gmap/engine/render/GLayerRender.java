/**
 * 
 */
package gmap.engine.render;

import java.io.File;
import java.io.FileReader;

import gmap.engine.GMapException;
import gmap.engine.data.GFeature;
import gmap.engine.data.GLayer;
import gmap.engine.data.GStyleIcon;
import gmap.engine.data.GStyleLine;
import gmap.engine.data.GStylePolygon;

/**
 * @author jzarzuela
 *
 */
public class GLayerRender {

    // ----------------------------------------------------------------------------------------------------
    public static String changeStyleReqText(GLayer layer) throws GMapException {

        String MAP_ID = layer.getOwnerMap().getGID();

        StringBuilder sb = new StringBuilder();

        sb.append("[\"").append(MAP_ID).append("\", null, null, null, null, null, ");
        sb.append("[\"").append(layer.getGID()).append("\", ");
        sb.append("[\"").append(layer.getStyleID()).append("\", null, [");

        // Named features
        for (GFeature feature : layer.getFeatures()) {

            // Feature
            sb.append("[[1, [1, null, [[null, null, 1, 0], [[null, null, null, null, \"");
            sb.append(feature.getGID());
            sb.append("\", null, null, 0], null, null, 0]]], [], 0],");

            // style
            _renderFeatureStyleText(sb, layer, feature);

            // fin
            sb.append("], ");
        }

        // Global layer style
        sb.append("[null, ");
        _renderFeatureStyleText(sb, layer, null);
        sb.append("]");

        //
        sb.append("]],");

        //
        _renderTailStyleText(sb, layer);

        //
        sb.append("], null, null, null, null, null, null, null, null, null, [[]]]");

        return sb.toString();
    }

    // ----------------------------------------------------------------------------------------------------
    public static String changeStyleReqText3(GLayer layer) throws GMapException {

        char cbuf[] = new char[1];
        StringBuilder sb = new StringBuilder();

        File f = new File("/Users/jzarzuela/Documents/Java/github-repos/eclipse-projects-set/Google My Maps/src/main/java/gmap/engine/p2.txt");
        try (FileReader fr = new FileReader(f)) {
            while (fr.ready()) {

                if (fr.read(cbuf) < 0)
                    break;
                char c = cbuf[0];

                if (c == '\r' || c == '\n') {
                    continue;
                }

                if (c == ' ') {
                    continue;
                }

                sb.append(c);
            }
        } catch (Exception ex) {
            throw new GMapException("error leyendo fichero", ex);
        }

        String str = sb.toString().replace("[<null>]", "[]");
        str = str.replace("<null>", "null");

        return str;
    }

    // ----------------------------------------------------------------------------------------------------
    private static String _getStyleIconColor(GLayer layer, GFeature feature) {

        if (feature != null && (feature.getStyle() instanceof GStyleIcon)) {
            return ((GStyleIcon) feature.getStyle()).getColor();
        } else {
            return layer.getDefStyleIcon().getColor();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static int _getStyleIconID(GLayer layer, GFeature feature) {

        if (feature != null && (feature.getStyle() instanceof GStyleIcon)) {
            return ((GStyleIcon) feature.getStyle()).getIconID();
        } else {
            return layer.getDefStyleIcon().getIconID();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static double _getStyleLineAlpha(GLayer layer, GFeature feature) {

        if (feature != null && (feature.getStyle() instanceof GStyleLine)) {
            return ((GStyleLine) feature.getStyle()).getAlpha();
        } else {
            return layer.getDefStyleLine().getAlpha();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static String _getStyleLineColor(GLayer layer, GFeature feature) {

        if (feature != null && (feature.getStyle() instanceof GStyleLine)) {
            return ((GStyleLine) feature.getStyle()).getColor();
        } else {
            return layer.getDefStyleLine().getColor();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static int _getStyleLineWith(GLayer layer, GFeature feature) {

        if (feature != null && (feature.getStyle() instanceof GStyleLine)) {
            return ((GStyleLine) feature.getStyle()).getWidth();
        } else {
            return layer.getDefStyleLine().getWidth();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static String _getStylePolygonBColor(GLayer layer, GFeature feature) {

        if (feature != null && (feature.getStyle() instanceof GStylePolygon)) {
            return ((GStylePolygon) feature.getStyle()).getBorderColor();
        } else {
            return layer.getDefStylePolygon().getBorderColor();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static int _getStylePolygonBWith(GLayer layer, GFeature feature) {

        if (feature != null && (feature.getStyle() instanceof GStylePolygon)) {
            return ((GStylePolygon) feature.getStyle()).getBorderWidth();
        } else {
            return layer.getDefStylePolygon().getBorderWidth();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static double _getStylePolygonFAlpha(GLayer layer, GFeature feature) {

        if (feature != null && (feature.getStyle() instanceof GStylePolygon)) {
            return ((GStylePolygon) feature.getStyle()).getFillAlpha();
        } else {
            return layer.getDefStylePolygon().getFillAlpha();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static String _getStylePolygonFColor(GLayer layer, GFeature feature) {

        if (feature != null && (feature.getStyle() instanceof GStylePolygon)) {
            return ((GStylePolygon) feature.getStyle()).getFillColor();
        } else {
            return layer.getDefStylePolygon().getFillColor();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _renderFeatureStyleText(StringBuilder sb, GLayer layer, GFeature feature) {

        //
        sb.append('[');

        // Icono
        sb.append("[").append(_getStyleIconID(layer, feature)).append(", null, null, [\"");
        sb.append(_getStyleIconColor(layer, feature)).append("\", 1], null,");
        _renderStyleSchema(sb, layer);
        sb.append(", null, null, [null, null, null, 0]],");

        // Linea
        sb.append("[").append(_getStyleLineWith(layer, feature)).append(", ");
        sb.append("[\"").append(_getStyleLineColor(layer, feature)).append("\", ");
        sb.append(_getStyleLineAlpha(layer, feature)).append("], null,");
        _renderStyleSchema(sb, layer);
        sb.append(", null, null, []],");

        // Poligono
        sb.append("[[\"").append(_getStylePolygonBColor(layer, feature)).append("\", 1], ");
        sb.append(_getStylePolygonBWith(layer, feature)).append(", ");
        sb.append("[\"").append(_getStylePolygonFColor(layer, feature)).append("\", ");
        sb.append(_getStylePolygonFAlpha(layer, feature)).append("], null, ");
        _renderStyleSchema(sb, layer);
        sb.append(", 0]");

        sb.append("]");
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _renderStyleSchema(StringBuilder sb, GLayer layer) {

        String attrRows[] = layer.getSchemaAttributeRows();

        //
        sb.append("[");

        //
        sb.append("[\"").append(layer.getTitlePropName()).append('"');
        for (String attrRow : attrRows) {
            sb.append(", \"").append(attrRow).append('"');
        }
        sb.append(", \"gx_image_links\", \"place_ref\"],");

        //
        sb.append("\"{").append(layer.getTitlePropName()).append("|title}{gx_image_links|images}");
        for (String attrRow : attrRows) {
            sb.append('{').append(attrRow).append("|attributerow}");
        }
        sb.append("{place_ref|placeref}\"");

        //
        sb.append("]");
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _renderTailStyleText(StringBuilder sb, GLayer layer) {

        String attrRows[] = layer.getSchemaAttributeRows();

        sb.append("[6,");
        sb.append("[1200, 1200, 0.3, \"000000\", \"DB4436\", null, \"000000\", 503],");
        sb.append("null, null, [], [], [0, 1], null, null,");
        sb.append("[\"").append(layer.getTitlePropName()).append("\"");
        for (String attrRow : attrRows) {
            sb.append(", \"").append(attrRow).append('"');
        }
        sb.append("], null, null, null, null,[], null, []");
        sb.append("]");
    }
}
