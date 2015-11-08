package arch.batch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;

/**
 *
 * @author Rafael Guterres
 */
public class JobScheduler implements Serializable {

    private static final long serialVersionUID = 5567769350807827834L;

    @Inject
    private ManagedScheduledExecutorService managedScheduledExecutorService;

    private List<JobUnit> jobs = new ArrayList<>();

    public List<JobUnit> getJobs() {
        return jobs;
    }

    public void addJob(JobUnit jobUnit) {
        jobs.add(jobUnit);
    }

    public void scheduleJobs() {
        for (JobUnit jobUnit : jobs) {
            managedScheduledExecutorService.scheduleAtFixedRate(
                    jobUnit.getRunnableTask(),
                    jobUnit.getInitialDelay(),
                    jobUnit.getPeriod(),
                    jobUnit.getTimeUnit());
        }
    }
}
