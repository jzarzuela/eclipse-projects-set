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
 * @author n000013
 * 
 */
public class Test1 {

    final static int EXIF_DATE_TIME = 306;

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
            Test1 me = new Test1();
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
            _movePhotos(baseFolder);
        }
    }

    private void _movePhotos(File baseFolder) throws Exception {

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");

        File files[] = baseFolder.listFiles();
        for (File jpegFile : files) {

            if (jpegFile.isDirectory()) {
                _movePhotos(jpegFile);
                continue;
            }

            if (!jpegFile.getName().toLowerCase().endsWith(".jpg"))
                continue;

            Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
            ExifSubIFDDirectory dir = metadata.getDirectory(ExifSubIFDDirectory.class);
            Date d = null;
            try {
                d = dir.getDate(36868);// EXIF_DATE_TIME);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            int w=0,h=0;
            try {
                w=dir.getInt(40962);
                h=dir.getInt(40963);
            }
            catch(Exception e) {
            }

//            File subFolder = new File(baseFolder, sdf1.format(d));
//            if (!subFolder.exists()) {
//                if (!subFolder.mkdirs())
//                    throw new Exception("Error creating folders for: " + subFolder);
//            }

//            File newFile = new File(subFolder, w+"x"+h+"-"+sdf2.format(d)+"-"+jpegFile.getName());
            File newFile = new File(jpegFile.getParentFile(), sdf1.format(d)+"-"+w+"x"+h+"-"+jpegFile.getName());
            if (!jpegFile.renameTo(newFile))
                throw new Exception("Error renaming file to: " + newFile);
            // System.out.println("move \""+jpegFile.getName()+"\" "+subFolder.getName());
        }

    }

}
