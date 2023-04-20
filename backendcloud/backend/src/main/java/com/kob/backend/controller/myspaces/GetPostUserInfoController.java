package com.kob.backend.controller.myspaces;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.user.myspaces.GetPostUserInfoService;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.util.Map;

@RestController
@Log
public class GetPostUserInfoController {

    @Resource
    GetPostUserInfoService getPostUserInfoService;

    @GetMapping("/api/myspaces/getInfo/")
    @ResponseBody
    public JSONObject getInfo(@RequestParam Map<String, String> data){
        Integer userId = Integer.valueOf(data.get("userId"));
        return getPostUserInfoService.getPostUserInfo(userId);
    }
}
