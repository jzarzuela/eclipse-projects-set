/**
 * 
 */
package com.jzb.jar;

import java.io.File;

/**
 * @author jzarzuela
 * 
 */
public class JARImport {

    private static final String mavenRepoFolderURL = "file:///Users/jzarzuela/Documents/Java/github-repos/my-maven-repos/releases/";

    // ----------------------------------------------------------------------------------------------------
    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        long t1 = 0, t2 = 0;
        try {
            System.out.println("\n***** EXECUTION STARTED *****\n");
            JARImport me = new JARImport();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            t2 = System.currentTimeMillis();
            System.out.println("***** EXECUTION FAILED [" + (t2 - t1) + "]*****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {


        String groupID = "JZB-EXT-JARs";
        String artifactID = "ext.jar.jFuzzyLogic";
        String version = "3.0";

        File jarFile = new File("/Users/jzarzuela/Documents/jFuzzyLogic-3.0.jar");

        _printDeployJar(jarFile, groupID, artifactID, version);
    }

    // ----------------------------------------------------------------------------------------------------
    private void _printDeployJar(File jarFile, String groupID, String artifactID, String version) {

        if (!jarFile.exists()) {
            System.out.println("JAR file doesn't exist: " + jarFile);
            System.exit(0);
        }

        System.out.println();
        System.out.println("mvn deploy:deploy-file -DgroupId=" + groupID + " \\");
        System.out.println("        -DartifactId=" + artifactID + " \\");
        System.out.println("        -Dversion=" + version + " \\");
        System.out.println("        -Dpackaging=jar \\");
        System.out.println("        -Dfile=" + jarFile + " \\");
        System.out.println("        -DrepositoryId=my.mvn.github.repo \\");
        System.out.println("        -Durl=" + mavenRepoFolderURL + "");
    }

    // ----------------------------------------------------------------------------------------------------
    private void _printDeployJarSources(File jarFile, String groupID, String artifactID, String version) {

        _printDeployJar(jarFile, groupID, artifactID, version);
        System.out.println("        -Dclassifier=sources");
    }
}
