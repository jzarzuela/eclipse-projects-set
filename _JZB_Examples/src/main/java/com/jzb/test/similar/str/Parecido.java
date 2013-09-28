/**
 * 
 */
package com.jzb.test.similar.str;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author n63636
 * 
 */
@SuppressWarnings("unused")
public class Parecido {

    TreeMap<Number, ArrayList<String>> m_data = new TreeMap<Number, ArrayList<String>>();

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
            Parecido me = new Parecido();
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

        String words[] = { "apos", "sopacaldo", "asac", "casa", "sopa", "maleta", "cassa", "cosa", "pie", "arbol", "casona", "supercomplicado" };
        DecimalFormat df = new DecimalFormat("0.0000");

        JaroWinklerDistance jwd = new JaroWinklerDistance(0.7, 4);

        for (int n = 0; n < words.length - 1; n++) {
            for (int i = n + 1; i < words.length; i++) {

                double d1 = jwd.distance(words[n], words[i]);
                if (d1 <= 0.25) {
                    System.out.println(words[n] + " - " + words[i] + " # " + d1);
                }
            }
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

        String words[] = { "apos", "sopacaldo", "asac", "casa", "sopa", "maleta", "cassa", "cosa", "pie", "arbol", "casona", "supercomplicado" };

        JaroWinklerDistance jwd1 = new JaroWinklerDistance();

        for (int n = 0; n < words.length; n++) {
            for (int i = n; i < words.length; i++) {

                Double dist1 = (double) LevenshteinDistance.computeLevenshteinDistance(words[n], words[i]);
                // Double dist2 = (double)jwd.distance(SoundexEng.soundex(words[n]), SoundexEng.soundex(words[i]));
                Double dist3 = jwd1.distance(words[n], words[i]);

                // dist1 -= Math.abs(words[n].length()-words[i].length());

                _getList(dist3).add(words[n] + " - " + words[i]);
                // _getList(dist1*dist3).add(words[n] + " - " + words[i]);
            }
        }

        for (Map.Entry<Number, ArrayList<String>> entry : m_data.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    private ArrayList<String> _getList(Number distance) {
        ArrayList<String> list = m_data.get(distance);
        if (list == null) {
            list = new ArrayList<String>();
            m_data.put(distance, list);
        }
        return list;
    }
}
