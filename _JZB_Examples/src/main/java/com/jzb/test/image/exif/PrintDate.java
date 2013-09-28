/**
 * 
 */
package com.jzb.test.image.exif;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

/**
 * @author n000013
 * 
 */
public class PrintDate {

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
            PrintDate me = new PrintDate();
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
        // String names[] = { "C:\\JZarzuela\\fotos", "C:\\JZarzuela\\fotos\\Paris", "C:\\JZarzuela\\fotos\\Roma", "C:\\JZarzuela\\fotos\\variadas", "C:\\JZarzuela\\fotos\\busi" };
        String names[] = {  "X:\\Backup_CDS\\Fotos\\Familia\\_Escapadas\\2xxx_10_09-12_Roma\\Filtradas_NO" };
        for (String name : names) {

            TreeSet<String> data = new TreeSet<String>();

            File baseFolder = new File(name);
            _printPhotosDate(baseFolder, data);

            System.out.println();
            System.out.println();
            for (String cad : data) {
                System.out.println(cad);
            }
        }

    }

    private void _printPhotosDate(File baseFolder, TreeSet<String> data) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd-HH:mm:ss");

        File files[] = baseFolder.listFiles();
        for (File jpegFile : files) {

            if (jpegFile.isDirectory()) {
                _printPhotosDate(jpegFile, data);
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
                System.err.println(jpegFile.getAbsolutePath());
                //e.printStackTrace();
                continue;
            }

            String cad = sdf.format(d) + " -- " + jpegFile.getAbsolutePath();
            data.add(cad);
        }

    }

}
