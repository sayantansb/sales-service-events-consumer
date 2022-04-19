package api.sales.service.service;

import api.sales.service.client.BookingClient;
import api.sales.service.mapping.output.BookingRequest;
import api.sales.service.mapping.output.BookingResponse;
import api.sales.service.mapping.util.MapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookingService {

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private BookingClient bookingClient;

    public BookingResponse createBookingFromPlatformEvent(String strOpportunityStatusEventSource) throws JsonProcessingException {
        try {
            log.info("In createBookingFromPlatformEvent, strOpportunityStatusEventSource = {}",strOpportunityStatusEventSource);
            BookingRequest bookingRequest = mapperUtil.convertOpportunityStatusEventTarget(strOpportunityStatusEventSource);
            log.info("In createBookingFromPlatformEvent, bookingRequest = {}",bookingRequest);
            String strBookingResponse = bookingClient.createBooking(MediaType.APPLICATION_JSON_VALUE,"sales","dev","SGVsbG8gV29ybGQK",bookingRequest);
            log.info("In createBookingFromPlatformEvent, strBookingResponse = {}",strBookingResponse);
            BookingResponse bookingResponse = (new ObjectMapper()).readValue(strBookingResponse,BookingResponse.class);
            return bookingResponse;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
