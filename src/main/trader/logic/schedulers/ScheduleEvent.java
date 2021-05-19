package logic.schedulers;

public class ScheduleEvent {
    private String referenceTag;
    public ScheduleEvent(String referenceTag) {
        this.referenceTag = referenceTag;
    }
    public String getReferenceTag() {
        return this.referenceTag;
    }
}
