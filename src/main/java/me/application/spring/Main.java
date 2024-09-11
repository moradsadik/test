package me.application.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.application.spring.integration.MainProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.util.Iterator;
import java.util.List;
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
    public IntegrationFlow flow(){
        return fileFlow();
    }

    private StandardIntegrationFlow fileFlow() {
        return IntegrationFlows
                .from(source(), pollingConfig())
                .channel(MessageChannels.executor(taskExecutor()))
                .handle((GenericHandler<File>) (payload, headers) -> {log.info("payload : {}", payload);return payload;})
                .handle(Files.outboundAdapter(new File(properties.getDataOut())).autoCreateDirectory(true).get())
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

    private ListMessageSource<Donne> sourceBdd(){
        return null;
    }

    public TaskExecutor taskExecutor(Integer poolSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(poolSize);
        executor.setCorePoolSize(poolSize);
        return executor;
    }

    record Donne(int id, int age,String adresse){}
}

class ListMessageSource<T> implements MessageSource<T> {
    private final Iterator<T> iterator;

    public ListMessageSource(List<T> list) {
        this.iterator = list.iterator();
    }

    @Override
    public Message<T> receive() {
        if (iterator.hasNext()) {
            T next = iterator.next();
            return MessageBuilder.withPayload(next).build();
        } else {
            return null; // or handle the end of the list as needed
        }
    }
}


