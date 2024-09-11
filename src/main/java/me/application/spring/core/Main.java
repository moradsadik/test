package me.application.spring.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class Main {
    public static void main(String[] args)  {
        SpringApplication.run(me.application.spring.core.Main.class, args);
    }

}
