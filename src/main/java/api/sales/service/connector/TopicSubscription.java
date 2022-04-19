package api.sales.service.connector;

public interface TopicSubscription {

    void cancel();

    long getReplayFrom();

    String getTopic();

}
