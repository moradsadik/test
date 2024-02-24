package me.application.spring.integration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PipelineRestarter{

    private final ApplicationContext context;
    private final MessageChannel controlBus;

    public PipelineRestarter(ApplicationContext context, MessageChannel controlBus) {
        this.context = context;
        this.controlBus = controlBus;
    }

    @PostMapping("/integration/{id}/{action}")
    public boolean restart(@PathVariable("id") String id, @PathVariable("action") Action action){
        log.info("@"+id+"."+action.getAction()+"()");
        return controlBus.send(new GenericMessage<>("@" + id + "." + action.getAction() + "()"));
    }

    @Getter
    enum Action{
        START("start"), STOP("stop");

        String action;
        Action(String action) {
            this.action = action;
        }
    }

}