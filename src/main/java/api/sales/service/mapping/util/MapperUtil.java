package api.sales.service.mapping.util;

import api.sales.service.mapping.output.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import api.sales.service.mapping.input.OpportunityStatusEventSource;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

@Component
@Slf4j
public class MapperUtil {

    public BookingRequest convertOpportunityStatusEventTarget(String strOpportunityStatusEventSource)  {
        try{
            OpportunityStatusEventSource opportunityStatusEventSource = (new ObjectMapper()).readValue(strOpportunityStatusEventSource, OpportunityStatusEventSource.class);
            BookingRequest bookingRequest = new BookingRequest();
            Request request = new Request();
            request.setAccountBookingType(opportunityStatusEventSource.getAccountBookingType());
            request.setAgreementForm(opportunityStatusEventSource.getAgreementForm());
            request.setAccountAccessTypes(getAccountAccessTypes(opportunityStatusEventSource.getAccountAccessTypes()));
            request.setBillingTerms(opportunityStatusEventSource.getBillingTerms());
            request.setClearingHouse(opportunityStatusEventSource.getClearingHouse());
            request.setUserContactAddress(getUserContactAddress(opportunityStatusEventSource));
            if(StringUtils.isNotBlank(opportunityStatusEventSource.getRequestorId())){
                request.setRequesterId(Integer.valueOf(opportunityStatusEventSource.getRequestorId()));
            }
            if(StringUtils.isNotBlank(opportunityStatusEventSource.getUserId())){
                request.setUserId(Integer.valueOf(opportunityStatusEventSource.getUserId().length()));
            }
            request.setClearingHouse(opportunityStatusEventSource.getClearingHouse());
            request.setLiableOffice(opportunityStatusEventSource.getLiableOffice());
            request.setSalesTeam(opportunityStatusEventSource.getSalesTeam());
            if(StringUtils.isNotBlank(opportunityStatusEventSource.getSalesManagerId())){
                request.setSalesManagerId(Integer.valueOf(opportunityStatusEventSource.getSalesManagerId().length()));
            }
            request.setAccountBookingType(opportunityStatusEventSource.getAccountBookingType());
            request.setOtherBillingTerms(opportunityStatusEventSource.getBillingTerms());
            request.setSalesChannels(getSalesChannels(opportunityStatusEventSource.getSalesChannels()));
            if(StringUtils.isNotBlank(opportunityStatusEventSource.getDealType())){
                request.setDealType(opportunityStatusEventSource.getDealType());
            }
            request.setProducts(getProducts(opportunityStatusEventSource.getProducts()));
            request.setSalesforceOpportunityId(opportunityStatusEventSource.getOpportunityId());
            request.setSmRevenueShares(getSmRevenueShares(opportunityStatusEventSource.getSmRevenueShares()));

            bookingRequest.setRequest(request);
            log.info("In convertOpportunityStatusEventTarget, bookingRequest : {}",bookingRequest);
            return bookingRequest;
        }catch (Exception ex){
            ex.printStackTrace();
        }
       return null;

    }

    private List<String> getAccountAccessTypes(String strAccountAccessTypes){
        log.info("In getAccountAccessTypes, strAccountAccessTypes : {}",strAccountAccessTypes);
        return Arrays.asList(strAccountAccessTypes);
    }

    private UserContactAddress getUserContactAddress(OpportunityStatusEventSource opportunityStatusEventSource){
        UserContactAddress userContactAddress = new UserContactAddress();
        userContactAddress.setEmailAddress(opportunityStatusEventSource.getUserEmailAddress());
        userContactAddress.setFirstname(opportunityStatusEventSource.getUserContactFirstName());
        userContactAddress.setLastname(opportunityStatusEventSource.getUserContactLastName());
        userContactAddress.setGender(opportunityStatusEventSource.getUserGender());
        userContactAddress.setCompanyName(opportunityStatusEventSource.getCompanyName());
        userContactAddress.setPhoneNumber(opportunityStatusEventSource.getUserPhoneNumber());
        userContactAddress.setCountry(opportunityStatusEventSource.getUserCountry());
        userContactAddress.setCity(opportunityStatusEventSource.getUserCity());
        userContactAddress.setZipCode(opportunityStatusEventSource.getUserZipCode());
        userContactAddress.setHouseNo(opportunityStatusEventSource.getUserHouseNumber());
        return userContactAddress;
    }

    private List<String> getSalesChannels(String strSalesChannels){
        log.info("In getSalesChannels, strSalesChannels : {}",strSalesChannels);
        return Arrays.asList(strSalesChannels);
    }

    private List<Product> getProducts(String strProducts){
        log.info("In getProducts, strProducts : {}",strProducts);
        List<Product> products = new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(strProducts,"||");
        while(stringTokenizer.hasMoreTokens()){
            String strThisProduct = stringTokenizer.nextToken();
            if(StringUtils.isNotBlank(strThisProduct)){
                Product prod = new Product();
                StringTokenizer st = new StringTokenizer(strThisProduct,":");
                while (st.hasMoreTokens()){
                    String keyValPair = st.nextToken();
                    if(StringUtils.isNotBlank(keyValPair) && keyValPair.indexOf("---")>-1){
                        int idx = keyValPair.indexOf("---");
                        String key = keyValPair.substring(0,idx);
                        String val = keyValPair.substring(idx+3);
                        switch (key){
                            case "Type__c" :
                                prod.setEnumValue(val);
                                break;
                            case "Price__c" :
                                prod.setPrice((Double.valueOf(val)).intValue());
                                break;
                            case "PricePerMonth__c" :
                                prod.setPricePerMonth((Double.valueOf(val)).intValue());
                                break;
                            case "description__c" :
                                prod.setDescription(val);
                                break;
                        }
                    }
                }
                products.add(prod);
            }

        }
        log.info("In getProducts, products : {}",products);
        return products;
    }

    private List<SmRevenueShare> getSmRevenueShares(String strSmRevenueShares){
        log.info("In getSmRevenueShares, strSmRevenueShares : {}",strSmRevenueShares);
        List<SmRevenueShare> smRevenueShares = new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(strSmRevenueShares,"||");
        while(stringTokenizer.hasMoreTokens()){
            String strThisRevenueShare = stringTokenizer.nextToken();
            if(StringUtils.isNotBlank(strThisRevenueShare)){
                SmRevenueShare revenueShare = new SmRevenueShare();
                StringTokenizer st = new StringTokenizer(strThisRevenueShare,":");
                while (st.hasMoreTokens()){
                    String keyValPair = st.nextToken();
                    if(StringUtils.isNotBlank(keyValPair) && keyValPair.indexOf("---")>-1){
                        int idx = keyValPair.indexOf("---");
                        String key = keyValPair.substring(0,idx);
                        String val = keyValPair.substring(idx+3);
                        switch (key){
                            case "SalesManagerId__c" :
                                revenueShare.setSalesManagerId(Integer.valueOf(val));
                                break;
                            case "ProfitCenter__c" :
                                revenueShare.setProfitCenter(val);
                                break;
                            case "TotalShareInPercent__c" :
                                revenueShare.setTotalShareInPercent(Double.valueOf(val));
                                break;
                            case "TotalShareValue__c" :
                                revenueShare.setTotalShareValue(Double.valueOf(val));
                                break;
                        }
                    }
                }
                smRevenueShares.add(revenueShare);
            }
        }
        log.info("In convertOpportunityStatusEventTarget, strSmRevenueShares : {}",smRevenueShares);
        return smRevenueShares;
    }



}
