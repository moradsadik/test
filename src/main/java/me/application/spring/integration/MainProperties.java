package me.application.spring.integration;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.main")
@RefreshScope
@Data
public class MainProperties {
    private Long pollerFrequency;
    private String pollerCron;
    private Integer threadPoolSize;
    private Integer channelPoolSize;
    private String dataIn;
    private String dataOut;
    private String fileRegexPattern;
}