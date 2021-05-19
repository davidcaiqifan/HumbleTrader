package logic;

import logic.schedulers.ScheduleJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class ScheduleManager {
    private Scheduler sched;
    private EventManager eventManager;

    public ScheduleManager(EventManager eventManager) throws SchedulerException {
        SchedulerFactory sf = new StdSchedulerFactory();
        this.sched = sf.getScheduler();
        this.eventManager = eventManager;
        sched.start();
    }

    public void periodicCallback(int interval, String reference) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(ScheduleJob.class)
                //.withIdentity("job1", "group1") // name "priceJob", group "group1"
                .usingJobData("reference", reference)
                .build();
        jobDetail.getJobDataMap().put("eventManager", this.eventManager);
        Trigger trigger = newTrigger()
                //.withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInMilliseconds(interval)
                        .repeatForever())
                .build();
        sched.scheduleJob(jobDetail, trigger);
    }

    public Scheduler getSched() {
        return sched;
    }
}
