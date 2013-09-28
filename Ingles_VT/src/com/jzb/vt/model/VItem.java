/**
 * Rigel Services Model Infrastructure, Version 1.0 Copyright (C) 2002 ISBAN. All Rights Reserved.
 */

package com.jzb.vt.model;

import java.util.Date;

/**
 * @author PS00A501
 */
public class VItem {

    private int m_rowIndex;
    
    private String m_nativeText;

    private String m_translatedText;

    private Date m_creationDate;

    private String m_theme;

    private int m_timesRead;

    private int m_timesFailed;

    private int m_timesInvRead;

    private int m_timesInvFailed;

    public VItem() {
    }

    /**
     * @return the nativeText
     */
    public String getNativeText() {
        return m_nativeText;
    }

    /**
     * @param nativeText
     *            the nativeText to set
     */
    public void setNativeText(String nativeText) {
        m_nativeText = nativeText;
    }

    /**
     * @return the translateText
     */
    public String getTranslatedText() {
        return m_translatedText;
    }

    /**
     * @param translateText
     *            the translateText to set
     */
    public void setTranslatedText(String translateText) {
        m_translatedText = translateText;
    }

    /**
     * @return the creationDate
     */
    public Date getCreationDate() {
        return m_creationDate;
    }

    /**
     * @param creationDate
     *            the creationDate to set
     */
    public void setCreationDate(Date creationDate) {
        m_creationDate = creationDate;
    }

    /**
     * @return the theme
     */
    public String getTheme() {
        return m_theme;
    }

    /**
     * @param theme
     *            the theme to set
     */
    public void setTheme(String theme) {
        m_theme = theme;
    }

    /**
     * @return the timesFailed
     */
    public int getTimesFailed() {
        return m_timesFailed;
    }

    /**
     * @param timesFailed
     *            the timesFailed to set
     */
    public void setTimesFailed(int timesFailed) {
        m_timesFailed = timesFailed;
    }

    /**
     * @return the timesInvFailed
     */
    public int getTimesInvFailed() {
        return m_timesInvFailed;
    }

    /**
     * @param timesInvFailed
     *            the timesInvFailed to set
     */
    public void setTimesInvFailed(int timesInvFailed) {
        m_timesInvFailed = timesInvFailed;
    }

    /**
     * @return the timesInvRead
     */
    public int getTimesInvRead() {
        return m_timesInvRead;
    }

    /**
     * @param timesInvRead
     *            the timesInvRead to set
     */
    public void setTimesInvRead(int timesInvRead) {
        m_timesInvRead = timesInvRead;
    }

    /**
     * @return the timesRead
     */
    public int getTimesRead() {
        return m_timesRead;
    }

    /**
     * @param timesRead
     *            the timesRead to set
     */
    public void setTimesRead(int timesRead) {
        m_timesRead = timesRead;
    }

    public int increaseTimesRead() {
        m_timesRead++;
        return m_timesRead;
    }

    public int increaseTimesFailed() {
        m_timesFailed++;
        return m_timesFailed;
    }

    public int increaseInvTimesRead() {
        m_timesInvRead++;
        return m_timesInvRead;
    }

    public int increaseInvTimesFailed() {
        m_timesInvFailed++;
        return m_timesInvFailed;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "'" + m_nativeText + "', '" + m_translatedText + "', " + m_creationDate + ", '" + m_theme + "', " + m_timesRead + ", "
                + m_timesFailed + ", " + m_timesInvRead + ", " + m_timesInvFailed;
    }

    /**
     * @return the rowIndex
     */
    public int getRowIndex() {
        return m_rowIndex;
    }

    /**
     * @param rowIndex the rowIndex to set
     */
    public void setRowIndex(int rowIndex) {
        m_rowIndex = rowIndex;
    }
}
