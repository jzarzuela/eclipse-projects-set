/**
 * 
 */
package com.jzb.swt.util;

import java.io.File;

import com.jzb.util.Tracer;

/**
 * @author n000013
 * 
 */
public abstract class BaseWorker {

    protected interface ICallable {

        public Object call() throws Exception;
    }

    protected boolean          m_justChecking;
    protected IProgressMonitor m_monitor;

    public BaseWorker(boolean justChecking, IProgressMonitor monitor) {
        m_justChecking = justChecking;
        m_monitor = monitor;
    }

    protected void _makeCall(final String baseFolderStr, final ICallable callable) {

        try {

            final File baseFolder = baseFolderStr == null ? null : new File(baseFolderStr);
            if (baseFolder != null && !baseFolder.isDirectory())
                throw new Exception("Indicated folder is not correct: '" + baseFolder + "'");

            Runnable r = new Runnable() {

                public void run() {
                    try {

                        Tracer._info("Processing started");

                        Object result = callable.call();

                        Tracer._info("Processing finished");
                        m_monitor.processingEnded(false, result);

                    } catch (Throwable ex) {
                        Tracer._error("Error in processing execution", ex);
                        m_monitor.processingEnded(true, null);
                    }
                }
            };

            new Thread(r, "WorkerThread").start();
        } catch (Throwable th) {
            Tracer._error("Error in processing execution", th);
            m_monitor.processingEnded(true, null);
        }
    }

    protected void _makeCall(final ICallable callable) {

        try {

            Runnable r = new Runnable() {

                public void run() {
                    try {

                        Tracer._info("Processing started");

                        Object result = callable.call();

                        Tracer._info("Processing finished");
                        m_monitor.processingEnded(false, result);

                    } catch (Throwable ex) {
                        Tracer._info("ERROR in processing execution");
                        Tracer._error("Error in processing execution", ex);
                        m_monitor.processingEnded(true, null);
                    }
                }
            };

            new Thread(r, "WorkerThread").start();
        } catch (Throwable th) {
            Tracer._error("Error in processing execution", th);
            m_monitor.processingEnded(true, null);
        }
    }

}
