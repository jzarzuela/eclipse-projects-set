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
@SuppressWarnings("unused")
public class AddDate {

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
            AddDate me = new AddDate();
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

        String names[] = { "C:\\JZarzuela\\fotos\\Business\\2008_04_11-13&05_06-10_SFCO_Impact_&_JavaOne\\Organizadas_Copia\\Sausalito" };
        T_OFFSET = -3600000+4*60000+5000;
        
        for (String name : names) {
            File baseFolder = new File(name);
            _addDateToPhotos(baseFolder);
        }
    }

    private long T_OFFSET = 0;

    private void _addDateToPhotos(File baseFolder) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");

        File files[] = baseFolder.listFiles();
        for (File jpegFile : files) {

            if (jpegFile.isDirectory()) {
                _addDateToPhotos(jpegFile);
                continue;
            }

            if (!jpegFile.getName().toLowerCase().endsWith(".jpg"))
                continue;

            if (!jpegFile.getName().contains("_jose_"))
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

            long t = d.getTime() + T_OFFSET;
            d.setTime(t);

            File newFile = new File(jpegFile.getParentFile(), sdf.format(d) + "_jose_" + jpegFile.getName().substring(25));
            System.out.println(newFile.getName());
            if (!jpegFile.renameTo(newFile)) {
                throw new Exception("Error renaming file to: " + newFile);
            }
        }

    }

    private void _addDateToPhotos2(File baseFolder) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");

        File files[] = baseFolder.listFiles();
        for (File jpegFile : files) {

            if (jpegFile.isDirectory()) {
                _addDateToPhotos2(jpegFile);
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

            long t = d.getTime() - 3600000 + 300000;
            d.setTime(t);

            File newFile = new File(jpegFile.getParentFile(), sdf.format(d) + "_jose_" + jpegFile.getName());
            System.out.println(newFile.getName());
            if (!jpegFile.renameTo(newFile)) {
                throw new Exception("Error renaming file to: " + newFile);
            }
        }

    }
}
