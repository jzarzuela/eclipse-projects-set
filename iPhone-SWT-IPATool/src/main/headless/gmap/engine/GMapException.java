/**
 * 
 */
package gmap.engine;


/**
 * @author jzarzuela
 *
 */
public class GMapException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 7254001888486628166L;

    /**
     * 
     */
    public GMapException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public GMapException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public GMapException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public GMapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public GMapException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
