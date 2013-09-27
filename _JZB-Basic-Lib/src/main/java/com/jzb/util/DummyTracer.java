/**
 * 
 */
package com.jzb.util;

/**
 * @author n63636
 * 
 */
public class DummyTracer extends Tracer {

    public DummyTracer(boolean buffered) {
        super(buffered);
    }

    protected void __debug(String msg) {
    }

    protected void __error(String msg) {
    }

    protected void __error(String msg, Throwable th) {
    }

    protected void __info(String msg) {
    }

    protected void __warn(String msg) {
    }

    protected void __warn(String msg, Throwable th) {
    }

    protected void _showTraceText(final Level level, final StringBuffer sb) {
    }
}
