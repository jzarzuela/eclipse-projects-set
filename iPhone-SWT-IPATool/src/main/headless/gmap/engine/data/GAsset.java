/**
 * 
 */
package gmap.engine.data;

import gmap.engine.GMapException;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author jzarzuela
 *
 */
public abstract class GAsset {

    // ----
    public String GID;

    public GAsset(String gid) {
        if (gid == null || gid.trim().length() == 0) {
            throw new RuntimeException("Asset GID can't be null or empty");
        }
        this.GID = gid;
    }

    public void assertParsing() throws GMapException {

    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        printValue(pw, "");
        return sw.toString();
    }

    protected abstract void printValue(PrintWriter pw, String padding);


}
