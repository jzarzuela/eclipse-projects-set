/**
 * 
 */
package com.jzb.ipa.swt;

import java.io.File;
import java.util.ArrayList;

import com.jzb.ipa.upd.IPAUpdater;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class UpdateWorker {

    private IProgressMonitor m_monitor;

    public UpdateWorker(IProgressMonitor monitor) {
        m_monitor = monitor;
    }

    private File _getFolder(String name) throws Exception {

        File folder = name == null ? null : new File(name);
        if (folder != null && !folder.isDirectory()) {
            throw new Exception("Base folder is not correct: '" + name + "'");
        } else {
            return folder;
        }
    }

    public void update(final String ExistingFolder, final String updateFolder, final String iTunesFolder) {
        new Thread(new Runnable() {

            public void run() {
                try {

                    Tracer._info("UpdateWorker - Start execution");
                    _update(_getFolder(ExistingFolder), _getFolder(updateFolder), _getFolder(iTunesFolder));
                    Tracer._info("UpdateWorker - End execution");

                    m_monitor.processingEnded(false, "OK");
                } catch (Throwable th) {
                    Tracer._error("Error executing IPA UpdateWorker", th);
                    m_monitor.processingEnded(true, "ERROR");
                }
            }

        }, "UpdateWorker").start();
    }

    private void _update(final File ExistingFolder, final File updateFolder, final File iTunesFolder) throws Exception {

        File backupFolder = new File(updateFolder, "_backup");
        File newfFolder = new File(updateFolder, "_new");
        backupFolder.mkdirs();
        newfFolder.mkdirs();

        IPAUpdater updater = new IPAUpdater();
        updater.update(ExistingFolder, _getOrigingFolders(updateFolder, iTunesFolder), backupFolder, newfFolder);
    }

    private File[] _getOrigingFolders(File updateFolder, File iTunesFolder) {

        ArrayList<File> folders = new ArrayList<File>();
        folders.add(updateFolder);

        for (File f : iTunesFolder.listFiles()) {
            if (f.isDirectory()) {
                File itf = new File(f, "iTunes Media"+File.separator+"Mobile Applications");
                if (itf.exists()) {
                    folders.add(itf);
                }
            }
        }

        return folders.toArray(new File[0]);
    }

}
