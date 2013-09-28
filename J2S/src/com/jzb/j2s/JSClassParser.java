/**
 * 
 */
package com.jzb.j2s;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author n63636
 * 
 */
public class JSClassParser {

    private static final File s_BASE_PATH = new File("C:\\JZarzuela\\Java\\eclipse-helios-SDK\\dropins\\J2S\\eclipse\\plugins\\net.sf.j2s.lib_2.0.0\\j2slib");

    private static char[]     s_jsBuffer    = new char[1000000];
    private static Pattern    s_mp          = Pattern.compile("(\\$_M)|(\\$_V)|(Clazz\\.defineMethod)|(Clazz\\.overrideMethod)", Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ | Pattern.DOTALL
                                                  | Pattern.MULTILINE);

    public static HashSet<String> parse(String className) throws Exception {

        HashSet<String> list = new HashSet<String>();

        File jsclass = new File(s_BASE_PATH, className.replace('.', '\\')+".js");

        String str = _readJSClass(jsclass);
        Matcher m = s_mp.matcher(str);
        while (m.find()) {
            int p1 = 1 + str.indexOf('"', m.end());
            int p2 = str.indexOf('"', p1 + 1);
            String methodName = str.substring(p1, p2);
            list.add(className + "@" + methodName);
        }

        if (list.size() == 0) {
            if (!(jsclass.getName().contains("Exception") || jsclass.getName().contains("Error")) && !(str.contains("$_I") || str.contains("Clazz.declareInterface"))) {
                System.out.println("NO METHODS FOUND IN: " + jsclass);
            }
        }

        return list;
    }

    private static String _readJSClass(File jsclass) throws Exception {

        String str;
        InputStreamReader isr = new InputStreamReader(new FileInputStream(jsclass), "UTF-8");
        int len = isr.read(s_jsBuffer);
        isr.close();

        if (len != -1) {
            str = new String(s_jsBuffer, 0, len);
        } else {
            str = "";
        }

        return str;

    }

}
