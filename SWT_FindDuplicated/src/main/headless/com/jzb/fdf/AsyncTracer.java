/**
 * 
 */
package com.jzb.fdf;

import java.io.PrintStream;

import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class AsyncTracer extends Tracer {

    private volatile transient int          m_cntTraces = 0;
    private volatile transient PrintStream  m_ps;
    private volatile transient StringBuffer m_sbTraces  = new StringBuffer();

    public AsyncTracer() {
        super(true);
        m_ps = System.out;
    }

    public AsyncTracer(PrintStream ps) {
        super(false);
        m_ps = ps;
    }

    @Override
    protected StringBuffer _getStringBuffer(Level level) {
        return m_sbTraces;
    }

    @Override
    protected synchronized int _incBufferCount(Level level) {
        return ++m_cntTraces;
    }

    @Override
    protected synchronized void _resetBufferCount(Level level) {
        m_cntTraces = 0;
    }

    @Override
    protected void _showTraceText(Level level, StringBuffer sb) {
        String fullMsg;
        synchronized (sb) {
            fullMsg = sb.toString();
            sb.setLength(0);
        }
        m_ps.print(fullMsg);
    }

}
