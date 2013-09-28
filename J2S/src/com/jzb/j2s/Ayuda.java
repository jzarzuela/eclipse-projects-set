/**
 * 
 */
package com.jzb.j2s;

/**
 * @author n63636
 * 
 */
public class Ayuda {

    public String hola(String str, String... str2) {
        try {
            int[][] arr = new int[1][1];
            arr[0][0] = 5;

            return "hola 4: " + arr[0][3];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return "kk-error";
        }
    }

    public String hola(String str) {
        return "hola 1: " + str;
    }

    public String hola(int n) {
        return "hola 2: " + n;
    }

    public String hola2(Object o) {
        return "hola 3: " + o;
    }
}
