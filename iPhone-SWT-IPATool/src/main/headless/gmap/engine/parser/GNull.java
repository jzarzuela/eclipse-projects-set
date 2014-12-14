/**
 * 
 */
package gmap.engine.parser;

/**
 * @author jzarzuela
 *
 */
public final class GNull {

    public static final GNull GNULL = new GNull();

    private GNull() {

    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "<null>";
    }
}
