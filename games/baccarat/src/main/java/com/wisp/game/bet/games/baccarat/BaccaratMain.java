package com.wisp.game.bet.games.baccarat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;



@ComponentScan(basePackages = {"com.wisp.game"})
@SpringBootApplication
public class BaccaratMain {

    public static void main(String[] args)
    {
        SpringApplication.run(BaccaratMain.class,args);
    }
}
