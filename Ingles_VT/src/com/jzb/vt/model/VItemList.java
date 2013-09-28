/**
 * Rigel Services Model Infrastructure, Version 1.0 Copyright (C) 2002 ISBAN. All Rights Reserved.
 */

package com.jzb.vt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

/**
 * @author PS00A501
 */
public class VItemList {

    private ArrayList m_list = new ArrayList();

    private ArrayList m_alreadyReadList = new ArrayList();

    private Iterator m_iterator = null;

    private VItem m_currentItem = null;

    private int m_max_times_read = -1;

    private int m_max_inv_times_read = -1;

    private boolean m_directTranslation;

    public VItemList() {
    }

    public void addVItem(VItem item) {
        int t_read = item.getTimesRead() - item.getTimesFailed();
        int t_i_read = item.getTimesInvRead() - item.getTimesInvFailed();

        if(m_max_times_read < t_read) m_max_times_read = t_read;
        if(m_max_inv_times_read < t_i_read) m_max_inv_times_read = t_i_read;
        m_list.add(item);
    }

    /**
     * @return the directTranslation
     */
    public boolean isDirectTranslation() {
        return m_directTranslation;
    }

    /**
     * @param directTranslation
     *            the directTranslation to set
     */
    public void setDirectTranslation(boolean directTranslation) {
        m_directTranslation = directTranslation;
    }

    public void organizeListAllTogether() {
        m_list.addAll(m_alreadyReadList);
        m_alreadyReadList.clear();
        m_iterator = m_list.iterator();
    }

    public void organizeListByThemeAndDate(Date aDate) {

        if (aDate != null) {
            for (Iterator iter = m_list.iterator(); iter.hasNext();) {
                VItem item = (VItem) iter.next();
                if (item.getCreationDate() == null || aDate.compareTo(item.getCreationDate()) >= 0) {
                    iter.remove();
                }
            }
        }

        Comparator comp = new Comparator() {
            /**
             * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
             */
            public int compare(Object arg0, Object arg1) {
                try {
                    int comp = ((VItem) arg0).getTheme().compareTo(((VItem) arg1).getTheme());
                    if (comp == 0) {
                        comp = ((VItem) arg0).getNativeText().compareTo(((VItem) arg1).getNativeText());
                    }
                    return comp;
                } catch (Throwable th) {
                    return 0;
                }
            }
        };
        Collections.sort(m_list, comp);

        m_iterator = m_list.iterator();

    }

    public void organizeListByAlreadyRead() {

        int maxTimes = m_directTranslation ? m_max_times_read : m_max_inv_times_read;
        for (Iterator iter = m_list.iterator(); iter.hasNext();) {
            
            VItem item = (VItem) iter.next();
            
            int timesRead = m_directTranslation ? item.getTimesRead() : item.getTimesInvRead();
            int timesFailed = m_directTranslation ? item.getTimesFailed() : item.getTimesInvFailed();
            if(timesFailed>0) {
                timesFailed=timesFailed+1-1;
            }
            
            if ((timesRead - timesFailed) >= maxTimes) {
                iter.remove();
                m_alreadyReadList.add(item);
            }
        }

        if (m_list.size() == 0) {
            ArrayList al = m_alreadyReadList;
            m_alreadyReadList = m_list;
            m_list = al;
        }

        m_iterator = new AlreadyReadIterator();
    }

    private class AlreadyReadIterator implements Iterator {

        public AlreadyReadIterator() {
        }

        /**
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return m_list.size() > 0;
        }

        /**
         * @see java.util.Iterator#next()
         */
        public Object next() {
            int next = (int) (Math.random() * (double) m_list.size());
            m_currentItem = (VItem) m_list.remove(next);
            m_alreadyReadList.add(m_currentItem);
            return m_currentItem;
        }

        /**
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            // TODO Auto-generated method stub

        }
    }

    public boolean hasNextVitem() {
        return m_iterator.hasNext();
    }

    public VItem moveOnNextVItem() {
        m_currentItem = (VItem) m_iterator.next();
        return m_currentItem;
    }

    /**
     * @return the currentItem
     */
    public VItem getCurrentItem() {
        return m_currentItem;
    }

    public String getDirectText() {
        return m_directTranslation ? m_currentItem.getNativeText() : m_currentItem.getTranslatedText();
    }

    public String getInvText() {
        return !m_directTranslation ? m_currentItem.getNativeText() : m_currentItem.getTranslatedText();
    }

    public void increaseFailed() {
        if (m_directTranslation)
            m_currentItem.increaseTimesFailed();
        else
            m_currentItem.increaseInvTimesFailed();
    }

    public void markAsRead() {
        if (m_directTranslation)
            m_currentItem.increaseTimesRead();
        else
            m_currentItem.increaseInvTimesRead();
    }
    
    public String getTheme() {
        return m_currentItem.getTheme();
    }
    
}
