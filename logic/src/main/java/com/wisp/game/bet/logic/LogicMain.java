package com.wisp.game.bet.logic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.wisp.game"})
@SpringBootApplication
public class LogicMain {

    public static void main(String[] args)
    {
        SpringApplication.run(LogicMain.class,args);
    }
}
