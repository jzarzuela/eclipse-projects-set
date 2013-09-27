/**
 * 
 */
package com.jzb.ipa.plist;

import java.util.ArrayList;

/**
 * @author  n63636
 */
/**
 * Holder for a binary PList array element.
 */
public class T_PLArray extends T_PLBase {

    ArrayList objectTable;
    int[]     objref;

    public Object getValue(int i) {
        return objectTable.get(objref[i]);
    }

    public int getSize() {
        return objref.length;
    }
    
    @Override
    public String toString() {
        return innerToString("  ");
    }
    
    String innerToString(String padding) {
        
        StringBuffer buf = new StringBuffer("Array{");
        for (int i = 0; i < objref.length; i++) {
            if (i > 0) {
                buf.append(',');
            }
            if (objectTable.size() > objref[i] && objectTable.get(objref[i]) != this) {
                Object value = objectTable.get(objref[i]);
                if (value instanceof T_PLBase) {
                    buf.append(((T_PLBase)value).innerToString(padding));
                } else {
                    buf.append(value);
                }
            } else {
                buf.append("*" + objref[i]);
            }
        }
        buf.append('}');
        return buf.toString();
    }
}