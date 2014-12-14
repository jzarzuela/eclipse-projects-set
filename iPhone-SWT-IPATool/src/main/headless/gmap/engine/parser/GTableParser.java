/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GLayer;
import gmap.engine.data.GTable;

import java.util.ArrayList;

/**
 * @author jzarzuela
 *
 */
public class GTableParser extends BaseParser {

    // ----------------------------------------------------------------------------------------------------
    public static void parseAllTablesInfoArray(ParserContext ctx, ArrayList<Object> allTablesInfoArray) throws GMapException {

        for (int n = 0; n < allTablesInfoArray.size(); n++) {

            ArrayList<Object> tableInfoArray = _getItemAsArray("allTablesInfoArray." + n, allTablesInfoArray, n);
            _parseTableInfoArray(ctx, tableInfoArray);
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _parseTableInfoArray(ParserContext ctx, ArrayList<Object> tableInfoArray) throws GMapException {

        String GID = _getItemAsString("table.id", tableInfoArray, 0);
        GLayer ownerLayer = ctx.removeUnlinkedAssetForChildID(GID);
        GTable table = new GTable(ownerLayer, GID);

        // *************************
        // Se añade como no linkado aun con sus Features (¿QUE PASA SI NO TIENE NINGUNA?)
        // *************************
        ctx.addUnlinkedOwnerAsset(table);

        // El schema esta en el primer elemento de un array (¿puede haber mas de un schema por tabla?)
        ArrayList<Object> schemaInfoArray = _getItemAsArray("table.schemaInfoArray", tableInfoArray, 2, 0);
        GSchemaParser.parseSchemaInfoArray(table, schemaInfoArray);

        // ----------------------------------------------------------------------------------------------------------------
        // Validaciones sobre campos que no sabemos lo que son para detectar cambios
        _checkItemNullValue("table.unknown.1", tableInfoArray, 1);
        _checkItemNullValue("table.unknown.4", tableInfoArray, 4);
        _checkItemNullValue("table.unknown.5", tableInfoArray, 5);
        _checkItemNullValue("table.unknown.6", tableInfoArray, 6);
        _checkItemNullValue("table.unknown.7", tableInfoArray, 7);
        _checkItemNullValue("table.unknown.10", tableInfoArray, 10);
        _checkItemNullValue("table.unknown.11", tableInfoArray, 11);
        _checkItemNullValue("table.unknown.12", tableInfoArray, 12);
        _checkItemNullValue("table.unknown.13", tableInfoArray, 13);
        _checkItemNullValue("table.unknown.15", tableInfoArray, 15);
        _checkItemNullValue("table.unknown.20", tableInfoArray, 20);

        _checkItemStringValue("table.unknown.8", "gme_geometry_", tableInfoArray, 8);
        _checkItemStringValue("table.unknown.18", "gx_image_links", tableInfoArray, 18);

        _checkItemArraySize("table.unknown.3", 1, tableInfoArray, 3);
        _checkItemArraySize("table.unknown.21", 1, tableInfoArray, 21);

        _getItemAsString("table.unknown.9", tableInfoArray, 9); // "place_ref"
        _getItemAsString("table.unknown.14", tableInfoArray, 14); // "feature_order"
        _getItemAsString("table.unknown.16", tableInfoArray, 16); // "gx_metafeatureid"
        _getItemAsString("table.unknown.17", tableInfoArray, 17); // "gx_routeinfo"
        
        _getItemAsArray("table.unknown.19", tableInfoArray, 19);
    }
}
