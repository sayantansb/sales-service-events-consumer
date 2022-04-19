package api.sales.service.event.consumption;

import api.sales.service.event.HandledPlatformEvent;
import api.sales.service.service.BookingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SalesforcePlatformEventHandler {

    @Autowired
    private BookingService bookingService;

    public synchronized void handleRequest(String payload, String salesforceEventName, Long replayId) {
        log.info("Received payload from platform event " + salesforceEventName + " with replayId: " + replayId);
        log.info(payload);
        if(HandledPlatformEvent.OPPORTUNITY_STATUS_EVENT.getEventResource().equalsIgnoreCase(salesforceEventName)){
            //opportunityStatusMessageSender.sendMessageFromEventBus(payload);
            try {
                bookingService.createBookingFromPlatformEvent(payload);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }else {
            log.error("Could handle request (it will be ignored): " + salesforceEventName + " with payload: " + payload);
        }
    }
}
