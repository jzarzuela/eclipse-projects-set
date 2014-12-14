/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GMap;

import java.util.ArrayList;

import org.joda.time.DateTime;

/**
 * @author jzarzuela
 *
 */
public class GMapParser extends BaseParser {

    // ----------------------------------------------------------------------------------------------------
    public static GMap parseMapInfoArray(ParserContext ctx, ArrayList<Object> mapInfoArray) throws GMapException {

        String GID = _getItemAsString("map.id", mapInfoArray, 0);
        GMap map = new GMap(GID);

        map.name = _getItemAsString("map.name", mapInfoArray, 1);
        map.desc = _getItemAsString("map.desc", mapInfoArray, 2);

        String strDate = _getItemAsString("map.creationTime", mapInfoArray, 5, 1);
        map.creationTime = new DateTime(Long.parseLong(strDate));
        strDate = _getItemAsString("map.creationTime", mapInfoArray, 5, 3);
        map.lastModifiedTime = new DateTime(Long.parseLong(strDate));

        // IDs de las capas que tiene el mapa
        ArrayList<Object> layerIDs = _getItemAsArray("map.layers", mapInfoArray, 4);
        for (int n = 0; n < layerIDs.size(); n++) {
            String layer_id = _getItemAsString("map.layers.id", layerIDs, n);
            ctx.addUnlinkedChildIDToAsset(layer_id, map);
        }

        // ----------------------------------------------------------------------------------------------------------------
        // Validaciones sobre campos que no sabemos lo que son para detectar cambios
        _checkItemNullValue("map.unknown.3", mapInfoArray, 3);
        _checkItemNullValue("map.unknown.10", mapInfoArray, 10);
        

        _checkItemStringValue("map.unknown.9", "0", mapInfoArray, 9);

        _getItemAsString("map.unknown.6", mapInfoArray, 6); // null - '1'
        _getItemAsString("map.unknown.11",  mapInfoArray, 11); // null - '0'
        _getItemAsString("map.unknown.12", mapInfoArray, 12); // Antiguo ID de GMap??
        _getItemAsString("map.unknown.13", mapInfoArray, 13); // 'es'
        _getItemAsString("map.unknown.14", mapInfoArray, 14); // 'es'
        _getItemAsString("map.unknown.15", mapInfoArray, 15); // '0'
        _getItemAsString("map.unknown.16", mapInfoArray, 16); // '1'

        _getItemAsString("map.unknown.18", mapInfoArray, 18);

        _getItemAsArray("map.unknown.7", mapInfoArray, 7); // ??
        _getItemAsArray("map.unknown.17", mapInfoArray, 17); // ??

        _checkItemArraySize("map.unknown.8", 1, mapInfoArray, 8);

        return map;
    }

}
