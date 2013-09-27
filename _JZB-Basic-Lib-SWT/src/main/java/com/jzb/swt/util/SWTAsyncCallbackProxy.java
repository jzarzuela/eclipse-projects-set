/**
 * 
 */
package com.jzb.swt.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.eclipse.swt.widgets.Display;

import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class SWTAsyncCallbackProxy implements InvocationHandler {

    final private Object m_callbackImpl;

    // ----------------------------------------------------------------------------------------------------
    public static <T> T createProxy(Object callbackObj, Class<T> callbackInteface) {

        if (callbackObj == null)
            return null;

        SWTAsyncCallbackProxy me = new SWTAsyncCallbackProxy(callbackObj);
        T proxy = (T) Proxy.newProxyInstance(callbackObj.getClass().getClassLoader(), new Class[] { callbackInteface }, me);
        return proxy;
    }

    // ----------------------------------------------------------------------------------------------------
    private SWTAsyncCallbackProxy(Object callbackImpl) {
        m_callbackImpl = callbackImpl;
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

        // Solo se permite llamar a metodos sin retorno
        if(!void.class.equals(method.getReturnType()) && !Void.class.equals(method.getReturnType())) {
            String msg = "SWTAsyncCallbackProxy - Error calling method without 'void' result: " + method;
            Tracer._error(msg);
            throw new InvocationTargetException(null,msg);
        }
        
        // Pospone la ejecucion al hilo grafico
        Display.getDefault().asyncExec(new Runnable() {

            @SuppressWarnings("synthetic-access")
            public void run() {
                try {
                    method.invoke(m_callbackImpl, args);
                } catch (Throwable th) {
                    Tracer._error("SWTAsyncCallbackProxy - Error calling method: " + method);
                }
            }
        });
        
        // Siempre retorna null
        return null;

    }

}
