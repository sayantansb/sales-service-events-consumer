
package api.sales.service.mapping.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Product {

    @JsonProperty("enumValue")
    private String enumValue;
    @JsonProperty("price")
    private Integer price;
    @JsonProperty("pricePerMonth")
    private Integer pricePerMonth;
    @JsonProperty("description")
    private String description;


}
