package api.sales.service.connector.example;

import static org.cometd.bayeux.Channel.META_CONNECT;
import static org.cometd.bayeux.Channel.META_DISCONNECT;
import static org.cometd.bayeux.Channel.META_HANDSHAKE;
import static org.cometd.bayeux.Channel.META_SUBSCRIBE;
import static org.cometd.bayeux.Channel.META_UNSUBSCRIBE;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import api.sales.service.connector.BayeuxParameters;
import api.sales.service.connector.SalesforceConnector;
import api.sales.service.connector.TopicSubscription;
import org.eclipse.jetty.util.ajax.JSON;

import lombok.extern.slf4j.Slf4j;

import api.sales.service.connector.LoginHelper;

@Slf4j
public class DevLoginExample {

    public static void main(String[] argv) throws Throwable {
        if (argv.length < 4 || argv.length > 5) {
            log.error("Usage: DevLoginExample url username password topic [replayFrom]");
        }
        Consumer<Map<String, Object>> consumer = event -> log.info(String.format("Received:\n%s", JSON.toString(event)));

        BearerTokenProvider tokenProvider = new BearerTokenProvider(() -> {
            try {
                return LoginHelper.login(new URL(argv[0]), argv[1], argv[2]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        BayeuxParameters params = tokenProvider.login();

        SalesforceConnector connector = new SalesforceConnector(params);
        LoggingListener loggingListener = new LoggingListener(true, true);

        connector.addListener(META_HANDSHAKE, loggingListener)
                .addListener(META_CONNECT, loggingListener)
                .addListener(META_DISCONNECT, loggingListener)
                .addListener(META_SUBSCRIBE, loggingListener)
                .addListener(META_UNSUBSCRIBE, loggingListener);

        connector.setBearerTokenProvider(tokenProvider);

        connector.start().get(5, TimeUnit.SECONDS);

        long replayFrom = SalesforceConnector.REPLAY_FROM_TIP;
        if (argv.length == 5) {
            replayFrom = Long.parseLong(argv[4]);
        }
        TopicSubscription subscription;
        try {
            subscription = connector.subscribe(argv[3], replayFrom, consumer).get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            log.error(e.getCause().toString());
            throw e.getCause();
        } catch (TimeoutException e) {
            log.error("Timed out subscribing");
            throw e.getCause();
        }

        log.info(String.format("Subscribed: %s", subscription));
    }
}
