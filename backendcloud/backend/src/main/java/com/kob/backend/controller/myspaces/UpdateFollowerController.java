package com.kob.backend.controller.myspaces;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.user.myspaces.UpdateFollowerService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@Log
public class UpdateFollowerController {

    @Resource
    UpdateFollowerService updateFollowerService;


    @PostMapping("/api/myspaces/updatefollower/")
    public JSONObject updateFollowerCount(@RequestParam("target_id") Integer targetId, @RequestParam("is_followed") Boolean isFollower){
        assert isFollower != null;
        if (isFollower) return updateFollowerService.removeFollower(targetId);
        return updateFollowerService.addFollower(targetId);
    }
}
