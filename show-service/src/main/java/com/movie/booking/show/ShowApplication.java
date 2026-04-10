package com.movie.booking.show;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ShowApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShowApplication.class, args);
    }
}
