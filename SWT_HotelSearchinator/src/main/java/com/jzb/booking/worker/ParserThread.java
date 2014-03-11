/**
 * 
 */
package com.jzb.booking.worker;

import com.jzb.booking.data.HotelDataParser;
import com.jzb.booking.data.PageDataParser;
import com.jzb.booking.data.THotelData;
import com.jzb.booking.data.TPageData;
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
                TPageData pageData = PageDataParser.parse(task.htmlText);

                // Si quedan mas paginas sigue
                if (pageData.nextPageUrl != null) {
                    Tracer._info("***** ParserWorker - Next page URL found. Queueing its processing");
                    m_owner.nextPageUrl(pageData.nextPageUrl);
                }

                // Parsea la informacion de los hoteles en la pagina
                for (THotelData hotel : pageData.hotels) {
                    try {
                        HotelDataParser.parse(hotel, pageData.numDays, pageData.lat, pageData.lng);
                        task.hotelDataList.add(hotel);
                    } catch (Throwable th) {
                        Tracer._error("Error processing hotel", th);
                    }
                }

                // Ya ha terminado
                m_owner.taskDone(task);

                Tracer._info("***** ParserWorker - Done processing ParserTask");

            } catch (Throwable th) {
                Tracer._error("***** ParserWorker - Error processing ParserTask", th);
            }
        }
    }

}
