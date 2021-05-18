package logic;

import logic.listeners.OrderBookEventListener;
import logic.listeners.ScheduledListener;
import logic.listeners.ScheduledPriceUpdate;
import logic.schedulers.ScheduleEvent;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    }

    public void periodicCallback(int interval, OrderBookEventListener orderBookEventListener) throws SchedulerException {
        this.eventManager.addOrderBookEventListener(orderBookEventListener, interval);
        JobDetail jobDetail = JobBuilder.newJob(ScheduleEvent.class)
                .withIdentity("job1", "group1") // name "priceJob", group "group1"
                .build();
        jobDetail.getJobDataMap().put("eventManager", this.eventManager);
        jobDetail.getJobDataMap().put("interval", interval);
        Trigger trigger = newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInMilliseconds(interval)
                        .repeatForever())
                .build();
        sched.scheduleJob(jobDetail, trigger);
        sched.start();
    }

    public Scheduler getSched() {
        return sched;
    }
}
