/**
 * 
 */
package com.jzb.ipa.plist;

import java.util.ArrayList;

/**
 * @author n63636
 */
/**
 * Holder for a binary PList dict element.
 */
public class T_PLDict extends T_PLBase {

    ArrayList objectTable;
    int[]     keyref;
    int[]     objref;

    public int getSize() {
        return keyref.length;
    }

    public String getKey(int i) {
        return objectTable.get(keyref[i]).toString();
    }

    public Object getValue(int i) {
        return objectTable.get(objref[i]);
    }

    public ArrayList<String> getKeys() {
        ArrayList<String> keys = new ArrayList<String>(); 
        for (int n = 0; n < keyref.length; n++) {
            keys.add(objectTable.get(keyref[n]).toString());
        }
        return keys;
    }
    
    public String getStrValue(String key) {
        int p1 = key.indexOf('/');
        if (p1 > 0) {
            String key1 = key.substring(0, p1);
            T_PLDict dict = (T_PLDict) _getValue(key1);
            if (dict != null)
                return dict.getStrValue(key.substring(p1 + 1));
            else
                return null;
        } else {
            return _getStrValue(key);
        }
    }

    private String _getStrValue(String key) {

        for (int n = 0; n < keyref.length; n++) {
            if (objectTable.get(keyref[n]).toString().equalsIgnoreCase(key)) {
                return objectTable.get(objref[n]).toString();
            }
        }
        return null;
    }

    public Object getValue(String key) {

        int p1 = key.indexOf('/');
        if (p1 > 0) {
            String key1 = key.substring(0, p1);
            T_PLDict dict = (T_PLDict) _getValue(key1);
            if (dict != null)
                return dict.getValue(key.substring(p1 + 1));
            else
                return null;
        } else {
            return _getValue(key);
        }
    }

    private Object _getValue(String key) {
        for (int n = 0; n < keyref.length; n++) {
            if (objectTable.get(keyref[n]).toString().equalsIgnoreCase(key)) {
                return objectTable.get(objref[n]);
            }
        }
        return null;
    }

    String innerToString(String padding) {

        StringBuffer buf = new StringBuffer("BPLDict{\n");

        String padding2 = padding + padding;

        for (int i = 0; i < keyref.length; i++) {

            if (i > 0) {
                buf.append(",\n");
            }

            buf.append(padding2);

            if (keyref[i] < 0 || keyref[i] >= objectTable.size()) {
                buf.append("#" + keyref[i]);
            } else if (objectTable.get(keyref[i]) == this) {
                buf.append("*" + keyref[i]);
            } else {
                buf.append(objectTable.get(keyref[i]));
                // buf.append(keyref[i]);
            }

            buf.append(":");
            if (objref[i] < 0 || objref[i] >= objectTable.size()) {
                buf.append("#" + objref[i]);
            } else if (objectTable.get(objref[i]) == this) {
                buf.append("*" + objref[i]);
            } else {
                Object value = objectTable.get(objref[i]);
                if (value instanceof T_PLBase) {
                    buf.append(((T_PLBase) value).innerToString(padding2));
                } else {
                    buf.append(value);
                }
                // buf.append(objref[i]);
            }
        }
        buf.append("\n" + padding + "}");
        return buf.toString();
    }
}