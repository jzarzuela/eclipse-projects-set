/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GMap;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 *
 */
public class GMapServiceParser extends BaseParser {

    // ----------------------------------------------------------------------------------------------------
    public static GMap parseMapDataJson(String mapdataJson) throws GMapException {

        // Pasa la cadena de texto a un conjunto de arrays de arrays y strings que el resto de parser entienden
        ArrayList<Object> rootContainer = new ArrayList<Object>();
        _parseStrToArrays(mapdataJson, 0, rootContainer);

        _dumpArrayMapData(mapdataJson, rootContainer);

        // Parsea el mapa y todas las entidades asociadas
        GMap map = __parseArrayInfo(rootContainer);

        return map;
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _dumpArrayMapData(String mapdataJson, ArrayList<Object> rootContainer) {

        String mapName = "unknownName";
        String mapID = "unknownID";

        try {
            mapName = _getItemAsString("mapName", rootContainer, 0, 0, 1);
        } catch (Throwable th) {
            Tracer._warn("_dumpArrayMapData: Unknown Map Name");
        }

        try {
            mapID = _getItemAsString("mapID", rootContainer, 0, 0, 0);
        } catch (Throwable th) {
            Tracer._warn("_dumpArrayMapData: Unknown Map ID");
        }

        String value = _prettyPrintArrays(rootContainer);

        File mapDataFile = new File(System.getProperty("user.home") + "/gmap/map_array_data/" + mapName + "_" + mapID + ".txt");
        mapDataFile.getParentFile().mkdirs();

        try (PrintWriter pw = new PrintWriter(mapDataFile)) {

            pw.println(value.toString());

        } catch (Throwable th) {
            Tracer._warn("Error saving file with cached map json response", th);
            mapDataFile.delete();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static GMap __parseArrayInfo(ArrayList<Object> rootContainer) throws GMapException {

        ParserContext ctx = new ParserContext();

        // El array principal solo tiene un elemento con toda la informacion en forma de mas arrays
        ArrayList<Object> container = _getItemAsArray("global.rootArray", rootContainer, 0);

        //
        // Parsea la informacion del mapa, que esta en el array con indice [0,0]
        ArrayList<Object> mapInfoArray = _getItemAsArray("global.mapInfoArray", container, 0);
        GMap ownerMap = GMapParser.parseMapInfoArray(ctx, mapInfoArray);

        //
        // Parsea la informacion de las capas, que esta en el array con indice [0,0]
        ArrayList<Object> allLayersInfoArray = _getItemAsArray("global.allLayersInfoArray", container, 1);
        GLayerParser.parseAllLayersInfoArray(ctx, allLayersInfoArray);

        // Parsea la informacion de los styles de cada Capa [0,3]
        ArrayList<Object> allStylesInfoArray = _getItemAsArray("global.allStylesInfoArray", container, 3);
        GStyleParser.parseAllStylesInfoArray(ctx, allStylesInfoArray);

        //
        // Parsea la informacion basica de los Tables [0,2]
        ArrayList<Object> allTablesInfoArray = _getItemAsArray("global.allTablesInfoArray", container, 2);
        GTableParser.parseAllTablesInfoArray(ctx, allTablesInfoArray);

        //
        // Parsea la informacion de los features de cada Table [0,4]
        ArrayList<Object> allFeaturesInfoArray = _getItemAsArray("global.allFeaturesInfoArray", container, 4);
        GFeatureParser.parseAllFeaturesInfoArray(ctx, allFeaturesInfoArray);

        //
        // ----------------------------------------------------------------------------------------------------------------
        // Validaciones sobre campos que no sabemos lo que son para detectar cambios
        _checkItemStringValue("global.unknown.5", "1", container, 5);
        _checkItemNullValue("global.unknown.6", container, 6);
        _checkItemStringValue("global.unknown.8", "1", container, 8);
        _checkItemStringValue("global.unknown.9", "0", container, 9);

        _getItemAsString("global.unknown.7", container, 7); // '0' - '1' ???

        return ownerMap;
    }

    // ----------------------------------------------------------------------------------------------------
    private static int _parseStrToArrays(String str, int index, ArrayList<Object> parentContainer) throws GMapException {

        StringBuilder value = new StringBuilder();

        int lastSlashCount = 0;
        boolean insideString = false;
        boolean lastWasArray = false;

        while (index < str.length()) {

            char c = str.charAt(index++);
            if (c == '\\') {
                lastSlashCount++;
                continue;
            }

            if (lastSlashCount > 0) {
                switch (c) {
                    case 'n':
                        c = '\n';
                        break;
                    case 't':
                        c = '\t';
                        break;
                    case '(':
                        c = '(';
                        break;
                    case ')':
                        c = ')';
                        break;
                    case '"':
                        if (lastSlashCount > 1) {
                            c = '\'';
                        }
                        break;
                    case 'u':
                        c = (char) Integer.parseInt(str.substring(index, index + 4), 16);
                        index += 4;
                        break;
                    default:
                        throw new GMapException("Unexpected char '" + c + "'");
                }
            }

            lastSlashCount = 0;

            if (c == '\"') {
                insideString = !insideString;
                continue;
            }

            if (insideString) {

                value.append(c);

            } else {

                switch (c) {

                    case '[':
                        ArrayList<Object> container = new ArrayList<Object>();
                        index = _parseStrToArrays(str, index, container);
                        parentContainer.add(container);
                        lastWasArray = true;
                        break;

                    case ']':
                        if (!lastWasArray) {
                            if (value.length() == 0) {
                                parentContainer.add(GNull.GNULL);
                            } else {
                                parentContainer.add(value.toString().trim());
                            }
                        }
                        return index;

                    case ',':
                        if (!lastWasArray) {
                            if (value.length() == 0) {
                                parentContainer.add(GNull.GNULL);
                            } else {
                                parentContainer.add(value.toString().trim());
                            }
                        }
                        value.setLength(0);
                        lastWasArray = false;
                        break;

                    case '\n':
                        break;

                    default:
                        value.append(c);
                        break;
                }
            }

        }

        return index;
    }

    // ----------------------------------------------------------------------------------------------------
    //@SuppressWarnings("unused")
    private static String _prettyPrintArrays(ArrayList<Object> container) {

        StringBuilder sb = new StringBuilder();
        _prettyPrintArrays2(sb, "", container);
        return sb.toString();
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _prettyPrintArrays2(StringBuilder sb, String padding, ArrayList<Object> container) {

        sb.append("\n").append(padding).append("[");
        for (int n = 0; n < container.size(); n++) {
            Object obj = container.get(n);
            if (obj instanceof ArrayList) {
                _prettyPrintArrays2(sb, padding + "  ", (ArrayList<Object>) obj);
                if (n < container.size() - 1) {
                    sb.append(", ");
                }
            } else {
                sb.append(obj.toString());
                if (n < container.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append("\n").append(padding).append("]");
    }
}
