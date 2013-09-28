/**
 * 
 */
package com.jzb.test.bytecode;

/**
 * @author n63636
 * 
 */
public class KKUtil {

    private double m_arg;

    public KKUtil(double arg) {
        m_arg = arg;
    }

    public double duplicate() {
        return m_arg * m_arg;
    }
}
