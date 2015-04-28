package rs.pelotas.arch.concurrency;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import rs.pelotas.arch.batch.Job;

/**
 *
 * @author Rafael Guterres
 */
public class Scheduler {

    @Resource
    ManagedScheduledExecutorService managedScheduledExecutorService;

    List<Job> jobs = new ArrayList<>();

    @PostConstruct
    public void init() {
        //TODO: implementar annotation para agendamento automatico
        //jobs.add(new PositionJob());
    }

    public void scheduleJobs() {
        for (Job job : jobs) {
            managedScheduledExecutorService.scheduleAtFixedRate(
                    job.getRunnableTask(),
                    job.getInitialDelay(),
                    job.getPeriod(),
                    job.getTimeUnit());
        }
    }
}