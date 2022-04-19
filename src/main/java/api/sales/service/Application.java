package api.sales.service;

import api.sales.service.connector.SalesforceConnector;
import api.sales.service.connector.TopicSubscription;
import api.sales.service.event.HandledPlatformEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;


@SpringBootApplication
@Slf4j
@EnableFeignClients
public class Application
{
    @Value("${salesforce.sourceEvent.list}")
    private HandledPlatformEvent[] salesforceEventList;

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private ApplicationContext context;

    public static void main( String[] args )
    {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void startPlatformEventSubscriptions() {

        SalesforceConnector emp = context.getBean(SalesforceConnector.class);

        // start platform event subscription
        log.info("starting salesforce platform event subscription");
        for (int i = 0; i < salesforceEventList.length; i++) {
            HandledPlatformEvent salesforceEvent = salesforceEventList[i];
            beanFactory.getBean(TopicSubscription.class, emp, salesforceEvent.getEventResource());
            log.info("created new salesforce platform event subscription " + salesforceEvent);
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
