package gr.aegean.bookclientspring.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "service")
public class ImmutableApiConfiguration {

    private final String host;
    private final String port;
    private final String api;
    
    @ConstructorBinding
    public ImmutableApiConfiguration(String host, String port, String api) {
    	this.host = host;
    	this.port = port;
    	this.api = api;
    }
    
    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getApi() {
        return api;
    }
}
