package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final Integer[][] g;
    private final static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    private final Player playerA, playerB;
    private Integer stepNextA = null; // A的下一步的方向
    private Integer stepNextB = null; // B的下一步的方向
    private String loser = ""; // all: 平局, a: a输, b: b输
    private String status = "playing"; // 游戏状态: playing -> finished
    private ReentrantLock lock = new ReentrantLock();

    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Integer idB) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new Integer[this.rows][this.cols];
        this.playerA = new Player(idA, this.rows - 2, 1, new ArrayList<>());
        this.playerB = new Player(idB, 1, this.cols - 2, new ArrayList<>());
    }

    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < rows; i ++){
            for (int j = 0; j < cols; j ++){
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    public void setStepNextA(Integer stepNextA) {
        lock.lock();
        try{
            this.stepNextA = stepNextA;
        } finally {
            lock.unlock();
        }
    }

    public void setStepNextB(Integer stepNextB){
        lock.lock();
        try{
            this.stepNextB = stepNextB;
        } finally {
            lock.unlock();
        }
    }

    public Player getPlayerA(){
        return playerA;
    }

    public Player getPlayerB(){
        return playerB;
    }

    public Integer[][] getG(){
        return this.g;
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty){ // 暴搜检查连通性
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for (int i = 0; i < 4; i ++){
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0){
                if (check_connectivity(x, y, tx, ty)){
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }
        g[sx][sy] = 0;
        return false;
    }

    private boolean draw(){
        for (int i = 0; i < rows; i ++){
            for (int j = 0; j < cols; j ++){
                g[i][j] = 0;
            }
        }

        for (int r = 0; r < this.rows; r ++){ // 给左右添加墙
            g[r][0] = g[r][this.cols - 1] = 1;
        }

        for (int c = 0; c < this.cols; c ++){ // 给上下添加墙
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        Random random = new Random();
        for (int i = 0; i < this.inner_walls_count / 2; i ++){
            for (int j = 0; j < 1000; i ++){
                int r = random.nextInt(this.rows); // 随机[0, this.rows)范围的数
                int c = random.nextInt(this.cols);
                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1){
                    continue;
                }
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) {
                    continue;
                }
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap(){
        for (int i = 0; i < 1000; i ++){
            if (draw()) break;
        }
    }

    private boolean stepNext(){ // 判断两端输入是否合法
        try{
            Thread.sleep(200);
        } catch(InterruptedException e){
            throw new RuntimeException(e);
        }
        for (int i = 0; i < 50; i ++){
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (stepNextA != null && stepNextB != null){
                        playerA.getSteps().add(stepNextA); // 记录玩家每一步的操作
                        playerB.getSteps().add(stepNextB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void sendAllMessage(String message){ // 将信息广播给所有人
        WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private void sendResult(){ // 发送最终结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB){ // 碰撞检测器
        int n = cellsA.size();
        Cell snackHead = cellsA.get(n - 1);
        if (g[snackHead.x][snackHead.y] == 1) return false;

        for (int i = 0; i < n - 1; i ++){ // 判断撞自己身体
            if (snackHead.x == cellsA.get(i).x && snackHead.y == cellsA.get(i).y) return false;
        }

        for (Cell cellB : cellsB){ // 判断撞另一头蛇
            if (snackHead.x == cellB.x && snackHead.y == cellB.y) return false;
        }
        return true;
    }

    private void judge(){ // 判断两名玩家下一步操作是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();
        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if (!validA || !validB){
            status = "finished";
            if (!validA && !validB){
                loser = "all";
            } else if (!validA){
                loser = "a";
            } else {
                loser = "b";
            }
        }
    }

    private void sendMove(){ // 发送移动信息
        lock.lock();
        try{
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", stepNextA);
            resp.put("b_direction", stepNextB);
            sendAllMessage(resp.toJSONString());
            stepNextA = stepNextB = null;
        } finally {
            lock.unlock();
        }
    }

    private void saveToDatabase(){
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepString(),
                playerB.getStepString(),
                getMapString(),
                loser,
                new Date()
        );
        WebSocketServer.recordMapper.insert(record);
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i ++) { // 保证程序正确不出现死循环
            if (stepNext()){
                judge();
                if (status.equals("playing")){
                    sendMove();
                } else {
                    sendResult();
                    break;
                }
            } else {
                lock.lock();
                try{
                    if (stepNextA == null && stepNextB == null){
                        loser = "all";
                    } else if (stepNextA == null){
                        loser = "a";
                    } else {
                        loser = "b";
                    }
                } finally {
                    lock.unlock();
                }
                status = "finished";
                sendResult();
                break;
            }
        }
    }
}
