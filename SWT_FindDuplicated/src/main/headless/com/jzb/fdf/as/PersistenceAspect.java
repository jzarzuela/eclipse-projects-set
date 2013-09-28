/**
 * 
 */
package com.jzb.fdf.as;

import java.util.Stack;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.jzb.fdf.srvc.impl.BaseSrvc;
import com.jzb.fdf.srvc.impl.NeedsEntityManager;
import com.jzb.fdf.srvc.impl.NeedsEntityManager.EM_Type;

/**
 * @author jzarzuela
 * 
 */
@Aspect
public class PersistenceAspect {

    private static final String                            PERSISTENCE_UNIT_NAME = "JPA Find Duplicates";
    private static EntityManagerFactory                    s_factory;
    private static final ThreadLocal<Stack<EntityManager>> s_thLclEM             = new ThreadLocal();

    // ----------------------------------------------------------------------------------------------------
    public static synchronized void done() {
        s_factory.close();
    }

    // ----------------------------------------------------------------------------------------------------
    public static synchronized void init(boolean deleteDB) {

        // delete the database named 'ddbb/findDuplicates' in the user home directory
        if (deleteDB) {
            org.h2.tools.DeleteDbFiles.execute("~/ddbb", "findDuplicates", true);
        }

        // Crea la factoria
        s_factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

        // Adelanta la creacion del modelo
        EntityManager em = s_factory.createEntityManager();
        em.close();
    }

    // ----------------------------------------------------------------------------------------------------
    private static Stack<EntityManager> getEMStack() {

        Stack<EntityManager> stack = s_thLclEM.get();
        if (stack == null) {
            stack = new Stack();
            s_thLclEM.set(stack);
        }
        return stack;
    }

    // ----------------------------------------------------------------------------------------------------
    @Around("call(* com.jzb.fdf.srvc.impl.BaseSrvc.currrentEntMngr(..))")
    public EntityManager getCurrentEntityManager(JoinPoint jp) throws Throwable {

        // Tracer._debug("PersistenceAspect - Returning current EntityManager from the stack. Method: " + jp);
        Stack<EntityManager> stack = getEMStack();
        if (stack.isEmpty()) {
            throw new PersistenceException("Persistence Manager was not set for this method call");
        } else {
            return stack.peek();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    @Around("this(BaseSrvc+) && execution(* com.jzb.fdf.srvc.impl.*.*(..)) && @annotation(emType)")
    public Object setupEntityManagerAndTrx(ProceedingJoinPoint pjp, NeedsEntityManager emType) throws Throwable {

        // Tracer._debug("PersistenceAspect - Executing object method: " + pjp);

        Object result = null;
        boolean doCommit = false;

        boolean emWasCreatedHere = _prepareInstanceEM_Trx(emType, (BaseSrvc) pjp.getThis());
        try {
            // Ejecuta el codigo
            result = pjp.proceed();
            doCommit = true;

        } finally {
            // Cierra la transaccion en curso y libera el EntityManager
            _releaseInstanceEM_Trx(emWasCreatedHere, doCommit);
        }

        return result;

    }

    // ----------------------------------------------------------------------------------------------------
    private boolean _prepareInstanceEM_Trx(NeedsEntityManager needsEM, BaseSrvc objThis) {

        // Tracer._debug("PersistenceAspect - In method: _prepareInstanceEM");
        boolean emWasCreatedHere = false;
        Stack<EntityManager> stack = getEMStack();
        if (stack.isEmpty() || needsEM.value().equals(EM_Type.newOne)) {
            // Tracer._debug("++  PersistenceAspect - Created EntityManager");
            EntityManager em = s_factory.createEntityManager();
            stack.push(em);
            emWasCreatedHere = true;
        }
        
        if(needsEM.needsTrx()) {
            EntityManager em = stack.peek();
            EntityTransaction trx = em.getTransaction();
            if(!trx.isActive()) {
                trx.begin();
            }
        }

        return emWasCreatedHere;
    }

    // ----------------------------------------------------------------------------------------------------
    private void _releaseInstanceEM_Trx(boolean emWasCreatedHere, boolean doCommit) {

        // Tracer._debug("PersistenceAspect - In method: _releaseInstanceEM(" + emWasCreatedHere + ")");

        // Consigue el stack de EntityManager activos
        Stack<EntityManager> stack = getEMStack();
        if (stack.isEmpty()) {
            return;
        }

        try {
            // Consigue el EntityManager activo
            EntityManager em = stack.peek();

            // Si existe, cierra la transaccion en curso apropiadamente
            EntityTransaction trx = em.getTransaction();
            if (trx != null && trx.isActive()) {
                em.flush();
                if (doCommit) {
                    if (emWasCreatedHere) {
                        // Tracer._debug("PersistenceAspect - Transaction COMMIT");
                        trx.commit();
                    } else {
                        // Tracer._debug("PersistenceAspect - Transaction postponed COMMIT");
                    }
                } else {
                    // Tracer._debug("PersistenceAspect - Transaction ROLBACK");
                    trx.rollback();
                }
            }

            // Si hace falta cierra el EntityManager
            if (emWasCreatedHere) {
                // Tracer._debug("PersistenceAspect - Closing EntityManager");
                em.close();
            }

        } catch (Throwable th) {
            // Tracer._error("PersistenceAspect - Error releasing EntityManager");
        } finally {
            if (emWasCreatedHere) {
                // Tracer._debug("--  PersistenceAspect - Disposed EntityManager");
                stack.pop();
            }
        }

    }

}
