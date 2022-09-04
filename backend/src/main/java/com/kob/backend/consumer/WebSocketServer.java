package com.kob.backend.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.file.WatchEvent;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    private Session session = null;
    private User user; // 维护这个链接的User信息
    public static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>(); // 线程安全的HashMap
    private static final CopyOnWriteArrayList<User> matchpool = new CopyOnWriteArrayList<>(); // 线程安全的set
    private Game game = null;

    private static UserMapper userMapper;
    public static RecordMapper recordMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper = userMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper){
        WebSocketServer.recordMapper = recordMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session = session;
        System.out.println("connected!");
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);

        if (this.user != null){
            users.put(userId, this);
        } else {
            this.session.close();
        }
    }

    @OnClose
    public void onClose() {
        System.out.println("disconnected!");
        if (this.user != null){
            users.remove(this.user.getId());
            matchpool.remove(this.user);
        }
    }

    private void start_matching(){
        System.out.println("start_matching!");
        matchpool.add(this.user);
        while (matchpool.size() >= 2){
            System.out.println("matched!");
            Iterator<User> it = matchpool.iterator();
            User a = it.next(), b = it.next();
            matchpool.remove(a);
            matchpool.remove(b);
            Game game = new Game(13, 14, 20, a.getId(), b.getId());
            game.createMap();
            users.get(a.getId()).game = game;
            users.get(b.getId()).game = game;
            game.start(); // Game中的run函数开始执行

            JSONObject repGame = new JSONObject();
            repGame.put("a_id", game.getPlayerA().getId());
            repGame.put("a_sx", game.getPlayerA().getSx());
            repGame.put("a_sy", game.getPlayerA().getSy());
            repGame.put("b_id", game.getPlayerB().getId());
            repGame.put("b_sx", game.getPlayerB().getSx());
            repGame.put("b_sy", game.getPlayerB().getSy());
            repGame.put("map", game.getG());

            JSONObject repA = new JSONObject();
            repA.put("event", "start-matching");
            repA.put("opponent_photo", b.getPhoto());
            repA.put("opponent_username", b.getUsername());
            repA.put("game", repGame);
            users.get(a.getId()).sendMessage(repA.toJSONString());

            JSONObject repB = new JSONObject();
            repB.put("event", "start-matching");
            repB.put("opponent_photo", a.getPhoto());
            repB.put("opponent_username", a.getUsername());
            repB.put("game", repGame);
            users.get(b.getId()).sendMessage(repB.toJSONString());
        }
    }

    private void stop_matching(){
        System.out.println("stop_matching!");
        matchpool.remove(this.user);
    }

    private void move(Integer direction){
        if (game.getPlayerA().getId().equals(user.getId())){
            game.setStepNextA(direction);
        } else if (game.getPlayerB().getId().equals(user.getId())){
            game.setStepNextB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) { // 当路由
        System.out.println("receive message!");
        JSONObject data = JSON.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)){
            start_matching();
        } else if ("stop-matching".equals(event)) {
            stop_matching();
        } else if ("move".equals(event)){
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message){ // server发送消息给client
        synchronized (this.session){
            try{
                this.session.getBasicRemote().sendText(message);
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
