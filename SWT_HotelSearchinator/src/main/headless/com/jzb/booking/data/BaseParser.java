/**
 * 
 */
package com.jzb.booking.data;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public abstract class BaseParser {

    protected String  m_baseURL;
    protected boolean m_spanishNumbers;

    // ----------------------------------------------------------------------------------------------------
    public BaseParser(String baseURL, boolean spanishNumbers) {
        m_baseURL = baseURL;
        m_spanishNumbers = spanishNumbers;
    }

    // ----------------------------------------------------------------------------------------------------
    protected String _cleanNonDigits(String strValue, boolean changeDotComma) {

        StringBuffer sb = new StringBuffer();
        for (char c : strValue.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }

            if (!changeDotComma) {
                if (c == '.')
                    sb.append(c);
            } else {
                if (c == ',') {
                    if (m_spanishNumbers) {
                        sb.append('.');
                    } else {
                        // lo omite
                    }
                }

                if (c == '.') {
                    if (m_spanishNumbers) {
                        // lo omite
                    } else {
                        sb.append('.');
                    }
                }
            }
        }
        return sb.toString();
    }

    // ----------------------------------------------------------------------------------------------------
    protected double _getXPathValueCurrency(DomNode node, String label, String xquery, String defValue, String attrName) throws Exception {

        String value = null;
        try {
            value = _getXPathValueStr(node, label, xquery, defValue, attrName);
            // Limpia la cadea
            value = _cleanNonDigits(value, true);
            double doubleValue = Double.parseDouble(value);
            return doubleValue;
        } catch (Throwable th) {
            Tracer._warn(node.asXml());
            throw new Exception("Error parsing room info (" + value + ") as Double: " + label);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    protected double _getXPathValueDouble(DomNode node, String label, String xquery, String defValue, String attrName) throws Exception {

        String value = null;
        try {
            value = _getXPathValueStr(node, label, xquery, defValue, attrName);
            // Limpia la cadea
            value = _cleanNonDigits(value, false);
            double doubleValue = Double.parseDouble(value);
            return doubleValue;
        } catch (Throwable th) {
            Tracer._warn(node.asXml());
            throw new Exception("Error parsing room info (" + value + ") as Double: " + label);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    protected int _getXPathValueInt(DomNode node, String label, String xquery, String defValue, String attrName) throws Exception {

        String value = _getXPathValueStr(node, label, xquery, defValue, attrName);
        return _parseIntValue(value, label);
    }

    // ----------------------------------------------------------------------------------------------------
    protected int _parseIntValue(String value, String label) {
        try {
            // Limpia la cadea
            value = _cleanNonDigits(value, false);
            int intValue = Integer.parseInt(value);
            return intValue;
        } catch (Throwable th) {
            Tracer._warn("Error parsing room info (" + value + ") as Integer: " + label);
            return 0;
        }
    }

    // ----------------------------------------------------------------------------------------------------
    protected String _getXPathValueStr(DomNode node, String label, String xquery, String defValue, String attrName) throws Exception {

        List<HtmlElement> dataList = (List<HtmlElement>) node.getByXPath(xquery);
        if (dataList == null || dataList.size() == 0) {
            if (defValue != null) {
                return defValue;
            } else {
                Tracer._error("Error parsing room info: " + label);
                Tracer._error("----------------------------------------------------------------------");
                Tracer._error(node.asXml());
                Tracer._error("----------------------------------------------------------------------");
                throw new Exception("Error parsing room info: " + label);
            }
        }

        if (dataList.size() == 1) {
            String value;
            if (attrName != null) {
                value = dataList.get(0).getAttribute(attrName);
            } else {
                value = dataList.get(0).asText();
            }
            return value;
        } else {
            Tracer._warn(node.asXml());
            throw new Exception("Error parsing room info: " + label);
        }
    }
}
