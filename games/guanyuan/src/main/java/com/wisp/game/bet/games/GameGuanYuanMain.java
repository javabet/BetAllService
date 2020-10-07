package com.wisp.game.bet.games;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.wisp.game"})
@SpringBootApplication
public class GameGuanYuanMain {
    public static void main(String[] args)
    {
        SpringApplication.run(GameGuanYuanMain.class,args);
    }
}
