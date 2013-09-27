/**
 * 
 */
package com.jzb.ipa.swt;

import java.io.File;

import com.jzb.ipa.chk.IPAChecker;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class CheckerWorker {

    private IProgressMonitor m_monitor;

    public CheckerWorker(IProgressMonitor monitor) {
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

    public void check(final String iphoneFolder, final String ipadFolder, final String mixedFolder) {
        new Thread(new Runnable() {

            public void run() {
                try {

                    Tracer._info("CheckWorker - Start execution");
                    _check(_getFolder(iphoneFolder), _getFolder(ipadFolder), _getFolder(mixedFolder));
                    Tracer._info("CheckWorker - End execution");

                    m_monitor.processingEnded(false, "OK");
                } catch (Throwable th) {
                    Tracer._error("Error executing IPA UpdateWorker", th);
                    m_monitor.processingEnded(true, "ERROR");
                }
            }

        }, "UpdateWorker").start();
    }

    private void _check(final File iphoneFolder, final File ipadFolder, final File mixedFolder) throws Exception {

        IPAChecker checker = new IPAChecker();
        checker.check(iphoneFolder, ipadFolder, mixedFolder);
    }


}
