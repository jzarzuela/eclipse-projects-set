/**
 * 
 */
package com.jzb.fdf.prcs;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jzarzuela
 * 
 */
public class MyAtomicInteger extends AtomicInteger {

    private static final long serialVersionUID = 6214790243416807050L;

    public MyAtomicInteger() {
        this(0);
    }

    public MyAtomicInteger(int initialValue) {
        super(initialValue);
    }

    public int decrementUntilZero() {

        // Decrementa solo hasta alcanzar cero
        for (;;) {
            int current = get();
            if (current == 0)
                return current;
            int next = current - 1;
            if (compareAndSet(current, next))
                return next;
        }
    }

}
