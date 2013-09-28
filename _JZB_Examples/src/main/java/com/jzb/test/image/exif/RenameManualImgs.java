/**
 * 
 */
package com.jzb.test.image.exif;

import java.io.File;
import java.util.Iterator;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("unused")
public class RenameManualImgs {

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
            RenameManualImgs me = new RenameManualImgs();
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
        File baseFolder = new File("C:\\Documents and Settings\\n63636\\Desktop\\ParqueEuropa");
        _processFolder(baseFolder);
    }

    private void _processFolder(File folder) throws Exception {

        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                _processFolder(file);
            } else {
                if (file.getName().toLowerCase().endsWith(".jpg")) {
                    _printFileAttrib(file);
                }
            }

        }
    }

    private void _printFileAttrib(File jpegFile) throws Exception {

        
        int p1=jpegFile.getName().indexOf('-');
        String justName = jpegFile.getName().substring(0,p1);
        
        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
        ExifSubIFDDirectory dir = metadata.getDirectory(ExifSubIFDDirectory.class);
        //_printDIR(dir);
        
        int FocalLen = dir.getInt(37386);
        
        
        String expTime = dir.getDescription(33434);
        String fNumber = dir.getDescription(33437);

        int iso = dir.getInt(34855);
        File newFile;
        
        String newName = justName+"--"+FocalLen+"--"+fNumber+"--"+expTime+"--ISO_"+iso+".jpg";
        newName = newName.replace('/','=');
        newFile=new File(jpegFile.getParentFile(),newName);
        
        System.out.println("Renaming file to: "+newFile);
        if(!jpegFile.renameTo(newFile)) {
            System.out.println("  ** ERROR renaming file **");
        }
    }
    
    private void _printFileAttrib2(File jpegFile) throws Exception {

        
        int p1=jpegFile.getName().lastIndexOf('.');
        String justName = jpegFile.getName().substring(0,p1);
        
        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
        ExifSubIFDDirectory dir = metadata.getDirectory(ExifSubIFDDirectory.class);
//        _printDIR(dir);
        
        int FocalLen = dir.getInt(37386);

        int value = dir.getInt(37386);
        int iso = dir.getInt(34855);
        File newFile;
        if (value != 0) {
            newFile=new File(jpegFile.getParentFile(),FocalLen+"-"+justName+"-M-ISO_"+iso+".jpg");
        }
        else {
            newFile=new File(jpegFile.getParentFile(),FocalLen+"-"+justName+"-A-ISO_"+iso+".jpg");
        }
        
        System.out.println("Renaming file to: "+newFile);
//        if(!jpegFile.renameTo(newFile)) {
//            System.out.println("  ** ERROR renaming file **");
//        }
    }

    private void _printDIR(Directory dir) throws Exception {
        System.out.println("***************************************************************");
        Iterator iter=dir.getTags().iterator();
        while(iter.hasNext()) {
            Tag tag=(Tag)iter.next();
            System.out.println(tag.getTagName()+"("+tag.getTagType()+"): "+tag.getDescription());
        }
        System.out.println("***************************************************************");
        
    }

}
