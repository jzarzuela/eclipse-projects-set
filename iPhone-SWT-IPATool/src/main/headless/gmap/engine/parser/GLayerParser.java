/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GLayer;
import gmap.engine.data.GMap;

import java.util.ArrayList;

/**
 * @author jzarzuela
 *
 */
public class GLayerParser extends BaseParser {

    // ----------------------------------------------------------------------------------------------------
    public static void parseAllLayersInfoArray(GMap ownerMap, ArrayList<Object> allLayersInfoArray) throws GMapException {

        for (int n = 0; n < allLayersInfoArray.size(); n++) {

            ArrayList<Object> layerInfoArray = _getItemAsArray("layerInfoArray_" + n, allLayersInfoArray, n);
            _parseLayerInfoArray(ownerMap, layerInfoArray);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parseLayerInfoArray(GMap ownerMap, ArrayList<Object> layerInfoArray) throws GMapException {

        GLayer layer = ownerMap.addLayer(_getItemAsString("layer.id", layerInfoArray, 0));

        layer.setName(_getItemAsStringDef("layer.name", "", layerInfoArray, 1));
        layer.setCreationTime(_getItemAsLongDef("layer.creationTime", 0, layerInfoArray, 7, 1));
        layer.setLastModifiedTime(_getItemAsLongDef("layer.creationTime", 0, layerInfoArray, 7, 3));

        layer.setStyleID(_getItemAsStringDef("layer.style_id", null, layerInfoArray, 3, 0));
        layer.setTableID(_getItemAsStringDef("layer.table_id", null, layerInfoArray, 4, 0));

    }
}
