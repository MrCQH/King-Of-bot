package com.kob.botruningsystem.service.Impl;

import com.kob.botruningsystem.service.BotRuningSystemService;
import com.kob.botruningsystem.service.utils.BotPool;
import org.springframework.stereotype.Service;

@Service
public class BotRuningSystemServiceImpl implements BotRuningSystemService {
    public final static BotPool botpool = new BotPool();

    @Override
    public String addBot(Integer userId, String botCode, String input) {
        System.out.println("add bot: " + userId + " " + botCode + " " + input);
        botpool.addBot(userId, botCode, input);
        return "add bot success!";
    }
}
