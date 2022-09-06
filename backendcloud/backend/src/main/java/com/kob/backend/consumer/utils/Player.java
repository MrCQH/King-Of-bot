package com.kob.backend.consumer.utils;

import com.apple.eawt.AppEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer sx;
    private Integer sy;
    private List<Integer> steps; // 记录后续蛇走的一系列操作

    private boolean checkTailIncreasing(Integer step){ // 蛇尾3回合增长一个单位长度
        if (step <= 10) return true;
        return step % 3 == 1;
    }

    public List<Cell> getCells(){
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        res.add(new Cell(x, y));
        int step = 0;
        for (Integer d: steps){
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!checkTailIncreasing( ++ step)){
                res.remove(0);
            }
        }
        return res;
    }

    public String getStepString(){
        StringBuilder res = new StringBuilder();
        for (Integer step: steps){
            res.append(step);
        }
        return res.toString();
    }
}
