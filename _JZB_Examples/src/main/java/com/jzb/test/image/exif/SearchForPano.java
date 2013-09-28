/**
 * 
 */
package com.jzb.test.image.exif;

import java.io.File;
import java.util.HashSet;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

/**
 * @author n000013
 * 
 */
public class SearchForPano {

    final static int EXIF_DATE_TIME     = 0x9004;
    final static int EXIF_EXPOSURE_MODE = 0xa402;

    public class TInfo {

        long _date;
        int  _exposure;
    }

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
            SearchForPano me = new SearchForPano();
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
        String names[] = { "W:\\Irlanda\\Fotos_Irlanda\\Separadas" };
        for (String name : names) {
            File baseFolder = new File(name);
            _processFolder(baseFolder);
        }

    }

    private void _processFolder(File baseFolder) throws Exception {

        HashSet<File> toMove = new HashSet<File>();
        
        File files[] = baseFolder.listFiles();
        
        for (int n = 1; n < files.length-1; n++) {
            
            File jpegFile = files[n];
            if (jpegFile.isDirectory()) {
                
                _processFolder(jpegFile);
                
            } else {

                TInfo cu = _getFileInfo(jpegFile);
                TInfo pr = _getFileInfo(files[n - 1]);
                TInfo ne = _getFileInfo(files[n + 1]);

                // Compare with previous file
                if (pr != null && pr._exposure == cu._exposure && Math.abs(pr._date - cu._date) < 10000) {

                    // Compare with next file
                    if (ne != null && ne._exposure == cu._exposure && Math.abs(ne._date - cu._date) < 10000) {
                        System.out.println(" + " + jpegFile + " " + Math.abs(ne._date - cu._date));
                        
                        toMove.add(files[n-1]);
                        toMove.add(files[n]);
                        toMove.add(files[n+1]);
                        
                        n++;
                        
                    }
                }

            }
        }
        
        for(File jpegfile: toMove) {
            _moveToPano(jpegfile);
        }
    }

    private void _moveToPano(File jpegFile) throws Exception {
        File panoFolder = new File(jpegFile.getParentFile(),"pano");
        
        panoFolder.mkdirs();
        
        File newfile = new File(panoFolder,jpegFile.getName());
        if(!jpegFile.renameTo(newfile)) {
            System.out.println("Error moving to pano folder: "+jpegFile);
            return;
        }
    }
    
    private TInfo _getFileInfo(File jpegFile) throws Exception {

        TInfo info = new TInfo();

        if (!jpegFile.getName().toLowerCase().endsWith(".jpg")) {
            info._date = info._exposure = -1;
            return info;
        }

        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
        ExifSubIFDDirectory dir = metadata.getDirectory(ExifSubIFDDirectory.class);

        try {
            info._date = dir.getDate(EXIF_DATE_TIME).getTime();
        } catch (Exception e) {
            info._date = -1;
        }

        try {
            info._exposure = dir.getInt(EXIF_EXPOSURE_MODE);
        } catch (Exception e) {
            info._exposure = -1;
        }

        return info;

    }
}
