package arch.core;

import java.util.logging.Logger;
import javax.ws.rs.core.Application;
import arch.batch.JobScheduler;

/**
 * Classe responsavel pela inicializacao da Aplicacao
 * Inicializa o servlet REST e substitui a declaração no web.xml
 *
 * @author Rafael Guterres
 */
public abstract class BaseApplication extends Application {

    public abstract JobScheduler getScheduler();

    public abstract Logger getLogger();

    protected abstract void init();
}
