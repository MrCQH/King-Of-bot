package com.kob.backend;

import com.kob.backend.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendApplicationTests {

    @Resource
    StringRedisTemplate template;
    @Resource
    RedisService service;

    @Test
    void contextLoads() {
        service.test();
    }
}