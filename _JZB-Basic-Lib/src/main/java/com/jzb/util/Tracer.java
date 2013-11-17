/**
 * 
 */
package com.jzb.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author n63636
 * 
 */
public abstract class Tracer {

    public enum Level {
        DEBUG, ERROR, INFO, WARN
    }

    private static Tracer                       s_tracer             = new PStreamTracer();

    private volatile transient StringBuffer     m_sbDebug            = new StringBuffer();
    private volatile transient StringBuffer     m_sbError            = new StringBuffer();
    private volatile transient StringBuffer     m_sbInfo             = new StringBuffer();
    private volatile transient StringBuffer     m_sbWarn             = new StringBuffer();

    private volatile transient int              m_cntDebug           = 0;
    private volatile transient int              m_cntError           = 0;
    private volatile transient int              m_cntInfo            = 0;
    private volatile transient int              m_cntWarn            = 0;

    private volatile transient Timer            m_flushTimer;

    private volatile transient SimpleDateFormat m_sdf                = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");

    private volatile transient static boolean   s_debugEnabled       = true;
    private volatile transient static boolean   s_infoEnabled        = true;
    private volatile transient static boolean   s_warnEnabled        = true;
    private volatile transient static boolean   s_errorEnabled       = true;

    private volatile transient boolean          m_buffered;

    public Tracer(boolean buffered) {
        m_buffered = buffered;
        if (buffered) {
            m_flushTimer = new Timer("flushTimer", true);

            TimerTask flushTask = new TimerTask() {

                @Override
                public void run() {
                    Tracer.flush();
                }
            };

            m_flushTimer.schedule(flushTask, 2000, 2000);
        }
    }

    public static void setLevelEnabled(Level level, boolean enabled) {
        switch (level) {
            case DEBUG:
                s_debugEnabled = enabled;
                break;
            case INFO:
                s_infoEnabled = enabled;
                if (s_tracer._propagateLevels(level)) {
                    setLevelEnabled(Level.DEBUG, enabled);
                }
                break;
            case WARN:
                s_warnEnabled = enabled;
                break;
            case ERROR:
                s_errorEnabled = enabled;
                if (s_tracer._propagateLevels(level)) {
                    setLevelEnabled(Level.WARN, enabled);
                }
                break;
        }
    }

    public static boolean isLevelEnabled(Level level) {
        switch (level) {
            case DEBUG:
                return s_debugEnabled;
            case INFO:
                return s_infoEnabled;
            case WARN:
                return s_warnEnabled;
            case ERROR:
                return s_errorEnabled;
            default:
                return false;
        }
    }

    protected boolean _propagateLevels(Level l) {
        return false;
    }

    public static void _debug(String fmt, Object... args) {
        if (fmt != null) {
            StringBuilder sb = new StringBuilder();
            try (Formatter formatter = new Formatter(sb)) {
                formatter.format(fmt, args);
            }
            s_tracer.__debug(sb.toString());

        } else {
            s_tracer.__debug(null);
        }
    }

    public static void _debug(String msg) {
        if (msg != null)
            s_tracer.__debug(msg.toString());
        else
            s_tracer.__debug(null);
    }

    public static void _error(String fmt, Object... args) {
        if (fmt != null) {
            StringBuilder sb = new StringBuilder();
            try (Formatter formatter = new Formatter(sb)) {
                formatter.format(fmt, args);
            }
            s_tracer.__error(sb.toString());
        } else {
            s_tracer.__error(null);
        }
    }

    public static void _error(String fmt, Throwable th, Object... args) {
        if (fmt != null) {
            StringBuilder sb = new StringBuilder();
            try (Formatter formatter = new Formatter(sb)) {
                formatter.format(fmt, args);
            }
            s_tracer.__error(sb.toString(), th);
        } else {
            s_tracer.__error(null);
        }
    }

    public static void _error(String msg) {
        if (msg != null)
            s_tracer.__error(msg.toString());
        else
            s_tracer.__error(null);
    }

    public static void _error(String msg, Throwable th) {
        if (msg != null)
            s_tracer.__error(msg.toString(), th);
        else
            s_tracer.__error(null, th);
    }

    public static void _info(String fmt, Object... args) {
        if (fmt != null) {
            StringBuilder sb = new StringBuilder();
            try (Formatter formatter = new Formatter(sb)) {
                formatter.format(fmt, args);
            }
            s_tracer.__info(sb.toString());
        } else {
            s_tracer.__info(null);
        }
    }

    public static void _info(String msg) {
        if (msg != null)
            s_tracer.__info(msg.toString());
        else
            s_tracer.__info(null);
    }

    public static void _warn(String fmt, Object... args) {
        if (fmt != null) {
            StringBuilder sb = new StringBuilder();
            try (Formatter formatter = new Formatter(sb)) {
                formatter.format(fmt, args);
            }
            s_tracer.__warn(sb.toString());
        } else {
            s_tracer.__warn(null);
        }
    }

    public static void _warn(String fmt, Throwable th, Object... args) {
        if (fmt != null) {
            StringBuilder sb = new StringBuilder();
            try (Formatter formatter = new Formatter(sb)) {
                formatter.format(fmt, args);
            }
            s_tracer.__warn(sb.toString(), th);
        } else {
            s_tracer.__warn(null);
        }
    }

    public static void _warn(String msg) {
        if (msg != null)
            s_tracer.__warn(msg.toString());
        else
            s_tracer.__warn(null);
    }

    public static void _warn(String msg, Throwable th) {
        if (msg != null)
            s_tracer.__warn(msg.toString(), th);
        else
            s_tracer.__warn(null, th);
    }

    public static String getLevelText(Level level) {
        switch (level) {
            case DEBUG:
                return "D";
            case INFO:
                return "I";
            case WARN:
                return "W";
            case ERROR:
                return "E";
            default:
                return "U";
        }
    }

    public static Tracer getTracer() {
        return s_tracer;
    }

    public static void reset() {
        s_tracer._reset();
    }

    public static void flush() {
        s_tracer._flush();
    }

    public static void setTracer(Tracer tracer) {
        s_tracer = tracer;
    }

    protected void __debug(String msg) {
        _addTraceText(Level.DEBUG, msg);
    }

    protected void __error(String msg) {
        _addTraceText(Level.ERROR, msg);
        if (_propagateLevels(Level.ERROR)) {
            __warn(msg);
        }
    }

    protected void __error(String msg, Throwable th) {
        StringWriter sw = new StringWriter();
        if (th != null)
            th.printStackTrace(new PrintWriter(sw));
        _addTraceText(Level.ERROR, msg);
        _addTraceText(Level.ERROR, sw.getBuffer().toString());
        if (_propagateLevels(Level.ERROR)) {
            __warn(msg, th);
        }
    }

    protected void __info(String msg) {
        _addTraceText(Level.INFO, msg);
        if (_propagateLevels(Level.INFO)) {
            __debug(msg);
        }
    }

    protected void __warn(String msg) {
        _addTraceText(Level.WARN, msg);
    }

    protected void __warn(String msg, Throwable th) {
        StringWriter sw = new StringWriter();
        if (th != null)
            th.printStackTrace(new PrintWriter(sw));
        _addTraceText(Level.WARN, msg);
        _addTraceText(Level.WARN, sw.getBuffer().toString());
    }

    protected void _flush() {

        StringBuffer sb;

        for (Level level : Level.values()) {
            sb = _getStringBuffer(level);
            int len;
            synchronized (sb) {
                len = sb.length();
            }
            if (len > 0) {
                _showTraceText(level, sb);
                _resetBufferCount(level);
            }
        }

    }

    protected void _reset() {
    }

    protected abstract void _showTraceText(final Level level, final StringBuffer sb);

    private void _addTraceText(final Level level, final String msg) {

        if (isLevelEnabled(level)) {
            String threadName = Thread.currentThread().getName();
            String fullMsg = Tracer.getLevelText(level) + " " + m_sdf.format(System.currentTimeMillis()) + " " + threadName + "\t- " + (msg != null ? msg : "null") + "\r\n";

            int counter;
            StringBuffer sb = _getStringBuffer(level);
            synchronized (sb) {
                sb.append(fullMsg);
                counter = _incBufferCount(level);
            }

            if (!m_buffered || counter >= 50) {
                _resetBufferCount(level);
                _showTraceText(level, sb);
            }
        }
    }

    protected synchronized int _incBufferCount(Level level) {
        switch (level) {
            case DEBUG:
                return ++m_cntDebug;
            case INFO:
                return ++m_cntInfo;
            case WARN:
                return ++m_cntWarn;
            case ERROR:
                return ++m_cntError;
            default:
                // by default we return "debug"
                return ++m_cntDebug;
        }
    }

    protected synchronized void _resetBufferCount(Level level) {
        switch (level) {
            case DEBUG:
                m_cntDebug = 0;
            case INFO:
                m_cntInfo = 0;
            case WARN:
                m_cntWarn = 0;
            case ERROR:
                m_cntError = 0;
            default:
                // by default we return "debug"
                m_cntDebug = 0;
        }
    }

    protected StringBuffer _getStringBuffer(Level level) {
        switch (level) {
            case DEBUG:
                return m_sbDebug;
            case INFO:
                return m_sbInfo;
            case WARN:
                return m_sbWarn;
            case ERROR:
                return m_sbError;
            default:
                // by default we return "debug"
                return m_sbDebug;
        }
    }

}
