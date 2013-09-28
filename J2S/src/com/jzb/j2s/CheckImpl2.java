/**
 * 
 */
package com.jzb.j2s;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * @author n63636
 * 
 */
public class CheckImpl2 {

    private static HashSet<Class>          classes     = new HashSet<Class>();

    private static HashMap<String, String> s_nameTrans = new HashMap<String, String>();

    static {
        s_nameTrans.put("boolean", "~B");
        s_nameTrans.put("java.lang.Boolean", "Boolean");
        s_nameTrans.put("java.lang.Number", "Number");
        s_nameTrans.put("java.lang.Integer", "Integer");
        s_nameTrans.put("java.lang.Long", "Long");
        s_nameTrans.put("java.lang.Object", "Object");
        s_nameTrans.put("java.lang.String", "String");

    }

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
            CheckImpl2 me = new CheckImpl2();
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
        TreeSet<String> jsMethodList = getJSMethodList();
        TreeSet<String> javaMethodList = getJaveMethodList();

        System.out.println(classes);

        System.out.println();
        System.out.println();
        System.out.println("**** 1.- Java Methods not included in JS");
        for (String jvm : javaMethodList) {
            if (!jsMethodList.contains(jvm)) {
                System.out.println(jvm);
            }
        }

        System.out.println();
        System.out.println();
        System.out.println("**** 2.- JS Methods not included in Java");
        for (String jsm : jsMethodList) {
            if (!javaMethodList.contains(jsm)) {
                System.out.println(jsm);
            }
        }

    }

    private boolean _OverridedMethod(Class clazz, Method m) {

        clazz = clazz.getSuperclass();
        while (clazz != null) {
            try {
                clazz.getDeclaredMethod(m.getName(), m.getParameterTypes());
                return true;
            } catch (Throwable th) {
            }
            clazz = clazz.getSuperclass();
        }
        return false;

    }

    private void getClassMehodList(Class clazz, TreeSet<String> mlist) throws Exception {

        String cname = s_nameTrans.get(clazz.getName());
        if (cname == null)
            cname = clazz.getName();

        for (Method m : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(m.getModifiers()) && !_OverridedMethod(clazz, m)) {
                // if ((Modifier.isPublic(m.getModifiers()) || Modifier.isProtected(m.getModifiers())) && !_OverridedMethod(clazz,m)) {
                String signature = cname + "::" + m.getName() + "[" + getParamsStr(m.getParameterTypes()) + "]";
                mlist.add(signature);
                // System.out.println(signature);
            }
        }

        for (Constructor c : clazz.getDeclaredConstructors()) {
            if (Modifier.isPublic(c.getModifiers()) || Modifier.isProtected(c.getModifiers())) {
                String signature = cname + "::construct[" + getParamsStr(c.getParameterTypes()) + "]";
                mlist.add(signature);
                // System.out.println(signature);
            }
        }
    }

    private TreeSet<String> getJaveMethodList() throws Exception {
        TreeSet<String> methodList = new TreeSet<String>();

        for (Class clazz : classes) {
            getClassMehodList(clazz, methodList);
        }
        return methodList;
    }

    private TreeSet<String> getJSMethodList() throws Exception {

        TreeSet<String> methodList = new TreeSet<String>();

        BufferedReader br = new BufferedReader(new FileReader("C:\\WKSPs\\Consolidado\\J2S\\traces.txt"));
        while (br.ready()) {
            String line = br.readLine();
            int p1 = line.indexOf("Method: ");
            if (p1 > 0) {
                String mn = line.substring(p1 + 8);
                mn = mn.replace("~O", "Object");
                mn = mn.replace("~S", "String");
                mn = mn.replace("$valueOf", "valueOf");

                p1 = mn.indexOf("::");
                String cn = mn.substring(0, p1);
                if (cn.indexOf(".") == -1)
                    cn = "java.lang." + cn;
                if (cn.indexOf(".lang.") > 0) {
                    try {
                        Class clazz = Class.forName(cn);
                        classes.add(clazz);
                        methodList.add(mn);
                    } catch (Throwable th) {
                        System.out.println(cn + " is not a Java class");
                    }
                }
            }
        }
        br.close();

        return methodList;
    }

    private String getParamsStr(Class params[]) {

        String str = "";
        for (Class clazz : params) {
            if (str.length() > 0)
                str += ", ";

            String trans = s_nameTrans.get(clazz.getName());
            if (trans == null)
                trans = clazz.getName();
            str += trans;
        }
        return str;
    }
}
