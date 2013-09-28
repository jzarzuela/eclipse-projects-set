/**
 * 
 */
package com.jzb.fdf.srvc.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jzarzuela
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NeedsEntityManager {

    public static enum EM_Type {
        currentOne, newOne;
    }

    EM_Type value() default EM_Type.currentOne;

    boolean needsTrx();
}
