package com.jzb.ttpoi.util;

public class LevenshteinDistance {

    /**
     * Static Main starting method
     * 
     * @param args command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("\n***** EXECUTION STARTED *****\n");
            LevenshteinDistance me = new LevenshteinDistance();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("\n***** EXECUTION FAILED *****\n");
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
    public void doIt(String[] args) throws Exception {
        System.out.println(LevenshteinDistance.compute("hola","adios"));
        System.out.println(LevenshteinDistance.compute("hola","ola"));
        System.out.println(LevenshteinDistance.compute("hola","olla"));
        System.out.println(LevenshteinDistance.compute("hola","hila"));
        System.out.println(LevenshteinDistance.compute("hola","hoal"));
    }
    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    public static int compute(CharSequence str1, CharSequence str2) {
        int[][] distance = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= str2.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= str1.length(); i++)
            for (int j = 1; j <= str2.length(); j++)
                distance[i][j] = minimum(distance[i - 1][j] + 1, distance[i][j - 1] + 1, distance[i - 1][j - 1] + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));

        return distance[str1.length()][str2.length()];
    }
}