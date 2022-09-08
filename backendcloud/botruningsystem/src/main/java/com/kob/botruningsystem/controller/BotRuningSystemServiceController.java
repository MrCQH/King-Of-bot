package com.kob.botruningsystem.controller;

import com.kob.botruningsystem.service.BotRuningSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class BotRuningSystemServiceController {
    @Autowired
    private BotRuningSystemService botRuningSystemService;

    @PostMapping("/bot/add/")
    public String addBot(@RequestParam MultiValueMap<String, String> data){
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        String botCode = data.getFirst("bot_code");
        String input = data.getFirst("input");
        return botRuningSystemService.addBot(userId, botCode, input);
    }
}
