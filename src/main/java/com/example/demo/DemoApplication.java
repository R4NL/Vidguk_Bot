package com.example.demo;

import com.example.demo.servises.PreStart;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        PreStart.createDirectories();
        ApiContextInitializer.init();
        SpringApplication.run(DemoApplication.class, args);
    }

}
