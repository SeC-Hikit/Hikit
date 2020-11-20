package org.sc.frontend.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    private final String port;
    private final String address;

    @Autowired
    public AppProperties(final @Value("${server.port}") String port,
                         final @Value("${service.backend.address}") String address) {
        this.port = port;
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public String getBackendAddress() {
        return address;
    }
}
