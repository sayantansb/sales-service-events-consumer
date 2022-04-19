package api.sales.service.connector.example;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingListener implements ClientSessionChannel.MessageListener {

    private boolean logSuccess;
    private boolean logFailure;

    public LoggingListener() {
        this.logSuccess = true;
        this.logFailure = true;
    }

    public LoggingListener(boolean logSuccess, boolean logFailure) {
        this.logSuccess = logSuccess;
        this.logFailure = logFailure;
    }

    @Override
    public void onMessage(ClientSessionChannel clientSessionChannel, Message message) {
        if (logSuccess && message.isSuccessful()) {
            log.info(">>>>");
            printPrefix();
            log.info("Success:[" + clientSessionChannel.getId() + "]");
            log.info(message.toString());
            log.info("<<<<");
        }

        if (logFailure && !message.isSuccessful()) {
            log.info(">>>>");
            printPrefix();
            log.info("Failure:[" + clientSessionChannel.getId() + "]");
            log.info(message.toString());
            log.info("<<<<");
        }
    }

    private void printPrefix() {
        log.info("[" + timeNow() + "] ");
    }

    private String timeNow() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        return dateFormat.format(now);
    }
}
