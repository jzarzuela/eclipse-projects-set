/**
 * 
 */
package com.jzb.pclus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import jsat.SimpleDataSet;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.DataPoint;
import jsat.clustering.OPTICS;
import jsat.linear.DenseVector;
import jsat.linear.VecPaired;
import jsat.linear.distancemetrics.DistanceMetric;
import jsat.linear.distancemetrics.EuclideanDistance;
import jsat.linear.distancemetrics.WeightedEuclideanDistance;

/**
 * @author jzarzuela
 * 
 */
public class TestPCluster {

    private static final int             null_catValues[] = new int[0];
    private static final CategoricalData null_catData[]   = new CategoricalData[0];

    private SimpleDateFormat             m_sdf            = new SimpleDateFormat("_yyyy-MM-dd_");

    // ----------------------------------------------------------------------------------------------------
    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("\n***** EXECUTION STARTED *****\n");
            TestPCluster me = new TestPCluster();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("\n***** EXECUTION FAILED *****\n");
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

        File baseFolder = new File("/Users/jzarzuela/Documents/personal/Viajes/_Fotos_/_EN TRABAJO_/2013-Belgica/100CANON");

        DistanceMetric dm = new WeightedEuclideanDistance(new DenseVector(new double[] { 1 }));
        dm = new EuclideanDistance();
        OPTICS opt = new OPTICS(dm, /* minPtn */3, /* XI */0.001);

        System.out.println("-- Reading DataSet --");
        ArrayList<DataPoint> dpl = new ArrayList<DataPoint>();
        _readDataPointsFromBaseFolder(baseFolder, dpl);
        SimpleDataSet sds = new SimpleDataSet(dpl);
        System.out.println("-- Clustering DataSet --");
        List<List<DataPoint>> result = opt.cluster(sds);
        System.out.println(result.size());
        System.out.println("-- Relocating DataSet --");
        _relocateFiles(baseFolder, result);
    }

    // ----------------------------------------------------------------------------------------------------
    private void _moveFilesToBaseFolder(File folder, File baseFolder) throws Exception {

        File listFiles[] = folder.listFiles();
        if (listFiles == null || listFiles.length == 0)
            return;

        boolean inBaseFolder = baseFolder.equals(folder);
        for (File f : listFiles) {
            if (f.isDirectory()) {
                _moveFilesToBaseFolder(f, baseFolder);
            } else if (!inBaseFolder) {
                File newgFile = new File(baseFolder, f.getName());
                if (!f.renameTo(newgFile)) {
                    System.out.println("*** Warning: Error renaming file: '" + f.getName() + "' to: " + newgFile);
                }
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _deleteEmptyFolders(File folder) throws Exception {

        File listFiles[] = folder.listFiles();
        if (listFiles == null || listFiles.length == 0)
            return;

        for (File f : listFiles) {
            if (!f.isDirectory())
                continue;

            File listFiles2[] = f.listFiles();
            if (listFiles2 != null && listFiles2.length > 0) {
                _deleteEmptyFolders(f);
            }
            if (!f.delete()) {
                System.out.println("*** Warning: Error deleting empty folder: " + f.getName());
            }

        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _readDataPointsFromBaseFolder(File baseFolder, ArrayList<DataPoint> dpl) throws Exception {
        _moveFilesToBaseFolder(baseFolder, baseFolder);
        _deleteEmptyFolders(baseFolder);
        _readDataPointsFromFiles(baseFolder, dpl);
    }

    // ----------------------------------------------------------------------------------------------------
    private void _readDataPointsFromFiles(File folder, ArrayList<DataPoint> dpl) throws Exception {

        File fileList[] = folder.listFiles();
        if (fileList == null || fileList.length == 0)
            return;

        for (File f : fileList) {
            if (f.isDirectory()) {
                _readDataPointsFromFiles(f, dpl);
            } else {
                if (!f.getName().toLowerCase().endsWith(".jpg") && !f.getName().toLowerCase().endsWith(".cr2")) {
                    continue;
                }

                long time = _readExifDate(f);
                VecPaired<DenseVector, File> vp = new VecPaired<DenseVector, File>(new DenseVector(new double[] { time }), f);
                DataPoint dp = new DataPoint(vp, null_catValues, null_catData);
                dpl.add(dp);
            }

        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _relocateFiles(File baseFolder, List<List<DataPoint>> dpl) throws Exception {

        // Mueve los ficheros a subcarpetas indexadas segun el cluster
        int index = 0;
        for (List<DataPoint> dpl2 : dpl) {
            if (dpl2.size() <= 0)
                continue;
            VecPaired<DenseVector, File> vp1 = (VecPaired<DenseVector, File>) dpl2.get(0).getNumericalValues();
            String dayFolder = _readExifDateStr(vp1.getPair());
            File indexedFolder = new File(baseFolder, dayFolder + "/_INX_" + index);
            indexedFolder.mkdirs();
            for (DataPoint dp : dpl2) {
                VecPaired<DenseVector, File> vp = (VecPaired<DenseVector, File>) dp.getNumericalValues();
                File jpegFile = new File(baseFolder, vp.getPair().getName());
                File newJpegFile = new File(indexedFolder, jpegFile.getName());
                if (!jpegFile.renameTo(newJpegFile)) {
                    System.out.println("*** Warning: Error renaming file: '" + jpegFile.getName() + "' to: " + newJpegFile);
                }
            }
            index++;
        }

        // Mueve los ficheros que quedan a subcarpetas indexadas segun la fecha
        File listFiles[] = baseFolder.listFiles();
        if (listFiles != null) {
            for (File f : listFiles) {
                if (f.isDirectory())
                    continue;
                String dayFolder = _readExifDateStr(f);
                File indexedFolder = new File(baseFolder, dayFolder);
                indexedFolder.mkdirs();
                File newFile = new File(indexedFolder, f.getName());
                if (!f.renameTo(newFile)) {
                    System.out.println("*** Warning: Error renaming file: '" + f.getName() + "' to: " + newFile);
                }
            }
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private String _readExifDateStr(File jpegFile) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
            ExifSubIFDDirectory dir = metadata.getDirectory(ExifSubIFDDirectory.class);
            Date d = dir.getDate(36868);
            return m_sdf.format(d);
        } catch (Exception e) {
            return "_0000-00-00_";
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private long _readExifDate(File jpegFile) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
            ExifSubIFDDirectory dir = metadata.getDirectory(ExifSubIFDDirectory.class);
            Date d = dir.getDate(36868);
            return d.getTime();
        } catch (Exception e) {
            return -1;
        }
    }
}
