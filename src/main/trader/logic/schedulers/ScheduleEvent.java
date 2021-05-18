package logic.schedulers;

import logic.EventManager;
import logic.ScheduleManager;
import logic.dataProcessors.MarketDataManager;
import logic.listeners.OrderBookEventListener;
import logic.listeners.ScheduledPriceUpdate;
import org.apache.log4j.BasicConfigurator;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ScheduleEvent implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        EventManager eventManager = (EventManager)dataMap.get("eventManager");
//        String jobSays = context.getMergedJobDataMap().getString("jobSays");
//        System.out.println(jobSays);
        eventManager.publishOrderBookEvent((int)dataMap.get("interval"));
    }

    public static void main(String[] args) {
        //BasicConfigurator.configure();
        ScheduleManager sched;
        EventManager eventManager = new EventManager(new MarketDataManager("BTCUSDT"));
        try {
            sched = new ScheduleManager(eventManager);
            sched.periodicCallback(500, new ScheduledPriceUpdate());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
