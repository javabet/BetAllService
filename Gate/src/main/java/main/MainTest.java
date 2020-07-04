package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainTest {
    public static void main(String[] args)
    {
        System.out.println("go this");
        SpringApplication.run(MainTest.class,args);
    }
}
