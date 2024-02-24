package me.application.spring.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class Main {

    public static final String PIPE_ID = "FLOW_ID";

    private final MainProperties properties;

    public static void main(String[] args)  {
        SpringApplication.run(Main.class, args);
    }

    @Bean public MessageChannel controlBus() {return MessageChannels.direct().get();}

    @Bean
    public IntegrationFlow controlBusFlow() {return IntegrationFlows.from("controlBus").controlBus().get();}

    @Bean public TaskExecutor taskExecutor() {return taskExecutor(properties.getThreadPoolSize());}

    @Bean
    IntegrationFlow flow(){
        return IntegrationFlows
                .from(source(), pollingConfig())
                .channel(MessageChannels.executor(taskExecutor()))
                .handle((GenericHandler<File>) (payload, headers) -> {
                    log.info("payload : {}", payload);
                    sleep(1000);
                    return payload;
                })
                .handle(Files.outboundAdapter(new File(properties.getDataOut()))
                        .autoCreateDirectory(true)
                        .get())
                .get();
    }

    private Consumer<SourcePollingChannelAdapterSpec> pollingConfig() {
        return p -> p.id(PIPE_ID).poller(
                pm -> pm.cron(properties.getPollerCron())
                        .maxMessagesPerPoll(properties.getChannelPoolSize()));
    }

    private FileReadingMessageSource source() {
        return Files.inboundAdapter(new File(properties.getDataIn()))
                .autoCreateDirectory(true)
                .regexFilter(properties.getFileRegexPattern())
                .get();
    }

    public TaskExecutor taskExecutor(Integer poolSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(poolSize);
        executor.setCorePoolSize(poolSize);
        return executor;
    }

    record Tableau(UUID id, String name, List<Column> columns){}
    record Column(UUID id, String name, ColumnType type,String taille,Options options, Tableau tableau){}
    record Options(UUID id, OptionKey key, String value,Column column){}
    enum OptionKey{NULLABLE,PRIMARY_KEY,DEFAULT_VALUE,FOREIGN_KEY}
    enum ColumnType{TINYINT,INT,BIGINT,FLOAT,DECIMAL,DOUBLE,TEXT,UUID,BLOB,VARCHAR,DATE,TIME,DATETIME,TIMESTAMP}

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}


