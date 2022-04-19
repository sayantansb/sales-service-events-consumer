package api.sales.service.event.consumption;

import java.util.Map;

import api.sales.service.connector.BayeuxParameters;
import api.sales.service.connector.DelegatingBayeuxParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SalesforceAccessTokenProvider {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Gson gson;


    public BayeuxParameters updateBayeuxParametersWithSalesforceAccessToken(String sfdcHost, String username,
                                                                            String password, String consumerKey, String secret, BayeuxParameters parameters) {
        return new DelegatingBayeuxParameters(parameters) {
            @Override
            public String bearerToken() {
                String token = "";
                try {
                    String url = sfdcHost + "/services/oauth2/token";
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Type", "application/x-www-form-urlencoded");
                    String tokenRequest = "grant_type=password&client_id=" + consumerKey + "&client_secret=" + secret
                            + "&username=" + username + "&password=" + password;
                    HttpEntity<String> request = new HttpEntity<>(tokenRequest, headers);
                    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                    String responseText = response.getBody();
                    @SuppressWarnings("unchecked")
                    Map<String, String> jwtMap = gson.fromJson(responseText, Map.class);
                    token = jwtMap.get("access_token");
                    log.info("received salesforce access token by salesforce username password");
                } catch (Exception e) {
                    log.error("Failed to get salesforce access token", e);
                }
                return token;
            }
        };
    }

}
