/**
 * 
 */
package gmap.engine.data;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author jzarzuela
 *
 */
public abstract class GStyle {

    // ----------------------------------------------------------------------------------------------------
    /**
     * 
     */
    public GStyle() {
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        printValue(pw, "");
        return sw.getBuffer().toString();
    }

    // ----------------------------------------------------------------------------------------------------
    protected abstract void printValue(PrintWriter pw, String padding);
}
