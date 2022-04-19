
package api.sales.service.mapping.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SmRevenueShare {

    @JsonProperty("salesManagerId")
    private Integer salesManagerId;
    @JsonProperty("profitCenter")
    private String profitCenter;
    @JsonProperty("totalShareInPercent")
    private Double totalShareInPercent;
    @JsonProperty("totalShareValue")
    private Double totalShareValue;


}
