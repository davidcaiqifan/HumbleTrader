package logic.schedulers;

import logic.EventManager;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ScheduleJob implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        EventManager eventManager = (EventManager)dataMap.get("eventManager");
        String referenceTag = (String)dataMap.get("reference");
        eventManager.publishScheduleEvent(new ScheduleEvent(referenceTag));
    }
}
