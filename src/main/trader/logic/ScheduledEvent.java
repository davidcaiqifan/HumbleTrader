package logic;

import org.quartz.Job;

public class ScheduledEvent {
    private Job job;

    public ScheduledEvent(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return this.job;
    }


}
