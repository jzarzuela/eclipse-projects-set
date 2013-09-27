/**
 * 
 */
package com.jzb.util;

import java.io.PrintStream;


/**
 * @author n000013
 * 
 */
public class PStreamTracer extends Tracer {

    private PrintStream m_ps;

    public PStreamTracer() {
        super(false);
        m_ps = System.out;
    }

    public PStreamTracer(PrintStream ps) {
        super(false);
        m_ps = ps;
    }

    
    @Override
    protected void _showTraceText(Level level, StringBuffer sb) {
        String fullMsg;
        synchronized(sb) {
            fullMsg=sb.toString();
            sb.setLength(0);
        }
        m_ps.print(fullMsg);
    }


}
