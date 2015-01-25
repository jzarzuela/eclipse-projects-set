/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GLayer;
import gmap.engine.data.GPropertyType;

import java.util.ArrayList;

/**
 * @author jzarzuela
 *
 */
public class GSchemaParser extends BaseParser {

    // Reserved property names:
    // -- feature_order
    // -- gme_geometry_
    // -- gx_image_links
    // -- gx_metadata
    // -- gx_metafeatureid
    // -- gx_routeinfo
    // -- is_directions
    // -- place_ref

    // ----------------------------------------------------------------------------------------------------
    public static void parseSchemaInfoArray(GLayer layer, ArrayList<Object> schemaInfoArray) throws GMapException {

        for (int n = 0; n < schemaInfoArray.size(); n++) {

            String propName = _getItemAsString("table.schema.name." + n, schemaInfoArray, n, 0);
            long propType = _getItemAsLongDef("table.schema.type." + n, null, schemaInfoArray, n, 1);

            layer.addPropertyToSchema(propName, GPropertyType.forTypeName((int) propType));
        }

    }

}
