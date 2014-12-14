/**
 * 
 */
package gmap.engine.data;

import java.io.PrintWriter;

/**
 * @author jzarzuela
 *
 */
public class GStylePolygon extends GStyleBase {

    public String color;
    public String alpha;
    public String width;

    public GStylePolygon(String color, String alpha, String width) {
        this.color = color;
        this.alpha = alpha;
        this.width = width;
    }

    protected void printValue(PrintWriter pw, String padding) {
        pw.println(padding + "GStylePolygon { color:" + color + ", alpha:" + alpha + ", width:" + width + "}");
    }

}
