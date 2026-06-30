package com.reactorAuth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class App {
    public static void main(String[] args) {

        Hooks.onOperatorDebug();
        SpringApplication.run(App.class, args);
    }
}
