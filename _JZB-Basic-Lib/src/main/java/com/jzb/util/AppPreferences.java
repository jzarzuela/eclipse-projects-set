/**
 * 
 */
package com.jzb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author n000013
 * 
 */
public class AppPreferences {

    private static final String DATE_FMT = "YYYY-MM-dd HH:mm:ss";
    Properties                  m_prefs  = new Properties();

    private String              m_name;

    public AppPreferences(String name) {

        if (name == null || name.trim().length() == 0) {
            m_name = "UnknownApp";
        } else {
            m_name = name.trim();
        }
    }

    public String getPref(String name) {
        return m_prefs.getProperty(name);
    }

    public String getPref(String name, String defValue) {
        String val = m_prefs.getProperty(name);
        if (val == null) {
            m_prefs.setProperty(name, defValue);
            val = defValue;
        }
        return val;
    }

    public Boolean getPrefBool(String name, boolean defValue) {
        String str = m_prefs.getProperty(name);
        if (str != null) {
            return Boolean.parseBoolean(str);
        } else {
            setPrefBool(name, defValue);
            return defValue;
        }
    }

    public Date getPrefDate(String name, Date defValue) throws ParseException {
        String str = m_prefs.getProperty(name);
        if (str != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FMT);
            return sdf.parse(str);
        } else {
            setPrefDate(name, defValue);
            return defValue;
        }
    }

    public long getPrefLong(String name, long defValue) {
        String str = m_prefs.getProperty(name);
        if (str != null) {
            return Long.parseLong(str);
        } else {
            setPrefLong(name, defValue);
            return defValue;
        }
    }

    public double getPrefDouble(String name, double defValue) {
        String str = m_prefs.getProperty(name);
        if (str != null) {
            return Double.parseDouble(str);
        } else {
            setPrefDouble(name, defValue);
            return defValue;
        }
    }
    
    public void load(boolean clearFirst) throws Exception {
        if (clearFirst)
            m_prefs.clear();

        File prefFile = getPrefFile();
        if (!prefFile.exists()) {
            return;
        }

        m_prefs.loadFromXML(new FileInputStream(prefFile));
    }

    public void save() throws Exception {
        File prefFile = getPrefFile();
        FileOutputStream fos = new FileOutputStream(prefFile);
        m_prefs.storeToXML(fos, "Preferences for '" + m_name + "'", "UTF-8");
        fos.close();
    }

    public String setPref(String name, String value) {
        return (String) m_prefs.setProperty(name, value);
    }

    public boolean setPrefBool(String name, boolean value) {
        String str = (String) m_prefs.setProperty(name, Boolean.toString(value));
        return str != null ? Boolean.parseBoolean(str) : false;
    }

    public Date setPrefDate(String name, Date value) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FMT);
        String str = (String) m_prefs.setProperty(name, sdf.format(value));
        return str != null ? sdf.parse(str) : null;
    }

    public long setPrefLong(String name, long value) {
        String str = (String) m_prefs.setProperty(name, Long.toString(value));
        return str != null ? Long.parseLong(str) : 0;
    }

    public long setPrefDouble(String name, double value) {
        String str = (String) m_prefs.setProperty(name, Double.toString(value));
        return str != null ? Long.parseLong(str) : 0;
    }
    
    public File getPrefFile() {

        File prefBaseFolder = new File(System.getProperty("user.home") + File.separatorChar + ".prefs");
        prefBaseFolder.mkdirs();
        File prefFile = new File(prefBaseFolder, m_name + ".xml");

        return prefFile;
    }
}
