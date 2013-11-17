/**
 * 
 */
package com.jzb.img.tsk;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.jzb.futil.FileExtFilter;
import com.jzb.futil.FileExtFilter.IncludeFolders;
import com.jzb.util.Tracer;
import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * @author jzarzuela
 * 
 */
@SuppressWarnings("restriction")
public class IPadResize extends BaseTask {

    public static class ImageInformation {

        public int height;
        public int orientation;
        public int width;

        public ImageInformation(int orientation, int width, int height) {
            this.orientation = orientation;
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return String.format("%dx%d,%d", this.width, this.height, this.orientation);
        }
    }

    public static final int IPAD_HEIGHT    = 768;
    public static final int IPAD_WIDTH     = 1024;

    public static final int IPHONE_HEIGHT  = 640;
    public static final int IPHONE_WIDTH   = 960;

    public static final int IPHONE5_HEIGHT = 640;
    public static final int IPHONE5_WIDTH  = 1136;

    // --------------------------------------------------------------------------------------------------------
    public IPadResize(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) {
        super(justChecking, baseFolder, recursive, true);
    }

    // --------------------------------------------------------------------------------------------------------
    public void resize(File dstFolder, String folderToSkipScan, String folderToSkipCreate) {

        try {
            _checkBaseFolder();

            Tracer._debug("");
            Tracer._debug("Resizing files " + (m_recursive == RecursiveProcessing.YES ? " * RECURSIVELY * " : " ") + "to destination folder: '" + dstFolder + "'"
                    + (folderToSkipScan != null ? " (skipping '" + folderToSkipScan + "')" : ""));
            Tracer._debug("");

            dstFolder = new File(dstFolder, m_baseFolder.getName());
            _resize(m_baseFolder, dstFolder, folderToSkipScan, folderToSkipCreate);

        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    public void checkExif(String folderNameToSkipScan) {

        try {
            _checkBaseFolder();
            _checkExif(m_baseFolder, folderNameToSkipScan);
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }

    }

    // --------------------------------------------------------------------------------------------------------
    private void _checkExif(File srcFolder, String folderNameToSkipScan) throws Exception {

        Tracer._debug("");
        Tracer._debug("Checking EXIF data for files in folder: '" + srcFolder + "'");
        Tracer._debug("");

        // Gets just image files and folders
        File fList[] = srcFolder.listFiles(FileExtFilter.imgFilter(IncludeFolders.YES));

        // Split files and folders to process them properly
        TreeSet<File> allFiles = new TreeSet<File>();
        TreeSet<File> subFolders = new TreeSet<File>();
        for (File fImg : fList) {
            if (fImg.isDirectory()) {
                subFolders.add(fImg);
            } else {
                allFiles.add(fImg);
            }
        }

        // ---------------------------------------------
        // Iterate files in subfolder
        for (File fImg : allFiles) {
            if (_readImageOrientation(fImg) == -1) {
            }
        }

        // ---------------------------------------------
        // Iterate subfolders
        if (m_recursive == RecursiveProcessing.YES) {
            for (File sfolder : subFolders) {
                if (!sfolder.getName().equalsIgnoreCase(folderNameToSkipScan)) {
                    _checkExif(sfolder, folderNameToSkipScan);
                }
            }
        }

    }

    // --------------------------------------------------------------------------------------------------------
    private void _resize(File srcFolder, File dstFolder, String folderToSkipScan, String folderToSkipCreate) throws Exception {

        Tracer._debug("");
        Tracer._debug("Resizing files in folder: '" + srcFolder + "'");
        Tracer._debug("");

        // Gets just image files and folders
        File fList[] = srcFolder.listFiles(FileExtFilter.imgFilter(IncludeFolders.YES));

        // Split files and folders to process them properly
        TreeSet<File> allFiles = new TreeSet<File>();
        TreeSet<File> subFolders = new TreeSet<File>();
        for (File fImg : fList) {
            if (fImg.isDirectory()) {
                subFolders.add(fImg);
            } else {
                allFiles.add(fImg);
            }
        }

        // ---------------------------------------------
        // Iterate files in subfolder
        for (File fImg : allFiles) {
            _resizeImgFile(fImg, dstFolder);
        }

        // ---------------------------------------------
        // Iterate subfolders
        if (m_recursive == RecursiveProcessing.YES) {
            for (File sfolder : subFolders) {
                if (!sfolder.getName().equalsIgnoreCase(folderToSkipScan)) {

                    File newDstFolder;
                    if (sfolder.getName().equalsIgnoreCase(folderToSkipCreate)) {
                        newDstFolder = dstFolder;
                    } else {
                        newDstFolder = new File(dstFolder, sfolder.getName());
                    }

                    _resize(sfolder, newDstFolder, folderToSkipScan, folderToSkipCreate);
                }
            }
        }

    }

    // --------------------------------------------------------------------------------------------------------
    private void _resizeImgFile(File imgFile, File dstFolder) throws Exception {

        Tracer._debug("Processing file: '" + imgFile.getName() + "'");
        int orientation = _readImageOrientation(imgFile);

        BufferedImage bufImg = ImageIO.read(imgFile);

        int width = bufImg.getWidth();
        int height = bufImg.getHeight();

        ImageInformation imgInfo = new ImageInformation(orientation, width, height);
        _calcScaleImgSize(imgInfo);

        bufImg = _scaleDownImage(bufImg, imgInfo.width, imgInfo.height);

        bufImg = _rotateImage(bufImg, imgInfo);

        File dstFile = new File(dstFolder, "ipad-" + imgFile.getName());
        _writeImage(bufImg, dstFile);

    }

    // --------------------------------------------------------------------------------------------------------
    private void _calcScaleImgSize(ImageInformation imgInfo) {

        int width, height;

        // Las imagenes pequeñas se quedan como estan
        if (imgInfo.width < IPAD_WIDTH || imgInfo.height < IPAD_HEIGHT) {
            return;
        }

        // El cambio depende de la orientacion de la imagen
        if (imgInfo.width > imgInfo.height) {

            // Se ajusta al alto o ancho del iPad dependiendo de la relacion de aspecto
            float ra = (float) imgInfo.width / (float) imgInfo.height;

            // Mientras no sea un 16:9 panoramico (o similar) ajustara al ancho. En otro caso al alto
            if (ra >= 1.7f) {
                height = IPAD_HEIGHT;
                width = (int) Math.ceil(IPAD_HEIGHT * ra);
                // Si estan a menos de 2pixels del tamaño del iPad lo ajusta a este, sino lo redondea a un numero par
                if (Math.abs(width - IPAD_WIDTH) <= 2) {
                    width = IPAD_WIDTH;
                } else {
                    width = (width >> 1) * 2;
                }
            } else {
                width = IPAD_WIDTH;
                height = (int) Math.ceil(IPAD_WIDTH / ra);
                // Si estan a menos de 2pixels del tamaño del iPad lo ajusta a este, sino lo redondea a un numero par
                if (Math.abs(height - IPAD_HEIGHT) <= 2) {
                    height = IPAD_HEIGHT;
                } else {
                    height = (height >> 1) * 2;
                }
            }

        } else {

            // Se ajusta al alto o ancho del iPad dependiendo de la relacion de aspecto
            float ra = (float) imgInfo.height / (float) imgInfo.width;

            // Mientras no sea un 16:9 panoramico (o similar) ajustara al ancho. En otro caso al alto
            if (ra >= 1.7f) {
                width = IPAD_HEIGHT;
                height = (int) Math.ceil(IPAD_HEIGHT * ra);
                // Si estan a menos de 2pixels del tamaño del iPad lo ajusta a este, sino lo redondea a un numero par
                if (Math.abs(height - IPAD_WIDTH) <= 2) {
                    width = IPAD_WIDTH;
                } else {
                    width = (width >> 1) * 2;
                }
            } else {
                height = IPAD_WIDTH;
                width = (int) Math.ceil(IPAD_WIDTH / ra);
                // Si estan a menos de 2pixels del tamaño del iPad lo ajusta a este o lo redondea a un numero par
                if (Math.abs(width - IPAD_HEIGHT) <= 2) {
                    width = IPAD_HEIGHT;
                } else {
                    width = (width >> 1) * 2;
                }
            }

        }

        imgInfo.width = width;
        imgInfo.height = height;
    }

    // --------------------------------------------------------------------------------------------------------
    private int _readImageOrientation(File imageFile) {

        int orientation = -1;
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
            Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
            orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        } catch (MetadataException mdex) {
            Tracer._warn("Could not get orientation (" + mdex.getMessage() + ") for: " + imageFile.getName());
        } catch (NullPointerException npex) {
            Tracer._warn("Could not get orientation (there is no Metadata) for: " + imageFile.getName());
        } catch (Exception ex) {
            Tracer._warn("Could not get orientation (" + ex.getMessage() + ") for: " + imageFile.getName(), ex);
        }

        return orientation;
    }

    // --------------------------------------------------------------------------------------------------------
    private BufferedImage _scaleDownImage(BufferedImage img, int targetWidth, int targetHeight) {


        ResampleOp resampleOp = new ResampleOp(targetWidth, targetHeight);
        resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);
        BufferedImage buff = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage rescaled = resampleOp.filter(img, buff);
        return rescaled;
    }

    // --------------------------------------------------------------------------------------------------------
    private BufferedImage _rotateImage(BufferedImage bufImg, ImageInformation imgInfo) throws Exception {

        BufferedImage rotatedImage;
        AffineTransform transform;
        AffineTransformOp op;

        switch (imgInfo.orientation) {
            case 6:
            case 7:
                rotatedImage = new BufferedImage(imgInfo.height, imgInfo.width, BufferedImage.TYPE_3BYTE_BGR);
                transform = new AffineTransform();
                transform.rotate(Math.toRadians(90), imgInfo.height / 2, imgInfo.height / 2);
                op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
                return op.filter(bufImg, rotatedImage);
            case 8:
            case 9:
                rotatedImage = new BufferedImage(imgInfo.height, imgInfo.width, BufferedImage.TYPE_3BYTE_BGR);
                transform = new AffineTransform();
                transform.rotate(Math.toRadians(-90), imgInfo.width / 2, imgInfo.width / 2);
                op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
                return op.filter(bufImg, rotatedImage);
            default:
                return bufImg;
        }

    }

    // --------------------------------------------------------------------------------------------------------
    private void _writeImage(BufferedImage bufImg, File dstFile) throws Exception {

        if (m_justChecking == JustCheck.NO) {
            dstFile.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(dstFile);

            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufImg);
            param.setQuality(0.95f, true);
            param.setDensityUnit(JPEGDecodeParam.DENSITY_UNIT_DOTS_INCH);
            param.setXDensity(300);

            encoder.setJPEGEncodeParam(param);
            encoder.encode(bufImg);
            out.close();
        }
    }

}
