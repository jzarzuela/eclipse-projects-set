/**
 * 
 */
package com.jzb.test.bytecode;

/**
 * @author n63636
 * 
 */
public class Clase1 {

    public Clase1() {
    }
    
    public int sumaUno(int valor) {
        return valor + 1;
    }
    
    public String diHola(String nombre) {
        return "hola "+nombre;
    }
    
    public String OtroHola() {
        return diHola("desconocido");
    }
}
