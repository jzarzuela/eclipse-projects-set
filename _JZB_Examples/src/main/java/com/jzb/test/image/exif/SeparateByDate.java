/**
 * 
 */
package com.jzb.test.image.exif;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

/**
 * @author n63636
 * 
 */
public class SeparateByDate {

    final static int EXIF_DATE_TIME = 36868;

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
            SeparateByDate me = new SeparateByDate();
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

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd-HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");

        File baseFolder = new File("C:\\Documents and Settings\\n63636\\My Documents\\_mis fotos\\Croacia\\Selecionadas");

        File files[] = baseFolder.listFiles();
        for (File jpegFile : files) {

            if (jpegFile.isDirectory())
                continue;

            if (!jpegFile.getName().toLowerCase().endsWith(".jpg"))
                continue;

            Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
            ExifSubIFDDirectory dir = metadata.getDirectory(ExifSubIFDDirectory.class);
            Date d = null;
            try {
                d = dir.getDate(EXIF_DATE_TIME);
                File newFile = new File(jpegFile.getParentFile(),sdf.format(d)+File.separator+jpegFile.getName());
                System.out.print("Moving '"+newFile.getName()+"' to '"+newFile.getParentFile().getName()+"'");
                newFile.getParentFile().mkdirs();
                if(jpegFile.renameTo(newFile)) {
                    System.out.println();
                }
                else {
                    System.out.println("    ERROR!!!!");
                }
            } catch (Exception e) {
                System.err.println(jpegFile.getAbsolutePath());
                // e.printStackTrace();
                continue;
            }

        }

    }

}
