package api.sales.service.connector;

import java.net.ConnectException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import org.cometd.bayeux.Channel;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SalesforceConnector {
    private static final String ERROR = "error";
    private static final String FAILURE = "failure";

    private class SubscriptionImpl implements TopicSubscription {
        private final String topic;
        private final Consumer<Map<String, Object>> consumer;

        private SubscriptionImpl(String topic, Consumer<Map<String, Object>> consumer) {
            this.topic = topic;
            this.consumer = consumer;
            subscriptions.add(this);
        }

        /*
         * (non-Javadoc)
         * @see com.salesforce.emp.connector.Subscription#cancel()
         */
        @Override
        public void cancel() {
            replay.remove(topicWithoutQueryString(topic));
            if (running.get() && client != null) {
                client.getChannel(topic).unsubscribe();
                subscriptions.remove(this);
            }
        }


        @Override
        public long getReplayFrom() {
            return replay.getOrDefault(topicWithoutQueryString(topic), REPLAY_FROM_TIP);
        }


        @Override
        public String getTopic() {
            return topic;
        }

        @Override
        public String toString() {
            return String.format("Subscription [%s:%s]", getTopic(), getReplayFrom());
        }

        Future<TopicSubscription> subscribe() {
            long replayFrom = getReplayFrom();
            ClientSessionChannel channel = client.getChannel(topic);
            CompletableFuture<TopicSubscription> future = new CompletableFuture<>();
            channel.subscribe((c, message) -> consumer.accept(message.getDataAsMap()), (message) -> {
                if (message.isSuccessful()) {
                    future.complete(this);
                } else {
                    Object error = message.get(ERROR);
                    if (error == null) {
                        error = message.get(FAILURE);
                    }
                    future.completeExceptionally(
                            new CannotSubscribe(parameters.endpoint(), topic, replayFrom, error != null ? error : message));
                }
            });
            return future;
        }
    }

    public static long REPLAY_FROM_EARLIEST = -2L;
    public static long REPLAY_FROM_TIP = -1L;

    private static String AUTHORIZATION = "Authorization";
    private static final Logger log = LoggerFactory.getLogger(SalesforceConnector.class);

    private volatile BayeuxClient client;
    private final HttpClient httpClient;
    private final BayeuxParameters parameters;
    private final ConcurrentMap<String, Long> replay = new ConcurrentHashMap<>();
    private final AtomicBoolean running = new AtomicBoolean();

    private final Set<SubscriptionImpl> subscriptions = new CopyOnWriteArraySet<>();
    private final Set<MessageListenerInfo> listenerInfos = new CopyOnWriteArraySet<>();

    private Function<Boolean, String> bearerTokenProvider;
    private AtomicBoolean reauthenticate = new AtomicBoolean(false);

    public SalesforceConnector(BayeuxParameters parameters) {
        this.parameters = parameters;
        httpClient = new HttpClient(parameters.sslContextFactory());
        httpClient.getProxyConfiguration().getProxies().addAll(parameters.proxies());
    }

    /**
     * Start the connector.
     * @return true if connection was established, false otherwise
     */
    public Future<Boolean> start() {
        if (running.compareAndSet(false, true)) {
            addListener(Channel.META_CONNECT, new AuthFailureListener());
            addListener(Channel.META_HANDSHAKE, new AuthFailureListener());
            replay.clear();
            return connect();
        }
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.complete(true);
        return future;
    }


    private void disconnect() {
        if (!running.compareAndSet(true, false)) {
            return;
        }
        if (client != null) {
            log.info("Disconnecting Bayeux Client in SalesforceConnector");
            client.disconnect();
            client = null;
        }
    }


    public void stop() {
        disconnect();
        if (httpClient != null) {
            try {
                log.info("Stopping the http client!");
                httpClient.stop();
            } catch (Exception e) {
                log.error("Unable to stop HTTP transport[{}]", parameters.endpoint(), e);
            }
        }
    }


    public void setBearerTokenProvider(Function<Boolean, String> bearerTokenProvider) {
        this.bearerTokenProvider = bearerTokenProvider;
    }


    public Future<TopicSubscription> subscribe(String topic, long replayFrom, Consumer<Map<String, Object>> consumer) {
        if (!running.get()) {
            throw new IllegalStateException(String.format("Connector[%s} has not been started",
                    parameters.endpoint()));
        }
        topic = topic.replaceAll("/$", "");

        final String topicWithoutQueryString = topicWithoutQueryString(topic);
        if (replay.putIfAbsent(topicWithoutQueryString, replayFrom) != null) {
            throw new IllegalStateException(String.format("Already subscribed to %s [%s]",
                    topic, parameters.endpoint()));
        }

        SubscriptionImpl subscription = new SubscriptionImpl(topic, consumer);

        return subscription.subscribe();
    }


    public void unsubscribe(String topic) {
        subscriptions.stream()
                .filter(subscription -> subscription.getTopic().equalsIgnoreCase(topic))
                .findAny()
                .ifPresent(SubscriptionImpl::cancel);
    }


    public Future<TopicSubscription> subscribeEarliest(String topic, Consumer<Map<String, Object>> consumer) {
        return subscribe(topic, REPLAY_FROM_EARLIEST, consumer);
    }


    public Future<TopicSubscription> subscribeTip(String topic, Consumer<Map<String, Object>> consumer) {
        return subscribe(topic, REPLAY_FROM_TIP, consumer);
    }

    public SalesforceConnector addListener(String channel, ClientSessionChannel.MessageListener messageListener) {
        listenerInfos.add(new MessageListenerInfo(channel, messageListener));
        return this;
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public boolean isDisconnected() {
        return client == null || client.isDisconnected();
    }

    public boolean isHandshook() {
        return client != null && client.isHandshook();
    }

    public long getLastReplayId(String topic) {
        return replay.get(topic);
    }

    private static String topicWithoutQueryString(String fullTopic) {
        return fullTopic.split("\\?")[0];
    }

    private Future<Boolean> connect() {
        log.info("SalesforceConnector connecting");
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        try {
            if (!httpClient.isStarted()) {
                httpClient.start();
            }
        } catch (Exception e) {
            log.error("Unable to start HTTP transport[{}]", parameters.endpoint(), e);
            running.set(false);
            future.complete(false);
            return future;
        }

        String bearerToken = bearerToken();

        LongPollingTransport httpTransport = new LongPollingTransport(parameters.longPollingOptions(), httpClient) {
            @Override
            protected void customize(Request request) {
                request.header(AUTHORIZATION, bearerToken);
            }
        };

        client = new BayeuxClient(parameters.endpoint().toExternalForm(), httpTransport);

        client.addExtension(new ReplayExtension(replay));

        addListeners(client);

        client.handshake((m) -> {
            if (!m.isSuccessful()) {
                Object error = m.get(ERROR);
                if (error == null) {
                    error = m.get(FAILURE);
                }
                future.completeExceptionally(new ConnectException(
                        String.format("Cannot connect [%s] : %s", parameters.endpoint(), error)));
                running.set(false);
            } else {
                subscriptions.forEach(SubscriptionImpl::subscribe);
                future.complete(true);
            }
        });

        return future;
    }

    private void addListeners(BayeuxClient client) {
        for (MessageListenerInfo info : listenerInfos) {
            client.getChannel(info.getChannelName()).addListener(info.getMessageListener());
        }
    }

    private String bearerToken() {
        String bearerToken;
        if (bearerTokenProvider != null) {
            bearerToken = bearerTokenProvider.apply(reauthenticate.get());
            reauthenticate.compareAndSet(true, false);
        } else {
            bearerToken = parameters.bearerToken();
        }

        return bearerToken;
    }

    private void reconnect() {
        if (running.compareAndSet(false, true)) {
            connect();
        } else {
            log.error("The current value of running is not as we expect, this means our reconnection may not happen");
        }
    }

    /**
     * Listens to /meta/connect channel messages and handles 401 errors, where client needs
     * to reauthenticate.
     */
    private class AuthFailureListener implements ClientSessionChannel.MessageListener {
        private static final String ERROR_401 = "401";
        private static final String ERROR_403 = "403";

        @Override
        public void onMessage(ClientSessionChannel channel, Message message) {
            if (!message.isSuccessful()) {
                if (isError(message, ERROR_401) || isError(message, ERROR_403)) {
                    reauthenticate.set(true);
                    disconnect();
                    reconnect();
                }
            }
        }

        private boolean isError(Message message, String errorCode) {
            String error = (String)message.get(Message.ERROR_FIELD);
            String failureReason = getFailureReason(message);

            return (error != null && error.startsWith(errorCode)) ||
                    (failureReason != null && failureReason.startsWith(errorCode));
        }

        private String getFailureReason(Message message) {
            String failureReason = null;
            Map<String, Object> ext = message.getExt();
            if (ext != null) {
                Map<String, Object> sfdc = (Map<String, Object>)ext.get("sfdc");
                if (sfdc != null) {
                    failureReason = (String)sfdc.get("failureReason");
                }
            }
            return failureReason;
        }
    }

    private static class MessageListenerInfo {
        private String channelName;
        private ClientSessionChannel.MessageListener messageListener;

        MessageListenerInfo(String channelName, ClientSessionChannel.MessageListener messageListener) {
            this.channelName = channelName;
            this.messageListener = messageListener;
        }

        String getChannelName() {
            return channelName;
        }

        ClientSessionChannel.MessageListener getMessageListener() {
            return messageListener;
        }
    }
}
