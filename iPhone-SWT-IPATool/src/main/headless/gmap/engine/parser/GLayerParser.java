/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GLayer;
import gmap.engine.data.GMap;

import java.util.ArrayList;

import org.joda.time.DateTime;

/**
 * @author jzarzuela
 *
 */
public class GLayerParser extends BaseParser {

    // ----------------------------------------------------------------------------------------------------
    public static void parseAllLayersInfoArray(ParserContext ctx, ArrayList<Object> allLayersInfoArray) throws GMapException {

        for (int n = 0; n < allLayersInfoArray.size(); n++) {

            ArrayList<Object> layerInfoArray = _getItemAsArray("allLayersInfoArray." + n, allLayersInfoArray, n);
            _parseLayerInfoArray(ctx, layerInfoArray);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parseLayerInfoArray(ParserContext ctx, ArrayList<Object> layerInfoArray) throws GMapException {

        String GID = _getItemAsString("layer.id", layerInfoArray, 0);
        GMap ownerMap = ctx.removeUnlinkedAssetForChildID(GID);
        GLayer layer = new GLayer(ownerMap, GID);

        layer.name = _getItemAsString("layer.name", layerInfoArray, 1);

        String strDate = _getItemAsString("map.creationTime", layerInfoArray, 7, 1);
        layer.creationTime = new DateTime(Long.parseLong(strDate));
        strDate = _getItemAsString("map.creationTime", layerInfoArray, 7, 3);
        layer.lastModifiedTime = new DateTime(Long.parseLong(strDate));

        // ID del style de la capa
        _checkItemArraySize("layer.style.id", 1, layerInfoArray, 3);
        String styleID = _getItemAsString("layer.style.id", layerInfoArray, 3, 0);
        ctx.addUnlinkedChildIDToAsset(styleID, layer);

        // ID de la Table de la capa
        _checkItemArraySize("layer.dsTable.id", 1, layerInfoArray, 4);
        String tableID = _getItemAsString("layer.dsTable.id", layerInfoArray, 4, 0);
        ctx.addUnlinkedChildIDToAsset(tableID, layer);

        // ----------------------------------------------------------------------------------------------------------------
        // Validaciones sobre campos que no sabemos lo que son para detectar cambios
        _checkItemArraySize("layer.unknown.2", 1, layerInfoArray, 2);
        _getItemAsArray("layer.unknown.5", layerInfoArray, 5);
        _getItemAsArray("layer.unknown.6", layerInfoArray, 6);
        _checkItemNullValue("layer.unknown.8", layerInfoArray, 8);
        _checkItemStringValue("layer.unknown.9", "4", layerInfoArray, 9);

    }

}
