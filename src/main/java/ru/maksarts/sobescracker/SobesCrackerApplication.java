package ru.maksarts.sobescracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SobesCrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SobesCrackerApplication.class, args);
    }

}
