package rs.pelotas.arch.batch;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;

/**
 *
 * @author Rafael Guterres
 */
public class JobSchedulerImpl implements JobScheduler {

    private static final long serialVersionUID = 5567769350807827834L;

    @Resource
    ManagedScheduledExecutorService managedScheduledExecutorService;

    List<JobUnit> jobs = new ArrayList<>();

    @Override
    public List<JobUnit> getJobs() {
        return jobs;
    }

    @Override
    public void addJob(JobUnit jobUnit) {
        jobs.add(jobUnit);
    }

    @Override
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