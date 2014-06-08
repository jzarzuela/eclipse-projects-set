/**
 * 
 */
package com.jzb.ttpoi;

/**
 * @author jzarzuela
 * 
 */
public class OfflineMapBucket {

    private int    m_count;

    private String m_name;

    /**
     * 
     */
    public OfflineMapBucket(String name, int count) {
        m_name = name;
        m_count = count;
    }

    public int getCount() {
        return m_count;
    }

    public int incrementAndGet() {
        m_count++;
        return m_count;
    }

    public String getName() {
        return m_name;
    }

}
