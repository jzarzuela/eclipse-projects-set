/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GNull;

import java.util.ArrayList;

/**
 * @author jzarzuela
 *
 */
public abstract class BaseParser {

    // ----------------------------------------------------------------------------------------------------
    protected static ArrayList<Object> _getItemAsArray(String label, ArrayList<Object> rootContainer, int... indexes) throws GMapException {

        Object item = _getItemAsObject(label, rootContainer, indexes);
        if (!(item instanceof ArrayList) && !(item instanceof GNull)) {
            throw new GMapException("[" + label + "] - Item class is not an ArrayList" + item.getClass());
        }
        return item instanceof GNull ? null : (ArrayList) item;
    }

    // ----------------------------------------------------------------------------------------------------
    protected static Double _getItemAsDouble(String label, ArrayList<Object> rootContainer, int... indexes) throws GMapException {

        Object item = _getItemAsObject(label, rootContainer, indexes);
        if (!(item instanceof Double) && !(item instanceof GNull)) {
            throw new GMapException("[" + label + "] - Item class is not an Double(str)" + item.getClass());
        }

        try {
            
            if (item instanceof GNull)
                return 0.0;
            
            return item != null ? (Double) item : null;
            
        } catch (Throwable th) {
            throw new GMapException("[" + label + "] - Error parsing Double(str) value: " + item, th);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    protected static Double _getItemAsDoubleDef(String label, Double defValue, ArrayList<Object> rootContainer, int... indexes) throws GMapException {

        Double value = _getItemAsDouble(label, rootContainer, indexes);
        return value != null ? value : defValue;
    }

    // ----------------------------------------------------------------------------------------------------
    protected static Long _getItemAsLong(String label, ArrayList<Object> rootContainer, int... indexes) throws GMapException {

        Object item = _getItemAsObject(label, rootContainer, indexes);
        if (!(item instanceof Long) && !(item instanceof GNull)) {
            throw new GMapException("[" + label + "] - Item class is not an Long(str)" + item.getClass());
        }

        try {
            if (item instanceof GNull)
                return 0L;
            
            return item != null ? (Long) item : null;
            
        } catch (Throwable th) {
            throw new GMapException("[" + label + "] - Error parsing Long(str) value: " + item, th);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    protected static Long _getItemAsLongDef(String label, Long defValue, ArrayList<Object> rootContainer, int... indexes) throws GMapException {

        Long value = _getItemAsLong(label, rootContainer, indexes);
        return value != null ? value : defValue;
    }

    // ----------------------------------------------------------------------------------------------------
    protected static Object _getItemAsObject(String label, ArrayList<Object> rootContainer, int... indexes) throws GMapException {

        Object item = rootContainer;

        for (int index : indexes) {

            if (!(item instanceof ArrayList)) {
                throw new GMapException("[" + label + "] - Can't search inside somethig that is not an ArrayList" + item.getClass());
            }

            ArrayList<Object> container = (ArrayList<Object>) item;

            if (index >= container.size()) {
                throw new GMapException("[" + label + "] - Incorrect container array size (" + container.size() + ") while accessing item at index: " + index);
            }

            item = container.get(index);

        }

        return item;
    }

    // ----------------------------------------------------------------------------------------------------
    protected static String _getItemAsString(String label, ArrayList<Object> rootContainer, int... indexes) throws GMapException {

        Object item = _getItemAsObject(label, rootContainer, indexes);
        if (!(item instanceof String) && !(item instanceof GNull)) {
            throw new GMapException("[" + label + "] - Item class is not an String" + item.getClass());
        }
        return item instanceof GNull ? null : (String) item;
    }

    // ----------------------------------------------------------------------------------------------------
    protected static String _getItemAsStringDef(String label, String defValue, ArrayList<Object> rootContainer, int... indexes) throws GMapException {

        String str = _getItemAsString(label, rootContainer, indexes);
        return str != null ? str : defValue;
    }

}
