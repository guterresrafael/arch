package rs.pelotas.arch.batch;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Rafael Guterres
 */
public interface JobScheduler extends Serializable {
    
    List<JobUnit> getJobs();
    
    void addJob(JobUnit jobUnit);
    
    void scheduleJobs();
}
