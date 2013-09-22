/**
 * 
 */
package com.jzb.basic.cdi;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author jzarzuela
 * 
 */
public class BasicCDI {

    // ----------------------------------------------------------------------------------------------------
    public static synchronized void init(Injector injector) {
        InjectorAspect._init(injector);
    }

    // ----------------------------------------------------------------------------------------------------
    public static synchronized void init() {
        
        InjectorAspect._init(Guice.createInjector(new CPScanInjectorModule()));
    }

}
