/**
 * 
 */
package com.jzb.fdf.prcs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.jzb.fdf.srvc.IFolderSrvc;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class FolderProcessor {

    private static final File[]    EMPTY_FILES                      = new File[0];
    private static final int       MAX_FOLDER_EXECUTORS             = 4 * 4;
    private static MyAtomicInteger s_completedFolderProcessorsCount = new MyAtomicInteger(0);
    private static MyAtomicInteger s_executingFolderProcessorsCount = new MyAtomicInteger(0);
    private static CountDownLatch  s_executionCompletedLatch;
    private static IFileFilter     s_fileFilter                     = new DefaultFileFilter();
    private static ExecutorService s_folderExecutor;

    // ----------------------------------------------------------------------------------------------------
    private FolderProcessor() {
    }

    // ----------------------------------------------------------------------------------------------------
    public static void _init() {
        s_folderExecutor = Executors.newFixedThreadPool(MAX_FOLDER_EXECUTORS);
        s_executionCompletedLatch = new CountDownLatch(1);
        s_executingFolderProcessorsCount.set(0);
        s_completedFolderProcessorsCount.set(0);
        FileProcessor._init();
    }

    // ----------------------------------------------------------------------------------------------------
    public static void awaitTermination() throws InterruptedException {

        Tracer._info("Folder Processor - Awaiting termination");
        if (s_executingFolderProcessorsCount.get() > 0) {
            s_executionCompletedLatch.await();
        }
        s_folderExecutor.shutdown();
        s_folderExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        FileProcessor.awaitTermination();
        Tracer._info("Folder Processor - ALL FOLDER PROCESSORS ARE DONE");
    }

    // ----------------------------------------------------------------------------------------------------
    public static void cancel() {

        // Manda cancelar todo y libera las cuentas de las tareas pendientes de ejecucion
        Tracer._warn("Folder Processor - Canceling execution");
        s_folderExecutor.shutdownNow();
        s_executionCompletedLatch.countDown();
        s_executingFolderProcessorsCount.set(0);

        // Lo mismo con los procesadores de los ficheros
        FileProcessor.cancel();
    }

    // ----------------------------------------------------------------------------------------------------
    public static int completedProcessorsCount() {
        return s_completedFolderProcessorsCount.get() + FileProcessor.completedProcessorsCount();
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @return the fileFilter
     */
    public static IFileFilter getFileFilter() {
        return s_fileFilter;
    }

    // ----------------------------------------------------------------------------------------------------
    public static int pendingProcessorsCount() {

        return s_executingFolderProcessorsCount.get() + FileProcessor.pendingProcessorsCount();
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param fileFilter
     *            the fileFilter to set
     */
    public static void setFileFilter(IFileFilter fileFilter) {
        s_fileFilter = fileFilter;
    }

    // ----------------------------------------------------------------------------------------------------
    public static void spawnNewProcessor(final File folder) {

        // Tracer._debug("Folder Processor - Spawning new instance for '" + folder + "'");
        _executionStartAccounting();

        try {
            s_folderExecutor.execute(new Runnable() {

                @Override
                @SuppressWarnings("synthetic-access")
                public void run() {

                    try {

                        // Tracer._debug("Folder Processor - Starting processing for '" + folder + "'");
                        FolderProcessor fp = new FolderProcessor();
                        fp._processFolder(folder);
                        // Tracer._debug("* Folder Processor - Processing has finished for '" + folder + "'");

                    } catch (Throwable th) {
                        Tracer._error("? Folder Processor - Error while processing '" + folder + "'", th);
                        FolderProcessor.cancel();
                    } finally {

                        // Avisa de que ha terminado
                        _executionEndAccounting();
                    }
                }
            });
        } catch (Throwable th) {
            // La ejecucion ha sido cancelada... A todos los efectos esta finalizada
            _executionEndAccounting();
            FolderProcessor.cancel();
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _executionEndAccounting() {
        s_completedFolderProcessorsCount.incrementAndGet();
        if (s_executingFolderProcessorsCount.decrementUntilZero() == 0) {
            if (s_executionCompletedLatch.getCount() > 0) {
                Tracer._info("FolderProcessor - ******************************************************************");
                Tracer._info("FolderProcessor - All folders have been processed. Waiting for files to be processed");
                Tracer._info("FolderProcessor - ******************************************************************");
            }
            s_executionCompletedLatch.countDown();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _executionStartAccounting() {
        s_executingFolderProcessorsCount.incrementAndGet();
    }

    // ----------------------------------------------------------------------------------------------------
    private HashMap<String, File> _getFilesAsMap(File folder) {

        File files[] = folder.listFiles();
        files = files != null ? files : EMPTY_FILES;

        IFileFilter filter = getFileFilter();

        HashMap<String, File> allFiles = new HashMap();
        for (File file : files) {
            if (!filter.isFiltered(file)) {
                allFiles.put(file.getName(), file);
            }
        }
        return allFiles;
    }

    // ----------------------------------------------------------------------------------------------------
    private void _processFolder(File folder) {

        // Consigue la informacion de la carpeta a procesar
        HashMap<String, File> mappedFolderContent = _getFilesAsMap(folder);

        // Procesa cambios para la carpeta actual
        ArrayList<File> listToProcess = IFolderSrvc.inst.compareAndCleanFolder(folder, mappedFolderContent);

        // Itera los subelementos para procesarlos
        for (File file : listToProcess) {

            if (s_folderExecutor.isShutdown())
                break;

            if (file.isDirectory()) {
                FolderProcessor.spawnNewProcessor(file);
            } else {
                FileProcessor.spawnNewProcessor(folder, file.getName());
            }
        }
    }

}
