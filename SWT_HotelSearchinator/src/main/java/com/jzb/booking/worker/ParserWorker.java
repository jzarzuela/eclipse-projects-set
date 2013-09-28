/**
 * 
 */
package com.jzb.booking.worker;

import java.util.LinkedList;
import com.jzb.booking.worker.ParserWorkerStatus.PWStatusType;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class ParserWorker {

    private static int             NUM_THREADS  = 5;

    private int                    m_currentJobCounter;
    private String                 m_currentJobID;
    private IProgressMonitor       m_monitor;
    private int                    m_numTasksForJob;
    private LinkedList<ParserTask> m_tasksQueue = new LinkedList<ParserTask>();

    // ----------------------------------------------------------------------------------------------------
    public ParserWorker(IProgressMonitor monitor) {
        m_monitor = monitor;

        for (int n = 0; n < NUM_THREADS; n++) {
            new ParserThread(this).start();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    public synchronized void parseHtmlText(final String baseURL, final String htmlText) {

        // Si es la primera tarea del trabajo, actualiza el contador
        if (m_numTasksForJob == 0) {
            m_numTasksForJob++;
        }

        ParserTask task = new ParserTask();
        task.jobID = getCurrentJobID();
        task.htmlText = htmlText;
        task.baseURL = baseURL;
        try {
            m_tasksQueue.add(task);
            this.notifyAll();
        } catch (Exception ex) {
            Tracer._error("Error putting a new ParserTask in the queue", ex);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    public synchronized void reset() {
        Tracer._info("***** Reset parsing JOB ****************************************************");
        Tracer._info("");
        m_numTasksForJob = 0;
        createNewJobID();
    }

    // ----------------------------------------------------------------------------------------------------
    protected synchronized ParserTask getNextTask() {

        // Espera a tener una tarea para el JobID en curso
        // Se descartan tareas con JobIDs diferentes
        for (;;) {

            if (m_tasksQueue.isEmpty()) {
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    Tracer._warn("Error waiting for a new ParserTask in queue", ex);
                }
            } else {
                ParserTask task = m_tasksQueue.poll();
                if (task != null && task.jobID.equals(getCurrentJobID())) {
                    return task;
                } else {
                    Tracer._debug("Discarding task from previous job");
                }
            }

        }
    }

    // ----------------------------------------------------------------------------------------------------
    protected synchronized void taskDone(ParserTask task) {

        // Solo hace algo si la tarea finalizada era del trabajo en curso y no una antigua
        if (task.jobID.equals(getCurrentJobID())) {

            // Crea el status
            ParserWorkerStatus pws = new ParserWorkerStatus();
            pws.hotelDataList = task.hotelDataList;

            // Decrementa el numero de tareas en ejecucion
            m_numTasksForJob--;

            // Actua dependiendo de lo que quede
            if (m_numTasksForJob > 0) {

                // **** AVISAR DE QUE SE HA TERMINADO CON UNA DE LAS PAGINAS
                pws.type = PWStatusType.PageEnded;
                m_monitor.processingEnded(false, pws);

            } else if (m_numTasksForJob == 0) {

                // **** AVISAR DE QUE SE HA TERMINADO CON TODAS LAS PAGINAS
                Tracer._info("***** All parsing tasks have ended *********************************");
                pws.type = PWStatusType.AllPagesEnded;
                m_monitor.processingEnded(false, pws);

            } else {
                // No debería salir una cuenta negativa
                String MSG = "Task count for job decremented below zero";
                Tracer._error(MSG);
                m_monitor.processingEnded(true, MSG);
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    protected synchronized void nextPageUrl(String nextPageUrl) {

        // Incrementa el numero de tareas que tiene este trabajo
        m_numTasksForJob++;

        // *** AVISA PARA QUE SE PARSEE LA SIGUIENTE PAGINA DEL TRABAJO
        ParserWorkerStatus pws = new ParserWorkerStatus();
        pws.type = PWStatusType.ParseNextPage;
        pws.nextPageUrl = nextPageUrl;
        m_monitor.processingEnded(false, pws);
    }

    // ----------------------------------------------------------------------------------------------------
    private synchronized String createNewJobID() {
        m_currentJobID = Long.toHexString(System.currentTimeMillis()) + "-" + Integer.toHexString(m_currentJobCounter);
        m_currentJobCounter++;
        m_numTasksForJob = 0;
        return m_currentJobID;
    }

    // ----------------------------------------------------------------------------------------------------
    private synchronized String getCurrentJobID() {
        return m_currentJobID;
    }

}
