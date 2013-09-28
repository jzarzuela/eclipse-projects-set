/**
 * 
 */
package com.jzb.test.bytecode;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;

/**
 * @author n63636
 * 
 */
public class JAMain {

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
            JAMain me = new JAMain();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
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

        ClassPool cp = ClassPool.getDefault();

        cp.importPackage("ja");

        CtClass ctc = cp.makeClass("TestJA");

        CtClass ctI = cp.makeInterface("com.jzb.test.bytecode.KKInt");

        ctc.addInterface(ctI);

        //@formatter:off
        String methodBody = "public double eval (double x) { " 
                + "  double base;" 
                + "  com.jzb.test.bytecode.KKUtil util = new com.jzb.test.bytecode.KKUtil(x);" 
                + "  base = util.duplicate();" 
                + "  double cosa[] = new double[10];"
                + "  for(int n=0;n<10;n++) {" 
                + "    cosa[n]=base+n;" 
                + "  }" 
                + "  for(int n=0;n<cosa.length;n++) {" 
                + "    base+=cosa[n];" + "  }" 
                + "  int n=(int)(Math.random()*10);"
                + "  return base+n;" + "}";
        //@formatter:on

        ctc.addMethod(CtNewMethod.make(methodBody, ctc));

        Class clazz = ctc.toClass();
        Object obj = clazz.newInstance();
        System.out.println(obj);

        KKInt ikk = (KKInt) clazz.newInstance();
        double x = ikk.eval(12);
        System.out.println(x);

    }

    public double eval(double x) {

        double base;

        KKUtil util = new KKUtil(x);

        base = util.duplicate();
        double cosa[] = new double[10];
        for (int n = 0; n < 10; n++) {
            cosa[n] = base + n;
        }

        for (double dd : cosa) {
            base += dd;
        }
        int n = (int) (Math.random() * 10);
        return base + n;
    }

}
