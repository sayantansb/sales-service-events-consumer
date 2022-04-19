package api.sales.service.connector.example;



import static api.sales.service.connector.LoginHelper.login;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import api.sales.service.connector.BayeuxParameters;
import api.sales.service.connector.SalesforceConnector;
import api.sales.service.connector.TopicSubscription;
import org.eclipse.jetty.util.ajax.JSON;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LoginExample {
    public static void main(String[] argv) throws Exception {
        if (argv.length < 3 || argv.length > 4) {
            log.error("Usage: LoginExample username password topic [replayFrom]");
        }
        long replayFrom = SalesforceConnector.REPLAY_FROM_TIP;
        if (argv.length == 4) {
            replayFrom = Long.parseLong(argv[3]);
        }

        BearerTokenProvider tokenProvider = new BearerTokenProvider(() -> {
            try {
                return login(argv[0], argv[1]);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        });

        BayeuxParameters params = tokenProvider.login();

        Consumer<Map<String, Object>> consumer = event -> log.info(String.format("Received:\n%s", JSON.toString(event)));

        SalesforceConnector connector = new SalesforceConnector(params);

        connector.setBearerTokenProvider(tokenProvider);

        connector.start().get(5, TimeUnit.SECONDS);

        TopicSubscription subscription = connector.subscribe(argv[2], replayFrom, consumer).get(5, TimeUnit.SECONDS);

        log.info(String.format("Subscribed: %s", subscription));
    }
}
