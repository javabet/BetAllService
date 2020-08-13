package com.wisp.game.bet;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.wisp.game"})
@SpringBootApplication
public class GameBaccaratMain {

    public static void main(String[] args)
    {
        SpringApplication.run(GameBaccaratMain.class,args);
    }
}
