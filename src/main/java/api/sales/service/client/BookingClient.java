package api.sales.service.client;

import api.sales.service.mapping.output.BookingRequest;
import feign.HeaderMap;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="SalesServiceClient", url="https://eoz04p8uq3.execute-api.eu-central-1.amazonaws.com")
public interface BookingClient {

    @RequestMapping(method = RequestMethod.POST, path = "/dev", consumes = MediaType.APPLICATION_JSON_VALUE)
    String createBooking(@RequestHeader("Content-Type") String strContentType,@RequestHeader("X-SEM-APP") String app,@RequestHeader("X-SEM-ENV") String env, @RequestHeader("X-SEM-TOKEN") String token,@RequestBody BookingRequest bookingRequest);

}
