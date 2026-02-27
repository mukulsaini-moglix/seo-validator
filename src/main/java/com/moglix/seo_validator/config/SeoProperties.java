package com.moglix.seo_validator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "seo")
@Data
public class SeoProperties {

    private Fetch fetch;
    private ThreadPool threadPool;

    @Data
    public static class Fetch {
        private int timeout;
        private String userAgent;
    }

    @Data
    public static class ThreadPool {
        private int size;
    }
}