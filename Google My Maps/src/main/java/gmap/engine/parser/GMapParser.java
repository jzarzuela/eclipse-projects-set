/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GMap;

import java.util.ArrayList;

/**
 * @author jzarzuela
 *
 */
public class GMapParser extends BaseParser {

    // ----------------------------------------------------------------------------------------------------
    public static GMap parseMapInfoArray(ArrayList<Object> mapInfoArray) throws GMapException {

        GMap map = new GMap(_getItemAsString("map.id", mapInfoArray, 0));

        map.setName(_getItemAsStringDef("map.name", "", mapInfoArray, 1));
        map.setDesc(_getItemAsStringDef("map.desc", "", mapInfoArray, 2));
        map.setCreationTime(_getItemAsLongDef("map.creationTime", 0L, mapInfoArray, 5, 1));
        map.setLastModifiedTime(_getItemAsLongDef("map.lastModifiedTime", 0L, mapInfoArray, 5, 3));

        return map;
    }
}
