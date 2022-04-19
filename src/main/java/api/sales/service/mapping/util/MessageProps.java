package api.sales.service.mapping.util;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "msg-props")
public class MessageProps {

    private Map<String, String> siteNames;

    public Map<String, String> getSiteNames() {
        return siteNames;
    }

    public void setSiteNames(final Map<String, String> siteNames) {
        this.siteNames = siteNames;
    }
}
