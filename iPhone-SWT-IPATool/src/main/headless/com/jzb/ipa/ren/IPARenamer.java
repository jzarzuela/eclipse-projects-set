/**
 * 
 */
package com.jzb.ipa.ren;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.jzb.ipa.bundle.BundleReader;
import com.jzb.ipa.bundle.T_BundleData;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class IPARenamer {

    private BundleReader m_ipaReader = new BundleReader();

    public void rename(File folders[], boolean recurse) {

        for (File folder : folders) {
            Tracer._info("Renaming base: ----------------------------------------------");
            _rename(folder, recurse);
        }
    }

    public void cleanAllJPEGs(File folders[], boolean recurse) {

        for (File folder : folders) {
            _clean(folder, recurse);
        }
    }

    private void _clean(File folder, boolean recurse) {

        Tracer._info("Cleaning: Processing folder: " + folder);

        _delOldJPEGs(folder);

        for (File f : folder.listFiles()) {
            if (f.isDirectory() && recurse) {
                _clean(f, true);
            }
        }
    }

    private void _rename(File folder, boolean recurse) {

        Tracer._info("Renaming: Processing folder: " + folder);

        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                if (recurse)
                    _rename(f, true);
            } else {
                String fname = f.getName().toLowerCase();
                if (fname.endsWith(".ipa")) {
                    try {
                        processIPA(f);
                    } catch (Throwable th) {
                        Tracer._error("Error processing IPA file: " + f, th);
                    }
                }
            }
        }

        _delOldJPEGs(folder);

    }

    public File processIPA(File f) throws Exception {

        try {
            Tracer._debug("Processing IPA file: " + f);
            T_BundleData ipaInfo = m_ipaReader.readInfo(f);

            String newName = NameComposer.composeName(ipaInfo);

            File newFile;

            if (ipaInfo.img != null) {
                Tracer._debug("Extracting JPEG file");
                newFile = new File(f.getParentFile(), newName + ipaInfo.imgExt);
                FileOutputStream fos = new FileOutputStream(newFile, false);
                fos.write(ipaInfo.img);
                fos.close();
            } else {
                Tracer._debug("Error while extracting JPEG file (iTunesArtwork) for IPA");
            }

            Tracer._debug("Renaming to: " + newName);
            newFile = new File(f.getParentFile(), newName + ".ipa");
            _moveFileHelper(f,newFile,"File coundn't be renamed: ");

            return newFile;
        } catch (Exception ex) {
            Tracer._error("Error processing IPA file", ex);
            throw ex;
        } catch (Throwable th) {
            Tracer._error("Error processing IPA file", th);
            throw new Exception("Error processing IPA file", th);
        }
    }

    private boolean _moveFileHelper(File org, File dst, String errorMsg) {

        if (org == null || !org.exists()) {
            Tracer._error("Trying to move from source file that does not exist: " + org);
            return false;
        }

        try {
            Files.move(Paths.get(org.getAbsolutePath()), Paths.get(dst.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Throwable th) {
            Tracer._error(errorMsg + dst, th);
            return false;
        }

    }

    public File extractJPEG(File f) throws Exception {

        Tracer._debug("Extracting JPEG from IPA file: " + f);
        T_BundleData ipaInfo = m_ipaReader.readInfo(f);

        Tracer._debug("Extracting JPEG file");
        String newName = f.getName().substring(0, f.getName().length() - 3) + "jpg";
        File newFile = new File(f.getParentFile(), newName);
        FileOutputStream fos = new FileOutputStream(newFile, false);
        fos.write(ipaInfo.img);
        fos.close();

        return newFile;
    }

    private void _delOldJPEGs(File folder) {

        Tracer._debug("Deleting old JPEG files that doesn't have any IPA associated in folder: " + folder);

        for (File f : folder.listFiles()) {
            String fname = f.getName();
            String lfname = fname.toLowerCase();
            if ((lfname.endsWith(".jpg") || lfname.endsWith(".png")) && lfname.contains("_pk[")) {
                File ipaFile = new File(folder, fname.substring(0, fname.length() - 3) + "ipa");
                if (!ipaFile.exists()) {
                    if (!f.delete()) {
                        Tracer._warn("Old JPEG file couldn't be deleted");
                    }
                }
            }
        }
    }
}
