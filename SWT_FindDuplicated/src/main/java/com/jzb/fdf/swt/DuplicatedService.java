/**
 * 
 */
package com.jzb.fdf.swt;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jzb.fdf.prcs.FolderProcessor;
import com.jzb.fdf.srvc.IModelSrvc;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class DuplicatedService {

    // ----------------------------------------------------------------------------------------------------
    public static interface ICallBack {

        public void processingEnded();

        public void updateProgress(String completedStr, String pendingStr, String endingTimeStr);
    }

    // ----------------------------------------------------------------------------------------------------
    private static class DummyCallBack implements ICallBack {

        public DummyCallBack() {
        }

        @Override
        public void processingEnded() {
        }

        @Override
        public void updateProgress(String completedStr, String pendingStr, String endingTimeStr) {
            Tracer._debug("DuplicatedService - Processing progress update. Completed = %s, Pending = %s, Ending Time = %s", completedStr, pendingStr, endingTimeStr);
        }
    }

    private DecimalFormat    m_nf  = new DecimalFormat("#,##0");

    private SimpleDateFormat m_sdf = new SimpleDateFormat("HH:mm:ss");

    // ----------------------------------------------------------------------------------------------------
    public DuplicatedService() {

        // Inicializa todo forma rapida
        FolderProcessor._init();
        IModelSrvc.inst.init(false);
    }

    // ----------------------------------------------------------------------------------------------------
    public void cancelProcessing() {
        Tracer._info("DuplicatedService - Canceling processing execution. Please Wait...");
        FolderProcessor.cancel();
    }

    // ----------------------------------------------------------------------------------------------------
    public void doProcessing(final boolean cleanDB, final String folders[], final ICallBack callback) {

        final ICallBack mycallback = callback != null ? callback : new DummyCallBack();
        new Thread(new Runnable() {

            @Override
            @SuppressWarnings("synthetic-access")
            public void run() {

                try {
                    Tracer._info("DuplicatedService - Start processing execution");
                    _doProcessing(cleanDB, folders, mycallback);
                    Tracer._info("DuplicatedService - Ended processing execution");
                } catch (Throwable th) {
                    Tracer._error("DuplicatedService - Error processing execution", th);
                }

                mycallback.processingEnded();
            }

        }, "DS-Thread").start();
    }

    // ----------------------------------------------------------------------------------------------------
    private void _doProcessing(final boolean cleanDB, final String folders[], final ICallBack callback) throws Exception {

        // Inicializa el modelo y los procesadores
        FolderProcessor._init();
        IModelSrvc.inst.init(cleanDB);

        // Itera las carpeta y las pone en procesamiento
        for (String folderStr : folders) {
            File folder = new File(folderStr);
            if (folder.exists()) {
                FolderProcessor.spawnNewProcessor(folder);
            } else {
                Tracer._warn("Folder doesn't exist: " + folder);
            }
        }

        // Mientras este procesando va actualizando el estado
        long initialTime = System.currentTimeMillis();
        while (FolderProcessor.pendingProcessorsCount() > 0) {

            Thread.sleep(1000);
            _updateProgress(callback, initialTime);
        }

        // Queda a la espera de la finalizacion del proceso
        FolderProcessor.awaitTermination();
        IModelSrvc.inst.done();
    }

    // ----------------------------------------------------------------------------------------------------
    private void _updateProgress(final ICallBack callback, final long initialTime) {

        double t_diff = System.currentTimeMillis() - initialTime;
        int completed = FolderProcessor.completedProcessorsCount();
        int pending = FolderProcessor.pendingProcessorsCount();

        String endingTimeStr = "unknown";
        long remainingTime = -1;
        if (completed > 0) {
            remainingTime = (long) (pending * (t_diff / completed));
            long endingTime = System.currentTimeMillis() + remainingTime;
            endingTimeStr = m_sdf.format(new Date(endingTime));
        }

        callback.updateProgress(m_nf.format(completed), m_nf.format(pending), endingTimeStr);
    }
}
