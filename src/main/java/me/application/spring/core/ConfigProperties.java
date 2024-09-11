package me.application.spring.core;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.config")
@RefreshScope
@Data
public class ConfigProperties {
    private Long pollerFrequency;
    private String pollerCron;
    private Integer threadPoolSize;
    private Integer channelPoolSize;
    private String dataIn;
    private String dataOut;
    private String fileRegexPattern;
}