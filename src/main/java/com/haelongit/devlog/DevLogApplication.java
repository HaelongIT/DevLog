package com.haelongit.devlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@EnableScheduling
public class DevLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevLogApplication.class, args);
    }


    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
