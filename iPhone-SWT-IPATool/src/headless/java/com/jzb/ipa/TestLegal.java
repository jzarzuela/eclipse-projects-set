/**
 * 
 */
package com.jzb.ipa;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import com.jzb.ipa.bundle.BundleReader;
import com.jzb.ipa.bundle.T_BundleData;
import com.jzb.ipa.ren.NameComposer;

/**
 * @author n63636
 * 
 */
public class TestLegal {

    private BundleReader m_ipaReader = new BundleReader();

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
            TestLegal me = new TestLegal();
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

        final Path ipaBaseFolder = Paths.get("/Users/jzarzuela/Documents/personal/iPhone/IPAs/");
        final Path ipaDestFolder = Paths.get("/Users/jzarzuela/Documents/personal/iPhone/IPAs_new/");
        Files.walkFileTree(ipaBaseFolder, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

                if (exc != null)
                    throw exc;

                File files[] = dir.toFile().listFiles();
                if (files == null || files.length == 0) {
                    Files.deleteIfExists(dir);
                }

                return FileVisitResult.CONTINUE;
            }

            @SuppressWarnings("synthetic-access")
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                try {
                    String fileName = file.getFileName().toString();
                    if (fileName.equalsIgnoreCase(".DS_Store")) {
                        Files.deleteIfExists(file);
                        return FileVisitResult.CONTINUE;
                    }

                    if (!fileName.toLowerCase().endsWith(".ipa")) {
                        return FileVisitResult.CONTINUE;
                    }

                    System.out.println("Moving IPA file: " + file);
                    T_BundleData ipaInfo = m_ipaReader.readInfo(file.toFile());

                    Path destFolder;
                    String relFolderStr = ipaBaseFolder.relativize(file.getParent()).toString();
                    switch (ipaInfo.isLegal) {
                        case LEGAL:
                            destFolder = ipaDestFolder.resolve(relFolderStr.replace("_cracked", "_legal"));
                            destFolder = ipaDestFolder.resolve(relFolderStr.replace("_inDoubt", "_legal"));
                            break;
                        default:
                            destFolder = ipaDestFolder.resolve(relFolderStr.replace("_legal", "_cracked"));
                            destFolder = ipaDestFolder.resolve(relFolderStr.replace("_inDoubt", "_cracked"));
                            break;
                    }

                    destFolder.toFile().mkdirs();

                    String newIPAName = NameComposer.composeName(ipaInfo) + ".ipa";
                    Path destIPAFile = destFolder.resolve(newIPAName);
                    Files.move(file, destIPAFile, LinkOption.NOFOLLOW_LINKS, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Moved!");

                    Path jpgFile = Paths.get(file.toFile().getAbsolutePath().replace(".ipa", ".jpg"));
                    if (Files.exists(jpgFile)) {
                        String newJPGName = NameComposer.composeName(ipaInfo) + ".jpg";
                        Path destJPGFile = destFolder.resolve(newJPGName);
                        System.out.println("Moving JPG file: " + jpgFile);
                        Files.move(jpgFile, destJPGFile, LinkOption.NOFOLLOW_LINKS, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                    }

                    return FileVisitResult.CONTINUE;

                } catch (Throwable th) {
                    th.printStackTrace();
                    return FileVisitResult.TERMINATE;
                }
            }

            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                if (file.toString().toLowerCase().endsWith(".jpg")) {
                    return FileVisitResult.CONTINUE;
                } else {
                    throw exc;
                }
            }

        });

    }
}
