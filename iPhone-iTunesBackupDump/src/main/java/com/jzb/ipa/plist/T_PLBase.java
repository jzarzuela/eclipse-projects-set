/**
 * 
 */
package com.jzb.ipa.plist;


/**
 * @author n63636
 *
 */
public abstract class T_PLBase {
    
    @Override
    public String toString() {
        return innerToString("  ");
    }
    
    abstract String innerToString(String padding);
    
}
