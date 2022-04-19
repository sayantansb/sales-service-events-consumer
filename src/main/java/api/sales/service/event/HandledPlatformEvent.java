package api.sales.service.event;

public enum HandledPlatformEvent {

    OPPORTUNITY_STATUS_EVENT("/event/OpportunityStatusEvent__e");

    private String eventResource;

    HandledPlatformEvent(String eventResource) {
        this.eventResource = eventResource;
    }

    public String getEventResource() {
        return eventResource;
    }

    @Override
    public String toString() {
        return name() + "(" + eventResource + ")";
    }
}
