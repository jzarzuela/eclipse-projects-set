/**
 * 
 */
package com.jzb.tt.compact;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

/**
 * @author jzarzuela
 * 
 */
public class MoveTogether {

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
            MoveTogether me = new MoveTogether();
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

        final Path destPath = Paths.get("/Users/jzarzuela/Documents/personal/_POIs_");
        final Path destPath2 = Paths.get("/Users/jzarzuela/Documents/personal/_POIs_/USB");
        final Path startPath = Paths.get("/Users/jzarzuela");
        final Path startPath2 = Paths.get("/Volumes/Seagate Expansion Drive");

        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {

            /**
             * @see java.nio.file.SimpleFileVisitor#visitFileFailed(java.lang.Object, java.io.IOException)
             */
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                Objects.requireNonNull(file);
                System.out.println("* ERROR processing file ********************> " + exc);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Objects.requireNonNull(dir);
                Objects.requireNonNull(attrs);

                if (dir.startsWith(destPath)) {
                    System.out.println("Skipping folder: " + dir);
                    return FileVisitResult.SKIP_SUBTREE;
                } else {
                    System.out.println("Processing folder: " + dir);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Objects.requireNonNull(file);
                Objects.requireNonNull(attrs);
                if (file.getFileName().toString().toLowerCase().endsWith(".kml") //
                        || file.getFileName().toString().toLowerCase().endsWith(".ov2") //
                        || file.getFileName().toString().toLowerCase().endsWith("mapsettings.cfg")) {

                    Path newPath;

                    if (file.getFileName().toString().toLowerCase().endsWith(".cfg")) {
                        newPath = Paths.get(destPath.toString(), "_TT_CFG_", startPath.relativize(file).toString());
                    } else {
                        newPath = Paths.get(destPath.toString(), startPath.relativize(file).toString());
                    }

                    if (Files.exists(newPath)) {
                        System.out.println("  File already exists: " + newPath);
                    } else {
                        System.out.println("  Coping file: " + newPath);
                        Files.createDirectories(newPath.getParent());
                        Files.copy(file, newPath, StandardCopyOption.COPY_ATTRIBUTES);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
