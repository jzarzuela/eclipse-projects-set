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

    private String m_borderColor;
    private String m_borderWidth;
    private String m_fillAlpha;
    private String m_fillColor;

    // ----------------------------------------------------------------------------------------------------
    public GStylePolygon(String borderColor, String borderWidth, String fillColor, String fillAlpha) {
        m_borderColor = borderColor;
        m_borderWidth = borderWidth;
        m_fillColor = fillColor;
        m_fillAlpha = fillAlpha;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the borderColor
     */
    public String getBorderColor() {
        return m_borderColor;
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the borderWidth
     */
    public String getBorderWidth() {
        return m_borderWidth;
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the fillAlpha
     */
    public String getFillAlpha() {
        return m_fillAlpha;
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the fillColor
     */
    public String getFillColor() {
        return m_fillColor;
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @param borderColor the borderColor to set
     */
    public void setBorderColor(String borderColor) {
        m_borderColor = borderColor;
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @param borderWidth the borderWidth to set
     */
    public void setBorderWidth(String borderWidth) {
        m_borderWidth = borderWidth;
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @param fillAlpha the fillAlpha to set
     */
    public void setFillAlpha(String fillAlpha) {
        m_fillAlpha = fillAlpha;
    }

    
    // ----------------------------------------------------------------------------------------------------
    /**
     * @param fillColor the fillColor to set
     */
    public void setFillColor(String fillColor) {
        m_fillColor = fillColor;
    }

    
    // ----------------------------------------------------------------------------------------------------
    @Override
    protected void printValue(PrintWriter pw, String padding) {
        pw.print(padding + "GStylePolygon { borderColor:" + m_borderColor + ", borderWidth:" + m_borderWidth + ", fillColor:" + m_fillColor + ", fillAlpha:" + m_fillAlpha + "}");
    }

}
