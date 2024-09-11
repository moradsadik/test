package me.application.spring.integration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.application.spring.Main;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PipelineRestarter{

    private final IntegrationFlowContext flowContext;
    private final ApplicationContext applicationContext;
    private final MessageChannel controlBus;
    private final MainProperties properties;

    @PostMapping("/integration/{id}/{action}")
    public boolean restart(@PathVariable("id") String id, @PathVariable("action") Action action){
        log.info("@"+id+"."+action.getAction()+"()");
        return controlBus.send(new GenericMessage<>("@" + id + "." + action.getAction() + "()"));
    }

    @PostMapping("/beans/{beanName}")
    public void refreshBean(@PathVariable("beanName") String beanName){
        DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory();
        registry.destroySingleton(beanName);
        registry.registerSingleton(beanName,new Main(properties).flow());
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