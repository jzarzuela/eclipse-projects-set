/**
 * 
 */
package com.jzb.test.image.exif;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("unused")
public class KK1 {

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
            KK1 me = new KK1();
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

        //String names[] = { "C:\\JZarzuela\\fotos\\Amsterdam", "C:\\JZarzuela\\fotos\\Paris", "C:\\JZarzuela\\fotos\\Roma", "C:\\JZarzuela\\fotos\\variadas", "C:\\JZarzuela\\fotos\\busi" };
        String names[] = { "" };
        for (String name : names) {
            File baseFolder = new File(name);
            _do2(baseFolder);
        }

    }

    private Pattern regEx = Pattern.compile("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]", Pattern.CASE_INSENSITIVE);

    private void _do2(File folder) throws Exception {

        System.out.println("Processing: "+folder);
        
        boolean dateFolder = regEx.matcher(folder.getName()).matches();
        boolean dateParent = regEx.matcher(folder.getParentFile().getName()).matches();

        File files[] = folder.listFiles();

        for (File afile : files) {

            if (afile.isDirectory()) {
                _do2(afile);
                if (regEx.matcher(afile.getName()).matches()) {
                    System.out.println("Deleting folder:" +afile);
                    if (!afile.delete()) {
                        System.out.println("** Couldn't delete file: " + afile);
                    }
                }
            }

            if (afile.isFile() && dateFolder) {
                System.out.println("Moving file: "+afile);
                File newFile = new File(folder.getParentFile(), afile.getName());
                if (!afile.renameTo(newFile)) {
                    System.out.println("** Couldn't move file: " + afile);
                }
            }
        }
    }
}
