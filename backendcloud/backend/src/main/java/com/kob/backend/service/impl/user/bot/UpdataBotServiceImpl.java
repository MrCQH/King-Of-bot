package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.UpdataBotService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UpdataBotServiceImpl implements UpdataBotService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> updata(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();

        User user = loginUser.getUser();
        Integer bot_id = Integer.parseInt(data.get("bot_id"));
        Bot bot = botMapper.selectById(bot_id);
        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Map<String, String> map = new HashMap<>();

        if (!bot.getUserId().equals(user.getId())){
            map.put("error_message", "你没有权限更新该Bot信息");
            return map;
        }

        if (title == null || title.length() == 0){
            map.put("error_message", "标题不能为空");
            return map;
        }

        if (title.length() > 100){
            map.put("error_message", "标题长度不能大于100");
            return map;
        }

        if (description == null){
            map.put("error_message", "简介不能为空");
            return map;
        }

        if (description.length() == 0){
            map.put("error_message", "这个人很懒，什么也没有留下～");
        }

        if (description.length() > 300){
            map.put("error_message", "简介的长度不能大于300");
            return map;
        }

        if (content == null || content.length() == 0){
            map.put("error_message", "代码内容不能为空");
        }

        if (content.length() > 10000){
            map.put("error_message", "代码不能超过10000字符");
            return map;
        }

        Date now = new Date();
        Bot new_bot = new Bot(
                bot.getId(),
                user.getId(),
                title,
                description,
                content,
                bot.getCreatetime(),
                now
        );

        botMapper.updateById(new_bot);
        map.put("error_message", "success");
        return map;
    }
}
