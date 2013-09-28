/**
 * 
 */
package com.jzb.test.image.exif;

import java.io.File;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;


/**
 * @author n000013
 * 
 */
public class PrintManualImgs {

    final static int EXIF_Exposure_Program = 0x8822;

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
            PrintManualImgs me = new PrintManualImgs();
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
        File baseFolder = new File("C:\\JZarzuela\\fotos\\Eslovenia\\NO_Descartadas\\01-Ljubljana");
        for (File file : baseFolder.listFiles()) {
            if (file.getName().toLowerCase().endsWith(".jpg")) {
                _printFileAttrib(file);
            }
        }
    }

    private void _printFileAttrib(File jpegFile) throws Exception {

        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
        ExifSubIFDDirectory dir = metadata.getDirectory(ExifSubIFDDirectory.class);
        int value=dir.getInt(EXIF_Exposure_Program);
        if(value!=2) {
            System.out.printf("move \"%s\" ..\\manual \n",jpegFile);
        }
    }

}
