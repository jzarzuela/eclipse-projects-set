/**
 * 
 */
package com.jzb.test.re;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author n000013
 * 
 */
public class ReExpr {

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
            ReExpr me = new ReExpr();
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

        File basePath = new File("C:\\JZarzuela\\_Fotos_\\Croacia");
        _renameFiles(basePath);

    }

    private void _renameFiles(File baseFolder) throws Exception {
        System.out.println("Renaming files in: "+baseFolder);
        for (File f : baseFolder.listFiles()) {
            if (f.isDirectory()) {
                _renameFiles(f);
            } else {
                _doRename(f);
            }
        }
    }

    private Pattern m_regExpr = Pattern.compile("(.*)-[A|M]-ISO_[0-9]*\\.jpg", Pattern.CASE_INSENSITIVE);

    private void _doRename(File f) throws Exception {

        String fName = f.getName();
        Matcher matcher = m_regExpr.matcher(fName);
        if (!matcher.find()) {
            System.out.println("* " + f);
            return;
        }

        File newFile = new File(f.getParentFile(),matcher.group(1)+".jpg");
        if(!f.renameTo(newFile)) {
            System.out.println("Error renaming to ("+newFile.getName()+")"+f);
        }
    }

}
