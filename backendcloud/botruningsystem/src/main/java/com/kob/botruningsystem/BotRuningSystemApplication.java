package com.kob.botruningsystem;

import com.kob.botruningsystem.service.Impl.BotRuningSystemServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotRuningSystemApplication {
    public static void main(String[] args) {
        BotRuningSystemServiceImpl.botpool.start();
        SpringApplication.run(BotRuningSystemApplication.class, args);
    }
}
