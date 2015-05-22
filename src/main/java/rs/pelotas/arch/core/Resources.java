package rs.pelotas.arch.core;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 
 * @author Rafael Guterres
 */
public class Resources implements Serializable {

    private static final long serialVersionUID = -5306963189660774964L;

    @PersistenceContext
    EntityManager entityManager;

    @Resource
    ManagedScheduledExecutorService managedScheduledExecutorService;

    @Produces
    public Logger getLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }
    
    @Produces
    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    @Produces
    public ManagedScheduledExecutorService getManagedScheduledExecutorService() {
        return managedScheduledExecutorService;
    }
}