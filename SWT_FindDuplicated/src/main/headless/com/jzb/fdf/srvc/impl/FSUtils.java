/**
 * 
 */
package com.jzb.fdf.srvc.impl;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;

/**
 * @author jzarzuela
 * 
 */
public class FSUtils {

    // ----------------------------------------------------------------------------------------------------
    private static Path _getUserHomePath() {
        return Paths.get("/Users/jzarzuela");
    }

    // ----------------------------------------------------------------------------------------------------
    private static FileStore _getUserHomeFileStore() throws IOException {

        return Files.getFileStore(_getUserHomePath());
    }

    // ----------------------------------------------------------------------------------------------------
    private static Path _getMountPoint(Path path) throws Exception {

        FileStore initialFS = Files.getFileStore(path);

        if (initialFS.equals(_getUserHomeFileStore())) {
            return _getUserHomePath();
        }

        for (;;) {
            Path parentPath = path.getParent();
            if (parentPath == null || !Files.getFileStore(parentPath).equals(initialFS)) {
                return path;
            }
            path = parentPath;
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static Path _getCleanDuplicatedFilesRootFolder(Path fpath) throws Exception {

        Path mountPoint = _getMountPoint(fpath.getParent());

        Path dupFilesFolder = mountPoint.resolve(Paths.get("_#DupFiles#_")).normalize().toAbsolutePath();

        if (!Files.exists(dupFilesFolder)) {
            Files.createDirectories(dupFilesFolder, new FileAttribute[0]);
        }

        return dupFilesFolder;
    }

    // ----------------------------------------------------------------------------------------------------
    public static void cleanDuplicated(String fname) throws Exception {

        Path fpath = Paths.get(fname);
        if (!Files.exists(fpath, LinkOption.NOFOLLOW_LINKS))
            return;

        Path dupFilesRoot = _getCleanDuplicatedFilesRootFolder(fpath);
        Path destPath = Paths.get(dupFilesRoot.toString(), fpath.normalize().toAbsolutePath().toString());

        Files.createDirectories(destPath.getParent(), new FileAttribute[0]);
        Files.move(fpath, destPath, LinkOption.NOFOLLOW_LINKS, StandardCopyOption.ATOMIC_MOVE);
    }

}
