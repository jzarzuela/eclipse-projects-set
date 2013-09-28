import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 
 */

/**
 * @author jzarzuela
 * 
 */
@SuppressWarnings("restriction")
public class KK {

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
            KK me = new KK();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(-1);
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

        String urlBase = "http://hm.stadtdigital.de/(S(pljdmk5lazmy24llcrly0vwy))/plaene/5663/High/";

        BufferedImage imgOut = new BufferedImage(256 * 20, 256 * 14, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = imgOut.getGraphics();

        for (int y = 0; y < 7; y++) {
            for (int x = 0; x < 20; x++) {
                URL url = new URL(urlBase + "TileGroup0/5-" + x + "-" + y + ".jpg");
                System.out.println("Downloading image = " + url);
                Image img = ImageIO.read(url);
                g.drawImage(img, x * 256, y * 256, null);
            }
        }

        for (int y = 7; y < 14; y++) {
            for (int x = 0; x < 20; x++) {
                URL url = new URL(urlBase + "TileGroup1/5-" + x + "-" + y + ".jpg");
                System.out.println("Downloading image = " + url);
                Image img = ImageIO.read(url);
                g.drawImage(img, x * 256, y * 256, null);
            }
        }

        g.dispose();

        File fout = new File("/Users/jzarzuela/Desktop/fout.jpg");
        _writeImage(imgOut, fout);
    }

    // --------------------------------------------------------------------------------------------------------
    private void _writeImage(BufferedImage bufImg, File dstFile) throws Exception {

        dstFile.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(dstFile);

        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufImg);
        param.setQuality(0.99f, true);
        param.setDensityUnit(JPEGDecodeParam.DENSITY_UNIT_DOTS_INCH);
        param.setXDensity(300);

        encoder.setJPEGEncodeParam(param);
        encoder.encode(bufImg);
        out.close();
    }

}
