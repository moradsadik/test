package me.application.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.PollerFactory;
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.messaging.support.MessageBuilder;

import java.time.Duration;
import java.util.function.Consumer;

@SpringBootApplication
@Slf4j
public class Main {

    public static void main(String[] args)  {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    IntegrationFlow flow(){
        return IntegrationFlow.from(
                (MessageSource<String>) () -> MessageBuilder.withPayload("hello world").build(),
                        pollerspec -> pollerspec.poller(pm -> PollerFactory.fixedDelay(Duration.ofMinutes(1)))
                )
                .handle((payload, headers)->{
                    log.info("Payload : "+payload);
                    log.info("Headers : "+headers);
                    return  null;
                })
                .get();
    }

}


