/**
 * 
 */
package com.jzb.j2s;

import java.io.File;
import java.lang.reflect.Modifier;

/**
 * @author n63636
 * 
 */
public class CheckImpl {

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
            CheckImpl me = new CheckImpl();
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

        File javaFolder = new File("C:\\Users\\n63636\\Desktop\\j2s\\jdk-1_5_0_22-src");
        File jsFolder = new File("C:\\JZarzuela\\Java\\eclipse-helios-SDK\\dropins\\J2S\\eclipse\\plugins\\net.sf.j2s.lib_2.0.0\\j2slib");

        iterateJavaFolder(javaFolder, jsFolder);
    }

    private void iterateJavaFolder(File javaFolder, File jsFolder) throws Exception {

        for (File jf : javaFolder.listFiles()) {
            if (jf.isDirectory()) {
                File jsSubFolder = new File(jsFolder, jf.getName());
                iterateJavaFolder(jf, jsSubFolder);
            }
        }

        if (_isJavaPkg(javaFolder)) {
            _checkPkg(javaFolder, jsFolder);
        }

    }

    private void _checkPkg(File javaFolder, File jsFolder) throws Exception {

        if (!jsFolder.exists()) {
            System.out.println("Paquete no implementado: " + javaFolder);
            return;
        }

        for (File jf : javaFolder.listFiles()) {
            if (jf.isFile() && jf.getName().toLowerCase().endsWith(".java")) {

                if (_checkClass(jf)) {
                    File jsFile = new File(jsFolder, jf.getName().substring(0, jf.getName().length() - 4) + "js");
                    if (!jsFile.exists()) {
                        System.out.println("Clase no implementada: " + jf);
                    }
                }

            }
        }
    }

    static final int s_javaFolderLen = (int) new File("C:\\Users\\n63636\\Desktop\\j2s\\jdk-1_5_0_22-src").getAbsolutePath().length();

    private boolean _checkClass(File classFile) {

        try {
            String className = classFile.getAbsolutePath().substring(s_javaFolderLen + 1).replace('\\', '.');
            className = className.substring(0, className.length() - 5);
            Class clazz = Class.forName(className);

            if (clazz.isAnnotation()) {
                return false;
            }
            if (Modifier.isPublic(clazz.getModifiers())) {
                return true;
            }

            return false;

        } catch (Exception ex) {
            return true;
        }

    }

    private boolean _isJavaPkg(File javaFolder) throws Exception {
        for (File jf : javaFolder.listFiles()) {
            if (jf.isFile() && jf.getName().endsWith(".java")) {
                return true;
            }
        }
        return false;
    }

}

// http://j2s.sourceforge.net/
// http://j2s.sourceforge.net/document.html
// http://j2s.sourceforge.net/overview.html
// http://j2s.sourceforge.net/articles/getting-started.html
// http://j2s.sourceforge.net/articles/oop-in-js-by-j2s.html

// http://www.google.es/#sclient=psy&hl=es&source=hp&q=%2Bjava2script+%2Bgwt&aq=f&aqi=&aql=&oq=&pbx=1&bav=on.2,or.r_gc.r_pw.&fp=14c07edcde9baa56&biw=1012&bih=547
// http://swik.net/java2script/Java2Script+Pacemaker+Blog

