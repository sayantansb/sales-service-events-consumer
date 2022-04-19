
package api.sales.service.mapping.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Request {

    @JsonProperty("requesterId")
    private Integer requesterId;
    @JsonProperty("userId")
    private Integer userId;
    @JsonProperty("userContactAddress")
    private UserContactAddress userContactAddress;
    @JsonProperty("userBillingAddress")
    private Object userBillingAddress;
    @JsonProperty("clearingHouse")
    private String clearingHouse;
    @JsonProperty("liableOffice")
    private String liableOffice;
    @JsonProperty("salesTeam")
    private String salesTeam;
    @JsonProperty("salesManagerId")
    private Integer salesManagerId;
    @JsonProperty("accountBookingType")
    private String accountBookingType;
    @JsonProperty("billingTerms")
    private String billingTerms;
    @JsonProperty("otherBillingTerms")
    private Object otherBillingTerms;
    @JsonProperty("salesChannels")
    private List<String> salesChannels = null;
    @JsonProperty("areSeatsLimited")
    private Boolean areSeatsLimited;
    @JsonProperty("seatsLimit")
    private Integer seatsLimit;
    @JsonProperty("accountAccessTypes")
    private List<String> accountAccessTypes = null;
    @JsonProperty("agreementForm")
    private String agreementForm;
    @JsonProperty("products")
    private List<Product> products = null;
    @JsonProperty("subscriptionStartDate")
    private Object subscriptionStartDate;
    @JsonProperty("subscriptionFullMonths")
    private Integer subscriptionFullMonths;
    @JsonProperty("subscriptionEndDate")
    private Object subscriptionEndDate;
    @JsonProperty("pricePerMonth")
    private Object pricePerMonth;
    @JsonProperty("discountAmount")
    private Object discountAmount;
    @JsonProperty("newBusinessAmountPart")
    private Object newBusinessAmountPart;
    @JsonProperty("renewalAmountPart")
    private Object renewalAmountPart;
    @JsonProperty("invoiceAmount")
    private Double invoiceAmount;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("businessLanguage")
    private String businessLanguage;
    @JsonProperty("isPurchaseOrderNumberRequired")
    private Boolean isPurchaseOrderNumberRequired;
    @JsonProperty("purchaseOrderNumber")
    private Object purchaseOrderNumber;
    @JsonProperty("vatId")
    private Object vatId;
    @JsonProperty("hasAutoCancellation")
    private Object hasAutoCancellation;
    @JsonProperty("contractDocumentUrl")
    private Object contractDocumentUrl;
    @JsonProperty("contractDocumentFileName")
    private Object contractDocumentFileName;
    @JsonProperty("contractDocument")
    private Object contractDocument;
    @JsonProperty("offerDocumentUrl")
    private Object offerDocumentUrl;
    @JsonProperty("offerDocumentFileName")
    private Object offerDocumentFileName;
    @JsonProperty("offerDocument")
    private Object offerDocument;
    @JsonProperty("isBundleBooking")
    private Object isBundleBooking;
    @JsonProperty("isInvoiceSplit")
    private Object isInvoiceSplit;
    @JsonProperty("dealType")
    private String dealType;
    @JsonProperty("salesforceOpportunityId")
    private String salesforceOpportunityId;
    @JsonProperty("smRevenueShares")
    private List<SmRevenueShare> smRevenueShares = null;
    @JsonProperty("flsRevenueShares")
    private List<Object> flsRevenueShares = null;
    @JsonProperty("additionalInfo")
    private Object additionalInfo;
    @JsonProperty("version")
    private Object version;

}
