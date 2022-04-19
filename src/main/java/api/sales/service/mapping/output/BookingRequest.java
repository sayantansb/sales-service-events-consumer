
package api.sales.service.mapping.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class BookingRequest {

    @JsonProperty("request")
    private Request request;


}
