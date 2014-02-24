/**
 * 
 */
package com.jzb.ttpoi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * @author jzarzuela
 * 
 */
public class DrawAllIconsImage {

    /**
     * 
     */
    public DrawAllIconsImage() {
        // TODO Auto-generated constructor stub
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        long t1 = 0, t2 = 0;
        try {
            System.out.println("\n***** EXECUTION STARTED *****\n");
            DrawAllIconsImage me = new DrawAllIconsImage();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            t2 = System.currentTimeMillis();
            System.out.println("***** EXECUTION FAILED [" + (t2 - t1) + "]*****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        //int NUM_ROWS = 14;
        //int NUM_COLS = 7;

        int NUM_ROWS = 5;
        int NUM_COLS = 20;
        
        //BufferedImage iconsImage = new BufferedImage(320, 635, BufferedImage.TYPE_INT_ARGB);
        BufferedImage iconsImage = new BufferedImage(905, 230, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = iconsImage.createGraphics();

        ArrayList<String> fnames = _readPlist();
        for (int y = 0; y < NUM_ROWS; y++) {
            
            for (int x = 0; x < NUM_COLS; x++) {
                
                int index = x + y * NUM_COLS;
                System.out.println("x = " + x + ", y = " + y + ", index = " + index);
                if(index>=98) continue;
                
                String fname = fnames.get(index);
                _drawIcon(g2d, fname, x, y);
            }
        }

        
        g2d.setColor(Color.LIGHT_GRAY);
        for (int n = 1; n < NUM_COLS; n++) {
            g2d.drawLine(2 + n * 45, 0, 2 + n * 45, iconsImage.getHeight());
        }
        for (int n = 1; n < NUM_ROWS; n++) {
            g2d.drawLine(0, 2 + n * 45, iconsImage.getWidth(), 2 + n * 45);
        }

        ImageIO.write(iconsImage, "PNG", new File("/Users/jzarzuela/Documents/objC-Campus/iTravelPOI/iTravelPOI-iOS/iTravelPOI-iOS/App GUI/Editors/IconEditorViewController/resources/out.png"));
    }

    // ----------------------------------------------------------------------------------------------------
    private void _drawIcon(Graphics2D g2d, String fname, int x, int y) throws Exception {
        
        String fnameS = fname.replace(".png", ".shadow.png");
        File fimgS = new File(fnameS);
        if (fimgS.exists()) {
            BufferedImage bi = ImageIO.read(fimgS);
            g2d.drawImage(bi, null, 9 + x * 45, 9 + y * 45);
        } else {
            System.out.println("no esta = " + fnameS);
        }

        File fimgI = new File(fname);
        if (fimgI.exists()) {
            BufferedImage bi = ImageIO.read(fimgI);
            g2d.drawImage(bi, null, 9 + x * 45, 9 + y * 45);
        } else {
            System.out.println("no esta = " + fname);
        }

    }
    
    // ----------------------------------------------------------------------------------------------------
    private ArrayList<String> _readPlist() throws Exception {

        ArrayList<String> fnames = new ArrayList();

        File plistFile = new File("/Users/jzarzuela/Documents/objC-Campus/iTravelPOI/iTravelPOI-iOS/iTravelPOI-iOS/App GUI/Editors/IconEditorViewController/resources/allGMapIconsInfo.plist");
        BufferedReader br = new BufferedReader(new FileReader(plistFile));
        while (br.ready()) {
            String s = br.readLine();
            if (s.indexOf("GMI_") > 0 && s.indexOf(".png") > 0) {
                int p1 = s.indexOf("<string>");
                int p2 = s.indexOf("</string>");
                String fn = s.substring(8 + p1, p2);
                fnames.add("/Users/jzarzuela/Documents/objC-Campus/iTravelPOI/iTravelPOI-Model/MapIconImages.bundle/" + fn);
            }
        }
        br.close();
        return fnames;
    }
}
