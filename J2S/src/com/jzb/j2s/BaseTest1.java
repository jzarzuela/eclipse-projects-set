/**
 * 
 */
package com.jzb.j2s;

import java.util.Date;

/**
 * @author n63636
 * 
 */
public class BaseTest1 {

    public Date method1(String cad) {
        System.out.println("-- MT --> BaseTest1.method1(cad)");
        return null;
    }

    public void method2(String cad) {
        System.out.println("-- MT --> BaseTest1.method2(cad)");
        method3();
        cad.toString();
    }

    private void method3() {
        System.out.println("-- MT --> BaseTest1.method3()");
        System.out.println("");
    }
}
