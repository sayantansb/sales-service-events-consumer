package api.sales.service.event.consumption;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import api.sales.service.connector.BayeuxParameters;
import api.sales.service.connector.SalesforceConnector;
import api.sales.service.connector.TopicSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import com.google.gson.Gson;

import api.sales.service.connector.example.BearerTokenProvider;

@Configuration
public class SalesforcePlatformeventConfig {

    private static Logger logger = LoggerFactory.getLogger(SalesforcePlatformeventConfig.class);

    @Value("${salesforce.host}")
    private String salesforceHost;

    @Value("${salesforce.version:46.0}")
    private String salesforceVersion;

    @Value("${salesforce.consumerKey:}")
    private String salesforceConsumerKey;

    @Value("${salesforce.secret:}")
    private String salesforceSecret;

    @Value("${salesforce.username:}")
    private String salesforceUsername;

    @Value("${salesforce.password:}")
    private String salesforcePassword;

    @Autowired
    protected SalesforcePlatformEventHandler eventHandler;

    @Autowired
    SalesforceAccessTokenProvider salesforceAccessTokenProvider;

    @Autowired
    private Gson gson;

    @Bean
    public BayeuxParameters bayeuxParameters() throws Exception {
        BayeuxParameters bayeuxParams = initBayeuxParam(salesforceVersion, salesforceHost);
        Supplier<BayeuxParameters> sessionSupplier = new Supplier<BayeuxParameters>() {
            @Override
            public BayeuxParameters get() {
                return salesforceAccessTokenProvider.updateBayeuxParametersWithSalesforceAccessToken(salesforceHost,
                        salesforceUsername, salesforcePassword, salesforceConsumerKey, salesforceSecret, bayeuxParams);
            }
        };
        BearerTokenProvider tokenProvider = new BearerTokenProvider(sessionSupplier);
        logger.debug("initiate BayeuxParameters for EMP Connector");
        return tokenProvider.login();
    }

    @Bean(destroyMethod = "stop")
    public SalesforceConnector salesforceConnector(BayeuxParameters bayeuxParameters) {
        logger.debug("BayeuxParameters url {} version {}", bayeuxParameters.host(), bayeuxParameters.version());
        SalesforceConnector salesforceConnector = new SalesforceConnector(bayeuxParameters);
        logger.debug("SalesforceConnector isConnected {} isHandshook {}", salesforceConnector.isConnected(),
                salesforceConnector.isHandshook());
        return salesforceConnector;
    }

    private static BayeuxParameters initBayeuxParam(String apiVersion, String host) {
        BayeuxParameters params = new BayeuxParameters() {
            @Override
            public String version() {
                return apiVersion;
            }

            @Override
            public String bearerToken() {
                return null;
            }

            @Override
            public URL host() {
                try {
                    return new URL(host);
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException(String.format("Unable to create url: %s", host), e);
                }
            }
        };
        return params;
    }

    @Bean(destroyMethod = "cancel")
    @Lazy
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public TopicSubscription startPlatformEventSubscription(SalesforceConnector salesforceConnector, String salesforceEventName) {
        TopicSubscription subscription = null;
        try {
            // default replay id -1, means new message
            long replayFrom = -1;
            logger.info("Setup event consumer with replyFrom {}", replayFrom);

            Consumer<Map<String, Object>> consumer = event -> eventHandler.handleRequest(gson.toJson(event.get("payload")), salesforceEventName,
                    ((Map<String, Long>) event.get("event")).get("replayId"));
            salesforceConnector.start().get(5, TimeUnit.SECONDS);
            logger.debug("Subscribing to event {}", salesforceEventName);
            subscription = salesforceConnector.subscribe(salesforceEventName, replayFrom, consumer).get(5, TimeUnit.SECONDS);
        } catch (java.util.concurrent.ExecutionException sube) {
            logger.error("ExecutionException Error from platform event " + salesforceEventName, sube);
        } catch (Exception e) {
            logger.error("Error when starting TopicSubscription " + salesforceEventName, e);
        }
        return subscription;
    }

}
