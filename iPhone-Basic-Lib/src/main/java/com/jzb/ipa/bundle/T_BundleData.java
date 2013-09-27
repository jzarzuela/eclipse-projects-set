/**
 * 
 */
package com.jzb.ipa.bundle;

import com.dd.plist.NSDictionary;

/**
 * @author n63636
 * 
 */
public class T_BundleData {

    public static enum LEGAL_TYPE {
        
        LEGAL('$'), CRACKED('#'), UNKNOWN('?');

        private char m_letter;

        private LEGAL_TYPE(char letter) {
            m_letter = letter;
        }

        public char getLetter() {
            return m_letter;
        }
    };

    public byte         img[];
    public String       fdate;
    public NSDictionary dict;
    public LEGAL_TYPE   isLegal = LEGAL_TYPE.UNKNOWN;
    
    public boolean wasLegal() {
        return isLegal==LEGAL_TYPE.LEGAL;
    }
}
