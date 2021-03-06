package arch.batch;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Rafael Guterres
 */
public interface JobUnit extends Serializable {

    String getJobName();

    long getInitialDelay();

    long getPeriod();

    TimeUnit getTimeUnit();

    Runnable getRunnableTask();
}
