/**
 * 
 */
package com.jzb.ttpoi;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;

import com.jzb.ttpoi.data.TPOIData;
import com.jzb.ttpoi.data.TPOIFileData;
import com.jzb.ttpoi.wl.KMLFileWriter;
import com.jzb.ttpoi.wl.OV2FileLoader;
import com.jzb.ttpoi.wl.OV2FileWriter;

/**
 * @author n63636
 * 
 */
public class KKTest {

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
            KKTest me = new KKTest();
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

        String ov2Files[] = { "C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_OV2_\\kk2\\kk2_all.ov2", "C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_OV2_\\Las Vegas-Full\\Las Vegas-Full_ALL.ov2" };
        String ov2Folders[] = { null };

        File outFolder = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_OV2_\\tmp");
        removeDuplicated(outFolder, ov2Files, ov2Folders);

    }

    private void differences(File outFolder, String ov2FileNames[], String ov2Folders[]) throws Exception {

        HashSet<File> ov2Files = _getAllOV2Files(ov2FileNames, ov2Folders);
        TPOIFileData ttData = new TPOIFileData();

        for (File f : ov2Files) {
            TPOIFileData tfd = OV2FileLoader.loadFile(f);
            _addNewPOIs(ttData, tfd);
        }

        ttData.setName("Differences");
        ttData.sort();
        KMLFileWriter.saveFile(new File(outFolder, "CheckDuplicated.kml"), ttData);
        OV2FileWriter.saveFile(new File(outFolder, "CheckDuplicated.ov2"), ttData.getAllPOIs());

    }

    private void removeDuplicated(File outFolder, String ov2FileNames[], String ov2Folders[]) throws Exception {

        HashSet<File> ov2Files = _getAllOV2Files(ov2FileNames, ov2Folders);
        TPOIFileData ttData = new TPOIFileData();

        for (File f : ov2Files) {
            TPOIFileData tfd = OV2FileLoader.loadFile(f);
            _addNewPOIs(ttData, tfd);
        }

        ttData.setName("CheckDuplicated");
        ttData.sort();
        KMLFileWriter.saveFile(new File(outFolder, "CheckDuplicated.kml"), ttData);
        OV2FileWriter.saveFile(new File(outFolder, "CheckDuplicated.ov2"), ttData.getAllPOIs());

    }

    private void _addNewPOIs(TPOIFileData allPOIs, TPOIFileData newPOIs) throws Exception {

        for (TPOIData poi1 : newPOIs.getAllPOIs()) {

            boolean isNew = true;
            for (TPOIData poi2 : allPOIs.getAllPOIs()) {

                int n = poi1.comparedToPOI(20, poi2); // A unos 20 metros de distancia
                if (n < 4) {
                    isNew = false;
                    break;
                }
            }

            if (isNew) {
                allPOIs.addPOI(poi1);
            }

        }

    }

    private HashSet<File> _getAllOV2Files(String ov2FileNames[], String ov2Folders[]) throws Exception {

        final HashSet<File> ov2Files = new HashSet<File>();

        for (String folder : ov2Folders) {
            Path startPath = Paths.get(folder);
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path fpath, BasicFileAttributes attrs) throws IOException {
                    File f = fpath.toFile();
                    if (f.getName().toLowerCase().endsWith("_all.ov2")) {
                        // Tracer._debug("Adding OV2 file: "+f);
                        ov2Files.add(f);
                    }

                    // if (FileUtils.getExtension(f).equals("ov2")) {
                    // // Tracer._debug("Adding OV2 file: "+f);
                    // ov2Files.add(f);
                    // }
                    return FileVisitResult.CONTINUE;
                }

            });
        }

        for (String s : ov2FileNames) {
            if (s == null)
                continue;
            File f = new File(s);
            if (f.exists()) {
                ov2Files.add(f);
            }
        }

        return ov2Files;
    }

}
