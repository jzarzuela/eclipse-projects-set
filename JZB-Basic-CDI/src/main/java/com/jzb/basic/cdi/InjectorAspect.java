/**
 * 
 */
package com.jzb.basic.cdi;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.google.inject.Injector;

/**
 * @author jzarzuela
 * 
 */
@Aspect
public class InjectorAspect {

    private static Injector s_injector;

    // ----------------------------------------------------------------------------------------------------
    public static synchronized void _init(Injector injector) {
        s_injector = injector;
    }

    // ----------------------------------------------------------------------------------------------------
    @Around("execution(*.new(..)) && within(@Injectable *)")
    public Object aroundConstructorOrInjectableBeans(ProceedingJoinPoint joinPoint) throws Throwable {

        Object instance = joinPoint.getThis();
        _injectFields(instance);
        joinPoint.proceed();
        return instance;
    }

    // ----------------------------------------------------------------------------------------------------
    private void _injectFields(Object instance) {

        Injector injector = getInjector();
        if (injector != null) {
            s_injector.injectMembers(instance);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private Injector getInjector() {
        
        if (s_injector == null) {
            BasicCDI.init();
        }
        return s_injector;
    }
}
