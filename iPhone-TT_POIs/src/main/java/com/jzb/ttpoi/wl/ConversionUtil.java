/**
 * 
 */
package com.jzb.ttpoi.wl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import com.jzb.ttpoi.data.TPOIData;

/**
 * @author n63636
 * 
 */
public class ConversionUtil {

    private static CharsetEncoder s_chEncoder = new MyCharSet().newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    private static HashMap<String, String> s_styleCatMap = null;

    public static String getCategoryFromStyle(String style) {

        // Se intenta quedar con el nombre dentro de la URL del icono de GMaps
        if (style == null || style.length() == 0) {
            return TPOIData.UNDEFINED_CATEGORY;
        } else if (style.contains("chst=d_map_pin_letter")) {
            int p1 = style.lastIndexOf("chld=");
            if (p1 > 0) {
                int p2 = style.lastIndexOf("|", p1);
                if (p2 == -1) {
                    p2 = style.length();
                }
                String catName;
                catName = "Pin_Letter_" + style.substring(p1 + 5, p2);
                return catName.replace('|', '_');
            } else {
                return style;
            }
        } else if (style.contains("/kml/paddle")) {
            int p1 = style.lastIndexOf("/");
            if (p1 > 0) {
                int p2 = style.indexOf("_maps", p1);
                if (p2 == -1) {
                    p2 = style.length();
                }
                String catName;
                catName = "Pin_Letter_" + style.substring(p1 + 1, p2);
                return catName;
            } else {
                return style;
            }
        } else if (style.contains("/mapfiles/ms/micons") || style.contains("/mapfiles/ms2/micons") || style.contains("/kml/shapes")) {
            int p1 = style.lastIndexOf('/') + 1;
            if (p1 >= 0) {
                int p2 = style.lastIndexOf('.');
                if (p2 == -1) {
                    p2 = style.length();
                }
                String catName;
                catName = style.substring(p1, p2);
                return catName;
            } else {
                return style;
            }
        } else {
            final String badStr[] = { "\\.", "\\:", "\\;", "\\/", "\\\\", "\\?", "\\&" };
            String catName = style;
            for (String s : badStr) {
                catName = catName.replaceAll(s, "_");
            }
            return catName;
        }

    }

    public static HashMap<String, String> getDefaultParseCategories() {

        if (s_styleCatMap == null) {
            s_styleCatMap = new HashMap<String, String>();
            try {
                Properties props = new Properties();
                props.load(MyCharSet.class.getResourceAsStream("DefaultCategories.properties"));
                for (Entry entry : props.entrySet()) {
                    s_styleCatMap.put(entry.getKey().toString(), entry.getValue().toString());
                }
            } catch (IOException ex) {
                System.err.println("Error (" + ex.getClass().getName() + ") reading values from 'DefaultCategories.properties': " + ex.getMessage());
            }
        }
        return s_styleCatMap;
    }

    public static String getOV2Text(String text) {
        return text.trim();
    }

    public static byte[] getStrANSIValue(String text) throws CharacterCodingException {

        try {
            ByteBuffer bf;
            bf = s_chEncoder.encode(CharBuffer.wrap(text));
            return bf.array();
        } catch (CharacterCodingException ex) {
            System.err.println("Error encoding to ANSI: '" + text + "'");
            throw ex;
        }
    }
}
