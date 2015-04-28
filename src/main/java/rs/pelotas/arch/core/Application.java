package rs.pelotas.arch.core;

import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import rs.pelotas.arch.concurrency.Scheduler;

/**
 *
 * Classe responsavel pela inicializacao da Aplicacao
 * Inicializa o servlet REST e substitui a declaração no web.xml
 * 
 * @author Rafael Guterres
 */
@ApplicationPath("/api")
public class Application extends javax.ws.rs.core.Application {

    @Inject
    Scheduler scheduler;
    
    @Inject
    Logger logger;
    
    @PostConstruct
    public void init() {
        logger.info("Application initialization..");
        scheduler.scheduleJobs();
    }
}