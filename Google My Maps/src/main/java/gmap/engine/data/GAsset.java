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
public abstract class GAsset {

    public String m_gid;

    // ----------------------------------------------------------------------------------------------------
    public GAsset(String gid) {
        if (gid == null || gid.trim().length() == 0) {
            throw new RuntimeException("Asset GID can't be null or empty");
        }
        m_gid = gid;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the gid
     */
    public String getGID() {
        return m_gid;
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
        return sw.toString();
    }

    // ----------------------------------------------------------------------------------------------------
    protected abstract void printValue(PrintWriter pw, String padding);

}
