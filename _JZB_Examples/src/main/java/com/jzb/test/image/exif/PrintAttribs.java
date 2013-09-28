/**
 * 
 */
package com.jzb.test.image.exif;

import java.io.File;
import java.util.Iterator;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;

/**
 * @author n000013
 * 
 */
public class PrintAttribs {

    final static int EXIF_DATE_TIME = 0x9004;

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
            PrintAttribs me = new PrintAttribs();
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
        File baseFolder = new File("W:\\Irlanda\\Fotos_Irlanda\\Separadas\\Etapa 3\\14-Kerry's Ring-El camino-4");
        for (File file : baseFolder.listFiles()) {
            if (file.getName().toLowerCase().endsWith(".jpg")) {
                _printFileAttrib(file);
            }
        }
    }

    private void _printFileAttrib(File jpegFile) throws Exception {
        System.out.println("----> "+jpegFile.getName());
        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
        ExifSubIFDDirectory dir = metadata.getDirectory(ExifSubIFDDirectory.class);
        Iterator iter=dir.getTags().iterator();
        while(iter.hasNext()) {
            Tag tag = (Tag) iter.next();
            System.out.println(tag.getTagTypeHex()+" "+tag+" "+dir.getObject(tag.getTagType()));
        }
    }

}
