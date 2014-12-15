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
public class GTableParser extends BaseParser {

    // ----------------------------------------------------------------------------------------------------
    public static void parseAllTablesInfoArray(GMap ownerMap, ArrayList<Object> allTablesInfoArray) throws GMapException {

        for (int n = 0; n < allTablesInfoArray.size(); n++) {

            ArrayList<Object> tableInfoArray = _getItemAsArray("tableInfoArray_." + n, allTablesInfoArray, n);
            _parseTableInfoArray(ownerMap, tableInfoArray);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parseTableInfoArray(GMap ownerMap, ArrayList<Object> tableInfoArray) throws GMapException {

        GLayer ownerLayer = ownerMap.getLayerForTableID(_getItemAsString("table.id", tableInfoArray, 0));

        // El schema esta en el primer elemento de un array
        ArrayList<Object> schemaInfoArray = _getItemAsArray("table.schemaInfoArray", tableInfoArray, 2, 0);
        GSchemaParser.parseSchemaInfoArray(ownerLayer, schemaInfoArray);

    }
}
