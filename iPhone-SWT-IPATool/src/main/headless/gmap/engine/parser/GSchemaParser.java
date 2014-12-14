/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GPropertyType;
import gmap.engine.data.GSchema;
import gmap.engine.data.GTable;

import java.util.ArrayList;

/**
 * @author jzarzuela
 *
 */
public class GSchemaParser extends BaseParser {

    // ----------------------------------------------------------------------------------------------------
    public static void parseSchemaInfoArray(GTable ownerTable, ArrayList<Object> schemaInfoArray) throws GMapException {

        GSchema schema = new GSchema(ownerTable);

        for (int n = 0; n < schemaInfoArray.size(); n++) {
            String propName = _getItemAsString("table.schema.name." + n, schemaInfoArray, n, 0);
            String propType = _getItemAsString("table.schema.type." + n, schemaInfoArray, n, 1);
            schema.addPropertyDef(propName, GPropertyType.forTypeName(propType));
        }

    }

}
