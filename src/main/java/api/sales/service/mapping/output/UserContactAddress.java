
package api.sales.service.mapping.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserContactAddress {

    @JsonProperty("firstname")
    private String firstname;
    @JsonProperty("lastname")
    private String lastname;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("companyName")
    private String companyName;
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    @JsonProperty("emailAddress")
    private String emailAddress;
    @JsonProperty("country")
    private String country;
    @JsonProperty("federalState")
    private String federalState;
    @JsonProperty("city")
    private String city;
    @JsonProperty("zipCode")
    private String zipCode;
    @JsonProperty("street")
    private String street;
    @JsonProperty("houseNo")
    private String houseNo;
    @JsonProperty("addOn")
    private String addOn;

}
