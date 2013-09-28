/**
 * 
 */
package com.jzb.test.bytecode;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

/**
 * @author n63636
 * 
 */
public class JATest2 {

    public static interface ITest {
        public void sayHello();
    };
    
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
            JATest2 me = new JATest2();
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
    public void doIt(String[] args) throws Exception {

        ClassPool pool = ClassPool.getDefault();
        CtClass cc=pool.makeClass("MiTest");
        CtClass ii=pool.makeInterface("com.jzb.ja.JATest2$ITest");
        cc.addInterface(ii);
        CtMethod cm = CtNewMethod.make("public void sayHello(){ System.out.println(\"hello2\"); }",cc);
        cc.addMethod(cm);
        Class c = cc.toClass();
        ITest icc = (ITest)c.newInstance();
        icc.sayHello();
    }
}
