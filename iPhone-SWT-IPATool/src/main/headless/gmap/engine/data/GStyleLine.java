/**
 * 
 */
package gmap.engine.data;

import java.io.PrintWriter;

/**
 * @author jzarzuela
 *
 */
public class GStyleLine extends GStyleBase {

    public String color;
    public String alpha;
    public String width;

    public GStyleLine(String color, String alpha, String width) {
        this.color = color;
        this.alpha = alpha;
        this.width = width;
    }

    protected void printValue(PrintWriter pw, String padding) {
        pw.println(padding + "GStyleLine { color:" + color + ", alpha:" + alpha + ", width:" + width + "}");
    }

}
