package rs.pelotas.arch.batch;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Rafael Guterres
 */
public interface Job extends Serializable {
    
    String getJobName();
    
    long getInitialDelay();
    
    long getPeriod();
    
    TimeUnit getTimeUnit();
    
    Runnable getRunnableTask();
}