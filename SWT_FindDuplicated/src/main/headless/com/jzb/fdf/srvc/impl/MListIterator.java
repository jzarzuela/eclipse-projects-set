/**
 * 
 */
package com.jzb.fdf.srvc.impl;

import java.util.List;

import javax.persistence.EntityManager;

import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
class MListIterator {

    // ----------------------------------------------------------------------------------------------------
    public MListIterator(int commitCount) {
    }

    // ----------------------------------------------------------------------------------------------------
    public static <T> void processList(EntityManager em, int commitCount, List<T> list, IItemProcessor<T> processor) throws Exception {

        try {
            int count1 = 0, count2 = 0;
            Tracer._debug("MListProcessor - Processing list items. Count: " + list.size());
            em.getTransaction().begin();
            for (T item : list) {
                processor.processItem(item);
                count1++;
                count2++;
                if (count1 >= commitCount) {
                    em.getTransaction().commit();
                    em.getTransaction().begin();
                    count1 = 0;
                    Tracer._debug("MListProcessor - Item processed: " + count2 + ", out of: " + list.size());
                }
            }
            em.getTransaction().commit();
        } catch (Throwable th) {
            Tracer._error("MListProcessor - Error processing item list", th);
            throw th;
        }

    }

}
