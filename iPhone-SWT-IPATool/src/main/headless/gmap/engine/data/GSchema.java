/**
 * 
 */
package gmap.engine.data;

import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author jzarzuela
 *
 */
public class GSchema {

    public GTable                               ownerTable;
    public LinkedHashMap<String, GPropertyType> propertyDefs = new LinkedHashMap<String, GPropertyType>();
    
    public GSchema(GTable ownerTable) {
        if (ownerTable == null) {
            throw new RuntimeException("Schema's ownerTable can't be null");
        }
        this.ownerTable = ownerTable;
        ownerTable.schema = this;
    }

    public void addPropertyDef(String propName, GPropertyType type) {
        propertyDefs.put(propName, type);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GSchema: " + propertyDefs;
    }

    protected void printValue(PrintWriter pw, String padding) {

        pw.println(padding + "GSchema {");
        for (Map.Entry<String, GPropertyType> entry : propertyDefs.entrySet()) {
            pw.println(padding + "  '" + entry.getKey()+ "' : '" + entry.getValue()+ "'");
        }
        pw.println(padding + "}");
    }

}
