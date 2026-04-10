package com.movie.booking.theatre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TheatreApplication {
    public static void main(String[] args) {
        SpringApplication.run(TheatreApplication.class, args);
    }
}
