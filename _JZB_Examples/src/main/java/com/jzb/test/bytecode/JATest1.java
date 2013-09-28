/**
 * 
 */
package com.jzb.test.bytecode;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * @author n63636
 * 
 */
public class JATest1 {

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            JATest1 me = new JATest1();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt2(String[] args) throws Exception {

        // Se carga la clase para la que se quiere modificar sus bytecodes
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get("com.jzb.ja.Clase1");

        // Se puede iterar imprimiendo los metodos, y su signatura, contenidos en la clase 
        for (CtMethod mm : cc.getMethods()) {
            System.out.println(mm.getName()+" "+mm.getMethodInfo().getDescriptor());
        }
        
        // Se puede buscar un metodo en concreto...
        CtMethod mr=cc.getMethod("diHola","(Ljava/lang/String;)Ljava/lang/String;");
        // ... Borrarlo...
        cc.removeMethod(mr);
        // .. Y volver a escribir su .class moficado en una carpeta raiz
        cc.writeFile("c:\\tmp");

        // Posteriormente, tanto por este codigo de creación dinamica, o porque se usa
        // el fichero .class generado antes, se obtiene un error de "Metodo No Existe"
        Class c1 = cc.toClass();
        Clase1 o1 = (Clase1) c1.newInstance();
        System.out.println(o1.sumaUno(2));
        System.out.println(o1.diHola("Pepe"));

    }
    
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {
        Clase1 o1 = new Clase1();
        System.out.println(o1.sumaUno(2));
        System.out.println(o1.diHola("Pepe"));
    }
    
}
