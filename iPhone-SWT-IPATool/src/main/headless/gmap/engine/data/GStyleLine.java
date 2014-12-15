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

    private String m_alpha;
    private String m_color;
    private String m_width;

    // ----------------------------------------------------------------------------------------------------
    public GStyleLine(String color, String alpha, String width) {
        m_color = color;
        m_alpha = alpha;
        m_width = width;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the alpha
     */
    public String getAlpha() {
        return m_alpha;
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the color
     */
    public String getColor() {
        return m_color;
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the width
     */
    public String getWidth() {
        return m_width;
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @param alpha the alpha to set
     */
    public void setAlpha(String alpha) {
        m_alpha = alpha;
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        m_color = color;
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @param width the width to set
     */
    public void setWidth(String width) {
        m_width = width;
    }

    
    // ----------------------------------------------------------------------------------------------------
    @Override
    protected void printValue(PrintWriter pw, String padding) {
        pw.print(padding + "GStyleLine { color: #" + m_color + ", alpha: " + m_alpha + ", width: " + m_width + "}");
    }

}
