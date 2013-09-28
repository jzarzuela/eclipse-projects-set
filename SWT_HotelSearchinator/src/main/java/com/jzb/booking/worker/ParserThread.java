/**
 * 
 */
package com.jzb.booking.worker;

import com.jzb.booking.data.PageRequestParser;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class ParserThread extends Thread {

    private static int   s_threadCount = 0;

    private ParserWorker m_owner;

    // ----------------------------------------------------------------------------------------------------
    private static synchronized int _getNextThreadCount() {
        return ++s_threadCount;
    }

    // ----------------------------------------------------------------------------------------------------
    public ParserThread(ParserWorker owner) {
        super("ParserThread-" + _getNextThreadCount());
        m_owner = owner;
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public synchronized void run() {

        for (;;) {
            try {
                
                Tracer._info("***** ParserWorker - Waiting for new ParserTask");
                ParserTask task = m_owner.getNextTask();

                Tracer._info("***** ParserWorker - Processing new ParserTask");
                PageRequestParser parser = new PageRequestParser();
                parser.initPage(task.baseURL, task.htmlText);

                String nextPageUrl = parser.searchNextPageLink();
                if (nextPageUrl != null) {
                    Tracer._info("***** ParserWorker - Next page URL found. Queueing its processing");
                    m_owner.nextPageUrl(nextPageUrl);
                }

                task.hotelDataList = parser.extractHotelInfo();

                m_owner.taskDone(task);
                
                Tracer._info("***** ParserWorker - Done processing ParserTask");
                
            } catch (Throwable th) {
                Tracer._error("***** ParserWorker - Error processing ParserTask", th);
            }
        }
    }
}
