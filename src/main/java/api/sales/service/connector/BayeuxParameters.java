
package api.sales.service.connector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.util.ssl.SslContextFactory;


public interface BayeuxParameters {


    String bearerToken();


    default URL endpoint() {
        String path = new StringBuilder().append(LoginHelper.COMETD_REPLAY).append(version()).toString();
        try {
            return new URL(host(), path);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(
                    String.format("Unable to create url: %s:%s", host().toExternalForm(), path), e);
        }
    }

    default URL host() {
        try {
            return new URL(LoginHelper.LOGIN_ENDPOINT);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(String.format("Unable to form URL for %s", LoginHelper.LOGIN_ENDPOINT));
        }
    }


    default long keepAlive() {
        return 60;
    }


    default TimeUnit keepAliveUnit() {
        return TimeUnit.MINUTES;
    }

    default Map<String, Object> longPollingOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("maxNetworkDelay", maxNetworkDelay());
        options.put("maxBufferSize", maxBufferSize());
        return options;
    }


    default int maxBufferSize() {
        return 1048576;
    }


    default int maxNetworkDelay() {
        return 15000;
    }


    default Collection<? extends org.eclipse.jetty.client.ProxyConfiguration.Proxy> proxies() {
        return Collections.emptyList();
    }


    default SslContextFactory sslContextFactory() {
        return new SslContextFactory();
    }


    default String version() {
        return "43.0";
    }
}
