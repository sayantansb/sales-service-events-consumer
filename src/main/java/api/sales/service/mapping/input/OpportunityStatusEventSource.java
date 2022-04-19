package api.sales.service.mapping.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpportunityStatusEventSource {

    @JsonProperty("AccountAccessTypes__c")
    private String accountAccessTypes;

    @JsonProperty("AccountBookingType__c")
    private String accountBookingType;

    @JsonProperty("AgreementForm__c")
    private String agreementForm;

    @JsonProperty("BillingTerms__c")
    private String billingTerms;

    @JsonProperty("ClearingHouse__c")
    private String clearingHouse;

    @JsonProperty("CompanyName__c")
    private String companyName;

    @JsonProperty("DealType__c")
    private String dealType;

    @JsonProperty("LiableOffice__c")
    private String liableOffice;

    @JsonProperty("OpportunityId__c")
    private String opportunityId;

    @JsonProperty("Products__c")
    private String products;

    @JsonProperty("RequestorId__c")
    private String requestorId;

    @JsonProperty("SalesChannels__c")
    private String salesChannels;

    @JsonProperty("SalesManagerId__c")
    private String salesManagerId;

    @JsonProperty("SalesTeam__c")
    private String salesTeam;

    @JsonProperty("SMRevenueShares__c")
    private String smRevenueShares;

    @JsonProperty("UserCity__c")
    private String userCity;

    @JsonProperty("UserContactFirstName__c")
    private String userContactFirstName;

    @JsonProperty("UserContactLastName__c")
    private String userContactLastName;

    @JsonProperty("UserCountry__c")
    private String userCountry;

    @JsonProperty("UserEmailAddress__c")
    private String userEmailAddress;

    @JsonProperty("UserGender__c")
    private String userGender;

    @JsonProperty("UserHouseNumber__c")
    private String userHouseNumber;

    @JsonProperty("UserId__c")
    private String userId;

    @JsonProperty("UserPhoneNumber__c")
    private String userPhoneNumber;

    @JsonProperty("UserStreet__c")
    private String userStreet;

    @JsonProperty("UserZipCode__c")
    private String userZipCode;


}
