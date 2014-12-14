/**
 * 
 */
package com.jzb.ipa.upd;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jzb.ipa.ren.IPARenamer;
import com.jzb.ipa.ren.NameComposer;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class IPAUpdater {

    private static class TIPAInfo {

        public File   imgFile;
        public File   ipaFile;
        public String pkg;
        public String ver;

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "pkg: " + pkg + " - ver: " + ver;
        }
    }

    private HashMap<String, TIPAInfo> m_existingIPAs   = new HashMap<String, TIPAInfo>();
    private ArrayList<TIPAInfo>       m_duplicatedIPAs = new ArrayList<TIPAInfo>();

    private IPARenamer                m_ipaRenamer     = new IPARenamer();
    private HashMap<String, TIPAInfo> m_updateIPAs     = new HashMap<String, TIPAInfo>();

    public void update(File ExistingFolder, File updateFolders[], File backupFolder, File newFolder, File duplicatedFolder) throws Exception {

        Tracer._info("Reading info for already existing IPAs");
        _readIPAsFromFolder(ExistingFolder, m_existingIPAs, m_duplicatedIPAs);

        Tracer._info("Reading info for new updated IPAs");
        for (File f : updateFolders) {
            _readIPAsFromFolder(f, m_updateIPAs, m_duplicatedIPAs);
        }
        
        if(m_duplicatedIPAs.size()>0) {
            Tracer._error("There are duplicated IPA files. Moving them to folder: "+duplicatedFolder);
            _moveDuplicatedFiles(duplicatedFolder);
        }

        Tracer._info("Checking for IPAs that have been updated");
        _checkForUpdates(backupFolder);

        Tracer._info("Moving completely new IPAs to folder: " + newFolder);
        _moveNewFiles(newFolder);

        Tracer._info("Cleaning JPEGs");
        _cleanJPEGs(updateFolders);
        _cleanJPEGs(new File[] { ExistingFolder });
    }

    private void _cleanJPEGs(File folders[]) {

        for (File folder : folders) {
            Tracer._debug("Cleaning old JPEG files that doesn't have any IPA associated anymore in: " + folder);
            for (File f : folder.listFiles()) {
                if (f.isDirectory()) {
                    _cleanJPEGs(new File[] { f });
                } else {
                    String fname = f.getName();
                    if ((fname.toLowerCase().endsWith(".jpg") || fname.toLowerCase().endsWith(".png")) && fname.toLowerCase().contains("_pk[")) {
                        File ipaFile = new File(f.getParentFile(), fname.substring(0, fname.length() - 3) + "ipa");
                        if (!ipaFile.exists()) {
                            Tracer._info("Cleaning JPEG file: " + f);
                            if (!f.delete()) {
                                Tracer._error("JPEG file couldn't be deleted: " + f);
                            }
                        }
                    }
                }
            }
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

    private void _checkForUpdates(File backupFolder) {

        backupFolder.mkdirs();

        for (Map.Entry<String, TIPAInfo> entry : m_existingIPAs.entrySet()) {
            TIPAInfo exIPA = entry.getValue();
            TIPAInfo upIPA = m_updateIPAs.remove(entry.getKey());
            if (upIPA != null) {
                if (exIPA.ver.compareTo(upIPA.ver) < 0) {
                    Tracer._info("There is an update for IPAs: " + exIPA.ipaFile);

                    Tracer._debug("Moving old files to backup folder");
                    File newFile;
                    if (exIPA.ipaFile != null && exIPA.ipaFile.exists()) {
                        newFile = new File(backupFolder, exIPA.ipaFile.getName());
                        _moveFileHelper(exIPA.ipaFile, newFile, "Moving IPA file to backup folder: ");
                    }
                    if (exIPA.imgFile != null && exIPA.imgFile.exists()) {
                        newFile = new File(backupFolder, exIPA.imgFile.getName());
                        _moveFileHelper(exIPA.imgFile, newFile, "Moving JPEG_IPA file to backup folder: ");
                    }

                    Tracer._debug("Moving new files to existing folder");
                    if (upIPA.ipaFile != null && upIPA.ipaFile.exists()) {
                        newFile = new File(exIPA.ipaFile.getParentFile(), upIPA.ipaFile.getName());
                        _moveFileHelper(upIPA.ipaFile, newFile, "Moving new IPA file to existing folder: ");
                    }
                    if (upIPA.imgFile != null && upIPA.imgFile.exists()) {
                        newFile = new File(exIPA.ipaFile.getParentFile(), upIPA.imgFile.getName());
                        _moveFileHelper(upIPA.imgFile, newFile, "Moving new JPEG_IPA file to existing folder: ");
                    }
                } else {
                    Tracer._info("Update IPA is older. It'll be descarted: " + upIPA.ipaFile);

                    Tracer._debug("Moving update files to backup folder");
                    File newFile;
                    newFile = new File(backupFolder, upIPA.ipaFile.getName());
                    _moveFileHelper(upIPA.ipaFile, newFile, "Moving IPA file to backup folder: ");

                    if (upIPA.imgFile != null) {
                        newFile = new File(backupFolder, upIPA.imgFile.getName());
                        _moveFileHelper(upIPA.imgFile, newFile, "Moving JPEG_IPA file to backup folder: ");
                    }
                }
            }
        }
    }

    private void _moveNewFiles(File newFolder) {

        for (Map.Entry<String, TIPAInfo> entry : m_updateIPAs.entrySet()) {

            TIPAInfo upIPA = entry.getValue();

            Tracer._debug("Moving new IPA files to a single folder");

            char isLegal = upIPA.ipaFile.getName().charAt(0);
            String legalFolder;
            switch (isLegal) {
                case '$':
                    legalFolder = "_legal" + File.separator;
                    break;
                default:
                    legalFolder = "_cracked" + File.separator;
                    break;

            }

            File newFile;
            File newSubFolder = new File(newFolder, legalFolder + upIPA.ipaFile.getName().substring(1, 2));
            newSubFolder.mkdirs();

            newFile = new File(newSubFolder, upIPA.ipaFile.getName());
            _moveFileHelper(upIPA.ipaFile, newFile, "Moving IPA file to folder: ");

            if (upIPA.imgFile != null) {
                newFile = new File(newSubFolder, upIPA.imgFile.getName());
                _moveFileHelper(upIPA.imgFile, newFile, "Moving JPEG_IPA file to folder: ");
            }
        }
    }
    
    
    private void _moveDuplicatedFiles(File duplicatedFolder) {

        duplicatedFolder.mkdirs();

        for (TIPAInfo upIPA : m_duplicatedIPAs) {

            Tracer._debug("Moving duplicated IPA files to a single folder");

            File newFile = new File(duplicatedFolder, upIPA.ipaFile.getName());
            _moveFileHelper(upIPA.ipaFile, newFile, "Moving IPA file to folder: ");

            if (upIPA.imgFile != null) {
                newFile = new File(duplicatedFolder, upIPA.imgFile.getName());
                _moveFileHelper(upIPA.imgFile, newFile, "Moving JPEG_IPA file to folder: ");
            }
        }
    }

    

    private TIPAInfo _readIPAInfo(File f) throws Exception {

        Tracer._debug("Reading info for IPA: " + f);
        String fname = f.getName();

        if (!NameComposer.isComposedName(fname)) {
            Tracer._warn("IPA without composedName found: " + f);
            f = m_ipaRenamer.processIPA(f);
            fname = f.getName();
        }

        String pkg = NameComposer.parsePkg(fname);
        String ver = NameComposer.parseVerCanonical(fname);

        TIPAInfo ii = new TIPAInfo();
        ii.ipaFile = f;
        ii.pkg = pkg;
        ii.ver = ver;

        File imgFile = new File(f.getParentFile(), fname.substring(0, fname.length() - 3) + "jpg");
        if (imgFile.exists()) {
            ii.imgFile = imgFile;
        } else {
            ii.imgFile = m_ipaRenamer.extractJPEG(f);
        }

        return ii;
    }

    private void _readIPAsFromFolder(File folder, HashMap<String, TIPAInfo> IPAs, ArrayList<TIPAInfo> duplicatedIPAs) throws Exception {

        if (folder.getName().equalsIgnoreCase("_duplicated")) {
            Tracer._warn("Skipping '_duplicated' IPAs folder: " + folder);
            return;
        }

        Tracer._debug("Reading info for IPA files in: " + folder);

        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                _readIPAsFromFolder(f, IPAs, duplicatedIPAs);
            } else {
                String fname = f.getName();
                String lfname = fname.toLowerCase();
                if (lfname.endsWith(".ipa")) {
                    TIPAInfo ii = _readIPAInfo(f);
                    TIPAInfo oo = IPAs.put(ii.pkg, ii);
                    if (oo != null) {
                        Tracer._error("IPA file duplicated found reading folder info:");
                        Tracer._error("  " + ii.ipaFile);
                        Tracer._error("  " + oo.ipaFile);
                        if (ii.ver.compareTo(oo.ver) < 0) {
                            // Deja en la hashmap la version mas nueva
                            IPAs.put(oo.pkg, oo);
                            duplicatedIPAs.add(ii);
                        } else {
                            duplicatedIPAs.add(oo);
                        }
                    }
                } else if ((lfname.endsWith(".jpg") || lfname.endsWith(".png")) && lfname.contains("_pk[")) {

                    // Apuntamos el JPG como un IPA "fantasma" para poder recolocar el nuevo
                    // Si sólo queda el JPG en la carpeta
                    File ipaFile = new File(f.getParentFile(), fname.substring(0, fname.length() - 3) + "ipa");
                    if (!ipaFile.exists()) {

                        String pkg = NameComposer.parsePkg(f.getName());
                        String ver = NameComposer.parseVerCanonical(f.getName());

                        TIPAInfo ii = new TIPAInfo();
                        ii.ipaFile = ipaFile;
                        ii.imgFile = f;
                        ii.pkg = pkg;
                        ii.ver = ver;

                        TIPAInfo oo = IPAs.put(ii.pkg, ii);
                        if (oo != null) {
                            Tracer._error("IPA (from old JPEG) file duplicated found reading folder info:");
                            Tracer._error("  " + ii.imgFile);
                            Tracer._error("  " + oo.ipaFile);
                        }
                    }
                }
            }
        }
    }
}
