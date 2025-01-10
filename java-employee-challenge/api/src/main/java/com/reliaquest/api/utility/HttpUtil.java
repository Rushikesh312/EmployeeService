package com.reliaquest.api.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class HttpUtil {

    // We can add SSL context required to set, certificates , etc to common rest client
    // Also we can add custom connection pool, custom HttpClient

    public static RestTemplate getAllAcceptRestTemplate() {

        try {
            final RestTemplate restTemplate = new RestTemplate();
            return restTemplate;
        } catch (Exception e) {
            log.error("Exception while building the RestTemplate");
            throw new RuntimeException("Exception while building the RestTemplate");
        }

    }

}
