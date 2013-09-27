/**
 * 
 */
package com.jzb.ipa.swt;

import java.io.File;
import java.util.ArrayList;

import com.jzb.ipa.ren.IPARenamer;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class RenameWorker {

    private IProgressMonitor m_monitor;

    public RenameWorker(IProgressMonitor monitor) {
        m_monitor = monitor;
    }

    public void cleanJPEGs(final String existingFolder, final String updateFolder, final String iTunesFolder, final boolean recurse) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    Tracer._info("CleanWorker - Start execution");
                    _cleanJPEGs(_getOrigingFolders(existingFolder, updateFolder, iTunesFolder), recurse);
                    Tracer._info("CleanWorker - End execution");

                    m_monitor.processingEnded(false, "OK");
                } catch (Throwable th) {
                    Tracer._error("Error executing IPA CleanWorker", th);
                    m_monitor.processingEnded(true, "ERROR");
                }
            }

        }, "RenameWorker").start();
    }

    private void _rename(final File folders[], final boolean recurse) throws Exception {
        IPARenamer renamer = new IPARenamer();
        renamer.rename(folders, recurse);
    }

    public void rename(final String existingFolder, final String updateFolder, final String iTunesFolder, final boolean recurse) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    Tracer._info("RenameWorker - Start execution");
                    _rename(_getOrigingFolders(existingFolder, updateFolder, iTunesFolder), recurse);
                    Tracer._info("RenameWorker - End execution");

                    m_monitor.processingEnded(false, "OK");
                } catch (Throwable th) {
                    Tracer._error("Error executing IPA RenameWorker", th);
                    m_monitor.processingEnded(true, "ERROR");
                }
            }

        }, "RenameWorker").start();
    }

    private void _cleanJPEGs(final File folders[], final boolean recurse) throws Exception {
        IPARenamer renamer = new IPARenamer();
        renamer.cleanAllJPEGs(folders, recurse);
    }

    private File _getFolder(String name) throws Exception {

        File folder = name == null ? null : new File(name);
        if (folder != null && !folder.isDirectory()) {
            throw new Exception("Base folder is not correct: '" + name + "'");
        } else {
            return folder;
        }
    }

    private File[] _getOrigingFolders(String strExistingFolder, String strUpdateFolder, String strITunesFolder) throws Exception {

        ArrayList<File> folders = new ArrayList<File>();

        if (strExistingFolder != null)
            folders.add(_getFolder(strExistingFolder));

        if (strUpdateFolder != null)
            folders.add(_getFolder(strUpdateFolder));

        if (strITunesFolder != null) {
            File iTunesFolder = _getFolder(strITunesFolder);

            for (File f : iTunesFolder.listFiles()) {
                if (f.isDirectory()) {
                    File itf = new File(f, "iTunes Media\\Mobile Applications");
                    if (itf.exists()) {
                        folders.add(itf);
                    }
                }
            }
        }

        return folders.toArray(new File[0]);
    }

}
