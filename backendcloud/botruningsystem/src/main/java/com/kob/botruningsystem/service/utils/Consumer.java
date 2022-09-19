package com.kob.botruningsystem.service.utils;

import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class Consumer extends Thread{
    private Bot bot;
    private static RestTemplate restTemplate;
    private final static String nextStepUrl = "http://127.0.0.1:3000/receive/bot/move/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        Consumer.restTemplate = restTemplate;
    }

    public void startTimeout(long timeout, Bot bot){ // 代码最多运行timeout秒
        this.bot = bot;
        this.start();

        try {
            this.join(timeout); // 最多等timeout秒之后继续向后执行
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.interrupt(); // 将该线程打断
        }
    }

    public String addUid(String code, String uid){
        int k = code.indexOf(" implements java.util.function.Supplier<Integer>");
        return code.substring(0, k) + uid + code.substring(k);
    }

    private void sendResult(Integer direction){
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("direction", direction.toString());
        data.add("user_id", bot.getUserId().toString());
        restTemplate.postForObject(nextStepUrl, data, String.class);
    }

    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0, 8); // 随机生成一个8位不重复的数字


        Supplier<Integer> botInterface = Reflect.compile(
                "com.kob.botruningsystem.utils.Bot" + uid,
                        addUid(bot.getBotCode(), uid)
        ).create().get();

        File file = new File("input.txt");
        try(PrintWriter fout = new PrintWriter(file)){
            fout.println(bot.getInput());
            fout.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Integer direction = botInterface.get();
        sendResult(direction);

        System.out.println("move direction: " + bot.getUserId() + " " + direction);
    }
}
