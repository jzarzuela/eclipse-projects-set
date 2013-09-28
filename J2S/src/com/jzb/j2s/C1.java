/**
 * 
 */
package com.jzb.j2s;


/**
 * @author n63636
 *
 */
public abstract class C1 {
    
    public C1() {
        System.out.println("C1 constructor");
    }

    
    /**
     * Static Main starting method
     * 
     * @param args command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            t1 = System.currentTimeMillis();
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
     * @param args command line parameters
     * @throws Exception if something fails during the execution
     */
    public String doMsg() throws Exception {
        return ("Esto está en la abstracta de la base");
    }
}
