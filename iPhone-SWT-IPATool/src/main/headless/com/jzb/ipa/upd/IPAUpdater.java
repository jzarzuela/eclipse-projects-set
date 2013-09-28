/**
 * 
 */
package com.jzb.ipa.upd;

import java.io.File;
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

    private HashMap<String, TIPAInfo> m_existingIPAs = new HashMap<String, TIPAInfo>();
    private IPARenamer                m_ipaRenamer   = new IPARenamer();
    private HashMap<String, TIPAInfo> m_updateIPAs   = new HashMap<String, TIPAInfo>();

    public void update(File ExistingFolder, File updateFolders[], File backupFolder, File newfFolder) throws Exception {

        Tracer._info("Reading info for already existing IPAs");
        _readIPAsFromFolder(ExistingFolder, m_existingIPAs);

        Tracer._info("Reading info for new updated IPAs");
        for (File f : updateFolders) {
            _readIPAsFromFolder(f, m_updateIPAs);
        }

        Tracer._info("Checking for IPAs that have been updated");
        _checkForUpdates(backupFolder);

        Tracer._info("Moving completely new IPAs to folder: " + newfFolder);
        _moveNewFiles(newfFolder);

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
                    if (fname.toLowerCase().endsWith(".jpg") && fname.toLowerCase().contains("_pk[")) {
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
                        if (!exIPA.ipaFile.renameTo(newFile)) {
                            Tracer._error("Moving IPA file to backup folder: " + newFile);
                        }
                    }
                    if (exIPA.imgFile != null && exIPA.imgFile.exists()) {
                        newFile = new File(backupFolder, exIPA.imgFile.getName());
                        if (!exIPA.imgFile.renameTo(newFile)) {
                            Tracer._error("Moving JPEG_IPA file to backup folder: " + newFile);
                        }
                    }

                    Tracer._debug("Moving new files to existing folder");
                    if (upIPA.ipaFile != null && upIPA.ipaFile.exists()) {
                        newFile = new File(exIPA.ipaFile.getParentFile(), upIPA.ipaFile.getName());
                        if (!upIPA.ipaFile.renameTo(newFile)) {
                            Tracer._error("Moving new IPA file to existing folder: " + newFile);
                        }
                    }
                    if (upIPA.imgFile != null && upIPA.imgFile.exists()) {
                        newFile = new File(exIPA.ipaFile.getParentFile(), upIPA.imgFile.getName());
                        if (!upIPA.imgFile.renameTo(newFile)) {
                            Tracer._error("Moving new JPEG_IPA file to existing folder: " + newFile);
                        }
                    }
                } else {
                    Tracer._info("Update IPA is older. It'll be descarted: " + upIPA.ipaFile);

                    Tracer._debug("Moving update files to backup folder");
                    File newFile;
                    newFile = new File(backupFolder, upIPA.ipaFile.getName());
                    if (!upIPA.ipaFile.renameTo(newFile)) {
                        Tracer._error("Moving IPA file to backup folder: " + newFile);
                    }
                    if (upIPA.imgFile != null) {
                        newFile = new File(backupFolder, upIPA.imgFile.getName());
                        if (!upIPA.imgFile.renameTo(newFile)) {
                            Tracer._error("Moving JPEG_IPA file to backup folder: " + newFile);
                        }
                    }
                }
            }
        }
    }

    private void _moveNewFiles(File newfFolder) {

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
            File newSubFolder = new File(newfFolder, legalFolder + upIPA.ipaFile.getName().substring(1, 2));
            newSubFolder.mkdirs();

            newFile = new File(newSubFolder, upIPA.ipaFile.getName());
            if (!upIPA.ipaFile.renameTo(newFile)) {
                Tracer._error("Moving IPA file to folder: " + newFile);
            }
            if (upIPA.imgFile != null) {
                newFile = new File(newSubFolder, upIPA.imgFile.getName());
                if (!upIPA.imgFile.renameTo(newFile)) {
                    Tracer._error("Moving JPEG_IPA file to folder: " + newFile);
                }
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

    private void _readIPAsFromFolder(File folder, HashMap<String, TIPAInfo> IPAs) throws Exception {

        Tracer._debug("Reading info for IPA files in: " + folder);

        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                _readIPAsFromFolder(f, IPAs);
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
                        }
                    }
                } else if (lfname.endsWith(".jpg") && lfname.contains("_pk[")) {

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
