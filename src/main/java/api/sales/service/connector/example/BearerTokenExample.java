package api.sales.service.connector.example;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import api.sales.service.connector.BayeuxParameters;
import api.sales.service.connector.SalesforceConnector;
import api.sales.service.connector.TopicSubscription;
import org.cometd.bayeux.Channel;
import org.eclipse.jetty.util.ajax.JSON;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BearerTokenExample {
    public static void main(String[] argv) throws Exception {
        if (argv.length < 2 || argv.length > 4) {
            log.error("Usage: BearerTokenExample url token topic [replayFrom]");
        }
        long replayFrom = SalesforceConnector.REPLAY_FROM_TIP;
        if (argv.length == 4) {
            replayFrom = Long.parseLong(argv[3]);
        }

        BayeuxParameters params = new BayeuxParameters() {

            @Override
            public String bearerToken() {
                return argv[1];
            }

            @Override
            public URL host() {
                try {
                    return new URL(argv[0]);
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException(String.format("Unable to create url: %s", argv[0]), e);
                }
            }
        };

        Consumer<Map<String, Object>> consumer = event -> log.info(String.format("Received:\n%s", JSON.toString(event)));
        SalesforceConnector connector = new SalesforceConnector(params);

        connector.addListener(Channel.META_CONNECT, new LoggingListener(true, true))
        .addListener(Channel.META_DISCONNECT, new LoggingListener(true, true))
        .addListener(Channel.META_HANDSHAKE, new LoggingListener(true, true));

        connector.start().get(5, TimeUnit.SECONDS);

        TopicSubscription subscription = connector.subscribe(argv[2], replayFrom, consumer).get(5, TimeUnit.SECONDS);

        log.info(String.format("Subscribed: %s", subscription));
    }
}
