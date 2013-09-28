package com.jzb.test.js;

import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * 
 */

/**
 * @author n63636
 * 
 */
public class JSRhino {

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
            JSRhino me = new JSRhino();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }

    final static int LOOP = 1000;

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {
        long t1, t2;

        t1 = System.currentTimeMillis();
        for (int n = 0; n < LOOP; n++)
            jam1();
        t2 = System.currentTimeMillis();
        System.out.println("***** Java Fib FINISHED [" + (t2 - t1) + "]*****");

        t1 = System.currentTimeMillis();
        for (int n = 0; n < LOOP; n++)
            jsm1();
        t2 = System.currentTimeMillis();
        System.out.println("***** JS M1 FINISHED [" + (t2 - t1) + "]*****");

        t1 = System.currentTimeMillis();
        for (int n = 0; n < LOOP; n++)
            jsm2();
        t2 = System.currentTimeMillis();
        System.out.println("***** JS M2 FINISHED [" + (t2 - t1) + "]*****");
    }

    private long jam1() throws Exception {
        long result = fib(20);

        // System.out.println(result);
        return result;

    }

    private long fib(int n) {
        if (n <= 1)
            return n;
        else
            return fib(n - 1) + fib(n - 2);
    }

    private Object jsm1() throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        Bindings bindings = engine.createBindings();
        bindings.put("num", "20");
        Object result = engine.eval("fib(num);" + "function fib(n) {" + "  if(n <= 1) return n; " + "  return fib(n-1) + fib(n-2); " + "};", bindings);
        return result;
        // System.out.println(result);
    }

    Map<String, CompiledScript> m = new HashMap<String, CompiledScript>();

    private Object jsm2() throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        CompiledScript script = m.get("fib");
        if (script == null) {
            Compilable compilingEngine = (Compilable) engine;
            script = compilingEngine.compile("fib(num);" + "function fib(n) {" + "  if(n <= 1) return n; " + "  return fib(n-1) + fib(n-2); " + "};");
            m.put("fib", script);
        }
        Bindings bindings = engine.createBindings();
        bindings.put("num", "20");
        Object result = script.eval(bindings);

        // System.out.println(result);
        return result;
    }

}
