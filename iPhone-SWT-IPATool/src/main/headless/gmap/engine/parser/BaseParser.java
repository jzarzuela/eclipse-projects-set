/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;

import java.util.ArrayList;

/**
 * @author jzarzuela
 *
 */
public abstract class BaseParser {

    // ----------------------------------------------------------------------------------------------------
    protected static void _checkItemArraySize(String label, int size, ArrayList<Object> rootContainer, int... indexes) throws GMapException {

        ArrayList<Object> a = _getItemAsArray(label, rootContainer, indexes);
        if ((size < 0 && a != null) || (size >= 0 && a == null) || size != a.size()) {
            throw new GMapException("["+label+"] - ArrayList value should have size of '" + size + "' an it has '" + (a != null ? a.size() : -1) + "'");
        }
    }

    // ----------------------------------------------------------------------------------------------------
    protected static void _checkItemNullValue(String label, ArrayList<Object> rootContainer, int... indexes) throws GMapException {

        Object o = _getItemAsObject(label, rootContainer, indexes);
        if (!(o instanceof GNull)) {
            throw new GMapException("["+label+"] - Value should be 'GNull' an it was '" + o + "'");
        }
    }

    // ----------------------------------------------------------------------------------------------------
    protected static void _checkItemStringValue(String label, String value, ArrayList<Object> rootContainer, int... indexes) throws GMapException {

        String s = _getItemAsString(label, rootContainer, indexes);
        if ((value == null && s != null) || (value != null && s == null) || !value.equals(s)) {
            throw new GMapException("["+label+"] - String value should be '" + value + "' an it was '" + s + "'");
        }
    }

    // ----------------------------------------------------------------------------------------------------
    protected static ArrayList<Object> _getItemAsArray(String label, ArrayList<Object> rootContainer, int... indexes) throws GMapException {

        Object item = _getItemAsObject(label, rootContainer, indexes);
        if (!(item instanceof ArrayList) && !(item instanceof GNull)) {
            throw new GMapException("["+label+"] - Item class is not an ArrayList" + item.getClass());
        }
        return item instanceof GNull ? null : (ArrayList<Object>) item;

    }

    // ----------------------------------------------------------------------------------------------------
    protected static Object _getItemAsObject(String label, ArrayList<Object> rootContainer, int... indexes) throws GMapException {

        Object item = rootContainer;

        for (int index : indexes) {

            if (!(item instanceof ArrayList)) {
                throw new GMapException("["+label+"] - Can't search an item inside somethig that is not an ArrayList" + item.getClass());
            }

            ArrayList<Object> container = (ArrayList<Object>) item;

            if (index >= container.size()) {
                throw new GMapException("["+label+"] - Incorrect container Array size (" + container.size() + ") while accessin item at index: " + index);
            }

            item = container.get(index);

        }

        return item;
    }

    // ----------------------------------------------------------------------------------------------------
    protected static String _getItemAsString(String label, ArrayList<Object> rootContainer, int... indexes) throws GMapException {

        Object item = _getItemAsObject(label, rootContainer, indexes);
        if (!(item instanceof String) && !(item instanceof GNull)) {
            throw new GMapException("["+label+"] - Item class is not an String" + item.getClass());
        }
        return item instanceof GNull ? null : (String) item;
    }

}
